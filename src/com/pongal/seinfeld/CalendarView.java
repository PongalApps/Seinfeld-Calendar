package com.pongal.seinfeld;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.pongal.seinfeld.data.Date;
import com.pongal.seinfeld.data.Task;

public class CalendarView extends LinearLayout {

    CalendarAdapter calendarAdapter;
    TextView taskName;
    TextView monthYr;
    GridView gridView;
    EditText notes;
    Date displayedMonth;
    Button nextMonthBtn;
    Task task;

    public CalendarView(Context context) {
	super(context);
	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	init(inflater);
	taskName = (TextView) findViewById(R.id.cal_taskName);
	monthYr = (TextView) findViewById(R.id.cal_monthYr);
	gridView = (GridView) findViewById(R.id.cal_datesGrid);
	calendarAdapter = new CalendarAdapter(gridView);
	notes = (EditText) findViewById(R.id.cal_notes);
	Button prevMonth = (Button) findViewById(R.id.cal_preMonth);
	prevMonth.setOnClickListener(getMonthChangeHandler(-1));
	nextMonthBtn = (Button) findViewById(R.id.cal_nextMonth);
	nextMonthBtn.setOnClickListener(getMonthChangeHandler(1));
    }

    public void setTask(Task aTask) {
	this.task = aTask;
	displayedMonth = new Date();
	monthYr.setText(displayedMonth.format("MMMM yyyy"));
	taskName.setText(task.getText());
	calendarAdapter.setData(task, displayedMonth);
	notes.setText(task.getNotes().get(displayedMonth));
	setNextMonthButtonState();
    }

    public void addSelectHandler(CalendarSelectHandler handler) {
	calendarAdapter.addSelectHandler(handler);
    }

    public Date getDisplayedMonth() {
	return displayedMonth;
    }

    private void init(LayoutInflater inflater) {
	LinearLayout contents = (LinearLayout) inflater.inflate(R.layout.cal_view, null);
	contents.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	addView(contents);
    }

    private OnClickListener getMonthChangeHandler(final int count) {
	return new OnClickListener() {
	    public void onClick(View v) {
		displayedMonth.addMonths(count);
		monthYr.setText(displayedMonth.format("MMMM yyyy"));
		calendarAdapter.setData(displayedMonth);
		notes.setText(task.getNotes().get(displayedMonth));
		setNextMonthButtonState();
	    }
	};
    }
    
    private void setNextMonthButtonState() {
	Date nextMonth = displayedMonth.clone();
	nextMonth.addMonths(1);
	nextMonthBtn.setEnabled(!nextMonth.isFutureDate());
    }

    public void addNotesChangeListener(OnEditorActionListener notesActionListener) {
	notes.setOnEditorActionListener(notesActionListener);
    }
}
