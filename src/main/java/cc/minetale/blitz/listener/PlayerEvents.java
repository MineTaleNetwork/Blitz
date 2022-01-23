package cc.minetale.blitz.listener;

import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.pigeon.payloads.friend.FriendJoinedPayload;
import cc.minetale.commonlib.pigeon.payloads.friend.FriendLeftPayload;
import cc.minetale.commonlib.profile.CachedProfile;
import cc.minetale.commonlib.punishment.PunishmentType;
import cc.minetale.commonlib.util.Message;
import cc.minetale.commonlib.util.PigeonUtil;
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

                    profile.save().get();
                    ProfileCache.updateCache(new CachedProfile(profile)).get();
                    return;
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }

            event.setResult(ResultedEvent.ComponentResult.denied(Message.parse(Language.Error.PROFILE_LOAD_ERROR)));
        });
    }

    @Subscribe
    public void onPlayerConnect(ServerConnectedEvent event) {
        var playerUuid = event.getPlayer().getUniqueId();
        var server = event.getServer().getServerInfo();
        var previousServer = event.getPreviousServer();

        ProfileCache.updateStatus(playerUuid, server.getName());

        if(previousServer.isEmpty()) {
            ProfileUtil.getCachedProfile(playerUuid)
                    .thenAccept(cachedProfile -> {
                        var profile = cachedProfile.getProfile();

                        if(profile.getFriends().size() != 0) {
                            PigeonUtil.broadcast(new FriendJoinedPayload(playerUuid));
                        }
                    });
        }
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        var playerUuid = event.getPlayer().getUniqueId();

        if (event.getLoginStatus() != DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN) return;

        ProfileCache.updateStatus(playerUuid, null);

        ProfileUtil.getCachedProfile(playerUuid)
                .thenAccept(cachedProfile -> {
                    var profile = cachedProfile.getProfile();

                    if(profile.getFriends().size() != 0) {
                        PigeonUtil.broadcast(new FriendLeftPayload(playerUuid));
                    }
                });
    }

}
