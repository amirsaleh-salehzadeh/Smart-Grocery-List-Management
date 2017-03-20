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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.pckg.sglg.R;
import android.pckg.sglg.Business.AprioriCore;
import android.pckg.sglg.Business.ProductCore;
import android.pckg.sglg.Business.SettingCore;
import android.pckg.sglg.Tools.AMSTools;
import android.pckg.sglg.Tools.NVL;
import android.pckg.sglg.customs.ProductAdminListAdapter;
import android.pckg.sglg.customs.ProductsListAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class ProductActivity extends Activity {
	String productName, barcodetype, technique;
	EditText searchField;
	String listIdTxt;

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(
				"android.pckg.sglg.Activities.SETTING");
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		final SettingCore score = new SettingCore(getApplicationContext());
		final ProductCore pcore = new ProductCore(getApplicationContext());
		String filters = "";
		setContentView(R.layout.activity_product);
		Button homebtn = (Button) findViewById(R.id.btn_home_product);
		homebtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("android.pckg.sglg.Activities.LISTACTIVITY");
				startActivity(intent);
			}
		});
		Button setting = (Button) findViewById(R.id.btn_setting);
		setting.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(
						"android.pckg.sglg.Activities.SETTING");
				startActivity(intent);
			}
		});
		Button addp = (Button) findViewById(R.id.btn_addProduct);
		addp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!score.getWebserviceSettings()) {

					AlertDialog builder = new AlertDialog.Builder(
							ProductActivity.this).create();
					builder.setTitle("Server Not Found");
					builder.setMessage("To add new product you need global data access. Please turn it on.");
					builder.setButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											"android.pckg.sglg.Activities.PRODUCTACTIVITY");
									startActivity(intent);
								}
							});
					builder.setButton2("Turn On",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									score.updateWebserviceSetting(1);
									Intent myIntent = new Intent(getApplicationContext(),
											AddProductPopupActivity.class);
									startActivity(myIntent);
								}
							});
					builder.show();
				} else {
					Intent myIntent = new Intent(getApplicationContext(),
							AddProductPopupActivity.class);
					startActivity(myIntent);
				}

			}
		});
		searchField = (EditText) findViewById(R.id.input_search_product);
		searchField.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence i, int start, int before,
					int count) {
				productName = i.toString();
				// new ReadProduct().execute(productName, listIdTxt, "");
				setListView(pcore.searchProduct(productName, listIdTxt, ""));
			}

			@Override
			public void beforeTextChanged(CharSequence i, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable i) {
				// new ReadProduct().execute(productName, listIdTxt, "");
				setListView(pcore.searchProduct(productName, listIdTxt, ""));
			}
		});

		if (productName == null)
			productName = "";

		// setListView(pcore.searchProduct(productName, listIdTxt, filters));
		// if (score.getWebserviceSettings())
		// new ReadProduct().execute(productName, listIdTxt, filters);
		// else
		setListView(pcore.searchProduct(productName, listIdTxt, filters));
	}

	public void setListView(ArrayList<HashMap<String, String>> result) {
		ProductAdminListAdapter adapter = new ProductAdminListAdapter(
				ProductActivity.this, this, result);
		final ListView listview = (ListView) findViewById(R.id.ProductslistView);
		listview.setAdapter(adapter);
	}

	public class ReadProduct extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			final ArrayList<HashMap<String, String>> lists = new ArrayList<HashMap<String, String>>();
			listIdTxt = params[1];
			try {
				HttpGet httpGet;
				httpGet = new HttpGet(AMSTools.GetServerAddress()
						+ "GetProductLST?query=" + params[0] + "&filters="
						+ params[2]);

				HttpParams httpParameters = new BasicHttpParams();
				// Set the timeout in milliseconds until a connection is established.
				// The default value is zero, that means the timeout is not used. 
				int timeoutConnection = 3000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				// Set the default socket timeout (SO_TIMEOUT) 
				// in milliseconds which is the timeout for waiting for data.
				int timeoutSocket = 5000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

				DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
				HttpResponse response = httpClient.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
					}
				} else {
					Log.e(ProductListPopupActivity.class.toString(),
							"Failed to download file");
					Intent intent = new Intent(
							"android.pckg.sglg.Activities.ERRORACTIVITY");
					startActivity(intent);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				Intent intent = new Intent(
						"android.pckg.sglg.Activities.ERRORACTIVITY");
				startActivity(intent);
				e.printStackTrace();
			}

			try {
				JSONArray jsonArray = new JSONArray(builder.toString());
				Log.i(ProductListPopupActivity.class.getName(),
						"Number of entries " + jsonArray.length());
				for (int i = 0; i < jsonArray.length(); i++) {
					HashMap<String, String> ent = new HashMap<String, String>();
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					ent.put("pname", jsonObject.getString("productName"));
					ent.put("pid", jsonObject.getString("productID"));
					ent.put("listid", listIdTxt);
					ent.put("thumb_url", AMSTools.GetServerIP() + "LPIMWS/"
							+ jsonObject.getString("img"));
					lists.add(ent);
					Log.i(ProductListPopupActivity.class.getName(),
							jsonObject.getString("productID"));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return lists;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			setListView(result);
		}

	}

}
