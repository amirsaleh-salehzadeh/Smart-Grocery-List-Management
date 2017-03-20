package android.pckg.sglg.Business;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.pckg.sglg.Common.User;
import android.pckg.sglg.Tools.AMSTools;
import android.pckg.sglg.Tools.NVL;

//------------> http://hmkcode.com/android-simple-sqlite-database-tutorial/
public class UsersCore {

	private static final String TABLE_USERS = "Users";

	// User Table Columns names
	private static final String KEY_ID = "uid";
	private static final String KEY_NICKNAME = "nickname";
	private static final String KEY_GENDER = "gender";
	private static final String KEY_TEL = "tel";
	private static final String KEY_DOB = "dob";
	private static final String KEY_WEB_UID = "webuid";
	private SQLiteDatabase db;
	private SGLGSQLiteHelper dbHelper;
	private static final String[] COLUMNS = { KEY_ID, KEY_NICKNAME, KEY_TEL,
			KEY_GENDER, KEY_DOB, KEY_WEB_UID };

	public UsersCore(Context context) {
		dbHelper = new SGLGSQLiteHelper(context);
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
		db.close();
	}

	public void insertUser(User user) {
		ContentValues values = new ContentValues();
		values.put(KEY_NICKNAME, user.getNickName());
		values.put(KEY_GENDER, user.getGender());
		values.put(KEY_TEL, user.getTel());
		values.put(KEY_DOB, user.getDob());
		values.put(KEY_WEB_UID, user.getWebuid());
		db.insert(TABLE_USERS, null, values);
		close();
	}

	public User getUser(int id) {
		open();
   		Cursor cursor = db.query(TABLE_USERS, COLUMNS, " uid = ?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		User user = new User();
		if (cursor.getCount() > 0) {
			user.setUid(Integer.parseInt(cursor.getString(0)));
			user.setNickName(cursor.getString(1));
			user.setTel(cursor.getString(2));
			user.setGender(Integer.parseInt(cursor.getString(3)));
			user.setDob(cursor.getString(4));
			user.setWebuid(NVL.getInt(cursor.getString(5)));
		}
		close();
		return user;
	}

	public int updateUser(User user) {
		ContentValues values = new ContentValues();
		values.put("nickname", user.getNickName());
		values.put("dob", user.getDob());
		values.put("tel", user.getTel());
		values.put("gender", user.getGender());
		values.put("webuid", user.getWebuid());
		open();
		int i = db.update(TABLE_USERS, // table
				values, // column/value
				KEY_ID + " = ?", // selections
				new String[] { String.valueOf(user.getUid()) }); // selection
		close();
		return i;
	}

	public void deleteUser(User user) {
		open();
		db.delete(TABLE_USERS, KEY_ID + " = ?",
				new String[] { String.valueOf(user.getUid()) }); // selections
		close();
	}
}
