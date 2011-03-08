package com.pongal.seinfeld;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.LinearLayout;

import com.pongal.seinfeld.DateState.Status;
import com.pongal.seinfeld.data.Date;
import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.db.DBManager;

public class CalendarActivity extends Activity {
    DBManager manager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	initDBManager();
	final int taskId = getIntent().getExtras().getInt("taskId");
	Task task = manager.getTaskDetails(taskId);

	setContentView(R.layout.main);
	LinearLayout wrapper = (LinearLayout) findViewById(R.id.wrapper);
	wrapper.setOrientation(LinearLayout.VERTICAL);

	CalendarView calendar = new CalendarView(getApplicationContext());
	calendar.setTask(task);
	calendar.setLayoutParams(new LinearLayout.LayoutParams(-1, getCalendarHeight()));
	disableDates(calendar);
	wrapper.addView(calendar);

	calendar.addSelectHandler(new CalendarSelectHandler() {
	    @Override
	    public void onChange(DateState e) {
		manager.updateTaskCalendar(taskId, e.getDate(), (e.getStatus() == Status.SELECTED) ? true : false);
	    }
	});
    }

    private int getCalendarHeight() {
	Display display = getWindowManager().getDefaultDisplay();
	int windowHeight = display.getHeight();
//	if (display.getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
//	    windowHeight = display.getHeight();
//	} else {
//	    windowHeight = display.getHeight();
//	}
	return Util.getInDIP(windowHeight * 2 / 3, getApplicationContext());
    }

    private void initDBManager() {
	if (manager == null)
	    manager = new DBManager(getApplicationContext());
    }

    private void disableDates(CalendarView view) {
	Set<Date> dds = new HashSet<Date>();
	dds.add(new Date(2011, Calendar.MARCH, 10));
	dds.add(new Date(2011, Calendar.MARCH, 11));
	dds.add(new Date(2011, Calendar.MARCH, 12));
	view.disableDates(dds);
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	manager.close();
    }

}