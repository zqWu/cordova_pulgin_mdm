package org.wzq.android.mdm;

import org.json.JSONObject;

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
	private Context ctx;

	private MdmExecutor(Context ctx) {
		Context app = ctx.getApplicationContext();
		this.ctx = ctx;
		dPM = (DevicePolicyManager) app.getSystemService(Context.DEVICE_POLICY_SERVICE);
		adminName = new ComponentName(app, DeviceAdmin.class);
	}

	private static MdmExecutor instance;

	public static void init(Context ctx) {
		if (instance == null) {
			instance = new MdmExecutor(ctx);
		}
	}

	/** should check if null */
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

	public static final String alpha_digit = "alpha_digit";
	public static final String alpha_digit_symbol = "alpha_digit_symbol";

	/**
	 * 
	 * @param cmd
	 * @return null = success execute and return null
	 * 		json = success execute and return json result, typically custom command
	 * @throws Exception execute fail / not support / illegal arguments ...
	 */
	public JSONObject execCmd(MdmCommand cmd) throws Exception {
		Log.i(TAG, "execute mdm cmd:" + cmd.toString());
		switch (cmd.cmdType) {
		case password_reset:
			cmd.argsCheckLength(1);
			boolean isSucc = dPM.resetPassword(cmd.args[0], 0);
			if (!isSucc) {
				throw new RuntimeException("execute mdm fail:" + cmd.toString());
			}
			break;
		case password_min_len:
			cmd.argsCheckLength(1);
			int minLen = Integer.parseInt(cmd.args[0]);
			dPM.setPasswordMinimumLength(adminName, minLen);
			resetPwdIfNecessary();
			break;
		case password_quality:
			cmd.argsCheckLength(1);
			if (alpha_digit_symbol.equals(cmd.args[0])) {
				dPM.setPasswordQuality(adminName, DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
			} else if (alpha_digit.equals(cmd.args[0])) {
				dPM.setPasswordQuality(adminName, DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);
			} else {
				throw new RuntimeException("password_quality illegal args:" + cmd.args[0]);
			}
			resetPwdIfNecessary();
		case password_min_letter:
			cmd.argsCheckLength(1);
			int minLenLetter = Integer.parseInt(cmd.args[0]);
			dPM.setPasswordMinimumLetters(adminName, minLenLetter);
			resetPwdIfNecessary();
			break;
		case password_min_letter_low:
			cmd.argsCheckLength(1);
			int minLenLow = Integer.parseInt(cmd.args[0]);
			dPM.setPasswordMinimumLowerCase(adminName, minLenLow);
			resetPwdIfNecessary();
			break;
		case password_min_digit:
			cmd.argsCheckLength(1);
			int minDigitLen = Integer.parseInt(cmd.args[0]);
			dPM.setPasswordMinimumNumeric(adminName, minDigitLen);
			resetPwdIfNecessary();
			break;
		case password_min_letter_cap:
			cmd.argsCheckLength(1);
			int minLenCap = Integer.parseInt(cmd.args[0]);
			dPM.setPasswordMinimumUpperCase(adminName, minLenCap);
			resetPwdIfNecessary();
			break;
		case password_expire_time:
			cmd.argsCheckLength(1);
			int expireTimeout = Integer.parseInt(cmd.args[0]);
			dPM.setPasswordExpirationTimeout(adminName, expireTimeout);
			break;
		case password_history_restrict:
			cmd.argsCheckLength(1);
			int newPwdMinLen = Integer.parseInt(cmd.args[0]);
			dPM.setPasswordHistoryLength(adminName, newPwdMinLen);
			break;
		case password_max_fail:
			cmd.argsCheckLength(1);
			int maxFail = Integer.parseInt(cmd.args[0]);
			dPM.setMaximumFailedPasswordsForWipe(adminName, maxFail);
			break;
		case lock_screen:
			dPM.lockNow();// 锁屏
			break;
		case timeout_screen:
			cmd.argsCheckLength(1);
			int screeTimeout = Integer.parseInt(cmd.args[0]);
			dPM.setMaximumTimeToLock(adminName, screeTimeout);
			break;
		case camera_disable:
			cmd.argsCheckLength(1);
			boolean isDisableCamera = "true".equalsIgnoreCase(cmd.args[0]);
			dPM.setCameraDisabled(adminName, isDisableCamera);
			break;
		case storage_crypt:
			boolean isEncrypt = "true".equalsIgnoreCase(cmd.args[0]);
			if (DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED == dPM.getStorageEncryptionStatus()) {
				throw new RuntimeException("storage_crypt not supported by system");
			}
			dPM.setStorageEncryption(adminName, isEncrypt);
			if (isEncrypt && DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE != dPM.getStorageEncryptionStatus()) {
				throw new RuntimeException("storage_crypt failed");
			} else if (!isEncrypt && DevicePolicyManager.ENCRYPTION_STATUS_INACTIVE != dPM.getStorageEncryptionStatus()) {
				throw new RuntimeException("storage_crypt failed");
			}
			break;
		case wipe_data:
			dPM.wipeData(0);
			break;

		case password_min_symbol:
			int minLenSym = Integer.parseInt(cmd.args[0]);
			dPM.setPasswordMinimumSymbols(adminName, minLenSym);
			resetPwdIfNecessary();
			break;

			// for some custom mdm, may return useful infomation
		// case get_installed_applist: //获取本机安装的程序信息列表
		// LocalInstalledAppInfoUtil.getInstance().execSubmitAppInfo();
		// break;
		// case uninstall_app:
		// uninstallApp(cmd.args[0]);
		// break;
		// case get_device_info:
		// DeviceInfoMgr.getInstance().execSubmitDeviceInfo();
		// break;
		// case install_app:
		// InstallMgr.getInstance().downloadFileByUrl(cmd.args[0]);
		// break;

		// case password_require:
		// // 密码长度最小1，会影响之前的长度设置，因此不可行
		// dPM.setPasswordMinimumLength(adminName, 1);
		// resetPwdIfNecessary();
		// break;
		//
		// case device_config:
		// new InstallConfig().exe(cmd.args[0]);
		// break;

		default: // this command not support
			throw new RuntimeException("cmd not supported by plugin:" + cmd.toString());
		}
		return null;
	}

	private void resetPwdIfNecessary() {
		boolean isOk = dPM.isActivePasswordSufficient();
		if (isOk) {
			return;
		}
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
		try {
			ctx.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}