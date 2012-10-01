package hive.gui;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class JAnimation extends JLabel
{
  private ImageIcon[] icons;
  private long delay;
  private boolean interrupt = false;
  private Object waitingMtx;
  private Object runningMtx;
  private Thread thread;

  public JAnimation(Image[] paramArrayOfImage, long paramLong)
  {
    this.icons = new ImageIcon[paramArrayOfImage.length];
    for (int i = 0; i < paramArrayOfImage.length; i++) {
      this.icons[i] = new ImageIcon(paramArrayOfImage[i]);
    }
    setIcon(this.icons[0]);
    this.delay = paramLong;
    this.runningMtx = new Object();
    this.waitingMtx = new Object();

    this.thread = new AnimThread();
    this.thread.setDaemon(true);
    this.thread.start();
  }

  public void start()
  {
    synchronized (this.runningMtx) {
      this.interrupt = false;
      this.runningMtx.notify();
    }
  }

  public void stop()
  {
    synchronized (this.waitingMtx) {
      this.interrupt = true;
      this.waitingMtx.notify();
    }
    synchronized (this.runningMtx) {
      int i = 2;
    }
  }

  class AnimThread extends Thread
  {
    private int index = 0;

    private void _setIcon(final int i) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          JAnimation.this.setIcon(icons[i]);
        }
      });
    }

    public void run()
    {
      synchronized (JAnimation.this.runningMtx)
      {
        synchronized (JAnimation.this.waitingMtx) {
          try {
            JAnimation.this.runningMtx.wait();
          }
          catch (Exception localException1) {
          }
          do {
            _setIcon(this.index++);
            if (this.index >= JAnimation.this.icons.length) this.index = 0;

            try
            {
              JAnimation.this.waitingMtx.wait(JAnimation.this.delay); } catch (Exception localException2) {  }
          }
          while (!JAnimation.this.interrupt);
        }
      }
    }
  }
}