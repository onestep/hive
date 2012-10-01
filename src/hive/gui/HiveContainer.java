package hive.gui;

import hive.event.HiveMouseEvent;
import hive.event.HiveMouseListener;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import javax.swing.JPanel;

public abstract class HiveContainer extends JPanel implements MouseListener {

    final double TG60 = 1.7320508075D;
    int CELL_WIDTH = 0;
    int CELL_HEIGHT = 0;
    protected int ORIGIN_X = 0;
    protected int ORIGIN_Y = 0;
    protected HashMap<Integer, Image> imagesMap = null;
    protected LinkedList<VisibleObject> visibleObjectsList = null;
    protected LinkedList<HiveMouseListener> mouseListenersList = null;
    private static final RenderingHints rh = new RenderingHints(null);

    HiveContainer() {
        visibleObjectsList = new LinkedList<VisibleObject>();
        mouseListenersList = new LinkedList<HiveMouseListener>();
        addMouseListener(this);
    }

    HiveContainer(HashMap<Integer, Image> imagesMap) {
        this();
        Image localImage = null;
        MediaTracker mediaTracker = new MediaTracker(this);
        this.imagesMap = imagesMap;

        for (Integer key : imagesMap.keySet()) {
            localImage = imagesMap.get(key);
            mediaTracker.addImage(localImage, key.intValue());
        }
        try {
            mediaTracker.waitForAll();
        } catch (Exception localException) {
            System.out.println("Error in HiveContainer.HiveContainer(Hashtable, Applet): MediaTracker can't load images.");

            return;
        }

        if (localImage != null) {
            CELL_HEIGHT = localImage.getHeight(this);
            CELL_WIDTH = localImage.getWidth(this);
            setMinimumSize(new Dimension(CELL_WIDTH, CELL_HEIGHT));
        } else {
            System.out.println("Error in HiveContainer.HiveContainer(Hashtable, Applet): Incorrect images size.");

            return;
        }
    }

    public synchronized void paint(Graphics paramGraphics) {
        super.paint(paramGraphics);
        if ((paramGraphics instanceof Graphics2D))
            ((Graphics2D) paramGraphics).setRenderingHints(rh);

        Iterator localIterator = this.visibleObjectsList.iterator();
        while (localIterator.hasNext())
            ((VisibleObject) localIterator.next()).paintObject(paramGraphics);
    }

    public void addHiveMouseListener(HiveMouseListener paramHiveMouseListener) {
        if (paramHiveMouseListener != null)
            this.mouseListenersList.add(paramHiveMouseListener);
    }

    public void removeHiveMouseListener(HiveMouseListener paramHiveMouseListener) {
        if (paramHiveMouseListener != null)
            this.mouseListenersList.remove(paramHiveMouseListener);
    }

    @Override
    public void mouseClicked(MouseEvent paramMouseEvent) {
        HiveMouseEvent localHiveMouseEvent = mouseEvent2HiveMouseEvent(paramMouseEvent);
        HiveMouseListener localHiveMouseListener = null;
        Iterator localIterator = ((Collection) this.mouseListenersList.clone()).iterator();

        while (localIterator.hasNext()) {
            localHiveMouseListener = (HiveMouseListener) localIterator.next();
            localHiveMouseListener.hiveMouseClicked(localHiveMouseEvent);
        }
    }

    @Override
    public void mouseEntered(MouseEvent paramMouseEvent) {
        HiveMouseEvent localHiveMouseEvent = mouseEvent2HiveMouseEvent(paramMouseEvent);
        HiveMouseListener localHiveMouseListener = null;
        Iterator localIterator = ((Collection) this.mouseListenersList.clone()).iterator();

        while (localIterator.hasNext()) {
            localHiveMouseListener = (HiveMouseListener) localIterator.next();
            localHiveMouseListener.hiveMouseEntered(localHiveMouseEvent);
        }
    }

