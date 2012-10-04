package hive.event;

import hive.gui.HiveContainer;
import hive.gui.HivePoint;
import java.awt.event.MouseEvent;

public class HiveMouseEvent {

    private int p;
    private int q;
    public MouseEvent originalEvent;
    public HiveContainer sender;

    public HiveMouseEvent(int p, int q, MouseEvent event) {
        this.p = p;
        this.q = q;
        originalEvent = event;
    }

    public int getP() {
        return p;
    }

    public int getQ() {
        return q;
    }

    public HivePoint getHivePoint() {
        return new HivePoint(p, q);
    }
}