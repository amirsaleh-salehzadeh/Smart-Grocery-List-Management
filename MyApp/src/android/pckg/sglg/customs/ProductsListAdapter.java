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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.pckg.sglg.R;
import android.pckg.sglg.Activities.ListActivity;
import android.pckg.sglg.Algorithms.Apriori.Apriori;
import android.pckg.sglg.Algorithms.Merge.GenerateSuggestionList;
import android.pckg.sglg.Business.ListCore;
import android.pckg.sglg.Business.SettingCore;
import android.pckg.sglg.Business.UsersCore;
import android.pckg.sglg.Common.List;
import android.pckg.sglg.Common.User;
import android.pckg.sglg.Tools.AMSTools;
import android.pckg.sglg.Tools.ImageLoader;
import android.pckg.sglg.Tools.NVL;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ProductsListAdapter extends BaseAdapter implements ListAdapter {
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	Context context;

	public ProductsListAdapter(Context c, Activity a,
			ArrayList<HashMap<String, String>> d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
		context = c;
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

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.listview_product_row_list, null);
		
		SettingCore score = new SettingCore(context);
		LinearLayout imageLO = (LinearLayout) vi.findViewById(R.id.thumbnail_prodList);
		if(!score.getImageview())
			imageLO.setVisibility(LinearLayout.GONE);
		TextView title = (TextView) vi.findViewById(R.id.title); // title
		ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image); // thumb
																				// image
		HashMap<String, String> list = new HashMap<String, String>();
		list = data.get(position);
		title.setText(list.get("pname"));
		imageLoader.DisplayImage(list.get("thumb_url"), thumb_image);
		final String pid = list.get("pid");
		final String lid = list.get("listid");
		OnClickListener itemListener = new OnClickListener() {
			public void onClick(View v) {
				inflater = (LayoutInflater) context
						.getSystemService(context.LAYOUT_INFLATER_SERVICE);
				addToList(pid, lid);
			}
		};
		vi.setOnClickListener(itemListener);
		return vi;
	}

	private void addToList(String pid, String listid) {
		ListCore core = new ListCore(context);
		core.updateDate();
		core.insertItem(Integer.parseInt(listid), Integer.parseInt(pid));
		Intent intent = new Intent("android.pckg.sglg.Activities.LISTACTIVITY");
		context.startActivity(intent);
	}

	public class AddToList extends
			AsyncTask<Integer, Void, Void> {
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