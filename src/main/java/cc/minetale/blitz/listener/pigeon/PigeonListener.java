package cc.minetale.blitz.listener.pigeon;

import cc.minetale.blitz.listener.pigeon.PigeonHandler;
import cc.minetale.commonlib.pigeon.payloads.network.ProxyPlayerConnectPayload;
import cc.minetale.pigeon.annotations.PayloadHandler;
import cc.minetale.pigeon.annotations.PayloadListener;
import cc.minetale.pigeon.listeners.Listener;

@PayloadListener
public class PigeonListener implements Listener {

    @PayloadHandler
    public void onProxyPlayerConnect(ProxyPlayerConnectPayload payload) {
        PigeonHandler.proxyPlayerConnect(payload.getProfile());
    }

}
