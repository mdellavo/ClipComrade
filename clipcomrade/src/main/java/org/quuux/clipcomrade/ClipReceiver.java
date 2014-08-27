package org.quuux.clipcomrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ClipReceiver extends BroadcastReceiver {
    public ClipReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ClipService.start(context);
    }
}
