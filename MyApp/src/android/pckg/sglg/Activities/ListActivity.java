package android.pckg.sglg.Activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javafx.beans.binding.ListBinding;

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

import com.sun.org.apache.bcel.internal.generic.LCONST;

import sun.text.UCompactIntArray;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.pckg.sglg.AlarmActivity;
import android.pckg.sglg.R;
import android.pckg.sglg.Welcome;
import android.pckg.sglg.AlarmManager.AlgorithmManagerService;
import android.pckg.sglg.AlarmManager.AlgorithmsCalculatorReceiver;
import android.pckg.sglg.Algorithms.Merge.GenerateSuggestionList;
import android.pckg.sglg.Business.AprioriCore;
import android.pckg.sglg.Business.ListCore;
import android.pckg.sglg.Business.ProductCore;
import android.pckg.sglg.Business.SettingCore;
import android.pckg.sglg.Business.UsersCore;
import android.pckg.sglg.Common.List;
import android.pckg.sglg.Tools.AMSTools;
import android.pckg.sglg.Tools.NVL;
import android.pckg.sglg.customs.HorizontalListView;
import android.pckg.sglg.customs.PickedItemsListAdapter;
import android.pckg.sglg.customs.SuggestedItemsListAdapter;
import android.pckg.sglg.customs.SwipeDismissListViewTouchListener;
import android.pckg.sglg.zxing.integration.IntentIntegrator;
import android.pckg.sglg.zxing.integration.IntentResult;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ListActivity extends Activity {
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Intent intent = new Intent("android.pckg.sglg.Activities.SETTING");
		startActivity(intent);
		return super.onKeyDown(keyCode, event);
	}

	String productName = "";
	protected static final int REQUEST_OK = 1;
	public static String value = "";
	Button setting, add, smart, voice, barcode, newlist;// , imageAct,
	// indicatorAct;
	TextView txtlistno, txtdate;
	String listId;
	int suggestedheight;
	String listIdTxt, ListIdDate;
	List itemsList = new List();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		final ListCore core = new ListCore(getApplicationContext());
		final SettingCore score = new SettingCore(getApplicationContext());
		listId = core.getLastList().getListId() + "";
		ListIdDate = core.getLastList().getDate();
		setting = (Button) findViewById(R.id.btn_setting);
		add = (Button) findViewById(R.id.btn_addToList);
		newlist = (Button) findViewById(R.id.btn_new_list);
		barcode = (Button) findViewById(R.id.btn_barcode);
		voice = (Button) findViewById(R.id.btn_voice);
		// txtlistno = (TextView) findViewById(R.id.txt_list_no);
		txtdate = (TextView) findViewById(R.id.txt_list_date);
		Button exitbtn = (Button) findViewById(R.id.btn_exit);
		exitbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
		LinearLayout horizontalLO = (LinearLayout) findViewById(R.id.newbtnlo);
		LayoutParams lp = (LayoutParams) horizontalLO.getLayoutParams();
		if (!score.getImageview())
			lp.height = 160;
		// imageAct = (Button) findViewById(R.id.btn_imageview);
		// indicatorAct = (Button) findViewById(R.id.btn_indicator);
		// if (score.getImageview())
		// imageAct.setCompoundDrawablesWithIntrinsicBounds(
		// R.drawable.image_activate, 0, 0, 0);
		// else
		// imageAct.setCompoundDrawablesWithIntrinsicBounds(
		// R.drawable.image_deactive, 0, 0, 0);
		// if (score.getIndicatorView())
		// indicatorAct.setCompoundDrawablesWithIntrinsicBounds(
		// R.drawable.performance, 0, 0, 0);
		// else
		// indicatorAct.setCompoundDrawablesWithIntrinsicBounds(
		// R.drawable.performancedeact, 0, 0, 0);
		// imageAct.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (score.getImageview()) {
		// score.changeImageview(0);
		// } else {
		// score.changeImageview(1);
		// }
		// Intent intent = new Intent(
		// "android.pckg.sglg.Activities.LISTACTIVITY");
		// startActivity(intent);
		// }
		// });
		//
		// indicatorAct.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (score.getIndicatorView()) {
		// score.changeIndicatorview(0);
		// } else {
		// score.changeIndicatorview(1);
		// }
		// Intent intent = new Intent(
		// "android.pckg.sglg.Activities.LISTACTIVITY");
		// startActivity(intent);
		// }
		// });

		newlist.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				itemsList = core.getLastList();
				if (itemsList.getListDetails().size() <= 0) {
					Intent intent = new Intent(getApplicationContext(),
							ProductListPopupActivity.class);
					intent.putExtra("ListId", listId);
					startActivity(intent);
				} else {

					AlertDialog builder = new AlertDialog.Builder(
							ListActivity.this).create();
					builder.setTitle("Confirmation");
					builder.setMessage("Are you sure you want to create new list?");
					builder.setButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									UsersCore ucore = new UsersCore(
											getApplicationContext());
									ListCore lcore = new ListCore(
											getApplicationContext());
									// Intent intent = new Intent(
									// "android.pckg.sglg.Activities.ALARMACTIVITY");
									Intent intent = new Intent(
											getBaseContext(),
											AlgorithmManagerService.class);
									startService(intent);
									new newList().execute(ucore.getUser(1)
											.getWebuid() + "",
											lcore.getProductsInLastList());
									core.newlist();
									ProductCore pcore = new ProductCore(
											getApplicationContext());
									pcore.updateAllProductsIntervals();
									pcore.updateProductsConfidence();
								}
							});
					builder.setButton2("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							});
					builder.show();
					return;
				}
			}
		});
		// // Barcode click
		barcode.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (v.getId() == R.id.btn_barcode) {
					IntentIntegrator scanIntegrator = new IntentIntegrator(
							ListActivity.this);
					scanIntegrator.initiateScan();
				}
			}
		});
		// Voice click
		voice.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
				try {
					startActivityForResult(i, REQUEST_OK);
				} catch (Exception e) {

				}
			}
		});
		// Setting click
		setting.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(
						"android.pckg.sglg.Activities.SETTING");
				startActivity(intent);
			}
		});
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Open Popup
				Intent intent = new Intent(getApplicationContext(),
						ProductListPopupActivity.class);
				intent.putExtra("ListId", listId);
				startActivity(intent);

			}

		});
		// Create local list
		Date date;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String dateInString = ListIdDate;
			date = formatter.parse(dateInString);
			formatter = new SimpleDateFormat("yyyy-MM-dd");
			ListIdDate = formatter.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Format formatter = new SimpleDateFormat("EEEE dd MMMM");// yyyy,
																// hh:mm:ss.SSS
																// a
		String today = formatter.format(new Date());
		System.out.println("Today : " + today);
		txtdate.setText(today);
		ListCore lcore = new ListCore(getApplicationContext());
		ArrayList<HashMap<String, String>> itemsHMap = new ArrayList<HashMap<String, String>>();
		itemsList = lcore.getLastList();
		itemsHMap = core.convertListIntoHMap(itemsList);
		new ReadProductHLV().execute(listId);
		if (itemsHMap.size() > 0)
			setListView(itemsHMap);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	public void setListView(ArrayList<HashMap<String, String>> result) {
		final PickedItemsListAdapter adapter = new PickedItemsListAdapter(
				getApplicationContext(), result, getWindow().getDecorView()
						.findViewById(android.R.id.content));
		final ListView listview = (ListView) findViewById(R.id.PickedItemsListView);
		listview.setAdapter(adapter);
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				listview,
				new SwipeDismissListViewTouchListener.DismissCallbacks() {
					@Override
					public boolean canDismiss(int position) {
						return true;
					}

					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							adapter.remove(position);
							new ReadProductHLV().execute(listId);
						}
						adapter.notifyDataSetChanged();

					}
				});

		listview.setOnTouchListener(touchListener);
		listview.setOnScrollListener(touchListener.makeScrollListener());
	}

	// On voice and barcode results
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, data);
		Intent intent = new Intent(getApplicationContext(),
				ProductListPopupActivity.class);
		// Barcode result
		boolean barcode = false;
		if (scanningResult != null) {
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			new GetProductBarcode().execute(scanContent, scanFormat);
			barcode = true;
		} else {
			intent.putExtra("Key", value);
			intent.putExtra("BarcodeType", "");
			intent.putExtra("Technique", "");
			intent.putExtra("ListId", listId);
		}
		// Voice Result
		if (requestCode == REQUEST_OK && resultCode == RESULT_OK) {
			ArrayList<String> thingsYouSaid = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			intent.putExtra("Key", thingsYouSaid.get(0));
			intent.putExtra("BarcodeType", "");
			intent.putExtra("Technique", "Voice");
			intent.putExtra("ListId", listId);
		} else
			barcode = true;
		if (!barcode)
			startActivity(intent);
	}

	public class ReadProductHLV extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			ArrayList<HashMap<String, String>> lists = new ArrayList<HashMap<String, String>>();
			// listIdTxt = params[1];
			SettingCore score = new SettingCore(getApplicationContext());
			GenerateSuggestionList GSL = new GenerateSuggestionList(
					getApplicationContext());
			if (!score.getWebserviceSettings())
				return GSL.getSuggestionList(NVL.getInt(params[0]));
			try {
				HttpGet httpGet;
				ListCore lcore = new ListCore(getApplicationContext());
				String filters = lcore.getFilteredProductsInLastList();
				httpGet = new HttpGet(AMSTools.GetServerAddress()
						+ "GetSuggestionsLST?filters="
						+ AMSTools.avoidDupInComma(filters));

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
				e.printStackTrace();
			}

			try {
				JSONArray jsonArray = new JSONArray(builder.toString());
				Log.i(ProductListPopupActivity.class.getName(),
						"Number of entries " + jsonArray.length());
				for (int i = 0; i < jsonArray.length(); i++) {
					HashMap<String, String> ent = new HashMap<String, String>();
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					ProductCore pcore = new ProductCore(getApplicationContext());
					ent.put("pname", jsonObject.getString("productName"));
					ent.put("pid", jsonObject.getString("productID"));
					ent.put("ListId", params[0]);
					ent.put("confidence", jsonObject.getString("confidence"));
					ent.put("thumb_url",
							pcore.getProduct(
									NVL.getInt(jsonObject
											.getString("productID"))).getImg());
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
		protected void onPreExecute() {
			ProductCore pcore = new ProductCore(getApplicationContext());
			ListCore lcore = new ListCore(getApplicationContext());
			final String filters = lcore.getFilteredProductsInLastList();
			HorizontalListView listview = (HorizontalListView) findViewById(R.id.SuggestedItemsListView);
			final SuggestedItemsListAdapter adapter = new SuggestedItemsListAdapter(
					getApplicationContext(), pcore.searchProduct(productName,
							listIdTxt, AMSTools.avoidDupInComma(filters)),
					getWindow().getDecorView().findViewById(
							android.R.id.content));
			listview.setAdapter(adapter);
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			HorizontalListView listview = (HorizontalListView) findViewById(R.id.SuggestedItemsListView);
			final SuggestedItemsListAdapter adapter = new SuggestedItemsListAdapter(
					getApplicationContext(), result, getWindow().getDecorView()
							.findViewById(android.R.id.content));
			listview.setAdapter(adapter);
		}
	}

	public class GetProductBarcode extends
			AsyncTask<String, Void, HashMap<String, String>> {
		String pName = "";
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();

		@Override
		protected HashMap<String, String> doInBackground(String... params) {

			try {
				HttpGet httpGet;
				String serviceAddress = AMSTools.GetServerAddress()
						+ "GetProductFromBarcode?barcodeNo=" + params[0]
						+ "&barcodeFormat=" + params[1];
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

			try {
				JSONArray jsonArray = new JSONArray(builder.toString());
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					pName = jsonObject.getString("productName");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			HashMap<String, String> ent = new HashMap<String, String>();
			ent.put("productName", pName);
			ent.put("barcodeNo", params[0]);
			ent.put("barcodeType", params[1]);
			return ent;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			value = (String) result.get("productName");
			Intent intent = new Intent(getApplicationContext(),
					ProductListPopupActivity.class);
			intent.putExtra("Key", value);
			intent.putExtra("BarcodeType", result.get("barcodeType"));
			intent.putExtra("barcodeNo", result.get("barcodeNo"));
			intent.putExtra("Technique", "Barcode");
			intent.putExtra("ListId", listId);
			if (!value.equals(""))
				startActivity(intent);
			else
				Toast.makeText(getApplicationContext(),
						"Problem with barcode value", Toast.LENGTH_LONG);
			super.onPostExecute(result);
		}

	}

	public class newList extends AsyncTask<String, Void, Void> {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();

		@Override
		protected Void doInBackground(String... params) {

			try {
				HttpGet httpGet;
				String serviceAddress = AMSTools.GetServerAddress()
						+ "CreateNewList?webuid=" + params[0] + "&pids="
						+ params[1];
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
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

	}

}
