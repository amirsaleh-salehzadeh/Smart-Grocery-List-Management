package android.pckg.sglg;

import android.os.Bundle;
import android.pckg.sglg.customs.SimpleEula;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class SGLG extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new SimpleEula(this).show();
		Intent intents = new Intent("android.pckg.sglg.Activities.WELCOME");
		startActivity(intents);
//		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
