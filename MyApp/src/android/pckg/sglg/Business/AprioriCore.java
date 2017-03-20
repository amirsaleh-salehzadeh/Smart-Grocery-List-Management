package android.pckg.sglg.Business;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.pckg.sglg.Algorithms.Apriori.Apriori;
import android.pckg.sglg.Algorithms.Apriori.ResultSet;
import android.pckg.sglg.Common.PredictionSetting;

public class AprioriCore {
	private static final String TABLE_ASSOCIATION_PRODUCTS = "association_products";
	private static final String TABLE_ASSOCIATION_RULES = "association_rules";

	// User Table Columns names
	private SGLGSQLiteHelper dbHelper;
	private Context context;
	private SQLiteDatabase db;

	public AprioriCore(Context ctx) {
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

	public Integer insertAssociation(String products) {
		String selectQuery = "INSERT INTO association_products(products) SELECT '"
				+ products
				+ "' WHERE NOT EXISTS(SELECT 1 FROM association_products WHERE products = '"
				+ products + "')";
		open();
		db.execSQL(selectQuery);
		close();
		return getAssociationId(products);
	}

	private Integer getLastInsertID(String p) {
		String selectQuery = "SELECT apid FROM association_products where products = "
				+ p + "  ORDER BY apid DESC LIMIT 1";//
		open();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor != null)
			cursor.moveToFirst();
		if (Integer.parseInt(cursor.getString(0)) != 0) {
			close();
			return Integer.parseInt(cursor.getString(0));
		} else {
			selectQuery = "SELECT apid FROM association_products ORDER BY apid DESC LIMIT 1";//
			open();
			cursor = db.rawQuery(selectQuery, null);
			if (cursor != null)
				cursor.moveToFirst();
			close();
			return Integer.parseInt(cursor.getString(0));
		}

	}

	public void insertAssociationRule(int arid1, int arid2, Double confidence) {
		String selectQuery = "INSERT INTO association_rules(arid_1,arid_2,confidence) SELECT "
				+ arid1
				+ ", "
				+ arid2
				+ ", "
				+ confidence
				+ " WHERE NOT EXISTS(SELECT 1 FROM association_rules WHERE arid_1 = "
				+ arid1 + " AND arid_2 = " + arid2 + ")";
		open();
		db.execSQL(selectQuery);
		close();
	}

	public void deleteAssociations() {
		open();
		db.execSQL("delete from " + TABLE_ASSOCIATION_PRODUCTS);
		close();
		open();
		db.execSQL("delete from " + TABLE_ASSOCIATION_RULES);
		close();
	}

	public void calculateAssociationRules() {
		PredictionSetting ps = new PredictionSetting();
		SettingCore score = new SettingCore(context);
		ps = score.getPredictionSettings();
		Apriori ap = new Apriori();
		ProductCore pcore = new ProductCore(context);
		ListCore lcore = new ListCore(context);
		Iterable<String> items = pcore.getAllProductsInTransactions();
		String[] transactions = lcore.getAllTransactions();
		if (transactions.length == 0)
			return;
		ResultSet rs = ap.ProcessTransaction(ps.getSupport() * 0.01,
				ps.getConfidence() * 0.01, items, transactions);
		if (rs.StrongRules.size() > 0) {
			this.deleteAssociations();
			for (int i = 0; i < rs.StrongRules.size(); i++) {
				int first = this.insertAssociation(rs.StrongRules.get(i)
						.getCombination());

				int second = this.insertAssociation(rs.StrongRules.get(i)
						.getRemaining());
				this.insertAssociationRule(first, second, rs.StrongRules.get(i)
						.getConfidence());
			}
		}
	}

	public int getAssociationId(String products) {
		String selectQuery = "SELECT * FROM association_products where products = '"
				+ products + "'";//
		open();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() > 0) {
			close();
			return Integer.parseInt(cursor.getString(0));
		}

		return 0;
	}

}
