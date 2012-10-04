package hive.tests;

import hive.game.Constants;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Piece;

public class BeetleTest extends HiveTest implements Constants {

    @Override
    public void prepareGame(Game game) {
        putPiece(game, Constants.pieces[BLUE][QUEEN], Coords.instance(0, 0));
        putPiece(game, Constants.pieces[BLUE][ANT], Coords.instance(-2, -2));
        putPiece(game, Constants.pieces[BLUE][HOPPER], Coords.instance(-1, -3));

        putPiece(game, Constants.pieces[SILVER][QUEEN], Coords.instance(-1, -1));
        putPiece(game, Constants.pieces[SILVER][BEETLE], Coords.instance(-1, -3));

        putPiece(game, Constants.pieces[BLUE][BEETLE], Coords.instance(-1, -1));
        putPiece(game, Constants.pieces[BLUE][BEETLE], Coords.instance(0, 0));
    }

    @Override
    public Piece getPiece() {
        return Constants.pieces[BLUE][BEETLE];
    }

    @Override
    public Coords getCoords() {
        return Coords.instance(-1, -1);
    }

    @Override
    public String getName() {
        return "\"Beetle false assumption\" test.";
    }
}