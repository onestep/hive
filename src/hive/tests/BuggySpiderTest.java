package hive.tests;

import hive.game.Constants;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Piece;

public class BuggySpiderTest extends HiveTest implements Constants {

    @Override
    public void prepareGame(Game game) {
        putPiece(game, Constants.pieces[0][0], Coords.instance(0, 0));
        putPiece(game, Constants.pieces[0][4], Coords.instance(0, 2));
        putPiece(game, Constants.pieces[0][4], Coords.instance(-1, 3));
        putPiece(game, Constants.pieces[0][4], Coords.instance(-2, 4));
        putPiece(game, Constants.pieces[0][2], Coords.instance(-3, 3));
        putPiece(game, Constants.pieces[0][3], Coords.instance(-3, 1));
        putPiece(game, Constants.pieces[0][1], Coords.instance(-3, -1));

        putPiece(game, Constants.pieces[1][3], Coords.instance(-1, -1));
        putPiece(game, Constants.pieces[1][3], Coords.instance(-1, -3));
        putPiece(game, Constants.pieces[1][2], Coords.instance(1, 3));
        putPiece(game, Constants.pieces[1][1], Coords.instance(1, 5));
        putPiece(game, Constants.pieces[1][3], Coords.instance(0, 6));
        putPiece(game, Constants.pieces[1][0], Coords.instance(2, 2));
    }

    @Override
    public Piece getPiece() {
        return Constants.pieces[0][1];
    }

    @Override
    public Coords getCoords() {
        return Coords.instance(-3, -1);
    }

    @Override
    public String getName() {
        return "Buggy Spider move test.";
    }
}