package android.pckg.sglg.Common;

import java.util.ArrayList;


public class List {
	int listId;
	String date;
	ArrayList<ListDetail> listDetails = new ArrayList<ListDetail>();

	public ArrayList<ListDetail> getListDetails() {
		return listDetails;
	}

	public void setListDetails(ArrayList<ListDetail> listDetails) {
		this.listDetails = listDetails;
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
