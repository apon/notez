package me.apon.notez.network;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import me.apon.notez.data.model.BaseResponse;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static okhttp3.internal.Util.UTF_8;

/**
 * 在此写用途
 *
 * @version V1.0 <描述当前版本功能>
 * @FileName: me.apon.notez.network.GsonResponseBodyConverter.java
 * @author: yaopeng(aponone@gmail.com)
 * @date: 2018-01-26
 */

public class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();

        BaseResponse baseResponse = gson.fromJson(response, BaseResponse.class);
        if (!baseResponse.isOk()){
            throw new ApiException(false,baseResponse.getMsg());
        }

        MediaType contentType = value.contentType();
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        InputStream inputStream = new ByteArrayInputStream(response.getBytes());
        Reader reader = new InputStreamReader(inputStream, charset);
        JsonReader jsonReader = gson.newJsonReader(reader);

        try {
            return adapter.read(jsonReader);
        } catch (Exception ex){
            //BaseResponse baseResponse = gson.fromJson(response, BaseResponse.class);
            throw new ApiException(false,ex.getMessage());
        }finally {
            value.close();
        }
    }
}
