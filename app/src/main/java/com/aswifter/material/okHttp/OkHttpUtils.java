package com.aswifter.material.okHttp;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.aswifter.material.Config;

import com.aswifter.material.okHttp.cookieStore.PersistentCookieStore;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;


import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by zlcd on 2015/12/29.
 */
public class OkHttpUtils {

    private static OkHttpClient singleton;

    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("application/octet-stream");
    private static final MediaType MEDIA_TYPE_TEXT= MediaType.parse("text/plain");

    /**
     * 创建文件类
     * @param file
     * @return
     */
    public   static RequestBody   CreateFileBody(@NonNull File file){

        RequestBody rb = RequestBody.create(MEDIA_TYPE_JPG, file);
        return rb;
    }

    /**
     * 非文件类
     * @param value
     * @return
     */
    public  static  RequestBody  CreateTextBody(@NonNull String  value){
        return RequestBody.create(MEDIA_TYPE_TEXT, value);
    }

    /**
     * 创建form表单
     * @param params
     * @param encodedKey
     * @param encodedValue
     * @return
     */
    public static RequestBody createRequestBody(@NonNull Map<String,String> params,String encodedKey,String encodedValue){
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        if(params!=null&&!params.isEmpty()){
            Set<String> keys = params.keySet();
            for(String key:keys){
                formEncodingBuilder.add(key,params.get(key));
            }
        }
        if(!TextUtils.isEmpty(encodedKey) && !TextUtils.isEmpty(encodedValue)){
            formEncodingBuilder.addEncoded(encodedKey,encodedValue);
        }
        return formEncodingBuilder.build();
    }

    public static RequestBody createRequestBody(@NonNull Map<String,String> params){
        return createRequestBody(params, null, null);
    }


    /**
     * 该不会开启异步线程。
     * @param request
     * @return
     * @throws IOException
     */
    public static Response execute(Request request) throws IOException {
        return singleton.newCall(request).execute();
    }

    /**
     * 不开异步且可回调
     * @param request
     * @param responseCallback
     */
    public static  boolean  execute(@NonNull Request  request,@Nullable Callback  responseCallback ){
        try {
            Response   response = execute(request);
            if(null != responseCallback){
                responseCallback.onResponse(response);
            }
            return  true;
        }catch (Exception e){
            if(null != responseCallback){
                responseCallback.onFailure(request,new IOException());
            }
            return  false;
        }

    }

    /**
     * 开启异步线程访问网络
     * @param request
     * @param responseCallback
     */
    public static void enqueue(Request request, Callback responseCallback){
        singleton.newCall(request).enqueue(responseCallback);
    }

    /**
     * 开启异步线程访问网络, 且不在意返回结果
     * @param request
     */
    public static void enqueue(Request request){
        singleton.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Response arg0) throws IOException {

            }

            @Override
            public void onFailure(Request arg0, IOException arg1) {

            }
        });
    }

    /**
     * 初始化保持cookie 自动管理
     * @param context
     */
    public static void initialize(@NonNull Context context) {
        singleton.setCookieHandler(new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL));
    }

    /**
     * 获取cookie
     * @return
     */
    public static CookieStore getCookieStore() {
        CookieManager cookieManager = (CookieManager) singleton.getCookieHandler();
        if(null == cookieManager){
            return  null;
        }
        return cookieManager.getCookieStore();
    }


    /**
     * 获取请求
     * @param url
     * @param method
     * @param requestBody
     * @param header
     *
     */
    public  static   Request   request(@NonNull String  url,@NonNull HttpMethod  method, @Nullable RequestBody  requestBody,
                                    @Nullable Headers header) throws  IOException{
        Request.Builder builder = new Request.Builder()
            .url(url);

        if(null != header){
            builder.headers(header);
        }
        switch (method){
            case GET:
                return  builder.get().build();

            case  POST:
                 return  builder.post(requestBody).build();
            case  PUT:
                return  builder.put(requestBody).build();
            case DELETE:
                return  builder.delete(requestBody).build();
        }
        return  null;
    }

    /**
     * 同步post请求
     * @param url
     * @param requestBody
     * @param callback
     * @return
     */
    public  static   boolean  doPost(String  url,RequestBody  requestBody,Callback callback){

        try {
            Request   request = request(url,HttpMethod.POST,requestBody,null);
            return   execute(request,callback);
        }catch (Exception e){
            return  false;
        }
    }

    public static   boolean   doPost(String  url,Map<String,String> parmas,Callback callback){
        RequestBody   requestBody = createRequestBody(parmas);
        return  doPost(url,requestBody,callback);

    }

    /**
     * 异步post请求
     * @param url
     * @param requestBody
     * @param callback
     */
    public  static   void   doAsyPost(String  url,RequestBody  requestBody,Callback callback){
        try{
            Request   request = request(url,HttpMethod.POST,requestBody,null);
             enqueue(request, callback);
        }catch (Exception e){
            e.printStackTrace();
            if(null != callback){
                callback.onFailure(null,new IOException());
            }
        }

    }


    /**
     * 同步get请求
     * @param url
     * @param callback
     */
    public  static   void   doGet(String  url,Callback  callback){

        try {
            Request   request = request(url,HttpMethod.GET,null,null);
            execute(request,callback);
        }catch (Exception e){
           e.printStackTrace();
        }
    }


    public static OkHttpClient getInstance() {
        if (singleton == null) {
            synchronized (OkHttpUtils.class) {
                if (singleton == null) {
                    File cacheDir = new File(Config.HTTP_Cache_Dir, Config.RESPONSE_CACHE);

                    singleton = new OkHttpClient();
                    singleton.networkInterceptors().add(new StethoInterceptor());
                    try {
                        singleton.setCache(new Cache(cacheDir, Config.RESPONSE_CACHE_SIZE));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    singleton.setConnectTimeout(Config.HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS);
                    singleton.setReadTimeout(Config.HTTP_READ_TIMEOUT, TimeUnit.SECONDS);
                }
            }

        }
        return singleton;
    }
}
