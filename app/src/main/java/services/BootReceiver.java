package services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import utils.Logger;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.info(this, "Boot signal received");
        Intent startServiceIntent = new Intent(context, AlarmService.class);
        Logger.debug(this,"Launching alarm");
        context.startService(startServiceIntent);
    }
}
