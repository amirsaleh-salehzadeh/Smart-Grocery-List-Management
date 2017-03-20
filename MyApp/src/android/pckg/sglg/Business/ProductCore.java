package android.pckg.sglg.Business;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.pckg.sglg.Common.List;
import android.pckg.sglg.Common.ListDetail;
import android.pckg.sglg.Common.PredictionSetting;
import android.pckg.sglg.Common.Product;
import android.pckg.sglg.Common.User;
import android.pckg.sglg.Tools.AMSTools;
import android.pckg.sglg.Tools.NVL;

//------------> http://hmkcode.com/android-simple-sqlite-database-tutorial/
public class ProductCore {

	private static final String TABLE_PRODUCT = "Products";

	// User Table Columns names
	private static final String KEY_ID = "pid";
	private static final String KEY_NAME = "productname";
	private static final String KEY_IMAGE = "image";
	private SQLiteDatabase db;
	private SGLGSQLiteHelper dbHelper;
	private static final String[] COLUMNS = { KEY_ID, KEY_NAME, KEY_IMAGE, "confidence", "interval" };
	private static Context context;

	public ProductCore(Context ctx) {
		dbHelper = new SGLGSQLiteHelper(ctx);
		context = ctx;
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
		db.close();
	}

	public void insertProduct(Product p) {
		open();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, p.getProductName());
		values.put(KEY_ID, p.getPid());
		values.put(KEY_IMAGE, p.getImg());

		db.insert(TABLE_PRODUCT, null, values);
		close();
	}

	public Product getProduct(int pid) {
		open();
		Cursor cursor = db.query(TABLE_PRODUCT, COLUMNS, " pid = ?",
				new String[] { String.valueOf(pid) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		Product p = new Product();
		if (cursor.getCount() > 0) {
			p.setPid(Integer.parseInt(cursor.getString(0)));
			p.setProductName(cursor.getString(1));
			p.setImg(cursor.getString(2));
			p.setConfidence(cursor.getDouble(3));
			p.setInterval(cursor.getInt(4));
		}
		cursor.close();
		close();
		return p;
	}

	public ArrayList<HashMap<String, String>> searchProduct(String keyword,
			String listIdTxt, String filters) {
		ArrayList<HashMap<String, String>> lists = new ArrayList<HashMap<String, String>>();
		String selectQuery = "SELECT * FROM products where productname like '%"
				+ keyword + "%' ";
		if (filters != "")
			selectQuery += "and pid not in (" + filters + ")";
		selectQuery += " order by productname";
		open();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor != null)
			cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			if (cursor.moveToFirst())
				do {
					HashMap<String, String> ent = new HashMap<String, String>();
					ent.put("pname",
							cursor.getString(cursor.getColumnIndex(KEY_NAME))
									+ "");
					ent.put("pid", cursor.getInt(cursor.getColumnIndex(KEY_ID))
							+ "");
					ent.put("thumb_url",
							AMSTools.GetServerIP()
									+ "LPIMWS/"
									+ cursor.getString(cursor
											.getColumnIndex(KEY_IMAGE)));
					ent.put("listid", listIdTxt);
					lists.add(ent);
				} while (cursor.moveToNext());
		}
		return lists;
	}

	public ArrayList<String> getAllProductsInTransactions() {
		ArrayList<String> res = new ArrayList<String>();
		String selectQuery = "SELECT pid FROM products where pid in (select distinct pid from listdetail) ";
		open();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor != null)
			cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			if (cursor.moveToFirst())
				do {
					res.add(cursor.getString(cursor.getColumnIndex(KEY_ID)));
				} while (cursor.moveToNext());
		}
		return res;
	}

	public void deleteProducts() {
		open();
		db.execSQL("delete from " + TABLE_PRODUCT);
		close();
	}

	public void updateAllProductsIntervals() {
		ProductCore pcore = new ProductCore(context);
		ArrayList<String> allProds = pcore.getAllProductsInTransactions();
		String sql = "";
		for (int i = 0; i < allProds.size(); i++) {
			sql = " update products set interval = (select sum(cnt)/(count(cnt)-1) from (select strftime('%J',q.createdate) "//it was %j before
					+ " - strftime('%J',coalesce((select r.createdate from listheader r "
					+ " inner join listdetail rld on rld.listid = r.listid"
					+ " where rld.pid = qld.pid "
					+ " and r.createdate < q.createdate "
					+ " order by r.createdate DESC limit 1), "
					+ " q.createdate)) cnt FROM listheader q "
					+ " inner join listdetail qld on qld.listid = q.listid"
					+ " WHERE qld.pid ='"
					+ allProds.get(i)
					+ "' )) where manualInterval = 0 and pid = "
					+ allProds.get(i);
			open();
			db.execSQL(sql);
			close();
		}

	}

	public void updateProductsConfidence() {
		String sql = "update products set confidence = "
				+ "(select ( ( (julianday('now') - julianday(lh.createdate) ) * 100 )"
				+ " / interval )  from listheader lh"
				+ " inner join listdetail ld on ld.listid = lh.listid "
				+ " where ld.pid = pid order by lh.listid desc limit 1)";
		open();
		db.execSQL(sql);
		close();
	}

	public String getAvailableProducts() {
		ListCore core = new ListCore(context);
		String sql = "select group_concat(pid, ',') pids from products ";
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

	public boolean getProductVisibility(int pid) {
		open();
		Cursor cursor = db.rawQuery(
				"SELECT visibility FROM products where pid = " + pid, null);
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
	
	public boolean getProductManualIntervalMode(int pid) {
		open();
		Cursor cursor = db.rawQuery(
				"SELECT visibility FROM manualInterval where pid = " + pid, null);
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

	public boolean getProductManualInterval(int pid) {
		open();
		Cursor cursor = db.rawQuery(
				"SELECT manualinterval FROM products where pid = " + pid, null);
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

	public void updateProductVisibility(int pid, int i) {
		String query = "";
		query = "UPDATE " + TABLE_PRODUCT + " SET visibility = " + i
				+ " where pid = ";
		query += pid;
		open();
		db.execSQL(query); // selection
		close();
	}

	public void updateProductIntervalMode(int pid, int val) {
		String query = "";
		query = "UPDATE " + TABLE_PRODUCT + " SET manualInterval = " + val
				+ " where pid = ";
		query += pid;
		open();
		Cursor cursor = db.rawQuery(query, null); // selection
		if (cursor != null)
			cursor.moveToFirst();
		close();
	}

	public void updateProductInterval(int pid, int interval) {
		String query = "";
		query = "UPDATE " + TABLE_PRODUCT + " SET interval = " + interval
				+ " where pid = " + pid;
		open();
		Cursor cursor = db.rawQuery(query, null); // selection
		if (cursor != null)
			cursor.moveToFirst();
		close();
	}
}
