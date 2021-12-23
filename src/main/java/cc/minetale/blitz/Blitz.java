package cc.minetale.blitz;

import cc.minetale.blitz.api.StaffMembers;
import cc.minetale.blitz.listeners.pigeon.PlayerListener;
import cc.minetale.blitz.listeners.velocity.PlayerEvents;
import cc.minetale.blitz.manager.PlayerManager;
import cc.minetale.commonlib.CommonLib;
import cc.minetale.pigeon.Pigeon;
import com.google.inject.Inject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.slf4j.Logger;

import java.util.Arrays;

@Getter
public class Blitz {

    @Getter private static Blitz blitz;
    private final ProxyServer server;
    private final Logger logger;

    private Pigeon pigeon;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    @Inject
    public Blitz(ProxyServer server, Logger logger) {
        Blitz.blitz = this;

        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        long start = System.currentTimeMillis();

        this.loadPigeon();
        this.loadMongo();

        new PlayerManager(CacheManagerBuilder.newCacheManagerBuilder().build(true));
        new CommonLib(this.mongoClient, this.mongoDatabase, this.pigeon);

        server.getEventManager().register(this, new PlayerEvents());

        this.logger.info("Done (" + (System.currentTimeMillis() - start) + "ms)! Blitz has successfully been initialized!");
    }

    private void loadMongo() {
        this.mongoClient = new MongoClient(
                System.getProperty("mongoHost", "127.0.0.1"),
                Integer.getInteger("mongoPort", 27017)
        );

        this.mongoDatabase = this.mongoClient.getDatabase(
                System.getProperty("mongoDatabase", "MineTale")
        );
    }

    private void loadPigeon() {
        this.pigeon = new Pigeon();

        this.pigeon.initialize(
                System.getProperty("pigeonHost", "127.0.0.1"),
                Integer.getInteger("pigeonPort", 5672),
                System.getProperty("pigeonNetwork", "minetale"),
                System.getProperty("pigeonUnit", "blitz")
        );

        Arrays.asList(
                new PlayerListener()
        ).forEach(listener -> this.pigeon.getListenersRegistry().registerListener(listener));

        this.pigeon.setupDefaultUpdater();

        this.pigeon.acceptDelivery();
    }

}
