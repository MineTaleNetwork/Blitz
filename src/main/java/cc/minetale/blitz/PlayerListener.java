package cc.minetale.blitz;

import cc.minetale.commonlib.modules.pigeon.payloads.network.ProxyPlayerConnectPayload;
import cc.minetale.commonlib.modules.pigeon.payloads.network.ProxyPlayerDisconnectPayload;
import cc.minetale.commonlib.modules.pigeon.payloads.network.ProxyPlayerSwitchPayload;
import cc.minetale.commonlib.util.PigeonUtil;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import java.util.Optional;

public class PlayerListener {

    @Subscribe
    public void onPlayerConnect(ServerPostConnectEvent event) {
        Player player = event.getPlayer();
        Optional<ServerConnection> optionalServer = player.getCurrentServer();

        if(optionalServer.isPresent()) {
            ServerInfo server = optionalServer.get().getServerInfo();
            RegisteredServer previousServer = event.getPreviousServer();

            if(previousServer == null) {
                PigeonUtil.broadcast(new ProxyPlayerConnectPayload(
                        player.getUniqueId(),
                        player.getUsername(),
                        server.getName()
                ));
            } else {
                PigeonUtil.broadcast(new ProxyPlayerSwitchPayload(
                        player.getUniqueId(),
                        player.getUsername(),
                        previousServer.getServerInfo().getName(),
                        server.getName()
                ));
            }
        }
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        if (event.getLoginStatus() == DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN) {
            Player player = event.getPlayer();

            player.getCurrentServer().ifPresent(server -> PigeonUtil.broadcast(new ProxyPlayerDisconnectPayload(
                    player.getUniqueId(),
                    player.getUsername(),
                    server.getServerInfo().getName()
            )));
        }
    }

}
