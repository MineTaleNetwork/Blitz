package cc.minetale.blitz.listeners.velocity;

import cc.minetale.blitz.api.BlitzPlayer;
import cc.minetale.blitz.api.StaffMembers;
import cc.minetale.blitz.manager.PlayerManager;
import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.MC;
import com.google.common.hash.Hashing;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.nio.charset.StandardCharsets;

public class PlayerEvents {

    @Subscribe(order = PostOrder.EARLY)
    public EventTask onPlayerLogin(LoginEvent event) {
        return EventTask.async(() -> {
            var manager = PlayerManager.getPlayerManager();
            var player = event.getPlayer();

            manager.createProfile(player.getUsername(), player.getUniqueId())
                    .thenAccept(result -> {
                        switch (result) {
                            case CREATED_PROFILE, PROFILE_EXISTS -> {
                                BlitzPlayer.getBlitzPlayer(player.getUniqueId()).thenAccept(blitzPlayer -> {
                                    var profile = blitzPlayer.getProfile();

                                    profile.setName(player.getUsername());

                                    if (profile.getFirstSeen() == 0L)
                                        profile.setFirstSeen(System.currentTimeMillis());

                                    var punishment = profile.getActiveBan();

                                    if (punishment != null) {
                                        player.disconnect(Component.text("TODO: Proper Punishment Message"));
                                        return;
                                    }

                                    profile.setLastSeen(System.currentTimeMillis());

                                    var ip = player.getRemoteAddress()
                                            .getAddress()
                                            .getHostAddress();

                                    var hashedIP = Hashing.sha256()
                                            .hashString(ip, StandardCharsets.UTF_8)
                                            .toString();

                                    if (profile.getCurrentAddress() == null)
                                        profile.setCurrentAddress(hashedIP);

                                    Profile.Staff staff = profile.getStaffProfile();

                                    if (!profile.getCurrentAddress().equals(hashedIP)) {
                                        if (staff.getTwoFactorKey() != null)
                                            staff.setLocked(true);

                                        profile.setCurrentAddress(hashedIP);
                                    }

                                    manager.updateProfile(profile);
                                });
                            }
                            default -> player.disconnect(Component.text("Failed to load your profile. Try again later.", NamedTextColor.RED));
                        }
                    });
        });
    }

    @Subscribe
    public void onPlayerConnect(ServerConnectedEvent event) {
        var player = event.getPlayer();
        var optionalServer = player.getCurrentServer();

        if (optionalServer.isPresent()) {
            var server = optionalServer.get().getServerInfo();
            var previousServer = event.getPreviousServer();

            BlitzPlayer.getBlitzPlayer(player.getUniqueId()).thenAccept(blitzPlayer -> {
                var profile = blitzPlayer.getProfile();

                if (Rank.hasMinimumRank(profile, Rank.HELPER)) {
                    if (previousServer.isEmpty()) {
                        StaffMembers.getStaffMembers().getAudience().add(blitzPlayer);

                        StaffMembers.getStaffMembers().sendMessage(
                                MC.notificationMessage("Staff",
                                        Component.text().append(
                                                profile.getChatFormat(),
                                                Component.text(" has connected to ", NamedTextColor.GRAY),
                                                Component.text(server.getName(), NamedTextColor.GOLD)
                                        ).build()));
                    } else {
                        StaffMembers.getStaffMembers().sendMessage(
                                MC.notificationMessage("Staff",
                                        Component.text().append(
                                                profile.getChatFormat(),
                                                Component.text(" has connected to ", NamedTextColor.GRAY),
                                                Component.text(server.getName(), NamedTextColor.GOLD),
                                                Component.text(" from ", NamedTextColor.GRAY),
                                                Component.text(previousServer.get().getServerInfo().getName(), NamedTextColor.GOLD)
                                        ).build()));
                    }
                }
            });
        }
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        if (event.getLoginStatus() == DisconnectEvent.LoginStatus.PRE_SERVER_JOIN) return;

        var player = event.getPlayer();

        BlitzPlayer.getBlitzPlayer(player.getUniqueId()).thenAccept(blitzPlayer -> {
            var profile = blitzPlayer.getProfile();

            StaffMembers.getStaffMembers().getAudience().remove(blitzPlayer);

            if (Rank.hasMinimumRank(profile, Rank.HELPER)) {
                var currentServer = player.getCurrentServer();

                currentServer.ifPresent(server -> StaffMembers.getStaffMembers().sendMessage(
                        MC.notificationMessage("Staff",
                                Component.text().append(
                                        profile.getChatFormat(),
                                        Component.text(" has disconnected from ", NamedTextColor.GRAY),
                                        Component.text(server.getServerInfo().getName(), NamedTextColor.GOLD)
                                ).build()))
                );
            }
        });
    }

}
