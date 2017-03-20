package common.list;

import java.sql.Date;
import java.util.ArrayList;

public class ListENT {

	int listid;
	Date date;
	int uid;
	ArrayList<ListDetailENT> details;

	public int getListid() {
		return listid;
	}

	public void setListid(int listid) {
		this.listid = listid;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public ArrayList<ListDetailENT> getDetails() {
		return details;
	}

	public void setDetails(ArrayList<ListDetailENT> details) {
		this.details = details;
	}

}
