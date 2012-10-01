package hive.game;

import java.util.Comparator;

public final class SimpleMoveComparator
        implements Comparator<Move>, Constants {

    private int[] piecePowerMovement = {0, 1, 2, 4, 3};
    private int[] piecePowerPutting = {0, 4, 3, 1, 2};
    private Game game;
    private Coords opponentQueen;
    private boolean beetleOnOpponentQueen;

    public SimpleMoveComparator(Game game) {
        this.game = game;
    }

    private final void initComparison(int paramInt) {
        opponentQueen = game.table.firstCoordsForPiece(Constants.pieces[1][0]);
        if (opponentQueen != null)
            beetleOnOpponentQueen = (game.table.getPieceAt(opponentQueen).color == paramInt);
        else
            beetleOnOpponentQueen = false;
    }

    private final int comparePrevCoords(Move paramMove1, Move paramMove2) {
        if (this.opponentQueen == null)
            return 0;
        int i = paramMove1.prevCoords.getEdge(this.opponentQueen);
        int j = paramMove2.prevCoords.getEdge(this.opponentQueen);
        return j >= 0 ? -1 : i >= 0 ? 0 : j < 0 ? 1 : 0;
    }

    private final int compareNewCoords(Move paramMove1, Move paramMove2) {
        if (this.opponentQueen == null)
            return 0;
        int i = paramMove1.newCoords.getEdge(this.opponentQueen);
        int j = paramMove2.newCoords.getEdge(this.opponentQueen);
        return j >= 0 ? 1 : i >= 0 ? 0 : j < 0 ? -1 : 0;
    }

    private final int compareBlockingQueen(Move paramMove1, Move paramMove2) {
        if (this.opponentQueen == null)
            return 0;
        return paramMove2.equals(this.opponentQueen) ? 1 : paramMove1.newCoords.equals(this.opponentQueen) ? 0 : !paramMove2.equals(this.opponentQueen) ? -1 : 0;
    }

    private final int compareMoving(Move paramMove1, Move paramMove2) {
        return !paramMove2.isInsertion() ? -1 : !this.beetleOnOpponentQueen ? 1 : !paramMove1.isInsertion() ? 0 : paramMove2.isInsertion() ? 1 : !this.beetleOnOpponentQueen ? -1 : 0;
    }

    private final int comparePiecesForMove(Piece paramPiece1, Piece paramPiece2) {
        return this.piecePowerMovement[paramPiece1.type] < this.piecePowerMovement[paramPiece2.type] ? 1 : this.piecePowerMovement[paramPiece1.type] > this.piecePowerMovement[paramPiece2.type] ? -1 : 0;
    }

    private final int comparePiecesForPutting(Piece paramPiece1, Piece paramPiece2) {
        return this.piecePowerPutting[paramPiece1.type] < this.piecePowerPutting[paramPiece2.type] ? 1 : this.piecePowerPutting[paramPiece1.type] > this.piecePowerPutting[paramPiece2.type] ? -1 : 0;
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

        if (i == 0)
            throw new Error();
        return i;
    }
}