package hive.tests;

import hive.game.Constants;
import hive.game.Coords;
import hive.game.Game;

public class FreedomToMove2Test extends FreedomToMove1Test implements Constants {

    @Override
    public void prepareGame(Game game) {
        super.prepareGame(game);
        putPiece(game, Constants.pieces[1][3], Coords.instance(-1, -1));
    }

    @Override
    public String getName() {
        return "Page 08. Freedom To Move part 2.";
    }
}