package cc.minetale.blitz.listener;

import cc.minetale.blitz.Blitz;
import cc.minetale.blitz.Staff;
import cc.minetale.sodium.lang.Language;
import cc.minetale.sodium.profile.Profile;
import cc.minetale.sodium.profile.grant.Grant;
import cc.minetale.sodium.profile.punishment.Punishment;
import cc.minetale.sodium.util.Message;
import cc.minetale.sodium.util.TimeUtil;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;

public class ProxyHandler {

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

    public static void addGrant(Player player, Grant grant) {
        var rank = grant.getRank();

        player.sendMessage(Message.chatSeparator());
        player.sendMessage(Message.notification("Grant",
                Component.text().append(
                        Component.text("A ", NamedTextColor.GRAY),
                        Component.text(rank.getName(), rank.getColor()),
                        Component.text(" grant has been applied to you " +
                                (grant.getDuration() == Integer.MAX_VALUE ? "permanently" :
                                        "for " + TimeUtil.millisToRoundedTime(grant.getDuration())) + ".", NamedTextColor.GRAY)
                ).build()));
        player.sendMessage(Message.chatSeparator());
    }

    public static void removeGrant(Player player, Grant grant) {
        var rank = grant.getRank();

        player.sendMessage(Message.chatSeparator());
        player.sendMessage(Message.notification("Grant",
                Component.text().append(
                        Component.text("Your ", NamedTextColor.GRAY),
                        Component.text(rank.getName(), rank.getColor()),
                        Component.text(" grant has been removed.", NamedTextColor.GRAY)
                ).build()));
        player.sendMessage(Message.chatSeparator());
    }

    public static void expireGrant(Player player, Grant grant) {
        var rank = grant.getRank();

        player.sendMessage(Message.chatSeparator());
        player.sendMessage(Message.notification("Grant",
                Component.text().append(
                        Component.text("Your ", NamedTextColor.GRAY),
                        Component.text(rank.getName(), rank.getColor()),
                        Component.text(" grant has expired.", NamedTextColor.GRAY)
                ).build()));
        player.sendMessage(Message.chatSeparator());
    }

    public static void addPunishment(Player player, Punishment punishment) {
        switch (punishment.getType()) {
            case BAN -> player.disconnect(Component.join(JoinConfiguration.newlines(), punishment.getPunishmentMessage()));
            case MUTE -> {
                for(var component : punishment.getPunishmentMessage()) {
                    player.sendMessage(component);
                }
            }
        }
    }

}
