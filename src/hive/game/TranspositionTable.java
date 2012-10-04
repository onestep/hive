package hive.game;

public class TranspositionTable
        implements Constants {

    private TranspositionEntry[] table;
    public final int size;
    private int count = 0;
    public int collisions = 0;

    public int getCount() {
        return this.count;
    }

    public TranspositionTable() {
        int i = 0;

        int j = 262144;
        do {
            try {
                table = new TranspositionEntry[j];
                i = 1;
            } catch (OutOfMemoryError localOutOfMemoryError) {
                System.gc();
                i = 0;
                j >>= 1;
            }
        } while (i == 0);
        size = j;
    }

    private int getHashIndex(TranspositionEntry paramTranspositionEntry) {
        return paramTranspositionEntry.tableRepresentation.hashCode() & size - 1;
    }

    private int getHashIndex(TableRepresentation paramTableRepresentation) {
        return paramTableRepresentation.hashCode() & size - 1;
    }

    private int getHashIndex(int paramInt) {
        return paramInt & size - 1;
    }

    public synchronized TranspositionEntry retrieve(TableRepresentation rep) {
        TranspositionEntry entry = this.table[getHashIndex(rep)];
        if ((entry != null) && entry.tableRepresentation.equals(rep))
            return entry;
        return null;
    }

    public synchronized void store(TranspositionEntry entry) {
        int i = getHashIndex(entry.tableRepresentation);
        if (table[i] != null) {
            TranspositionEntry tableEntry = this.table[i];
            if (entry == tableEntry)
                return;
            collisions += 1;
            if (entry.numOfPieces + entry.depth() > tableEntry.numOfPieces + tableEntry.depth())
                table[i] = entry;

        } else {
            table[i] = entry;
            count += 1;
        }
    }

    private static int abs(int i) {
        return i > 0 ? i : -i;
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < size; i++)
            if (table[i] != null)
                str = str + table[i].toString() + "\n";
        return str;
    }

    public void print() {
        for (int i = 0; i < size; i++)
            if (table[i] != null)
                System.out.println(table[i].toString());
    }
}