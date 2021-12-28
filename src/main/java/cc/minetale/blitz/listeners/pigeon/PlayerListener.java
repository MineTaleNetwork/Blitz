package cc.minetale.blitz.listeners.pigeon;

import cc.minetale.blitz.Blitz;
import cc.minetale.blitz.util.SoundsUtil;
import cc.minetale.commonlib.api.Grant;
import cc.minetale.commonlib.api.Punishment;
import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantAddPayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantRemovePayload;
import cc.minetale.commonlib.pigeon.payloads.minecraft.MessagePlayerPayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentAddPayload;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.TimeUtil;
import cc.minetale.pigeon.annotations.PayloadHandler;
import cc.minetale.pigeon.annotations.PayloadListener;
import cc.minetale.pigeon.listeners.Listener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;

@PayloadListener
public class PlayerListener implements Listener {

//    @PayloadHandler(requiredState = RequiredState.REQUEST)
//    public void onProfileRequest(ProfileRequestPayload payload) {
//        switch (payload.getType()) {
//            case SINGLE -> {
//                var uuid = payload.getId();
//                var name = payload.getName();
//
//                if (name != null && uuid != null) {
//                    PlayerManager.getProfile(uuid)
//                            .thenAccept(existingProfile -> {
//                                if (existingProfile != null) {
//                                    payload.sendResponse(new ProfileRequestPayload(
//                                            ProfileQueryResult.RETRIEVED,
//                                            Collections.singletonList(existingProfile)));
//                                    return;
//                                }
//
//                                var profile = Profile.createBlitzProfile(uuid, name);
//
//                                PlayerManager.createProfile(profile)
//                                        .thenAccept(result -> payload.sendResponse(new ProfileRequestPayload(
//                                                result,
//                                                Collections.singletonList(profile)
//                                        )));
//                            });
//                }
//
//                if (uuid != null) {
//                    PlayerManager.getProfile(payload.getId()).thenAccept(profile -> {
//                        if (profile != null) {
//                            payload.sendResponse(new ProfileRequestPayload(
//                                    ProfileQueryResult.RETRIEVED,
//                                    Collections.singletonList(profile)
//                            ));
//                        } else {
//                            payload.sendResponse(new ProfileRequestPayload(
//                                    ProfileQueryResult.NOT_FOUND,
//                                    Collections.emptyList()
//                            ));
//                        }
//                    });
//                    return;
//                }
//
//                if (name != null) {
//                    PlayerManager.getProfile(name)
//                            .thenAccept(profile -> {
//                                if (profile != null) {
//                                    payload.sendResponse(new ProfileRequestPayload(
//                                            ProfileQueryResult.RETRIEVED,
//                                            Collections.singletonList(profile)
//                                    ));
//                                } else {
//                                    payload.sendResponse(new ProfileRequestPayload(
//                                            ProfileQueryResult.NOT_FOUND,
//                                            Collections.emptyList()
//                                    ));
//                                }
//                            });
//                }
//            }
//
//            case BULK -> {
//                var names = payload.getNames();
//                var uuids = payload.getIds();
//
//                if (payload.areConnected()) {
//                    if ((uuids != null && !uuids.isEmpty()) && (names != null && !names.isEmpty())) {
//                        var areConnected = payload.areConnected();
//                        if (areConnected) {
//                            final var nIt = names.iterator();
//                            final var iIt = uuids.iterator();
//                            while (nIt.hasNext()) {
//                                if (!iIt.hasNext()) {
//                                    break;
//                                }
//
//                                var name = nIt.next();
//                                if (name == null || name.isEmpty()) {
//                                    continue;
//                                }
//
//                                var id = iIt.next();
//                                if (id == null) {
//                                    continue;
//                                }
//
//                                var profile = Profile.createBlitzProfile(id, name);
//
//                                PlayerManager.getProfile(id)
//                                        .thenAccept(existingProfile -> {
//                                            if (existingProfile != null) {
//                                                payload.sendResponse(new ProfileRequestPayload(
//                                                        ProfileQueryResult.RETRIEVED,
//                                                        Collections.singletonList(existingProfile)
//                                                ));
//                                                return;
//                                            }
//
//                                            PlayerManager.createProfile(profile)
//                                                    .thenAccept(result -> payload.sendResponse(
//                                                            new ProfileRequestPayload(result,
//                                                                    Collections.singletonList(profile))
//                                                    ));
//                                        });
//                            }
//                        }
//                    }
//                } else {
//                    if (uuids != null && !uuids.isEmpty()) {
//                        PlayerManager.getProfilesByIds(uuids)
//                                .thenAccept(profiles -> {
//                                    if (profiles != null && !profiles.isEmpty()) {
//                                        payload.sendResponse(new ProfileRequestPayload(
//                                                ProfileQueryResult.RETRIEVED,
//                                                profiles
//                                        ));
//                                    } else {
//                                        payload.sendResponse(new ProfileRequestPayload(
//                                                ProfileQueryResult.NOT_FOUND,
//                                                Collections.emptyList()
//                                        ));
//                                    }
//                                });
//                    }
//
//                    if (names != null && !names.isEmpty()) {
//                        PlayerManager.getProfilesByNames(names)
//                                .thenAccept(profiles -> {
//                                    if (profiles != null && !profiles.isEmpty()) {
//                                        payload.sendResponse(new ProfileRequestPayload(
//                                                ProfileQueryResult.RETRIEVED,
//                                                profiles
//                                        ));
//                                    } else {
//                                        payload.sendResponse(new ProfileRequestPayload(
//                                                ProfileQueryResult.NOT_FOUND,
//                                                Collections.emptyList()
//                                        ));
//                                    }
//                                });
//                    }
//                }
//            }
//        }
//    }

