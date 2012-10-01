package hive.game;

import java.util.Collection;
import java.util.Iterator;

public abstract class MTD implements Constants {

    protected TranspositionTable tTable;
    Game g;
    public Move foundMove = null;
    public int foundMinMaxValue = 0;
    public int forColor = -1;
    private int tTUsed;
    private int cutOffs;
    private int ETCCutoffs;
    private int total;
    private int tTOrderingWorks;
    private boolean interrupted;
    private int searchDepth;
    private int driverPasses;
    private int ETCResult;
    private SimpleMoveComparator simpleComparator;

    public MTD(Game paramGame, TranspositionTable paramTranspositionTable) {
        this.tTable = paramTranspositionTable;
        this.g = paramGame;

        this.simpleComparator = new SimpleMoveComparator(paramGame);
    }

    public MTD(Game paramGame) {
        this(paramGame, new TranspositionTable());
    }

    protected abstract int first(int paramInt);

    protected abstract int next(int L, int H, int g);

    public void interrupt() {
        this.interrupted = true;
    }

    public boolean isInterrupted() {
        return this.interrupted;
    }

    public final synchronized void search(int paramInt1, int paramInt2) {
        int k = 1000000001;
        int m = -1000000001;
        int i;
        int j = i = first(paramInt1);

        Move localMove = this.foundMove;
        this.interrupted = false;

        this.searchDepth = paramInt2;

        this.driverPasses = 0;

        System.out.println("********* MTD Routine***************");

        System.out.println("Depth=" + paramInt2);

        this.tTUsed = (this.cutOffs = this.ETCCutoffs = this.total = 0);
        this.tTOrderingWorks = 0;
        do {
            i = mtTopMost(j - 1, paramInt2, paramInt1);

            if (i < j)
                k = i;
            else
                m = i;

            this.driverPasses += 1;

            j = next(m, k, i);
        } while ((k > m) && (!this.interrupted));

        System.out.println("driver passes " + this.driverPasses);

        if (this.interrupted)
            System.out.println("INTERRUPTED");

        System.out.println("TT-size " + this.tTable.getCount());
        System.out.println("TT-collisions " + this.tTable.collisions);
        System.out.println("Total nodes searched :" + this.total);
        System.out.println("Transpositions :" + this.tTUsed);
        System.out.println("Cutoffs( both normal and transposed) :" + this.cutOffs);
        System.out.println("Enchanced Transposition cutoffs :" + this.ETCCutoffs);
        System.out.println("Transopistion Orderings proper:" + this.tTOrderingWorks);
        System.out.println("Found value:" + i);
        System.out.println("************************************");

        if (!this.interrupted) {
            this.foundMinMaxValue = i;
            this.forColor = paramInt1;
        } else
            this.foundMove = localMove;
    }

    private final boolean isUseful(Move paramMove, int paramInt) {
        return true;
    }

    private final Move ETC(int paramInt1, Collection paramCollection, int paramInt2, int paramInt3) {
        Move localObject = null;

        Iterator localIterator = paramCollection.iterator();
        while ((localIterator.hasNext()) && (!this.interrupted)) {
            Move localMove = (Move) localIterator.next();
            this.g.doMove(localMove);

            TableRepresentation localTableRepresentation = TableRepresentation.getKey(this.g.table);
            TranspositionEntry localTranspositionEntry = this.tTable.retrieve(localTableRepresentation);
            if (localTranspositionEntry != null) {
                int i = localTranspositionEntry.getLowerBound(paramInt3, paramInt2 - 1);
                if (i > paramInt1) {
                    this.ETCResult = i;
                    localObject = localMove;
                    localTranspositionEntry.cutoff = true;
                }
            }

            this.g.unDoMove(localMove);
            if (localObject != null)
                return localObject;
        }
        return null;
    }

