package cc.minetale.blitz;

import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.packet.PlayerListItem;
import com.velocitypowered.proxy.protocol.packet.PluginMessage;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collections;
import java.util.UUID;

public class Constants {

    static {
        var brandBuf = Unpooled.buffer();
        ProtocolUtils.writeString(brandBuf, "Limbo");

        BRAND_PACKET = new PluginMessage("minecraft:brand", brandBuf);
    }

    public static final PluginMessage BRAND_PACKET;

    public static final GameProfile.Property GRAY_TEXTURE = new GameProfile.Property(
            "textures",
            "ewogICJ0aW1lc3RhbXAiIDogMTYyMTU0MTc5NzE1NSwKICAicHJvZmlsZUlkIiA6ICI5OTdjZjFlMmY1NGQ0YzEyOWY2ZjU5ZTVlNjU1YjZmNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJpbzEyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkxN2I0ZWQyNzFhNzk1MmRhNTA3ZWE2ZDk3NDZhNmI2MzYyMjQ5Y2NjNTY1OTIyZjRlNDdkMWY5ZGEyYWIxNGYiCiAgICB9CiAgfQp9",
            "fHEkWk+c3f7vae1iUQPlo3dEMuesaRMlTq3scb4o0VZwvnvywFD6CKnBiCVfC2FB1WpMaGbtWA6vhRYO9yVB9JS1IYCIwaI8XCprZMyrnjO32H9vtvdcr+WDRf2t1+1dm1iCH5o5U6rxzbYaZ5mgOp2/49vazNJbrsq2wgZ1mGuEXHubO9ldQrXp5YV+1V/2eGSO+lNn6fOUixKlDiDpUtLIxGqS3Mhr9M2dqYX/zz1GFUB5IGKUQKNfhbvPwOCCpubpvXBcqJ9liFf334KghrRQK2UugLfF2jsgOSlvsFIrEzUq/dykZDg7WCyRbUkn6J5PPqzzCIKuoIBdl6GQpchHbMv8Z5avpP4llVU0PrN6pefr3W6pcq24a6BmcFBTZI2F8IFrvn5BtPpv24Sq0BAfzbKOtk5enhQzkYg4Zhxr8MpqVYVRejt8LkrEaCeb+1uFbhrSaN9ZuB2voI2bWzU4WRztBNF6vxQWOD5w66mLC+v71ZHWjwBxg5YmpYXf9IL9AsTGdGQqkYh4S5ECQ9zdFdTNL0dM2luNMqLzm8OlU+pdGUMEWCzI7aIR4e7DuPi+IZBy8hJfgWJZWzQkZqPbidLAaij3EYXpPvq9at5mXW4pLnDqIdVOF/SLINhP8TL+pukp4Tq5R/GKhiLTyu66qajRIdjYZFN4OqMU2Ys="
    );

    public static final PlayerListItem PLAYER_TAB_PACKET = new PlayerListItem(PlayerListItem.ADD_PLAYER,
            Collections.singletonList(new PlayerListItem.Item(UUID.randomUUID())
                    .setName("Limbo")
                    .setLatency(1)
                    .setGameMode(1)
                    .setProperties(Collections.singletonList(Constants.GRAY_TEXTURE))
                    .setDisplayName(Component.text("Limbo", NamedTextColor.GRAY))
            ));

}
