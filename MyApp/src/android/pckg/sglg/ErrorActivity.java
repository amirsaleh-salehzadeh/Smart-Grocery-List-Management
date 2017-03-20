package android.pckg.sglg;

import android.os.Bundle;
import android.pckg.sglg.Business.SettingCore;
import android.pckg.sglg.Business.UsersCore;
import android.pckg.sglg.Common.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MotionEvent;

public class ErrorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Server Not Found");
		builder.setMessage("Problem in connection with server. ");
		builder.setNegativeButton("Try again",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								"android.pckg.sglg.Activities.WELCOME");
						startActivity(intent);
					}
				});
		final String btnTxt = "";
		String btn = "";
		final SettingCore score = new SettingCore(getApplicationContext());
		UsersCore core = new UsersCore(getApplicationContext());

		core.open();
		User user = new User();
		user = core.getUser(1);
		core.close();
		if (user.getNickName() != null) {

			if (score.getWebserviceSettings())
				btn = "Off";
			else
				btn = "On";
			btn = btnTxt;
			builder.setPositiveButton("Turn off connection",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (btnTxt.equals("Off")) {
								score.updateWebserviceSetting(1);
							} else {
								score.updateWebserviceSetting(0);
							}
							Intent intent = new Intent(
									"android.pckg.sglg.Activities.WELCOME");
							startActivity(intent);
						}
					});
		} else {
			builder.setNeutralButton("Exit",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(Intent.ACTION_MAIN);
							intent.addCategory(Intent.CATEGORY_HOME);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);

						}
					});
		}
		builder.show();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final Intent launchIntent = new Intent(
				"android.pckg.sglg.Activities.WELCOME");
		startActivity(launchIntent);
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.error, menu);
		return true;
	}

}
