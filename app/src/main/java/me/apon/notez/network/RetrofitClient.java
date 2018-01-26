package me.apon.notez.network;


import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.concurrent.TimeUnit;

import me.apon.notez.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 在此写用途
 *
 * @version V1.0 <描述当前版本功能>
 * @FileName: me.apon.notez.network.RetrofitClient.java
 * @author: yaopeng(aponone@gmail.com)
 * @date: 2018-01-26
 */

public class RetrofitClient {

    private static Retrofit mRetrofit;
    private RetrofitClient(){}

    public static <T> T service(final Class<T> service) {
        return getRetrofit("").create(service);
    }

    public static Retrofit getRetrofit(String host){
        if (mRetrofit == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (BuildConfig.DEBUG){//Stetho
                builder.addNetworkInterceptor(new StethoInterceptor());
            }
            /**
             * Log信息拦截器
             */

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
            /**
             * 设置cookie
             */

            /**
             * 设置超时和重连
             */
            builder.connectTimeout(10, TimeUnit.SECONDS);
            builder.readTimeout(10, TimeUnit.SECONDS);
            builder.writeTimeout(10, TimeUnit.SECONDS);
            builder.retryOnConnectionFailure(true);
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(host)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(builder.build())
                    .build();
        }
        return mRetrofit;
    }
}
