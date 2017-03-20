package android.pckg.sglg.Algorithms.Merge;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.pckg.sglg.Algorithms.Apriori.Apriori;
import android.pckg.sglg.Algorithms.Apriori.Item;
import android.pckg.sglg.Algorithms.Apriori.Rule;
import android.pckg.sglg.Business.ListCore;
import android.pckg.sglg.Business.ProductCore;
import android.pckg.sglg.Business.SGLGSQLiteHelper;
import android.pckg.sglg.Common.List;
import android.pckg.sglg.Common.ListDetail;
import android.pckg.sglg.Common.Product;
import android.pckg.sglg.Tools.AMSTools;
import android.pckg.sglg.Tools.NVL;
import android.pckg.sglg.customs.CustomData;
import android.pckg.sglg.customs.ProductsListAdapter;

public class GenerateSuggestionList {
	Context context;
	String sql;

	private SQLiteDatabase db;
	private SGLGSQLiteHelper dbHelper;

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
		db.close();
	}

	public GenerateSuggestionList(Context ctx) {
		context = ctx;
		dbHelper = new SGLGSQLiteHelper(context);
	}

	public ArrayList<Product> getAprioriList() {
		ListCore lcore = new ListCore(context);
		List lastList = new List();

		lastList = lcore.getLastList();
		ArrayList<String> templist = new ArrayList<String>();
		for (int i = 0; i < lastList.getListDetails().size(); i++) {
			templist.add(lastList.getListDetails().get(i).getPid() + "");
		}
		// if (lastList.getListDetails().size() < 7)
		// return new ArrayList<Product>();
		// Iterable<String> items = templist;
		// Apriori ap = new Apriori();

		// Current List Ferquent Items >>>>>> Just those there are association
		// for them

		// ArrayList<Item> currentItemsList = ap
		// .GetFrequentItemsTransaction(items);
		ArrayList<Item> sievedItemsList = new ArrayList<Item>();
		sql = "select distinct ar.arid_1, ar.confidence, ap.products from association_rules ar "
				+ "left outer join association_products ap on ap.apid = ar.arid_1"
				+ " order by ar.confidence desc";
		open();
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.moveToFirst()) {
			do {
				Item item = new Item();
				item.setName(cursor.getString(0));
				item.setConfidence(cursor.getDouble(1));
				item.setValue(cursor.getString(2));
				sievedItemsList.add(item);
			} while (cursor.moveToNext());
		}
		// }
		sievedItemsList = this
				.filterSievedItems(sievedItemsList, AMSTools
						.avoidDupInComma(lcore.getFilteredProductsInLastList()));

		// Get available associated product ids
		ArrayList<Item> associationeRules4CurrentList = new ArrayList<Item>();
		for (int i = 0; i < sievedItemsList.size(); i++) {
			int currentProduct = NVL.getInt(sievedItemsList.get(i).getName());
			open();
			sql = "select group_concat(ap.products, ',') as plist, ar1.confidence from association_rules ar1 "
					+ "inner join association_products ap on ap.apid = ar1.arid_2 "
					+ " where ar1.arid_1 = '" + currentProduct + "'";// group by
																		// ar1.arid_1,
																		// ar1.confidence";
																		// sql =
																		// "select group_concat(ap.products, ',') as plist, ar1.confidence from association_products ap "
			// + "inner join association_rules ar1 on ap.apid = ar1.arid_2 "
			// + " where ar1.arid_1 = '"
			// + currentProduct
			// + "' and ar1.confidence = "
			// + sievedItemsList.get(i).getConfidence();
			Cursor cursor2 = db.rawQuery(sql, null);
			if (cursor2 != null)
				cursor2.moveToFirst();
			if (cursor2.getCount() > 0) {
				do {
					Item item = new Item();
					item.setName(AMSTools.SortMyString(AMSTools
							.avoidDupInComma(cursor2.getString(0))));
					item.setConfidence(cursor2.getDouble(1));
					associationeRules4CurrentList.add(item);
				} while (cursor.moveToNext());
			}
			close();
		}
		ArrayList<Product> allProductsArray = new ArrayList<Product>();
		String allProducts = "";
		associationeRules4CurrentList = AMSTools
				.sievedMyArray(associationeRules4CurrentList);
		ProductCore pcore = new ProductCore(context);
		for (int i = 0; i < associationeRules4CurrentList.size(); i++) {
			String[] Prods = associationeRules4CurrentList.get(i).getName()
					.split(",");
			for (int j = 0; j < Prods.length; j++) {
				if (!allProducts.contains(Prods[j])) {
					allProducts = allProducts + "," + Prods[j];
					Product p = pcore.getProduct(NVL.getInt(Prods[j]));
					p.setConfidence(associationeRules4CurrentList.get(i)
							.getConfidence());
					allProductsArray.add(p);
				}
			}
		}
		return allProductsArray;

		//
		//
		// for (int i = 0; i < associationeRules4CurrentList.size(); i++) {
		// String[] Prods = associationeRules4CurrentList.get(i).getName()
		// .split(",");
		// for (int j = 0; j < Prods.length; j++) {
		// if (!allProducts.contains(Prods[j]))
		// allProducts = allProducts + "," + Prods[j];
		// }
		// }
		// open();
		// allProducts = AMSTools.SortMyString(allProducts);
		// sql = "select p.productname, ld.* from products p "
		// + "inner join listdetail ld on ld.pid = p.pid where ld.pid not in ("
		// + allProducts + ") and ld.listid = " + lastList.getListId();
		// Cursor cursor = db.rawQuery(sql, null);
		// if (cursor != null)
		// cursor.moveToFirst();
		// if (cursor.getCount() > 0) {
		// Product p = new Product();
		// p.setPid(cursor.getInt(cursor.getColumnIndex("pid")));
		// p.setProductName(cursor.getString(cursor
		// .getColumnIndex("productname")));
		// // Adding contact to list
		//
		// ps.add(p);
		// }
		//
		// return ps;
	}

	int sievedctnr = 0;
	ArrayList<Item> newseived = new ArrayList<Item>();

	private ArrayList<Item> filterSievedItems(ArrayList<Item> sievedItemsList,
			String lastList) {
		String[] ll = lastList.split(",");
		if (lastList.equalsIgnoreCase(""))
			return sievedItemsList;
		if (ll.length > 0 && sievedctnr <= sievedItemsList.size()) {
			for (int i = 0; i < sievedItemsList.size(); i++) {
				if (sievedItemsList.get(i).getValue() != null) {
					String[] ps = sievedItemsList.get(i).getValue().split(",");
					int cntr = 0;
					for (int j = 0; j < ps.length; j++) {
						for (int k = 0; k < ll.length; k++) {
							if (ps[j].equalsIgnoreCase(ll[k]))
								cntr++;
						}
					}
					if (cntr == ps.length)
						newseived.add(sievedItemsList.get(i));
					sievedItemsList.remove(i);
					return filterSievedItems(sievedItemsList, lastList);
				}
			}
		}
		return newseived;
	}

	public ArrayList<Product> getAllPrioretrizedProductsList() {
		ArrayList<Product> ps = new ArrayList<Product>();
		ListCore lcore = new ListCore(context);
		String filters = lcore.getProductsInLastList();
		String sql = "select * from products ";
		if (filters != null || !filters.equals(""))
			sql += " where pid not in (" + filters + ") and visibility = 1";
		sql += " order by confidence desc";
		open();
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null)
			cursor.moveToFirst();
		if (cursor.getCount() > 0)
			if (cursor.moveToFirst())
				while (cursor.isAfterLast() == false) {
					Product p = new Product();
					p.setPid(cursor.getInt(cursor.getColumnIndex("pid")));
					p.setProductName(cursor.getString(cursor
							.getColumnIndex("productname")));

					double d = cursor.getDouble(cursor
							.getColumnIndex("confidence"));
					if (d > 150)
						d = 0;
					else if (d > 100)
						d = 100;
					p.setConfidence(d * 0.01);
					p.setImg(cursor.getString(cursor.getColumnIndex("image")));
					// Adding contact to list
					ps.add(p);
					cursor.moveToNext();
				}
		close();
		return ps;

	}

	public ArrayList<HashMap<String, String>> getSuggestionList(int listid) {

		ArrayList<Product> allproducts = this.getAllPrioretrizedProductsList();
		ArrayList<Product> allFrequentItems = this.getAprioriList();
		ArrayList<HashMap<String, String>> res = new ArrayList<HashMap<String, String>>();
		boolean chk = true;
		for (int i = 0; i < allproducts.size(); i++) {
			chk = true;
			if (allFrequentItems.size() > 0)
				for (int j = 0; j < allFrequentItems.size(); j++) {
					// if()
					if (allFrequentItems.get(j).getPid() == allproducts.get(i)
							.getPid()
							&& allFrequentItems.get(j).getConfidence() > allproducts
									.get(i).getConfidence()) {
						chk = false;
						res.add(this.convertProductToHash(
								allFrequentItems.get(j), listid));
					}
				}
			if (chk)
				res.add(this.convertProductToHash(allproducts.get(i), listid));
		}
		Collections.sort(res, new Comparator<HashMap<String, String>>() {

			@Override
			public int compare(HashMap<String, String> lhs,
					HashMap<String, String> rhs) {
				Double firstValue = NVL.getDbl(new DecimalFormat("##.##")
						.format(NVL.getDbl(lhs.get("confidence"))));
				Double secondValue = NVL.getDbl(new DecimalFormat("##.##")
						.format(NVL.getDbl(rhs.get("confidence"))));
				return secondValue.compareTo(firstValue);
			}

		});
		return res;
	}

	private HashMap<String, String> convertProductToHash(Product p, int listid) {
		HashMap<String, String> res = new HashMap<String, String>();
		res.put("ListId", listid + "");
		res.put("pid", p.getPid() + "");
		res.put("pname", p.getProductName());
		res.put("thumb_url", p.getImg());
		res.put("confidence", p.getConfidence() + "");
		return res;
	}
}

abstract class MapComparator implements Comparator<HashMap<String, String>> {
	private final String key;

	public MapComparator(String key) {
		this.key = key;
	}

	public int compare(HashMap<String, String> first,
			HashMap<String, String> second) {
		// TODO: Null checking, both for maps and values
		Double firstValue = NVL.getDbl(key);
		Double secondValue = NVL.getDbl(key);
		return firstValue.compareTo(secondValue);
	}
}
