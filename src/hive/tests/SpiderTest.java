package hive.tests;

import static hive.game.Constants.*;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Piece;

public class SpiderTest extends HiveTest {

    @Override
    public void prepareGame(Game game) {
        putPiece(game, Piece.pieces[SILVER][QUEEN], Coords.instance(0, 0));
        putPiece(game, Piece.pieces[BLUE][BEETLE], Coords.instance(0, -2));
        putPiece(game, Piece.pieces[SILVER][ANT], Coords.instance(0, -4));
        putPiece(game, Piece.pieces[SILVER][BEETLE], Coords.instance(1, -5));
        putPiece(game, Piece.pieces[SILVER][SPIDER], Coords.instance(-1, 1));
        putPiece(game, Piece.pieces[BLUE][HOPPER], Coords.instance(-1, -5));
        putPiece(game, Piece.pieces[BLUE][SPIDER], Coords.instance(-2, 2));
        putPiece(game, Piece.pieces[SILVER][HOPPER], Coords.instance(-2, -6));
        putPiece(game, Piece.pieces[BLUE][QUEEN], Coords.instance(-3, -5));
        putPiece(game, Piece.pieces[BLUE][ANT], Coords.instance(-3, -3));
        putPiece(game, Piece.pieces[BLUE][SPIDER], Coords.instance(-3, -1));
    }

    @Override
    public Piece getPiece() {
        return Piece.pieces[BLUE][SPIDER];
    }

    @Override
    public Coords getCoords() {
        return Coords.instance(-2, 2);
    }

    @Override
    public String getName() {
        return "Page 13. Spider move test.";
    }
}