package com.nju.urbangreen.zhenjiangurbangreen.message;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nju.urbangreen.zhenjiangurbangreen.PollingService;

public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, PollingService.class);
        context.startService(i);

    }
}
