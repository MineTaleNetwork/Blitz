package cc.minetale.blitz.limbo;

import com.velocitypowered.api.proxy.Player;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboSessionHandler;
import net.elytrium.limboapi.api.player.GameMode;
import net.elytrium.limboapi.api.player.LimboPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Arrays;

public record BlitzSessionHandler(Player player) implements LimboSessionHandler {

    @Override
    public void onChat(String chat) {
        // TODO
    }

    @Override
    public void onSpawn(Limbo server, LimboPlayer player) {
        player.disableFalling();
        player.setGameMode(GameMode.SPECTATOR);

        player.writePacket(Constants.BRAND_PACKET);
        player.writePacket(Constants.PLAYER_TAB_PACKET);
        player.flushPackets();

        this.player.sendPlayerListHeaderAndFooter(
                Component.join(JoinConfiguration.separator(Component.newline()), Arrays.asList(
                        Component.text("                                                "),
                        Component.text("MineTale Network", NamedTextColor.GOLD, TextDecoration.BOLD),
                        Component.text("                                                ")
                )),
                Component.join(JoinConfiguration.separator(Component.newline()), Arrays.asList(
                        Component.text("                                                "),
                        Component.text("??? online", NamedTextColor.GRAY),
                        Component.text("                                                ")
                ))
        );
    }

}
