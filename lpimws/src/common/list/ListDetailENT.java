package common.list;

import common.product.ProductENT;

public class ListDetailENT {
	int detailid;
	ProductENT product;
	int listid;

	public int getDetailid() {
		return detailid;
	}
	public void setDetailid(int detailid) {
		this.detailid = detailid;
	}
	public ProductENT getProduct() {
		return product;
	}
	public void setProduct(ProductENT product) {
		this.product = product;
	}
	public int getListid() {
		return listid;
	}
	public void setListid(int listid) {
		this.listid = listid;
	}

}