    @PayloadHandler
    public void onMessagePlayer(MessagePlayerPayload payload) {
        var player = Blitz.getBlitz().getServer().getPlayer(payload.getPlayer());

        player.ifPresent(value -> value.sendMessage(payload.getMessage()));
    }

    @PayloadHandler
    public void onGrantAdd(GrantAddPayload payload) {
        var optionalPlayer = Blitz.getBlitz().getServer().getPlayer(payload.getPlayerUuid());
        var grant = Grant.getGrant(payload.getGrant());

        if (optionalPlayer.isEmpty() || grant == null) return;

        var player = optionalPlayer.get();
        var rank = grant.getRank();

        if (rank != Rank.MEMBER) {
            player.sendMessage(MC.SEPARATOR_80);
            player.sendMessage(MC.notificationMessage("Grant",
                    Component.text().append(
                            Component.text("A '", NamedTextColor.GRAY),
                            Component.text(rank.getName(), rank.getColor()),
                            Component.text("' grant has been applied to you " +
                                    (grant.getDuration() == Integer.MAX_VALUE ? "permanently" :
                                            "for " + TimeUtil.millisToRoundedTime(grant.getDuration())) + ".", NamedTextColor.GRAY)
                    ).build()));
            player.sendMessage(MC.SEPARATOR_80);

            if (!grant.isPermanent()) {
//                PlayerManager.createTimers(player.getUniqueId());
            }
        }
    }

    @PayloadHandler
    public void onGrantRemove(GrantRemovePayload payload) {
        var optionalPlayer = Blitz.getBlitz().getServer().getPlayer(payload.getPlayerUuid());
        var grant = Grant.getGrant(payload.getGrant());

        if (optionalPlayer.isEmpty() || grant == null) return;

        var player = optionalPlayer.get();
        var rank = grant.getRank();

        if (rank != Rank.MEMBER) {
            player.sendMessage(MC.SEPARATOR_80);
            player.sendMessage(MC.notificationMessage("Grant",
                    Component.text().append(
                            Component.text("Your '", NamedTextColor.GRAY),
                            Component.text(rank.getName(), rank.getColor()),
                            Component.text("' grant has been removed.", NamedTextColor.GRAY)
                    ).build()));
            player.sendMessage(MC.SEPARATOR_80);
        }

        if(!grant.isPermanent()) {
//            GrantTimer.getTimer(grant.getId()).ifPresent(Timer::start);
        }
    }

    @PayloadHandler
    public void onPunishmentAdd(PunishmentAddPayload payload) {
        var optionalPlayer = Blitz.getBlitz().getServer().getPlayer(payload.getPlayerUuid());
        var punishment = Punishment.getPunishment(payload.getPunishment());

        if (optionalPlayer.isEmpty() || punishment == null) {
            return;
        }

        var player = optionalPlayer.get();

        if (!punishment.isRemoved()) {
            if (punishment.getType() == Punishment.Type.BAN || punishment.getType() == Punishment.Type.BLACKLIST) {
                player.disconnect(Component.join(JoinConfiguration.separator(Component.newline()), punishment.getPunishmentMessage()));
            } else if (punishment.getType() == Punishment.Type.MUTE) {
                punishment.getPunishmentMessage().forEach(player::sendMessage);
                SoundsUtil.playErrorSound(player);
            }
        }

    }

}
