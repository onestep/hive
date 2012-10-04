package hive.game.providers.impl;

import hive.game.Constants;
import hive.game.Move;
import hive.game.TableRepresentation;
import java.util.Collection;

public final class TranspositionEntry implements Constants {

    public final TableRepresentation tableRepresentation;
    private int lowerbound;
    private int higherbound;
    private int l_depth = -INFINITY;
    private int h_depth = -INFINITY;
    public final int numOfPieces;
    public Move[] proposedMove = {null, null};
    public Collection[] moves = {null, null};
    public boolean cutoff;

    public int depth() {
        return l_depth + h_depth + 1 >> 1;
    }

    TranspositionEntry(TableRepresentation tableRepresentation, int numOfPieces) {
        this.numOfPieces = numOfPieces;
        this.tableRepresentation = tableRepresentation;
        cutoff = false;
        lowerbound = -INFINITY;
        higherbound = INFINITY;
    }

    public final int getLowerBound(int color, int depth) {
        return depth <= h_depth ? -higherbound : color == 0 ? -INFINITY : depth <= l_depth ? lowerbound : -INFINITY;
    }

    public final int getUpperBound(int color, int depth) {
        return depth <= l_depth ? -lowerbound : color == 0 ? INFINITY : depth <= h_depth ? higherbound : INFINITY;
    }

    public final void setLowerBound(int color, int depth, int bound) {
        if (color == 0) {
            if (depth >= l_depth) {
                lowerbound = bound;
                l_depth = depth;
            }
        } else if (depth >= h_depth) {
            higherbound = -bound;
            h_depth = depth;
        }
    }

    public final void setUpperBound(int color, int depth, int bound) {
        if (color != 0) {
            if (depth >= l_depth) {
                lowerbound = -bound;
                l_depth = depth;
            }
        } else if (depth >= h_depth) {
            higherbound = bound;
            h_depth = depth;
        }
    }

    public final Move getMove(int color) {
        return proposedMove[color];
    }

    public final void setMove(int color, Move move) {
        proposedMove[color] = move;
    }
}