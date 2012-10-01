package hive.gui;

import java.awt.Graphics;

abstract class VisibleObject {

    protected int p = 0;
    protected int q = 0;
    protected int ID = -1;
    protected HiveContainer owner = null;

    VisibleObject(int paramInt1, int paramInt2) {
        this.p = paramInt1;
        this.q = paramInt2;
    }

    VisibleObject(int paramInt1, int paramInt2, int paramInt3) {
        this(paramInt1, paramInt2);
        this.ID = paramInt3;
    }

    VisibleObject(int paramInt1, int paramInt2, HiveContainer paramHiveContainer) {
        this(paramInt1, paramInt2);
        this.owner = paramHiveContainer;
    }

    VisibleObject(int paramInt1, int paramInt2, int paramInt3, HiveContainer paramHiveContainer) {
        this(paramInt1, paramInt2, paramHiveContainer);
        this.ID = paramInt3;
    }

    abstract void paintObject(Graphics paramGraphics);

    public int get_p() {
        return this.p;
    }

    public int get_q() {
        return this.q;
    }

    public int getID() {
        return this.ID;
    }

    void set_p(int paramInt) {
        this.p = paramInt;
    }

    void set_q(int paramInt) {
        this.q = paramInt;
    }

    void setOwner(HiveContainer paramHiveContainer) {
        this.owner = paramHiveContainer;
    }
}