package com.jianzhiniu.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

/******************
 * json解析工具类（有些函数是共用的）
 * 
 * @param str
 */
public class JsonTool {
	/**
	 * 登录
	 * @param str
	 * @param key
	 * @return
	 */
	public static Map<String, String> Loginjson(String str, String key) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
			}else {
				map.put("result", "1");
				JSONArray array = object.getJSONArray(key);
				JSONObject object2 = array.getJSONObject(0);
				if (key.equals("JobSeekersInfo")) {
					map.put("id", object2.getString("F_SeekerID"));
					map.put("loginid", object2.getString("F_LoginID"));
					map.put("name", object2.getString("F_Name"));
					map.put("mobile", object2.getString("F_Mobile"));
					map.put("email", object2.getString("F_Email"));
					map.put("birth", object2.getString("F_Birthday"));
					map.put("age", object2.getString("F_Age"));
					map.put("height", object2.getString("F_Height"));
					map.put("school", object2.getString("F_School"));
					map.put("profile", object2.getString("F_Profile"));
					map.put("experience", object2.getString("F_Experience"));
					map.put("capital", object2.getString("F_Capital"));
					map.put("city", object2.getString("F_City"));
					map.put("area", object2.getString("F_Area"));
					map.put("freetime", object2.getString("F_FreeTime"));
					map.put("jobsearchid", object2.getString("F_JobSearchID"));
					map.put("jobsearch", object2.getString("F_JobSearch"));
					map.put("pic", object2.getString("F_HeadPicPath"));
					map.put("sex", object2.getString("F_Sex"));
				}else {
					map.put("id", object2.getString("F_EntID"));
					map.put("loginid", object2.getString("F_LoginID"));
					map.put("companyname", object2.getString("F_CompanyName"));
					map.put("profile", object2.getString("F_Profile"));
					map.put("pic", object2.getString("F_LogoPicPath"));
					map.put("contactperson", object2.getString("F_ContactPerson"));
					map.put("mobile", object2.getString("F_ContactMobile"));
					map.put("email", object2.getString("F_ContactEmail"));
					map.put("authentication", object2.getString("F_Authentication"));
				}
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return map;
	}
	/**
	 * 判断手机号是否已注册
	 * @param str
	 * @param key
	 * @return
	 */
	public static Map<String, String> IsphoneRegisterjson(String str, String key) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
			}else {
				map.put("result", "1");
				JSONArray array = object.getJSONArray(key);
				JSONObject object2 = array.getJSONObject(0);
				if (key.equals("JobSeekersInfo")) {
					map.put("id", object2.getString("F_SeekerID"));
				}else {
					map.put("id", object2.getString("F_EntID"));
				}
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return map;
	}
	/**
	 *	版本检测
	 * @param str
	 * @param key
	 * @return
	 */
	public static Map<String, String> CheckVersionjson(String str) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
			}else {
				map.put("result", "1");
				JSONArray array = object.getJSONArray("VersionList");
				JSONObject object2 = array.getJSONObject(0);
				map.put("no", object2.getString("F_VersionNo"));
				map.put("desc", object2.getString("F_VersionDesc"));
				map.put("path", object2.getString("F_VersionPath"));
				map.put("time", object2.getString("F_IssueTime"));
				map.put("name", object2.getString("F_Version"));
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return map;
	}
	/**
	 * 时间,工作,单位类型
	 * @param str
	 * @param key
	 * @return
	 */
	public static List<Map<String, String>> JTUTypejson(String str, String key, int m) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
				list.add(map);
			}else {
				JSONArray array = object.getJSONArray(key);
				int size = array.length();
				JSONObject object2;
				for (int i = 0; i < size; i++) {
					map = new HashMap<String, String>();
					object2 = array.getJSONObject(i);
					if (m == 1) {
						map.put("id", object2.getString("F_JobSearchID"));
						map.put("name", object2.getString("F_JobSearch"));
					}else if (m == 2) {
						map.put("id", object2.getString("F_JobTypeID"));
						map.put("name", object2.getString("F_JobType"));
					}else if (m == 3) {
						map.put("id", object2.getString("F_UnitID"));
						map.put("name", object2.getString("F_WageUnit"));
					}
					list.add(map);
				}
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return list;
	}
	
	/**
	 * 广告图
	 * @param str
	 * @param key
	 * @return
	 */
	public static List<Map<String, String>> AdvertiseImgjson(String url, String str, String key) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
				list.add(map);
			}else {
				JSONArray array = object.getJSONArray(key);
				int size = array.length();
				JSONObject object2;
				for (int i = 0; i < size; i++) {
					object2 = array.getJSONObject(i);
					map = new HashMap<String, String>();
					map.put("id", object2.getString("F_AdvertID"));
					map.put("title", object2.getString("F_Title"));
					map.put("pic", url + object2.getString("F_PicPath"));
					list.add(map);
				}
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return list;
	}
	/**
	 * 报名工作信息
	 * @param str
	 * @param key
	 * @return
	 */
	public static Map<String, String> RegJobInfojson(String str) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
			}else {
				JSONArray array = object.getJSONArray("JobRegInfo");
				JSONObject object2;
				object2 = array.getJSONObject(0);
				map.put("jobid", object2.getString("F_JobID"));
				map.put("title", object2.getString("F_jobTitle"));
				map.put("entid", object2.getString("F_EntID"));
				map.put("jsid", object2.getString("F_JobSearchID"));
				map.put("city", object2.getString("F_City"));
				map.put("wage", object2.getString("F_Wage"));
				map.put("unit", object2.getString("F_WageUnit"));
				map.put("issuetime", object2.getString("F_ReleaseTime"));
				map.put("seekid", object2.getString("F_SeekerID"));
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return map;
	}
	/**
	 * 工作申请人员列表
	 * @param str
	 * @param key
	 * @return
	 */
	public static List<Map<String, String>> JobApplyerListjson(String str) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
				list.add(map);
			}else {
				JSONObject object2;
				JSONArray array = object.getJSONArray("JobRegTJ");
				object2 = array.getJSONObject(0);
				map.put("total", object2.getString("F_ALl"));
				map.put("hire", object2.getString("F_Employ"));
				map.put("refuse", object2.getString("F_Refuse"));
				list.add(map);
				array = object.getJSONArray("JobRegList");
				int size = array.length();
				for (int i = 0; i < size; i++) {
					object2 = array.getJSONObject(i);
					map = new HashMap<String, String>();
					map.put("regid", object2.getString("F_RegID"));
					map.put("seekerid", object2.getString("F_SeekerID"));
					map.put("status", object2.getString("F_StatusDisplay"));
					map.put("statusnum", object2.getString("F_Status"));
					map.put("name", object2.getString("F_SeekerName"));
					map.put("phone", object2.getString("F_SeekerMobile"));
					list.add(map);
				}
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return list;
	}
	/**
	 * 录用人员列表
	 * @param str
	 * @param key
	 * @return
	 */
	public static List<Map<String, String>> HireListjson(String str) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
				list.add(map);
			}else {
				JSONObject object2;
				JSONArray array = object.getJSONArray("JobEmployList");
				int size = array.length();
				for (int i = 0; i < size; i++) {
					object2 = array.getJSONObject(i);
					map = new HashMap<String, String>();
					map.put("regid", object2.getString("F_RegID"));
					map.put("seekerid", object2.getString("F_SeekerID"));
					map.put("status", object2.getString("F_StatusDisplay"));
					map.put("statusnum", object2.getString("F_Status"));
					map.put("name", object2.getString("F_SeekerName"));
					map.put("phone", object2.getString("F_SeekerMobile"));
					list.add(map);
				}
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return list;
	}
	/**
	 * 最新兼职
	 * @param str
	 * @param key
	 * @return
	 */
	public static List<Map<String, String>> NewJobjson(String str, String key, boolean isnear) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
				list.add(map);
			}else {
				JSONArray array = object.getJSONArray(key);
				int size = array.length();
				JSONObject object2;
				for (int i = 0; i < size; i++) {
					object2 = array.getJSONObject(i);
					map = new HashMap<String, String>();
					map.put("jobid", object2.getString("F_JobID"));
					map.put("entid", object2.getString("F_EntID"));
					map.put("comname", object2.getString("F_CompanyName"));
					map.put("title", object2.getString("F_JobTitle"));
					map.put("wage", object2.getString("F_Wage"));
					map.put("unit", object2.getString("F_WageUnit"));
					map.put("phone", object2.getString("F_ContactMobile"));
					map.put("linkman", object2.getString("F_ContactPerson"));
					map.put("city", object2.getString("F_City"));
					map.put("x", object2.getString("F_X"));
					map.put("y", object2.getString("F_Y"));
					map.put("jsid", object2.getString("F_JobSearchID"));
					map.put("issuetime", object2.getString("F_ReleaseTime"));
					map.put("status", object2.getString("F_Status"));
					map.put("auth", object2.getString("F_Authentication"));
					map.put("statusdisplay", object2.getString("F_StatusDisplay"));
					map.put("num", object2.getString("RowNumber"));
					map.put("num1", object2.getString("RowNumber1"));
					if (isnear) {
						map.put("distance", object2.getString("F_Distance"));
					}
					list.add(map);
				}
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return list;
	}
	/**
	 * 我的兼职
	 * @param str
	 * @param key
	 * @return
	 */
	public static List<Map<String, String>> MyJobjson(String str, String key) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
				list.add(map);
			}else {
				JSONArray array = object.getJSONArray(key);
				int size = array.length();
				JSONObject object2;
				for (int i = 0; i < size; i++) {
					object2 = array.getJSONObject(i);
					map = new HashMap<String, String>();
					map.put("jobid", object2.getString("F_JobID"));
					map.put("entid", object2.getString("F_EntID"));
					map.put("jsid", object2.getString("F_JobSearchID"));
					map.put("comname", object2.getString("F_CompanyName"));
					map.put("title", object2.getString("F_jobTitle"));
					map.put("linkman", object2.getString("F_ContactPerson"));
					map.put("phone", object2.getString("F_ContactMobile"));
					map.put("etime", object2.getString("F_EmployTime"));
					list.add(map);
				}
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return list;
	}
	/**
	 * 兼职管理
	 * @param str
	 * @param key
	 * @return
	 */
	public static List<Map<String, String>> JobManagejson(String str, String key) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
				list.add(map);
			}else {
				JSONArray array = object.getJSONArray(key);
				int size = array.length();
				JSONObject object2;
				for (int i = 0; i < size; i++) {
					object2 = array.getJSONObject(i);
					map = new HashMap<String, String>();
					map.put("jobid", object2.getString("F_JobID"));
					map.put("entid", object2.getString("F_EntID"));
					map.put("jsid", object2.getString("F_JobSearchID"));
					map.put("js", object2.getString("F_JobSearch"));
					map.put("jtid", object2.getString("F_JobTypeID"));
					map.put("jt", object2.getString("F_JobType"));
					map.put("jobtime", object2.getString("F_JobTime"));
					map.put("recruit", object2.getString("F_Recruitment"));
					map.put("comname", object2.getString("F_CompanyName"));
					map.put("title", object2.getString("F_JobTitle"));
					map.put("wage", object2.getString("F_Wage"));
					map.put("unit", object2.getString("F_WageUnit"));
					map.put("desc", object2.getString("F_Description"));
					map.put("istel", object2.getString("F_TelConsult"));
					map.put("phone", object2.getString("F_ContactMobile"));
					map.put("linkman", object2.getString("F_ContactPerson"));
					map.put("city", object2.getString("F_City"));
					map.put("addr", object2.getString("F_JobAddr"));
					map.put("x", object2.getString("F_X"));
					map.put("y", object2.getString("F_Y"));
					map.put("issuetime", object2.getString("F_ReleaseTime"));
					map.put("status", object2.getString("F_Status"));
					map.put("auth", object2.getString("F_Authentication"));
					map.put("statusdisplay", object2.getString("F_StatusDisplay"));
					map.put("num", object2.getString("RowNumber"));
					map.put("num1", object2.getString("RowNumber1"));
					map.put("count", object2.getString("F_RegCount"));
					list.add(map);
				}
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return list;
	}
	/**
	 * 我的申请
	 * @param str
	 * @param key
	 * @return
	 */
	public static List<Map<String, String>> MyApplyjson(String str, String key) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
				list.add(map);
			}else {
				JSONArray array = object.getJSONArray(key);
				int size = array.length();
				JSONObject object2;
				for (int i = 0; i < size; i++) {
					object2 = array.getJSONObject(i);
					map = new HashMap<String, String>();
					map.put("regid", object2.getString("F_RegID"));
					map.put("jobid", object2.getString("F_JobID"));
					map.put("entid", object2.getString("F_EntID"));
					map.put("title", object2.getString("F_jobTitle"));
					map.put("companyname", object2.getString("F_CompanyName"));
					map.put("jsid", object2.getString("F_JobSearchID"));
					map.put("phone", object2.getString("F_ContactMobile"));
					map.put("city", object2.getString("F_City"));
					map.put("wage", object2.getString("F_Wage"));
					map.put("unit", object2.getString("F_WageUnit"));
					map.put("issuetime", object2.getString("F_ReleaseTime"));
					map.put("status", object2.getString("F_Status"));
					map.put("regtime", object2.getString("F_RegTime"));
					map.put("statusdisplay", object2.getString("F_StatusDisplay"));
					map.put("contactman", object2.getString("F_ContactPerson"));
					map.put("seekid", object2.getString("F_SeekerID"));
					map.put("jobstatus", object2.getString("F_JobStatus"));
					map.put("jobdisplay", object2.getString("F_JobStatusDisplay"));
					map.put("seekname", object2.getString("F_SeekerName"));
					map.put("seekphone", object2.getString("F_SeekerMobile"));
					list.add(map);
				}
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return list;
	}
	/**
	 * 报名跟踪
	 * @param str
	 * @param key
	 * @return
	 */
	public static List<Map<String, String>> ApplyFollowjson(String str, String key) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
				list.add(map);
			}else {
				JSONArray array = object.getJSONArray(key);
				int size = array.length();
				JSONObject object2;
				for (int i = 0; i < size; i++) {
					object2 = array.getJSONObject(i);
					map = new HashMap<String, String>();
					map.put("time", object2.getString("F_LogTime"));
					map.put("content", object2.getString("F_LogContent"));
					map.put("status", object2.getString("F_StatusDisplay"));
					map.put("statusnum", object2.getString("F_Status"));
					list.add(map);
				}
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return list;
	}
	/**
	 * 最新通知
	 * @param str
	 * @param key
	 * @return
	 */
	public static List<Map<String, String>> Noticejson(String str, String key) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
				list.add(map);
			}else {
				JSONArray array = object.getJSONArray(key);
				int size = array.length();
				JSONObject object2;
				for (int i = 0; i < size; i++) {
					object2 = array.getJSONObject(i);
					map = new HashMap<String, String>();
					map.put("id", object2.getString("F_NoticeID"));
					map.put("title", object2.getString("F_Title"));
					map.put("content", object2.getString("F_PureContent"));
					map.put("issuetime", object2.getString("F_ReleaseTime"));
					list.add(map);
				}
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return list;
	}
	/**
	 * 最新消息
	 * @param str
	 * @param key
	 * @return
	 */
	public static List<Map<String, String>> Messagejson(String str, String key) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
				list.add(map);
			}else {
				JSONArray array = object.getJSONArray(key);
				int size = array.length();
				JSONObject object2;
				for (int i = 0; i < size; i++) {
					object2 = array.getJSONObject(i);
					map = new HashMap<String, String>();
					map.put("id", object2.getString("F_MsgID"));
					map.put("type", object2.getString("F_MsgType"));
					map.put("content", object2.getString("F_Content"));
					map.put("issuetime", object2.getString("F_SubmitTime"));
					map.put("read", object2.getString("F_Read"));
					map.put("formid", object2.getString("F_FormAccountID"));
					map.put("toid", object2.getString("F_ToAccountID"));
					map.put("reid", object2.getString("F_RelationID"));
					map.put("sid", object2.getString("F_SendID"));
					list.add(map);
				}
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return list;
	}
	/**
	 * 咨询列表
	 * @param str
	 * @param key
	 * @return
	 */
	public static List<Map<String, String>> ConsultListjson(String str, String key) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
				list.add(map);
			}else {
				JSONArray array = object.getJSONArray(key);
				int size = array.length();
				JSONObject object2;
				for (int i = 0; i < size; i++) {
					object2 = array.getJSONObject(i);
					map = new HashMap<String, String>();
					map.put("question", object2.getString("F_Question"));
					map.put("qid", object2.getString("F_QuestionID"));
					map.put("sname", object2.getString("F_SeekersName"));
					map.put("sid", object2.getString("F_SeekerID"));
					map.put("qtime", object2.getString("F_QuestionTime"));
					map.put("eid", object2.getString("F_EntiD"));
					map.put("ename", object2.getString("F_CompanyName"));
					map.put("etime", object2.getString("F_ReplyTIme"));
					map.put("reply", object2.getString("F_Reply"));
					list.add(map);
				}
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return list;
	}
	
	/**
	 * 兼职信息
	 * @param str
	 * @param key
	 * @return
	 */
	public static Map<String, String> JobInfojson(String str, String key) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			if (object.getString("Result").equals("0")) {
				map.put("result", "0");
				map.put("msg", object.getString("Msg"));
			}else {
				map.put("result", "1");
				JSONArray array = object.getJSONArray(key);
				JSONObject object2;
				object2 = array.getJSONObject(0);
				map.put("jobid", object2.getString("F_JobID"));
				map.put("entid", object2.getString("F_EntID"));
				map.put("comname", object2.getString("F_CompanyName"));
				map.put("title", object2.getString("F_JobTitle"));
				map.put("js", object2.getString("F_JobSearch"));
				map.put("jsid", object2.getString("F_JobSearchID"));
				map.put("jtid", object2.getString("F_JobTypeID"));
				map.put("jt", object2.getString("F_JobType"));
				map.put("jobtime", object2.getString("F_JobTime"));
				map.put("recruit", object2.getString("F_Recruitment"));
				map.put("wage", object2.getString("F_Wage"));
				map.put("unit", object2.getString("F_WageUnit"));
				map.put("descrip", object2.getString("F_Description"));
				map.put("emai", object2.getString("F_ContactEmail"));
				map.put("linkman", object2.getString("F_ContactPerson"));
				map.put("phone", object2.getString("F_ContactMobile"));
				map.put("telconsult", object2.getString("F_TelConsult"));
				map.put("city", object2.getString("F_City"));
				map.put("addr", object2.getString("F_JobAddr"));
				map.put("x", object2.getString("F_X"));
				map.put("y", object2.getString("F_Y"));
				map.put("subtime", object2.getString("F_SubmitTime"));
				map.put("issuetime", object2.getString("F_ReleaseTime"));
				map.put("status", object2.getString("F_Status"));
				map.put("auth", object2.getString("F_Authentication"));
				map.put("statusdisplay", object2.getString("F_StatusDisplay"));
			}
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return map;
	}
	
	/**
	 * 统一解析，判断执行是否成功
	 * @param str
	 * @param key
	 * @return
	 */
	public static Map<String, String> isSuccessfuljson(String str) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			JSONObject object = new JSONObject(str);
			map.put("result", object.getString("Result"));
			map.put("msg", object.getString("Msg"));
		} catch (Exception e) {
			Log.e("err", "error parse the json,the cause is: "
					+ e.getMessage());
			return null;
		}
		return map;
	}
	
}
