package com.aswifter.material;

import android.app.Application;
import android.content.Context;

import com.aswifter.material.okHttp.OkHttpUtils;
import com.facebook.stetho.Stetho;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

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
         initImageLoader(this);
    }

    public static void initImageLoader(Context context) {

        File cacheDir = StorageUtils.getCacheDirectory(context);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)

            .threadPoolSize(3) // default
            .threadPriority(Thread.NORM_PRIORITY - 2) // default
            .tasksProcessingOrder(QueueProcessingType.FIFO) // default
            .denyCacheImageMultipleSizesInMemory()

            .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
            .memoryCacheSize(2 * 1024 * 1024)
            .memoryCacheSizePercentage(13) // default
            .diskCache(new UnlimitedDiskCache(cacheDir)) // default
            .diskCacheSize(50 * 1024 * 1024)
            .diskCacheFileCount(100)
            .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
            .imageDownloader(new BaseImageDownloader(context)) // default
            .imageDecoder(new BaseImageDecoder(true)) // default
            .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
            .writeDebugLogs()
            .build();
        // Initialize ImageLoader with configuration.

        ImageLoader.getInstance().init(config);
    }
}
