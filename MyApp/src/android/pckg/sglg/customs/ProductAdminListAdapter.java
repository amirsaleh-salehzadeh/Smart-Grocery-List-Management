package android.pckg.sglg.customs;

import java.awt.Dialog;
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

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.pckg.sglg.R;
import android.pckg.sglg.Activities.ListActivity;
import android.pckg.sglg.Activities.ProductActivity;
import android.pckg.sglg.Algorithms.Apriori.Apriori;
import android.pckg.sglg.Algorithms.Merge.GenerateSuggestionList;
import android.pckg.sglg.Business.AprioriCore;
import android.pckg.sglg.Business.ListCore;
import android.pckg.sglg.Business.ProductCore;
import android.pckg.sglg.Business.SettingCore;
import android.pckg.sglg.Business.UsersCore;
import android.pckg.sglg.Common.List;
import android.pckg.sglg.Common.Product;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ProductAdminListAdapter extends BaseAdapter implements ListAdapter {
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	Context context;

	public ProductAdminListAdapter(Context c, Activity a,
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
		LinearLayout imageLO = (LinearLayout) vi
				.findViewById(R.id.thumbnail_prodList);
		if (!score.getImageview())
			imageLO.setVisibility(LinearLayout.GONE);
		TextView title = (TextView) vi.findViewById(R.id.title); // title
		ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image); // thumb
																				// image
		HashMap<String, String> list = new HashMap<String, String>();
		list = data.get(position);
		title.setText(list.get("pname"));
		imageLoader.DisplayImage(list.get("thumb_url"), thumb_image);
		final String pid = list.get("pid");
		final String pname = list.get("pname");
		final String lid = list.get("listid");

		OnClickListener itemListener = new OnClickListener() {
			public void onClick(View v) {
				ProductCore pcore = new ProductCore(context);
				inflater = (LayoutInflater) context
						.getSystemService(context.LAYOUT_INFLATER_SERVICE);
				showDialogBox(pname, pid);

			}
		};
		vi.setOnClickListener(itemListener);
		return vi;
	}

	private void showDialogBox(String name, final String pid) {
		InputMethodManager in = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		in.hideSoftInputFromWindow(null, InputMethodManager.HIDE_NOT_ALWAYS);
		final View layout = inflater.inflate(
				R.layout.product_selection_dialog_box, null);
		final ProductCore pcore = new ProductCore(context);
		Product p = pcore.getProduct(NVL.getInt(pid));
		final EditText nameBox = (EditText) layout
				.findViewById(R.id.product_edit_text_days);
		TextView itemName = (TextView) layout.findViewById(R.id.items_title);
		itemName.setText(p.getProductName());
		if (p.getInterval() > 0)
			nameBox.setText(p.getInterval() + "");
		ImageView img = (ImageView) layout.findViewById(R.id.product_image);
		imageLoader.DisplayImage(p.getImg(), img);
		// AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// builder.setView(layout);

		ToggleButton toggleVisibility = (ToggleButton) layout
				.findViewById(R.id.productvisibilityonoff);
		toggleVisibility
				.setChecked(pcore.getProductVisibility(NVL.getInt(pid)));

		ToggleButton togglemanual = (ToggleButton) layout
				.findViewById(R.id.productmanualonoff);
		togglemanual
				.setChecked(!pcore.getProductManualInterval(NVL.getInt(pid)));
		final LinearLayout ll = (LinearLayout) layout
				.findViewById(R.id.intervallayout);
		if (!pcore.getProductManualInterval(p.getPid()))
			ll.setVisibility(View.GONE);
		toggleVisibility
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							pcore.updateProductVisibility((NVL.getInt(pid)), 1);
						} else {
							pcore.updateProductVisibility((NVL.getInt(pid)), 0);
						}

					}
				});
		togglemanual
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (!isChecked) {
							pcore.updateProductIntervalMode((NVL.getInt(pid)),
									1);
							ll.setVisibility(View.VISIBLE);
						} else {
							pcore.updateProductIntervalMode((NVL.getInt(pid)),
									0);
							ll.setVisibility(View.GONE);
						}

					}
				});

		final AlertDialog builder = new AlertDialog.Builder(context).create();
		builder.setView(layout);
		Button button = (Button) layout.findViewById(R.id.save);
		button.setOnClickListener(new OnClickListener() {
			ProductCore pcore = new ProductCore(context);

			@Override
			public void onClick(View v) {
				// if (pcore.getProductManualInterval((NVL.getInt(pid)))) {
				if (pcore.getProductManualInterval(NVL.getInt(pid)) && (nameBox.getText().toString().matches("")
						|| nameBox.getText().toString().matches("0"))) {
					Toast.makeText(context.getApplicationContext(),
							"please choose interval", Toast.LENGTH_LONG).show();
					return;
				} else {
					pcore.updateProductInterval((NVL.getInt(pid)),
							NVL.getInt(nameBox.getText().toString()));
					Toast.makeText(context.getApplicationContext(),
							"Saved successfully", Toast.LENGTH_LONG).show();
					builder.dismiss();
				}
				// } else {
				// Toast.makeText(context.getApplicationContext(),
				// "Please check the manual checkbox",
				// Toast.LENGTH_LONG).show();
				// return;
				// }
			}
		});
		Button buttoncancel = (Button) layout.findViewById(R.id.cancel);
		buttoncancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				builder.dismiss();
			}
		});
		builder.show();

	}

	class CustomListener implements View.OnClickListener {
		private final Dialog dialog;

		public CustomListener(Dialog dialog) {
			this.dialog = dialog;
		}

		@Override
		public void onClick(View v) {

			// Do whatever you want here

			// If tou want to close the dialog, uncomment the line below
			// dialog.dismiss();
		}
	}
}