package android.pckg.sglg.Activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
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

import android.os.AsyncTask;
import android.os.Bundle;
import android.pckg.sglg.R;
import android.pckg.sglg.R.string;
import android.pckg.sglg.Tools.AMSTools;
import android.pckg.sglg.Tools.NVL;
import android.pckg.sglg.Business.UsersCore;
import android.pckg.sglg.Common.User;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	Button BTNRegister, BTNBack;
	EditText nickName;
	DatePicker datePicker;
	String date;
	RadioGroup gender;
	String gendertext;
	RadioButton genderRBTN;
	int genderno;
	User pubUser = new User();
	Intent intent;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		UsersCore core = new UsersCore(getApplicationContext());
		User user = new User();
		core.open();
		user = core.getUser(1);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		datePicker = (DatePicker) findViewById(R.id.datePicker);
		nickName = (EditText) findViewById(R.id.input_nickname);
		gender = (RadioGroup) findViewById(R.id.radioGroup_gender);
		Button homebtn = (Button) findViewById(R.id.btn_home_register);
		Button settingbtn = (Button) findViewById(R.id.btn_setting);
		settingbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(
						"android.pckg.sglg.Activities.SETTING");
				startActivity(intent);
				
			}
		});
		homebtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("android.pckg.sglg.Activities.LISTACTIVITY");
				startActivity(intent);
			}
		});

		if (user.getGender() == 1)
			gendertext = "Male";
		else
			gendertext = "Female";
		datePicker.updateDate(1990, 0, 1);
		if (user.getNickName() != null) {
			nickName.setText(user.getNickName());
			datePicker.updateDate(
					Integer.parseInt(user.getDob().split("/")[0]),
					Integer.parseInt(user.getDob().split("/")[1]),
					Integer.parseInt(user.getDob().split("/")[2]));
		}
		pubUser = user;
		int count = gender.getChildCount();
		for (int i = 0; i < count; i++) {
			View o = gender.getChildAt(i);
			if (o instanceof RadioButton) {
				RadioButton genderRBTN = (RadioButton) o;
				if (genderRBTN.getText().toString()
						.equalsIgnoreCase(gendertext)) {
					genderRBTN.setChecked(genderRBTN.isChecked());
					gender.check(genderRBTN.getId());
				}
			}
		}
		try {
			Field f[] = datePicker.getClass().getDeclaredFields();
			for (Field field : f) {
				if (field.getName().equals("mCalendarView")) {
					field.setAccessible(true);
					Object calendarPicker = new Object();
					calendarPicker = field.get(datePicker);
					((View) calendarPicker).setVisibility(View.GONE);
				}
			}
			BTNRegister = (Button) findViewById(R.id.btn_register);
			
			BTNBack = (Button) findViewById(R.id.btn_register_canel);
			RelativeLayout rl = (RelativeLayout) findViewById(R.id.titlelo);
			if (user.getNickName() == null){
				BTNBack.setVisibility(View.GONE);
				rl.setVisibility(View.GONE);
				BTNRegister.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			}
			BTNBack.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					intent = new Intent(
							"android.pckg.sglg.Activities.SETTING");
					startActivity(intent);
				}
			});
			BTNRegister.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (nickName.getText().toString().matches("")) {
						Toast.makeText(getApplicationContext(),
								"Nick name cannot be empty", Toast.LENGTH_LONG)
								.show();
						return;
					}
					int genderid = gender.getCheckedRadioButtonId();
					genderRBTN = (RadioButton) findViewById(genderid);
					if (genderRBTN.getText().equals("Male"))
						genderno = 1;
					else
						genderno = 0;
					date = datePicker.getYear() + "/" + datePicker.getMonth()
							+ "/" + datePicker.getDayOfMonth();
					User userT = new User();
					userT.setDob(date);
					userT.setNickName(nickName.getText().toString());
					userT.setGender(genderno);
					UsersCore core = new UsersCore(getApplicationContext());
					core.open();

					if (pubUser.getNickName() == null) {
						core.insertUser(userT);
						core.open();
						userT = core.getUser(1);
						intent = new Intent(
								"android.pckg.sglg.Activities.LISTACTIVITY");
					} else {
						userT.setUid(1);
						core.updateUser(userT);

						intent = new Intent(
								"android.pckg.sglg.Activities.SETTING");
					}
					core.close();
					core.open();
					User x = core.getUser(1);
					TelephonyManager tMgr =(TelephonyManager)getSystemService(getApplicationContext().TELEPHONY_SERVICE);
				    x.setTel(tMgr.getLine1Number());
					new InsertOrUpdateUseer().execute(x.getNickName(),
							x.getTel(), x.getUid() + "", x.getWebuid() + "",
							x.getGender() + "", x.getDob());

				}
			});
		} catch (SecurityException e) {
			Log.d("ERROR", e.getMessage());
		} catch (IllegalArgumentException e) {
			Log.d("ERROR", e.getMessage());
		} catch (IllegalAccessException e) {
			Log.d("ERROR", e.getMessage());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	
	// Convert barcode into product name and then sets a variable
	public class InsertOrUpdateUseer extends
			AsyncTask<String, Void, HashMap<String, String>> {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();

		@Override
		protected HashMap<String, String> doInBackground(String... params) {

			try {
				HttpGet httpGet;
				String serviceAddress = AMSTools.GetServerAddress()
						+ "InsertOrUpdateUser?nickName=" + params[0] + "&tel="
						+ params[1] + "&uid=" + params[2] + "&webuid="
						+ params[3] + "&gender=" + params[4] + "&dob="
						+ params[5];
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
			String webuid = "";
			String id = "";
			try {
				JSONArray jsonArray = new JSONArray(builder.toString());
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					webuid = jsonObject.getString("UID");
					id = jsonObject.getString("localUID");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			HashMap<String, String> ent = new HashMap<String, String>();
			ent.put("webuid", webuid);
			ent.put("id", id);
			return ent;
		}

		@Override
		protected void onPostExecute(HashMap<String, String> result) {
			UsersCore core = new UsersCore(getApplicationContext());
			core.open();
			User user = core.getUser(NVL.getInt(result.get("id")));
			user.setWebuid(NVL.getInt(result.get("webuid")));
			core.close();
			core.open();
			core.updateUser(user);
			startActivity(intent);
			super.onPostExecute(result);
		}
	}

}
