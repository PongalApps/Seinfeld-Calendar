package com.pongal.seinfeld.data;

import java.util.HashSet;
import java.util.Set;

import android.util.Log;

public final class Task {
	private Integer id;
	private String text;
	private Set<Date> accomplishedDates = new HashSet<Date>();
	
	public Task() {
	    
	}

	public Task(int id, String text) {
		this.id = id;
		this.text = text;
	}

	public Integer getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getAccomplishedDatesCount() {
	    return accomplishedDates.size();
	}
	
	public boolean isAccomplishedDate(Date date) {
	    return accomplishedDates.contains(date);
	}
	
	public void addAccomplishedDates(Date date) {
		this.accomplishedDates.add(date);
	}

	public void removeAccomplishedDates(Date date) {
	    this.accomplishedDates.remove(date);
	}

}
