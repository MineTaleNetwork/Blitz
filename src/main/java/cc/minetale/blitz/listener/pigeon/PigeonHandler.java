package cc.minetale.blitz.listener.pigeon;

import cc.minetale.blitz.Blitz;
import cc.minetale.blitz.Staff;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.Message;

public class PigeonHandler {

    // TODO -> Dont send a staff message and a friend message

    public static void proxyPlayerConnect(Profile profile, String currentServer) {
        var proxy = Blitz.getBlitz().getServer();

        var staffMessage = Message.parse(Language.Staff.STAFF_JOIN, profile.getChatFormat(), currentServer);

        if(profile.isStaff()) {
            Staff.getMembers().forEach(player -> player.sendMessage(staffMessage));
        }

        var friendMessage = Message.parse(Language.Friend.JOINED_NETWORK, profile.getChatFormat());

        for(var friend : profile.getFriends()) {
            var oPlayer = proxy.getPlayer(friend);

            oPlayer.ifPresent(player -> player.sendMessage(friendMessage));
        }
    }

    public static void proxyPlayerSwitch(Profile profile, String currentServer, String previousServer) {
        var message = Message.parse(Language.Staff.STAFF_SWITCH, profile.getChatFormat(), currentServer, previousServer);

        if(profile.isStaff()) {
            Staff.getMembers().forEach(player -> player.sendMessage(message));
        }
    }

    public static void proxyPlayerDisconnect(Profile profile, String currentServer) {
        var proxy = Blitz.getBlitz().getServer();

        var staffMessage = Message.parse(Language.Staff.STAFF_LEAVE, profile.getChatFormat(), currentServer);

        if(profile.isStaff()) {
            Staff.getMembers().forEach(player -> player.sendMessage(staffMessage));
        }

        var friendMessage = Message.parse(Language.Friend.LEFT_NETWORK, profile.getChatFormat());

        for(var friend : profile.getFriends()) {
            var oPlayer = proxy.getPlayer(friend);

            oPlayer.ifPresent(player -> player.sendMessage(friendMessage));
        }
    }

}
