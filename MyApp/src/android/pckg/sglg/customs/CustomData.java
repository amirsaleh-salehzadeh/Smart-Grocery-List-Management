package android.pckg.sglg.customs;

/**
 * This is just a simple class for holding data that is used to render our
 * custom view
 */
public class CustomData {
	private int mBackgroundColor;
	private String pName, pId, listId, Image;

	public CustomData(int backgroundColor, String pname, String pid,
			String listid, String image) {
		mBackgroundColor = backgroundColor;
		pName = pname;
		pId = pid;
		listId = listid;
		Image = image;
	}

	public int getBackgroundColor() {
		return mBackgroundColor;
	}

	public String getPname() {
		return pName;
	}
	
	public String getImage() {
		return Image;
	}
	
	public String getListId() {
		return listId;
	}
	public String getPid() {
		return pId;
	}
}
