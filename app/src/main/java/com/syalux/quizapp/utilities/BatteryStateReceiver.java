package com.syalux.quizapp.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BatteryStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_BATTERY_LOW.equals(action)) {
            Toast.makeText(context, "Battery LOW! Please charge your device.", Toast.LENGTH_LONG).show();
        }
        else if (Intent.ACTION_BATTERY_OKAY.equals(action)) {
            Toast.makeText(context, "Battery OKAY! Good to go.", Toast.LENGTH_LONG).show();
        }
    }
}
