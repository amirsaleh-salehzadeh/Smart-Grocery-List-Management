package business;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebService;

import common.AMSTools;
import common.list.ListDetailENT;
import common.list.ListENT;
import common.product.ProductENT;

public class ProductBusiness {

	public ArrayList<String> getAllProductsInTransactions() {
		ArrayList<String> res = new ArrayList<String>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sglg", "root", "root");
			String sql = "";
			sql = "SELECT pid FROM products where pid in (select distinct pid from list_line)";
			PreparedStatement ps = con.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				res.add(rs.getString("pid"));
			}
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return res;
	}
	
	

	public int insertProduct(ProductENT p) {
		int id = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sglg", "root", "root");
			PreparedStatement ps;
			String sql = "INSERT ignore INTO products (pname) " + " values (\""
					+ p.getProductName() + "\"); ";
			ps = con.prepareStatement(sql);
			ps.execute();
			sql = "SELECT pid FROM products WHERE pname = \""
					+ p.getProductName() + "\" LIMIT 1; ";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				id = rs.getInt("pid");
			}
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		return id;
	}

	public boolean productExist(ProductENT p) {
		int id = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sglg", "root", "root");
			PreparedStatement ps;
			String sql = "SELECT pid FROM products WHERE pname = \""
					+ p.getProductName() + "\" LIMIT 1; ";
			ps = con.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				id = rs.getInt("pid");
			}
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		if (id == 0)
			return false;
		else
			return true;
	}

	public ArrayList<ProductENT> getProductList(String query, String filters) {
		ArrayList<ProductENT> productENTs = new ArrayList<ProductENT>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sglg", "root", "root");
			String sql = "";
			PreparedStatement ps;
			sql = "Select * from products where pname like ? ";
			if (filters != null && !filters.equalsIgnoreCase(""))
				sql += "and pid not in (" + filters + ")";
			sql += " order by pname ";
			ps = con.prepareStatement(sql);
			ps.setString(1, "%" + query + "%");

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				ProductENT p = new ProductENT();
				p.setProductID(rs.getInt("pid"));
				p.setProductName(rs.getString("pname"));
				p.setconfidence(rs.getDouble("confidence"));
				p.setImg("image/" + rs.getString("img"));
				productENTs.add(p);
			}
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return productENTs;
	}

	public ArrayList<ProductENT> getProductLST(String query) {
		ArrayList<ProductENT> ents = new ArrayList<ProductENT>();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sglg", "root", "root");
			String[] ids = query.split(",");
			for (int i = 0; i < ids.length; i++) {
				String sql = "Select * from product where pid = ?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, ids[i]);
				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					ProductENT p = new ProductENT();
					p.setProductID(rs.getInt("pid"));
					p.setProductName(rs.getString("pname"));
					p.setImg("image/" + rs.getString("img"));
					ents.add(p);
				}
			}
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return ents;
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
			ps.setString(1, query);
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

	public String getProductsInLastList() {
		String res = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/SGLG", "root", "root");
			PreparedStatement ps;
			String sql = "select group_concat(pid) pids from products ";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.first()) {
				String id = rs.getString("pids");
				con.close();
				return id;
			}
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return res;
	}
}
