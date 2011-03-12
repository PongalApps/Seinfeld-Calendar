package com.pongal.seinfeld.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.pongal.seinfeld.R;
import com.pongal.seinfeld.data.Task;

public class TaskListView extends LinearLayout {

    Context context;
    TaskSelectionChangedHandler selectionChangeHandler;

    public TaskListView(Context context) {
	super(context);
	this.context = context;
	addView(getTasksView());
    }

    public void addTasks(Set<Task> tasks, OnClickListener listener) {
	LinearLayout body = (LinearLayout) findViewById(R.id.body);
	for (Task task : tasks) {
	    LinearLayout row = new LinearLayout(context);
	    row.setBackgroundResource(R.layout.taskbg);
	    row.getBackground().setDither(true);
	    row.setTag(task);
	    if (listener != null) {
		row.setOnClickListener(listener);
	    }

	    CheckBox chkBox = new CheckBox(context);
	    chkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		    selectionChangeHandler.onSelectionChange(getSelections());
		}
	    });
	    row.addView(chkBox);

	    TextView item = new TextView(context);
	    item.setSingleLine();
	    item.setEllipsize(TextUtils.TruncateAt.END);
	    item.setText(task.getText());
	    item.setTextColor(Color.WHITE);
	    item.setPadding(10, 0, 10, 0);
	    item.setGravity(Gravity.LEFT);
	    row.addView(item);
	    body.addView(row);
	}
    }

    private View getTasksView() {
	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	return inflater.inflate(R.layout.tasklist, null);
    }

    public void clear() {
	LinearLayout body = (LinearLayout) findViewById(R.id.body);
	body.removeAllViews();
    }

    public List<Task> getSelections() {
	List<Task> selected = new ArrayList<Task>();
	LinearLayout body = (LinearLayout) findViewById(R.id.body);
	for (int i = 0; i < body.getChildCount(); i++) {
	    LinearLayout row = (LinearLayout) body.getChildAt(i);
	    CheckBox chkBox = (CheckBox) row.getChildAt(0);
	    if (chkBox.isChecked()) {
		selected.add((Task) row.getTag());
	    }
	}
	return selected;
    }
    
    public void addSelectionChangedHandler(TaskSelectionChangedHandler handler) {
	selectionChangeHandler = handler;
    }

}
