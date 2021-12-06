package cc.minetale.blitz.listeners.pigeon;

import cc.minetale.blitz.Blitz;
import cc.minetale.blitz.api.BlitzPlayer;
import cc.minetale.blitz.manager.PlayerManager;
import cc.minetale.commonlib.api.Grant;
import cc.minetale.commonlib.api.Punishment;
import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantAddPayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantExpirePayload;
import cc.minetale.commonlib.pigeon.payloads.grant.GrantRemovePayload;
import cc.minetale.commonlib.pigeon.payloads.minecraft.MessagePlayerPayload;
import cc.minetale.commonlib.pigeon.payloads.profile.ProfileRequestPayload;
import cc.minetale.commonlib.pigeon.payloads.profile.ProfileUpdatePayload;
import cc.minetale.commonlib.pigeon.payloads.punishment.PunishmentAddPayload;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.profile.ProfileQueryResult;
import cc.minetale.commonlib.util.MC;
import cc.minetale.commonlib.util.PigeonUtil;
import cc.minetale.commonlib.util.TimeUtil;
import cc.minetale.pigeon.annotations.PayloadHandler;
import cc.minetale.pigeon.annotations.PayloadListener;
import cc.minetale.pigeon.feedback.RequiredState;
import cc.minetale.pigeon.listeners.Listener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collections;

@PayloadListener
public class PlayerListener implements Listener {

    @PayloadHandler(requiredState = RequiredState.REQUEST)
    public void onProfileRequest(ProfileRequestPayload payload) {
        System.out.println("Got a Profile Request");

        var playerManager = PlayerManager.getPlayerManager();

        switch (payload.getType()) {
            case SINGLE -> {
                playerManager.getProfile(payload.getId()).thenAccept(profile -> {
                    payload.sendResponse(new ProfileRequestPayload(ProfileQueryResult.RETRIEVED, Collections.singletonList(profile)));
                });
            }

            case BULK -> {}
        }


//        if(type == ProfileRequestPayload.Type.SINGLE) {
//            var name = payload.getName();
//            var id = payload.getId();
//
//            if(id != null && name != null) {
//                profilesManager
//                        .getProfile(id)
//                        .thenAccept(existingProfile -> {
//                            if(existingProfile != null) {
//                                payload.sendResponse(new ProfileRequestPayload(ProfileQueryResult.RETRIEVED, Collections.singletonList(existingProfile)));
//                                return;
//                            }
//
//                            var profile = new Profile(name, id);
//                            profilesManager
//                                    .createProfile(profile)
//                                    .thenAccept(result -> payload.sendResponse(new ProfileRequestPayload(result, Collections.singletonList(profile))));
//                        });
//            } else if(id != null) {
//                profilesManager
//                        .getProfile(id)
//                        .thenAccept(profile -> {
//                            if(profile != null) {
//                                payload.sendResponse(new ProfileRequestPayload(
//                                        ProfileQueryResult.RETRIEVED,
//                                        Collections.singletonList(profile)));
//                            } else {
//                                payload.sendResponse(new ProfileRequestPayload(
//                                        ProfileQueryResult.NOT_FOUND,
//                                        Collections.emptyList()));
//                            }
//                        });
//            } else if(name != null) {
//                profilesManager
//                        .getProfile(name)
//                        .thenAccept(profile -> {
//                            if(profile != null) {
//                                payload.sendResponse(new ProfileRequestPayload(
//                                        ProfileQueryResult.RETRIEVED,
//                                        Collections.singletonList(profile)));
//                            } else {
//                                payload.sendResponse(new ProfileRequestPayload(
//                                        ProfileQueryResult.NOT_FOUND,
//                                        Collections.emptyList()));
//                            }
//                        });
//            }
//        } else if(type == ProfileRequestPayload.Type.BULK) {
//            var names = payload.getNames();
//            var ids = payload.getIds();
//
//            if(payload.areConnected()) {
//                if((ids != null && !ids.isEmpty()) && (names != null && !names.isEmpty())) {
//                    var areConnected = payload.areConnected();
//                    if(areConnected) {
//                        final var nIt = names.iterator();
//                        final var iIt = ids.iterator();
//                        while(nIt.hasNext()) {
//                            if(!iIt.hasNext()) { break; }
//
//                            var name = nIt.next();
//                            if(name == null || name.isEmpty()) { continue; }
//
//                            var id = iIt.next();
//                            if(id == null) { continue; }
//
//                            var profile = new Profile(name, id);
//                            profilesManager
//                                    .getProfile(id)
//                                    .thenAccept(existingProfile -> {
//                                        if(existingProfile != null) {
//                                            payload.sendResponse(new ProfileRequestPayload(ProfileQueryResult.RETRIEVED,
//                                                    Collections.singletonList(existingProfile)));
//                                            return;
//                                        }
//
//                                        profilesManager
//                                                .createProfile(profile)
//                                                .thenAccept(result -> payload.sendResponse(new ProfileRequestPayload(result,
//                                                        Collections.singletonList(profile))));
//                                    });
//                        }
//                    }
//                }
//            } else {
//                if(ids != null && !ids.isEmpty()) {
//                    profilesManager
//                            .getProfilesByIds(ids)
//                            .thenAccept(profiles -> {
//                                if(profiles != null && !profiles.isEmpty()) {
//                                    payload.sendResponse(new ProfileRequestPayload(
//                                            ProfileQueryResult.RETRIEVED,
//                                            profiles));
//                                } else {
//                                    payload.sendResponse(new ProfileRequestPayload(
//                                            ProfileQueryResult.NOT_FOUND,
//                                            Collections.emptyList()));
//                                }
//                            });
//                }
//
//                if(names != null && !names.isEmpty()) {
//                    profilesManager
//                            .getProfilesByNames(names)
//                            .thenAccept(profiles -> {
//                                if(profiles != null && !profiles.isEmpty()) {
//                                    payload.sendResponse(new ProfileRequestPayload(
//                                            ProfileQueryResult.RETRIEVED,
//                                            profiles));
//                                } else {
//                                    payload.sendResponse(new ProfileRequestPayload(
//                                            ProfileQueryResult.NOT_FOUND,
//                                            Collections.emptyList()));
//                                }
//                            });
//                }
//            }
//        }
    }

