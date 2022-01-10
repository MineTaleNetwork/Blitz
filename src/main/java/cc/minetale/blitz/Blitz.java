package cc.minetale.blitz;

import cc.minetale.blitz.listeners.pigeon.PlayerListener;
import cc.minetale.blitz.listeners.velocity.PlayerEvents;
import cc.minetale.commonlib.CommonLib;
import cc.minetale.pigeon.Pigeon;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.slf4j.Logger;

@Getter
public class Blitz {

    @Getter private static Blitz blitz;
    private final ProxyServer proxyServer;
    private final Logger logger;

    @Inject
    public Blitz(ProxyServer proxyServer, Logger logger) {
        blitz = this;

        this.proxyServer = proxyServer;
        this.logger = logger;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        long start = System.currentTimeMillis();

        CommonLib.init();
        Pigeon.getPigeon().getListenersRegistry().registerListener(new PlayerListener());

        this.proxyServer.getEventManager().register(this, new PlayerEvents());
        System.out.println("Done (" + (System.currentTimeMillis() - start) + "ms)! Blitz has successfully been initialized!");
    }

}
