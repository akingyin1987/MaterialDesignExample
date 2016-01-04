package com.aswifter.material.net.scalar;

import com.squareup.okhttp.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit.Converter;

/**
 * Created by zlcd on 2015/12/31.
 */
public final class ScalarsConverterFactory extends  Converter.Factory{
    public static ScalarsConverterFactory create() {
        return new ScalarsConverterFactory();
    }

    private ScalarsConverterFactory() {
    }

    @Override
    public Converter<?, RequestBody> toRequestBody(Type type, Annotation[] annotations) {
        System.out.println("type="+type.getClass().getName());
        if (type == String.class
            || type == boolean.class
            || type == Boolean.class
            || type == byte.class
            || type == Byte.class
            || type == char.class
            || type == Character.class
            || type == double.class
            || type == Double.class
            || type == float.class
            || type == Float.class
            || type == int.class
            || type == Integer.class
            || type == long.class
            || type == Long.class
            || type == short.class
            || type == Short.class) {
            return ScalarRequestBodyConverter.INSTANCE;
        }
        return null;
    }
}
