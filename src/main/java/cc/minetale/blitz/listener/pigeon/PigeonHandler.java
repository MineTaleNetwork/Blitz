package cc.minetale.blitz.listener.pigeon;

import cc.minetale.blitz.Blitz;
import cc.minetale.commonlib.lang.Language;
import cc.minetale.commonlib.profile.Profile;
import cc.minetale.commonlib.util.Message;

public class PigeonHandler {

    public static void proxyPlayerConnect(Profile profile, String serverName) {
        var server = Blitz.getBlitz().getServer();

        if(profile.getFriends().size() == 0) { return; }

        for(var friend : profile.getFriends()) {
            var oPlayer = server.getPlayer(friend);

            if(oPlayer.isEmpty()) { continue; }

            oPlayer.get().sendMessage(Message.parse(Language.Friend.JOINED_NETWORK, profile.getChatFormat()));

            for(var staff : Blitz.getStaff().values()) {
                staff.sendMessage(Message.parse(Language.Staff.STAFF_JOIN, profile.getChatFormat(), serverName));
            }
        }
    }

}
