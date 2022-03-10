package cc.minetale.blitz.listener.pigeon;

import cc.minetale.blitz.Blitz;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.pigeon.payloads.party.*;
import cc.minetale.commonlib.util.Message;
import cc.minetale.pigeon.annotations.PayloadHandler;
import cc.minetale.pigeon.annotations.PayloadListener;
import cc.minetale.pigeon.listeners.Listener;
import net.kyori.adventure.text.Component;

@PayloadListener
public class PartyListener implements Listener {

    @PayloadHandler
    public void onPartyDisband(PartyDisbandPayload payload) {
        var party = payload.getParty();
        var server = Blitz.getBlitz().getServer();

        var message = Message.parse(Language.Party.PARTY_DISBANDED);

        for (var member : party.getMembers()) {
            var oPlayer = server.getPlayer(member.player());

            oPlayer.ifPresent(player -> player.sendMessage(message));
        }
    }

    @PayloadHandler
    public void onPartyChat(PartyChatPayload payload) {
        var profile = payload.getProfile();
        var party = payload.getParty();
        var server = Blitz.getBlitz().getServer();

        var message = Message.parse(Language.Party.PARTY_CHAT_FORMAT,
                profile.getColoredPrefix(),
                profile.getColoredName(),
                "",  // TODO
                payload.getMessage()
        );

        for (var member : party.getMembers()) {
            var oPlayer = server.getPlayer(member.player());

            oPlayer.ifPresent(player -> player.sendMessage(message));
        }
    }

    @PayloadHandler
    public void onPartyInvite(PartyRequestCreatePayload payload) {
        var playerProfile = payload.getInitiator();
        var targetProfile = payload.getTarget();
        var server = Blitz.getBlitz().getServer();

        var playerMessage = Message.parse(Language.Party.INVITE_PARTY, targetProfile.getChatFormat());
        var targetMessage = Message.parse(Language.Party.INVITE_TARGET, playerProfile.getChatFormat());

        for(var member : payload.getParty().getMembers()) {
            var oPlayer = server.getPlayer(member.player());

            oPlayer.ifPresent(player -> player.sendMessage(playerMessage));
        }

        var oTarget = server.getPlayer(targetProfile.getUuid());

        oTarget.ifPresent(target -> target.sendMessage(targetMessage));
    }

    @PayloadHandler
    public void onPartyJoin(PartyJoinPayload payload) {
        var party = payload.getParty();
        var profile = payload.getPlayer();
        var server = Blitz.getBlitz().getServer();

        var message = Message.parse(Language.Party.PARTY_JOIN, profile.getChatFormat());

        for (var member : party.getMembers()) {
            var oPlayer = server.getPlayer(member.player());

            oPlayer.ifPresent(player -> player.sendMessage(message));
        }
    }

    @PayloadHandler
    public void onPartyKick(PartyKickPayload payload) {
        var party = payload.getParty();
        var profile = payload.getPlayer();
        var server = Blitz.getBlitz().getServer();

        var message = Message.parse(Language.Party.PARTY_KICK, profile.getChatFormat());

        for (var member : party.getMembers()) {
            var oPlayer = server.getPlayer(member.player());

            oPlayer.ifPresent(player -> player.sendMessage(message));
        }
    }

    @PayloadHandler
    public void onPartyLeave(PartyLeavePayload payload) {
        var party = payload.getParty();
        var profile = payload.getPlayer();
        var server = Blitz.getBlitz().getServer();

        var message = Message.parse(Language.Party.PARTY_LEAVE, profile.getChatFormat());

        for (var member : party.getMembers()) {
            var oPlayer = server.getPlayer(member.player());

            oPlayer.ifPresent(player -> player.sendMessage(message));
        }
    }

    @PayloadHandler
    public void onPartySummon(PartySummonPayload payload) {
        // TODO
    }

    @PayloadHandler
    public void onPartyRoleChange(PartyRoleChangePayload payload) {
        var party = payload.getParty();
        var profile = payload.getPlayer();
        var newRole = payload.getNewRole();
        var oldRole = payload.getOldRole();
        var server = Blitz.getBlitz().getServer();

        Component message;

        if(oldRole.ordinal() < newRole.ordinal()) {
            message = Message.parse(Language.Party.PARTY_DEMOTE, profile.getChatFormat(), newRole.getReadable());
        } else {
            message = Message.parse(Language.Party.PARTY_PROMOTE, profile.getChatFormat(), newRole.getReadable());
        }

        for (var member : party.getMembers()) {
            var oPlayer = server.getPlayer(member.player());

            oPlayer.ifPresent(player -> player.sendMessage(message));
        }
    }

}
