package org.telegram.p009ui.Components;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class ForegroundDetector implements Application.ActivityLifecycleCallbacks {
    private static ForegroundDetector Instance;
    private int refs;
    private boolean wasInBackground = true;
    private long enterBackgroundTime = 0;
    private CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<>();

    public interface Listener {
        void onBecameBackground();

        void onBecameForeground();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    public static ForegroundDetector getInstance() {
        return Instance;
    }

    public ForegroundDetector(Application application) {
        Instance = this;
        application.registerActivityLifecycleCallbacks(this);
    }

    public boolean isForeground() {
        return this.refs > 0;
    }

    public boolean isBackground() {
        return this.refs == 0;
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        int i = this.refs + 1;
        this.refs = i;
        if (i == 1) {
            if (SystemClock.elapsedRealtime() - this.enterBackgroundTime < 200) {
                this.wasInBackground = false;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m33d("switch to foreground");
            }
            Iterator<Listener> it = this.listeners.iterator();
            while (it.hasNext()) {
                try {
                    it.next().onBecameForeground();
                } catch (Exception e) {
                    FileLog.m30e(e);
                }
            }
        }
    }

    public boolean isWasInBackground(boolean z) {
        if (z && Build.VERSION.SDK_INT >= 21 && SystemClock.elapsedRealtime() - this.enterBackgroundTime < 200) {
            this.wasInBackground = false;
        }
        return this.wasInBackground;
    }

    public void resetBackgroundVar() {
        this.wasInBackground = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        int i = this.refs - 1;
        this.refs = i;
        if (i == 0) {
            this.enterBackgroundTime = SystemClock.elapsedRealtime();
            this.wasInBackground = true;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m33d("switch to background");
            }
            Iterator<Listener> it = this.listeners.iterator();
            while (it.hasNext()) {
                try {
                    it.next().onBecameBackground();
                } catch (Exception e) {
                    FileLog.m30e(e);
                }
            }
        }
    }
}
