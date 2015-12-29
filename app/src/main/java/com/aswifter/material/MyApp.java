package com.aswifter.material;

import android.app.Application;

import com.aswifter.material.okHttp.OkHttpUtils;
import com.facebook.stetho.Stetho;

/**
 * Created by zlcd on 2015/12/29.
 */
public class MyApp  extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkHttpUtils.getInstance();
        OkHttpUtils.initialize(this);

        Stetho.initialize(Stetho.newInitializerBuilder(this)
            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
            .build());

    }
}
