package cc.minetale.blitz.listeners.velocity;

import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.grant.Grant;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.punishment.Punishment;
import com.google.common.hash.Hashing;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlayerEvents {

    @Subscribe(order = PostOrder.FIRST)
    public EventTask onPlayerLogin(LoginEvent event) {
        return EventTask.async(() -> {
            var player = event.getPlayer();

            try {
                var profile = this.retrieveProfile(player.getUniqueId(), player.getUsername()).get(3, TimeUnit.SECONDS);

                if(profile == null) {
                    player.disconnect(Component.text("Failed to load your profile. Try again later.", NamedTextColor.RED));
                    return;
                }

                profile.setName(player.getUsername());

                var punishments = Punishment.getPunishments(profile).get(3, TimeUnit.SECONDS);
                profile.setPunishments(punishments);

                if (profile.getFirstSeen() == 0L)
                    profile.setFirstSeen(System.currentTimeMillis());

                profile.checkPunishments();

                var punishment = profile.getActiveBan();

                if (punishment != null) {
                    player.disconnect(Component.join(
                            JoinConfiguration.separator(Component.newline()),
                            punishment.getPunishmentMessage()
                    ));

                    return;
                }

                var grants = Grant.getGrants(profile).get(3, TimeUnit.SECONDS);
                System.out.println(CommonLib.getGson().toJson(grants));
                profile.setGrants(grants);

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

                ProfileCache.updateCache(profile);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
                player.disconnect(Component.text("Failed to load your profile. Try again later.", NamedTextColor.RED));
            }
        });
    }

    private CompletableFuture<@Nullable Profile> retrieveProfile(UUID uuid, String name) {
        return new CompletableFuture<Profile>()
                .completeAsync(() -> {
                    try {
                        var databaseProfile = ProfileCache.getFromDatabase(uuid).get();

                        if(databaseProfile != null) {
                            return databaseProfile;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        return null;
                    }

                    var profile = new Profile();

                    profile.setUuid(uuid);
                    profile.setName(name);
                    profile.setSearch(name.toUpperCase());

                    return profile;
                });
    }

//    @Subscribe
//    public void onPlayerConnect(ServerConnectedEvent event) {
//        var player = event.getPlayer();
//        var server = event.getServer().getServerInfo();
//        var previousServer = event.getPreviousServer();
//
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
//    }
//
//    @Subscribe
//    public void onPlayerDisconnect(DisconnectEvent event) {
//        var player = event.getPlayer();
//        var manager = Blitz.getBlitz().getTimerManager();
//
//        GrantTimer.getTimers(player.getUniqueId()).forEach(Timer::stop);
//
//        if (event.getLoginStatus() != DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN) return;
//
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
//    }

}
