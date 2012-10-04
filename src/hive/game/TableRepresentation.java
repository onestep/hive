package hive.game;

import java.util.Arrays;

public final class TableRepresentation implements Constants {

    public byte[] rep;
    private int hash;
    private static TableRepresentation KEY = new TableRepresentation();
    private static Coords zero = Coords.instance(0, 0);
    private static Coords[] _coords = new Coords[3];

    public TableRepresentation(Table table) {
        rep = new byte[54];
        countTableRepresentation(table, rep);
        hash = UniversalHash.hashByteArray(rep, 0);
    }

    public TableRepresentation(byte[] rep) {
        this.rep = new byte[54];
        System.arraycopy(rep, 0, this.rep, 0, 54);
        hash = UniversalHash.hashByteArray(this.rep, 0);
    }
    
    private TableRepresentation() {
        rep = new byte[54];
    }

    public static TableRepresentation getKey(Table table) {
        countTableRepresentation(table, KEY.rep);
        KEY.hash = UniversalHash.hashByteArray(KEY.rep, 0);
        return KEY;
    }

    public static void countTableRepresentation(Table table, byte[] rep) {
        if (rep.length != 54)
            throw new IllegalArgumentException("Bad array length");
        int i = 0;

        for (int j = 0; j <= 1; j++)
            for (int k = 0; k < 5; k++)
                i = getCoords(table, pieces[j][k], rep, i);
    }

    private static int getCoords(Table table, Piece piece, byte[] rep, int pos) {
        int i = 0;
        for (int j = 0; j < howManyPieces[piece.type]; j++) {
            _coords[j] = table.coordsIndex[piece.color][piece.type][j];
            if (_coords[j] == null)
                _coords[j] = zero;
            else
                i = (byte) (i + 1);
        }

        sortCoords(_coords, 0, howManyPieces[piece.type] - 1);

        for (int k = 0; k < howManyPieces[piece.type]; k++) {
            rep[pos + k] = shiftCoord(_coords[k].c1);
            rep[pos + k + howManyPieces[piece.type]] = shiftCoord(_coords[k].c2);
        }

        rep[pos + (howManyPieces[piece.type] << 1)] = (byte) i;
        return pos + (howManyPieces[piece.type] << 1) + 1;
    }

    private static byte shiftCoord(int c) {
        return (byte) (c + 44);
    }

    private static void sortCoords(Coords[] coordsArray, int a, int b) {
        if (a == b)
            return;
        Coords coords;
        if (coordsArray[a].compareTo(coordsArray[b]) > 0) {
            coords = coordsArray[a];
            coordsArray[a] = coordsArray[b];
            coordsArray[b] = coords;
        }

        if (b - a == 2) {
            int i = a + 1;

            if (coordsArray[i].compareTo(coordsArray[b]) > 0) {
                coords = coordsArray[i];
                coordsArray[i] = coordsArray[b];
                coordsArray[b] = coords;
            } else if (coordsArray[a].compareTo(coordsArray[i]) > 0) {
                coords = coordsArray[a];
                coordsArray[a] = coordsArray[i];
                coordsArray[i] = coords;
            }
        }
    }

    public final TableRepresentation cloneTableRepresentation() {
        return new TableRepresentation(this.rep);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public Object clone() {
        return cloneTableRepresentation();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof TableRepresentation) {
            TableRepresentation tableRepresentation = (TableRepresentation) object;
            if (tableRepresentation.hash == hash)
                return Arrays.equals(rep, tableRepresentation.rep);
            return false;
        }
        return false;
    }
}