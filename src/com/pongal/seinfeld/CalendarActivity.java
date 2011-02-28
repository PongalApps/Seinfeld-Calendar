package com.pongal.seinfeld;

import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

public class CalendarActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		LinearLayout wrapper = (LinearLayout) findViewById(R.id.wrapper);

		CalendarView calendar = new CalendarView(getApplicationContext());
		calendar.setData(Calendar.getInstance());
		wrapper.addView(calendar);

		calendar.addSelectHandler(new CalendarSelectHandler() {
			@Override
			public void onChange(CalendarSelectEvent e) {
				Log.e(null, "Aatha naan pass aagiten" + e);
			}
		});
	}
}