package com.pongal.seinfeld;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.pongal.seinfeld.DateState.Status;
import com.pongal.seinfeld.data.Task;
import com.pongal.seinfeld.db.DBManager;
import com.pongal.seinfeld.homescreen.HomeScreenWidgetProvider;

public class CalendarActivity extends Activity {
    DBManager dbManager;
    Task task;
    CalendarView calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	initDBManager();
	final int taskId = getIntent().getExtras().getInt("taskId");
	task = dbManager.getTaskDetails(taskId);

	setContentView(R.layout.main);
	LinearLayout wrapper = (LinearLayout) findViewById(R.id.wrapper);
	wrapper.setOrientation(LinearLayout.VERTICAL);

	calendar = new CalendarView(getApplicationContext());
	calendar.setTask(task);
	calendar.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	// disableDates(calendar);
	wrapper.addView(calendar);

	calendar.addSelectHandler(getCalendarSelectHandler());
	calendar.addNotesChangeListener(getNotesActionListener());
    }
    
    @Override
    protected void onResume() {
	super.onResume();
	task = dbManager.getTaskDetails(task.getId());
	calendar.setTask(task);
    }

    private void initDBManager() {
	if (dbManager == null) {
	    dbManager = new DBManager(getApplicationContext());
	}
    }

    private OnEditorActionListener getNotesActionListener() {
	return new OnEditorActionListener() {
	    @Override
	    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
		    dbManager.updateNotes(task.getId(), calendar.getDisplayedMonth(), v.getText().toString());
		    task.getNotes().put(calendar.getDisplayedMonth(), v.getText().toString());
		    InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(
			    Context.INPUT_METHOD_SERVICE);
		    inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		    return true;
		}
		return false;
	    }
	};
    }

    public CalendarSelectHandler getCalendarSelectHandler() {
	return new CalendarSelectHandler() {
	    @Override
	    public void onChange(DateState e) {
		if (e.getStatus() == Status.SELECTED) {
		    task.addAccomplishedDates(e.getDate());
		    dbManager.updateTaskCalendar(task.getId(), e.getDate(), true);
		} else {
		    task.removeAccomplishedDates(e.getDate());
		    dbManager.updateTaskCalendar(task.getId(), e.getDate(), false);
		}
		sendBroadcast(new Intent(HomeScreenWidgetProvider.ACTION_REFRESH));
	    }
	};
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	dbManager.close();
    }

}