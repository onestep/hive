package hive.tests;

import hive.game.Constants;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Piece;

public class PlacingTest extends HiveTest implements Constants {

    @Override
    public void prepareGame(Game game) {
        putPiece(game, Constants.pieces[BLUE][SPIDER], Coords.instance(0, 0));
        putPiece(game, Constants.pieces[SILVER][ANT], Coords.instance(0, 2));
    }

    @Override
    public Coords getCoords() {
        return null;
    }

    @Override
    public Piece getPiece() {
        return Constants.pieces[SILVER][HOPPER];
    }

    @Override
    public String getName() {
        return "Page 05. Placing.";
    }
}