package com.pongal.seinfeld;

import com.pongal.seinfeld.data.Date;

public class DateState {

	private boolean selected;
	private Date date;

	public DateState(boolean selected, Date date) {
		this.selected = selected;
		this.date = date;
	}

	public boolean isSelected() {
		return selected;
	}

	public Date getDate() {
		return date;
	}

	public void toggleState() {
		selected = !selected;
	}

	@Override
	public String toString() {
		return "CalendarSelectEvent [data=" + date + ", selected=" + selected
				+ "]";
	}

}
