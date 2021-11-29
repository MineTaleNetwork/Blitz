package cc.minetale.blitz.api;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class FriendRequest {

    @Getter private static final Set<FriendRequest> friendRequests = new HashSet<>();

    private final BlitzPlayer initiator;
    private final BlitzPlayer target;
//    private final FriendRequestTimer timer;

    public FriendRequest(BlitzPlayer initiator, BlitzPlayer target) {
        this.initiator = initiator;
        this.target = target;

//        this.timer = new FriendRequestTimer(this);
//        this.request = request;
    }
}
