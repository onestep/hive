package hive.tests;

import static hive.game.Constants.*;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Piece;

public class OneHiveRuleTest extends HiveTest {

    @Override
    public void prepareGame(Game game) {
        putPiece(game, Piece.pieces[BLUE][BEETLE], Coords.instance(0, 0));
        putPiece(game, Piece.pieces[SILVER][ANT], Coords.instance(1, -1));
        putPiece(game, Piece.pieces[BLUE][ANT], Coords.instance(-1, 1));
        putPiece(game, Piece.pieces[BLUE][QUEEN], Coords.instance(-2, 2));
        putPiece(game, Piece.pieces[SILVER][ANT], Coords.instance(-2, 0));
    }

    @Override
    public Coords getCoords() {
        return Coords.instance(-1, 1);
    }

    @Override
    public Piece getPiece() {
        return Piece.pieces[BLUE][ANT];
    }

    @Override
    public String getName() {
        return "Page 07. One Hive Rule.";
    }
}