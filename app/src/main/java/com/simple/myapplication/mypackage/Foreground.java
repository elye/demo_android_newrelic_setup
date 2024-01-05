package com.simple.myapplication.mypackage;

import static com.newrelic.agent.android.NewRelic.recordHandledException;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Foreground implements Application.ActivityLifecycleCallbacks {

    public static final String TAG = Foreground.class.getName();
    public static final long CHECK_DELAY = 2000;
    public interface Listener {
        void onBecameForeground();
        void onBecameBackground();
    }

    public interface Binding {
        void unbind();
    }

    private interface Callback {
        void invoke(Listener listener);
    }

    private static class Listeners {
        private List<WeakReference<Listener>> listeners = new CopyOnWriteArrayList<>();

        public Binding add(Listener listener){
            final WeakReference<Listener> wr = new WeakReference<>(listener);
            listeners.add(wr);
            return new Binding(){
                public void unbind() {
                    listeners.remove(wr);
                }
            };
        }

        public void each(Callback callback){
            for (Iterator<WeakReference<Listener>> it = listeners.iterator(); it.hasNext();) {
                try {
                    WeakReference<Listener> wr = it.next();
                    Listener l = wr.get();
                    if (l != null)
                        callback.invoke(l);
                    else
                        it.remove();
                } catch (Exception exc) {
                    recordHandledException(new Exception("Listener threw exception!"), null);
                }
            }
        }
    }

    private static Callback becameForeground = new Callback() {
        @Override
        public void invoke(Listener listener) {
            listener.onBecameForeground();
        }
    };

    private static Callback becameBackground = new Callback() {
        @Override
        public void invoke(Listener listener) {
            listener.onBecameBackground();
        }
    };

    private static Foreground instance;

    private boolean foreground;
    private WeakReference<Activity> current;
    private Listeners listeners = new Listeners();
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable check;

    public static Foreground init(Application application){
        if (instance == null) {
            instance = new Foreground();
            application.registerActivityLifecycleCallbacks(instance);
        }
        return instance;
    }

    public static Foreground get(Application application){
        if (instance == null) {
            init(application);
        }
        return instance;
    }

    public static Foreground get(){
        if (instance == null) {
            throw new IllegalStateException(
                    "Foreground is not initialised - first invocation must use parameterised init/get");
        }
        return instance;
    }

    public boolean isForeground(){
        return foreground;
    }

    public boolean isBackground(){
        return !foreground;
    }

    public Binding addListener(Listener listener){
        return listeners.add(listener);
    }

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {
        // if we're changing configurations we aren't going background so
        // no need to schedule the check
        if (!activity.isChangingConfigurations()) {
            // don't prevent activity being gc'd
            final WeakReference<Activity> ref = new WeakReference<>(activity);
            handler.postDelayed(check = new Runnable() {
                @Override
                public void run() {
                    onActivityCeased(ref.get());
                }
            }, CHECK_DELAY);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        recordHandledException(new Exception("Elye NewRelic recordHandledException"), null);
        current = new WeakReference<>(activity);
        // remove any scheduled checks since we're starting another activity
        // we're definitely not going background
        if (check != null) {
            handler.removeCallbacks(check);
        }

        // check if we're becoming foreground and notify listeners
        if (!foreground && (activity != null && !activity.isChangingConfigurations())){
            foreground = true;
            recordHandledException(new Exception("become foreground"), null);
            listeners.each(becameForeground);
        } else {
            recordHandledException(new Exception("still foreground"), null);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (check != null) {
            handler.removeCallbacks(check);
        }
        onActivityCeased(activity);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {}


    private void onActivityCeased(Activity activity){
        if (foreground) {
            if ((activity == current.get()) && (activity != null && !activity.isChangingConfigurations())){
                foreground = false;
                recordHandledException(new Exception("went background"), null);
                listeners.each(becameBackground);
            } else {
                recordHandledException(new Exception("still foreground"), null);
            }
        } else {
            recordHandledException(new Exception("still background"), null);
        }
    }
}
