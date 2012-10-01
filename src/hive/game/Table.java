package hive.game;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public final class Table implements Constants {

    int modCount;
    private int _size;
    private boolean canIterate;
    private Iterator i;
    private Piece pieceToIterate;
    private int i_index;
    private boolean canIterate2;
    private static final Placement DEFAULT_EMPTY_PLACEMENT = new Placement(null);
    private Map map;
    Coords[][][] coordsIndex;

    public Table() {
        modCount = 0;

        _size = 0;

        canIterate = false;

        canIterate2 = false;

        coordsIndex = new Coords[2][][];
        coordsIndex[0] = new Coords[5][];
        coordsIndex[1] = new Coords[5][];
        for (int j = 0; j < 2; j++)
            for (int k = 0; k < 5; k++)
                coordsIndex[j][k] = new Coords[Constants.howManyPieces[k]];
        this.map = new TreeMap();
    }

    public void reset() {
        canIterate = false;
        canIterate2 = false;
        _size = 0;
        map.clear();
        modCount += 1;
        coordsIndex = new Coords[2][][];
        coordsIndex[0] = new Coords[5][];
        coordsIndex[1] = new Coords[5][];
        for (int j = 0; j < 2; j++)
            for (int k = 0; k < 5; k++)
                this.coordsIndex[j][k] = new Coords[Constants.howManyPieces[k]];
    }

    public final Piece getPieceAt(Coords paramCoords, int paramInt) {
        return getPlacement(paramCoords).getPiece(paramInt);
    }

    public final Piece getPieceAt(Coords coords) {
        return getPlacement(coords).getPiece();
    }

    public final int countPiecesAt(Coords coords) {
        return getPlacement(coords).count();
    }

    public final void putPieceAt(Coords paramCoords, Piece paramPiece) {
        Placement localPlacement = (Placement) map.get(paramCoords);
        if (null == localPlacement) {
            localPlacement = new Placement(paramPiece);
            map.put(paramCoords, localPlacement);
        } else
            localPlacement.putPiece(paramPiece);
        canIterate = false;
        canIterate2 = false;

        for (int j = 0; j < Constants.howManyPieces[paramPiece.type]; j++)
            if (coordsIndex[paramPiece.color][paramPiece.type][j] == null) {
                coordsIndex[paramPiece.color][paramPiece.type][j] = paramCoords;
                break;
            }
        _size += 1;
        modCount += 1;
    }

    public final Piece removePieceAt(Coords paramCoords) {
        Piece localPiece = getPlacement(paramCoords).remove();

        for (int j = 0; j < Constants.howManyPieces[localPiece.type]; j++)
            if (paramCoords.equals(coordsIndex[localPiece.color][localPiece.type][j])) {
                coordsIndex[localPiece.color][localPiece.type][j] = null;
                break;
            }
        canIterate = false;
        canIterate2 = false;
        _size -= 1;
        modCount += 1;
        return localPiece;
    }

    public int size() {
        return this._size;
    }

    public Coords firstCoords() {
        this.i = this.map.keySet().iterator();
        Coords localCoords;
        do {
            this.canIterate = this.i.hasNext();
            if (!this.canIterate)
                return null;
            localCoords = (Coords) this.i.next();
        } while (countPiecesAt(localCoords) < 1);
        return localCoords;
    }

    public Coords nextCoords() {
        if (!this.canIterate)
            throw new IllegalStateException();
        Coords localCoords;
        do {
            this.canIterate = this.i.hasNext();
            if (!this.canIterate)
                return null;
            localCoords = (Coords) this.i.next();
        } while (countPiecesAt(localCoords) < 1);
        return localCoords;
    }

    public Coords firstCoordsForPiece(Piece paramPiece) {
        this.pieceToIterate = paramPiece;
        for (this.i_index = 0; this.i_index < Constants.howManyPieces[paramPiece.type]; this.i_index += 1)
            if (this.coordsIndex[paramPiece.color][paramPiece.type][this.i_index] != null) {
                this.canIterate2 = true;
                return this.coordsIndex[paramPiece.color][paramPiece.type][(this.i_index++)];
            }
        this.canIterate2 = false;
        return null;
    }

    public Coords nextCoordsForPiece() {
        if (!this.canIterate2)
            throw new IllegalStateException();
        for (; this.i_index < Constants.howManyPieces[this.pieceToIterate.type]; this.i_index += 1)
            if (this.coordsIndex[this.pieceToIterate.color][this.pieceToIterate.type][this.i_index] != null) {
                this.canIterate2 = true;
                return this.coordsIndex[this.pieceToIterate.color][this.pieceToIterate.type][(this.i_index++)];
            }
        this.canIterate2 = false;
        return null;
    }

    private Placement getPlacement(Coords coords) {
        Placement placement = (Placement) map.get(coords);
        return placement != null ? placement : DEFAULT_EMPTY_PLACEMENT;
    }

    public String toString() {
        Iterator localIterator = this.map.keySet().iterator();
        String str = "";
        while (localIterator.hasNext()) {
            Coords localCoords = (Coords) localIterator.next();
            Placement localPlacement = (Placement) this.map.get(localCoords);
            int j = localPlacement.count();
            if (j > 0) {
                str = str + "on " + localCoords + "(";
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

        public Placement(Piece paramPiece) {
            this.piece = paramPiece;
        }

        public final boolean isEmpty() {
            return this.piece == null;
        }

        public final int count() {
            return this.howManyUnder + (this.piece != null ? 1 : 0);
        }

        public final Piece remove() {
            Piece localPiece = this.piece;
            this.piece = (this.howManyUnder > 0 ? this.piecesUnder[(--this.howManyUnder)] : null);
            return localPiece;
        }

        public final Piece getPiece(int paramInt) {
            if (paramInt == 0)
                return this.piece;
            int i = this.howManyUnder - paramInt;
            if (i >= 0)
                return this.piecesUnder[i];
            return null;
        }

        public final Piece getPiece() {
            return this.piece;
        }

        public final void putPiece(Piece paramPiece) {
            if ((this.piece != null) && (paramPiece != null)) {
                if (this.piecesUnder == null)
                    this.piecesUnder = new Piece[4];
                this.piecesUnder[(this.howManyUnder++)] = this.piece;
            }
            this.piece = paramPiece;
        }
    }
}