package hive.game;

public class MTD_f extends MTD {

    public MTD_f(Game game, TranspositionTable t) {
        super(game, t);
    }

    public MTD_f(Game game) {
        super(game);
    }

    @Override
    protected int first(int color) {
        if (forColor == color)
            return foundMinMaxValue;
        return -foundMinMaxValue;
    }

    @Override
    protected int next(int L, int H, int g) {
        return (g == L ? (
                    g + 200 <= H ? g + 200 :
                    g + 100 <= H ? g + 100 :
                    g + 50 <= H ? g + 50 :
                    g + 20 <= H ? g + 20 :
                    g + 10 <= H ? g + 10 :
                    g + 2
                ) :
                g - 198 >= L ? g - 198 :
                g - 98 >= L ? g - 98 :
                g - 48 >= L ? g - 48 :
                g - 18 >= L ? g - 18 :
                g - 8 >= L ? g - 8 : g);
    }
}