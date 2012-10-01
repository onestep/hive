package hive.gui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class HiveLabel extends VisibleObject
{
  private int x_offset;
  private int y_offset;
  private String text;

  public HiveLabel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString)
  {
    super(paramInt1, paramInt2);
    this.x_offset = paramInt3;
    this.y_offset = paramInt4;
    this.text = paramString;
  }

  void paintObject(Graphics paramGraphics)
  {
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics;

    if (owner != null) {
      Font localFont = owner.getFont();
      paramGraphics.setFont(localFont);
    }
    int i = owner.p2x(this.p) + this.x_offset;
    int j = owner.q2y(this.q) + this.y_offset;
    localGraphics2D.drawString(this.text, i, j);
  }

  public void setText(String paramString) {
    this.text = paramString;
  }
}