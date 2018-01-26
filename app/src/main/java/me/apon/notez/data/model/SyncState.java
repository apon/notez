package me.apon.notez.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/26.
 */

public class SyncState {

    /**
     * LastSyncUsn : 3232
     * LastSyncTime : 上次同步时间
     */

    @SerializedName("LastSyncUsn")
    private int LastSyncUsn;
    @SerializedName("LastSyncTime")
    private String LastSyncTime;

    public int getLastSyncUsn() {
        return LastSyncUsn;
    }

    public void setLastSyncUsn(int LastSyncUsn) {
        this.LastSyncUsn = LastSyncUsn;
    }

    public String getLastSyncTime() {
        return LastSyncTime;
    }

    public void setLastSyncTime(String LastSyncTime) {
        this.LastSyncTime = LastSyncTime;
    }
}
