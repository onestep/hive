package hive.game;

import hive.intf.MoveHighlighter;
import hive.intf.MoveProvider;
import java.io.PrintWriter;

public class GameProcess implements Constants {

    private MoveProvider[] providers = {null, null};
    private String[] names = {null, null};
    private PrintWriter out;
    private int curColor;
    private Game game;
    private MoveHighlighter h;
    private boolean finished;

    public GameProcess(Game game, MoveProvider moveProvider1, MoveProvider moveProvider2, int startColor, PrintWriter printWriter, MoveHighlighter moveHighlighter, boolean newLook) {
        providers[0] = moveProvider1;
        providers[1] = moveProvider2;
        if (newLook) {
            names[0] = (moveProvider1.getName() + " (ivory)");
            names[1] = (moveProvider2.getName() + " (black)");
        } else {
            names[0] = (moveProvider1.getName() + " (blue)");
            names[1] = (moveProvider2.getName() + " (silver)");
        }
        this.game = game;
        h = moveHighlighter;
        out = printWriter;
        curColor = startColor;
        finished = false;
    }

    public final synchronized boolean isFinished() {
        return finished;
    }

    public final synchronized void finish() {
        if (finished)
            return;
        providers[0]._break();
        providers[1]._break();
        providers[0].cleanUp();
        providers[1].cleanUp();
        finished = true;
    }

    public void nextPly() {
        if (finished)
            throw new IllegalStateException("The game is already finished");

        if (game.getMoves(curColor).isEmpty())
            out.println(names[curColor] + " has no moves");
        else {
            out.println(names[curColor] + "'s" + " turn");

            h.highlightTurn(curColor);

            Move move = providers[curColor].findMove(game, curColor);
            if (finished)
                return;
            if (null == move) {
                out.println(names[curColor] + "has no moves");
                curColor = (curColor > 0 ? 0 : 1);
                return;
            }

            h.highlightBefore(move);
            game.doMove(move);
            h.highlightAfter(move);

            h.highlightTurn(-1);

            boolean[] wins = {game.isWin(0), game.isWin(1)};

            if (wins[0] || wins[1])
                finish();

            if (wins[0] ^ wins[1]) {
                out.println((wins[0] ? names[0] : names[1]) + " wins !!!");
                h.highlightWin(wins[0] ? 0 : 1);
            } else if (wins[0]) {
                h.highlightWin(0);
                h.highlightWin(1);
                out.println("ending on draw !!!");
            }
        }

        curColor = curColor > 0 ? 0 : 1;
    }
}