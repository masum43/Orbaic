package com.orbaic.miner;

import android.app.Application;
import android.content.Context;

import com.chesire.lifecyklelog.LifecykleLog;

public class MyApp extends Application {

    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        LifecykleLog.INSTANCE.initialize(this);
        LifecykleLog.INSTANCE.setRequireAnnotation(false);
    }
}
