package org.wzq.android.mdm;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MdmCommand {
	private static HashMap<String, MdmCmdType> map;
	static {
		map = new HashMap<String, MdmCmdType>();
		for (MdmCmdType aType : MdmCmdType.values()) {
			map.put(aType.toString(), aType);
		}
	}

	final MdmCmdType cmdType;
	final String[] args;

	private JSONObject result;

	public JSONObject getResult() {
		return result;
	}

	public void setResult(JSONObject result) {
		this.result = result;
	}

	private MdmCommand(MdmCmdType cmdType, String[] args) {
		this.cmdType = cmdType;
		this.args = args;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append("type:" + cmdType.toString());
		if (args != null && args.length > 0) {
			sb.append("\nargs:[");
			// args here
			for (String s : args) {
				sb.append(s + ",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("]");
		}
		if (result != null) {
			sb.append("\nresult:" + result.toString());
		}
		return sb.toString();
	}

	public void argsCheckLength(int required) throws Exception {
		if (required == 0)
			return;

		if (args == null || args.length < required)
			throw new RuntimeException("mdm cmd args length check fail:" + this.toString());
	}

	/** parse single mdm command */
	private static MdmCommand fromJson(JSONObject json) throws Exception {
		if (json == null) {
			return null;
		}

		try {
			String type = json.getString("type");
			JSONArray arr = json.getJSONArray("args");
			MdmCmdType cmdType = map.get(type);
			String[] args = null;
			if (arr.length() > 0) {
				args = new String[arr.length()];
				for (int i = 0; i < arr.length(); i++)
					args[i] = arr.getString(i);
			}
			return new MdmCommand(cmdType, args);
		} catch (JSONException e) {
			throw new RuntimeException("cannot parse json to mdm command:" + json.toString());
		}
	}

	/** parse mdm list */
	public static List<MdmCommand> getCmdFromJson(JSONObject json) throws Exception {
		if (json == null) {
			return null;
		}

		try {
			String info = json.getString("info");
			if (!"mdm".equals(info)) {
				throw new RuntimeException("info must be mdm");
			}
			JSONArray arr = json.getJSONArray("list");
			List<MdmCommand> cmdList = new Vector<MdmCommand>();
			for (int i = 0; i < arr.length(); i++) {
				JSONObject item = arr.getJSONObject(i);
				MdmCommand cmd = fromJson(item);
				cmdList.add(cmd);
			}
			return cmdList;
		} catch (JSONException e) {
			throw new RuntimeException("cannot parse json to mdm command:" + json.toString());
		}
	}

}