    private final int mtTopMost(int paramInt1, int paramInt2, int paramInt3) {
        this.total += 1;

        TableRepresentation localTableRepresentation = TableRepresentation.getKey(this.g.table);
        this.foundMove = null;
        TranspositionEntry localTranspositionEntry = this.tTable.retrieve(localTableRepresentation);
        Move localMove1 = null;

        if (localTranspositionEntry != null) {
            int i = localTranspositionEntry.getLowerBound(paramInt3, paramInt2);
            int j = localTranspositionEntry.getUpperBound(paramInt3, paramInt2);

            this.foundMove = localTranspositionEntry.getMove(paramInt3);

            if (this.foundMove != null)
                if (i < j) {
                    if (i > paramInt1) {
                        this.cutOffs += 1;
                        this.tTUsed += 1;

                        localTranspositionEntry.cutoff = true;
                        return i;
                    }
                    if (j < paramInt1) {
                        this.tTUsed += 1;

                        localTranspositionEntry.cutoff = false;
                        return j;
                    }
                } else if (i == j) {
                    this.tTUsed += 1;
                    this.cutOffs += 1;

                    return i;
                }

            localMove1 = localTranspositionEntry.getMove(paramInt3);
        }

        Move localObject = null;
        int m = -1000000000;
        int n = 0;

        if (localTranspositionEntry == null)
            localTranspositionEntry = new TranspositionEntry(localTableRepresentation.cloneTableRepresentation(), this.g.table.size());

        if (paramInt2 == 0)
            m = this.g.evaluate(paramInt3);
        else if ((this.g.isWin(0)) || (this.g.isWin(1))) {
            n = 1;
            m = this.g.evaluate(paramInt3);
        } else {
            Collection localCollection;
            if (localTranspositionEntry.moves[paramInt3] == null) {
                localCollection = this.g.getMoves(paramInt3, this.simpleComparator);
                localTranspositionEntry.moves[paramInt3] = localCollection;
            } else
                localCollection = localTranspositionEntry.moves[paramInt3];
            Move localMove2;
            if ((paramInt2 > 1)
                    && ((localMove2 = ETC(paramInt1, localCollection, paramInt2, paramInt3)) != null)) {
                this.ETCCutoffs += 1;
                this.foundMove = localMove2;
                return this.ETCResult;
            }

            Iterator localIterator = localCollection.iterator();

            if (localMove1 != null)
                localMove2 = localMove1;
            else if (localIterator.hasNext())
                localMove2 = (Move) localIterator.next();
            else
                return -mt(-paramInt1, paramInt2 - 1, paramInt3 == 0 ? 1 : 0);

            localObject = localMove2;

            while ((localMove2 != null) && (m < paramInt1) && (!this.interrupted)) {
                if ((m == -1000000000) || (isUseful(localMove2, 0))) {
                    this.g.doMove(localMove2);
                    int k = -mt(-paramInt1, paramInt2 - 1, paramInt3 == 0 ? 1 : 0);
                    this.g.unDoMove(localMove2);

                    if (k > m) {
                        m = k;
                        localObject = localMove2;
                    }
                }

                localMove2 = localIterator.hasNext() ? (Move) localIterator.next() : null;
                if ((localMove2 != null)
                        && (localMove2.equals(localMove1)))
                    localMove2 = localIterator.hasNext() ? (Move) localIterator.next() : null;

            }

            if (this.interrupted)
                return 0;

            this.foundMove = localObject;

            if (this.foundMove == localTranspositionEntry.getMove(paramInt3))
                this.tTOrderingWorks += 1;

        }

        if (n != 0) {
            localTranspositionEntry.setUpperBound(paramInt3, 1000000000, m);
            localTranspositionEntry.setLowerBound(paramInt3, 1000000000, m);
            localTranspositionEntry.cutoff = (m >= paramInt1);
        } else if (paramInt2 == 0) {
            localTranspositionEntry.setUpperBound(paramInt3, 0, m);
            localTranspositionEntry.setLowerBound(paramInt3, 0, m);
        } else {
            localTranspositionEntry.setMove(paramInt3, localObject);
            if (m < paramInt1) {
                localTranspositionEntry.setUpperBound(paramInt3, paramInt2, m);
                localTranspositionEntry.cutoff = false;
            } else {
                localTranspositionEntry.setLowerBound(paramInt3, paramInt2, m);
                localTranspositionEntry.cutoff = true;

                this.cutOffs += 1;
            }
        }

        this.tTable.store(localTranspositionEntry);

        return m;
    }

