package org.wzq.android.mdm;

import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PluginMdm extends CordovaPlugin {
	public static final int REQUEST_CODE_MDM = 10;

	public final static String TAG = "PluginMdm";
	private static final String ACTION_CHECK = "check";
	private static final String ACTION_ACTIVATE = "activate";
	private static final String ACTION_INACTIVATE = "inactivate";
	private static final String ACTION_COMMAND = "exec";

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		Log.v(TAG, "PluginMdm init...");

		Activity currAct = (Activity) webView.getContext();
		MdmExecutor.init(currAct);
		// if (!MdmExecutor.getInstance().isActive()) {
		// activate();
		// }
	}

	private CallbackContext activeCallbackContext;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode != REQUEST_CODE_MDM) {
			// we do nothing here
			onActivityResult(requestCode, resultCode, intent);
			return;
		}

		Log.i(TAG, "active, result=" + (resultCode == Activity.RESULT_OK));
		if (activeCallbackContext != null) {
			JSONObject jb = new JSONObject();
			String isResultOk = resultCode == Activity.RESULT_OK ? "true" : "false";
			try {
				jb.put("active_result", isResultOk);
				activeCallbackContext.success(jb);
			} catch (JSONException e) {
				// should not happen
			}
			activeCallbackContext = null;
		}
	}

	private void activate() {
		CordovaActivity act = (CordovaActivity) this.webView.getContext();
		Log.i(TAG, "active mdm by =" + act.getClass().getName());
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, MdmExecutor.getInstance().getAdminName());
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "press button to active mdm");
		act.startActivityForResult(PluginMdm.this, intent, REQUEST_CODE_MDM);
	}

	private void inactivate() {
		CordovaActivity act = (CordovaActivity) this.webView.getContext();
		ComponentName devAdminReceiver = MdmExecutor.getInstance().getAdminName();
		DevicePolicyManager dpm = (DevicePolicyManager) act.getSystemService(Context.DEVICE_POLICY_SERVICE);
		dpm.removeActiveAdmin(devAdminReceiver);
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		Log.d(TAG, "plugin Mdm, action=" + action + ",rawArgs=" + args.toString());
		if (ACTION_CHECK.equals(action)) {
			Log.v(TAG, ACTION_CHECK);
			JSONObject jb = new JSONObject();
			jb.put("isActive", MdmExecutor.getInstance().isActive());
			callbackContext.success(jb);
			return true;
		}

		// activate
		if (ACTION_ACTIVATE.equals(action)) {
			Log.v(TAG, ACTION_ACTIVATE);
			if (MdmExecutor.getInstance().isActive()) {
				callbackContext.success();
				return true;
			}
			activeCallbackContext = callbackContext;
			activate();
			return true;
		}

		// de-activate
		if (ACTION_INACTIVATE.equals(action)) {
			Log.v(TAG, ACTION_INACTIVATE);
			if (!MdmExecutor.getInstance().isActive()) {
				callbackContext.success();
				return true;
			}
			activeCallbackContext = callbackContext;
			inactivate();
			return true;
		}

		// do mdm execute
		if (ACTION_COMMAND.endsWith(action)) {
			if (!MdmExecutor.getInstance().isActive()) {
				callbackContext.error("mdm not active");
			}

			JSONObject jb = args.getJSONObject(0);
			List<MdmCommand> list = null;
			try {
				list = MdmCommand.getCmdFromJson(jb);
			} catch (Exception e) {
				// cannot parse
				callbackContext.error("cannot parse to mdm command");
			}

			// current: if one cmd fail, the other is still execute
			for (MdmCommand cmd : list) {
				try {
					MdmExecutor.getInstance().execCmd(cmd);
				} catch (Exception e) {
					// execute fail
					JSONObject json = new JSONObject();
					json.put("success", "false");
					json.put("reason", e.toString());
					cmd.setResult(json);
				}
			}

			// callback & return
			JSONArray jsonArr = new JSONArray();
			for (MdmCommand cmd : list) {
				jsonArr.put(cmd.toString());
			}
			callbackContext.success(jsonArr);
			return true;
		}

		Log.e(TAG, "not support action = " + action);
		return false;
	}
}
