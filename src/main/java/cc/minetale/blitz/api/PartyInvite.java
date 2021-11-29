package cc.minetale.blitz.api;

import lombok.Getter;

@Getter
public class PartyInvite {

    private final Party party;
    private final BlitzPlayer initiator;
    private final BlitzPlayer target;
//    private final PartyInviteTimer timer;

    public PartyInvite(Party party, BlitzPlayer initiator, BlitzPlayer target) {
        this.party = party;
        this.initiator = initiator;
        this.target = target;

//        this.timer = new PartyInviteTimer(partyUUID, targetUUID);
//        this.timer.start();
    }

}
