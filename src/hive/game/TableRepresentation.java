package hive.game;

import java.io.Serializable;

public final class TableRepresentation
        implements Constants, Serializable {

    private byte[] rep;
    private int hash;
    private static TableRepresentation KEY = new TableRepresentation();
    private static Coords zero = Coords.instance(0, 0);
    private static Coords[] _coords = new Coords[3];

    TableRepresentation(Table table) {
        rep = new byte[54];
        countTableRepresentation(table, rep);
        hash = UniversalHash.hashByteArray(rep, 0);
    }

    private TableRepresentation() {
        rep = new byte[54];
    }

    public static TableRepresentation getKey(Table table) {
        countTableRepresentation(table, KEY.rep);
        KEY.hash = UniversalHash.hashByteArray(KEY.rep, 0);
        return KEY;
    }

    public static final void countTableRepresentation(Table table, byte[] paramArrayOfByte) {
        if (paramArrayOfByte.length != 54)
            throw new IllegalArgumentException("Bad array length");
        int i = 0;

        for (int j = 0; j <= 1; j++)
            for (int k = 0; k < 5; k++)
                i = getCoords(table, pieces[j][k], paramArrayOfByte, i);
    }

    private static final int getCoords(Table table, Piece piece, byte[] paramArrayOfByte, int paramInt) {
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
            paramArrayOfByte[(paramInt + k)] = shiftCoord(_coords[k].c1);
            paramArrayOfByte[(paramInt + k + howManyPieces[piece.type])] = shiftCoord(_coords[k].c2);
        }

        paramArrayOfByte[(paramInt + (howManyPieces[piece.type] << 1))] = (byte) i;
        return paramInt + (howManyPieces[piece.type] << 1) + 1;
    }

    public static boolean equalByteArrays(byte[] array1, byte[] array2) {
        if ((array1 == null) || (array2 == null))
            return array1 == array2;
        if (array1.length != array2.length)
            return false;
        for (int i = 0; i < array1.length; i++)
            if (array1[i] != array2[i])
                return false;
        return true;
    }

    private static final byte shiftCoord(int paramInt) {
        return (byte) (paramInt + 44);
    }

    private static final void sortCoords(Coords[] coordsArray, int a, int b) {
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

    public int hashCode() {
        return hash;
    }

    public final TableRepresentation cloneTableRepresentation() {
        TableRepresentation clone = new TableRepresentation();
        clone.hash = this.hash;
        System.arraycopy(rep, 0, clone.rep, 0, rep.length);

        return clone;
    }

    public Object clone() {
        return cloneTableRepresentation();
    }

    public boolean equals(Object object) {
        if (object instanceof TableRepresentation) {
            TableRepresentation tableRepresentation = (TableRepresentation) object;
            if (tableRepresentation.hash == hash)
                return equalByteArrays(rep, tableRepresentation.rep);
            return false;
        }
        return false;
    }
}