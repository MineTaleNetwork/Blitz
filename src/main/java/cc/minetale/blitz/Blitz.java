package cc.minetale.blitz;

import cc.minetale.blitz.listener.PlayerEvents;
import cc.minetale.commonlib.CommonLib;
import cc.minetale.commonlib.cache.ProfileCache;
import cc.minetale.commonlib.util.Cache;
import cc.minetale.commonlib.util.Redis;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboFactory;
import net.elytrium.limboapi.api.chunk.Dimension;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Getter
public class Blitz {

    @Getter private static Blitz blitz;
    private final ProxyServer server;
    private Limbo limbo;

    @Inject
    public Blitz(ProxyServer server) {
        blitz = this;

        this.server = server;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        CommonLib.init();

        var factory = (LimboFactory) server.getPluginManager().getPlugin("limboapi").flatMap(PluginContainer::getInstance).orElseThrow();
        this.limbo = factory.createLimbo(factory.createVirtualWorld(Dimension.THE_END, 0, 0, 0, 0, 0)).setName("Limbo");

        Arrays.asList(
                new PlayerEvents()
        ).forEach(proxyEvent -> server.getEventManager().register(this, proxyEvent));

        server.getScheduler()
                .buildTask(this, () -> CompletableFuture.runAsync(() -> Redis.runRedisCommand(jedis -> {
                    var pipeline = jedis.pipelined();

                    for(var player : server.getAllPlayers()) {
                        pipeline.expire(Cache.getProfileCache().getKey(player.getUniqueId().toString()), TimeUnit.HOURS.toSeconds(12));
                    }

                    pipeline.sync();

                    return null;
                })))
                .repeat(120L, TimeUnit.MINUTES)
                .schedule();
    }

}
