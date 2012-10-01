package hive.event;

public abstract interface HiveMouseListener {

    public abstract void hiveMouseClicked(HiveMouseEvent paramHiveMouseEvent);

    public abstract void hiveMousePressed(HiveMouseEvent paramHiveMouseEvent);

    public abstract void hiveMouseReleased(HiveMouseEvent paramHiveMouseEvent);

    public abstract void hiveMouseEntered(HiveMouseEvent paramHiveMouseEvent);

    public abstract void hiveMouseExited(HiveMouseEvent paramHiveMouseEvent);
}