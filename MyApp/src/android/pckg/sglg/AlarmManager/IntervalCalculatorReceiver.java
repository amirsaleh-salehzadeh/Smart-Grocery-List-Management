package android.pckg.sglg.AlarmManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.pckg.sglg.AlarmActivity;
import android.pckg.sglg.R;
import android.pckg.sglg.Business.ProductCore;

public class IntervalCalculatorReceiver extends Service {
	private NotificationManager mManager;


	@Override
	public IBinder onBind(Intent intent) {
		
//		getApplicationContext().startService(service1);
		return null;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		ProductCore pcore = new ProductCore(getApplicationContext());
		pcore.updateProductsConfidence();
//		Intent service1 = new Intent(getApplicationContext(), IntervalManagerService.class);
		mManager = (NotificationManager) getApplicationContext()
				.getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
		Intent intent1 = new Intent(getApplicationContext(),
				AlarmActivity.class);

		Notification notification = new Notification(R.drawable.ic_launcher,
				"SGLG updated successfully", System.currentTimeMillis());
		intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pendingNotificationIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, intent1,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(getApplicationContext(), "SGLG Update",
				"SGLG updated intervals successfully",
				pendingNotificationIntent);
		mManager.notify(0, notification);
		return super.onStartCommand(intent, flags, startId);
	}
}