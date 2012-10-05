package hive.tests;

import static hive.game.Constants.*;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Piece;

public class QueenBeeTest extends HiveTest {

    @Override
    public void prepareGame(Game game) {
        putPiece(game, Piece.pieces[BLUE][QUEEN], Coords.instance(0, 0));
        putPiece(game, Piece.pieces[BLUE][ANT], Coords.instance(0, 2));
        putPiece(game, Piece.pieces[BLUE][SPIDER], Coords.instance(0, -2));
        putPiece(game, Piece.pieces[SILVER][SPIDER], Coords.instance(1, -3));
        putPiece(game, Piece.pieces[SILVER][ANT], Coords.instance(2, -2));
        putPiece(game, Piece.pieces[SILVER][QUEEN], Coords.instance(3, -1));
        putPiece(game, Piece.pieces[BLUE][BEETLE], Coords.instance(2, 0));
        putPiece(game, Piece.pieces[SILVER][BEETLE], Coords.instance(2, 2));
        putPiece(game, Piece.pieces[SILVER][SPIDER], Coords.instance(0, 4));
        putPiece(game, Piece.pieces[BLUE][SPIDER], Coords.instance(1, 3));
    }

    @Override
    public Coords getCoords() {
        return Coords.instance(0, 0);
    }

    @Override
    public Piece getPiece() {
        return Piece.pieces[BLUE][QUEEN];
    }

    @Override
    public String getName() {
        return "Page 10. Freedom To Move part 1.";
    }
}