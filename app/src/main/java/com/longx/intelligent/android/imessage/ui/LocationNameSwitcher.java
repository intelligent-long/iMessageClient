package com.longx.intelligent.android.imessage.ui;

import android.app.Activity;
import android.widget.TextView;

import com.longx.intelligent.android.imessage.media.helper.LocationHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by LONG on 2024/2/5 at 9:59 PM.
 */
public class LocationNameSwitcher {
    private final Activity activity;
    private final TextView textView;
    private int callNumber;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public LocationNameSwitcher(Activity activity, TextView textView) {
        this.activity = activity;
        this.textView = textView;
    }

    public synchronized void fetchAndSwitchFromCoordinates(double latitude, double longitude){
        int thisCallNumber = ++ callNumber;
        executorService.submit(() -> {
            String locationName = LocationHelper.getLocationNameFromCoordinates(activity, latitude, longitude);
            synchronized (this) {
                activity.runOnUiThread(() -> {
                    if (thisCallNumber != callNumber) {
                        return;
                    }
                    textView.setText(locationName);
                });
            }
        });
    }

    public synchronized void clear(){
        activity.runOnUiThread(() -> {
            ++ callNumber;
            textView.setText("");
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
