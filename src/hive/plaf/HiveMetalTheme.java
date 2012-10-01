package hive.plaf;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

public class HiveMetalTheme extends DefaultMetalTheme
{
  public static final HiveMetalTheme instance = new HiveMetalTheme();

  private static final ColorUIResource yellow1 = new ColorUIResource(198, 165, 41);
  private static final ColorUIResource yellow2 = new ColorUIResource(231, 189, 8);
  private static final ColorUIResource yellow3 = new ColorUIResource(247, 206, 24);

  private static final ColorUIResource blue1 = new ColorUIResource(90, 148, 107);
  private static final ColorUIResource blue2 = new ColorUIResource(66, 181, 189);
  private static final ColorUIResource blue3 = new ColorUIResource(49, 239, 255);

  public String getName()
  {
    return "Hive";
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