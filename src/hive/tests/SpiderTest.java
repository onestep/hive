package hive.tests;

import hive.game.Constants;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Move;
import hive.game.Piece;

public class SpiderTest extends HiveTest implements Constants {

    @Override
    public void prepareGame(Game game) {
        putPiece(game, Constants.pieces[1][0], Coords.instance(0, 0));

        game.doMove(Move.instance(Constants.pieces[0][2], null, Coords.instance(0, -2)));
        game.doMove(Move.instance(Constants.pieces[1][3], null, Coords.instance(0, -4)));
        game.doMove(Move.instance(Constants.pieces[1][2], null, Coords.instance(1, -5)));
        game.doMove(Move.instance(Constants.pieces[1][1], null, Coords.instance(-1, 1)));
        game.doMove(Move.instance(Constants.pieces[0][4], null, Coords.instance(-1, -5)));
        game.doMove(Move.instance(Constants.pieces[0][1], null, Coords.instance(-2, 2)));
        game.doMove(Move.instance(Constants.pieces[1][4], null, Coords.instance(-2, -6)));
        game.doMove(Move.instance(Constants.pieces[0][0], null, Coords.instance(-3, -5)));
        game.doMove(Move.instance(Constants.pieces[0][3], null, Coords.instance(-3, -3)));
        game.doMove(Move.instance(Constants.pieces[0][1], null, Coords.instance(-3, -1)));
    }

    @Override
    public Piece getPiece() {
        return Constants.pieces[0][1];
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