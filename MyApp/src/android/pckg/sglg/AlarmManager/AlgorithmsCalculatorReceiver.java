package android.pckg.sglg.AlarmManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class AlgorithmsCalculatorReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		if (ifilter == null) {
			ifilter = new IntentFilter();
			ifilter.addAction(Intent.ACTION_POWER_CONNECTED);
			ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
		}
		Intent batteryStatus = context.getApplicationContext()
				.registerReceiver(null, ifilter);
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
				|| status == BatteryManager.BATTERY_STATUS_FULL;
		if (!isCharging) {
			Intent i = new Intent(
					context,
					AlgorithmManagerService.class);
			i.putExtra("postpone", "true");
//			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(intent);
		} else {
			int chargePlug = batteryStatus.getIntExtra(
					BatteryManager.EXTRA_PLUGGED, -1);
			boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
			boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
			int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,
					-1);
			Intent service1 = new Intent(context,
					AlgorithmCalculatorAlarmService.class);
			context.startService(service1);
		}

	}
}