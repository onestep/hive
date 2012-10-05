package hive.tests;

import static hive.game.Constants.*;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Piece;

public class PlacingTest extends HiveTest {

    @Override
    public void prepareGame(Game game) {
        putPiece(game, Piece.pieces[BLUE][SPIDER], Coords.instance(0, 0));
        putPiece(game, Piece.pieces[SILVER][ANT], Coords.instance(0, 2));
    }

    @Override
    public Coords getCoords() {
        return null;
    }

    @Override
    public Piece getPiece() {
        return Piece.pieces[SILVER][HOPPER];
    }

    @Override
    public String getName() {
        return "Page 05. Placing.";
    }
}