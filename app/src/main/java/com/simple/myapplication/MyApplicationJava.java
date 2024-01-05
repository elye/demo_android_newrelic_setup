package com.simple.myapplication;

import android.app.Application;

import com.newrelic.agent.android.NewRelic;
import com.simple.myapplication.mypackage.Foreground;

public class MyApplicationJava extends Application {
    public MyApplicationJava() {
    }

    private Foreground.Listener foregroundListener = null;


    public void onCreate() {
        super.onCreate();

        NewRelic.withApplicationToken(
                "<NEWRELIC-APPKEY>"
        ).start(this);

        Foreground.init(this);
        foregroundListener = new Foreground.Listener() {
            @Override
            public void onBecameForeground() {
            }

            @Override
            public void onBecameBackground() {
            }
        };
        Foreground.get().addListener(foregroundListener);
    }
}
