package com.pongal.seinfeld.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Task {
    private Integer id;
    private String text;
    private Set<Date> accomplishedDates = new HashSet<Date>();
    private Map<Date, String> notes = new HashMap<Date, String>();

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
    
    public void putNote(Date date, String note) {
	notes.put(date, note);
    }
    
    public Map<Date, String> getNotes() {
	return notes;
    }
}
