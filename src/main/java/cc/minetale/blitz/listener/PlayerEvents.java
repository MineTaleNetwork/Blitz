package cc.minetale.blitz.listener;

import cc.minetale.blitz.Blitz;
import cc.minetale.blitz.BlitzSessionHandler;
import cc.minetale.blitz.Constants;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.PlayerListItem;
import io.netty.buffer.Unpooled;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboSessionHandler;
import net.elytrium.limboapi.api.event.LoginLimboRegisterEvent;
import net.elytrium.limboapi.api.player.GameMode;
import net.elytrium.limboapi.api.player.LimboPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class PlayerEvents {

    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerConnect(LoginLimboRegisterEvent event) {
        var player = event.getPlayer();
        var limbo = Blitz.getBlitz().getLimbo();

        event.addCallback(() -> limbo.spawnPlayer(player, new BlitzSessionHandler(player)));
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onPlayerKicked(KickedFromServerEvent event) {
        var player = event.getPlayer();
        var limbo = Blitz.getBlitz().getLimbo();

        var reasonOptional = event.getServerKickReason();

        if(reasonOptional.isPresent()) {
            var reason = ((TextComponent) reasonOptional.get()).content();

            if(reason.equalsIgnoreCase("limbo")) {
                player.sendMessage(Component.text("You have been sent to limbo.", NamedTextColor.RED));
                limbo.spawnPlayer(player, new BlitzSessionHandler(player));
            }
        }
    }

//    @Subscribe(order = PostOrder.FIRST)
//    public EventTask onPlayerLogin(LoginEvent event) {
//        return EventTask.async(() -> {
//            var player = event.getPlayer();
//
//            try {
//                var profile = ProfileUtil.retrieveProfile(player.getUniqueId(), player.getUsername()).get(3, TimeUnit.SECONDS);
//
//                if(profile != null) {
//                    profile.setUsername(player.getUsername());
//
//                    if (profile.getFirstSeen() == 0L)
//                        profile.setFirstSeen(System.currentTimeMillis());
//
//                    profile.expirePunishments();
//
//                    var punishment = profile.getActivePunishmentByType(PunishmentType.BAN);
//
//                    if (punishment != null) {
//                        player.disconnect(Component.join(
//                                JoinConfiguration.separator(Component.newline()),
//                                punishment.getPunishmentMessage()
//                        ));
//
//                        return;
//                    }
//
//                    profile.setLastSeen(System.currentTimeMillis());
//
//                    var ip = player.getRemoteAddress()
//                            .getAddress()
//                            .getHostAddress();
//
//                    var hashedIP = Hashing.sha256()
//                            .hashString(ip, StandardCharsets.UTF_8)
//                            .toString();
//
//                    if (profile.getCurrentAddress() == null)
//                        profile.setCurrentAddress(hashedIP);
//
//                    var staff = profile.getStaffProfile();
//
//                    if (!profile.getCurrentAddress().equals(hashedIP)) {
//                        if (!staff.getTwoFactorKey().isEmpty() && !staff.isLocked())
//                            staff.setLocked(true);
//
//                        profile.setCurrentAddress(hashedIP);
//                    }
//
//                    profile.save().get();
//                    Cache.getProfileCache().updateProfile(profile).get();
//                    return;
//                }
//            } catch (InterruptedException | ExecutionException | TimeoutException e) {
//                e.printStackTrace();
//            }
//
//            event.setResult(ResultedEvent.ComponentResult.denied(Message.parse(Language.Error.PROFILE_LOAD)));
//        });
//    }

//    @Subscribe
//    public void onPlayerConnect(ServerConnectedEvent event) {
//        var playerUuid = event.getPlayer().getUniqueId();
//        var server = event.getServer().getServerInfo();
//        var previousServer = event.getPreviousServer();
//
//        ProfileCache.updateStatus(playerUuid, server.getName());
//
//        if(previousServer.isEmpty()) {
//            ProfileUtil.getCachedProfile(playerUuid)
//                    .thenAccept(cachedProfile -> {
//                        var profile = cachedProfile.getProfile();
//
//                        if(profile.getFriends().size() != 0) {
//                            PigeonUtil.broadcast(new FriendJoinedPayload(playerUuid));
//                        }
//                    });
//        }
//    }
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
