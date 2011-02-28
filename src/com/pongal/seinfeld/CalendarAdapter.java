package com.pongal.seinfeld;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter {

	Calendar calendar;
	Calendar startDay;
	private int count;
	String[] headers = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri",
			"Sat" };
	private Set<CalendarSelectHandler> selectHandler = new HashSet<CalendarSelectHandler>();

	public void setData(Calendar cal) {
		calendar = cal;
		startDay = (Calendar) calendar.clone();

		int startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		startDay.add(Calendar.DATE, 1 - startDayOfWeek);
		int totalDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		Double noOfRows = Math.ceil(((startDayOfWeek - 1 + totalDays) / 7.0));
		count = noOfRows.intValue() * 7;
	}

	@Override
	public int getCount() {
		return count + headers.length;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = (TextView) convertView;
		if (textView == null) {
			textView = new TextView(parent.getContext());
			textView.setLayoutParams(new GridView.LayoutParams(-1, 40));
			textView.setGravity(Gravity.CENTER);
			textView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					view.setBackgroundResource(R.color.calHeaderBg);
					notifySelectHandlers(true, (Calendar) view.getTag());
				}
			});
		}
		textView.setTag(getCurrentLabelDate(position));
		updateLabel(textView, position);
		updateStyle(textView, position);
		return textView;
	}

	private void updateStyle(TextView view, int index) {
		if (index < headers.length) {
			view.setTextAppearance(view.getContext(), R.style.calHeader);
			view.setBackgroundResource(R.color.calHeaderBg);
		} else {
			view.setBackgroundResource(R.color.calFieldBg);
			Calendar tempDate = (Calendar) view.getTag();
			if (tempDate.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
				view.setTextAppearance(view.getContext(), R.style.calField);
			} else {
				view.setTextAppearance(view.getContext(),
						R.style.calFieldDisabled);
			}
		}
	}

	private void updateLabel(TextView view, int index) {
		if (index < headers.length) {
			view.setText(headers[index]);
		} else {
			view.setText(((Calendar) view.getTag()).get(Calendar.DATE) + "");
		}
	}

	private Calendar getCurrentLabelDate(int index) {
		index = index - headers.length;
		Calendar tempDate = (Calendar) startDay.clone();
		tempDate.add(Calendar.DATE, index);
		return tempDate;
	}

	public void addSelectHandler(CalendarSelectHandler handler) {
		selectHandler.add(handler);
	}

	void notifySelectHandlers(boolean selected, Calendar cal) {
		for (CalendarSelectHandler h : selectHandler) {
			h.onChange(new CalendarSelectEvent(selected, cal));
		}
	}
}
