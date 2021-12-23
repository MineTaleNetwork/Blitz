package cc.minetale.blitz.api;

import net.kyori.adventure.text.Component;

import java.util.HashSet;
import java.util.Set;

public class StaffMembers {

    private static final Set<BlitzPlayer> members = new HashSet<>();

    public static void sendMessage(Component component) {
        members.forEach(blitzPlayer -> {
            var optionalPlayer = blitzPlayer.getProxyPlayer();
            if(optionalPlayer.isPresent()) {
                var profile = blitzPlayer.getProfile();

                if(profile.getStaffProfile().isReceivingStaffMessages()) {
                    var proxyPlayer = optionalPlayer.get();

                    proxyPlayer.sendMessage(component);
                }
            }
        });
    }

    public static void addMember(BlitzPlayer player) {
        members.add(player);
    }

    public static void removeMember(BlitzPlayer player) {
        members.remove(player);
    }

}
