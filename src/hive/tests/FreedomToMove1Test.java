package hive.tests;

import hive.game.Constants;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Piece;

public class FreedomToMove1Test extends HiveTest implements Constants {

    @Override
    public void prepareGame(Game game) {
        putPiece(game, Constants.pieces[0][3], Coords.instance(0, 0));
        putPiece(game, Constants.pieces[0][0], Coords.instance(-1, 1));
        putPiece(game, Constants.pieces[1][2], Coords.instance(0, 2));
        putPiece(game, Constants.pieces[1][0], Coords.instance(1, 1));
        putPiece(game, Constants.pieces[1][1], Coords.instance(1, -1));
    }

    @Override
    public Coords getCoords() {
        return Coords.instance(0, 0);
    }

    @Override
    public Piece getPiece() {
        return Constants.pieces[0][3];
    }

    @Override
    public String getName() {
        return "Page 08. Freedom To Move part 1.";
    }
}