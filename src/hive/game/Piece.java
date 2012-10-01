package hive.game;

public final class Piece implements Constants {

    public final int type;
    public final int color;
    private transient String name = null;
    private int hash;
    public static final Piece[][] pieces = new Piece[2][];

    private Piece(int type, int color) {
        this.type = type;
        this.color = color;
        hash = (type << 1 | color);
    }

    @Override
    public String toString() {
        if (name == null)
            name = (Constants.colorNames[color] + " " + Constants.pieceNames[type]);
        return name;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Piece) {
            Piece piece = (Piece) object;
            return type == piece.type && color == piece.color;
        }
        return false;
    }

    static {
        for (int i = 0; i < 2; i++) {
            pieces[i] = new Piece[5];
            for (int j = 0; j < 5; j++)
                pieces[i][j] = new Piece(j, i);
        }
    }
}