package com.aswifter.material.net;

import android.content.Context;
import android.support.annotation.RequiresPermission;


import com.aswifter.material.net.scalar.ScalarsConverterFactory;
import com.aswifter.material.okHttp.OkHttpUtils;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.WireConverterFactory;
import rx.internal.util.RxThreadFactory;


/**
 * Created by zlcd on 2015/12/25.
 */
public class RetrofitUtils {

    private static Retrofit singleton;

    private static Retrofit strRetrofit;

    private  static  final  String   baseUrl="https://api.douban.com";

    public static <T> T createApi(Context context, Class<T> clazz) {
        if (singleton == null) {
            synchronized (RetrofitUtils.class) {
                if (singleton == null) {
                    Retrofit.Builder builder = new Retrofit.Builder();
                    builder.baseUrl(baseUrl);//设置远程地址
                    builder.addConverterFactory(GsonConverterFactory.create());

                    builder.client(OkHttpUtils.getInstance());

                    singleton = builder.build();

                }
            }
        }
        return singleton.create(clazz);
    }

    public static <T> T createStringApi(Context context, Class<T> clazz) {
        if (strRetrofit == null) {
            synchronized (RetrofitUtils.class) {
                if (strRetrofit == null) {
                    Retrofit.Builder builder = new Retrofit.Builder();
                    builder.baseUrl(baseUrl);//设置远程地址

                    builder.client(OkHttpUtils.getInstance());
                    strRetrofit = builder.build();
                }
            }
        }
        System.out.println("size=="+strRetrofit.converterFactories().size());
        return strRetrofit.create(clazz);
    }
}
