package hive.tests;

import hive.game.Coords;
import hive.game.Game;
import hive.game.Move;
import hive.game.Piece;
import java.util.HashSet;

public abstract class HiveTest {

    public abstract void prepareGame(Game game);

    public HashSet<Coords> getTestCoords(Game game) {
        return game.getTargetCoords(getPiece(), getCoords());
    }

    public abstract Piece getPiece();

    public abstract Coords getCoords();

    public abstract String getName();

    protected void putPiece(Game game, Piece piece, Coords coords) {
        game.doMove(new Move(piece, null, coords));
    }
}