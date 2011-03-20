package com.pongal.seinfeld;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.pongal.seinfeld.DateState.Status;
import com.pongal.seinfeld.data.Date;
import com.pongal.seinfeld.data.Task;

public class CalendarAdapter extends BaseAdapter {

    Date date;
    Date startDate;
    private int noOfRows;
    private int count;
    String[] HEADERS = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
    int HEADER_HT = 20;
    int DATE_CELL_HT = 45;

    private Set<CalendarSelectHandler> selectHandler = new HashSet<CalendarSelectHandler>();
    GridView view;
    Task task;

    public CalendarAdapter(GridView view) {
	this.view = view;
	this.view.setAdapter(this);
	view.setNumColumns(7);
	view.setVerticalSpacing(0);
	view.setHorizontalSpacing(0);
	view.setVerticalScrollBarEnabled(false);
	view.setPadding(0, 0, 0, 0);

    }

    private void initializeMonth(Date theDate) {
	date = theDate;
	date.resetToFirstDayOfMonth();
	startDate = date.clone();
	int startDayOfWeek = date.getDayOfWeek();
	startDate.addDays(1 - startDayOfWeek);
	int totalDays = date.getMaximumDays();
	noOfRows = new Double(Math.ceil(((startDayOfWeek - 1 + totalDays) / 7.0))).intValue();
	count = noOfRows * 7;
    }

    public void setData(Task task, Date displayMonth) {
	this.task = task;
	setData(displayMonth);
    }

    public void setData(Date displayMonth) {
	initializeMonth(displayMonth);
	view.getLayoutParams().height = Util.getInDIP(getHeight(), view.getContext());
	view.invalidateViews();
    }

    public void addSelectHandler(CalendarSelectHandler handler) {
	selectHandler.add(handler);
    }

    @Override
    public int getCount() {
	return count + HEADERS.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	Context context = parent.getContext();
	TextView textView = (TextView) convertView;
	if (position < HEADERS.length) {
	    return makeDayCell(textView, context, position);
	} else {
	    return makeDateCell(textView, context, getModel(position));
	}
    }

    private int getHeight() {
	return (DATE_CELL_HT * noOfRows) + HEADER_HT + 15;
    }

    private TextView makeDayCell(TextView view, Context context, int index) {
	TextView textView = view == null ? new TextView(context) : view;
	textView.setLayoutParams(new GridView.LayoutParams(-1, Util.getInDIP(HEADER_HT, context)));
	textView.setGravity(Gravity.CENTER);
	textView.setOnClickListener(getDateClickListener());
	textView.setText(HEADERS[index]);
	textView.setTag(null);
	textView.setBackgroundResource(R.color.calHeaderBg);
	textView.setTextAppearance(textView.getContext(), R.style.calHeader);
	textView.setEnabled(false);
	return textView;
    }

    private TextView makeDateCell(TextView view, Context context, DateState model) {
	TextView textView = view == null ? new TextView(context) : view;
	textView.setLayoutParams(new GridView.LayoutParams(-1, Util.getInDIP(DATE_CELL_HT, context)));
	textView.setGravity(Gravity.CENTER);
	textView.setOnClickListener(getDateClickListener());
	textView.setTag(model);
	textView.setText(Integer.toString(model.getDate().getDay()));
	refreshStyle(textView);
	return textView;
    }

    void refreshStyle(TextView view) {
	Context context = view.getContext();
	DateState model = ((DateState) view.getTag());
	switch (model.getStatus()) {
	case DISABLED:
	    view.setTextAppearance(context, R.style.calFieldDisabled);
	    view.setBackgroundResource(R.layout.datebg);
	    view.setEnabled(false);
	    break;
	case NORMAL:
	    view.setTextAppearance(context, R.style.calField);
	    view.setBackgroundResource(R.layout.datebg);
	    view.setEnabled(true);
	    break;
	case SELECTED:
	    view.setTextAppearance(context, R.style.calFieldSelected);
	    view.setBackgroundResource(R.layout.datebg_highlight);
	    view.setEnabled(true);
	}
    }

    private OnClickListener getDateClickListener() {
	return new OnClickListener() {
	    @Override
	    public void onClick(View view) {
		DateState data = (DateState) view.getTag();
		if (data != null) {
		    data.toggleSelected();
		    refreshStyle((TextView) view);
		    notifySelectHandlers(data);
		}
	    }
	};
    }

    private DateState getModel(int index) {
	index = index - HEADERS.length;
	Date temp = startDate.clone();
	temp.addDays(index);
	DateState model = new DateState(temp);
	if (temp.isFutureDate()) {
	    model.setStatus(Status.DISABLED);
	} else if (task.isAccomplishedDate(temp)) {
	    model.setStatus(Status.SELECTED);
	}
	return model;
    }

    private void notifySelectHandlers(DateState state) {
	for (CalendarSelectHandler h : selectHandler) {
	    h.onChange(state);
	}
    }

    @Override
    public Object getItem(int arg0) {
	return null;
    }

    @Override
    public long getItemId(int position) {
	return 0;
    }
}
