package hive.plaf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;

public class NuHiveButtonUI extends BasicButtonUI
{
  private static final BasicButtonUI buttonUI = new NuHiveButtonUI();

  final double TG60 = 1.7320508075D;
  final int strokeSize = 4;
  private static Rectangle viewRect = new Rectangle();
  private static Rectangle textRect = new Rectangle();
  private static Rectangle iconRect = new Rectangle();
  private static final int shadowOffset = 8;
  private static final int roundDiameter = 30;
  private static final Color rolloverColor = Color.orange;
  private static final Color normalColor = Color.white;
  private static final Color shadow = new Color(190, 170, 170);

  private static final RenderingHints rh = new RenderingHints(null);

  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return buttonUI;
  }

  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    ((AbstractButton)paramJComponent).setRolloverEnabled(true);
    Insets localInsets = paramJComponent.getInsets();
    EmptyBorder localEmptyBorder = new EmptyBorder(localInsets);
    LookAndFeel.uninstallBorder((AbstractButton)paramJComponent);

    Font localFont = new Font("Arial Narrow", paramJComponent.getFont().getStyle() | 0x1, 5 * paramJComponent.getFont().getSize() >> 2);

    paramJComponent.setFont(localFont);
  }

  public void drawHexagonBorder(Graphics paramGraphics, Rectangle paramRectangle)
  {
    BasicStroke localBasicStroke = new BasicStroke(4.0F, 1, 2);
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
    Color localColor = Color.black;
    int i = paramRectangle.height;
    int j = paramRectangle.width;
    int k = (int)(0.5D * i / 1.7320508075D);

    int m = paramRectangle.x + (paramRectangle.height >> 1);
    int n = paramRectangle.y + (paramRectangle.height >> 1);

    int[] arrayOfInt1 = { m - k, m + k, (int)(m + 0.5D * i) - 2, m + k, m - k, (int)(m - 0.5D * i) + 2, m - k };

    int[] arrayOfInt2 = { n - (int)(0.5D * i) + 2, n - (int)(0.5D * i) + 2, n, n + (int)(0.5D * i) - 2, n + (int)(0.5D * i) - 2, n, n - (int)(0.5D * i) + 2 };

    localGraphics2D.setColor(localColor);
    localGraphics2D.setStroke(localBasicStroke);
    localGraphics2D.drawPolygon(arrayOfInt1, arrayOfInt2, 7);
  }

  public void fillHexagon(Graphics paramGraphics, Rectangle paramRectangle)
  {
    BasicStroke localBasicStroke = new BasicStroke(4.0F, 1, 2);
    Graphics2D localGraphics2D = (Graphics2D)paramGraphics;
    Color localColor = Color.black;
    int i = paramRectangle.height;
    int j = paramRectangle.width;
    int k = (int)(0.5D * i / 1.7320508075D);

    int m = paramRectangle.x + (paramRectangle.height >> 1);
    int n = paramRectangle.y + (paramRectangle.height >> 1);

    int[] arrayOfInt1 = { m - k, m + k, (int)(m + 0.5D * i) - 2, m + k, m - k, (int)(m - 0.5D * i) + 2, m - k };

    int[] arrayOfInt2 = { n - (int)(0.5D * i) + 2, n - (int)(0.5D * i) + 2, n, n + (int)(0.5D * i) - 2, n + (int)(0.5D * i) - 2, n, n - (int)(0.5D * i) + 2 };

    localGraphics2D.fillPolygon(arrayOfInt1, arrayOfInt2, 7);
  }

  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    if ((paramGraphics instanceof Graphics2D)) {
      ((Graphics2D)paramGraphics).setRenderingHints(rh);
    }

    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    ButtonModel localButtonModel = localAbstractButton.getModel();

    Insets localInsets = paramJComponent.getInsets();

    viewRect.x = (localInsets.left + 8);
    viewRect.y = localInsets.top;
    viewRect.width = localAbstractButton.getWidth();
    viewRect.height = localAbstractButton.getHeight();

    textRect.x = (textRect.y = textRect.width = textRect.height = 0);
    iconRect.x = (iconRect.y = iconRect.width = iconRect.height = 0);

    viewRect.height -= 8;

    if (localButtonModel.isPressed()) {
      viewRect.y += 8;
    }
    else if (localButtonModel.isEnabled())
    {
      paramGraphics.setColor(shadow);
      Rectangle localObject = new Rectangle(viewRect.x - 8, viewRect.y + 8, viewRect.width, viewRect.height);
      fillHexagon(paramGraphics, localObject);
    }

    if (localButtonModel.isRollover())
      paramGraphics.setColor(rolloverColor);
    else {
      paramGraphics.setColor(normalColor);
    }

    fillHexagon(paramGraphics, viewRect);
    drawHexagonBorder(paramGraphics, viewRect);

    Font localObject = paramJComponent.getFont();
    paramGraphics.setFont(localObject);
    FontMetrics localFontMetrics = paramGraphics.getFontMetrics();

    String str = SwingUtilities.layoutCompoundLabel(paramJComponent, localFontMetrics, localAbstractButton.getText(), localAbstractButton.getIcon(), localAbstractButton.getVerticalAlignment(), localAbstractButton.getHorizontalAlignment(), localAbstractButton.getVerticalTextPosition(), localAbstractButton.getHorizontalTextPosition(), viewRect, iconRect, textRect, localAbstractButton.getText() == null ? 0 : localAbstractButton.getIconTextGap());

    clearTextShiftOffset();

    if (localButtonModel.isRollover())
      paramGraphics.setColor(rolloverColor);
    else {
      paramGraphics.setColor(normalColor);
    }
    paramGraphics.fillRect(textRect.x, textRect.y, textRect.width, textRect.height);

    if ((localButtonModel.isArmed()) && (localButtonModel.isPressed())) {
      paintButtonPressed(paramGraphics, localAbstractButton);
    }

    if (localAbstractButton.getIcon() != null) {
      paintIcon(paramGraphics, paramJComponent, iconRect);
    }

    if ((str != null) && (!str.equals(""))) {
      paintText(paramGraphics, localAbstractButton, textRect, str);
    }

    if ((localAbstractButton.isFocusPainted()) && (localAbstractButton.hasFocus()))
    {
      paintFocus(paramGraphics, localAbstractButton, viewRect, textRect, iconRect);
    }
  }

  public Dimension getPreferredSize(JComponent paramJComponent)
  {
    Dimension localDimension = super.getPreferredSize(paramJComponent);
    int i = (int)((localDimension.getHeight() + 8.0D) / 1.7320508075D);
    localDimension.setSize(localDimension.getWidth() + i + 3.0D + 8.0D, localDimension.getHeight() + 8.0D + 25.0D);
    return localDimension;
  }

  static
  {
    rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    rh.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
  }
}