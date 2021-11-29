package cc.minetale.blitz.listeners.pigeon;

import cc.minetale.commonlib.api.Rank;
import cc.minetale.commonlib.pigeon.payloads.rank.RankReloadPayload;
import cc.minetale.commonlib.pigeon.payloads.rank.RankRemovePayload;
import cc.minetale.pigeon.annotations.PayloadHandler;
import cc.minetale.pigeon.annotations.PayloadListener;
import cc.minetale.pigeon.listeners.Listener;

@PayloadListener
public class RankListener implements Listener {

    @PayloadHandler
    public void onReloadRank(RankReloadPayload payload) {
        var ranks = Rank.getRanks();
        var rank = payload.getRank();

        ranks.remove(rank);
        ranks.add(rank);
    }

    @PayloadHandler
    public void onRemoveRank(RankRemovePayload payload) {
        var ranks = Rank.getRanks();
        var rank = payload.getRank();

        ranks.remove(rank);
    }

}
