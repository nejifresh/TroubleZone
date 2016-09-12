package com.nejitawo.troublezone.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nejitawo.troublezone.Services.LocationService;

/**
 * Created by devthehomes on 8/3/16.
 */
public class StartServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)){
            Intent serviceIntent = new Intent(context, LocationService.class);
            context.startService(serviceIntent);

        }
    }
}
