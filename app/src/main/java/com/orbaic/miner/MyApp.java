package com.orbaic.miner;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.chesire.lifecyklelog.LifecykleLog;

import java.util.Locale;

public class MyApp extends Application {

    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        setAppLocale("en");
        context = this;

        LifecykleLog.INSTANCE.initialize(this);
        LifecykleLog.INSTANCE.setRequireAnnotation(false);
    }

    public void setAppLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}
