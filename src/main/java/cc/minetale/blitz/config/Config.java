package cc.minetale.blitz.config;

import lombok.Getter;

@Getter
public class Config {

    private String name = "Proxy-1";

    private String networkId = "minetale";

    private String mongoHost = "127.0.0.1";
    private Integer mongoPort = 27017;
    private String mongoDatabase = "MineTale";

    private String rabbitMqHost = "127.0.0.1";
    private Integer rabbitMqPort = 5672;

}
