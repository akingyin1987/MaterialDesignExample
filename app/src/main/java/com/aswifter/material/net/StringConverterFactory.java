package com.aswifter.material.net;


import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit.Converter;

/**
 * Created by zlcd on 2015/12/30.
 */
public final  class StringConverterFactory  extends  Converter.Factory {
    private StringConverterFactory() {

    }
    public static StringConverterFactory create() {
        return new StringConverterFactory();
    }
    @Override
    public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {
          StringResponBodyConverter  responBodyConverter = new StringResponBodyConverter<String>();
        return responBodyConverter;
    }

    @Override
    public Converter<?, RequestBody> toRequestBody(Type type, Annotation[] annotations) {
        return super.toRequestBody(type, annotations);
    }
}
