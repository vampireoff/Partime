package com.jianzhiniu.network;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.jianzhiniu.utils.MyUtils;
/**
 * Map�����ࣨ��Щ�����ǹ��õģ�
 * @author Administrator
 *
 */
public class MapUtil {
	private Context context;
	private String accesskey = "";
	
	public MapUtil(Context context){
		this.context = context;
		accesskey = MyUtils.getAccesskey();
	}
	
	/**
	 * ��ͨ��¼
	 * @return
	 */
	public Map<String, Object> LoginMap(String id, String pwd){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("LoginID", id);
		map.put("Pwd", pwd);
		return map;
	}
	/**
	 * �汾���
	 * @return
	 */
	public Map<String, Object> CheckVersionMap(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("type", "android");
		return map;
	}
	/**
	 * ��ȡ��Ա��Ϣ
	 * @return
	 */
	public Map<String, Object> UserInfoMap(String id){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("ID", id);
		return map;
	}
	/**
	 * ��ȡ����������Ա
	 * @return
	 */
	public Map<String, Object> JobApplyersMap(String id, int size, int page){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("jobID", id);
		map.put("pageSize", size);
		map.put("pageIndex", page);
		return map;
	}
	/**
	 * ֪ͨ�б�
	 * @return
	 */
	public Map<String, Object> NoticeMap(int size, int page){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("pageSize", size);
		map.put("pageIndex", page);
		return map;
	}
	/**
	 * ��ְ�����б�
	 * @return
	 */
	public Map<String, Object> JobManageMap(String id, int status, 
			String sid, String tid, String title, int size, int page){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("EntID", id);
		map.put("status", status);
		map.put("JobSearchID", sid);
		map.put("JobTypeID", tid);
		map.put("JobTitle", title);
		map.put("pageSize", size);
		map.put("pageIndex", page);
		return map;
	}
	/**
	 * ��ʷ��ְ�б�
	 * @return
	 */
	public Map<String, Object> HistoryMap(String id, int size, int page){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("EntID", id);
		map.put("status", "");
		map.put("JobSearchID", "");
		map.put("JobTypeID", "");
		map.put("JobTitle", "");
		map.put("pageSize", size);
		map.put("pageIndex", page);
		return map;
	}
	/**
	 * �ҵļ�ְ-�����С����¼�
	 * @return
	 */
	public Map<String, Object> MyJobMap(int ise, String id, int status, int size, int page){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("status", status);
		map.put("pageSize", size);
		map.put("pageIndex", page);
		if (ise == 1) {
			map.put("EntID", id);
		}else {
			map.put("SeekerID", id);
		}
		return map;
	}
	/**
	 * ��Ϣ�б�
	 * @return
	 */
	public Map<String, Object> MessageMap(int size, int page, String id){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("pageSize", size);
		map.put("pageIndex", page);
		map.put("accountID", id);
		return map;
	}
	/**
	 * ������Ϣ
	 * @return
	 */
	public Map<String, Object> JobInfoMap(String id){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("JobID", id);
		return map;
	}
	/**
	 * ��ѯ�б�
	 * @return
	 */
	public Map<String, Object> ConsultListMap(String id, int size, int page){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("jobID", id);
		map.put("pageSize", size);
		map.put("pageIndex", page);
		return map;
	}
	/**
	 * ������ѯ
	 * @return
	 */
	public Map<String, Object> SendConsultMap(String id, String sid, String content){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("JobID", id);
		map.put("SeekerID", sid);
		map.put("Question", content);
		return map;
	}
	/**
	 * ��ְ����
	 * @return
	 */
	public Map<String, Object> ApplyJobMap(String sid, String jid){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("JobID", jid);
		map.put("SeekerID", sid);
		return map;
	}
	/**
	 * ����Ͷ��
	 * @return
	 */
	public Map<String, Object> SendComplainMap(String jid, String sid, String eid, String content, String senderid){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("sComplaintJobID", jid);
		map.put("sComplaintEntID", eid);
		map.put("sComplaintSeekerID", sid);
		map.put("sComplaint", content);
		map.put("sComplaintUserID", senderid);
		return map;
	}
	/**
	 * �������
	 * @return
	 */
	public Map<String, Object> SendFeedbackMap(String email, String sid, String content){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("sEmail", email);
		map.put("sContent", content);
		map.put("sFeedbackUserID", sid);
		return map;
	}
	/**
	 * ��ѯ�ظ�
	 * @return
	 */
	public Map<String, Object> ConsultReplyMap(String QuestionID, String EntID, String Reply){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("QuestionID", QuestionID);
		map.put("EntID", EntID);
		map.put("Reply", Reply);
		return map;
	}
	/**
	 * ��ϢΪ�Ѷ�
	 * @return
	 */
	public Map<String, Object> IsReadedMap(String MsgID){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("MsgID", MsgID);
		return map;
	}
	/**
	 * ��ҵ��Ϣ
	 * @return
	 */
	public Map<String, Object> EntInfoMap(String id){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("ID", id);
		return map;
	}
	/**
	 * ������ҵ��Ϣ
	 * @return
	 */
	public Map<String, Object> AlterEinfoMap(String id, String name, String person, String 
			email, String mobile, String pro){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("EntID", id);
		map.put("CompanyName", name);
		map.put("ContactPerson", person);
		map.put("ContactEmail", email);
		map.put("ContactMobile", mobile);
		map.put("Profile", pro);
		return map;
	}
	/**
	 * �ϴ�ͷ��
	 * @return
	 */
	public Map<String, Object> UploadPicMap(String id, String pic, String type){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("ID", id);
		map.put("img", pic);
		map.put("Type", type);
		return map;
	}
	/**
	 * ����1���¼�2
	 * @return
	 */
	public Map<String, Object> ZMXJMap(String id, int status){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("Job", id);
		map.put("Status", status);
		return map;
	}
	/**
	 * ¼��3���ܾ�2
	 * @return
	 */
	public Map<String, Object> HireRefuseMap(String id, int status){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("RegID", id);
		map.put("Status", status);
		return map;
	}
	/**
	 * ���ĸ�����Ϣ
	 * @return
	 */
	public Map<String, Object> AlterPinfoMap(String id, String name, String age, String 
			email, String mobile, String birth, String height, String school, String pro, 
			String exp, String city, String cap, String area, String free, String sid, 
			String s, String sex)
			{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("SeekerID", id);
		map.put("Name", name);
		map.put("Mobile", mobile);
		map.put("Email", email);
		map.put("Birthday", birth);
		map.put("Age", age);
		map.put("Height", height);
		map.put("School", school);
		map.put("Profile", pro);
		map.put("Experience", exp);
		map.put("City", city);
		map.put("Capital", cap);
		map.put("Area", area);
		map.put("FreeTime", free);
		map.put("JobSearchID", sid);
		map.put("JobSearch", s);
		map.put("Sex", sex);
		return map;
	}
	
