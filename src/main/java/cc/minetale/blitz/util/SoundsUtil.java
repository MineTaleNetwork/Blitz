package cc.minetale.blitz.util;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class SoundsUtil {

    public static void playClickSound(Player player) {
        player.playSound(Sound.sound(Key.key("minecraft:ui.button.click"), Sound.Source.MASTER, 1F, 2.0F));
    }

    public static void playErrorSound(Player player) {
        player.playSound(Sound.sound(Key.key("block.note_block.bass"), Sound.Source.MASTER, 1F, 0.5F));
    }

    public static void playMessageSound(Player player) {
        player.playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1F, 0.5F));
    }

}
