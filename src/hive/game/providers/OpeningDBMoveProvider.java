package hive.game;

import hive.intf.MoveProvider;
import java.util.Random;
import java.util.Set;

public class OpeningDBMoveProvider implements MoveProvider {

    private MoveProvider delegate;
    private OpeningDB db;
    private Random random;

    public OpeningDBMoveProvider(OpeningDB db, MoveProvider delegate) {
        this.delegate = delegate;
        this.db = db;
        random = new Random(System.currentTimeMillis());
    }

    @Override
    public Move findMove(Game game, int color) {
        if (db != null) {
            Set<Move> moves = (Set<Move>) db.retrieveMoves(game);
            if (moves != null && !moves.isEmpty()) {
                Object[] moveArray = moves.toArray();
                for (int i = random.nextInt(moveArray.length); i < moveArray.length; i++) {
                    Move move = (Move) moveArray[i];
                    if (move.piece.color == color)
                        return move;
                }
            }
        }
        return delegate.findMove(game, color);
    }

    @Override
    public void cleanUp() {
        delegate.cleanUp();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void _break() {
        delegate._break();
    }
}