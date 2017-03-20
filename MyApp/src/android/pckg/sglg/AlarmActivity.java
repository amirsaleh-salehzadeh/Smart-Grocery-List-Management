package android.pckg.sglg;

import java.util.Calendar;

import android.os.Bundle;
import android.pckg.sglg.AlarmManager.AlgorithmsCalculatorReceiver;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.Menu;
import android.view.MotionEvent;

public class AlarmActivity extends Activity {

	private PendingIntent pendingIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		Intent IntentRc = getIntent();
		String postpone = IntentRc.getStringExtra("postpone");
		Calendar calendar = Calendar.getInstance();
		Calendar today = Calendar.getInstance();
		today.add(today.SECOND, 5);
		calendar.set(Calendar.MONTH, today.get(today.MONTH));
		calendar.set(Calendar.YEAR, today.get(today.YEAR));
		calendar.set(Calendar.DAY_OF_MONTH, today.get(today.DAY_OF_MONTH));

		calendar.set(Calendar.HOUR_OF_DAY, today.get(today.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, today.get(today.MINUTE));
		calendar.set(Calendar.SECOND, today.get(today.SECOND));
		calendar.set(Calendar.AM_PM, today.get(today.AM_PM));

		Intent myIntent = new Intent(AlarmActivity.this,
				AlgorithmsCalculatorReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0,
				myIntent, 0);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(),
				pendingIntent);
		if (postpone != null && postpone.equalsIgnoreCase("true")) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else if (postpone != null && postpone.equalsIgnoreCase("setting")) {
			Intent intent = new Intent(
					"android.pckg.sglg.Activities.SETTING");
			startActivity(intent);
		} else {
			Intent intent = new Intent(
					"android.pckg.sglg.Activities.LISTACTIVITY");
			startActivity(intent);
		}

	} // end onCreate

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Intent intent = new Intent("android.pckg.sglg.Activities.LISTACTIVITY");
		startActivity(intent);
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alarm, menu);
		return true;
	}

}
