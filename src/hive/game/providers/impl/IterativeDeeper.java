package hive.game.providers.impl;

public final class IterativeDeeper implements Runnable {

    private int depth;
    private Thread finder;
    private final Object monitor = new Object();
    private MTD mtd;
    private int increment;
    private int startingDepth;
    private int color;

    public IterativeDeeper(MTD mtd) {
        this.mtd = mtd;
    }

    @Override
    public void run() {
        depth = startingDepth + increment;
        while (true) {
            mtd.search(color, depth);
            synchronized (monitor) {
                if (!mtd.isInterrupted())
                    depth += increment;
                else
                    break;
            }
        }
    }

    public void search(int color, long timeout, int startingDepth, int increment) {
        this.color = color;
        this.increment = increment;
        this.startingDepth = startingDepth;

        mtd.search(color, startingDepth);
        finder = new Thread(this, "Iterative deeping thread");
        finder.start();
        try {
            finder.join(timeout);
            synchronized (monitor) {
                if (finder.isAlive())
                    mtd.interrupt();
            }
            finder.join();
        } catch (InterruptedException ex) {
        }
        System.out.println("Depth: " + (depth - increment));
    }
}