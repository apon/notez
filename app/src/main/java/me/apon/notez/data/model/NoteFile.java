package me.apon.notez.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/26.
 */

public class NoteFile {
    @SerializedName("FileId")
    String fileId; // 服务器端Id
    @SerializedName("LocalFileId")
    String localFileId; // 客户端Id
    @SerializedName("Type")
    String type; // images/png, doc, xls, 根据fileName确定
    @SerializedName("Title")
    String title;
    @SerializedName("HasBody")
    boolean hasBody; // 传过来的值是否要更新内容, 如果有true, 则必须传文件
    @SerializedName("IsAttach")
    boolean isAttach; // 是否是附件, 不是附件就是图片

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getLocalFileId() {
        return localFileId;
    }

    public void setLocalFileId(String localFileId) {
        this.localFileId = localFileId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHasBody() {
        return hasBody;
    }

    public void setHasBody(boolean hasBody) {
        this.hasBody = hasBody;
    }

    public boolean isAttach() {
        return isAttach;
    }

    public void setAttach(boolean attach) {
        isAttach = attach;
    }
}
