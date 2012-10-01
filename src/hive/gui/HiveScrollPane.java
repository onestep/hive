package hive.gui;

import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class HiveScrollPane extends JScrollPane
{
  private HiveContainer hiveContainer = null;

  public HiveScrollPane()
  {
  }

  public HiveScrollPane(HiveContainer paramHiveContainer)
  {
    super(paramHiveContainer);
    this.hiveContainer = paramHiveContainer;
    setPreferredSize(new Dimension(2 * paramHiveContainer.CELL_WIDTH, 2 * paramHiveContainer.CELL_HEIGHT));
    initHiveScrollPane();
  }

  public void setViewportView(HiveContainer paramHiveContainer)
  {
    super.setViewportView(paramHiveContainer);
    this.hiveContainer = paramHiveContainer;
    setPreferredSize(new Dimension(2 * paramHiveContainer.CELL_WIDTH, 2 * paramHiveContainer.CELL_HEIGHT));
    initHiveScrollPane();
  }

  public void initHiveScrollPane()
  {
    int i = this.hiveContainer.getOriginX() - (int)getSize().getWidth() / 2;

    int j = this.hiveContainer.getOriginY() - (int)getSize().getHeight() / 2;

    this.viewport.setViewPosition(new Point(i, j));
  }
}