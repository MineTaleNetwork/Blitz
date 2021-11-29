package cc.minetale.blitz.listeners.pigeon;

import cc.minetale.blitz.Blitz;
import cc.minetale.commonlib.pigeon.payloads.minecraft.MessagePlayerPayload;
import cc.minetale.pigeon.annotations.PayloadHandler;
import cc.minetale.pigeon.annotations.PayloadListener;
import cc.minetale.pigeon.listeners.Listener;
import com.velocitypowered.api.proxy.Player;

@PayloadListener
public class PlayerListener implements Listener {

    @PayloadHandler
    public void onMessagePlayer(MessagePlayerPayload payload) {
        var player = Blitz.getBlitz().getServer().getPlayer(payload.getPlayer());

        player.ifPresent(value -> value.sendMessage(payload.getMessage()));
    }

}
