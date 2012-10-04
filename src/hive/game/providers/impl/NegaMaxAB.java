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
	int i;
	try {
	    i = negaMaxABTop(color, 0, -1000000000, 1000000000, true);
	} finally {
	    thinker.stopThinking();
	}

	System.out.println("MinMaxVal=" + i + " Evaluation=" + game.evaluate(color));

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

    private boolean check(Move move, int paramInt, boolean paramBoolean) {
	if (!paramBoolean)
	    return true;

	Coords coords = game.table.firstCoordsForPiece(Constants.queens[Game.opponent(move.piece.color)]);
	boolean b1 = (coords != null ? game.table.getPieceAt(coords).color == move.piece.color : false);
	boolean b2 = (b1 && coords.getEdge(move.newCoords) >= 0 || !move.isInsertion());

	switch (paramInt) {
	    case 0:
	    case 1:
		return true;
	    case 2:
		return b2;
	    default:
		return b2 && (move.piece.type != 3);
	}
    }

    private int negaMaxABTop(int color, int depth, int paramInt3, int paramInt4, boolean paramBoolean) {
	if ((depth == MAX_DEPTH) || game.isWin(BLUE) || game.isWin(SILVER))
	    return game.evaluate(color);
	int i = -INFINITY;
	int j = i;

	Collection localCollection = game.getMoves(color, comparator);
	System.out.println("Branching factor:" + new Integer(localCollection.size()));

	if (localCollection.isEmpty())
	    return -negaMaxAB(Game.opponent(color), depth, -paramInt4, -paramInt3, false);
	Iterator localIterator = localCollection.iterator();

	while (localIterator.hasNext() && (!interrupted) && (i < paramInt4)) {
	    Move move = (Move) localIterator.next();
	    if (i > paramInt3)
		paramInt3 = i;

	    if ((check(move, depth, paramBoolean)) || (i == -1000000000)) {
		game.doMove(move);
		j = -negaMaxAB(Game.opponent(color), depth + 1, -paramInt4, -paramInt3, true);
		game.unDoMove(move);
	    }

	    if (j > i) {
		i = j;
		foundMove = move;
	    }
	}

	return i;
    }

    private int negaMaxAB(int color, int depth, int a, int b, boolean paramBoolean) {
	if ((depth == MAX_DEPTH) || game.isWin(BLUE) || game.isWin(SILVER))
	    return game.evaluate(color);
	int i = -INFINITY;
	int j = i;

	Collection moves = game.getMoves(color, this.comparator);

	if (moves.isEmpty())
	    return -negaMaxAB(Game.opponent(color), depth + 1, -b, -a, false);
	Iterator it = moves.iterator();

	while (it.hasNext() && (!interrupted) && (i < b)) {
	    Move move = (Move) it.next();
	    if (i > a)
		a = i;
	    if (check(move, depth, paramBoolean) || (i == -INFINITY)) {
		game.doMove(move);
		j = -negaMaxAB(Game.opponent(color), depth + 1, -b, -a, true);
		game.unDoMove(move);
	    }
	    if (j > i)
		i = j;
	}
	return i;
    }
}