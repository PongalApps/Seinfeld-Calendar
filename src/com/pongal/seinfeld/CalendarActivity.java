package com.pongal.seinfeld;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.pongal.seinfeld.DateState.Status;
import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.db.DBManager;

public class CalendarActivity extends Activity {
    DBManager manager;
    Task task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	initDBManager();
	final int taskId = getIntent().getExtras().getInt("taskId");
	task = manager.getTaskDetails(taskId);

	setContentView(R.layout.main);
	LinearLayout wrapper = (LinearLayout) findViewById(R.id.wrapper);
	wrapper.setOrientation(LinearLayout.VERTICAL);

	CalendarView calendar = new CalendarView(getApplicationContext());
	calendar.setTask(task);
	calendar.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	// disableDates(calendar);
	wrapper.addView(calendar);

	calendar.addSelectHandler(new CalendarSelectHandler() {
	    @Override
	    public void onChange(DateState e) {
		if (e.getStatus() == Status.SELECTED) {
		    task.addAccomplishedDates(e.getDate());
		    manager.updateTaskCalendar(taskId, e.getDate(), true);
		} else {
		    task.removeAccomplishedDates(e.getDate());
		    manager.updateTaskCalendar(taskId, e.getDate(), false);
		}
	    }
	});
    }

    private void initDBManager() {
	if (manager == null)
	    manager = new DBManager(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	manager.close();
    }

}