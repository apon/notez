package me.apon.notez.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/26.
 */
@Entity(tableName = "notebooks")
public class Notebook {
    @PrimaryKey(autoGenerate = true)
    long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @SerializedName("NotebookId")
    String notebookId;
    @SerializedName("UserId")
    String userId;
    @SerializedName("ParentNotebookId")
    String parentNotebookId; // 上级
    @SerializedName("Seq")
    int seq;// 排序
    @SerializedName("Title")
    String title;
    @SerializedName("IsBlog")
    boolean isBlog;
    @SerializedName("IsDeleted")
    boolean isDeleted;
    @SerializedName("CreatedTime")
    String createdTime;
    @SerializedName("UpdatedTime")
    String updatedTime;

    @SerializedName("Usn")
    int usn;  // 更新序号

    boolean isDirty;

    boolean isTrash;

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public boolean isTrash() {
        return isTrash;
    }

    public void setTrash(boolean trash) {
        isTrash = trash;
    }

    public String getNotebookId() {
        return notebookId;
    }

    public void setNotebookId(String notebookId) {
        this.notebookId = notebookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParentNotebookId() {
        return parentNotebookId;
    }

    public void setParentNotebookId(String parentNotebookId) {
        this.parentNotebookId = parentNotebookId;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isBlog() {
        return isBlog;
    }

    public void setBlog(boolean blog) {
        isBlog = blog;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
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

    public int getUsn() {
        return usn;
    }

    public void setUsn(int usn) {
        this.usn = usn;
    }
}
