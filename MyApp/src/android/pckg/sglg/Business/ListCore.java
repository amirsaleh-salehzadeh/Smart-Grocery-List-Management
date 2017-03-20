package android.pckg.sglg.Business;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.pckg.sglg.Common.List;
import android.pckg.sglg.Common.ListDetail;
import android.pckg.sglg.Common.PredictionSetting;
import android.pckg.sglg.Common.User;
import android.pckg.sglg.Tools.AMSTools;
import android.pckg.sglg.Tools.NVL;

//------------> http://hmkcode.com/android-simple-sqlite-database-tutorial/
public class ListCore {

	private static final String TABLE_LIST = "ListHeader";
	private static final String TABLE_LIST_DETAILS = "ListDetail";

	// User Table Columns names
	private static final String KEY_LIST_ID = "listid";
	private static final String KEY_LIST_DETAIL_ID = "listdetailid";
	private static final String KEY_PID = "pid";
	private SQLiteDatabase db;
	private SGLGSQLiteHelper dbHelper;

	public ListCore(Context context) {
		dbHelper = new SGLGSQLiteHelper(context);
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
		db.close();
	}

	public void insertItem(int listid, int pid) {
		String selectQuery = "INSERT INTO " + TABLE_LIST_DETAILS
				+ " (listid, pid) SELECT " + listid + ", " + pid
				+ " WHERE NOT EXISTS(SELECT * FROM " + TABLE_LIST_DETAILS
				+ " WHERE listid = " + listid + " and pid = " + pid + " ); "
				+ "update listheader set createdate = '"
				+ AMSTools.getTodayDate() + "' where listid = " + listid;
		open();
		db.execSQL(selectQuery);
		close();

	}
	
