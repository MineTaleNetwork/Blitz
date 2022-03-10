package cc.minetale.blitz;

import com.velocitypowered.api.proxy.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class Staff {

    private static final HashMap<UUID, Player> staff = new HashMap<>();

    public static void putStaff(Player player) {
        staff.put(player.getUniqueId(), player);
    }

    public static Player getStaff(UUID uuid) {
        return staff.get(uuid);
    }

    public static void removeStaff(UUID uuid) {
        staff.remove(uuid);
    }

    public static Collection<Player> getMembers() {
        return staff.values();
    }

}
