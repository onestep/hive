package hive.game.providers.impl;

import hive.game.Constants;
import hive.game.Game;
import hive.game.Move;
import hive.game.SimpleMoveComparator;
import java.util.Collection;
import java.util.Iterator;

public abstract class MTD implements Constants {

    private TranspositionTable tTable;
    private Game game;
    public Move foundMove = null;
    protected int foundMinMaxValue = 0;
    protected int forColor = -1;
    private boolean interrupted;
    private int tTUsed;
    private int cutOffs;
    private int ETCCutoffs;
    private int total;
    private int tTOrderingWorks;
    private int driverPasses;
    private int ETCResult;
    private SimpleMoveComparator comparator;

    public MTD(Game game, TranspositionTable table) {
        this.tTable = table;
        this.game = game;

        comparator = new SimpleMoveComparator(game);
    }

    public MTD(Game game) {
        this(game, new TranspositionTable());
    }

    protected abstract int first(int color);

    protected abstract int next(int L, int H, int g);

    public void interrupt() {
        interrupted = true;
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public final synchronized void search(int color, int depth) {
        int H = INFINITY + 1;
        int L = -(INFINITY + 1);
        int g;
        int G = g = first(color);

        Move localMove = foundMove;
        interrupted = false;

        driverPasses = 0;

        System.out.println("********* MTD Routine***************");
        System.out.println("Depth=" + depth);

        tTOrderingWorks = tTUsed = cutOffs = ETCCutoffs = total = 0;
        do {
            g = mtTopMost(G - 1, depth, color);

            if (g < G)
                H = g;
            else
                L = g;

            driverPasses++;

            G = next(L, H, g);
        } while ((H > L) && (!interrupted));

        System.out.println("driver passes " + driverPasses);

        if (interrupted)
            System.out.println("INTERRUPTED");

        System.out.println("TT-size " + tTable.getCount());
        System.out.println("TT-collisions " + tTable.collisions);
        System.out.println("Total nodes searched :" + total);
        System.out.println("Transpositions :" + tTUsed);
        System.out.println("Cutoffs (both normal and transposed) :" + cutOffs);
        System.out.println("Enhanced Transposition cutoffs :" + ETCCutoffs);
        System.out.println("Transposition Orderings proper:" + tTOrderingWorks);
        System.out.println("Found value:" + g);
        System.out.println("************************************");

        if (!interrupted) {
            foundMinMaxValue = g;
            forColor = color;
        } else
            foundMove = localMove;
    }

    private boolean isUseful(Move paramMove, int paramInt) {
        return true;
    }

    private Move ETC(int bound, Collection moves, int depth, int color) {
        Move ETCMove = null;

        Iterator it = moves.iterator();
        while (it.hasNext() && (!interrupted)) {
            Move move = (Move) it.next();
            game.doMove(move);

            TableRepresentation tr = TableRepresentation.getKey(game.table);
            TranspositionEntry entry = tTable.retrieve(tr);
            if (entry != null) {
                int lowerBound = entry.getLowerBound(color, depth - 1);
                if (lowerBound > bound) {
                    ETCResult = lowerBound;
                    ETCMove = move;
                    entry.cutoff = true;
                }
            }

            game.unDoMove(move);
            if (ETCMove != null)
                return ETCMove;
        }
        return null;
    }

    private int mtTopMost(int bound, int depth, int color) {
        total += 1;

        TableRepresentation tr = TableRepresentation.getKey(game.table);
        foundMove = null;
        TranspositionEntry entry = tTable.retrieve(tr);
        Move entryMove = null;

        if (entry != null) {
            int lowerBound = entry.getLowerBound(color, depth);
            int upperBound = entry.getUpperBound(color, depth);

            foundMove = entry.getMove(color);

            if (foundMove != null)
                if (lowerBound < upperBound) {
                    if (lowerBound > bound) {
                        cutOffs++;
                        tTUsed++;

                        entry.cutoff = true;
                        return lowerBound;
                    }
                    if (upperBound < bound) {
                        tTUsed++;

                        entry.cutoff = false;
                        return upperBound;
                    }
                } else if (lowerBound == upperBound) {
                    tTUsed++;
                    cutOffs++;

                    return lowerBound;
                }

            entryMove = entry.getMove(color);
        }

        Move localObject = null;
        int m = -INFINITY;
        int n = 0;

        if (entry == null)
            entry = new TranspositionEntry(tr.cloneTableRepresentation(), game.table.size());

        if (depth == 0)
            m = game.evaluate(color);
        else if (game.isWin(BLUE) || game.isWin(SILVER)) {
            n = 1;
            m = game.evaluate(color);
        } else {
            Collection moves;
            if (entry.moves[color] == null) {
                moves = game.getMoves(color, comparator);
                entry.moves[color] = moves;
            } else
                moves = entry.moves[color];
            Move ETCMove;
            if ((depth > 1) && ((ETCMove = ETC(bound, moves, depth, color)) != null)) {
                ETCCutoffs++;
                foundMove = ETCMove;
                return ETCResult;
            }

            Iterator it = moves.iterator();

            if (entryMove != null)
                ETCMove = entryMove;
            else if (it.hasNext())
                ETCMove = (Move) it.next();
            else
                return -mt(-bound, depth - 1, Game.opponent(color));

            localObject = ETCMove;

            while ((ETCMove != null) && (m < bound) && (!interrupted)) {
                if ((m == -INFINITY) || isUseful(ETCMove, 0)) {
                    game.doMove(ETCMove);
                    int k = -mt(-bound, depth - 1, color == 0 ? 1 : 0);
                    game.unDoMove(ETCMove);

                    if (k > m) {
                        m = k;
                        localObject = ETCMove;
                    }
                }

                ETCMove = it.hasNext() ? (Move) it.next() : null;
                if ((ETCMove != null)
                        && (ETCMove.equals(entryMove)))
                    ETCMove = it.hasNext() ? (Move) it.next() : null;

            }

            if (interrupted)
                return 0;

            foundMove = localObject;

            if (foundMove == entry.getMove(color))
                tTOrderingWorks++;

        }

        if (n != 0) {
            entry.setUpperBound(color, INFINITY, m);
            entry.setLowerBound(color, INFINITY, m);
            entry.cutoff = (m >= bound);
        } else if (depth == 0) {
            entry.setUpperBound(color, 0, m);
            entry.setLowerBound(color, 0, m);
        } else {
            entry.setMove(color, localObject);
            if (m < bound) {
                entry.setUpperBound(color, depth, m);
                entry.cutoff = false;
            } else {
                entry.setLowerBound(color, depth, m);
                entry.cutoff = true;

                cutOffs++;
            }
        }

        tTable.store(entry);

        return m;
    }

    private int mt(int bound, int depth, int color) {
        total += 1;

        TableRepresentation tr = TableRepresentation.getKey(game.table);
        TranspositionEntry entry = tTable.retrieve(tr);
        Move entryMove = null;

        if (entry != null) {
            int lowerBound = entry.getLowerBound(color, depth);
            int upperBound = entry.getUpperBound(color, depth);

            if (lowerBound < upperBound) {
                if (lowerBound > bound) {
                    cutOffs++;
                    tTUsed++;

                    entry.cutoff = true;
                    return lowerBound;
                }
                if (upperBound < bound) {
                    tTUsed++;

                    entry.cutoff = false;
                    return upperBound;
                }
            } else if (lowerBound == upperBound) {
                cutOffs++;
                tTUsed++;

                return lowerBound;
            }

            entryMove = entry.getMove(color);
        }

        Move localObject = null;
        int m = -INFINITY;
        int n = 0;

        if (entry == null)
            entry = new TranspositionEntry(tr.cloneTableRepresentation(), game.table.size());

        if (depth == 0)
            m = game.evaluate(color);
        else if (game.isWin(BLUE) || game.isWin(SILVER)) {
            n = 1;
            m = game.evaluate(color);
        } else {
            Collection moves;
            if (entry.moves[color] == null) {
                moves = game.getMoves(color, comparator);
                entry.moves[color] = moves;
            } else
                moves = entry.moves[color];
            if ((depth > 1) && (ETC(bound, moves, depth, color) != null)) {
                ETCCutoffs++;
                return ETCResult;
            }

            Iterator it = moves.iterator();

            Move ETCMove;
            if (entryMove != null)
                ETCMove = entryMove;
            else if (it.hasNext())
                ETCMove = (Move) it.next();
            else
                return -mt(-bound, depth, Game.opponent(color));

            localObject = ETCMove;

            while ((ETCMove != null) && (m < bound) && (!this.interrupted)) {
                if ((m == -INFINITY) || isUseful(ETCMove, 0)) {
                    game.doMove(ETCMove);
                    int k = -mt(-bound, depth - 1, color == 0 ? 1 : 0);

                    game.unDoMove(ETCMove);

                    if (k > m) {
                        m = k;
                        localObject = ETCMove;
                    }

                }

                ETCMove = it.hasNext() ? (Move) it.next() : null;
                if ((ETCMove != null) && ETCMove.equals(entryMove))
                    ETCMove = it.hasNext() ? (Move) it.next() : null;

            }

            if (interrupted)
                return 0;

            if (localObject == entry.getMove(color))
                tTOrderingWorks++;
        }

        if (n != 0) {
            entry.setUpperBound(color, INFINITY, m);
            entry.setLowerBound(color, INFINITY, m);
            entry.cutoff = (m >= bound);
        } else if (depth == 0) {
            entry.setUpperBound(color, 0, m);
            entry.setLowerBound(color, 0, m);
        } else {
            entry.setMove(color, localObject);
            if (m < bound) {
                entry.setUpperBound(color, depth, m);
                entry.cutoff = false;
            } else {
                entry.setLowerBound(color, depth, m);
                entry.cutoff = true;

                cutOffs++;
            }
        }

        tTable.store(entry);

        return m;
    }
}