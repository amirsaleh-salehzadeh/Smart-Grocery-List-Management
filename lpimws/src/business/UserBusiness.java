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
import common.users.UserENT;

public class UserBusiness {

	public UserENT InsertOrUpdateUser(UserENT user) {
		int id =  user.getUID();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/sglg", "root", "root");
			String sql = "";
			PreparedStatement ps;
			if (user.getUID() == 0)
				sql = "insert into users (nickname, tel, dob, localuid, gender) "
						+ " values (?, ?, ?, ?, ?) ";
			else
				sql = "update users set nickname = ?, tel = ?, dob = ?, localuid = ? , gender = ? where uid = ? ";
			ps = con.prepareStatement(sql);
			ps.setString(1, user.getNickName());
			ps.setString(2, user.getTel());
			ps.setString(3, user.getDOB());
			ps.setInt(4, user.getLocalUID());
			ps.setInt(5, user.getGender());
			if (user.getUID() != 0)
				ps.setInt(6, id);
			ps.execute();
			if(user.getUID() != 0)
				user.setUID(id);
			else{
				sql = "SELECT users.uid FROM users ORDER BY uid DESC LIMIT 1 ";
				ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				ProductENT p = new ProductENT();
				while (rs.next()) {
					user.setUID(rs.getInt("uid"));
				}
			}
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return user;
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

}
