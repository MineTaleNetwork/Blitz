package cc.minetale.blitz;

import cc.minetale.blitz.listeners.pigeon.PlayerListener;
import cc.minetale.blitz.listeners.velocity.PlayerEvents;
import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.api.Pterodactyl;
import cc.minetale.commonlib.server.ServerType;
import cc.minetale.pigeon.Pigeon;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.Getter;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Getter
public class Blitz {

    @Getter private static Blitz blitz;
    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Map<ServerType, Set<RegisteredServer>> serverMap;

    @Inject
    public Blitz(ProxyServer proxyServer, Logger logger) {
        blitz = this;

        this.proxyServer = proxyServer;
        this.logger = logger;
        this.serverMap = new HashMap<>();

        for(var type : ServerType.values()) {
            this.serverMap.put(type, new HashSet<>());
        }
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        long start = System.currentTimeMillis();

        CommonLib.init();

        Pigeon.getPigeon().getListenersRegistry().registerListener(new PlayerListener());

        try {
            var servers = Pterodactyl.getServers().get(5, TimeUnit.SECONDS);

            for(var server : servers) {
                var allocation = Pterodactyl.getPteroApplication().retrieveAllocationById(server.getDefaultAllocationId()).execute();
                var type = ServerType.valueOf(server.getExternalId().split(":")[1]);
                var registeredServer = this.proxyServer.registerServer(new ServerInfo(server.getName(), new InetSocketAddress(allocation.getIP(), allocation.getPortInt())));

                System.out.println("Successfully Registered: " + server.getName() + " of type " + type.name());
                this.serverMap.get(type).add(registeredServer);
            }

            for(var server : this.serverMap.get(ServerType.HUB)) {
                this.proxyServer.getConfiguration().getAttemptConnectionOrder().add(server.getServerInfo().getName());
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }

        this.proxyServer.getEventManager().register(this, new PlayerEvents());
        System.out.println("Done (" + (System.currentTimeMillis() - start) + "ms)! Blitz has successfully been initialized!");
    }

}
