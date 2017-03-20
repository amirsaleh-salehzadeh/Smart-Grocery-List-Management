package android.pckg.sglg;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.pckg.sglg.Activities.ProductListPopupActivity;
import android.pckg.sglg.AlarmManager.AlgorithmManagerService;
import android.pckg.sglg.AlarmManager.IntervalManagerService;
import android.pckg.sglg.Business.ListCore;
import android.pckg.sglg.Business.ProductCore;
import android.pckg.sglg.Business.SettingCore;
import android.pckg.sglg.Business.UsersCore;
import android.pckg.sglg.Common.Product;
import android.pckg.sglg.Common.User;
import android.pckg.sglg.Tools.AMSTools;
import android.pckg.sglg.Tools.NVL;
import android.pckg.sglg.Tools.TypefaceUtil;
import android.pckg.sglg.customs.SimpleEula;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Welcome extends Activity {
	Intent intent;
	Bitmap bmImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		ImageView exitbtn = (ImageView) findViewById(R.id.btn_exitwelcome);
		
		exitbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
		TypefaceUtil.overrideFont(getApplicationContext(), "SERIF",
				"fonts/Roboto-Regular.ttf");
		if (!isOnline()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Internet Connection Problem!");
			builder.setMessage("Please check your internet connection!");
			builder.setNeutralButton("Exit",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(Intent.ACTION_MAIN);
							intent.addCategory(Intent.CATEGORY_HOME);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);

						}
					});

			builder.setNegativeButton("Try again",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent launchIntent = new Intent(
									"android.pckg.sglg.Activities.WELCOME");
							startActivity(launchIntent);
						}
					});

			builder.setPositiveButton("Setting",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							startActivityForResult(
									new Intent(
											android.provider.Settings.ACTION_WIRELESS_SETTINGS),
									0);
						}
					});
			builder.show();
			return;
		}
		UsersCore core = new UsersCore(getApplicationContext());

		SettingCore score = new SettingCore(getApplicationContext());
		core.open();
		User user = new User();
		user = core.getUser(1);
		core.close();
		if (user.getNickName() != null) {
			intent = new Intent("android.pckg.sglg.Activities.LISTACTIVITY");

		} else {
			intent = new Intent("android.pckg.sglg.Activities.REGISTER");
		}

		// createSampleData();
		Intent intentservice = new Intent(
				getBaseContext(),
				IntervalManagerService.class);
		startService(intentservice);

		if (score.getWebserviceSettings())
			new ReadProduct(Welcome.this).execute("");
		else
			startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final Intent launchIntent = new Intent(
				"android.pckg.sglg.Activities.WELCOME");
		startActivity(launchIntent);
		return super.onTouchEvent(event);
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public class ReadProduct extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		private ProgressDialog dialog;

		public ReadProduct(final Welcome welcome) {
			runOnUiThread(new Runnable() {
				public void run() {
					dialog = new ProgressDialog(welcome);
				}
			});
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			runOnUiThread(new Runnable() {
				public void run() {
					dialog.setMessage("Downloading Data From SGLG, Please Wait...");
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
				
				
//				HttpResponse response = client.execute(httpGet);
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
			dialog.dismiss();
			startActivity(intent);
		}

	}

	public void createSampleData() {
		String[] p = new String[] { "1,2,5", "2,4", "2,3", "1,2,4", "1,3",
				"2,3", "1,3", "1,2,3,5", "1,2,3" };
		for (int i = 0; i < p.length; i++) {
			ListCore lcore = new ListCore(getApplicationContext());
			for (int j = 0; j < p[i].split(",").length; j++) {
				lcore.insertItem(lcore.getLastList().getListId(),
						NVL.getInt(p[i].split(",")[j]));
			}
			lcore.newlist();
		}
	}
}
