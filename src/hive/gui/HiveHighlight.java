package hive.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

class HiveHighlight extends VisibleObject
{
  private Color HIGHLIGHT_COLOR = Color.red;

  private float HIGHLIGHT_STROKE = 1.0F;
  private BasicStroke lineStroke;

  public HiveHighlight(int paramInt1, int paramInt2, HiveContainer paramHiveContainer)
  {
    super(paramInt1, paramInt2, paramHiveContainer);
  }

  public HiveHighlight(int paramInt1, int paramInt2, Color paramColor, float paramFloat, HiveContainer paramHiveContainer)
  {
    super(paramInt1, paramInt2, paramHiveContainer);
    if (paramColor != null)
      this.HIGHLIGHT_COLOR = paramColor;
    if (paramFloat != 0.0F)
      this.HIGHLIGHT_STROKE = paramFloat;
    this.lineStroke = new BasicStroke(paramFloat, 1, 2);
  }

  void paintObject(Graphics paramGraphics)
  {
    int i = owner.p2x(this.p);
    int j = owner.q2y(this.q);
    int k = (int)(0.5D * owner.CELL_HEIGHT / 1.7320508075D);

    int[] arrayOfInt1 = { i - k, i + k, (int)(i + 0.5D * owner.CELL_WIDTH), i + k, i - k, (int)(i - 0.5D * owner.CELL_WIDTH), i - k };

    int[] arrayOfInt2 = { j - (int)(0.5D * owner.CELL_HEIGHT), j - (int)(0.5D * owner.CELL_HEIGHT), j, j + (int)(0.5D * owner.CELL_HEIGHT), j + (int)(0.5D * owner.CELL_HEIGHT), j, j - (int)(0.5D * owner.CELL_HEIGHT) };

    Graphics2D localGraphics2D = (Graphics2D)paramGraphics;

    localGraphics2D.setColor(this.HIGHLIGHT_COLOR);
    localGraphics2D.setStroke(this.lineStroke);
    localGraphics2D.drawPolygon(arrayOfInt1, arrayOfInt2, 7);
  }
}