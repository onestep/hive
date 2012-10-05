package hive.tests;

import static hive.game.Constants.*;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Piece;

public class FreedomToMove1Test extends HiveTest {

    @Override
    public void prepareGame(Game game) {
        putPiece(game, Piece.pieces[BLUE][ANT], Coords.instance(0, 0));
        putPiece(game, Piece.pieces[BLUE][QUEEN], Coords.instance(-1, 1));
        putPiece(game, Piece.pieces[SILVER][BEETLE], Coords.instance(0, 2));
        putPiece(game, Piece.pieces[SILVER][QUEEN], Coords.instance(1, 1));
        putPiece(game, Piece.pieces[SILVER][SPIDER], Coords.instance(1, -1));
    }

    @Override
    public Coords getCoords() {
        return Coords.instance(0, 0);
    }

    @Override
    public Piece getPiece() {
        return Piece.pieces[BLUE][ANT];
    }

    @Override
    public String getName() {
        return "Page 08. Freedom To Move part 1.";
    }
}