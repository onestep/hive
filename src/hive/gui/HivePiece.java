package hive.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;

public class HivePiece extends VisibleObject {

    private boolean stack;
    private Color stackColor;
    private static final Stroke lineStroke = new BasicStroke(2.0F, 1, 2);

    public HivePiece(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, Color paramColor, HiveContainer paramHiveContainer) {
        super(paramInt1, paramInt2, paramInt3, paramHiveContainer);
        this.stack = paramBoolean;
        this.stackColor = paramColor;
    }

    @Override
    void paintObject(Graphics paramGraphics) {
        Graphics2D localGraphics2D = (Graphics2D) paramGraphics;
        Image localImage = owner.imagesMap.get(new Integer(this.ID));
        int i = owner.p2x(p) - (1 + localImage.getWidth(null) >> 1);
        int j = owner.q2y(q) - (1 + localImage.getHeight(null) >> 1);
        if (!localGraphics2D.drawImage(localImage, i, j, owner))
            System.out.println("Error in HivePiece.paintObject(Graphics): Image not completely loaded.");

        if (this.stack) {
            int k = (int) (owner.p2x(p) - 0.25D * owner.CELL_WIDTH);
            int m = (int) (owner.q2y(q) - 0.25D * owner.CELL_HEIGHT);
            int n = (int) (0.125D * owner.CELL_HEIGHT / 1.7320508075D);
            int i1 = owner.CELL_WIDTH / 4;
            int i2 = owner.CELL_HEIGHT / 4;

            int[] arrayOfInt1 = {k - n, k + n, (int) (k + 0.5D * i1), k + n, k - n, (int) (k - 0.5D * i1), k - n};

            int[] arrayOfInt2 = {m - (int) (0.5D * i2), m - (int) (0.5D * i2), m, m + (int) (0.5D * i2), m + (int) (0.5D * i2), m, m - (int) (0.5D * i2)};

            localGraphics2D.setColor(this.stackColor);
            localGraphics2D.setStroke(lineStroke);
            localGraphics2D.fillPolygon(arrayOfInt1, arrayOfInt2, 7);
        }
    }
}