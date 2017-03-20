package business;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import algorithm.apriori.Apriori;
import algorithm.apriori.Item;

import common.AMSTools;
import common.NVL;
import common.product.ProductENT;

public class AprioriBusiness {
	public void deleteAssociations() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/SGLG", "root", "root");
			String sql = "delete from association_products;";
			PreparedStatement ps;
			ps = con.prepareStatement(sql);
			ps.execute();
			sql = "delete from association_rules;";
			ps = con.prepareStatement(sql);
			ps.execute();
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	public int insertAssociation(String products) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/SGLG", "root", "root");
			String sql = "INSERT INTO association_products(products) "
					+ "select * from (select \""
					+ products
					+ "\") as tmp "
					+ "WHERE NOT EXISTS(SELECT 1 FROM association_products WHERE products = \""
					+ products + " \") LIMIT 1; ";
			PreparedStatement ps;
			ps = con.prepareStatement(sql);
			ps.execute();
			sql = "SELECT associationid FROM association_products WHERE products = \""
					+ products + " \"";
			ResultSet rs = ps.executeQuery(sql);
			if (rs.first()) {
				int id = rs.getInt("associationid");
				con.close();
				return id;
			}
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return getAssociationId(products);
	}

	public int getAssociationId(String products) {
		String sql = "SELECT * FROM association_products where products = '"
				+ products + "'";//
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/SGLG", "root", "root");
			PreparedStatement ps;
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				int id = rs.getInt("associationid");
				con.close();
				return id;
			}
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public void insertAssociationRule(int first, int second, double confidence) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/SGLG", "root", "root");
			String c = new DecimalFormat("##.##").format(confidence);
			String sql = "INSERT INTO association_rules(arid_1, arid_2, confidence) SELECT * from ( select "
					+ first
					+ ","
					+ second
					+ ","
					+ c
					+ ") as tmp WHERE NOT EXISTS(SELECT 1 FROM association_rules WHERE arid_1 = "
					+ first
					+ " and arid_2 = "
					+ second
					+ " and confidence = "
					+ c + ")";
			PreparedStatement ps;
			ps = con.prepareStatement(sql);
			ps.execute();
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	public ArrayList<ProductENT> getSuggestionList(String filters) {
		ProductBusiness pb = new ProductBusiness();
		ArrayList<ProductENT> allproducts = pb.getProductList("", filters);
		ArrayList<ProductENT> allFrequentItems = this.getAprioriList(filters);
		ArrayList<ProductENT> res = new ArrayList<ProductENT>();
		boolean chk = true;
		for (int i = 0; i < allproducts.size(); i++) {
			chk = true;
			if (allFrequentItems.size() > 0)
				for (int j = 0; j < allFrequentItems.size(); j++) {
					if (allFrequentItems.get(j).getProductID() == allproducts.get(i)
							.getProductID()
							&& allFrequentItems.get(j).getconfidence() > allproducts
									.get(i).getconfidence()) {
						chk = false;
						res.add(allFrequentItems.get(j));
					}
				}
			if (chk)
				res.add(allproducts.get(i));
		}
		Collections.sort(res, new Comparator<ProductENT>() {
			public int compare(ProductENT o1, ProductENT o2) {
				Double firstValue = o1.getconfidence();
				Double secondValue = o2.getconfidence();
				return secondValue.compareTo(firstValue);
			}
		});
		return res;
	}

	private ArrayList<ProductENT> getAprioriList(String filters) {
		ArrayList<Item> sievedItemsList = new ArrayList<Item>();
		String sql = "select distinct ar.arid_1, ar.confidence, ap.products from association_rules ar "
				+ "left outer join association_products ap on ap.associationid = ar.arid_1"
				+ " order by ar.confidence desc";

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sglg", "root", "root");
			PreparedStatement ps = con.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Item item = new Item();
				item.setName(rs.getString("arid_1"));
				item.setConfidence(rs.getDouble("confidence"));
				item.setValue(rs.getString("products"));
				sievedItemsList.add(item);
			}
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		// }
		sievedItemsList = this.filterSievedItems(sievedItemsList, filters);

		// Get available associated product ids
		ArrayList<Item> associationeRules4CurrentList = new ArrayList<Item>();
		for (int i = 0; i < sievedItemsList.size(); i++) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection(
						"jdbc:mysql://localhost:3306/sglg", "root", "root");
				int currentProduct = NVL.getInt(sievedItemsList.get(i)
						.getName());
				sql = "select group_concat(ap.products) as plist, ar1.confidence from association_products ap "
						+ "inner join association_rules ar1 on ap.associationid = ar1.arid_2 "
						+ " where ar1.arid_1 = '"
						+ currentProduct
						+ "' ";
//				and ar1.confidence = "
//					+ sievedItemsList.get(i).getConfidence();
				PreparedStatement ps = con.prepareStatement(sql);

				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					Item item = new Item();
					item.setName(AMSTools
							.avoidDupInComma(rs.getString("plist")));
					item.setConfidence(rs.getDouble("confidence"));
					associationeRules4CurrentList.add(item);
				}
				con.close();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		ArrayList<ProductENT> allProductsArray = new ArrayList<ProductENT>();
		String allProducts = "";
		associationeRules4CurrentList = AMSTools
				.sievedMyArray(associationeRules4CurrentList);
		ProductBusiness pb = new ProductBusiness();
		for (int i = 0; i < associationeRules4CurrentList.size(); i++) {
			String[] Prods = associationeRules4CurrentList.get(i).getName()
					.split(",");
			for (int j = 0; j < Prods.length; j++) {
				if (!allProducts.contains(Prods[j])) {
					allProducts = allProducts + "," + Prods[j];
					ProductENT p = pb.getProductENT(NVL.getInt(Prods[j]) + "");
					p.setconfidence(associationeRules4CurrentList.get(i)
							.getConfidence());
					allProductsArray.add(p);
				}
			}
		}

		return allProductsArray;
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

	public static void main(String[] args) {
		Apriori acore = new Apriori();
		acore.calculateAssociationRules();
	}
}
