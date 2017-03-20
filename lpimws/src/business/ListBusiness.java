package business;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import common.AMSTools;
import common.list.ListDetailENT;
import common.list.ListENT;
import common.product.ProductENT;
import common.users.UserENT;

public class ListBusiness {

	public void InsertItemsToList(int listid, int pid) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sglg", "root", "root");
			String sql = "";
			PreparedStatement ps;
			sql = "insert into list_line (listid, pid) " + " values (?, ?) ";
			ps = con.prepareStatement(sql);
			ps.setInt(1, listid);
			ps.setInt(2, pid);
			ps.execute();
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	public ListENT getList(String query, String uid) {
		ArrayList<ListDetailENT> ListDetailENT = new ArrayList<ListDetailENT>();
		ListENT listENT = new ListENT();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sglg", "root", "root");
			String sql = "Select * from list_header where listid = ? and uid = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, query);
			ps.setString(2, uid);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				listENT.setDate(rs.getDate("date"));
				listENT.setUid(rs.getInt("uid"));
				listENT.setListid(rs.getInt("listid"));
			}
			sql = "Select * from list_line where listlineid = ?";
			ps = con.prepareStatement(sql);
			ps.setInt(1, listENT.getListid());
			rs = ps.executeQuery();
			ArrayList<ListDetailENT> lde = new ArrayList<ListDetailENT>();
			while (rs.next()) {
				ListDetailENT p = new ListDetailENT();
				p.setDetailid(rs.getInt("listlineid"));
				p.setListid(rs.getInt("listid"));
				ProductENT prod = this.getProductENT(rs.getString("pid"));
				p.setProduct(prod);
				lde.add(p);
			}
			listENT.setDetails(lde);
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return listENT;
	}

	public ProductENT getProductENT(String query) {
		ProductENT p = new ProductENT();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/SGLG", "root", "root");
			String sql = "";
			PreparedStatement ps;
			sql = "Select * from products where pid = ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, query);
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				p.setProductID(rs.getInt("pid"));
				p.setProductName(rs.getString("pname"));
				p.setImg("image/" + rs.getString("img"));
			}
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return p;
	}

	public String getOrdersString(String query) {
		String res = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/lpim", "root", "");
			String sql = "Select productID from orderdetail where orderID = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, query);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				res += (rs.getString("productID")) + ',';
			}
			res = res.substring(0, res.length() - 1);
			System.out.println(res);
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return res;
	}

	public int newList(int uid) {
		int id = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sglg", "root", "root");
			String sql = "";
			PreparedStatement ps;
			sql = "insert into list_header (uid, date) " + " values (?, ?); ";
			ps = con.prepareStatement(sql);
			ps.setInt(1, uid);
			ps.setString(2, AMSTools.getTodayDate());
			ps.execute();
			sql = "SELECT MAX(listid) as listid FROM list_header;";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				id = rs.getInt("listid");
				con.close();
				return id;
			}
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return id;
	}

	public String[] getAllTransactions() {
		String[] res = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/SGLG", "root", "root");
			PreparedStatement ps;
			String sql = "select listid ,(select GROUP_CONCAT(ld2.pid) as pids from"
					+ " list_line ld2 where ld1.listid = ld2.listid group by ld2.listid) as img "
					+ "from list_line ld1 group by ld1.listid; ";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			rs.last();
			res = new String[rs.getRow()];
			int count = 0;
			rs.beforeFirst();
			while (rs.next()) {
				res[count] = rs.getString("img");
				count++;
			}
			st.close();
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return res;
	}

	public static void createRandomData() {
		int noOfLists = 150;
		int noOfDetail = 20;
		ProductBusiness p = new ProductBusiness();
		String[] n = p.getProductsInLastList().split(",");
		int[] q = new int[n.length];
		for (int i = 0; i < n.length; i++) {
			q[i]= Integer.parseInt(n[i]);
		}
		Random rng = new Random(); // Ideally just create one instance globally
		// Note: use LinkedHashSet to maintain insertion order
		
		
		ListBusiness lb = new ListBusiness();
		for (int i = 0; i <noOfLists; i++) {
			int listid = lb.newList(111);
			Set<Integer> generated = new LinkedHashSet<Integer>();
			while (generated.size() < noOfDetail)
			{
			    Integer next = rng.nextInt(q[rng.nextInt(q.length)]) + 1;
			    // As we're adding to a set, this will automatically do a containment check
			    generated.add(next);
			}
			for (int j : generated) {
				lb.InsertItemsToList(listid,j);
			}
		}
		
	}

	public static void main(String[] args) {
		String[] p = new String[] { "1,2,5", "2,4", "2,3", "1,2,4", "1,3",
				"2,3", "1,3", "1,2,3,5", "1,2,3" };
		ListBusiness lcore = new ListBusiness();
		lcore.createRandomData();
		// for (int i = 0; i < p.length; i++) {
		// for (int j = 0; j < p[i].split(",").length; j++) {
		// lcore.InsertItemsToList((i+1),
		// NVL.getInt(p[i].split(",")[j]));
		// }
		// }
	}
}
