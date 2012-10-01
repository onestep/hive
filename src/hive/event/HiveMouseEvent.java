package hive.event;

import hive.gui.HiveContainer;
import hive.gui.HivePoint;
import java.awt.event.MouseEvent;

public class HiveMouseEvent
{
  private int p;
  private int q;
  public MouseEvent originalEvent;
  public HiveContainer sender;

  public HiveMouseEvent(int paramInt1, int paramInt2, MouseEvent paramMouseEvent)
  {
    this.p = paramInt1;
    this.q = paramInt2;
    this.originalEvent = paramMouseEvent;
  }

  public int getP()
  {
    return this.p;
  }

  public int getQ()
  {
    return this.q;
  }

  public HivePoint getHivePoint()
  {
    return new HivePoint(this.p, this.q);
  }
}