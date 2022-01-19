package cc.minetale.blitz.api;

import cc.minetale.blitz.Blitz;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.Message;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter @Setter
public class BlitzPlayer {

    private final UUID uniqueId;
    private final Map<BlitzPlayer, PartyInvite> partyInvites; // BlitzPlayer -> Inviter | TODO -> BitCrack will rework this
//    private final Set<FriendRequest> sentInvites; TODO -> BitCrack will rework this
    private BlitzPlayer lastMessaged;
    private Profile profile;

    public BlitzPlayer(Profile profile) {
        this.uniqueId = profile.getUuid();
        this.partyInvites = new HashMap<>();
        this.profile = profile;
    }

    private BlitzPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.partyInvites = new HashMap<>();
    }

    public String getName() {
        return this.profile.getUsername();
    }

//    public static CompletableFuture<BlitzPlayer> getBlitzPlayer(UUID uniqueId) {
//        var player = PlayerManager.getCache().get(uniqueId);
//
//        if (player == null) {
//            player = new BlitzPlayer(uniqueId);
//
//            final var finalPlayer = player;
//
//            return PlayerManager.getProfile(uniqueId)
//                    .thenCompose(profile -> {
//                        finalPlayer.setProfile(profile);
//                        return CompletableFuture.completedFuture(finalPlayer);
//                    });
//        }
//
//        return CompletableFuture.completedFuture(player);
//    }

    public Optional<PartyInvite> getPartyInvite(BlitzPlayer player) {
        return Optional.ofNullable(this.partyInvites.get(player));
    }

    public void sendNotification(String prefix, Component message) {
        this.sendMessage(Message.notification(prefix, message));
    }

    public void sendMessage(@NotNull Component message) {
        getProxyPlayer().ifPresent(player -> player.sendMessage(message));
    }

    public Optional<Player> getProxyPlayer() {
        return Blitz.getBlitz().getProxyServer().getPlayer(this.uniqueId);
    }

    public boolean isOnline() {
        return getProxyPlayer().isPresent();
    }

    public Optional<Party> getParty() {
        return Party.getParties()
                .stream()
                .filter(party -> party.getMembers().contains(this))
                .findFirst();
    }

    public boolean isInParty() {
        return getParty().isPresent();
    }

    @Override
    public boolean equals(Object object) {
        return this == object || object instanceof BlitzPlayer other && other.getUniqueId().equals(this.uniqueId);
    }

    @Override
    public int hashCode() {
        return this.uniqueId.hashCode();
    }

}
