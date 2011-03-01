package com.pongal.seinfeld.data;

import java.util.Calendar;

import android.util.Log;

public class Date implements Cloneable {

	Calendar calendar = Calendar.getInstance();

	public Date() {
	}
	
	public Date(int year, int month, int date) {
		java.util.Date jDate = new java.util.Date(year - 1900, month, date);
		calendar.setTime(jDate);
	}

	public void addDays(int count) {
		calendar.add(Calendar.DATE, count);
	}

	public int getMaximumDays() {
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public int getDayOfWeek() {
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	public int getDay() {
		return calendar.get(Calendar.DATE);
	}

	public int getMonth() {
		return calendar.get(Calendar.MONTH);
	}
	
	public int getYear() {
		return calendar.get(Calendar.YEAR);
	}
	
	public void resetToFirstDayOfMonth() {
		calendar.set(Calendar.DATE, 1);
	}
	
	public java.util.Date getDate() {
		return calendar.getTime();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getDay(); 
		result = prime * result + getMonth();
		result = prime * result + getYear();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		
		if (this == obj)
			return true;
		
		if (getClass() != obj.getClass())
			return false;
		
		Date rhs = (Date) obj;
		
		return this.getDay() == rhs.getDay()
		&& this.getMonth() == rhs.getMonth()
		&& this.getYear() == rhs.getYear();		
	}
	
	@Override
	public String toString() {
		return getDay() + "-" + getMonth()+ "-" + getYear();
	}

	public Date clone() {
		java.util.Date d = calendar.getTime();
		return new Date(d.getYear() + 1900, d.getMonth(), d.getDate());
	}

}
