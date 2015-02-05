package org.wzq.android.mdm;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeviceAdmin extends DeviceAdminReceiver {
    //	implement onEnabled(), onDisabled(),

    public final static String TAG = DeviceAdmin.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    public void onEnabled(Context context, Intent intent) {
        Log.v(TAG,"onEnabled");
    }

    public void onDisabled(Context context, Intent intent) {
        Log.v(TAG, "onDisabled");
    }
}