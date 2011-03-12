package com.pongal.seinfeld;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.pongal.seinfeld.DateState.Status;
import com.pongal.seinfeld.data.Date;
import com.pongal.seinfeld.data.Task;

public class DatesView extends TableLayout {

    Date currMonth;
    Date startDate;
    private int count;
    private int noOfCols = 7;
    String[] headers = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
    private Set<CalendarSelectHandler> selectHandler = new HashSet<CalendarSelectHandler>();

    public DatesView(Context context) {
	super(context);
    }

    public void setMonth(Date theDate) {
	currMonth = theDate;
	currMonth.resetToFirstDayOfMonth();
	startDate = currMonth.clone();
	int startDayOfWeek = currMonth.getDayOfWeek();
	startDate.addDays(1 - startDayOfWeek);
	int totalDays = currMonth.getMaximumDays();
	Double noOfRows = Math.ceil(((startDayOfWeek - 1 + totalDays) / 7.0));
	count = noOfRows.intValue() * noOfCols;
    }

    public void setData(Task task) {

    }

    public void addSelectHandler(CalendarSelectHandler handler) {
	selectHandler.add(handler);
    }
    
    /*private void createDateCells() {
	//First draw header
	for(String header : headers) {
	    addCell(createDateCell(getContext()));
	}
	TextView textView = (TextView) convertView;
	Log.d(null, "Position: " + position);
	if (position < headers.length) {
	    //return textView == null ? createDayCell(context, position) : textView;
	    return createDayCell(context, position);
	} else {
	    DateState model;
	    if (textView == null) {
		textView = createDateCell(context);
	    }
	    model = getModel(position);
	    textView.setTag(model);
	    textView.setText(Integer.toString(model.getDate().getDay()));
	    refreshStyle(textView);
	    return textView;
	}
    }*/
    
    private int currCell = 1;
    /*private void addCell(TextView view) {
	addVi
    }*/

    private TextView createDateCell(Context context) {
	TextView textView = new TextView(context);
	textView.setLayoutParams(new GridView.LayoutParams(-1, Util.getInDIP(45, context)));
	textView.setGravity(Gravity.CENTER);
	textView.setOnClickListener(getDateClickListener());
	return textView;
    }

    private TextView createDayCell(Context context, int index) {
	TextView view = new TextView(context);
	view.setLayoutParams(new GridView.LayoutParams(-1, Util.getInDIP(36, context)));
	view.setGravity(Gravity.CENTER);
	view.setText(headers[index]);
	view.setBackgroundResource(R.color.calHeaderBg);
	view.setTextAppearance(view.getContext(), R.style.calHeader);
	view.setEnabled(false);
	return view;
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
		data.toggleSelected();
		refreshStyle((TextView) view);
		notifySelectHandlers(data);
	    }
	};
    }

    /*private DateState getModel(int index) {
	index = index - headers.length;
	Date temp = startDate.clone();
	temp.addDays(index);
	DateState model = new DateState(temp);
	if (temp.isFutureDate()) {
	    model.setStatus(Status.DISABLED);
	} else if (task.isAccomplishedDate(temp)) {
	    model.setStatus(Status.SELECTED);
	}
	return model;
    }*/

    private void notifySelectHandlers(DateState state) {
	for (CalendarSelectHandler h : selectHandler) {
	    h.onChange(state);
	}
    }

}
