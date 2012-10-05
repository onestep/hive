package hive.game;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public final class Table implements Constants {

    int modCount;
    private int _size;
    private boolean canIterate;
    private boolean canIterate2;
    private Iterator i;
    private Piece pieceToIterate;
    private int i_index;
    private static final Placement DEFAULT_EMPTY_PLACEMENT = new Placement(null);
    private Map<Coords, Placement> map;
    public Coords[][][] coordsIndex;

    public Table() {
        canIterate = false;
        canIterate2 = false;
        _size = 0;
        map = new TreeMap<Coords, Placement>();
        modCount = 0;

        coordsIndex = new Coords[2][][];
        coordsIndex[0] = new Coords[5][];
        coordsIndex[1] = new Coords[5][];
        for (int color = 0; color < 2; color++)
            for (int type = 0; type < 5; type++)
                coordsIndex[color][type] = new Coords[Constants.howManyPieces[type]];
    }

    public void reset() {
        canIterate = false;
        canIterate2 = false;
        _size = 0;
        map.clear();
        modCount++;

	coordsIndex = new Coords[2][][];
        coordsIndex[0] = new Coords[5][];
        coordsIndex[1] = new Coords[5][];
        for (int color = 0; color < 2; color++)
            for (int type = 0; type < 5; type++)
                coordsIndex[color][type] = new Coords[Constants.howManyPieces[type]];
    }

    public final Piece getPieceAt(Coords coords, int z) {
        return getPlacement(coords).getPiece(z);
    }

    public final Piece getPieceAt(Coords coords) {
        return getPlacement(coords).getPiece();
    }

    public final int countPiecesAt(Coords coords) {
        return getPlacement(coords).count();
    }

    public final void putPieceAt(Coords coords, Piece piece) {
        Placement placement = (Placement) map.get(coords);
        if (placement == null) {
            placement = new Placement(piece);
            map.put(coords, placement);
        } else
            placement.putPiece(piece);
        canIterate = false;
        canIterate2 = false;

        for (int j = 0; j < Constants.howManyPieces[piece.type]; j++)
            if (coordsIndex[piece.color][piece.type][j] == null) {
                coordsIndex[piece.color][piece.type][j] = coords;
                break;
            }
        _size++;
        modCount++;
    }

    public final Piece removePieceAt(Coords coords) {
        Piece piece = getPlacement(coords).remove();

        for (int j = 0; j < Constants.howManyPieces[piece.type]; j++)
            if (coords.equals(coordsIndex[piece.color][piece.type][j])) {
                coordsIndex[piece.color][piece.type][j] = null;
                break;
            }
        canIterate = false;
        canIterate2 = false;
        _size--;
        modCount++;
        return piece;
    }

    public int size() {
        return this._size;
    }

    public Coords firstCoords() {
        i = map.keySet().iterator();
        Coords coords;
        do {
            canIterate = i.hasNext();
            if (!canIterate)
                return null;
            coords = (Coords) i.next();
        } while (countPiecesAt(coords) < 1);
        return coords;
    }

    public Coords nextCoords() {
        if (!canIterate)
            throw new IllegalStateException();
        Coords coords;
        do {
            canIterate = i.hasNext();
            if (!canIterate)
                return null;
            coords = (Coords) i.next();
        } while (countPiecesAt(coords) < 1);
        return coords;
    }

    public Coords firstCoordsForPiece(Piece piece) {
        pieceToIterate = piece;
        for (i_index = 0; i_index < Constants.howManyPieces[piece.type]; i_index++)
            if (coordsIndex[piece.color][piece.type][i_index] != null) {
                canIterate2 = true;
                return coordsIndex[piece.color][piece.type][i_index++];
            }
        canIterate2 = false;
        return null;
    }

    public Coords nextCoordsForPiece() {
        if (!canIterate2)
            throw new IllegalStateException();
        for (; i_index < Constants.howManyPieces[pieceToIterate.type]; i_index++)
            if (coordsIndex[pieceToIterate.color][pieceToIterate.type][i_index] != null) {
                canIterate2 = true;
                return coordsIndex[pieceToIterate.color][pieceToIterate.type][i_index++];
            }
        canIterate2 = false;
        return null;
    }

    private Placement getPlacement(Coords coords) {
        Placement placement = (Placement) map.get(coords);
        return placement != null ? placement : DEFAULT_EMPTY_PLACEMENT;
    }

    @Override
    public String toString() {
        String str = "";
        for (Coords coords : this.map.keySet()) {
            Placement localPlacement = (Placement) this.map.get(coords);
            int j = localPlacement.count();
            if (j > 0) {
                str = str + "on " + coords + "(";
                for (int k = j - 1; k >= 0; k--) {
                    str = str + localPlacement.getPiece(k);
                    if (k > 0)
                        str = str + " , ";
                }
                str = str + ")\n";
            }
        }

        return str;
    }

    public static final class Placement {

        private Piece piece;
        private int howManyUnder = 0;
        private Piece[] piecesUnder;

        public Placement(Piece piece) {
            this.piece = piece;
        }

        public final boolean isEmpty() {
            return piece == null;
        }

        public final int count() {
            return howManyUnder + (piece != null ? 1 : 0);
        }

        public final Piece remove() {
            Piece localPiece = this.piece;
            this.piece = (howManyUnder > 0 ? piecesUnder[--howManyUnder] : null);
            return localPiece;
        }

        public final Piece getPiece(int z) {
            if (z == 0)
                return piece;
            int i = howManyUnder - z;
            if (i >= 0)
                return piecesUnder[i];
            return null;
        }

        public final Piece getPiece() {
            return piece;
        }

        public final void putPiece(Piece piece) {
            if ((this.piece != null) && (piece != null)) {
                if (piecesUnder == null)
                    piecesUnder = new Piece[4];
                piecesUnder[howManyUnder++] = this.piece;
            }
            this.piece = piece;
        }
    }
}