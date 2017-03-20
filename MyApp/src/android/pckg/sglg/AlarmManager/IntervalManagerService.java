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
import android.os.SystemClock;
import android.pckg.sglg.AlarmActivity;
import android.pckg.sglg.R;
import android.pckg.sglg.Business.AprioriCore;

public class IntervalManagerService extends Service {

	private PendingIntent pendingIntent;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {


		super.onCreate();
	}

	@SuppressWarnings("static-access")
	@Override
	public void onStart(Intent intent, int startId) {
		

		Intent myIntent = new Intent(getBaseContext(),
				IntervalCalculatorReceiver.class);
		pendingIntent = PendingIntent.getService(getBaseContext(), 0,
				myIntent, 0);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
			    SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY,
			    AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
		super.onStart(intent, startId);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}