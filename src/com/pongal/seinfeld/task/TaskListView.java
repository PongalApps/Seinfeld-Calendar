package com.pongal.seinfeld.task;

import java.util.Set;

import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pongal.seinfeld.R;
import com.pongal.seinfeld.data.Task;

public class TaskListView extends LinearLayout {

    Context context;
    ImageView questionView;
    TextView menuHelpTxt;
    LinearLayout body;
    String helpNote;

    public TaskListView(final Context context, String helpNote) {
	super(context);
	this.context = context;
	this.helpNote = helpNote;
	addView(getTasksView());
	questionView = (ImageView) findViewById(R.id.questionImg);
	questionView.setVisibility(View.GONE);
	body = (LinearLayout) findViewById(R.id.body);
	menuHelpTxt = (TextView) findViewById(R.id.menuHelpTxt);
    }

    public void addQuestionClickListener(OnClickListener clickListener) {
	questionView.setVisibility(View.VISIBLE);
	questionView.setOnClickListener(clickListener);
    }

    public void addTasks(Set<Task> tasks, int taskLayout, OnClickListener listener) {
	if (tasks.size() == 0) {
	    addHelpNote();
	} else {
	    clear();
	    for (Task task : tasks) {
		LinearLayout row = getATaskView(taskLayout);
		row.setOnTouchListener(getHighlightListener());
		row.setTag(task);
		if (listener != null) {
		    row.setOnClickListener(listener);
		}
		TextView item = (TextView) row.findViewById(R.id.taskName);
		item.setText(task.getText());
		TextView stats = (TextView) row.findViewById(R.id.taskStats);
		if (stats != null) {
		    int[] chainLengths = task.getChainLengths();
		    stats.setText(Html.fromHtml("Current streak : <b>" + chainLengths[0]
			    + "</b> ,  Longest streak : <b>" + chainLengths[1] + "</b>"));
		}
		body.addView(row);
	    }
	}
    }

    private View getTasksView() {
	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	return inflater.inflate(R.layout.tasklist, null);
    }

    private LinearLayout getATaskView(int taskLayout) {
	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	return (LinearLayout) inflater.inflate(taskLayout, null);
    }

    private void clear() {
	LinearLayout body = (LinearLayout) findViewById(R.id.body);
	body.removeAllViews();
    }

    private void addHelpNote() {
	clear();
	TextView helpText = new TextView(getContext());
	helpText.setText(helpNote);
	helpText.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	helpText.setGravity(Gravity.CENTER);
	helpText.setTextColor(getResources().getColor(R.color.helpTextColor));
	body.addView(helpText);
    }

    private OnTouchListener getHighlightListener() {
	return new OnTouchListener() {
	    @Override
	    public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
		    v.setBackgroundResource(R.layout.taskbg_highlt);
		}
		if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
		    v.setBackgroundResource(R.layout.taskbg);
		}
		return false;
	    }
	};
    }

}
