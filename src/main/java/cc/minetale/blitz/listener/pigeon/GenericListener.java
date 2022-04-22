package cc.minetale.blitz.listener.pigeon;

import cc.minetale.blitz.Blitz;
import cc.minetale.blitz.listener.ProxyHandler;
import cc.minetale.postman.payload.PayloadHandler;
import cc.minetale.postman.payload.PayloadListener;
import cc.minetale.sodium.payloads.GrantPayload;
import cc.minetale.sodium.payloads.PunishmentAddPayload;
import cc.minetale.sodium.payloads.proxy.ProxyPlayerConnectPayload;
import cc.minetale.sodium.payloads.proxy.ProxyPlayerDisconnectPayload;
import cc.minetale.sodium.payloads.proxy.ProxyPlayerSwitchPayload;

public class GenericListener implements PayloadListener {

    @PayloadHandler
    public void onProxyPlayerConnect(ProxyPlayerConnectPayload payload) {
        ProxyHandler.proxyPlayerConnect(payload.getProfile(), payload.getServer());
    }

    @PayloadHandler
    public void onProxyPlayerSwitch(ProxyPlayerSwitchPayload payload) {
        ProxyHandler.proxyPlayerSwitch(payload.getProfile(), payload.getServerTo(), payload.getServerFrom());
    }

    @PayloadHandler
    public void onProxyPlayerDisconnect(ProxyPlayerDisconnectPayload payload) {
        ProxyHandler.proxyPlayerDisconnect(payload.getProfile(), payload.getServer());
    }

    @PayloadHandler
    public void onGrant(GrantPayload payload) {
        var oPlayer = Blitz.getBlitz().getServer().getPlayer(payload.getPlayer());

        oPlayer.ifPresent(player -> {
            var grant = payload.getGrant();

            switch (payload.getAction()) {
                case ADD -> ProxyHandler.addGrant(player, grant);
                case EXPIRE -> ProxyHandler.expireGrant(player, grant);
                case REMOVE -> ProxyHandler.removeGrant(player, grant);
            }
        });
    }

    @PayloadHandler
    public void onProxyPlayerDisconnect(PunishmentAddPayload payload) {
        var oPlayer = Blitz.getBlitz().getServer().getPlayer(payload.getPlayer());

        oPlayer.ifPresent(player -> ProxyHandler.addPunishment(player, payload.getPunishment()));
    }

}
