package me.apon.notez.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/26.
 */

@Entity(tableName = "tags")
public class Tag {
    @PrimaryKey(autoGenerate = true)
    long id;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @SerializedName("TagId")
    String tagId;
    @SerializedName("UserId")
    String userId;
    @SerializedName("Tag")
    String tag;
    @SerializedName("CreatedTime")
    String createdTime;
    @SerializedName("UpdatedTime")
    String updatedTime;
    @SerializedName("IsDeleted")
    boolean isDeleted; // 删除位
    // 更新序号
    @SerializedName("Usn")
    int usn;

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public int getUsn() {
        return usn;
    }

    public void setUsn(int usn) {
        this.usn = usn;
    }
}
