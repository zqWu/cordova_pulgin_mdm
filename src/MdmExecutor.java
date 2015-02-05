package org.wzq.android.mdm;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MdmExecutor {

	public final static String TAG = MdmExecutor.class.getSimpleName();
	public static final int REQUEST_CODE_MDM = 10;
	private DevicePolicyManager dPM;
	private ComponentName adminName;

	private MdmExecutor(Context ctx) {
		Context app = ctx.getApplicationContext();
		dPM = (DevicePolicyManager) app.getSystemService(Context.DEVICE_POLICY_SERVICE);
		adminName = new ComponentName(app, DeviceAdmin.class);
	}

	private static MdmExecutor instance;

	public static void init(Context ctx) {
		if (instance == null) {
			instance = new MdmExecutor(ctx);
		}
	}

	/**
	 * should check if null
	 * 
	 * @return
	 */
	public static MdmExecutor getInstance() {
		if (instance == null) {
			Log.e(TAG, "should call init(Context ctx) before");
		}
		return instance;
	}
 
	public boolean isActive() {
		return dPM.isAdminActive(adminName);
	}

	public ComponentName getAdminName() {
		return adminName;
	}

	public DevicePolicyManager getDpm() {
		return dPM;
	}

	public void active(Activity act) {
		if (isActive()) {
			Log.i(TAG, "mdm actived");
			return;
		}

		Log.i(TAG, "try activy mdm by =" + act.getClass().getName());
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminName);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "press button to active mdm");
		act.startActivityForResult(intent, REQUEST_CODE_MDM);
	}
}