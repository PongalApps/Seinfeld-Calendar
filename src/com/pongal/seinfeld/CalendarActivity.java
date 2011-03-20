package com.pongal.seinfeld;

import android.app.Activity;
import android.content.Context;
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

public class CalendarActivity extends Activity {
    DBManager manager;
    Task task;
    CalendarView calendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	initDBManager();
	final int taskId = getIntent().getExtras().getInt("taskId");
	task = manager.getTaskDetails(taskId);

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

    private void initDBManager() {
	if (manager == null)
	    manager = new DBManager(getApplicationContext());
    }

    private OnEditorActionListener getNotesActionListener() {
	return new OnEditorActionListener() {
	    @Override
	    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
		    manager.updateNotes(task.getId(), calendar.getDisplayedMonth(), v.getText().toString());
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
		    manager.updateTaskCalendar(task.getId(), e.getDate(), true);
		} else {
		    task.removeAccomplishedDates(e.getDate());
		    manager.updateTaskCalendar(task.getId(), e.getDate(), false);
		}
	    }
	};
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	manager.close();
    }

}