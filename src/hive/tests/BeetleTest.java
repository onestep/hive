package hive.tests;

import static hive.game.Constants.*;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Piece;

public class BeetleTest extends HiveTest {

    @Override
    public void prepareGame(Game game) {
        putPiece(game, Piece.pieces[BLUE][QUEEN], Coords.instance(0, 0));
        putPiece(game, Piece.pieces[BLUE][ANT], Coords.instance(-2, -2));
        putPiece(game, Piece.pieces[BLUE][HOPPER], Coords.instance(-1, -3));

        putPiece(game, Piece.pieces[SILVER][QUEEN], Coords.instance(-1, -1));
        putPiece(game, Piece.pieces[SILVER][BEETLE], Coords.instance(-1, -3));

        putPiece(game, Piece.pieces[BLUE][BEETLE], Coords.instance(-1, -1));
        putPiece(game, Piece.pieces[BLUE][BEETLE], Coords.instance(0, 0));
    }

    @Override
    public Piece getPiece() {
        return Piece.pieces[BLUE][BEETLE];
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