    @PayloadHandler
    public void onMessagePlayer(MessagePlayerPayload payload) {
        var player = Blitz.getBlitz().getServer().getPlayer(payload.getPlayer());

        player.ifPresent(value -> value.sendMessage(payload.getMessage()));
    }

    @PayloadHandler
    public void onProfileUpdateUpdate(ProfileUpdatePayload payload) {
        var profile = payload.getProfile();

        BlitzPlayer.getBlitzPlayer(profile.getId()).thenAccept(player -> player.setProfile(profile));
        profile.validate();

        PigeonUtil.broadcast(new ProfileUpdatePayload(profile, result -> {}));
    }

    @PayloadHandler
    public void onGrantAdd(GrantAddPayload payload) {
        var optionalPlayer = Blitz.getBlitz().getServer().getPlayer(payload.getPlayerUuid());
        var grant = Grant.getGrant(payload.getGrant());

        if(optionalPlayer.isEmpty() || grant == null) { return; }

        var player = optionalPlayer.get();
        var rank = grant.getRank();

        if(rank != Rank.DEFAULT) {
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
        }
    }

    @PayloadHandler
    public void onGrantExpire(GrantExpirePayload payload) {
        var optionalPlayer = Blitz.getBlitz().getServer().getPlayer(payload.getPlayerUuid());
        var grant = Grant.getGrant(payload.getGrant());

        if(optionalPlayer.isEmpty() || grant == null) { return; }

        var player = optionalPlayer.get();
        var rank = grant.getRank();

        if(rank != Rank.DEFAULT) {
            player.sendMessage(MC.SEPARATOR_80);
            player.sendMessage(MC.notificationMessage("Grant",
                    Component.text().append(
                            Component.text("Your '", NamedTextColor.GRAY),
                            Component.text(rank.getName(), rank.getColor()),
                            Component.text("' grant has expired.", NamedTextColor.GRAY)
                    ).build()));
            player.sendMessage(MC.SEPARATOR_80);
        }
    }

    @PayloadHandler
    public void onGrantRemove(GrantRemovePayload payload) {
        var optionalPlayer = Blitz.getBlitz().getServer().getPlayer(payload.getPlayerUuid());
        var grant = Grant.getGrant(payload.getGrant());

        if(optionalPlayer.isEmpty() || grant == null) { return; }

        var player = optionalPlayer.get();
        var rank = grant.getRank();

        if(rank != Rank.DEFAULT) {
            player.sendMessage(MC.SEPARATOR_80);
            player.sendMessage(MC.notificationMessage("Grant",
                    Component.text().append(
                            Component.text("Your '", NamedTextColor.GRAY),
                            Component.text(rank.getName(), rank.getColor()),
                            Component.text("' grant has been removed.", NamedTextColor.GRAY)
                    ).build()));
            player.sendMessage(MC.SEPARATOR_80);
        }
    }

    @PayloadHandler
    public void onPunishmentAdd(PunishmentAddPayload payload) {
        var optionalPlayer = Blitz.getBlitz().getServer().getPlayer(payload.getPlayerUuid());
        var punishment = Punishment.getPunishment(payload.getPunishment());

        if(optionalPlayer.isEmpty() || punishment == null) { return; }

        var player = optionalPlayer.get();

        if(!punishment.isRemoved()) {
            if (punishment.getType() == Punishment.Type.BAN || punishment.getType() == Punishment.Type.BLACKLIST) {
//                player.kick(Component.join(JoinConfiguration.separator(Component.newline()), FlameUtil.getPunishmentMessage(punishment, true)));
            } else if (punishment.getType() == Punishment.Type.MUTE) {
//                FlameUtil.getPunishmentMessage(punishment, true).forEach(player::sendMessage);
//                SoundsUtil.playErrorSound(player);
            }
        }

    }

}
