package common.users;

import java.sql.Blob;




public class UserENT {
	
	int UID;
	String nickName;
	String tel;
	int gender;
	int localUID;
	String DOB;
	public int getUID() {
		return UID;
	}
	public void setUID(int uID) {
		UID = uID;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public int getLocalUID() {
		return localUID;
	}
	public void setLocalUID(int localUID) {
		this.localUID = localUID;
	}
	public String getDOB() {
		return DOB;
	}
	public void setDOB(String dOB) {
		DOB = dOB;
	}


}
