package android.pckg.sglg.Business;

import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.pckg.sglg.Common.PredictionSetting;

//------------> http://hmkcode.com/android-simple-sqlite-database-tutorial/
public class SettingCore {

	private static final String TABLE_PREDICTION_SETTING = "Prediction_setting";
	private static final String TABLE_WEBSERVICE_SETTING = "webservice_setting";
	private static final String TABLE_IMAGE_VIEW = "imageview";

	// User Table Columns names
	private static final String KEY_PREDICTION_ID = "psid";
	private static final String KEY_IMAGE_VIEW = "status";
	private static final String KEY_SUPPORT = "support";
	private static final String KEY_Confidence = "confidence";
//	private static final String KEY_Mean = "mean";
	private static final String KEY_WEBSERVICE_ID = "wsid";
	private SQLiteDatabase db;
	private SGLGSQLiteHelper dbHelper;
	private static final String[] COLUMNS_PREDICTION = { KEY_PREDICTION_ID,
			KEY_SUPPORT, KEY_Confidence};//, KEY_Mean };

	public SettingCore(Context context) {
		dbHelper = new SGLGSQLiteHelper(context);
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
		db.close();
	}

	public PredictionSetting getPredictionSettings() {
		open();
		Cursor cursor = db.query(TABLE_PREDICTION_SETTING, COLUMNS_PREDICTION,
				" psid = ?", new String[] { String.valueOf(1) }, null, null,
				null, null);
		
		// 3. if we got results get the first one
		if (cursor != null)
			cursor.moveToFirst();

		// 4. build book object
		PredictionSetting PS = new PredictionSetting();
		if (cursor.getCount() > 0) {
			PS.setSupport(cursor.getInt(1));
			PS.setConfidence(cursor.getInt(2));
//			PS.setMean(cursor.getInt(3));
		}
		// 5. return book
		close();
		cursor.close();
		return PS;
	}

	public boolean getImageview() {
		open();
		Cursor cursor = db.rawQuery("SELECT * FROM ImageView LIMIT 1", null);
		int i = 0;
		// 3. if we got results get the first one
		if (cursor != null)
			cursor.moveToFirst();

		// 4. build book object
		PredictionSetting PS = new PredictionSetting();
		if (cursor.getCount() > 0) {
			i = cursor.getInt(1);
		}
		close();
		cursor.close();
		if (i == 1)
			return true;
		else
			return false;
		// 5. return book
	}
	
	public boolean getIndicatorView() {
		open();
		Cursor cursor = db.rawQuery("SELECT * FROM IndicatorView LIMIT 1", null);
		int i = 0;
		// 3. if we got results get the first one
		if (cursor != null)
			cursor.moveToFirst();

		// 4. build book object
		PredictionSetting PS = new PredictionSetting();
		if (cursor.getCount() > 0) {
			i = cursor.getInt(1);
		}
		close();
		cursor.close();
		if (i == 1)
			return true;
		else
			return false;
		// 5. return book
	}

	public void changeImageview(int i) {
		String query = "UPDATE " + TABLE_IMAGE_VIEW + " SET status = " + i
				+ " where ivid = 1";
		open();
		Cursor cursor = db.rawQuery(query, null); // selection
		if (cursor != null)
			cursor.moveToFirst();
		close();
		cursor.close();
	}
	
	public void changeIndicatorview(int i) {
		String query = "UPDATE indicatorview SET status = " + i
				+ " where ivid = 1";
		open();
		Cursor cursor = db.rawQuery(query, null); // selection
		if (cursor != null)
			cursor.moveToFirst();
		close();
		cursor.close();
	}

	public boolean getWebserviceSettings() {

		String selectQuery = "SELECT * FROM webservice_setting LIMIT 1";//
		open();
		Cursor cursor = db.rawQuery(selectQuery, null);
		int res = 0;
		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() > 0) {
			res = cursor.getInt(1);
		}
		close();
		cursor.close();
		if (res == 1)
			return true;
		else
			return false;
	}

	public void updateWebserviceSetting(int input) {

		String query = "UPDATE " + TABLE_WEBSERVICE_SETTING + " SET status = "
				+ input + " WHERE wsid = 1 ";
		open();
		Cursor cursor = db.rawQuery(query, null); // selection
		if (cursor != null)
			cursor.moveToFirst();
		close();
		cursor.close();

	}

	public int updatePredictionSetting(PredictionSetting PS) {
		ContentValues values = new ContentValues();
		values.put("support", PS.getSupport());
		values.put("confidence", PS.getConfidence());
//		values.put("mean", PS.getMean());
		open();
		int i = db.update(TABLE_PREDICTION_SETTING, // table
				values, // column/value
				KEY_PREDICTION_ID + " = ?", // selections
				new String[] { String.valueOf(1) }); // selection
		close();
		return i;
	}

}
