package com.app.ride.authentication.utility;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import java.util.Locale;

public class CoreApp extends MultiDexApplication {

    public static final String TAG = CoreApp.class.getSimpleName();

    private static CoreApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MultiDex.install(this);
    }

    public static synchronized CoreApp getInstance() {
        return instance;
    }
}

