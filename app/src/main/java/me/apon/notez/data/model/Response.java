package me.apon.notez.data.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static me.apon.notez.data.model.ResponseStatus.ERROR;
import static me.apon.notez.data.model.ResponseStatus.LOADING;
import static me.apon.notez.data.model.ResponseStatus.SUCCESS;

/**
 * 在此写用途
 *
 * @version V1.0 <描述当前版本功能>
 * @FileName: me.apon.notez.data.model.Response.java
 * @author: yaopeng(aponone@gmail.com)
 * @date: 2018-01-26
 */

public class Response {
    public final ResponseStatus status;

    @Nullable
    public final Object data;

    @Nullable
    public final Throwable error;

    private Response(ResponseStatus status, @Nullable Object data, @Nullable Throwable error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static Response loading() {
        return new Response(LOADING, null, null);
    }

    public static Response success(@NonNull Object data) {
        return new Response(SUCCESS, data, null);
    }

    public static Response error(@NonNull Throwable error) {
        return new Response(ERROR, null, error);
    }
}
