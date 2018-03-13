package me.apon.notez.data.network;

import android.util.Log;


import me.apon.notez.utils.JsonUtil;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 在此写用途
 *
 * @version V1.0 <描述当前版本功能>
 * @FileName: me.apon.notez.data.network.HttpLogger.java
 * @author: yaopeng(aponone@gmail.com)
 * @date: 2018-01-26
 */

public class HttpLogger implements HttpLoggingInterceptor.Logger{

    private StringBuilder mMessage = new StringBuilder();
    @Override
    public void log(String message) {
        //Log.d("HTTP",message);
        // 请求或者响应开始
        if (message.startsWith("--> POST") || message.startsWith("--> GET")) {
            mMessage.setLength(0);
        }
//        // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
        if ((message.startsWith("{") && message.endsWith("}"))
                || (message.startsWith("[") && message.endsWith("]"))) {
            message = JsonUtil.formatJson(JsonUtil.decodeUnicode(message));
        }
        mMessage.append(message.concat("\n"));
        // 响应结束，打印整条日志
        if (message.startsWith("<-- END HTTP")||message.startsWith("<-- HTTP FAILED")) {
            Log.d("HTTP",mMessage.toString());
        }
    }
}
