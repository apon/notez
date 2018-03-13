package me.apon.notez.data.network;


import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import me.apon.notez.BuildConfig;
import me.apon.notez.app.NoteApp;
import me.apon.notez.data.database.AppDatabase;
import me.apon.notez.data.model.Account;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 在此写用途
 *
 * @version V1.0 <描述当前版本功能>
 * @FileName: me.apon.notez.data.network.RetrofitClient.java
 * @author: yaopeng(aponone@gmail.com)
 * @date: 2018-01-26
 */

public class RetrofitClient {

    private static Retrofit mRetrofit;
    private RetrofitClient(){}

    public static <T> T service(final Class<T> service) {
        return getRetrofit("http://note.apon.me").create(service);
    }

    public static OkHttpClient.Builder getBuilder(){

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

        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("Connection","close")
                        .build();
                HttpUrl url = request.url();
                HttpUrl newUrl = url;
                Account account = AppDatabase.getInstance(NoteApp.app).accountDao().getCurrent();
                if (account!=null) {
                    newUrl = url.newBuilder()
                            .addQueryParameter("token", account.getToken())
                            .build();
                }
                Request newRequest = request.newBuilder()
                        .url(newUrl)
                        .build();
                return chain.proceed(newRequest);
            }
        });
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

        return builder;
    }

    public static Retrofit getRetrofit(String host){
        if (mRetrofit == null) {

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(host)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(getBuilder().build())
                    .build();
        }
        return mRetrofit;
    }
}
