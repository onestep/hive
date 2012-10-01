package hive.game;

import hive.intf.MoveProvider;
import java.util.Collection;
import java.util.Random;

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
            Collection localCollection = db.retrieveMoves(game);
            if (localCollection != null) {
                Object[] arrayOfObject = localCollection.toArray();
                if (arrayOfObject.length > 0)
                    for (int i = random.nextInt(arrayOfObject.length); i < arrayOfObject.length; i++) {
                        Move localMove = (Move) arrayOfObject[i];
                        if (localMove.piece.color == color)
                            return localMove;
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