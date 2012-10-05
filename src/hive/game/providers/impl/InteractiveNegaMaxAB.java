package hive.game.providers.impl;

import hive.game.*;
import hive.intf.MoveHighlighter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public class InteractiveNegaMaxAB implements Constants {

    private static final int MAX_DEPTH = 4;
    private Move foundMove;
    private Game game;
    private Comparator<Move> comparator;
    private boolean interrupted;
    private MoveHighlighter highlighter;

    public InteractiveNegaMaxAB(Game game, MoveHighlighter highlighter) {
        this.game = game;
        this.highlighter = highlighter;
        comparator = new SimpleMoveComparator(game);
    }

    public synchronized Move findMove(int color) {
        interrupted = false;
        foundMove = null;

        int minMaxValue = negaMaxABTop(color, 0, -INFINITY, INFINITY, true);

        System.out.println("MinMaxVal=" + minMaxValue + " Evaluation=" + game.evaluate(color));

        if (!interrupted)
            return foundMove;
        System.out.println("Interrupted");
        return null;
    }

    public void interrupt() {
        this.interrupted = true;
        synchronized (this) {
            this.interrupted = true;
        }
    }

    private boolean check(Move move, int depth, boolean paramBoolean) {
        if (!paramBoolean)
            return true;

        Coords coords = game.table.firstCoordsForPiece(Piece.pieces[Game.opponent(move.piece.color)][QUEEN]);
        boolean b1 = coords != null ? false : game.table.getPieceAt(coords).color == move.piece.color;
        boolean b2 = (b1 && coords.getEdge(move.newCoords) >= 0 || !move.isInsertion());

        switch (depth) {
            case 0:
            case 1:
                return true;
            case 2:
                return b2;
            default:
                return b2 && (move.piece.type != ANT);
        }
    }

    private int negaMaxABTop(int color, int depth, int a, int b, boolean paramBoolean) {
        if ((depth == MAX_DEPTH) || game.isWin(BLUE) || game.isWin(SILVER))
            return game.evaluate(color);
        int minMaxValue = -INFINITY;
        int j = minMaxValue;

        Collection moves = game.getMoves(color, comparator);

        if (moves.isEmpty())
            return -negaMaxAB(Game.opponent(color), depth, -b, -a, false);
        Iterator it = moves.iterator();

        while (it.hasNext() && (!interrupted) && (minMaxValue < b)) {
            Move move = (Move) it.next();
            if (minMaxValue > a)
                a = minMaxValue;

            if (check(move, depth, paramBoolean) || (minMaxValue == -INFINITY)) {
                game.doMove(move);
                highlighter.highlightAfter(move);
                j = -negaMaxAB(Game.opponent(color), depth + 1, -b, -a, true);
                game.unDoMove(move);
                highlighter.repaintGame();
            }

            if (j > minMaxValue) {
                minMaxValue = j;
                foundMove = move;
            }
        }

        return minMaxValue;
    }

    private int negaMaxAB(int color, int depth, int a, int b, boolean paramBoolean) {
        /* break recursion */
        if ((depth == MAX_DEPTH) || game.isWin(BLUE) || game.isWin(SILVER))
            return game.evaluate(color);

        int minMaxValue = -INFINITY;
        int j = minMaxValue;

        Collection moves = game.getMoves(color, comparator);

        if (moves.isEmpty())
            return -negaMaxAB(Game.opponent(color), depth + 1, -b, -a, false);
        Iterator it = moves.iterator();

        while (it.hasNext() && (!interrupted) && (minMaxValue < b)) {
            Move move = (Move) it.next();
            if (minMaxValue > a)
                a = minMaxValue;
            if (check(move, depth, paramBoolean) || (minMaxValue == -INFINITY)) {
                game.doMove(move);
                highlighter.highlightAfter(move);
                j = -negaMaxAB(Game.opponent(color), depth + 1, -b, -a, true);
                game.unDoMove(move);
                highlighter.repaintGame();
            }
            if (j > minMaxValue)
                minMaxValue = j;
        }
        return minMaxValue;
    }
}