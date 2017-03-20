package android.pckg.sglg.Business;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.pckg.sglg.Tools.AMSTools;

public class SGLGSQLiteHelper extends SQLiteOpenHelper {

	// Database Version
	public static final int DATABASE_VERSION = AMSTools.getDatabaseVersion();
	// Database Name
	private static final String DATABASE_NAME = AMSTools.getDatabaseName();

	public SGLGSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS Users ( "
				+ "uid INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " + "nickname TEXT, "
				+ "tel TEXT, " + "gender INTEGER, " + "webuid INTEGER, "
				+ "dob TEXT )";

		String CREATE_PRODUCT_TABLE = "CREATE TABLE IF NOT EXISTS Products ( "
				+ "pid INTEGER PRIMARY KEY, " + "image TEXT, "+ "confidence FLOAT , "
				+ "productname TEXT NOT NULL UNIQUE," + " interval INTEGER " + ", visibility integer default 1, manualInterval integer default 0 )";

		String CREATE_ASSOCIATION_PRODUCTS_TABLE = "CREATE TABLE IF NOT EXISTS association_products ( "
				+ "apid INTEGER PRIMARY KEY, " 
				+ "products TEXT NOT NULL UNIQUE)";

		String CREATE_ASSOCIATION_RULES_TABLE = "CREATE TABLE IF NOT EXISTS association_rules ( "
				+ "arid_1 INTEGER , "
				+ "confidence FLOAT , "
				+ "arid_2 INTEGER , PRIMARY KEY (arid_1, arid_2) )";

		String CREATE_LIST_TABLE = "CREATE TABLE IF NOT EXISTS ListHeader ( "
				+ "listid INTEGER PRIMARY KEY AUTOINCREMENT, " + "createdate DATETIME )";

		String CREATE_LIST_DETAIL_TABLE = "CREATE TABLE IF NOT EXISTS ListDetail ( "
				+ "listdetailid INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "listid INTEGER, " + "pid TEXT, " + "inbasket INTEGER DEFAULT 0 )";

		String CREATE_PREDICTION_SETTING_TABLE = "CREATE TABLE IF NOT EXISTS Prediction_setting ( "
				+ "psid INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "support INTEGER, "
				+ "confidence INTEGER )";

		String CREATE_WEBSERVICE_TABLE = "CREATE TABLE IF NOT EXISTS webservice_setting ( "
				+ "wsid INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "status INTEGER ) ";

		String CREATE_IMAGE_VIEW_TABLE = "CREATE TABLE IF NOT EXISTS ImageView ( "
				+ "ivid INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "status INTEGER ) ";
		
		String CREATE_INDICATOR_VIEW_TABLE = "CREATE TABLE IF NOT EXISTS IndicatorView ( "
				+ "ivid INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "status INTEGER ) ";
		ContentValues values = new ContentValues();
		db.execSQL(CREATE_WEBSERVICE_TABLE);
		values.put("status", 1);
		db.insert("webservice_setting", null, values);

		db.execSQL(CREATE_PREDICTION_SETTING_TABLE);
		values = new ContentValues();
		values.put("support", 20);
		values.put("confidence", 70);
//		values.put("mean", 100);

		db.insert("Prediction_setting", null, values);

		db.execSQL(CREATE_USER_TABLE);
		db.execSQL(CREATE_ASSOCIATION_PRODUCTS_TABLE);
		db.execSQL(CREATE_ASSOCIATION_RULES_TABLE);
		db.execSQL(CREATE_PRODUCT_TABLE);
		db.execSQL(CREATE_LIST_TABLE);
		values = new ContentValues();
		values.put("createdate", AMSTools.getTodayDate());
//		values.put("createdate","2014-10-04 00:00:00");
		db.insert("ListHeader", null, values);
//		values.put("createdate", "2014-10-22 00:00:00");
//		db.insert("ListHeader", null, values);
		
//		values.put("date", "2014-09-07 00:00:00");
//		db.insert("ListHeader", null, values);
//		values.put("date", "2014-09-09 00:00:00");
//		db.insert("ListHeader", null, values);
		
//		values = new ContentValues();
		db.execSQL(CREATE_LIST_DETAIL_TABLE);
//		values.put("listid", 1);
//		values.put("pid", 1+"");
//		db.insert("ListDetail", null, values);
//		values.put("listid", 2);
//		values.put("pid",1+"");
//		db.insert("ListDetail", null, values);
//		values.put("listid", 3);
//		values.put("pid", 1+"");
//		db.insert("ListDetail", null, values);
		
//		values.put("listid", 4);
//		values.put("pid", 1+"");
//		db.insert("ListDetail", null, values);
		
		db.execSQL(CREATE_IMAGE_VIEW_TABLE);
		values = new ContentValues();
		values.put("status", 1);
		db.insert("ImageView", null, values);
		
		db.execSQL(CREATE_INDICATOR_VIEW_TABLE);
		values = new ContentValues();
		values.put("status", 0	);
		db.insert("IndicatorView", null, values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older books table if existed
		db.execSQL("DROP TABLE IF EXISTS Users");
		db.execSQL("DROP TABLE IF EXISTS Products");
		db.execSQL("DROP TABLE IF EXISTS Product_Interval");
		db.execSQL("DROP TABLE IF EXISTS ListHeader");
		db.execSQL("DROP TABLE IF EXISTS ListDetail");
		db.execSQL("DROP TABLE IF EXISTS webservice_setting");
		db.execSQL("DROP TABLE IF EXISTS Prediction_setting");
		db.execSQL("DROP TABLE IF EXISTS ImageView");
		db.execSQL("DROP TABLE IF EXISTS IndicatorView");
		db.execSQL("DROP TABLE IF EXISTS association_rules");
		db.execSQL("DROP TABLE IF EXISTS association_products");

		// create fresh books table
		this.onCreate(db);
	}

}