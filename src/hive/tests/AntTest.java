package hive.tests;

import hive.game.Constants;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Piece;

public class AntTest extends HiveTest implements Constants {

    @Override
    public void prepareGame(Game game) {
        putPiece(game, Constants.pieces[BLUE][QUEEN], Coords.instance(0, 0));
        putPiece(game, Constants.pieces[BLUE][SPIDER], Coords.instance(-1, -1));
        putPiece(game, Constants.pieces[BLUE][ANT], Coords.instance(-2, 0));
        putPiece(game, Constants.pieces[BLUE][HOPPER], Coords.instance(-2, -4));
        putPiece(game, Constants.pieces[BLUE][ANT], Coords.instance(-3, -3));
        putPiece(game, Constants.pieces[BLUE][ANT], Coords.instance(-2, -6));
        putPiece(game, Constants.pieces[BLUE][SPIDER], Coords.instance(-1, -9));

        putPiece(game, Constants.pieces[SILVER][QUEEN], Coords.instance(-1, -7));
        putPiece(game, Constants.pieces[SILVER][SPIDER], Coords.instance(-1, -3));
        putPiece(game, Constants.pieces[SILVER][SPIDER], Coords.instance(-1, 3));
        putPiece(game, Constants.pieces[SILVER][ANT], Coords.instance(0, -2));
        putPiece(game, Constants.pieces[SILVER][ANT], Coords.instance(1, -1));
        putPiece(game, Constants.pieces[SILVER][ANT], Coords.instance(0, 2));
    }

    @Override
    public Piece getPiece() {
        return Constants.pieces[BLUE][ANT];
    }

    @Override
    public Coords getCoords() {
        return Coords.instance(-2, 0);
    }

    @Override
    public String getName() {
        return "Ant bug test.";
    }
}