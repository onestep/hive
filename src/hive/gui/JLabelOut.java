package hive.gui;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class JLabelOut extends OutputStream
{
  private boolean newLine;
  private JLabel label;

  public JLabelOut(JLabel paramJLabel)
  {
    this.label = paramJLabel;
    this.newLine = false;
  }

  public void write(int i) throws IOException
  {
    final int bb = i;
    SwingUtilities.invokeLater(new Runnable() {
      public void run() { if (JLabelOut.this.newLine) JLabelOut.this.label.setText("");
        JLabelOut.this.label.setText(JLabelOut.this.label.getText() + (char)bb);
        JLabelOut.this.newLine = (bb == 10);
      }
    });
  }
}