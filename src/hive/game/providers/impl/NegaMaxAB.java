package hive.game.providers.impl;

import hive.game.*;
import hive.intf.Thinker;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public class NegaMaxAB implements Constants {

    private static final int MAX_DEPTH = 4;
    private Move foundMove;
    private Game game;
    private Comparator<Move> comparator;
    private boolean interrupted;
    private Thinker thinker;

    public NegaMaxAB(Game game, Thinker thinker) {
        this.game = game;
        this.thinker = thinker;
        comparator = new SimpleMoveComparator(game);
    }

    public synchronized Move findMove(int color) {
        interrupted = false;
        foundMove = null;

        thinker.startThinking(color);
        int minMaxValue;
        try {
            minMaxValue = negaMaxABTop(color, 0, -INFINITY, INFINITY, true);
        } finally {
            thinker.stopThinking();
        }

        System.out.println("MinMaxVal=" + minMaxValue + " Evaluation=" + game.evaluate(color));

        if (!interrupted)
            return foundMove;
        System.out.println("Interrupted");
        return null;
    }

    public void interrupt() {
        interrupted = true;
        synchronized (this) {
            interrupted = true;
        }
    }

    private boolean check(Move move, int depth, boolean paramBoolean) {
        if (!paramBoolean)
            return true;

        Coords coords = game.table.firstCoordsForPiece(Piece.pieces[Game.opponent(move.piece.color)][QUEEN]);
        boolean b1 = (coords != null ? game.table.getPieceAt(coords).color == move.piece.color : false);
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
        System.out.println("Branching factor: " + moves.size() + ", Depth: " + depth + ", Color: " + color);

        if (moves.isEmpty())
            return -negaMaxAB(Game.opponent(color), depth, -b, -a, false);
        Iterator it = moves.iterator();

        while (it.hasNext() && (!interrupted) && (minMaxValue < b)) {
            Move move = (Move) it.next();
            if (minMaxValue > a)
                a = minMaxValue;

            if (check(move, depth, paramBoolean) || (minMaxValue == -INFINITY)) {
                game.doMove(move);
                j = -negaMaxAB(Game.opponent(color), depth + 1, -b, -a, true);
                game.unDoMove(move);
            }

            if (j > minMaxValue) {
                minMaxValue = j;
                foundMove = move;
            }
        }

        return minMaxValue;
    }

    private int negaMaxAB(int color, int depth, int a, int b, boolean paramBoolean) {
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
                j = -negaMaxAB(Game.opponent(color), depth + 1, -b, -a, true);
                game.unDoMove(move);
            }
            if (j > minMaxValue)
                minMaxValue = j;
        }
        return minMaxValue;
    }
}