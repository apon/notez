package me.apon.notez.data.model;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/20.
 */

public class SyncEvent {
    public static final int TYPE_NOTE = 100;
    public static final int TYPE_NOTEBOOK = 200;

    private int type;
    private boolean isOk;

    public SyncEvent(int type, boolean isOk) {
        this.type = type;
        this.isOk = isOk;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }
}
