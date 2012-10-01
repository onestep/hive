package hive.gui;

import java.awt.Component;
import java.awt.Image;
import java.io.PrintStream;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class HivePopupMenu extends JPopupMenu
{
  private final float SCALE = 30.0F;
  private HiveContainer owner;

  public HivePopupMenu(HiveContainer paramHiveContainer)
  {
    this.owner = paramHiveContainer;
  }

  public HivePopupMenu(String paramString)
  {
    super(paramString);
  }

  public void show(Component paramComponent, int paramInt1, int paramInt2, Image[] paramArrayOfImage)
  {
    int i = paramArrayOfImage.length;

    removeAll();
    System.out.println("Popup");
    for (int m = 0; m < i; m++) {
      int j = paramArrayOfImage[m].getWidth(this.owner);
      int k = paramArrayOfImage[m].getHeight(this.owner);
      Image localImage = paramArrayOfImage[m];

      add(new JMenuItem(new ImageIcon(localImage)));
    }
    super.show(paramComponent, paramInt1, paramInt2);
  }
}