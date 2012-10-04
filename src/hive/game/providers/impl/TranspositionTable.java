package hive.game.providers.impl;

import hive.game.Constants;

public class TranspositionTable implements Constants {

    private TranspositionEntry[] table;
    public final int size;
    private int count = 0;
    public int collisions = 0;

    public int getCount() {
        return count;
    }

    public TranspositionTable() {
        int i;
        int tableSize = 262144;
        do {
            try {
                table = new TranspositionEntry[tableSize];
                i = 1;
            } catch (OutOfMemoryError err) {
                System.gc();
                i = 0;
                tableSize >>= 1;
            }
        } while (i == 0);
        size = tableSize;
    }

    private int getHashIndex(TableRepresentation tr) {
        return tr.hashCode() & size - 1;
    }

    public synchronized TranspositionEntry retrieve(TableRepresentation tr) {
        TranspositionEntry entry = this.table[getHashIndex(tr)];
        if ((entry != null) && entry.tableRepresentation.equals(tr))
            return entry;
        return null;
    }

    public synchronized void store(TranspositionEntry entry) {
        int i = getHashIndex(entry.tableRepresentation);
        if (table[i] != null) {
            TranspositionEntry tableEntry = table[i];
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