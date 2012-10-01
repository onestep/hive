package hive.tests;

import hive.game.Coords;
import hive.game.Game;
import hive.game.Move;
import hive.game.Piece;
import java.util.Collection;

public abstract class HiveTest {

    public abstract void prepareGame(Game paramGame);

    public Collection getTestCoords(Game paramGame) {
        return paramGame.getTargetCoords(getPiece(), getCoords());
    }

    public abstract Piece getPiece();

    public abstract Coords getCoords();

    public abstract String getName();

    protected void putPiece(Game paramGame, Piece paramPiece, Coords paramCoords) {
        Move localMove = Move.instance(paramPiece, null, paramCoords);
        paramGame.doMove(localMove);
    }
}