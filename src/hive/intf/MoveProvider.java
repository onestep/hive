package hive.intf;

import hive.game.Game;
import hive.game.Move;

public interface MoveProvider {

    Move findMove(Game game, int color);

    void cleanUp();

    String getName();

    void _break();
}