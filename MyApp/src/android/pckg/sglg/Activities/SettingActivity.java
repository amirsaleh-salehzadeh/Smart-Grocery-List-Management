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
import android.os.AsyncTask;
import android.os.Bundle;
import android.pckg.sglg.R;
import android.pckg.sglg.Welcome;
import android.pckg.sglg.AlarmManager.AlgorithmManagerService;
import android.pckg.sglg.Business.AprioriCore;
import android.pckg.sglg.Business.ProductCore;
import android.pckg.sglg.Business.SettingCore;
import android.pckg.sglg.Business.UsersCore;
import android.pckg.sglg.Common.PredictionSetting;
import android.pckg.sglg.Common.Product;
import android.pckg.sglg.Tools.AMSTools;
import android.pckg.sglg.Tools.NVL;
import android.pckg.sglg.Welcome.ReadProduct;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SettingActivity extends Activity {
	int counter;
	private LinearLayout products, prediction, profile, back; // reminder,
	private TextView Confvalue, suppvalue;// , meanvalue;
	private SeekBar confseek, suppseek;// , meanseek;
	public static final String NOTIFY_ID = "";
	ImageView productsiv, profileiv, predictioniv, globaliv, cciv, diiv,
			supportinfo, confinfo;
	ToggleButton global, imageAct, indicatorAct;
	PopupWindow popUp;
	LinearLayout layout;
	TextView tv;
	LayoutParams params;
	LinearLayout mainLayout;
	Button but;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		final SettingCore core = new SettingCore(getApplicationContext());
		counter = 0;
		global = (ToggleButton) findViewById(R.id.btn_setting_global);
		imageAct = (ToggleButton) findViewById(R.id.btn_setting_imageview);
		indicatorAct = (ToggleButton) findViewById(R.id.btn_setting_colorcode);
		back = (LinearLayout) findViewById(R.id.btn_setting_back);
		prediction = (LinearLayout) findViewById(R.id.btn_setting_predection);
		// reminder = (Button) findViewById(R.id.btn_setting_reminder);
		products = (LinearLayout) findViewById(R.id.btn_setting_products);
		profile = (LinearLayout) findViewById(R.id.btn_setting_profile);
		productsiv = (ImageView) findViewById(R.id.infoProducts);
		predictioniv = (ImageView) findViewById(R.id.infoSuggestion);
		profileiv = (ImageView) findViewById(R.id.infoProfile);
		globaliv = (ImageView) findViewById(R.id.infoGlobal);
		cciv = (ImageView) findViewById(R.id.infoColorcode);
		diiv = (ImageView) findViewById(R.id.infoImageview);

		Button homebtn = (Button) findViewById(R.id.btn_home_setting);
		homebtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(
						"android.pckg.sglg.Activities.LISTACTIVITY");
				startActivity(intent);
			}
		});

		predictioniv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AlertDialog alt_bld = new AlertDialog.Builder(
						SettingActivity.this).create();
				alt_bld.setMessage("Here you can configure the related settings which SGLG uses to predict the suggestions.");
				alt_bld.setCancelable(false);
				alt_bld.setButton("Close",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				alt_bld.show();
			}
		});
		profileiv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog alt_bld = new AlertDialog.Builder(
						SettingActivity.this).create();
				alt_bld.setMessage("Here you can edit your personal information");
				alt_bld.setCancelable(false);
				alt_bld.setButton("Close",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				alt_bld.show();
			}
		});
		globaliv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog alt_bld = new AlertDialog.Builder(
						SettingActivity.this).create();
				alt_bld.setMessage("Enabling this option will allow you to use global"
						+ " suggestions which come from analysing all SGLG user list data. "
						+ "In addition, SGLG will collect your list data and update your items "
						+ "list with the latest data inside the server.");
				alt_bld.setCancelable(false);
				alt_bld.setButton("Close",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				alt_bld.show();
			}
		});
		cciv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog alt_bld = new AlertDialog.Builder(
						SettingActivity.this).create();
				alt_bld.setMessage("Enabling this option will allow you to see the suggestions’ "
						+ "priorities by colors as well. The red products are most suggested items "
						+ "and green ones the least.");
				alt_bld.setCancelable(false);
				alt_bld.setButton("Close",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				alt_bld.show();
			}
		});
		diiv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog alt_bld = new AlertDialog.Builder(
						SettingActivity.this).create();
				alt_bld.setMessage("Enabling this option will allow SGLG to present items "
						+ "via pictures. Disabling this option will allow you to have more items "
						+ "on the search and shopping list");
				alt_bld.setCancelable(false);
				alt_bld.setButton("Close",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				alt_bld.show();
			}
		});
		productsiv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AlertDialog alt_bld = new AlertDialog.Builder(
						SettingActivity.this).create();
				alt_bld.setMessage("Here you can add a new product with or without barcode.\n "
						+ "By tapping any product, you can disable or enable its presentation on "
						+ "the search or suggestion results. In addition, you can allocate a purchase "
						+ "interval to the product if you are sure about this value. Otherwise, SGLG calculates "
						+ "it automatically. ");
				alt_bld.setCancelable(false);
				alt_bld.setButton("Close",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				alt_bld.show();
			}
		});
		profile.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(
						"android.pckg.sglg.Activities.REGISTER");
				startActivity(intent);
			}
		});
		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(
						"android.pckg.sglg.Activities.LISTACTIVITY");
				startActivity(intent);
			}
		});
		// global toggle bar settings

		global.setChecked(core.getWebserviceSettings());
		global.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			SettingCore core = new SettingCore(getApplicationContext());

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					core.updateWebserviceSetting(1);
					new ReadProduct(SettingActivity.this).execute("");
				} else {
					core.updateWebserviceSetting(0);
				}

			}
		});
		imageAct.setChecked(core.getImageview());
		imageAct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked) {
					core.changeImageview(1);
				} else {
					core.changeImageview(0);
				}

			}
		});
		indicatorAct.setChecked(core.getIndicatorView());
		indicatorAct
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean isChecked) {
						if (isChecked) {
							core.changeIndicatorview(1);
						} else {
							core.changeIndicatorview(0);
						}

					}
				});
		// setting prediction seek bars and update seek bar
		prediction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Open Popup
				LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				View popupView = layoutInflater.inflate(
						R.layout.predection_popup, null);
				final PopupWindow popupWindow = new PopupWindow(popupView,
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
				// Read setting default
				SettingCore core = new SettingCore(getApplicationContext());
				core.open();
				PredictionSetting PS = new PredictionSetting();
				PS = core.getPredictionSettings();
				supportinfo = (ImageView) popupView.findViewById(R.id.infosupport);
				confinfo = (ImageView) popupView.findViewById(R.id.infoconfiden);
				supportinfo.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						AlertDialog alt_bld = new AlertDialog.Builder(
								SettingActivity.this).create();
						alt_bld.setMessage("Support means the percentage of all items which occurs in lists \n"
								+ "For instance if you set support to 20%, then all items which make up at least 20% of the "
								+ "lists will be selected."
								+ "\n\nCaution: decreasing this value will cause large processing times while calculating new suggestions and also"
								+ " decrease the accuracy of suggestions.");
						alt_bld.setCancelable(false);
						alt_bld.setButton("Close",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						alt_bld.show();
					}
				});
				confinfo.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						AlertDialog alt_bld = new AlertDialog.Builder(
								SettingActivity.this).create();
						alt_bld.setMessage("Is the percentage of all lists in which any specific product or products are associated with any "
								+ "other specific product or products.\n"
								+ "For instance, if you set confidence to 70%, then all products which "
								+ "were associated together at least 70% of the time, and which satisfy the support value, will be suggested."
								+ "\n\nCaution: decreasing this value will cause large processing times while calculating new suggestions and also"
								+ " decrease the accuracy of suggestions.");
						alt_bld.setCancelable(false);
						alt_bld.setButton("Close",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						alt_bld.show();
					}
				});
				Confvalue = (TextView) popupView.findViewById(R.id.Confvalue);
				Confvalue.setText(PS.getConfidence() + "");
				confseek = (SeekBar) popupView
						.findViewById(R.id.seekBar_Confidence);
				confseek.setProgress(PS.getConfidence());
				confseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						Confvalue.setText(String.valueOf(progress));
					}
				});
				suppvalue = (TextView) popupView.findViewById(R.id.Suppvalue);
				suppvalue.setText(PS.getSupport() + "");
				suppseek = (SeekBar) popupView
						.findViewById(R.id.seekBar_Support);
				suppseek.setProgress(PS.getSupport());
				suppseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						suppvalue.setText(String.valueOf(progress));

					}
				});
				// meanvalue = (TextView)
				// popupView.findViewById(R.id.Meanvalue);
				// meanvalue.setText(PS.getMean() + "");
				// meanseek = (SeekBar)
				// popupView.findViewById(R.id.seekBar_Mean);
				// meanseek.setProgress(PS.getMean());
				// meanseek.setOnSeekBarChangeListener(new
				// SeekBar.OnSeekBarChangeListener() {
				//
				// @Override
				// public void onStopTrackingTouch(SeekBar seekBar) {
				// // TODO Auto-generated method stub
				//
				// }
				//
				// @Override
				// public void onStartTrackingTouch(SeekBar seekBar) {
				// // TODO Auto-generated method stub
				//
				// }
				//
				// @Override
				// public void onProgressChanged(SeekBar seekBar,
				// int progress, boolean fromUser) {
				// meanvalue.setText(String.valueOf(progress));
				//
				// }
				// });
				Button btnDismiss = (Button) popupView
						.findViewById(R.id.btn_predection_cancel);
				btnDismiss.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						popupWindow.dismiss();
					}
				});

				Button btnSave = (Button) popupView
						.findViewById(R.id.btn_prediction_save);
				btnSave.setOnClickListener(new Button.OnClickListener() {
					@Override
					public void onClick(View v) {
						PredictionSetting PS = new PredictionSetting();
						PS.setConfidence(confseek.getProgress());
						PS.setSupport(suppseek.getProgress());
						// PS.setMean(meanseek.getProgress());
						SettingCore core = new SettingCore(
								getApplicationContext());
						core.open();
						core.updatePredictionSetting(PS);
						Intent intent = new Intent(getBaseContext(),
								AlgorithmManagerService.class);
						intent.putExtra("postpone", "setting");
						startService(intent);
						popupWindow.dismiss();
					}
				});

				popupWindow.showAsDropDown(prediction, 50, -30);
				popupWindow.setBackgroundDrawable(new ColorDrawable(
						android.graphics.Color.TRANSPARENT));

			}
		});
		// reminder.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		// Intent intent = new Intent(
		// "android.pckg.sglg.Activities.REMINDER");
		// startActivity(intent);
		// }
		// });
		products.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(
						"android.pckg.sglg.Activities.PRODUCTACTIVITY");
				startActivity(intent);
			}
		});
		Bundle b = getIntent().getExtras();
		if (b != null) {
			String value = b.getString("msg");
			Toast.makeText(this, value, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent("android.pckg.sglg.Activities.LISTACTIVITY");
		startActivity(intent);
		super.onBackPressed();
	}

	public class ReadProduct extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		private ProgressDialog dialog;

		public ReadProduct(final SettingActivity welcome) {
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
					ent.put("pname", jsonObject.getString("productName"));
					ent.put("pid", jsonObject.getString("productID"));
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
//			if (result.size() > 0)
//				core.deleteProducts();
			for (int i = 0; i < result.size(); i++) {
				Product p = new Product();
				p.setPid(NVL.getInt(result.get(i).get("pid")));
				p.setProductName(result.get(i).get("pname"));
				p.setImg(result.get(i).get("img"));
				core.insertProduct(p);
			}
			dialog.dismiss();
		}

	}
}
