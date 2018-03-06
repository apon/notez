package me.apon.notez.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * 在此写用途
 *
 * @version V1.0 <描述当前版本功能>
 * @FileName: me.apon.notez.data.model.BaseResponse.java
 * @author: yaopeng(aponone@gmail.com)
 * @date: 2018-01-26
 */

public class BaseResponse {

    /**
     * Ok : true
     * Msg :
     */

    @SerializedName("Ok")
    private boolean Ok = true;
    @SerializedName("Msg")
    private String Msg;

    public boolean isOk() {
        return Ok;
    }

    public void setOk(boolean Ok) {
        this.Ok = Ok;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String Msg) {
        this.Msg = Msg;
    }
}
