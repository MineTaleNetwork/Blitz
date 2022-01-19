package cc.minetale.blitz.api;

import cc.minetale.commonlib.util.Colors;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.*;

@Getter
public class Party {

    @Getter private static final Set<Party> parties = new HashSet<>();

    private final UUID uniqueId;
    private final List<BlitzPlayer> members;
    private final Long createdAt;
    private BlitzPlayer leader;

    public Party(BlitzPlayer leader) {
        this.uniqueId = UUID.randomUUID();
        this.leader = leader;
        this.members = new ArrayList<>(Collections.singletonList(leader));
        this.createdAt = System.currentTimeMillis();

        parties.add(this);
    }

    public static Optional<Party> getPartyByUniqueId(UUID uniquePartyId) {
        return parties.stream()
                .filter(party -> party.getUniqueId()
                        .equals(uniquePartyId))
                .findFirst();
    }

    public static Optional<Party> getPartyByMember(BlitzPlayer player) {
        return parties.stream()
                .filter(party -> party.getMembers()
                        .contains(player))
                .findFirst();
    }

    // TODO -> Needs additional code (Like messages)
    public void setLeader(BlitzPlayer player) {
        this.leader = player;
    }

    public void addMember(BlitzPlayer player) {
        if (this.members.contains(player)) {
            player.sendNotification("Party", Component.text("You are already in that party.", NamedTextColor.RED));
            return;
        }

        this.members.add(player);

        this.sendPartyMessage(Component.text().append(
                player.getProfile().getChatFormat(),
                Component.text(" has joined the party.", NamedTextColor.GREEN)
        ).build());
    }

    // TODO -> Was removed from the party
    public void removeMember(BlitzPlayer player) {
        this.getMembers().remove(player);
    }

    public void sendPartyMessage(BlitzPlayer player, String message) {
        var profile = player.getProfile();
        var color = profile.getGrant().getRank().getColor();

        this.sendPartyMessage(
                Component.text().append(
                        profile.getChatFormat(),
                        Component.text(" » ", NamedTextColor.DARK_GRAY),
                        Component.text(message, Colors.bleach(color, 0.80))
                ).build());
    }

    public void sendPartyMessage(Component message) {
        for (var player : this.getMembers()) {
            player.sendNotification("Party", message);
        }
    }

    public void summonParty(RegisteredServer server, boolean forced) {
        if(forced) {
            for (var player : this.members) {
                player.getProxyPlayer()
                        .ifPresent(proxyPlayer -> proxyPlayer.createConnectionRequest(server)
                                .fireAndForget());
            }
        } else {
            // TODO
        }
    }

    public boolean isDisbanded() {
        return !parties.contains(this);
    }

    public void disbandParty(String reason) {
        this.sendPartyMessage(Component.text(reason, NamedTextColor.RED));

        parties.remove(this);
    }

    @Override
    public boolean equals(Object object) {
        return this == object || object instanceof Party other && other.getUniqueId().equals(this.uniqueId);
    }

    @Override
    public int hashCode() {
        return this.uniqueId.hashCode();
    }

}
