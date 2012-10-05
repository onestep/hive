package hive.game;

public final class Piece {

    public static final Piece[][] pieces;

    static {
        pieces = new Piece[2][];
        for (int color = 0; color < 2; color++) {
            pieces[color] = new Piece[5];
            for (int type = 0; type < 5; type++)
                pieces[color][type] = new Piece(type, color);
        }
    }

    public final int type;
    public final int color;
    private int hash;
    private String name = null;

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
            return (type == piece.type) && (color == piece.color);
        }
        return false;
    }
}