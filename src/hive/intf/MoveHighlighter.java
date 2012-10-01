package hive.intf;

import hive.game.Move;

public interface MoveHighlighter {

    void highlightTurn(int paramInt);

    void highlightBefore(Move move);

    void highlightAfter(Move move);

    void highlightWin(int paramInt);

    void repaintGame();
}