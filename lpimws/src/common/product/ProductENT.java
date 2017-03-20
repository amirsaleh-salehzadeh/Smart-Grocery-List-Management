package common.product;

import java.sql.Blob;




public class ProductENT {
	int productID;
	String productName;
	String img;
	Double confidence;

	public Double getconfidence() {
		return confidence;
	}

	public void setconfidence(Double confiden) {
		this.confidence = confiden;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String string) {
		this.img = string;
	}

	public int getProductID() {
		return productID;
	}

	public void setProductID(int productID) {
		this.productID = productID;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

}
