package hive.game;

import java.util.Comparator;

public final class SimpleMoveComparator implements Comparator<Move>, Constants {

    private int[] piecePowerMovement = {0, 1, 2, 4, 3};
    private int[] piecePowerPutting = {0, 4, 3, 1, 2};
    private Game game;
    private Coords opponentQueen;
    private boolean beetleOnOpponentQueen;

    public SimpleMoveComparator(Game game) {
	this.game = game;
    }

    private void initComparison(int color) {
	opponentQueen = game.table.firstCoordsForPiece(Constants.pieces[SILVER][QUEEN]);
	if (opponentQueen != null)
	    beetleOnOpponentQueen = (game.table.getPieceAt(opponentQueen).color == color);
	else
	    beetleOnOpponentQueen = false;
    }

    private int comparePrevCoords(Move move1, Move move2) {
	if (opponentQueen == null)
	    return 0;
	int i = move1.prevCoords.getEdge(opponentQueen);
	int j = move2.prevCoords.getEdge(opponentQueen);
	return i >= 0 ? j < 0 ? 1 : 0 : j >= 0 ? -1 : 0;
    }

    private int compareNewCoords(Move move1, Move move2) {
	if (opponentQueen == null)
	    return 0;
	int i = move1.newCoords.getEdge(opponentQueen);
	int j = move2.newCoords.getEdge(opponentQueen);
	return i >= 0 ? j < 0 ? -1 : 0 : j >= 0 ? 1 : 0;
    }

    private int compareBlockingQueen(Move move1, Move move2) {
	if (opponentQueen == null)
	    return 0;
	/* TODO: check for wrong equals */
	return (move1.newCoords.equals(opponentQueen)
		? !move2.equals(opponentQueen) ? -1 : 0
		: move2.equals(opponentQueen) ? 1 : 0);
    }

    private int compareMoving(Move move1, Move move2) {
	return (!move1.isInsertion()
		? move2.isInsertion() ? !beetleOnOpponentQueen ? -1 : 1 : 0
		: !move2.isInsertion() ? !beetleOnOpponentQueen ? 1 : -1 : 0);
    }

    private int comparePiecesForMove(Piece piece1, Piece piece2) {
	return ((piecePowerMovement[piece1.type] > piecePowerMovement[piece2.type]) ? -1
		: (piecePowerMovement[piece1.type] < piecePowerMovement[piece2.type]) ? 1 : 0);
    }

    private int comparePiecesForPutting(Piece piece1, Piece piece2) {
	return ((piecePowerPutting[piece1.type] > piecePowerPutting[piece2.type]) ? -1
		: (piecePowerPutting[piece1.type] < piecePowerPutting[piece2.type]) ? 1 : 0);
    }

    @Override
    public int compare(Move move1, Move move2) {
	initComparison(move1.piece.color);

	int i = compareMoving(move1, move2);
	if (i == 0) {
	    if (!move1.isInsertion()) {
		i = compareBlockingQueen(move1, move2);
		if (i == 0) {
		    i = comparePrevCoords(move1, move2);
		    if (i == 0) {
			i = compareNewCoords(move1, move2);
			if (i == 0) {
			    i = comparePiecesForMove(move1.piece, move2.piece);
			    if (i == 0)
				i = move1.prevCoords.compareTo(move2.prevCoords);
			}
		    }
		}
	    } else {
		i = compareNewCoords(move1, move2);
		if (i == 0)
		    i = comparePiecesForPutting(move1.piece, move2.piece);
	    }

	    if (i == 0)
		i = move1.newCoords.compareTo(move2.newCoords);
	}

	return i;
    }
}