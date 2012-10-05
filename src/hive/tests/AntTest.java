package hive.tests;

import static hive.game.Constants.*;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Piece;

public class AntTest extends HiveTest {

    @Override
    public void prepareGame(Game game) {
        putPiece(game, Piece.pieces[BLUE][QUEEN], Coords.instance(0, 0));
        putPiece(game, Piece.pieces[BLUE][SPIDER], Coords.instance(-1, -1));
        putPiece(game, Piece.pieces[BLUE][ANT], Coords.instance(-2, 0));
        putPiece(game, Piece.pieces[BLUE][HOPPER], Coords.instance(-2, -4));
        putPiece(game, Piece.pieces[BLUE][ANT], Coords.instance(-3, -3));
        putPiece(game, Piece.pieces[BLUE][ANT], Coords.instance(-2, -6));
        putPiece(game, Piece.pieces[BLUE][SPIDER], Coords.instance(-1, -9));

        putPiece(game, Piece.pieces[SILVER][QUEEN], Coords.instance(-1, -7));
        putPiece(game, Piece.pieces[SILVER][SPIDER], Coords.instance(-1, -3));
        putPiece(game, Piece.pieces[SILVER][SPIDER], Coords.instance(-1, 3));
        putPiece(game, Piece.pieces[SILVER][ANT], Coords.instance(0, -2));
        putPiece(game, Piece.pieces[SILVER][ANT], Coords.instance(1, -1));
        putPiece(game, Piece.pieces[SILVER][ANT], Coords.instance(0, 2));
    }

    @Override
    public Piece getPiece() {
        return Piece.pieces[BLUE][ANT];
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