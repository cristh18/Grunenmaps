package com.leinaro.grunenthal;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.leinaro.grunenthal.api.models.Pharmacies;
import com.leinaro.grunenthal.api.models.Pharmacy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adela on 7/02/2016.
 */
public class GrnenthalApplication extends Application{

    private static GrnenthalApplication sInstance;


    public static List<String> franquicias;
    public static List<Pharmacy> pharmacies1;
    public static List<Pharmacy> pharmacies2;
    public static List<Pharmacy> pharmacies3;
    public static String terms;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        pharmacies1 = new ArrayList<>();
        pharmacies2 = new ArrayList<>();
        pharmacies3 = new ArrayList<>();
        franquicias = new ArrayList<>();
    }

    public static GrnenthalApplication get() {
        return sInstance;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}
