package com.pongal.seinfeld;

import java.util.Calendar;

public class CalendarSelectEvent {

	private boolean selected;
	private Calendar data;

	public CalendarSelectEvent(boolean selected, Calendar data) {
		this.selected = selected;
		this.data = data;
	}

	public boolean isSelected() {
		return selected;
	}

	public Calendar getData() {
		return data;
	}

	@Override
	public String toString() {
		return "CalendarSelectEvent [data=" + data + ", selected=" + selected
				+ "]";
	}

}
