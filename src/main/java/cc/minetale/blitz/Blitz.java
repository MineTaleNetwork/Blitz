package cc.minetale.blitz;

import cc.minetale.blitz.listener.PlayerEvents;
import cc.minetale.blitz.listener.pigeon.FriendListener;
import cc.minetale.blitz.listener.pigeon.GenericListener;
import cc.minetale.blitz.listener.pigeon.PartyListener;
import cc.minetale.commonlib.CommonLib;
import cc.minetale.pigeon.Pigeon;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;

import java.util.Arrays;

@Getter
public class Blitz {

    @Getter private static Blitz blitz;
    private final ProxyServer server;

    @Inject
    public Blitz(ProxyServer server) {
        blitz = this;

        this.server = server;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        CommonLib.init();

        Arrays.asList(
                new GenericListener(),
                new PartyListener(),
                new FriendListener()
        ).forEach(Pigeon.getPigeon().getListenersRegistry()::registerListener);

        Arrays.asList(
                new PlayerEvents()
        ).forEach(proxyEvent -> server.getEventManager().register(this, proxyEvent));
    }

}
