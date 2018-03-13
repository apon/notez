package me.apon.notez.data.network;

/**
 * 在此写用途
 *
 * @version V1.0 <描述当前版本功能>
 * @FileName: me.apon.notez.data.network.ApiException.java
 * @author: yaopeng(aponone@gmail.com)
 * @date: 2018-01-26
 */

public class ApiException extends RuntimeException {

    private boolean isOk;

    public ApiException(boolean isOk, String msg) {
            super(msg);
           this.isOk = isOk;
    }

    public boolean isOk() {
        return isOk;
    }
}
