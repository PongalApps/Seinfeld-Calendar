package com.pongal.seinfeld;

import java.util.Set;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pongal.seinfeld.data.Task;

public class TaskView extends LinearLayout {

    Context context;

    public TaskView(Context context) {
	super(context);
	this.context = context;
	addView(getTasksView());
    }

    public void addTasks(Set<Task> tasks, OnClickListener listener) {
	LinearLayout body = (LinearLayout) findViewById(R.id.body);
	for (Task task : tasks) {
	    LinearLayout row = new LinearLayout(context);
	    row.setBackgroundResource(R.layout.datebg);
	    row.setTag(task);
	    if (listener != null) {
		row.setOnClickListener(listener);
	    }

	    CheckBox chkBox = new CheckBox(context);
	    row.addView(chkBox);
	    
	    TextView item = new TextView(context);
	    item.setText(task.getText());
	    item.setTextColor(Color.BLACK);
	    item.setGravity(Gravity.LEFT);
	    row.addView(item);
	    body.addView(row);
	}
    }

    private View getTasksView() {
	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	return inflater.inflate(R.layout.tasklist, null);
    }

}
