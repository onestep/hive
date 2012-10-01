package hive.gui;

import hive.game.GameProcess;

public class GameThread implements Runnable {

    private GameProcess process;
    private Thread thread;

    public GameThread(GameProcess gameProcess) {
        process = gameProcess;
        thread = new Thread(this);
    }

    public void start() {
        thread.start();
    }

    public void finish() {
        process.finish();
        synchronized (this) {
            String str = "ala ma kota";
            str = str + "b";
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            while (!process.isFinished())
                process.nextPly();
        }
    }
}