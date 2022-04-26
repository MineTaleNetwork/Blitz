package cc.minetale.blitz.listener;

import cc.minetale.blitz.Staff;
import cc.minetale.postman.Postman;
import cc.minetale.sodium.Sodium;
import cc.minetale.sodium.cache.ProfileCache;
import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.payloads.proxy.ProxyPlayerConnectPayload;
import cc.minetale.sodium.payloads.proxy.ProxyPlayerDisconnectPayload;
import cc.minetale.sodium.payloads.proxy.ProxyPlayerSwitchPayload;
import cc.minetale.sodium.profile.Profile;
import cc.minetale.sodium.profile.ProfileUtil;
import cc.minetale.sodium.profile.punishment.PunishmentType;
import cc.minetale.sodium.util.Message;
import cc.minetale.sodium.util.MongoUtil;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
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

public class PlayerEvents {

    @Subscribe(order = PostOrder.FIRST)
    public EventTask onPlayerLogin(LoginEvent event) {
        return EventTask.async(() -> {
            var player = event.getPlayer();

            var profile = ProfileUtil.retrieveProfile(player.getUniqueId(), player.getUsername());

            if (profile != null) {
                profile.setUsername(player.getUsername());

                if (profile.getFirstSeen() == 0L)
                    profile.setFirstSeen(System.currentTimeMillis());

                profile.expirePunishments();

                var punishment = profile.getActivePunishmentByType(PunishmentType.BAN);

                if (punishment != null) {
                    player.disconnect(Component.join(JoinConfiguration.newlines(), punishment.getPunishmentMessage()));
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
                    if (!staff.getTwoFactorKey().isEmpty() && !staff.isLocked()) {
                        staff.setLocked(true);
                    }

                    profile.setCurrentAddress(hashedIP);
                }

                profile.activateNextGrant();

                if (profile.isStaff()) {
                    Staff.putStaff(player);
                }

                MongoUtil.saveDocument(Profile.getCollection(), profile.getUuid(), profile);
                ProfileCache.updateProfile(profile);
                return;
            }

            event.setResult(ResultedEvent.ComponentResult.denied(Message.parse(Language.Error.PROFILE_LOAD)));
        });
    }

    @Subscribe
    public void onPlayerConnect(ServerConnectedEvent event) {
        var playerUuid = event.getPlayer().getUniqueId();
        var currentServer = event.getServer().getServerInfo().getName();
        var oPreviousServer = event.getPreviousServer();

        var redisProfile = ProfileUtil.fromCache(playerUuid);

        if (redisProfile == null) {
            return;
        }

        var profile = redisProfile.getProfile();
        var postman = Postman.getPostman();

        ProfileCache.updateStatus(playerUuid, currentServer);

        if (oPreviousServer.isEmpty()) {
            postman.sendTo("proxy", new ProxyPlayerConnectPayload(profile, currentServer));
            ProxyHandler.proxyPlayerConnect(profile, currentServer);
        } else {
            var previousServer = oPreviousServer.get().getServerInfo().getName();

            postman.sendTo("proxy", new ProxyPlayerSwitchPayload(profile, currentServer, previousServer));
            ProxyHandler.proxyPlayerSwitch(profile, currentServer, previousServer);
        }
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        var playerUuid = event.getPlayer().getUniqueId();

        if (event.getLoginStatus() != DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN) return;

        var redisProfile = ProfileUtil.fromCache(playerUuid);

        if (redisProfile == null) {
            return;
        }

        var profile = redisProfile.getProfile();
        var postman = Postman.getPostman();

        ProfileCache.updateStatus(playerUuid, null);

        var oCurrentServer = event.getPlayer().getCurrentServer();

        if (oCurrentServer.isPresent()) {
            var currentServer = oCurrentServer.get().getServerInfo().getName();

            postman.sendTo("proxy", new ProxyPlayerDisconnectPayload(profile, currentServer));
            ProxyHandler.proxyPlayerDisconnect(profile, currentServer);
        }
    }

}
