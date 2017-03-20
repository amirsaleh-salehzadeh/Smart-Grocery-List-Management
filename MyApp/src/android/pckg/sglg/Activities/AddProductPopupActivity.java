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

import com.sun.xml.internal.ws.api.Cancelable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.pckg.sglg.R;
import android.pckg.sglg.Welcome;
import android.pckg.sglg.Activities.ListActivity.GetProductBarcode;
import android.pckg.sglg.Business.ProductCore;
import android.pckg.sglg.Business.SettingCore;
import android.pckg.sglg.Business.UsersCore;
import android.pckg.sglg.Common.Product;
import android.pckg.sglg.Common.User;
import android.pckg.sglg.Tools.AMSTools;
import android.pckg.sglg.Tools.NVL;
import android.pckg.sglg.Welcome.ReadProduct;
import android.pckg.sglg.customs.ProductsListAdapter;
import android.pckg.sglg.zxing.integration.IntentIntegrator;
import android.pckg.sglg.zxing.integration.IntentResult;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

public class AddProductPopupActivity extends Activity {
	EditText productname;
	EditText barcodeno;
	EditText barcodetype;
	boolean fromPopup = false;
	String listId = "";

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_product_dialog_box);
		Intent Intent = getIntent();
		String barcodeType = Intent.getStringExtra("BarcodeType");
		String productName = Intent.getStringExtra("Key");
		String barcodeNo = Intent.getStringExtra("barcodeNo");
		listId = Intent.getStringExtra("listId");
		if (barcodeType != null || productName != null || barcodeNo != null)
			fromPopup = true;
		productname = (EditText) findViewById(R.id.createproductname);
		productname.setText(productName);
		barcodeno = (EditText) findViewById(R.id.createbarcodeno);
		barcodeno.setText(barcodeNo);
		barcodetype = (EditText) findViewById(R.id.createbarcodetype);
		barcodetype.setText(barcodeType);
		barcodeno.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				IntentIntegrator scanIntegrator = new IntentIntegrator(
						AddProductPopupActivity.this);
				scanIntegrator.initiateScan();
				return false;
			}
		});
		Button button = (Button) findViewById(R.id.save);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (productname.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(),
							"Product name cannot be empty", Toast.LENGTH_LONG)
							.show();
					return;
				} else
					new AddProduct(AddProductPopupActivity.this).execute(productname.getText().toString(),
							barcodeno.getText().toString(), barcodetype
									.getText().toString());
			}
		});
		Button buttoncancel = (Button) findViewById(R.id.cancel);
		buttoncancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Rect dialogBounds = new Rect();
		getWindow().getDecorView().getHitRect(dialogBounds);

		if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
			// Tapped outside so we finish the activity
			return false;
		}
		return super.dispatchTouchEvent(ev);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, data);
		barcodeno.setText(scanningResult.getContents());
		barcodetype.setText(scanningResult.getFormatName());
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void setListView(ArrayList<HashMap<String, String>> result) {
		ProductsListAdapter adapter = new ProductsListAdapter(
				AddProductPopupActivity.this, this, result);
		final ListView listview = (ListView) findViewById(R.id.productListView);
		listview.setAdapter(adapter);
	}

	public class AddProduct extends
			AsyncTask<String, Void, HashMap<String, String>> {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		private ProgressDialog dialog;

		public AddProduct(final AddProductPopupActivity p) {
			runOnUiThread(new Runnable() {
				public void run() {
					dialog = new ProgressDialog(p);
				}
			});
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			runOnUiThread(new Runnable() {
				public void run() {
					dialog.setMessage("Saving data in server, Please Wait...");
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();
				}
			});
		}

		@Override
		protected HashMap<String, String> doInBackground(String... params) {

			try {
				HttpGet httpGet;
				String serviceAddress = AMSTools.GetServerAddress()
						+ "InsertProductBarcode?ProductName="
						+ params[0].replaceAll(" ", "+") + "&BarcodeNo="
						+ params[1] + "&BarcodeType=" + params[2];
				httpGet = new HttpGet(serviceAddress);

				HttpParams httpParameters = new BasicHttpParams();
				// Set the timeout in milliseconds until a connection is
				// established.
				// The default value is zero, that means the timeout is not
				// used.
				int timeoutConnection = 3000;
				HttpConnectionParams.setConnectionTimeout(httpParameters,
						timeoutConnection);
				// Set the default socket timeout (SO_TIMEOUT)
				// in milliseconds which is the timeout for waiting for data.
				int timeoutSocket = 5000;
				HttpConnectionParams
						.setSoTimeout(httpParameters, timeoutSocket);

				DefaultHttpClient httpClient = new DefaultHttpClient(
						httpParameters);
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
					Intent intent = new Intent(
							"android.pckg.sglg.Activities.ERRORACTIVITY");
					startActivity(intent);
					Log.e(ListActivity.class.toString(), "Failed");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				Intent intent = new Intent(
						"android.pckg.sglg.Activities.ERRORACTIVITY");
				startActivity(intent);
				e.printStackTrace();
			}
			HashMap<String, String> ent = new HashMap<String, String>();
			try {
				JSONArray jsonArray = new JSONArray(builder.toString());
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					ent.put("product", jsonObject.getString("product"));
					ent.put("barcode", jsonObject.getString("barcode"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ent;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			String p = "";
			String b = "";
			if (!result.get("product").equalsIgnoreCase(""))
				p = result.get("product") + "!\n";
			else
				p = "Product added successfully!\n";
			if (!result.get("barcode").equalsIgnoreCase(""))
				b = result.get("barcode") + "!";
			else
				b = "Barcode added successfully!";

			showDialogBox(p + b);
			super.onPostExecute(result);
		}
	}

	public class ReadProduct extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		private ProgressDialog dialog;

		public ReadProduct(final AddProductPopupActivity p) {
			runOnUiThread(new Runnable() {
				public void run() {
					dialog = new ProgressDialog(p);
				}
			});
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			runOnUiThread(new Runnable() {
				public void run() {
					dialog.setMessage("Loading Data From SGLG Please Wait...");
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();
				}
			});
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			final ArrayList<HashMap<String, String>> lists = new ArrayList<HashMap<String, String>>();
			try {
				HttpGet httpGet;
				ProductCore pcore = new ProductCore(getApplicationContext());
				String filters = pcore.getAvailableProducts();
				httpGet = new HttpGet(AMSTools.GetServerAddress()
						+ "GetProductLST?query=" + params[0] + "&filters="
						+ filters);

				HttpParams httpParameters = new BasicHttpParams();
				// Set the timeout in milliseconds until a connection is
				// established.
				// The default value is zero, that means the timeout is not
				// used.
				int timeoutConnection = 3000;
				HttpConnectionParams.setConnectionTimeout(httpParameters,
						timeoutConnection);
				// Set the default socket timeout (SO_TIMEOUT)
				// in milliseconds which is the timeout for waiting for data.
				int timeoutSocket = 5000;
				HttpConnectionParams
						.setSoTimeout(httpParameters, timeoutSocket);

				DefaultHttpClient httpClient = new DefaultHttpClient(
						httpParameters);
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
					Intent intent = new Intent(
							"android.pckg.sglg.Activities.ERRORACTIVITY");
					startActivity(intent);
					Log.e(ProductListPopupActivity.class.toString(),
							"Failed to download file");

				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				Intent intent = new Intent(
						"android.pckg.sglg.Activities.ERRORACTIVITY");
				startActivity(intent);
				cancel(true);
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
					ent.put("thumb_url", AMSTools.GetServerIP() + "LPIMWS/"
							+ jsonObject.getString("img"));
					ent.put("img", jsonObject.getString("img"));
					lists.add(ent);
					AMSTools.downloadFile(AMSTools.GetServerIP() + "LPIMWS/"
							+ jsonObject.getString("img"),
							jsonObject.getString("img"), getContentResolver());
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
			ProductCore core = new ProductCore(getApplicationContext());
			// core.deleteProducts();
			for (int i = 0; i < result.size(); i++) {
				Product p = new Product();
				p.setPid(NVL.getInt(result.get(i).get("pid")));
				p.setProductName(result.get(i).get("pname"));
				p.setImg(result.get(i).get("img"));
				core.insertProduct(p);
				//
			}
			Intent intent;
			if (fromPopup) {
				finish();
				intent = new Intent(getApplicationContext(),
						ProductListPopupActivity.class);
				intent.putExtra("Key", productname.getText().toString());
				intent.putExtra("ListId", listId);
			} else {
				intent = new Intent(getApplicationContext(),
						ProductActivity.class);
			}
			startActivity(intent);
		}

	}

	private void showDialogBox(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new ReadProduct(AddProductPopupActivity.this).execute("");
			}
		});
		builder.show();
	}
}
