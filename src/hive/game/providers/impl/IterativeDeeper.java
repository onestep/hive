package hive.game.providers.impl;

import java.io.PrintStream;

public final class IterativeDeeper
        implements Runnable {

    private int depth;
    private PrintStream old_out;
    private Thread finder;
    Object monitor = new Object();
    private MTD mtd;
    private int increment;
    private int startingDepth;
    private long milisec;
    private int color;

    public IterativeDeeper(MTD paramMTD) {
        this.mtd = paramMTD;
    }

    public void run() {
        this.depth = (this.startingDepth + this.increment);
        while (true) {
            this.mtd.search(this.color, this.depth);
            synchronized (this.monitor) {
                if (!this.mtd.isInterrupted())
                    this.depth += this.increment;
                else
                    break;
            }
        }
    }

    public void search(int paramInt1, long paramLong, int paramInt2, int paramInt3) {
        this.milisec = paramLong;
        this.increment = paramInt3;
        this.startingDepth = paramInt2;

        this.mtd.search(paramInt1, paramInt2);
        this.color = paramInt1;
        Thread localThread = new Thread(this, "Iterative deeping thread");
        localThread.start();
        try {
            localThread.join(paramLong);
            synchronized (this.monitor) {
                if (localThread.isAlive())
                    this.mtd.interrupt();
            }
            localThread.join();
        } catch (InterruptedException localInterruptedException) {
        }
        System.out.println("Depth: " + (this.depth - paramInt3));
    }
}