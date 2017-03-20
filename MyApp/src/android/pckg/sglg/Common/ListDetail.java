package android.pckg.sglg.Common;

public class ListDetail {
	int listId;
	int listDetailId;
	int pid;
	String pName;
	String img;
	int inBasket;

	public int getInBasket() {
		return inBasket;
	}

	public void setInBasket(int inBasket) {
		this.inBasket = inBasket;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

	public int getListDetailId() {
		return listDetailId;
	}

	public void setListDetailId(int listDetailId) {
		this.listDetailId = listDetailId;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
	}

}
