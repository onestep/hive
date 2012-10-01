package hive.game;

import java.util.Collection;

public final class TranspositionEntry
  implements Constants
{
  public final TableRepresentation tableRepresentation;
  private static final int forColor = 0;
  private int lowerbound;
  private int higherbound;
  private int l_depth = -1000000000; private int h_depth = -1000000000;
  public final int numOfPieces;
  public Move[] proposedMove = { null, null };
  public Collection[] moves = { null, null };
  public boolean cutoff;

  public int depth()
  {
    return this.l_depth + this.h_depth + 1 >> 1;
  }

  TranspositionEntry(TableRepresentation paramTableRepresentation, int paramInt)
  {
    this.numOfPieces = paramInt;
    this.tableRepresentation = paramTableRepresentation;
    this.cutoff = false;
    this.lowerbound = -1000000000;
    this.higherbound = 1000000000;
  }

  public final int getLowerBound(int paramInt1, int paramInt2)
  {
    return paramInt2 <= this.h_depth ? -this.higherbound : paramInt1 == 0 ? -1000000000 : paramInt2 <= this.l_depth ? this.lowerbound : -1000000000;
  }

  public final int getUpperBound(int paramInt1, int paramInt2)
  {
    return paramInt2 <= this.l_depth ? -this.lowerbound : paramInt1 == 0 ? 1000000000 : paramInt2 <= this.h_depth ? this.higherbound : 1000000000;
  }

  public final void setLowerBound(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 == 0) {
      if (paramInt2 >= this.l_depth) {
        this.lowerbound = paramInt3;
        this.l_depth = paramInt2;
      }
    }
    else if (paramInt2 >= this.h_depth) {
      this.higherbound = (-paramInt3);
      this.h_depth = paramInt2;
    }
  }

  public final void setUpperBound(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt1 != 0) {
      if (paramInt2 >= this.l_depth) {
        this.lowerbound = (-paramInt3);
        this.l_depth = paramInt2;
      }
    }
    else if (paramInt2 >= this.h_depth) {
      this.higherbound = paramInt3;
      this.h_depth = paramInt2;
    }
  }

  public final Move getMove(int paramInt)
  {
    return this.proposedMove[paramInt];
  }

  public final void setMove(int paramInt, Move paramMove)
  {
    this.proposedMove[paramInt] = paramMove;
  }
}