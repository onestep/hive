package hive.game;

import java.io.Serializable;

public final class Coords implements Cloneable, Constants, Comparable, Serializable {

    public final int c1;
    public final int c2;
    private final int hash;

    public Coords(int c1, int c2) {
        this.c1 = c1;
        this.c2 = c2;
        hash = hashCode();
    }

    public static final synchronized Coords instance(int paramInt1, int paramInt2) {
        return new Coords(paramInt1, paramInt2);
    }

    public final int getEdge(Coords coords) {
        return localToNeighbour(coords.c1 - c1, coords.c2 - c2);
    }

    public final int getEdge(int c1, int c2) {
        return localToNeighbour(c1 - this.c1, c2 - this.c2);
    }

    public final Coords getNeighbour(int i) {
        return instance(c1 + Constants.neighbourToC1[i], c2 + Constants.neighbourToC2[i]);
    }

    final int abs(int i) {
        return i >= 0 ? i : -i;
    }

    public final int distance(Coords coords) {
        if (coords == null)
            return 1000000000;
        return abs(c1 - coords.c1 + c2 - coords.c2) >> 1;
    }

    private static final int localToNeighbour(int paramInt1, int paramInt2) {
        switch (paramInt1) {
            case 0:
                if (paramInt2 == 2)
                    return 0;
                if (paramInt2 == -2)
                    return 3;
                return -1;
            case 1:
                if (paramInt2 == 1)
                    return 1;
                if (paramInt2 == -1)
                    return 2;
                return -1;
            case -1:
                if (paramInt2 == -1)
                    return 4;
                if (paramInt2 == 1)
                    return 5;
                return -1;
        }
        return -1;
    }

    public static final int compareCoords(Coords coords, Coords coords_9_) {
        /* TODO: this is dummy, avoid it */
        throw new RuntimeException("Not implemented");
    }

    public static final int hashC1C2(int c1, int c2) {
        /* TODO: this is dummy, aboid it */
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String toString() {
        return "[ " + Integer.toString(c1) + "," + Integer.toString(c2) + " ]";
    }

    @Override
    public final int hashCode() {
        int i = c1 + c2 + 22;
        return i * (i + 1) / 2 + c2 + 22;
    }

    @Override
    public final boolean equals(Object object) {
        if (object instanceof Coords) {
            Coords coords = (Coords) object;
            return c1 == coords.c1 && c2 == coords.c2;
        }
        return false;
    }

    @Override
    public Object clone() {
        return this;
    }

    @Override
    public int compareTo(Object object) {
        Coords coords = (Coords) object;
        return (c1 > coords.c1 ? 1 : c1 < coords.c1 ? -1 : c2 > coords.c2 ? 1 : c2 < coords.c2 ? -1 : 0);
    }
}