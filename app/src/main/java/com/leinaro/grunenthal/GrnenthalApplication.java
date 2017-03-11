package com.leinaro.grunenthal;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adela on 7/02/2016.
 */
public class GrnenthalApplication extends Application{

    public static List<String> franquicias;
    public static List<Pharmacies> pharmacies1;
    public static List<Pharmacies> pharmacies2;
    public static String terms;

    @Override
    public void onCreate() {
        super.onCreate();
        pharmacies1 = new ArrayList<Pharmacies>();
        pharmacies2 = new ArrayList<Pharmacies>();
        franquicias = new ArrayList<String>();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}
