package android.pckg.sglg.Activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.pckg.sglg.R;
import android.pckg.sglg.Business.BarcodeCore;
import android.pckg.sglg.Business.ListCore;
import android.pckg.sglg.Business.ProductCore;
import android.pckg.sglg.Business.SettingCore;
import android.pckg.sglg.Tools.AMSTools;
import android.pckg.sglg.customs.ProductsListAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ProductListPopupActivity extends Activity {
	String productName, barcodetype, technique, barcodeno;
	public static final String KEY_TITLE = "title";
	public static final String KEY_ID = "product_id";
	public static final String KEY_THUMB_URL = "thumb_url";
	protected static final int REQUEST_OK = 1;
	Button setting, add, smart, addp;// , close;
	EditText searchField, listIdField;
	String listIdTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		final SettingCore score = new SettingCore(getApplicationContext());
		final ProductCore pcore = new ProductCore(getApplicationContext());
		final ListCore lcore = new ListCore(getApplicationContext());
		final String filters = lcore.getFilteredProductsInLastList();
		setContentView(R.layout.activity_product_list_popup);
		addp = (Button) findViewById(R.id.btn_add_product);
		// close = (Button) findViewById(R.id.btn_prodlist_cancel);
		searchField = (EditText) findViewById(R.id.input_search_product);
		listIdField = (EditText) findViewById(R.id.input_list_id);
		Intent Intent = getIntent();
		technique = Intent.getStringExtra("Technique");
		barcodetype = Intent.getStringExtra("BarcodeType");
		productName = Intent.getStringExtra("Key");
		barcodeno = Intent.getStringExtra("barcodeNo");
		listIdTxt = Intent.getStringExtra("ListId");
		// close.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(
		// "android.pckg.sglg.Activities.LISTACTIVITY");
		// startActivity(intent);
		//
		// }
		// });
		addp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final Intent intent = new Intent(getApplicationContext(),
						AddProductPopupActivity.class);
				intent.putExtra("barcodeNo", barcodeno);
				intent.putExtra("BarcodeType", barcodetype);
				intent.putExtra("Key", searchField.getText().toString());
				intent.putExtra("listId", listIdTxt);
				if (barcodetype != null && !barcodetype.equals(""))
					addp.setText("Add Barcode");
				if (!score.getWebserviceSettings()) {
					AlertDialog builder = new AlertDialog.Builder(
							ProductListPopupActivity.this).create();
					builder.setTitle("Server Not Found");
					builder.setMessage("To add a product you need global data access. Please turn it on.");
					builder.setButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							});
					builder.setButton2("Turn On",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									score.updateWebserviceSetting(1);
									finish();
									startActivity(intent);
								}
							});
					builder.show();
				} else {
					finish();
					startActivity(intent);
				}
			}
		});
		searchField.setText(productName);
		searchField.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence i, int start, int before,
					int count) {
				productName = i.toString();
				// searchField.getText().toString()
				//
				// if (score.getWebserviceSettings()) {
				// new ReadProduct().execute(productName, listIdTxt, AMSTools.avoidDupInComma(filters));
				// } else {
				technique = "";
				setListView(pcore
						.searchProduct(productName, listIdTxt, AMSTools.avoidDupInComma(filters)));
				// }

			}

			@Override
			public void beforeTextChanged(CharSequence i, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable i) {
				// if (score.getWebserviceSettings()) {
				// new ReadProduct().execute(productName, listIdTxt, filters);
				// } else {
				technique = "";
				setListView(pcore
						.searchProduct(productName, listIdTxt, AMSTools.avoidDupInComma(filters)));
				// }
			}
		});

		listIdField.setText(listIdTxt);
		listIdField.setVisibility(View.GONE);
		if (productName == null)
			productName = "";

		// if (score.getWebserviceSettings()) {
		// new ReadProduct().execute(productName, listIdTxt, filters);
		// } else {
		setListView(pcore.searchProduct(productName, listIdTxt, AMSTools.avoidDupInComma(filters)));
		// }

	}

	// public class ReadProduct extends
	// AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
	// StringBuilder builder = new StringBuilder();
	// HttpClient client = new DefaultHttpClient();
	//
	// @Override
	// protected ArrayList<HashMap<String, String>> doInBackground(
	// String... params) {
	// final ArrayList<HashMap<String, String>> lists = new
	// ArrayList<HashMap<String, String>>();
	// listIdTxt = params[1];
	// try {
	// HttpGet httpGet;
	// httpGet = new HttpGet(AMSTools.GetServerAddress()
	// + "GetProductLST?query=" + params[0] + "&filters="
	// + params[2]);
	//
	// HttpResponse response = client.execute(httpGet);
	// StatusLine statusLine = response.getStatusLine();
	// int statusCode = statusLine.getStatusCode();
	// if (statusCode == 200) {
	// HttpEntity entity = response.getEntity();
	// InputStream content = entity.getContent();
	// BufferedReader reader = new BufferedReader(
	// new InputStreamReader(content));
	// String line;
	// while ((line = reader.readLine()) != null) {
	// builder.append(line);
	// }
	// } else {
	// Log.e(ProductListPopupActivity.class.toString(),
	// "Failed to download file");
	// Toast.makeText(ProductListPopupActivity.this,
	// statusCode + "", Toast.LENGTH_LONG).show();
	// }
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// try {
	// JSONArray jsonArray = new JSONArray(builder.toString());
	// Log.i(ProductListPopupActivity.class.getName(),
	// "Number of entries " + jsonArray.length());
	// for (int i = 0; i < jsonArray.length(); i++) {
	// HashMap<String, String> ent = new HashMap<String, String>();
	// JSONObject jsonObject = jsonArray.getJSONObject(i);
	// ent.put("pname", jsonObject.getString("productName"));
	// ent.put("pid", jsonObject.getString("productID"));
	// ent.put("listid", listIdTxt);
	// ent.put(KEY_THUMB_URL, AMSTools.GetServerIP() + "LPIMWS/"
	// + jsonObject.getString("img"));
	// lists.add(ent);
	// Log.i(ProductListPopupActivity.class.getName(),
	// jsonObject.getString("productID"));
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return lists;
	// }
	//
	// @Override
	// protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
	// setListView(result);
	// }
	//
	// }

	public void setListView(ArrayList<HashMap<String, String>> result) {
		if (result.size() <= 10) {
			addp.setVisibility(View.VISIBLE);
		} else {
			addp.setVisibility(View.GONE);
		}
		if (technique != null)
			if (technique.equalsIgnoreCase("Voice")
					|| technique.equalsIgnoreCase("Barcode"))
				if (result.size() == 1)
					addToList(result.get(0).get("pid"),
							result.get(0).get("listid"));
		ProductsListAdapter adapter = new ProductsListAdapter(
				ProductListPopupActivity.this, this, result);
		final ListView listview = (ListView) findViewById(R.id.productListView);
		listview.setAdapter(adapter);
	}

	private void addToList(String pid, String listid) {
		ListCore core = new ListCore(getApplicationContext());
		core.updateDate();
		core.insertItem(Integer.parseInt(listid), Integer.parseInt(pid));
		Intent intent = new Intent("android.pckg.sglg.Activities.LISTACTIVITY");
		startActivity(intent);
	}

}
