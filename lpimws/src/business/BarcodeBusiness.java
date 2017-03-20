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
import common.barcode.BarcodeENT;
import common.list.ListDetailENT;
import common.list.ListENT;
import common.product.ProductENT;

public class BarcodeBusiness {

	public ProductENT getProducFromBarcode(String barcodeNo,
			String barcodeFormat) {
		ProductENT productENT = new ProductENT();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sglg", "root", "root");
			String sql = "";
			PreparedStatement ps;
			sql = "Select p.* from barcode b "
					+ "inner join barcode_product bp on b.BID = bp.BID "
					+ "left outer join products p on p.pid = bp.pid"
					+ " where b.bno = ? and b.barcode_format = ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, barcodeNo);
			ps.setString(2, barcodeFormat);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				productENT.setProductID(rs.getInt("pid"));
				productENT.setProductName(rs.getString("pname"));
				productENT.setImg("image/" + rs.getString("img"));
			}
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return productENT;
	}

	public int insertBarcode(BarcodeENT b) {
		int id = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sglg", "root", "root");
			

			
			PreparedStatement ps;
			
			
			String sql = "INSERT INTO barcode (bno, barcode_format) "
				+ "select * from (select \""
				+ b.getBarcodeCode()
				+ "\", \""
				+ b.getBarcodeFormat()
				+ "\""
				+ ") as tmp "
				+ "WHERE NOT EXISTS(SELECT 1 FROM barcode WHERE bno = \""
				+ b.getBarcodeCode() + "\" and barcode_format = \""+b.getBarcodeFormat()+"\") LIMIT 1; ";
			
			
			
			ps = con.prepareStatement(sql);
			ps.execute();
			sql ="SELECT bid FROM barcode WHERE bno = \""
				+ b.getBarcodeCode() + " \" and  barcode_format = \""
				+ b.getBarcodeFormat() + " \" LIMIT 1; ";
			
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				id = rs.getInt("bid");
			}
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return id;
	}

	public int insertBarcodeProduct(int b, int p) {
		int id = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sglg", "root", "root");
			PreparedStatement ps;
			String sql = "INSERT INTO barcode_product (bid, pid) "
				+ "select * from (select "
				+ b
				+ ", "
				+ p
				+ ") as tmp "
				+ "WHERE NOT EXISTS(SELECT 1 FROM barcode_product WHERE bid = "
				+ b + " and pid = "+p+") LIMIT 1; ";
			
			ps = con.prepareStatement(sql);
			ps.execute();
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return id;
	}

	public boolean barcodeExist(BarcodeENT b) {
		int id = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sglg", "root", "root");
			PreparedStatement ps;
			String sql = "SELECT bid FROM barcode WHERE bno = \""
					+ b.getBarcodeCode() + "\" and barcode_format =\""
					+ b.getBarcodeFormat() + "\" LIMIT 1;";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				id = rs.getInt("bid");
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
	
	public boolean barcodeProductExist(int bid, int pid) {
		int id = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sglg", "root", "root");
			PreparedStatement ps;
			String sql = "SELECT BPID FROM barcode_product " +
					" WHERE bid = "
					+ bid+ " and pid ="
					+ pid + " LIMIT 1;";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				id = rs.getInt("bpid");
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
}
