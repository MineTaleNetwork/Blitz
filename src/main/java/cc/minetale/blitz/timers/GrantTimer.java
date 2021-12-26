package cc.minetale.blitz.timers;

import cc.minetale.blitz.Blitz;
import cc.minetale.blitz.api.BlitzPlayer;
import cc.minetale.blitz.manager.PlayerManager;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.timer.Timer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class GrantTimer extends Timer {

    private final String grantId;
    private final UUID playerUuid;

    public GrantTimer(String grantId, UUID playerUuid, long remainingTime) {
        super(TimerType.COUNTDOWN, Blitz.getBlitz().getTimerManager());

        this.grantId = grantId;
        this.playerUuid = playerUuid;

        setDuration(remainingTime);
        start();
    }

    @Override
    public void tick() {}

    @Override
    public void onStart() {
        System.out.println("A remove grant timer has started!");
    }

    @Override
    public void onComplete() {
        BlitzPlayer.getBlitzPlayer(this.playerUuid)
                .thenAccept(blitzPlayer -> {
                    var profile = blitzPlayer.getProfile();

                    profile.getCachedGrants()
                            .stream()
                            .filter(grant -> grant.getId().equals(this.grantId))
                            .findFirst()
                            .ifPresent(grant -> {
                                if (!grant.isRemoved() && !grant.isDefault()) {
                                    var rank = grant.getRank();

                                    blitzPlayer.getProxyPlayer().ifPresent(player -> {
                                        player.sendMessage(MC.SEPARATOR_80);
                                        player.sendMessage(MC.notificationMessage("Grant",
                                                Component.text().append(
                                                        Component.text("Your '", NamedTextColor.GRAY),
                                                        Component.text(rank.getName(), rank.getColor()),
                                                        Component.text("' grant has expired.", NamedTextColor.GRAY)
                                                ).build()));
                                        player.sendMessage(MC.SEPARATOR_80);
                                    });

                                    profile.expireGrant(grant, System.currentTimeMillis());
                                    PlayerManager.updateProfile(profile);
                                }
                            });
                });
    }

    @Override
    public void onCancel() {}

    public static List<Timer> getTimers(UUID playerUuid) {
        return Blitz.getBlitz()
                .getTimerManager()
                .getTimers()
                .stream()
                .filter(timer -> {
                    if(timer instanceof GrantTimer grantTimer) {
                        return grantTimer.getPlayerUuid().equals(playerUuid);
                    }

                    return false;
                }).collect(Collectors.toList());
    }

    public static Optional<Timer> getTimer(String grantId) {
       return Blitz.getBlitz()
               .getTimerManager()
               .getTimers()
               .stream()
               .filter(timer -> {
                   if(timer instanceof GrantTimer grantTimer) {
                       return grantTimer.getGrantId().equals(grantId);
                   }

                   return false;
        }).findFirst();
    }

    @Override
    public boolean equals(Object object) {
        return this == object || object instanceof GrantTimer other && other.getGrantId().equals(this.grantId);
    }

}
