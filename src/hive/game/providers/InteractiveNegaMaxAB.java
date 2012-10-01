package hive.game.providers;

import hive.game.Constants;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Move;
import hive.game.SimpleMoveComparator;
import hive.intf.MoveHighlighter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public class InteractiveNegaMaxAB implements Constants {

    private static final int MAX_DEPTH = 4;
    private Move foundMove;
    private Game game;
    private Comparator comparator;
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

	int i = negaMaxABTop(color, 0, -INFINITY, INFINITY, true);

	System.out.println("MinMaxVal=" + i + " Evaluation=" + game.evaluate(color));

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

    private boolean check(Move move, int paramInt, boolean paramBoolean) {
	if (!paramBoolean)
	    return true;

	Coords coords = game.table.firstCoordsForPiece(Constants.queens[Game.opponent(move.piece.color)]);
	boolean i = coords != null ? false : game.table.getPieceAt(coords).color == move.piece.color;
	boolean bool = (i && (coords.getEdge(move.newCoords) >= 0)) || (!move.isInsertion());

	switch (paramInt) {
	    case 0:
	    case 1:
		return true;
	    case 2:
		return bool;
	    case 3:
	}
	return bool && (move.piece.type != 3);
    }

    private int negaMaxABTop(int color, int depth, int a, int b, boolean paramBoolean) {
	if ((depth == MAX_DEPTH) || game.isWin(color) || game.isWin(Game.opponent(color)))
	    return game.evaluate(color);
	int i = -INFINITY;
	int j = i;

	Collection localCollection = game.getMoves(color, comparator);

	if (localCollection.isEmpty())
	    return -negaMaxAB(Game.opponent(color), depth, -b, -a, false);
	Iterator localIterator = localCollection.iterator();

	while (localIterator.hasNext() && (!interrupted) && (i < b)) {
	    Move move = (Move) localIterator.next();
	    if (i > a)
		a = i;

	    if ((check(move, depth, paramBoolean)) || (i == -INFINITY)) {
		game.doMove(move);
		highlighter.highlightAfter(move);
		j = -negaMaxAB(Game.opponent(color), depth + 1, -b, -a, true);
		game.unDoMove(move);
		highlighter.repaintGame();
	    }

	    if (j > i) {
		i = j;
		foundMove = move;
	    }
	}

	return i;
    }

    private int negaMaxAB(int color, int depth, int a, int b, boolean paramBoolean) {
	if ((depth == MAX_DEPTH) || game.isWin(color) || game.isWin(Game.opponent(color)))
	    return this.game.evaluate(color);
	int i = -INFINITY;
	int j = i;

	Collection localCollection = this.game.getMoves(color, this.comparator);

	if (localCollection.isEmpty())
	    return -negaMaxAB(Game.opponent(color), depth + 1, -b, -a, false);
	Iterator localIterator = localCollection.iterator();

	while ((localIterator.hasNext()) && (!this.interrupted) && (i < b)) {
	    Move localMove = (Move) localIterator.next();
	    if (i > a)
		a = i;
	    if (check(localMove, depth, paramBoolean) || (i == -INFINITY)) {
		game.doMove(localMove);
		highlighter.highlightAfter(localMove);
		j = -negaMaxAB(Game.opponent(color), depth + 1, -b, -a, true);
		game.unDoMove(localMove);
		highlighter.repaintGame();
	    }
	    if (j > i)
		i = j;
	}
	return i;
    }
}