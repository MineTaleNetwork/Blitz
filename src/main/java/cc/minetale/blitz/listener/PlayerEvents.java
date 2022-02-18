package cc.minetale.blitz.listener;

import cc.minetale.blitz.Blitz;
import cc.minetale.blitz.limbo.BlitzSessionHandler;
import cc.minetale.blitz.listener.pigeon.PigeonHandler;
import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.pigeon.payloads.network.ProxyPlayerConnectPayload;
import cc.minetale.commonlib.punishment.PunishmentType;
import cc.minetale.commonlib.util.Message;
import cc.minetale.commonlib.util.PigeonUtil;
import cc.minetale.commonlib.util.ProfileUtil;
import com.google.common.hash.Hashing;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlayerEvents {

//    @Subscribe(order = PostOrder.FIRST)
//    public void onPlayerKicked(KickedFromServerEvent event) {
//        var player = event.getPlayer();
//        var limbo = Blitz.getBlitz().getLimbo();
//
//        var reasonOptional = event.getServerKickReason();
//
//        if(reasonOptional.isPresent()) {
//            var reason = ((TextComponent) reasonOptional.get()).content();
//
//            if(reason.equalsIgnoreCase("limbo")) {
//                player.sendMessage(Component.text("You have been sent to limbo.", NamedTextColor.RED));
//                limbo.spawnPlayer(player, new BlitzSessionHandler(player));
//            }
//        }
//    }

    // TODO -> VALIDATE LIMBO WORKS
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
                        if (!staff.getTwoFactorKey().isEmpty() && !staff.isLocked()) {
                            staff.setLocked(true);
                            Blitz.getBlitz().getLimbo().spawnPlayer(player, new BlitzSessionHandler(player)); // TODO -> MAKE SURE THIS WORKS
                        }

                        profile.setCurrentAddress(hashedIP);
                    }

                    if(profile.getGrant().getRank().isStaff()) {
                        Blitz.getStaff().put(player.getUniqueId(), player);
                    }

                    profile.save().get();
                    ProfileCache.updateProfile(profile).get();
                    return;
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }

            event.setResult(ResultedEvent.ComponentResult.denied(Message.parse(Language.Error.PROFILE_LOAD)));
        });
    }

    @Subscribe
    public void onPlayerConnect(ServerConnectedEvent event) {
        var playerUuid = event.getPlayer().getUniqueId();
        var currentServer = event.getServer().getServerInfo().getName();
        var oPreviousServer = event.getPreviousServer();

        ProfileUtil.getCachedProfile(playerUuid)
                .thenAccept(cachedProfile -> {
                    if(cachedProfile == null) { return; }

                    var profile = cachedProfile.getProfile();

                    ProfileCache.updateStatus(playerUuid, currentServer);

                    if(oPreviousServer.isEmpty()) {
                        PigeonUtil.broadcast(new ProxyPlayerConnectPayload(profile, currentServer));
                        PigeonHandler.proxyPlayerConnect(profile, currentServer);
                    } else {
                        // TODO -> Server Switch & Staff Messages
                    }
                });
    }

//
//    @Subscribe
//    public void onPlayerDisconnect(DisconnectEvent event) {
//        var playerUuid = event.getPlayer().getUniqueId();
//
//        if (event.getLoginStatus() != DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN) return;
//
//        ProfileCache.updateStatus(playerUuid, null);
//
//        ProfileUtil.getCachedProfile(playerUuid)
//                .thenAccept(cachedProfile -> {
//                    var profile = cachedProfile.getProfile();
//
//                    if(profile.getFriends().size() != 0) {
//                        PigeonUtil.broadcast(new FriendLeftPayload(playerUuid));
//                    }
//                });
//    }

}
