package me.apon.notez.utils;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * Created by yaopeng(aponone@gmail.com) on 2018/3/6.
 */

public class ExceptionMsgUtil {

    public static String getMsg(Throwable e){
        if (e instanceof SocketTimeoutException){
            return "请求超时!";
        } else if(e instanceof UnknownHostException){
            return "网络不可用!";
        } else if (e instanceof HttpException){
            return "服务器出错!";//HTTP 502 Bad Gateway等
        }else {
            return e.getMessage();
        }
    }
}
