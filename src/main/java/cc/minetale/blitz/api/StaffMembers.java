package cc.minetale.blitz.api;

import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.HashSet;
import java.util.Set;

@Getter
public class StaffMembers {

    @Getter private static StaffMembers staffMembers;
    private final Set<BlitzPlayer> audience;

    public StaffMembers() {
        StaffMembers.staffMembers = this;
        this.audience = new HashSet<>();
    }

    public void sendMessage(Component component) {
        audience.forEach(blitzPlayer -> {
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

}
