package cc.minetale.blitz;

import cc.minetale.blitz.config.Config;
import cc.minetale.blitz.config.ConfigLoader;
import cc.minetale.commonlib.CommonLib;
import cc.minetale.pigeon.Pigeon;
import com.google.inject.Inject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

@Plugin(
        id = "blitz",
        name = "Blitz",
        version = "1.0",
        authors = { "oHate", "BitCrack" }
)
public class Blitz {

    private final ProxyServer server;
    private final Logger logger;
    private Config config;
    private Pigeon pigeon;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private CommonLib commonLib;

    @Inject
    public Blitz(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

        try {
            File configFile = new File("blitz.json");
            Config config = ConfigLoader.loadConfig(new Config(), configFile);

            if(config != null) {
                this.config = config;

                this.loadPigeon();
                this.loadMongo();

                this.commonLib = new CommonLib(this.mongoClient, this.mongoDatabase, this.pigeon);
            } else {
                this.server.shutdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.server.shutdown();
        }
    }

    private void loadPigeon() {
        String host = this.config.getRabbitMqHost();
        int port = this.config.getRabbitMqPort();

        this.pigeon = new Pigeon();
        this.pigeon.initialize(host, port, this.config.getNetworkId(), this.config.getName());
        this.pigeon.setupDefaultUpdater();
    }

    private void loadMongo() {
        this.mongoClient = new MongoClient(this.config.getMongoHost(), this.config.getMongoPort());
        this.mongoDatabase = this.mongoClient.getDatabase(this.config.getMongoDatabase());
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        this.server.getEventManager().register(this, new PlayerListener());

        this.logger.info("Blitz has successfully been initialized.");
    }

}
