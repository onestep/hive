package hive.tests;

import static hive.game.Constants.ANT;
import static hive.game.Constants.SILVER;
import hive.game.Coords;
import hive.game.Game;
import hive.game.Piece;

public class FreedomToMove2Test extends FreedomToMove1Test {

    @Override
    public void prepareGame(Game game) {
        super.prepareGame(game);
        putPiece(game, Piece.pieces[SILVER][ANT], Coords.instance(-1, -1));
    }

    @Override
    public String getName() {
        return "Page 08. Freedom To Move part 2.";
    }
}