package com.pongal.seinfeld;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.pongal.seinfeld.data.Date;

public class CalendarActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		LinearLayout wrapper = (LinearLayout) findViewById(R.id.wrapper);

		CalendarView calendar = new CalendarView(getApplicationContext());
		calendar.setData(new Date());
		disableDates(calendar);
		wrapper.addView(calendar);
		
		calendar.addSelectHandler(new CalendarSelectHandler() {
			@Override
			public void onChange(DateState e) {
				Log.e(null, "Aatha naan pass aagiten" + e);
			}
		});
	}
	
	private void disableDates(CalendarView view) {
		Set<Date> dds = new HashSet<Date>();
		dds.add(new Date(2011, Calendar.MARCH, 10));
		dds.add(new Date(2011, Calendar.MARCH, 11));
		dds.add(new Date(2011, Calendar.MARCH, 12));
		view.disableDates(dds);
	}
}