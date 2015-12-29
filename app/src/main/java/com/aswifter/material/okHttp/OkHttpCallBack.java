package com.aswifter.material.okHttp;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by zlcd on 2015/12/29.
 */
public abstract class OkHttpCallBack  implements Callback{

    //请求错误
    public  abstract void   onFailure(int code,String  message);

    //请求成功
    public  abstract  void  onSuccess(JSONObject   jsonObject);


    private    boolean    iSintercept  = true;

    public boolean Valintercept() {
        return iSintercept;
    }

    public void setiSintercept(boolean iSintercept) {
        this.iSintercept = iSintercept;
    }

    @Override
    public void onFailure(Request request, IOException e) {
        onFailure(0,"网络连接错误，请检查网络是否正常开启");
    }

    @Override
    public void onResponse(Response response) throws IOException {
        try{
            int  httpcode = response.code();
            if(response.isSuccessful()){
                JSONObject    result = new JSONObject(response.body().string());
                if(iSintercept){
                    boolean   val = Valintercept();
                    if(!val){
                        return;
                    }
                }
                onSuccess(result);
            }else{
                String   errormsg = "连接服务异常，请稍后再试";
                if(httpcode == 408){
                    errormsg="请求超时，请检查网络或稍后再试";
                }else if(httpcode>=500){
                    errormsg="连接服务器失败";
                }
                onFailure(httpcode,errormsg);
            }
        }catch (Exception e){
            e.printStackTrace();
            onFailure(0,"处理数据异常");
        }
    }
}
