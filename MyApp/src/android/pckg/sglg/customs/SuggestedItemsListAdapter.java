package android.pckg.sglg.customs;

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

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.pckg.sglg.R;
import android.pckg.sglg.Activities.ListActivity;
import android.pckg.sglg.Activities.ProductListPopupActivity;
import android.pckg.sglg.Activities.ListActivity.ReadProductHLV;
import android.pckg.sglg.Algorithms.Merge.GenerateSuggestionList;
import android.pckg.sglg.Business.ListCore;
import android.pckg.sglg.Business.ProductCore;
import android.pckg.sglg.Business.SettingCore;
import android.pckg.sglg.Common.List;
import android.pckg.sglg.Tools.AMSTools;
import android.pckg.sglg.Tools.ImageLoader;
import android.pckg.sglg.Tools.NVL;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SuggestedItemsListAdapter extends BaseAdapter implements
		ListAdapter {
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	Context context;
	View view;
	private int count = 0;
	private int firClick = 0;
	private int secClick = 0;
	private int flage = 0;
	int postmp = -1;

	public SuggestedItemsListAdapter(Context c,
			ArrayList<HashMap<String, String>> d, View v) {
		data = d;
		inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(c.getApplicationContext());
		context = c;
		view = v;
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.listview_horizontal_items_row_list,
					null);
		SettingCore score = new SettingCore(context);
		LinearLayout horizontallayoutitem = (LinearLayout) vi
				.findViewById(R.id.horizontallayoutitem);

		TextView title = (TextView) vi.findViewById(R.id.items_title); // title
		ImageView thumb_image = (ImageView) vi
				.findViewById(R.id.horizontal_list_items_image); // thumb
		if (!score.getImageview()) {
			thumb_image.setVisibility(ImageView.GONE);
			LinearLayout ll = (LinearLayout) vi.findViewById(R.id.thumbnail);
			ll.getLayoutParams().height = 60;
		}
		HashMap<String, String> list = new HashMap<String, String>();
		list = data.get(position);
		if (score.getIndicatorView()) {
			String confi = list.get("confidence");
			if (confi == null || confi.equals(""))
				confi = "0";
			int conf = Math.round(Float.parseFloat(confi) * 100);
			int R = (255 * conf) / 100;
			int G = (255 * (100 - conf)) / 100;
			horizontallayoutitem.setBackgroundColor(Color.rgb(R, G, 0));
		}
		title.setText(list.get("pname"));
		// imageLoader.DisplayImage(list.get("thumb_url"), thumb_image);
		// if (!score.getWebserviceSettings()) {
		String path = Environment.getExternalStorageDirectory().toString();
		imageLoader.DisplayImage(list.get("thumb_url"), thumb_image);
		// thumb_image.setImageBitmap(BitmapFactory.decodeFile(path +
		// "/SGLG/"+list.get("thumb_url")));
		// }

		// final String pid = list.get("pid");
		// final String lid = list.get("ListId");
		// vi.setOnClickListener(itemListener);
		vi.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (MotionEvent.ACTION_DOWN == event.getAction()) {
					count++;
					if (count == 1) {
						Toast.makeText(context, "Tap Again", Toast.LENGTH_SHORT)
								.show();
						firClick = (int) System.currentTimeMillis();
						secClick = 0;
						postmp = position;
					} else if (count == 2) {
						secClick = (int) System.currentTimeMillis();
						switch (flage) {
						case 0:
							if (secClick - firClick < 6000) {//
								flage++;
							}
							if (postmp == position)
								addToList(position);
							postmp = -1;
							count = 0;
							firClick = 0;
							secClick = 0;
							return true;
						case 1:
							if (secClick - firClick < 6000) {//
								flage--;
							}
							if (postmp == position)
								addToList(position);
							postmp = -1;
							count = 0;
							firClick = 0;
							secClick = 0;
							return true;
						default:
							return true;
						}
					}
				}
				return true;
			}
		});
		return vi;
	}

	public void addToList(int pos) {
		String pid = data.get(pos).get("pid");
		final String listid = data.get(pos).get("ListId");

		ListCore core = new ListCore(context);
		core.updateDate();
		core.insertItem(Integer.parseInt(listid), Integer.parseInt(pid));
		ArrayList<HashMap<String, String>> itemsHMap = new ArrayList<HashMap<String, String>>();
		ListCore lcore = new ListCore(context);
		List itemsList = lcore.getLastList();
		itemsHMap = core.convertListIntoHMap(itemsList);
		if (itemsHMap.size() > 0) {
			final PickedItemsListAdapter adapter = new PickedItemsListAdapter(
					context.getApplicationContext(), itemsHMap, view);
			final ListView listview = (ListView) view
					.findViewById(R.id.PickedItemsListView);
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
							}
							new ReadProductHLV().execute(listid);
							adapter.notifyDataSetChanged();
						}
					});
			listview.setOnTouchListener(touchListener);
			listview.setOnScrollListener(touchListener.makeScrollListener());
		}
		notifyDataSetChanged();
		new ReadProductHLV().execute(listid);
	}

	public class ReadProductHLV extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			final ArrayList<HashMap<String, String>> lists = new ArrayList<HashMap<String, String>>();
			// listIdTxt = params[1];
			SettingCore score = new SettingCore(context.getApplicationContext());
			GenerateSuggestionList GSL = new GenerateSuggestionList(
					context.getApplicationContext());
			if (!score.getWebserviceSettings())
				return GSL.getSuggestionList(NVL.getInt(params[0]));
			try {
				HttpGet httpGet;
				ListCore lcore = new ListCore(context.getApplicationContext());
				String filters = lcore.getProductsInLastList();
				httpGet = new HttpGet(AMSTools.GetServerAddress()
						+ "GetSuggestionsLST?filters=" + filters);

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
					Log.e(ProductListPopupActivity.class.toString(),
							"Failed to download file");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				JSONArray jsonArray = new JSONArray(builder.toString());
				Log.i(ProductListPopupActivity.class.getName(),
						"Number of entries " + jsonArray.length());
				for (int i = 0; i < jsonArray.length(); i++) {
					HashMap<String, String> ent = new HashMap<String, String>();
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					ProductCore pcore = new ProductCore(context);
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
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			data = result;
			notifyDataSetChanged();
		}

	}

	public class AddToList extends AsyncTask<Integer, Void, Void> {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();

		@Override
		protected Void doInBackground(Integer... params) {

			try {
				HttpGet httpGet;
				String serviceAddress = AMSTools.GetServerAddress()
						+ "AddToList?webuid=" + params[1] + "&listid="
						+ params[0];
				httpGet = new HttpGet(serviceAddress);

				HttpResponse response = client.execute(httpGet);
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
					Log.e(ListActivity.class.toString(), "Failed");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}
}