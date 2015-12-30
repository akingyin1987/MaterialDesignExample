package com.aswifter.material.net;

import com.google.gson.TypeAdapter;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.Reader;

import retrofit.Converter;

/**
 * Created by zlcd on 2015/12/30.
 */
public class StringResponBodyConverter<T>  implements Converter<Response, T> {




    @Override
    public T convert(Response value) throws IOException {

        return(T) value.body().string();
    }
}
