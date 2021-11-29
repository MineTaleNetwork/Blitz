package cc.minetale.blitz.api;

import cc.minetale.commonlib.util.Util;
import lombok.Getter;

@Getter
public class SyncCode {

    // TODO Assign to the BlitzPlayer
    private final BlitzPlayer player;
    private final String code;
//    private final SyncCodeTimer timer;

    public SyncCode(BlitzPlayer player) {
        this.player = player;
        this.code = Util.generateId();

//        this.timer = new SyncCodeTimer(this);
    }

}
