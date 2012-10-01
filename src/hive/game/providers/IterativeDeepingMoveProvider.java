package hive.game.providers;

import hive.game.Game;
import hive.game.MTD;
import hive.game.MTD_f;
import hive.game.Move;
import hive.intf.MoveProvider;
import hive.game.TranspositionTable;

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
            mtd = new MTD_f(game, t);
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
        if (this.mtd != null)
            this.mtd.interrupt();
    }

    @Override
    public String getName() {
        return "Computer";
    }
}