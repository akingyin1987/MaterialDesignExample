package com.aswifter.material.net.scalar;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;

import retrofit.Converter;

/**
 * Created by zlcd on 2015/12/31.
 */
final class ScalarRequestBodyConverter<T> implements Converter<T, RequestBody> {
    static final ScalarRequestBodyConverter<Object> INSTANCE = new ScalarRequestBodyConverter<>();

    private static final MediaType MEDIA_TYPE = MediaType.parse("text/plain; charset=UTF-8");

    private ScalarRequestBodyConverter() {
    }


    @Override
    public RequestBody convert(T value) throws IOException {
        return RequestBody.create(MEDIA_TYPE, String.valueOf(value));
    }
}
