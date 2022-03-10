package cc.minetale.blitz.listener.pigeon;

import cc.minetale.blitz.Blitz;
import cc.minetale.commonlib.pigeon.payloads.network.ProxyPlayerConnectPayload;
import cc.minetale.commonlib.pigeon.payloads.network.ProxyPlayerDisconnectPayload;
import cc.minetale.commonlib.pigeon.payloads.network.ProxyPlayerSwitchPayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentAddPayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentExpirePayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentRemovePayload;
import cc.minetale.pigeon.annotations.PayloadHandler;
import cc.minetale.pigeon.annotations.PayloadListener;
import cc.minetale.pigeon.listeners.Listener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;

@PayloadListener
public class GenericListener implements Listener {

    @PayloadHandler
    public void onProxyPlayerConnect(ProxyPlayerConnectPayload payload) {
        PigeonHandler.proxyPlayerConnect(payload.getProfile(), payload.getServer());
    }

    @PayloadHandler
    public void onProxyPlayerSwitch(ProxyPlayerSwitchPayload payload) {
        PigeonHandler.proxyPlayerSwitch(payload.getProfile(), payload.getServerTo(), payload.getServerFrom());
    }

    @PayloadHandler
    public void onProxyPlayerDisconnect(ProxyPlayerDisconnectPayload payload) {
        PigeonHandler.proxyPlayerDisconnect(payload.getProfile(), payload.getServer());
    }

    // TODO -> These should be done on the individual server
    @PayloadHandler
    public void onPunishmentAdd(PunishmentAddPayload payload) {
        var punishment = payload.getPunishment();
        var proxy = Blitz.getBlitz().getServer();
        var oPlayer = proxy.getPlayer(payload.getPlayer());

        if(oPlayer.isPresent()) {
            var player = oPlayer.get();

            switch (punishment.getType()) {
                case BAN -> player.disconnect(Component.join(JoinConfiguration.newlines(), punishment.getPunishmentMessage()));
                case MUTE -> {
                    for (var message : punishment.getPunishmentMessage()) {
                        player.sendMessage(message);
                    }
                }
            }
        }
    }

    @PayloadHandler
    public void onPunishmentExpire(PunishmentExpirePayload payload) {
        var punishment = payload.getPunishment();
        var proxy = Blitz.getBlitz().getServer();
        var oPlayer = proxy.getPlayer(payload.getPlayer());

        if(oPlayer.isPresent()) {
            var player = oPlayer.get();

            // TODO
            player.sendMessage(Component.text("Punishment Expired"));
        }
    }

    @PayloadHandler
    public void onPunishmentRemove(PunishmentRemovePayload payload) {
        var punishment = payload.getPunishment();
        var proxy = Blitz.getBlitz().getServer();
        var oPlayer = proxy.getPlayer(payload.getPlayer());

        if(oPlayer.isPresent()) {
            var player = oPlayer.get();

            // TODO
            player.sendMessage(Component.text("Punishment Removed"));
        }
    }

}