    private final int mt(int paramInt1, int paramInt2, int paramInt3) {
        this.total += 1;

        TableRepresentation localTableRepresentation = TableRepresentation.getKey(this.g.table);
        TranspositionEntry localTranspositionEntry = this.tTable.retrieve(localTableRepresentation);
        Move localMove1 = null;

        if (localTranspositionEntry != null) {
            int i = localTranspositionEntry.getLowerBound(paramInt3, paramInt2);
            int j = localTranspositionEntry.getUpperBound(paramInt3, paramInt2);

            if (i < j) {
                if (i > paramInt1) {
                    this.cutOffs += 1;
                    this.tTUsed += 1;

                    localTranspositionEntry.cutoff = true;
                    return i;
                }
                if (j < paramInt1) {
                    this.tTUsed += 1;

                    localTranspositionEntry.cutoff = false;
                    return j;
                }
            } else if (i == j) {
                this.cutOffs += 1;
                this.tTUsed += 1;

                return i;
            }

            localMove1 = localTranspositionEntry.getMove(paramInt3);
        }

        Move localObject = null;
        int m = -1000000000;
        int n = 0;

        if (localTranspositionEntry == null)
            localTranspositionEntry = new TranspositionEntry(localTableRepresentation.cloneTableRepresentation(), this.g.table.size());

        if (paramInt2 == 0)
            m = this.g.evaluate(paramInt3);
        else if ((this.g.isWin(0)) || (this.g.isWin(1))) {
            n = 1;
            m = this.g.evaluate(paramInt3);
        } else {
            Collection localCollection;
            if (localTranspositionEntry.moves[paramInt3] == null) {
                localCollection = this.g.getMoves(paramInt3, this.simpleComparator);
                localTranspositionEntry.moves[paramInt3] = localCollection;
            } else
                localCollection = localTranspositionEntry.moves[paramInt3];
            Move localMove2;
            if ((paramInt2 > 1)
                    && ((localMove2 = ETC(paramInt1, localCollection, paramInt2, paramInt3)) != null)) {
                this.ETCCutoffs += 1;
                return this.ETCResult;
            }

            Iterator localIterator = localCollection.iterator();

            if (localMove1 != null)
                localMove2 = localMove1;
            else if (localIterator.hasNext())
                localMove2 = (Move) localIterator.next();
            else
                return -mt(-paramInt1, paramInt2, paramInt3 == 0 ? 1 : 0);

            localObject = localMove2;

            while ((localMove2 != null) && (m < paramInt1) && (!this.interrupted)) {
                if ((m == -1000000000) || (isUseful(localMove2, 0))) {
                    this.g.doMove(localMove2);
                    int k = -mt(-paramInt1, paramInt2 - 1, paramInt3 == 0 ? 1 : 0);

                    this.g.unDoMove(localMove2);

                    if (k > m) {
                        m = k;
                        localObject = localMove2;
                    }

                }

                localMove2 = localIterator.hasNext() ? (Move) localIterator.next() : null;
                if ((localMove2 != null)
                        && (localMove2.equals(localMove1)))
                    localMove2 = localIterator.hasNext() ? (Move) localIterator.next() : null;

            }

            if (this.interrupted)
                return 0;

            if (localObject == localTranspositionEntry.getMove(paramInt3))
                this.tTOrderingWorks += 1;

        }

        if (n != 0) {
            localTranspositionEntry.setUpperBound(paramInt3, 1000000000, m);
            localTranspositionEntry.setLowerBound(paramInt3, 1000000000, m);
            localTranspositionEntry.cutoff = (m >= paramInt1);
        } else if (paramInt2 == 0) {
            localTranspositionEntry.setUpperBound(paramInt3, 0, m);
            localTranspositionEntry.setLowerBound(paramInt3, 0, m);
        } else {
            localTranspositionEntry.setMove(paramInt3, localObject);
            if (m < paramInt1) {
                localTranspositionEntry.setUpperBound(paramInt3, paramInt2, m);
                localTranspositionEntry.cutoff = false;
            } else {
                localTranspositionEntry.setLowerBound(paramInt3, paramInt2, m);
                localTranspositionEntry.cutoff = true;

                this.cutOffs += 1;
            }
        }

        this.tTable.store(localTranspositionEntry);

        return m;
    }
}