package hive.game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class Move implements Constants, Cloneable, Serializable {

    public transient Piece piece;
    public final Coords prevCoords;
    public final Coords newCoords;
    private int hash;

    private Move(Piece piece, Coords prevCoords, Coords newCoords) {
        this.piece = piece;
        this.prevCoords = prevCoords;
        this.newCoords = newCoords;
        this.hash = (piece.hashCode() << 24 + (prevCoords != null ? prevCoords.hashCode() << 16 : 0) + newCoords.hashCode());
    }

    public static final synchronized Move instance(Piece piece, Coords prevCoords, Coords newCoords) {
        return new Move(piece, prevCoords, newCoords);
    }

    public boolean isInsertion() {
        return prevCoords == null;
    }

    private void writeObject(ObjectOutputStream out)
            throws IOException {
        out.defaultWriteObject();
        out.writeInt(piece.color);
        out.writeInt(piece.type);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        int color = in.readInt();
        int type = in.readInt();
        piece = pieces[color][type];
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Move) {
            Move move = (Move) object;
            return (piece == move.piece) && (prevCoords != null ? prevCoords.equals(move.prevCoords) : move.prevCoords == null) && (newCoords.equals(move.newCoords));
        }
        return false;
    }

    @Override
    public Object clone() {
        return this;
    }

    @Override
    public String toString() {
        return "MOVE " + (piece != null ? piece.toString() : "null") + " FROM " + (prevCoords != null ? prevCoords.toString() : "null") + " TO " + newCoords.toString();
    }
}