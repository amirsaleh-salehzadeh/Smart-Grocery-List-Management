package android.pckg.sglg.Activities;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.pckg.sglg.R;
import android.pckg.sglg.Tools.AMSTools;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;

public class ReminderActivity extends Activity {
	private TimePicker timePicker1;
	private Button btnChangeTime;

	// private RadioGroup radioGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reminder);
		// radioGroup = (RadioGroup) findViewById(R.id.days);
		timePicker1 = (TimePicker) findViewById(R.id.timePicker1);
		btnChangeTime = (Button) findViewById(R.id.shopping_time_setting_Btn);

		timePicker1.setIs24HourView(true);
		String time = AMSTools.getShoppingTime();
		if (time != null && !"".equalsIgnoreCase(time)) {
			timePicker1.setCurrentHour(Integer.parseInt(time.split(":")[0]));
			timePicker1.setCurrentMinute(Integer.parseInt(time.split(":")[1]));
		} else {
			timePicker1.setCurrentHour(11);
			timePicker1.setCurrentMinute(11);
		}
		
		btnChangeTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Spinner spinner = (Spinner) findViewById(R.id.week_days);
				final String days = spinner.getSelectedItem().toString();
				final Integer h = timePicker1.getCurrentHour();
				final Integer m = timePicker1.getCurrentMinute();
				int day = onRadioButtonClicked(dayToInt(days));
				AMSTools.setShoppingTime(h, m, day);
				Bundle b = new Bundle();
				b.putString("msg", "New time sets"); // Your id
				finish();
			}
		});
		

	}

	private int dayToInt(String i){
		if (i.contains("Sun")) {
			return 6;
		} else if (i.contains("Sat")) {
			return 5;
		}else if (i.contains("Mon")) {
			return 0;
		}else if (i.contains("Tue")) {
			return 1;
		}else if (i.contains("Thu")) {
			return 3;
		}else if (i.contains("Fri")) {
			return 4;
		}else if (i.contains("Wed")) {
			return 2;
		}			
			
			return -1;
	}

	public int onRadioButtonClicked(int i) {
		// Is the button now checked?
		int day = 0;
		// Check which radio button was clicked
		switch (i) {
		case 5:
			day = Calendar.SATURDAY;
			break;
		case 6:
			day = Calendar.SUNDAY;
			break;
		case 0:
			day = Calendar.MONDAY;
			break;
		case 1:
			day = Calendar.TUESDAY;
			break;
		case 3:
			day = Calendar.THURSDAY;
			break;
		case 4:
			day = Calendar.FRIDAY;
			break;
		case 2:
			day = Calendar.WEDNESDAY;
			break;
		}
		return day;
	}

}
