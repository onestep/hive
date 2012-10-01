package hive.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.util.HashMap;

public class HivePane extends HiveContainer {

    public HivePane() {
    }

    public HivePane(HashMap<Integer, Image> imagesMap) {
        super(imagesMap);

        Dimension localDimension = new Dimension(2 * CELL_WIDTH, 2 * CELL_HEIGHT);
        setPreferredSize(localDimension);

        ORIGIN_X = (int) (localDimension.getWidth() / 2.0D);
        ORIGIN_Y = (int) (localDimension.getHeight() / 2.0D);
    }

    int p2x(int paramInt) {
        return (int) ((CELL_WIDTH - CELL_HEIGHT / 3.464101615D) * paramInt + ORIGIN_X);
    }

    int q2y(int paramInt) {
        return (int) (-0.5D * CELL_HEIGHT * paramInt + this.ORIGIN_Y);
    }

    public synchronized void setHivePiece(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, Color paramColor) {
        HivePiece localHivePiece = null;

        localHivePiece = (HivePiece) getVisibleObject(paramInt1, paramInt2, HivePiece.class);
        if (localHivePiece != null)
            visibleObjectsList.remove(localHivePiece);

        if (paramInt3 > 0) {
            localHivePiece = new HivePiece(paramInt1, paramInt2, paramInt3, paramBoolean, paramColor, this);
            visibleObjectsList.add(localHivePiece);
        }
        repaint();
    }

    public synchronized void setHivePiece(int paramInt1, int paramInt2, int paramInt3) {
        setHivePiece(paramInt1, paramInt2, paramInt3, false, null);
    }

    public int getHivePieceID(int paramInt1, int paramInt2) {
        HivePiece localHivePiece = null;

        localHivePiece = (HivePiece) getVisibleObject(paramInt1, paramInt2, HivePiece.class);

        if (localHivePiece != null)
            return localHivePiece.getID();
        return 0;
    }

    public synchronized void setHighlight(int paramInt1, int paramInt2, Color paramColor, float paramFloat, boolean paramBoolean) {
        HiveHighlight localHiveHighlight = (HiveHighlight) getVisibleObject(paramInt1, paramInt2, HiveHighlight.class);

        if (localHiveHighlight != null)
            visibleObjectsList.remove(localHiveHighlight);
        if (paramBoolean) {
            visibleObjectsList.add(new HiveHighlight(paramInt1, paramInt2, paramColor, paramFloat, this));
            repaint();
        }
    }

    public synchronized void setHighlight(int paramInt1, int paramInt2, boolean paramBoolean) {
        setHighlight(paramInt1, paramInt2, null, 0.0F, paramBoolean);
    }

    HivePoint xy2pq(int paramInt1, int paramInt2) {
        int i = paramInt1 - ORIGIN_X;

        int j = (int) (i / (CELL_WIDTH - 0.5D * CELL_HEIGHT / 1.7320508075D));
        int k;
        if (i >= 0)
            k = j + 1;
        else {
            k = j;
            j--;
        }

        int m = y2q(paramInt2, j);
        int n = y2q(paramInt2, k);

        if (module(paramInt1, paramInt2, round(p2x(j), 0, 1), round(q2y(m), 0, 1)) <= module(paramInt1, paramInt2, round(p2x(k), 0, 1), round(q2y(n), 0, 1)))
            return new HivePoint(j, m);
        return new HivePoint(k, n);
    }

    int roundDown(double paramDouble) {
        return paramDouble >= 0.0D ? (int) paramDouble : (int) paramDouble - 1;
    }

    double module(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        return Math.sqrt((paramInt1 - paramInt3) * (paramInt1 - paramInt3) + (paramInt2 - paramInt4) * (paramInt2 - paramInt4));
    }

    int y2q(int paramInt1, int paramInt2) {
        int i = paramInt1 - ORIGIN_Y;
        double d = i / (-0.5D * CELL_HEIGHT);

        if (paramInt2 % 2 == 0)
            return round(d, 0, 2);

        return round(d, 1, 2);
    }

    int round(double paramDouble, int paramInt1, int paramInt2) {
        double d1 = paramDouble - paramInt1;
        int i = (int) d1 / paramInt2;
        double d2 = Math.abs(d1 - i * paramInt2);

        if (d1 >= 0.0D)
            return d2 < paramInt2 / 2.0D ? paramInt1 + i * paramInt2 : paramInt1 + i * paramInt2 + paramInt2;

        return d2 < paramInt2 / 2.0D ? paramInt1 + i * paramInt2 : paramInt1 + i * paramInt2 - paramInt2;
    }
}