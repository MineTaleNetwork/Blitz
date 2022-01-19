package cc.minetale.blitz.listeners.velocity;

import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.profile.CachedProfile;
import cc.minetale.commonlib.punishment.PunishmentType;
import cc.minetale.commonlib.util.ProfileUtil;
import com.google.common.hash.Hashing;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlayerEvents {

    @Subscribe(order = PostOrder.FIRST)
    public EventTask onPlayerLogin(LoginEvent event) {
        return EventTask.async(() -> {
            var player = event.getPlayer();

            try {
                var profile = ProfileUtil.retrieveProfile(player.getUniqueId(), player.getUsername()).get(3, TimeUnit.SECONDS);

                if(profile != null) {
                    profile.setUsername(player.getUsername());

                    if (profile.getFirstSeen() == 0L)
                        profile.setFirstSeen(System.currentTimeMillis());

                    profile.expirePunishments();

                    var punishment = profile.getActivePunishmentByType(PunishmentType.BAN);

                    if (punishment != null) {
                        player.disconnect(Component.join(
                                JoinConfiguration.separator(Component.newline()),
                                punishment.getPunishmentMessage()
                        ));

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

                    var staff = profile.getStaffProfile();

                    if (!profile.getCurrentAddress().equals(hashedIP)) {
                        if (!staff.getTwoFactorKey().isEmpty() && !staff.isLocked())
                            staff.setLocked(true);

                        profile.setCurrentAddress(hashedIP);
                    }

                    ProfileCache.writeCachedProfile(new CachedProfile(profile)).get();
                    return;
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }

            event.setResult(ResultedEvent.ComponentResult.denied(Component.text("Failed to load your profile. Try again later.", NamedTextColor.RED)));
        });
    }

    @Subscribe
    public void onPlayerConnect(ServerConnectedEvent event) {
        var player = event.getPlayer();
        var server = event.getServer().getServerInfo();
        var previousServer = event.getPreviousServer();

        ProfileCache.updateStatus(player.getUniqueId(), server.getName());

//        BlitzPlayer.getBlitzPlayer(player.getUniqueId())
//                .thenAccept(blitzPlayer -> {
//                    var profile = blitzPlayer.getProfile();
//
//                    if (Rank.hasMinimumRank(profile, Rank.HELPER)) {
//                        if (previousServer.isEmpty()) {
//                            StaffMembers.addMember(blitzPlayer);
//
//                            StaffMembers.sendMessage(
//                                    MC.notificationMessage("Staff",
//                                            Component.text().append(
//                                                    profile.getChatFormat(),
//                                                    Component.text(" has connected to ", NamedTextColor.GRAY),
//                                                    Component.text(server.getName(), NamedTextColor.GOLD)
//                                            ).build()));
//                        } else {
//                            StaffMembers.sendMessage(
//                                    MC.notificationMessage("Staff",
//                                            Component.text().append(
//                                                    profile.getChatFormat(),
//                                                    Component.text(" has connected to ", NamedTextColor.GRAY),
//                                                    Component.text(server.getName(), NamedTextColor.GOLD),
//                                                    Component.text(" from ", NamedTextColor.GRAY),
//                                                    Component.text(previousServer.get().getServerInfo().getName(), NamedTextColor.GOLD)
//                                            ).build()));
//                        }
//                    }
//                });
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        var player = event.getPlayer();

        if (event.getLoginStatus() != DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN) return;

        ProfileCache.updateStatus(player.getUniqueId(), null);

//        BlitzPlayer.getBlitzPlayer(player.getUniqueId())
//                .thenAccept(blitzPlayer -> {
//                    var profile = blitzPlayer.getProfile();
//
//                    StaffMembers.removeMember(blitzPlayer);
//
//                    if (Rank.hasMinimumRank(profile, Rank.HELPER)) {
//                        var currentServer = player.getCurrentServer();
//
//                        currentServer.ifPresent(server -> StaffMembers.sendMessage(
//                                MC.notificationMessage("Staff",
//                                        Component.text().append(
//                                                profile.getChatFormat(),
//                                                Component.text(" has disconnected from ", NamedTextColor.GRAY),
//                                                Component.text(server.getServerInfo().getName(), NamedTextColor.GOLD)
//                                        ).build()))
//                        );
//                    }
//                });
    }

}
