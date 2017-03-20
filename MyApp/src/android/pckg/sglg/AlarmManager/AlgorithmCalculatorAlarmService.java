package android.pckg.sglg.AlarmManager;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import android.pckg.sglg.Business.ListCore;

public class AlgorithmCalculatorAlarmService extends Service {

	private NotificationManager mManager;

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

	ExecutorService executor = Executors.newSingleThreadExecutor();

	@SuppressWarnings("static-access")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		// new updateConfidence().execute();
		ListCore lcore = new ListCore(getApplicationContext());
		String[] transactions = lcore.getAllTransactions();
		if (transactions.length == 0)
			return;

//		int nbThreads = Thread.getAllStackTraces().keySet().size();
//
//		int nbRunning = 0;
//		for (Thread t : Thread.getAllStackTraces().keySet()) {
//			if (t.getState() == Thread.State.RUNNABLE)
//				nbRunning++;
//		}
//		if (nbRunning > 0)
//			executor.shutdown();
		executor = Executors.newCachedThreadPool();
		Runnable taskOne = new runUpdate();
		executor.execute(taskOne);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private class runUpdate implements Runnable {

		@Override
		public void run() {
			AprioriCore acore = new AprioriCore(getApplicationContext());
			acore.calculateAssociationRules();
			mManager = (NotificationManager) getApplicationContext()
					.getSystemService(
							getApplicationContext().NOTIFICATION_SERVICE);
			Intent intent1 = new Intent(getApplicationContext(),
					AlarmActivity.class);

			Notification notification = new Notification(
					R.drawable.ic_launcher, "SGLG updated lists successfully",
					System.currentTimeMillis());
			intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);

			PendingIntent pendingNotificationIntent = PendingIntent
					.getActivity(getApplicationContext(), 0, intent1,
							PendingIntent.FLAG_UPDATE_CURRENT);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.setLatestEventInfo(getApplicationContext(),
					"SGLG Update", "SGLG updated frequent items successfully",
					pendingNotificationIntent);
			mManager.notify(0, notification);
			executor.shutdown();
		}

	}

	public class updateConfidence extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			AprioriCore acore = new AprioriCore(getApplicationContext());
			acore.calculateAssociationRules();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mManager = (NotificationManager) getApplicationContext()
					.getSystemService(
							getApplicationContext().NOTIFICATION_SERVICE);
			Intent intent1 = new Intent(getApplicationContext(),
					AlarmActivity.class);

			Notification notification = new Notification(
					R.drawable.ic_launcher, "SGLG updated lists successfully",
					System.currentTimeMillis());
			intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);

			PendingIntent pendingNotificationIntent = PendingIntent
					.getActivity(getApplicationContext(), 0, intent1,
							PendingIntent.FLAG_UPDATE_CURRENT);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.setLatestEventInfo(getApplicationContext(),
					"SGLG Update", "SGLG updated suggestions successfully",
					pendingNotificationIntent);
			mManager.notify(0, notification);
			super.onPostExecute(result);
		}

	}

}