package com.pongal.seinfeld;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.view.Gravity;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CalendarView extends LinearLayout {

	CalendarAdapter calendarAdapter;
	TextView header;
	GridView gridView;

	public CalendarView(Context context) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);
		gridView = new GridView(context);
		calendarAdapter = new CalendarAdapter();
		gridView.setAdapter(calendarAdapter);
		gridView.setNumColumns(7);
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
		// gridView.setScrollBarStyle(GridView.SCROLLBARS_INSIDE_INSET);
		gridView.setVerticalScrollBarEnabled(false);
		gridView.setLayoutParams(new GridView.LayoutParams(
				GridView.LayoutParams.FILL_PARENT,
				GridView.LayoutParams.FILL_PARENT));
		gridView.setPadding(3, 3, 0, 3);

		header = new TextView(context);
		header.setText("");
		header.setGravity(Gravity.CENTER);
		header.setPadding(5, 5, 5, 5);
		header.setTextAppearance(context, R.style.calHeader);
		header.setBackgroundResource(R.color.calHeaderBg2);
		addView(header);
		addView(gridView);
	}

	public void setData(Calendar cal) {
		cal.roll(Calendar.DATE, 1);
		header.setText(new SimpleDateFormat("MMMM yyyy").format(cal.getTime()));
		calendarAdapter.setData(cal);
	}

	public void addSelectHandler(CalendarSelectHandler handler) {
		calendarAdapter.addSelectHandler(handler);
	}

}
