package android.pckg.sglg.AlarmManager;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.pckg.sglg.AlarmActivity;
import android.pckg.sglg.R;
import android.pckg.sglg.Business.AprioriCore;

public class AlgorithmManagerService extends Service {

	private PendingIntent pendingIntent;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@SuppressWarnings("static-access")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		String postpone = intent.getStringExtra("postpone");
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

		Intent myIntent = new Intent(getBaseContext(),
				AlgorithmsCalculatorReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, myIntent,
				0);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(),
				pendingIntent);
		if (postpone != null && postpone.equalsIgnoreCase("true")) {
			Intent intent2 = new Intent(Intent.ACTION_MAIN);
			intent2.addCategory(Intent.CATEGORY_HOME);
			intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent2);
		} else
		if (postpone != null && postpone.equalsIgnoreCase("setting")) {
			Intent intent3 = new Intent("android.pckg.sglg.Activities.SETTING");
			intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent3);
		} else {
			Intent intent4 = new Intent(
					"android.pckg.sglg.Activities.LISTACTIVITY");
			intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent4);
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}