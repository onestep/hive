package hive.game;

public class TranspositionTable
  implements Constants
{
  private TranspositionEntry[] table;
  public final int size;
  private int count = 0;

  public int collisions = 0;

  public int getCount()
  {
    return this.count;
  }

  public TranspositionTable()
  {
    int i = 0;

    int j = 262144;
    do
    {
      try
      {
        this.table = new TranspositionEntry[j];
        i = 1;
      } catch (OutOfMemoryError localOutOfMemoryError) {
        System.gc();
        i = 0;
        j >>= 1;
      }
    }
    while (i == 0);
    this.size = j;
  }

  private int getHashIndex(TranspositionEntry paramTranspositionEntry)
  {
    return paramTranspositionEntry.tableRepresentation.hashCode() & this.size - 1;
  }

  private int getHashIndex(TableRepresentation paramTableRepresentation) {
    return paramTableRepresentation.hashCode() & this.size - 1;
  }

  private int getHashIndex(int paramInt) {
    return paramInt & this.size - 1;
  }

  public synchronized TranspositionEntry retrieve(TableRepresentation paramTableRepresentation)
  {
    TranspositionEntry localTranspositionEntry = this.table[getHashIndex(paramTableRepresentation)];
    if ((localTranspositionEntry != null) && 
      (localTranspositionEntry.tableRepresentation.equals(paramTableRepresentation))) return localTranspositionEntry;
    return null;
  }

  public synchronized void store(TranspositionEntry paramTranspositionEntry)
  {
    int i = getHashIndex(paramTranspositionEntry.tableRepresentation);
    if (this.table[i] != null)
    {
      TranspositionEntry localTranspositionEntry = this.table[i];
      if (paramTranspositionEntry == localTranspositionEntry) return;
      this.collisions += 1;
      if (paramTranspositionEntry.numOfPieces + paramTranspositionEntry.depth() > localTranspositionEntry.numOfPieces + localTranspositionEntry.depth()) {
        this.table[i] = paramTranspositionEntry;
      }

    }
    else
    {
      this.table[i] = paramTranspositionEntry;
      this.count += 1;
    }
  }

  private static int abs(int paramInt)
  {
    return paramInt > 0 ? paramInt : -paramInt;
  }

  public String toString()
  {
    String str = "";
    for (int i = 0; i < this.size; i++)
      if (this.table[i] != null) str = str + this.table[i].toString() + "\n";
    return str;
  }

  public void print()
  {
    for (int i = 0; i < this.size; i++)
      if (this.table[i] != null) System.out.println(this.table[i].toString());
  }
}