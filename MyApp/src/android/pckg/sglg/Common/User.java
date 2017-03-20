package android.pckg.sglg.Common;

public class User {
	int uid;
	int webuid;
	String nickName;
	String tel = "";
	String dob;
	int gender; // Male 1, Female 0

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public int getWebuid() {
		return webuid;
	}

	public void setWebuid(int webuid) {
		this.webuid = webuid;
	}

}
