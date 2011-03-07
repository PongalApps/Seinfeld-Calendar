package com.pongal.seinfeld.task;

import java.util.List;

import com.pongal.seinfeld.data.Task;

public interface TaskSelectionChangedHandler {

    public void onSelectionChange(List<Task> tasks);
}
