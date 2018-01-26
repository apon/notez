package me.apon.notez.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/1/26.
 */

public class Login {

    /**
     * Ok : true
     * Token : 5500830738f41138e90003232
     * UserId : 52d26b4e99c37b609a000001
     * Email : leanote@leanote.com
     * Username : leanote
     */

    @SerializedName("Ok")
    private boolean Ok;
    @SerializedName("Token")
    private String Token;
    @SerializedName("UserId")
    private String UserId;
    @SerializedName("Email")
    private String Email;
    @SerializedName("Username")
    private String Username;

    public boolean isOk() {
        return Ok;
    }

    public void setOk(boolean Ok) {
        this.Ok = Ok;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String Token) {
        this.Token = Token;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String UserId) {
        this.UserId = UserId;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }
}