    @Override
    public void mouseExited(MouseEvent paramMouseEvent) {
        HiveMouseEvent localHiveMouseEvent = mouseEvent2HiveMouseEvent(paramMouseEvent);
        HiveMouseListener localHiveMouseListener = null;
        Iterator localIterator = ((Collection) this.mouseListenersList.clone()).iterator();

        while (localIterator.hasNext()) {
            localHiveMouseListener = (HiveMouseListener) localIterator.next();
            localHiveMouseListener.hiveMouseExited(localHiveMouseEvent);
        }
    }

    public void mousePressed(MouseEvent paramMouseEvent) {
        HiveMouseEvent localHiveMouseEvent = mouseEvent2HiveMouseEvent(paramMouseEvent);
        HiveMouseListener localHiveMouseListener = null;
        Iterator localIterator = ((Collection) this.mouseListenersList.clone()).iterator();
        while (localIterator.hasNext()) {
            localHiveMouseListener = (HiveMouseListener) localIterator.next();
            localHiveMouseListener.hiveMousePressed(localHiveMouseEvent);
        }
    }

    public void mouseReleased(MouseEvent paramMouseEvent) {
        HiveMouseEvent localHiveMouseEvent = mouseEvent2HiveMouseEvent(paramMouseEvent);
        HiveMouseListener localHiveMouseListener = null;
        Iterator localIterator = ((Collection) this.mouseListenersList.clone()).iterator();

        while (localIterator.hasNext()) {
            localHiveMouseListener = (HiveMouseListener) localIterator.next();
            localHiveMouseListener.hiveMouseReleased(localHiveMouseEvent);
        }
    }

    public synchronized void clear() {
        this.visibleObjectsList.clear();
        repaint();
    }

    public synchronized void clearHighlights() {
        Iterator localIterator = this.visibleObjectsList.iterator();
        while (localIterator.hasNext())
            if ((localIterator.next() instanceof HiveHighlight))
                localIterator.remove();
        repaint();
    }

    public void setOriginX(int paramInt) {
        this.ORIGIN_X = paramInt;
        repaint();
    }

    public void setOriginY(int paramInt) {
        this.ORIGIN_Y = paramInt;
        repaint();
    }

    public int getOriginX() {
        return this.ORIGIN_X;
    }

    public int getOriginY() {
        return this.ORIGIN_Y;
    }

    synchronized VisibleObject getVisibleObject(int paramInt1, int paramInt2, Class paramClass) {
        VisibleObject localObject = null;
        Iterator localIterator = this.visibleObjectsList.iterator();

        while (localIterator.hasNext())
            try {
                VisibleObject localVisibleObject = (VisibleObject) localIterator.next();
                if ((localVisibleObject.get_p() == paramInt1) && (localVisibleObject.get_q() == paramInt2) && ((paramClass.isInstance(localVisibleObject)) || (paramClass == null)))
                    localObject = localVisibleObject;
            } catch (NoSuchElementException localNoSuchElementException) {
                return null;
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        return localObject;
    }

    private HiveMouseEvent mouseEvent2HiveMouseEvent(MouseEvent paramMouseEvent) {
        HivePoint localHivePoint = xy2pq(paramMouseEvent.getX(), paramMouseEvent.getY());
        HiveMouseEvent localHiveMouseEvent = new HiveMouseEvent(localHivePoint.p, localHivePoint.q, paramMouseEvent);
        localHiveMouseEvent.sender = this;
        return localHiveMouseEvent;
    }

    public synchronized void addVisibleObject(VisibleObject paramVisibleObject) {
        this.visibleObjectsList.add(paramVisibleObject);
        paramVisibleObject.setOwner(this);
        repaint();
    }

    public int getCellWidth() {
        return this.CELL_WIDTH;
    }

    public int getCellHeight() {
        return this.CELL_HEIGHT;
    }

    abstract int p2x(int paramInt);

    abstract int q2y(int paramInt);

    abstract HivePoint xy2pq(int paramInt1, int paramInt2);

    static {
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        rh.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
}