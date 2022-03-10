package cc.minetale.blitz.listener.pigeon;

import cc.minetale.blitz.Blitz;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.pigeon.payloads.friend.FriendRemovePayload;
import cc.minetale.commonlib.pigeon.payloads.friend.FriendRequestAcceptPayload;
import cc.minetale.commonlib.pigeon.payloads.friend.FriendRequestCreatePayload;
import cc.minetale.commonlib.pigeon.payloads.friend.FriendRequestDenyPayload;
import cc.minetale.commonlib.util.Message;
import cc.minetale.pigeon.annotations.PayloadHandler;
import cc.minetale.pigeon.annotations.PayloadListener;
import cc.minetale.pigeon.listeners.Listener;

@PayloadListener
public class FriendListener implements Listener {

    @PayloadHandler
    public void onFriendRequestDeny(FriendRequestDenyPayload payload) {
        var profile = payload.getInitiator();
        var server = Blitz.getBlitz().getServer();

        var oPlayer = server.getPlayer(payload.getTarget());

        oPlayer.ifPresent(player -> player.sendMessage(Message.parse(Language.Friend.DENY_TARGET, profile.getChatFormat())));
    }

    @PayloadHandler
    public void onFriendRemove(FriendRemovePayload payload) {
        var profile = payload.getInitiator();
        var server = Blitz.getBlitz().getServer();

        var oPlayer = server.getPlayer(payload.getTarget());

        oPlayer.ifPresent(player -> player.sendMessage(Message.parse(Language.Friend.REMOVE_TARGET, profile.getChatFormat())));
    }

    @PayloadHandler
    public void onFriendRequestAccept(FriendRequestAcceptPayload payload) {
        var profile = payload.getInitiator();
        var server = Blitz.getBlitz().getServer();

        var oPlayer = server.getPlayer(payload.getTarget());

        oPlayer.ifPresent(player -> player.sendMessage(Message.parse(Language.Friend.ACCEPT_REQUEST, profile.getChatFormat())));
    }

    @PayloadHandler
    public void onFriendRequestCreate(FriendRequestCreatePayload payload) {
        var profile = payload.getInitiator();
        var server = Blitz.getBlitz().getServer();

        var oPlayer = server.getPlayer(payload.getTarget());

        oPlayer.ifPresent(player -> player.sendMessage(Message.parse(Language.Friend.INVITE_TARGET, profile.getChatFormat())));
    }

}
