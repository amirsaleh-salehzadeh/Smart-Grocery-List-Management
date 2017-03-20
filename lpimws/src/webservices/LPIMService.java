package webservices;

import java.util.ArrayList;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import business.AprioriBusiness;
import business.BarcodeBusiness;
import business.ListBusiness;
import business.ProductBusiness;
import business.UserBusiness;
import common.NVL;
import common.barcode.BarcodeENT;
import common.list.ListENT;
import common.product.ProductENT;
import common.users.UserENT;

import com.google.gson.Gson;

@Path("/WebService")
public class LPIMService {

	@GET
	@Path("/GetProductFromBarcode")
	@Produces("application/json")
	public String getProductFrombarcode(
			@QueryParam("barcodeNo") String barcodeNo,
			@QueryParam("barcodeFormat") String barcodeFormat) {
		String json = null;
		try {
			ProductENT product;
			BarcodeBusiness barcodeBusiness = new BarcodeBusiness();
			product = barcodeBusiness.getProducFromBarcode(barcodeNo,
					barcodeFormat);
			Gson gson = new Gson();
			System.out.println(gson.toJson(product));
			json = "[" + gson.toJson(product) + "]";
		}

		catch (Exception e) {
			System.out.println("Exception Error"); // Console
		}
		return json;
	}

	@GET
	@Path("/InsertOrUpdateUser")
	@Produces("application/json")
	public String insertOrUpdateUser(@QueryParam("nickName") String nickName,
			@QueryParam("tel") String tel, @QueryParam("dob") String dob,
			@QueryParam("uid") int uid, @QueryParam("gender") int gender,
			@QueryParam("webuid") int webuid) {
		String json = null;
		try {
			UserENT user = new UserENT();
			user.setNickName(nickName);
			user.setGender(gender);
			user.setTel(tel);
			user.setLocalUID(uid);
			user.setDOB(dob);
			if (webuid != 0)
				user.setUID(webuid);
			UserBusiness business = new UserBusiness();
			user = business.InsertOrUpdateUser(user);
			Gson gson = new Gson();
			System.out.println(gson.toJson(user));
			json = "[" + gson.toJson(user) + "]";
		}

		catch (Exception e) {
			System.out.println("Exception Error"); // Console
		}
		return json;
	}

	@GET
	@Path("/InsertProductBarcode")
	@Produces("application/json")
	public String insertProductBarcode(
			@QueryParam("ProductName") String productName,
			@QueryParam("BarcodeNo") String barcodeNo,
			@QueryParam("BarcodeType") String barcodeType) {
		String json = "";
		try {
			ProductENT p = new ProductENT();
			p.setProductName(productName);
			ProductBusiness pbusiness = new ProductBusiness();
			int productid = 0;
			boolean pexist = pbusiness.productExist(p);

			if (pexist)
				json = "[{\"product\":\"Product already exists\"";
			else
				json = "[{\"product\":\"\"";
			productid = pbusiness.insertProduct(p);
			int barcodeid = 0;
			BarcodeBusiness bbusiness = new BarcodeBusiness();
			BarcodeENT b = new BarcodeENT();
			b.setBarcodeCode(barcodeNo);
			b.setBarcodeFormat(barcodeType);
			boolean bexist = bbusiness.barcodeExist(b);
			if (bexist && barcodeNo != null && !barcodeNo.equals(""))
				json += ",\"barcode\":\"Barcode already exists\"}]";
			else
				json += ",\"barcode\":\"\"}]";
			barcodeid = bbusiness.insertBarcode(b);
			if (barcodeid > 0 && productid > 0
					&& !bbusiness.barcodeProductExist(barcodeid, productid))
				bbusiness.insertBarcodeProduct(barcodeid, productid);
			System.out.println(json);
		} catch (Exception e) {
			System.out.println("Exception Error"); // Console
		}
		return json;
	}

	@GET
	@Path("/GetProductLST")
	@Produces("application/json")
	public String getProductLST(@QueryParam("query") String query,
			@QueryParam("filters") String filters) {
		String json = null;
		try {
			ArrayList<ProductENT> ProductLST = new ArrayList<ProductENT>();
			ProductBusiness ProductBusiness = new ProductBusiness();
			if (filters == null)
				filters = "";
			ProductLST = ProductBusiness.getProductList(query, filters);
			Gson gson = new Gson();
			System.out.println(gson.toJson(ProductLST));
			json = gson.toJson(ProductLST);
		}

		catch (Exception e) {
			System.out.println("Exception Error"); // Console
		}
		return json;
	}

	@GET
	@Path("/GetSuggestionsLST")
	@Produces("application/json")
	public String getSuggestionsLST(@QueryParam("filters") String filters) {
		String json = null;
		try {
			ArrayList<ProductENT> ProductLST = new ArrayList<ProductENT>();
			AprioriBusiness ab = new AprioriBusiness();
			if (filters == null)
				filters = "";
			ProductLST = ab.getSuggestionList(filters);
			Gson gson = new Gson();
			System.out.println(gson.toJson(ProductLST));
			json = gson.toJson(ProductLST);
		}

		catch (Exception e) {
			System.out.println("Exception Error"); // Console
		}
		return json;
	}

	@GET
	@Path("/CreateNewList")
	@Produces("application/json")
	public String createNewList(@QueryParam("webuid") String query,
			@QueryParam("pids") String pids) {
		String json = null;
		try {
			ListBusiness listBusiness = new ListBusiness();
			int id = listBusiness.newList(NVL.getInt(query));
			String[] ps = pids.split(",");
			for (int i = 0; i < ps.length; i++) {
				listBusiness.InsertItemsToList(id, NVL.getInt(ps[i]));
			}
		} catch (Exception e) {
			System.out.println("Exception Error"); // Console
		}
		return "";
	}

	@GET
	@Path("/AddToList")
	@Produces("application/json")
	public String addToList(@QueryParam("webuid") int uid,
			@QueryParam("listid") int listid) {
		try {
			ListBusiness business = new ListBusiness();
			business.InsertItemsToList(listid, uid);
		}

		catch (Exception e) {
			System.out.println("Exception Error"); // Console
		}
		return "";
	}

}
