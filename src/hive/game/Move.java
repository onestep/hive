package hive.game;

public final class Move {

    public final Piece piece;
    public final Coords prevCoords;
    public final Coords newCoords;
    private int hash;

    public Move(Piece piece, Coords prevCoords, Coords newCoords) {
        this.piece = piece;
        this.prevCoords = prevCoords;
        this.newCoords = newCoords;
        hash = (piece.hashCode() << 24 + (prevCoords != null ? prevCoords.hashCode() << 16 : 0) + newCoords.hashCode());
    }

    public boolean isInsertion() {
        return prevCoords == null;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Move) {
            Move move = (Move) object;
            return (piece == move.piece) && (prevCoords != null ? prevCoords.equals(move.prevCoords) : move.prevCoords == null) && newCoords.equals(move.newCoords);
        }
        return false;
    }

    @Override
    public String toString() {
        return "MOVE " + piece.toString() + (prevCoords != null ? " FROM " + prevCoords.toString() : "") + " TO " + newCoords.toString();
    }
}