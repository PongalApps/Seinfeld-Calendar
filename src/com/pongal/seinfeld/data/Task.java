package com.pongal.seinfeld.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


public final class Task {
    private Integer id;
    private String text;
    private Set<Date> accomplishedDates = new LinkedHashSet<Date>();
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

    public int[] getChainLengths() {
	if (accomplishedDates.size() != 0) {
	    Date yesterday = new Date();
	    yesterday.addDays(-1);
	    int maxChainLen = 1;
	    int curChainLen = 0;
	    int itrChainLen = 1;
	    Date prevDate = null;
	    Iterator<Date> itr = accomplishedDates.iterator();
	    do {
		Date currDate = itr.next();
		if (currDate.isYesterday(prevDate)) {
		    itrChainLen++;
		    maxChainLen = (itrChainLen > maxChainLen) ? itrChainLen : maxChainLen;
		} else {
		    itrChainLen = 1;
		}
		curChainLen = isTodayOrYesterday(currDate) ? itrChainLen : 0;
		prevDate = currDate;
		
	    } while (itr.hasNext());
	    return new int[] {curChainLen, maxChainLen};
	}
	return new int[] {0,0};
    }

    private boolean isTodayOrYesterday(Date date) {
	Date today = new Date();
	Date yesterday = today.clone();
	yesterday.addDays(-1);
	return today.equals(date) || yesterday.equals(date);
    }
    
    public boolean isTodayAccomplished() {
	return isAccomplishedDate(new Date());
    }
}
