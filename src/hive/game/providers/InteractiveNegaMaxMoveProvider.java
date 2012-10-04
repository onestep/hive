package hive.game.providers;

import hive.game.Game;
import hive.game.Move;
import hive.game.providers.impl.InteractiveNegaMaxAB;
import hive.intf.MoveHighlighter;
import hive.intf.MoveProvider;

public class InteractiveNegaMaxMoveProvider implements MoveProvider {

    private Game game;
    private InteractiveNegaMaxAB negamax;
    private MoveHighlighter highligher;

    public InteractiveNegaMaxMoveProvider(Game game, MoveHighlighter highlighter) {
        this.game = game;
        this.highligher = highlighter;
        negamax = new InteractiveNegaMaxAB(game, highlighter);
    }

    @Override
    public Move findMove(Game game, int paramInt) {
        if (this.game != game) {
            this.game = game;
            negamax = new InteractiveNegaMaxAB(game, highligher);
        }
        return negamax.findMove(paramInt);
    }

    @Override
    public void cleanUp() {
        negamax.interrupt();
    }

    @Override
    public String getName() {
        return "Computer";
    }

    @Override
    public void _break() {
        negamax.interrupt();
    }
}