package hive.plaf;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

public class NuHiveMetalTheme extends DefaultMetalTheme
{
  public static final NuHiveMetalTheme instance = new NuHiveMetalTheme();

  private static final ColorUIResource yellow2 = new ColorUIResource(255, 255, 255);
  private static final ColorUIResource yellow1 = new ColorUIResource(128, 128, 128);
  private static final ColorUIResource yellow3 = new ColorUIResource(64, 64, 64);

  private static final ColorUIResource blue1 = new ColorUIResource(0, 0, 0);
  private static final ColorUIResource blue2 = new ColorUIResource(255, 255, 255);
  private static final ColorUIResource blue3 = new ColorUIResource(180, 90, 23);

  public String getName()
  {
    return "NuHive";
  }

  protected ColorUIResource getPrimary1()
  {
    return blue1; } 
  protected ColorUIResource getPrimary2() { return blue2; } 
  protected ColorUIResource getPrimary3() { return blue3; }

  protected ColorUIResource getSecondary1() {
    return yellow1; } 
  protected ColorUIResource getSecondary2() { return yellow1; } 
  protected ColorUIResource getSecondary3() { return yellow2; } 
  protected ColorUIResource getWhite() {
    return yellow3;
  }
}