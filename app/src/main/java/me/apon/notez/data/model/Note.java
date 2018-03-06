package me.apon.notez.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/26.
 */
@Entity(tableName = "notes")
public class Note {
    @SerializedName("NoteId")
    String noteId;
    @SerializedName("NotebookId")
    String notebookId;
    @SerializedName("UserId")
    String userId;
    @SerializedName("Title")
    String title;
    @SerializedName("Tags")
    List<String> tags;
    @SerializedName("Content")
    String content;
    @SerializedName("IsMarkdown")
    boolean isMarkdown;
    @SerializedName("IsBlog")
    boolean isBlog;
    @SerializedName("IsTrash")
    boolean isTrash;
    @SerializedName("Files")
    List<NoteFile> files; // 图片, 附件
    @SerializedName("CreatedTime")
    String createdTime;
    @SerializedName("UpdatedTime")
    String updatedTime;
    @SerializedName("PublicTime")
    String publicTime;

    // 更新序号
    @SerializedName("Usn")
    int usn;

    @PrimaryKey(autoGenerate = true)
    Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isMarkdown() {
        return isMarkdown;
    }

    public void setMarkdown(boolean markdown) {
        isMarkdown = markdown;
    }

    public boolean isBlog() {
        return isBlog;
    }

    public void setBlog(boolean blog) {
        isBlog = blog;
    }

    public boolean isTrash() {
        return isTrash;
    }

    public void setTrash(boolean trash) {
        isTrash = trash;
    }

    public List<NoteFile> getFiles() {
        return files;
    }

    public void setFiles(List<NoteFile> files) {
        this.files = files;
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

    public String getPublicTime() {
        return publicTime;
    }

    public void setPublicTime(String publicTime) {
        this.publicTime = publicTime;
    }

    public int getUsn() {
        return usn;
    }

    public void setUsn(int usn) {
        this.usn = usn;
    }
}
