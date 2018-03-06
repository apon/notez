package me.apon.notez.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 *
 * Created by yaopeng(aponone@gmail.com) on 2018/3/5.
 */

@Entity(tableName = "accounts")
public class Account {

    @PrimaryKey(autoGenerate = true)
    Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
    @SerializedName("Verified")
    boolean verified;
    @SerializedName("Logo")
    String logo;

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

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
