package hive.game;

import static hive.game.Constants.BLUE;
import static hive.game.Constants.SILVER;

public final class Box {

    private int[][] _pieces;
    private int[] total;
    private boolean changed = false;
    private int iteration_color;
    private int iteration_index;

    Box() {
        _pieces = new int[2][];
        _pieces[0] = new int[5];
        _pieces[1] = new int[5];
        total = new int[2];
        total[0] = 11;
        total[1] = 11;
        reset();
    }

    public int howMany(int color, int type) {
        return _pieces[color][type];
    }

    public int howMany(int color) {
        return total[color];
    }

    public Piece first(int color) {
        changed = false;
        iteration_color = color;
        for (iteration_index = 0; iteration_index < 5; iteration_index++)
            if (_pieces[color][iteration_index] > 0)
                return Piece.pieces[color][iteration_index++];
        return null;
    }

    public Piece next() {
        if (changed)
            throw new IllegalStateException("Hive Box changed during iteration");
        for (; iteration_index < 5; iteration_index++)
            if (_pieces[iteration_color][iteration_index] > 0)
                return Piece.pieces[iteration_color][iteration_index++];
        return null;
    }

    public boolean contains(Piece piece) {
        return _pieces[piece.color][piece.type] > 0;
    }

    public int remove(Piece piece) {
        changed = (changed || (piece.color == iteration_color));
        _pieces[piece.color][piece.type]--;
        total[piece.color]--;
        return _pieces[piece.color][piece.type];
    }

    public int putback(Piece piece) {
        changed = (changed || (piece.color == iteration_color));
        _pieces[piece.color][piece.type]++;
        total[piece.color]++;
        return _pieces[piece.color][piece.type];
    }

    public void reset() {
        changed = true;
        for (int color = 0; color < 2; color++)
            System.arraycopy(Constants.howManyPieces, 0, _pieces[color], 0, 5);
        total[BLUE] = 11;
        total[SILVER] = 11;
    }

    @Override
    public String toString() {
        String str = "";
        for (int color = 0; color < 2; color++)
            for (int type = 0; type < 5; type++)
                str = str + Piece.pieces[color][type].toString() + " = " + new Integer(_pieces[color][type]).toString() + "\n";
        return str;
    }
}