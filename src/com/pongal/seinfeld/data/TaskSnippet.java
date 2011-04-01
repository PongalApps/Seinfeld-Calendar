package com.pongal.seinfeld.data;

public class TaskSnippet {
    public int taskId;
    public String taskName;
    public boolean doneToday;

    public TaskSnippet() {
    }

    public TaskSnippet(int taskId, String taskName, boolean doneToday) {
	this.taskId = taskId;
	this.taskName = taskName;
	this.doneToday = doneToday;
    }

    @Override
    public String toString() {
	return "TaskSnippet [taskId=" + taskId + ", taskName=" + taskName + ", doneToday=" + doneToday + "]";
    }

}
