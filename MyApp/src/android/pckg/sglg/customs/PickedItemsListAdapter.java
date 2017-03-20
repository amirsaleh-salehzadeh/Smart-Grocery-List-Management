package android.pckg.sglg.customs;

import java.io.BufferedReader;
import java.io.File;
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

import com.sun.org.apache.bcel.internal.generic.LCONST;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.pckg.sglg.R;
import android.pckg.sglg.Activities.ListActivity;
import android.pckg.sglg.Activities.ProductListPopupActivity;
import android.pckg.sglg.Algorithms.Merge.GenerateSuggestionList;
import android.pckg.sglg.Business.ListCore;
import android.pckg.sglg.Business.ProductCore;
import android.pckg.sglg.Business.SettingCore;
import android.pckg.sglg.Tools.AMSTools;
import android.pckg.sglg.Tools.ImageLoader;
import android.pckg.sglg.Tools.NVL;
import android.pckg.sglg.customs.SwipeDismissTouchListener.DismissCallbacks;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

public class PickedItemsListAdapter extends BaseAdapter implements ListAdapter {
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	Context context;
	String ListDetailId = "";
	View view;
	String listId = "";

	// protected MyGestureListener myGestureListener;

	public PickedItemsListAdapter(Context c,
			ArrayList<HashMap<String, String>> d, View v) {
		data = d;
		inflater = LayoutInflater.from(c);
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
			vi = inflater.inflate(R.layout.listview_items_row_list, null);

		TextView title = (TextView) vi.findViewById(R.id.items_title); // title
		ImageView thumb_image = (ImageView) vi
				.findViewById(R.id.list_items_image); // thumb
		ImageView removeImage = (ImageView) vi
				.findViewById(R.id.btn_remove_from_list);

		ImageView inbasket = (ImageView) vi.findViewById(R.id.inbasket);
		// image
		SettingCore score = new SettingCore(context);
		if (!score.getImageview()) {
			thumb_image.setVisibility(LinearLayout.GONE);
			removeImage.getLayoutParams().width = 40;
			inbasket.getLayoutParams().width = 40;
			title.getLayoutParams().height = 60;
		}
		HashMap<String, String> list = new HashMap<String, String>();
		list = data.get(position);
		ListCore lcore = new ListCore(context);
		title.setText(list.get("pname"));
		imageLoader.DisplayImage(list.get("thumb_url"), thumb_image);
		ListDetailId = list.get("ListDetailId");
		boolean inbasketbool = lcore.getInBasket(NVL.getInt(ListDetailId));
		if (inbasketbool) {
			vi.setAlpha((float) 0.51);
		} else
			inbasket.setVisibility(LinearLayout.GONE);
		listId = list.get("ListId");
		// myGestureListener = new MyGestureListener(context);
		removeImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				remove(position);
				new ReadProductHLV().execute(listId);
			}
		});
		return vi;
	}


	public void remove(int position) {
		ListCore lcore = new ListCore(context);
		lcore.deleteFromList(NVL.getInt(data.get(position).get("ListDetailId")));
		data = lcore.convertListIntoHMap(lcore.getLastList());
		notifyDataSetChanged();
		new ReadProductHLV().execute(listId);
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
				ListCore lcore = new ListCore(
						context.getApplicationContext());
				String filters = lcore.getProductsInLastList();
				httpGet = new HttpGet(AMSTools.GetServerAddress()
						+ "GetSuggestionsLST?filters=" + filters);

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
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				Intent intent = new Intent(
						"android.pckg.sglg.Activities.ERRORACTIVITY");
				
				context.startActivity(intent);
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
			setSuggestionsListView(result);
		}

	}

	public void setSuggestionsListView(ArrayList<HashMap<String, String>> result) {
		HorizontalListView listview = (HorizontalListView) view
				.findViewById(R.id.SuggestedItemsListView);
		SuggestedItemsListAdapter adapter = new SuggestedItemsListAdapter(
				context.getApplicationContext(), result, view);
		adapter.notifyDataSetChanged();
		listview.setAdapter(adapter);
	}

}