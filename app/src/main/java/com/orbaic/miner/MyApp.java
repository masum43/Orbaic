package com.orbaic.miner;

import android.app.Application;

import com.chesire.lifecyklelog.LifecykleLog;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LifecykleLog.INSTANCE.initialize(this);
        LifecykleLog.INSTANCE.setRequireAnnotation(false);
    }
}
