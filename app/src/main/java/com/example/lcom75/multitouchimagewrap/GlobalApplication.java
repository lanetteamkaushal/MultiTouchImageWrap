package com.example.lcom75.multitouchimagewrap;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by lcom75 on 5/7/16.
 */
public class GlobalApplication extends Application {
    public static Handler applicationHandler;
    public static volatile Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        applicationHandler = new Handler(applicationContext.getMainLooper());
    }
}
