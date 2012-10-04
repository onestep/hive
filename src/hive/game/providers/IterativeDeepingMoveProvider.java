package hive.game.providers;

import hive.game.providers.impl.MTD;
import hive.game.providers.impl.IterativeDeeper;
import hive.game.providers.impl.TranspositionTable;
import hive.game.providers.impl.MTDf;
import hive.game.*;
import hive.intf.MoveProvider;

public class IterativeDeepingMoveProvider implements MoveProvider {

    private Game game = null;
    private MTD mtd = null;
    private IterativeDeeper id = null;
    private TranspositionTable t;

    public IterativeDeepingMoveProvider(TranspositionTable t) {
        this.t = t;
    }

    @Override
    public Move findMove(Game game, int color) {
        if (this.game != game) {
            this.game = game;
            mtd = new MTDf(game, t);
            id = new IterativeDeeper(mtd);
        }

        id.search(color, 1000L, 2, 1);

        return mtd.foundMove;
    }

    @Override
    public void cleanUp() {
    }

    @Override
    public void _break() {
        if (mtd != null)
            mtd.interrupt();
    }

    @Override
    public String getName() {
        return "Computer";
    }
}