	public String getProductsInLastList() {
		String sql = "select group_concat(pid, ',') pids from listdetail "
				+ " where listid = ( select max(listid) from listheader)";
		open();
		String res = "";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null)
			cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			if (cursor.moveToFirst())
				if (cursor.getString(cursor.getColumnIndex("pids")) != null)
					res = cursor.getString(cursor.getColumnIndex("pids"));
		}
		return res;
	}
	
	public String getFilteredProductsInLastList() {
		this.getProductsInLastList();
		String sql = "select group_concat(p.pid, ',') pids from products p " + 
				"left join listdetail l on p.pid = l.pid where " +
				"l.listid = ( select max(listid) from listheader) or p.visibility = 0 ";
		open(); 
		String res = "";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null)
			cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			if (cursor.moveToFirst())
				if (cursor.getString(cursor.getColumnIndex("pids")) != null)
					res = cursor.getString(cursor.getColumnIndex("pids"));
		}
		return res;
	}

	public List getLastList() {
		// Cursor cursor = db.query(TABLE_LIST, COLUMNS_LIST, null, null, null,
		// null, "listid DESC", "1");
		String selectQuery = "SELECT * FROM ListHeader ORDER BY listid DESC LIMIT 1";//
		open();
		Cursor cursor = db.rawQuery(selectQuery, null);
		List list = new List();
		ArrayList<ListDetail> listDetails = new ArrayList<ListDetail>();

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() > 0) {
			list.setListId(Integer.parseInt(cursor.getString(0)));
			list.setDate(cursor.getString(1));
			if (cursor.moveToFirst()) {
				do {
					selectQuery = "SELECT ld.*, p.productname as pname, p.image as img FROM ListDetail ld "
							+ "left join products p on p.pid = ld.pid where ld.listid = "
							+ list.getListId() + " ORDER BY ld.inbasket, ld.listdetailid desc";
					open();
					Cursor cursor2 = db.rawQuery(selectQuery, null);
					if (cursor2.getCount() > 0) 
						if (cursor2.moveToFirst()) 
							while (cursor2.isAfterLast() == false) {
								ListDetail detail = new ListDetail();
								detail.setListDetailId(cursor2.getInt(cursor2
										.getColumnIndex(KEY_LIST_DETAIL_ID)));
								detail.setListId(cursor2.getInt(cursor2
										.getColumnIndex(KEY_LIST_ID)));
								detail.setPid(cursor2.getInt(cursor2
										.getColumnIndex(KEY_PID)));
								detail.setInBasket(cursor2.getInt(cursor2
										.getColumnIndex("inbasket")));
								detail.setpName(cursor2.getString(cursor2
										.getColumnIndex("pname")));
								detail.setImg(cursor2.getString(cursor2
										.getColumnIndex("img")));
								listDetails.add(detail);
								cursor2.moveToNext();
							}
					close();
				} while (cursor.moveToNext());
			}
		}
		list.setListDetails(listDetails);
		close();
		return list;
	}

	public ArrayList<List> getAllLists() {
		// Cursor cursor = db.query(TABLE_LIST, COLUMNS_LIST, null, null, null,
		// null, "listid DESC", "1");
		String selectQuery = "SELECT * FROM ListHeader ORDER BY listid DESC";//
		open();
		Cursor cursor = db.rawQuery(selectQuery, null);
		ArrayList<List> al = new ArrayList<List>();
		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() > 0) {

			if (cursor.moveToFirst()) {
				do {
					List list = new List();
					ArrayList<ListDetail> listDetails = new ArrayList<ListDetail>();
					list.setListId(Integer.parseInt(cursor.getString(0)));
					list.setDate(cursor.getString(1));
					selectQuery = "SELECT ld.*, p.productname as pname, p.image as img FROM ListDetail ld "
							+ "left join products p on p.pid = ld.pid where listid = "
							+ list.getListId();// ORDER BY listid DESC LIMIT 1
					open();
					Cursor cursor2 = db.rawQuery(selectQuery, null);
					if (cursor2.moveToFirst()) {
						while (cursor2.isAfterLast() == false) {
							ListDetail detail = new ListDetail();
							detail.setListDetailId(cursor2.getInt(cursor2
									.getColumnIndex(KEY_LIST_DETAIL_ID)));
							detail.setListId(cursor2.getInt(cursor2
									.getColumnIndex(KEY_LIST_ID)));
							detail.setPid(cursor2.getInt(cursor2
									.getColumnIndex(KEY_PID)));
							detail.setpName(cursor2.getString(cursor2
									.getColumnIndex("pname")));
							detail.setImg(cursor2.getString(cursor2
									.getColumnIndex("img")));
							// Adding contact to list

							listDetails.add(detail);
							cursor2.moveToNext();
						}
					}
					list.setListDetails(listDetails);
					al.add(list);
					close();
				} while (cursor.moveToNext());
			}
		}
		close();
		return al;
	}

	public static ArrayList<HashMap<String, String>> convertListIntoHMap(
			List plist) {
		ArrayList<HashMap<String, String>> HMList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < plist.getListDetails().size(); i++) {
			HashMap<String, String> listDetail = new HashMap<String, String>();
			listDetail.put("ListDetailId", plist.getListDetails().get(i)
					.getListDetailId()
					+ "");
			listDetail.put("ListId", plist.getListDetails().get(i).getListId()
					+ "");
			listDetail.put("pid", plist.getListDetails().get(i).getPid() + "");
			listDetail.put("pname", plist.getListDetails().get(i).getpName());
			listDetail.put("thumb_url", plist.getListDetails().get(i).getImg());
			HMList.add(listDetail);
		}
		return HMList;
	}

	public List getSuggestionLastList() {
		String selectQuery = "SELECT * FROM ListHeader ORDER BY listid DESC LIMIT 1";//
		open();
		Cursor cursor = db.rawQuery(selectQuery, null);
		List list = new List();
		ArrayList<ListDetail> listDetails = new ArrayList<ListDetail>();

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() > 0) {
			int listid = Integer.parseInt(cursor.getString(0));
			list.setListId(listid);
			list.setDate(cursor.getString(1));
			if (cursor.moveToFirst()) {
				do {

					selectQuery = "SELECT * FROM products ";
					cursor = db.rawQuery(selectQuery, null);
					if (cursor.getCount() > 0) {
						if (cursor.moveToFirst()) {
							while (cursor.isAfterLast() == false) {
								ListDetail detail = new ListDetail();
								detail.setListDetailId(0);
								detail.setListId(listid);
								detail.setPid(cursor.getInt(cursor
										.getColumnIndex(KEY_PID)));
								detail.setpName(cursor.getString(cursor
										.getColumnIndex("productname")));
								// Adding contact to list

								listDetails.add(detail);
								cursor.moveToNext();
							}
						}

					}
				} while (cursor.moveToNext());
			}
		}
		list.setListDetails(listDetails);
		close();
		return list;
	}

	public String[] getAllTransactions() {
		String sql = "select listid, (select group_concat(pid, ',') from "
				+ " listdetail ld2 where ld1.listid = ld2.listid group by ld2.listid)"
				+ "  from listdetail ld1 group by ld1.listid ";
		open();
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null)
			cursor.moveToFirst();
		String[] res = new String[cursor.getCount()];
		int i = 0;
		if (cursor.getCount() > 0) {
			do {
				res[i] = cursor.getString(0);
				res[i] = AMSTools.SortMyString(cursor.getString(1));
				i++;
			} while (cursor.moveToNext());
		}
		close();
		return res;
	}

	public void newlist() {
		 this.mergeList();
		// String sql = "update listdetail set listid = "
		// +
		// "(select min(lh1.listid) from listheader lh1 group by lh1.date having count(*) > 1) "
		// + " where listid = "
		// +
		// "(select max(lh2.listid) from listheader lh2 group by lh2.date having count(*) > 1) and "
		// +
		// "NOT EXISTS(SELECT * FROM  listdetail ld2 WHERE ld2.listid = (select min(lh3.listid) "
		// +
		// " from listheader lh3 group by lh3.date having count(*) > 1)  and ld2.pid =  pid );"
		// + "delete from listdetail where listid = " +
		// "(select max(lh2.listid) from listheader lh2 group by lh2.date having count(*) > 1); ";

		// String sql = "INSERT INTO " + TABLE_LIST + " (createdate) SELECT "
		// + AMSTools.getTodayDate();
		open();
		ContentValues values = new ContentValues();
		values.put("createdate", AMSTools.getTodayDate());

		db.insert(TABLE_LIST, null, values);
		close();
		// sql = "INSERT INTO " + TABLE_LIST + " DEFAULT VALUES ";
		// db.insert(TABLE_LIST, null, values);
		close();
	}

	private void mergeList() {
		int maxlistid, minlistid = 0;
		String sql = "select min(listid) listid, count(*) from listheader "
				+ " group by createdate having count(*) > 1";
		open();
		Cursor cursor1 = db.rawQuery(sql, null);
		if (cursor1 != null)
			cursor1.moveToFirst();
		int i = 0;

		if (cursor1.getCount() > 0)
			minlistid = cursor1.getInt(cursor1.getColumnIndex("listid"));
		sql = "select max(listid) listid from listheader "
				+ " group by createdate having count(*) > 1";
		// String sql = "select * from listheader";
		open();
		Cursor cursor2 = db.rawQuery(sql, null);
		if (cursor2 != null)
			cursor2.moveToFirst();
		if (cursor2.getCount() > 0) {
			do {

				maxlistid = cursor2.getInt(cursor2.getColumnIndex("listid"));
				close();
				open();
				sql = "select pid from listdetail where listid = " + maxlistid;
				Cursor cursor3 = db.rawQuery(sql, null);
				if (cursor3 != null)
					cursor3.moveToFirst();
				do {
					this.insertItem(minlistid, NVL.getInt(cursor3.getInt(0)));
					i++;
				} while (cursor3.moveToNext());
				sql = "delete from listdetail where listid = " + maxlistid;
				close();
				open();
				db.execSQL(sql);
				sql = "delete from listheader where listid = " + maxlistid;
				close();
				open();
				db.execSQL(sql);
				cursor3.close();
				i++;
			} while (cursor2.moveToNext());
		}
		cursor1.close();
		cursor2.close();

		close();
	}

	public void updateDate() {
		open();
		db.execSQL("update "
				+ TABLE_LIST
				+ " set createdate = '"
				+ AMSTools.getTodayDate()
				+ "' where listid = (SELECT listid FROM ListHeader ORDER BY listid DESC LIMIT 1)");
		close();
	}

	public void deleteFromList(int listDetailId) {
		open();
		db.execSQL("delete from " + TABLE_LIST_DETAILS
				+ " where listdetailid = " + listDetailId);
		close();
	}

	public boolean getInBasket(int listdetailid) {
		open();
		Cursor cursor = db.rawQuery(
				"SELECT inbasket FROM listdetail where listdetailid = "
						+ listdetailid, null);
		int i = 0;
		// 3. if we got results get the first one
		if (cursor != null)
			cursor.moveToFirst();

		// 4. build book object
		PredictionSetting PS = new PredictionSetting();
		if (cursor.getCount() > 0) {
			i = cursor.getInt(0);
		}
		close();
		if (i == 1)
			return true;
		else
			return false;
		// 5. return book
	}

	public void inBasketUpdate(int listdetailid) {
		String query = "";
		if (this.getInBasket(listdetailid))
			query = "UPDATE " + TABLE_LIST_DETAILS + " SET inbasket = 0"
					+ " where listdetailid = ";
		else
			query = "UPDATE " + TABLE_LIST_DETAILS + " SET inbasket = 1"
					+ " where listdetailid = ";
		query += listdetailid;
		open();
		db.execSQL(query); // selection
		close();
	}

	public List getLastListFiltered() {
		String selectQuery = "SELECT * FROM ListHeader ORDER BY listid DESC LIMIT 1";//
		open();
		Cursor cursor = db.rawQuery(selectQuery, null);
		List list = new List();
		ArrayList<ListDetail> listDetails = new ArrayList<ListDetail>();

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() > 0) {
			list.setListId(Integer.parseInt(cursor.getString(0)));
			list.setDate(cursor.getString(1));
			if (cursor.moveToFirst()) {
				do {
					selectQuery = "SELECT ld.*, p.productname as pname, p.image as img FROM ListDetail ld "
							+ "left join products p on p.pid = ld.pid where ld.listid = "
							+ list.getListId() + " ORDER BY ld.inbasket, ld.listdetailid desc";
					open();
					Cursor cursor2 = db.rawQuery(selectQuery, null);
					if (cursor2.getCount() > 0) 
						if (cursor2.moveToFirst()) 
							while (cursor2.isAfterLast() == false) {
								ListDetail detail = new ListDetail();
								detail.setListDetailId(cursor2.getInt(cursor2
										.getColumnIndex(KEY_LIST_DETAIL_ID)));
								detail.setListId(cursor2.getInt(cursor2
										.getColumnIndex(KEY_LIST_ID)));
								detail.setPid(cursor2.getInt(cursor2
										.getColumnIndex(KEY_PID)));
								detail.setInBasket(cursor2.getInt(cursor2
										.getColumnIndex("inbasket")));
								detail.setpName(cursor2.getString(cursor2
										.getColumnIndex("pname")));
								detail.setImg(cursor2.getString(cursor2
										.getColumnIndex("img")));
								listDetails.add(detail);
								cursor2.moveToNext();
							}
					close();
				} while (cursor.moveToNext());
			}
		}
		list.setListDetails(listDetails);
		close();
		return list;
	}
}
