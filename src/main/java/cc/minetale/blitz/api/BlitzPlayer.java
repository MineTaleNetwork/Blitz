package cc.minetale.blitz.api;

import cc.minetale.blitz.Blitz;
import cc.minetale.blitz.manager.PlayerManager;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.profile.ProfileQueryResult;
import cc.minetale.commonlib.util.MC;
import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter @Setter
public class BlitzPlayer implements ForwardingAudience {

    private final UUID uniqueId;
    private final Map<BlitzPlayer, PartyInvite> partyInvites; // BlitzPlayer -> Inviter | TODO -> BitCrack will rework this
//    private final Set<FriendRequest> sentInvites; TODO -> BitCrack will rework this
    private BlitzPlayer lastMessaged;
    private Profile profile;

    public BlitzPlayer(Profile profile) {
        this.uniqueId = profile.getId();
        this.partyInvites = new HashMap<>();
        this.profile = profile;
    }

    private BlitzPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.partyInvites = new HashMap<>();
    }

    public String getName() {
        return this.profile.getName();
    }

    public static CompletableFuture<BlitzPlayer> getBlitzPlayer(UUID uniqueId) {
        var manager = PlayerManager.getPlayerManager();
        var player = manager.getCache().get(uniqueId);

        if (player == null) {
            player = new BlitzPlayer(uniqueId);

            final var finalPlayer = player;

            return manager.getProfile(uniqueId).thenCompose(profile -> {
                finalPlayer.setProfile(profile);
                return CompletableFuture.completedFuture(finalPlayer);
            });
        }

        return CompletableFuture.completedFuture(player);
    }

    public Optional<PartyInvite> getPartyInvite(BlitzPlayer player) {
        return Optional.ofNullable(this.partyInvites.get(player));
    }

    public void sendNotification(String prefix, Component message) {
        this.sendMessage(MC.Chat.notificationMessage(prefix, message));
    }

    public void sendMessage(@NotNull Component message) {
        getProxyPlayer().ifPresent(player -> player.sendMessage(message));
    }

    public Optional<Player> getProxyPlayer() {
        return Blitz.getBlitz().getServer().getPlayer(this.uniqueId);
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

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        var proxyPlayer = getProxyPlayer();

        if(proxyPlayer.isPresent()) {
            return Collections.singletonList(proxyPlayer.get());
        } else {
            return Collections.emptyList();
        }
    }
}