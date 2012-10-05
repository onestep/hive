package hive.tests;

import static hive.game.Constants.*;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Piece;

public class HopperTest extends HiveTest {

    @Override
    public void prepareGame(Game game) {
        putPiece(game, Piece.pieces[BLUE][QUEEN], Coords.instance(0, 0));
        putPiece(game, Piece.pieces[BLUE][HOPPER], Coords.instance(0, 2));
        putPiece(game, Piece.pieces[BLUE][HOPPER], Coords.instance(-1, 3));
        putPiece(game, Piece.pieces[BLUE][HOPPER], Coords.instance(-2, 4));
        putPiece(game, Piece.pieces[BLUE][BEETLE], Coords.instance(-3, 3));
        putPiece(game, Piece.pieces[BLUE][ANT], Coords.instance(-3, 1));
        putPiece(game, Piece.pieces[BLUE][SPIDER], Coords.instance(-3, -1));

        putPiece(game, Piece.pieces[SILVER][ANT], Coords.instance(-1, -1));
        putPiece(game, Piece.pieces[SILVER][ANT], Coords.instance(-1, -3));
        putPiece(game, Piece.pieces[SILVER][BEETLE], Coords.instance(1, 3));
        putPiece(game, Piece.pieces[SILVER][SPIDER], Coords.instance(1, 5));
        putPiece(game, Piece.pieces[SILVER][ANT], Coords.instance(0, 6));
        putPiece(game, Piece.pieces[SILVER][QUEEN], Coords.instance(2, 2));
        putPiece(game, Piece.pieces[SILVER][HOPPER], Coords.instance(0, 4));
    }

    @Override
    public Piece getPiece() {
        return Piece.pieces[BLUE][SPIDER];
    }

    @Override
    public Coords getCoords() {
        return Coords.instance(0, 4);
    }

    @Override
    public String getName() {
        return "Grass Hopper move test.";
    }
}