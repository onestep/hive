package hive.plaf;

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
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;

public class HiveButtonUI extends BasicButtonUI
{
  private static final BasicButtonUI buttonUI = new HiveButtonUI();

  private static Rectangle viewRect = new Rectangle();
  private static Rectangle textRect = new Rectangle();
  private static Rectangle iconRect = new Rectangle();
  private static final int shadowOffset = 5;
  private static final int roundDiameter = 30;
  private static final Color lightBlue = new Color(49, 239, 255);
  private static final Color middleBlue = new Color(66, 181, 189);
  private static final Color shineBlue = new Color(99, 247, 255);
  private static final Color darkBlue = new Color(90, 148, 107);
  private static final Color shadow = new Color(198, 165, 41);

  private static final RenderingHints rh = new RenderingHints(null);

  public static ComponentUI createUI(JComponent paramJComponent)
  {
    return buttonUI;
  }

  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    ((AbstractButton)paramJComponent).setRolloverEnabled(true);
    LookAndFeel.uninstallBorder((AbstractButton)paramJComponent);
  }

  public void paint(Graphics paramGraphics, JComponent paramJComponent)
  {
    if ((paramGraphics instanceof Graphics2D)) {
      ((Graphics2D)paramGraphics).setRenderingHints(rh);
    }

    AbstractButton localAbstractButton = (AbstractButton)paramJComponent;
    ButtonModel localButtonModel = localAbstractButton.getModel();

    FontMetrics localFontMetrics = paramGraphics.getFontMetrics();

    Insets localInsets = paramJComponent.getInsets();

    viewRect.x = localInsets.left;
    viewRect.y = localInsets.top;
    viewRect.width = (localAbstractButton.getWidth() - (localInsets.right + viewRect.x));
    viewRect.height = (localAbstractButton.getHeight() - (localInsets.bottom + viewRect.y));

    textRect.x = (textRect.y = textRect.width = textRect.height = 0);
    iconRect.x = (iconRect.y = iconRect.width = iconRect.height = 0);

    viewRect.height -= 5;

    if (localButtonModel.isPressed()) {
      viewRect.y += 4;
    } else if (localButtonModel.isEnabled())
    {
      paramGraphics.setColor(shadow);
      paramGraphics.fillRoundRect(viewRect.x, viewRect.y, viewRect.width, viewRect.height + 5, viewRect.height + 5, viewRect.height + 5);
    }

    paramGraphics.setColor(darkBlue);
    paramGraphics.drawRoundRect(viewRect.x, viewRect.y, viewRect.width - 1, viewRect.height, 30, 30);

    if (!localButtonModel.isRollover())
      paramGraphics.setColor(middleBlue);
    else {
      paramGraphics.setColor(lightBlue);
    }
    paramGraphics.fillRoundRect(viewRect.x, viewRect.y, viewRect.width - 1, viewRect.height, 30, 30);

    if (localButtonModel.isRollover()) {
      paramGraphics.setColor(shineBlue);
      paramGraphics.drawLine(viewRect.x + 30 >> 1, viewRect.y + 3, viewRect.x + viewRect.width - 30 >> 1, viewRect.y + 3);
      paramGraphics.drawLine(viewRect.x + 30 >> 1, viewRect.y + 15 - 3, viewRect.x + viewRect.width - 30 >> 1, viewRect.y + 15 - 3);
    }

    Font localFont = paramJComponent.getFont();
    paramGraphics.setFont(localFont);

    String str = SwingUtilities.layoutCompoundLabel(paramJComponent, localFontMetrics, localAbstractButton.getText(), localAbstractButton.getIcon(), localAbstractButton.getVerticalAlignment(), localAbstractButton.getHorizontalAlignment(), localAbstractButton.getVerticalTextPosition(), localAbstractButton.getHorizontalTextPosition(), viewRect, iconRect, textRect, localAbstractButton.getText() == null ? 0 : localAbstractButton.getIconTextGap());

    clearTextShiftOffset();

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
    localDimension.setSize(localDimension.getWidth() + 30.0D, 35.0D);
    return localDimension;
  }

  static
  {
    rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    rh.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

    rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
  }
}