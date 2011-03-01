package com.pongal.seinfeld;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pongal.seinfeld.data.Date;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter {

	Date date;
	Date startDate;
	Set<Date> disabledDates = new HashSet<Date>();
	private int count;
	String[] headers = new String[] { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri",
			"Sat" };
	private Set<CalendarSelectHandler> selectHandler = new HashSet<CalendarSelectHandler>();
	GridView view;

	public CalendarAdapter(GridView view) {
		this.view = view;
		this.view.setAdapter(this);
	}

	public void setData(Date theDate) {
		date = theDate;
		date.resetToFirstDayOfMonth();
		startDate = date.clone();
		int startDayOfWeek = date.getDayOfWeek();
		startDate.addDays(1 - startDayOfWeek);
		int totalDays = date.getMaximumDays();
		Double noOfRows = Math.ceil(((startDayOfWeek - 1 + totalDays) / 7.0));
		count = noOfRows.intValue() * 7;
	}

	public void disableDates(Set<Date> dates) {
		disabledDates = dates;
		Log.d(null, "Setting disabled dates");
	}

	@Override
	public int getCount() {
		return count + headers.length;
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
					DateState data = (DateState) view.getTag();
					data.toggleState();
					view.setBackgroundResource(data.isSelected() ? R.color.dateSelectedBg : R.color.dateBg);
					notifySelectHandlers(data);
				}
			});
		}

		Date date = getCurrentLabelDate(position);
		textView.setTag(new DateState(false, date));
		updateView(textView, position);

		/*updateState(textView, position);
		updateLabel(textView, position);
		updateStyle(textView, position);*/
		return textView;
	}
	
	private void updateView(TextView view, int index) {
		if (index < headers.length) {
			view.setText(headers[index]);			
			view.setBackgroundResource(R.color.calHeaderBg);
			view.setTextAppearance(view.getContext(), R.style.calHeader);
			view.setEnabled(false);
		} else {			
			Date model = ((DateState) view.getTag()).getDate();
			final boolean sameMonth = model.getMonth() == date.getMonth();
			final boolean disabledDate = disabledDates.contains(model);
			
			view.setText(Integer.toString(model.getDay()));
			view.setBackgroundResource(R.color.dateBg);
			view.setTextAppearance(view.getContext(), sameMonth && !disabledDate ? R.style.calField : R.style.calFieldDisabled);
			view.setEnabled(sameMonth ? !disabledDate : false);
		}
	}

	private void updateStyle(TextView view, int index) {
		if (index < headers.length) {
			view.setTextAppearance(view.getContext(), R.style.calHeader);
			view.setBackgroundResource(R.color.calHeaderBg);
		} else {
			view.setBackgroundResource(R.color.dateBg);
			Date tempDate = ((DateState) view.getTag()).getDate();
			if (tempDate.getMonth() == date.getMonth()) {
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
			view.setText(((DateState) view.getTag()).getDate().getDay() + "");
		}
	}

	private void updateState(TextView view, int index) {
		if (index < headers.length) {
			view.setEnabled(false);
		} else {
			Date tempDate = ((DateState) view.getTag()).getDate();
			if (tempDate.getMonth() != date.getMonth()) {
				view.setEnabled(false);
			} else {
				view.setEnabled(!disabledDates.contains(tempDate));
			}
		}
	}

	private Date getCurrentLabelDate(int index) {
		index = index - headers.length;
		Date temp = startDate.clone();
		temp.addDays(index);
		return temp;
	}

	public void addSelectHandler(CalendarSelectHandler handler) {
		selectHandler.add(handler);
	}

	void notifySelectHandlers(DateState state) {
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
