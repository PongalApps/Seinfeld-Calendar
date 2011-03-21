package com.pongal.seinfeld.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Date implements Cloneable {

    Calendar calendar = Calendar.getInstance();
    public static DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

    public Date() {
    }

    public Date(String dateStr) {
	try {
	    java.util.Date jDate = dateFormatter.parse(dateStr);
	    calendar.setTime(jDate);
	} catch (ParseException e) {
	    throw new RuntimeException("Incorrect date string to format", e);
	}
    }

    public Date(int year, int month, int date) {
	java.util.Date jDate = new java.util.Date(year - 1900, month - 1, date);
	calendar.setTime(jDate);
    }

    public Date(java.util.Date jDate) {
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

    public void addMonths(int count) {
	calendar.add(Calendar.MONTH, count);
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

    public String format(String format) {
	return new SimpleDateFormat(format).format(calendar.getTime());
    }

    public boolean isFutureDate() {
	Date currentDate = new Date();
	return calendar.after(currentDate.calendar);
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
	return this.getDay() == rhs.getDay() && this.getMonth() == rhs.getMonth() && this.getYear() == rhs.getYear();
    }

    @Override
    public String toString() {
	return dateFormatter.format(this.getDate());
    }

    public Date clone() {
	return new Date(getDate());
    }

    public boolean isYesterday(Date date) {
	Date yesterday = this.clone();
	yesterday.addDays(-1);
	return yesterday.equals(date);
    }

}
