package com.cg.autoshell;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;


/**
 * Created by chengang on 2017/11/9.
 */

public class BaseApp extends MultiDexApplication {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();


    }

    public static Context getAppContext() {
        return context;
    }
}