	/**
	 * ��������¼
	 * @return
	 */
	public Map<String, Object> TLoginMap(String id, String name, String pic, String type){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("LoginID", id);
		map.put("Name", name == null ? "" : name);
		map.put("picPath", pic == null ? "" : pic);
		map.put("Type", type);
		return map;
	}
	
	/**
	 * ��ְ��ע��
	 * @return
	 */
	public Map<String, Object> SeekerRegisterMap(String id, String pwd){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("LoginID", id);
		map.put("Pwd", pwd);
		return map;
	}
	/**
	 * ��֤�ֻ����Ƿ���ע��
	 * @return
	 */
	public Map<String, Object> VerifyphoneMap(String num){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("Mobile", num);
		return map;
	}
	/**
	 * ֻ�跢����֤��
	 * @return
	 */
	public Map<String, Object> AccesskeyMap(){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		return map;
	}
	/**
	 * �޸�����
	 * @return
	 */
	public Map<String, Object> AlterpwdMap(String idString, String pwd, String 
			type){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("ID", idString);
		map.put("Pwd", pwd);
		map.put("Type", type);
		return map;
	}
	/**
	 * ���¼�ְ
	 * @return
	 */
	public Map<String, Object> NewJobMap(String JobSearchID, String JobTypeID, String 
			JobTitle, String city, int pageSize, int pageIndex){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("JobSearchID", JobSearchID);
		map.put("JobTypeID", JobTypeID);
		map.put("JobTitle", JobTitle);
		map.put("city", city);
		map.put("pageSize", pageSize);
		map.put("pageIndex", pageIndex);
		return map;
	}
	/**
	 * ����Ȥ��ְ
	 * @return
	 */
	public Map<String, Object> InterestJobMap(String JobSearchID, String JobTypeID, String 
			JobTitle, String city, int pageSize, int pageIndex){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("JobSearchIDList", JobSearchID);
		map.put("JobTypeIDList", JobTypeID);
		map.put("JobTitle", JobTitle);
		map.put("city", city);
		map.put("pageSize", pageSize);
		map.put("pageIndex", pageIndex);
		return map;
	}
	/**
	 * �ҵ�����
	 * @return
	 */
	public Map<String, Object> MyApplyMap(String id, int pageSize, int pageIndex){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("SeekerID", id);
		map.put("pageSize", pageSize);
		map.put("pageIndex", pageIndex);
		return map;
	}
	/**
	 * �������١���ȡ����������Ϣ
	 * @return
	 */
	public Map<String, Object> ApplyFollowMap(String id){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("RegID", id);
		return map;
	}
	/**
	 * ȡ������
	 * @return
	 */
	public Map<String, Object> CancleApplyMap(String id, String sta){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("RegID", id);
		map.put("Status", sta);
		return map;
	}
	/**
	 * ������ְ
	 * @return
	 */
	public Map<String, Object> NearbyJobMap(String JobSearchID, String JobTypeID, String 
			JobTitle, double MyX, double MyY, double Distance, int pageSize, int pageIndex){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("JobSearchID", JobSearchID);
		map.put("JobTypeID", JobTypeID);
		map.put("JobTitle", JobTitle);
		map.put("MyX", String.valueOf(MyX));
		map.put("MyY", String.valueOf(MyY));
		map.put("Distance", String.valueOf(Distance));
		map.put("pageSize", pageSize);
		map.put("pageIndex", pageIndex);
		return map;
	}
	/**
	 * ������ְ
	 * @return
	 */
	public Map<String, Object> IssueJobMap(String entid, String title, String jtid, String ttid, 
			String time, String count, String unit, String unitname, String descrip, String email, 
			String person, String phone, String isphone, String city, String addr, String x, String y){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accessKey", accesskey);
		map.put("EntID", entid);
		map.put("JobTitle", title);
		map.put("JobSearchID", jtid);
		map.put("JobTypeID", ttid);
		map.put("JobTime", time);
		map.put("RecruitMent", count);
		map.put("Wage", unit);
		map.put("WageUnit", unitname);
		map.put("Description", descrip);
		map.put("ContactEmail", email);
		map.put("ContactPerson", person);
		map.put("ContactMobile", phone);
		map.put("TelConsult", isphone);
		map.put("City", city);
		map.put("JobAddr", addr);
		map.put("X", x);
		map.put("Y", y);
		return map;
	}
	
}
