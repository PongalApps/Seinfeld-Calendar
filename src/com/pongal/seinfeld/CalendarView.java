package com.pongal.seinfeld;

import java.util.Set;

import android.content.Context;
import android.view.Gravity;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pongal.seinfeld.data.Date;
import com.pongal.seinfeld.data.Task;

public class CalendarView extends LinearLayout {

	CalendarAdapter calendarAdapter;
	TextView header;
	GridView gridView;

	public CalendarView(Context context) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);
		gridView = new GridView(context);
		gridView.setNumColumns(7);
		gridView.setVerticalSpacing(0);
		gridView.setHorizontalSpacing(0);
		gridView.setVerticalScrollBarEnabled(false);
		gridView.setLayoutParams(new GridView.LayoutParams(
				GridView.LayoutParams.FILL_PARENT,
				GridView.LayoutParams.FILL_PARENT));
		gridView.setPadding(2, 0, 2, 0);
		calendarAdapter = new CalendarAdapter(gridView);

		header = new TextView(context);
		header.setText("");
		header.setGravity(Gravity.CENTER);
		header.setPadding(5, 5, 5, 5);
		header.setTextAppearance(context, R.style.calHeader);
		header.setBackgroundResource(R.color.calHeaderBg2);
		LayoutParams headerLayout = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		headerLayout.setMargins(2, 5, 2, 0);
		addView(header, headerLayout);
		addView(gridView);
	}

	public void setTask(Task task) {
	    	Date currDate = new Date();
		header.setText(currDate.format("MMMM yyyy"));
		calendarAdapter.setData(task);
	}

	public void addSelectHandler(CalendarSelectHandler handler) {
		calendarAdapter.addSelectHandler(handler);
	}

	public void disableDates(Set<Date> dates) {
		calendarAdapter.disableDates(dates);
	}

}
