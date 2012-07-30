package com.pongal.paid.seinfeld.task;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TimePicker;

import com.pongal.paid.seinfeld.R;
import com.pongal.paid.seinfeld.Util;
import com.pongal.paid.seinfeld.data.Task;

public class EditTaskView extends Dialog {

    public static final int ADD_TASK = 1;
    public static final int EDIT_TASK = 2;

    Task task;
    TaskUpdatedHandler handler;
    EditText taskName;
    Button okButton;
    Button cancelButton;
    CheckBox reminderCheckBox;
    TimePicker timePicker;
    CheckBox sendReminderCheckBox;
    EditText phoneNumber;
    EditText reminderText;

    public EditTaskView(Context context) {
	super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.edittask);
	LayoutParams params = getWindow().getAttributes();
	params.width = LayoutParams.FILL_PARENT;
    }

    public void init(final Task task, int type, final TaskUpdatedHandler handler) {
	this.task = task;
	this.handler = handler;
	this.taskName = (EditText) findViewById(R.id.taskName);
	this.okButton = (Button) findViewById(R.id.addTask);
	this.cancelButton = (Button) findViewById(R.id.cancelTask);
	this.reminderCheckBox = (CheckBox) findViewById(R.id.setReminder);
	this.timePicker = (TimePicker) findViewById(R.id.timePicker);
	this.sendReminderCheckBox = (CheckBox) findViewById(R.id.sendReminder);
	this.phoneNumber = (EditText) findViewById(R.id.phoneNumber);
	this.reminderText = (EditText) findViewById(R.id.reminderText);

	setTitle(type == EditTaskView.EDIT_TASK ? "Edit Task" : "Add Task");
	cancelButton.setOnClickListener(getCancelHandler());
	okButton.setOnClickListener(getSaveHandler());
	okButton.layout(0, 0, 0, 0);
	okButton.setText("Ok");
	taskName.setText(task.getText());
	reminderCheckBox.setChecked(task.isReminderSet());
	timePicker.setVisibility(task.isReminderSet() ? View.VISIBLE : View.GONE);
	sendReminderCheckBox.setVisibility(task.isReminderSet() ? View.VISIBLE : View.GONE);
	sendReminderCheckBox.setChecked(task.isSendReminderSet());
	phoneNumber.setText(task.getPhoneNumber());
	reminderText.setText(task.getReminderText());
	phoneNumber.setVisibility(task.isSendReminderSet() ? View.VISIBLE : View.GONE);
	reminderText.setVisibility(task.isSendReminderSet() ? View.VISIBLE : View.GONE);

	if (task.isReminderSet()) {
	    final java.util.Date reminderTime = task.getReminderTime();
	    timePicker.setCurrentHour(reminderTime.getHours());
	    timePicker.setCurrentMinute(reminderTime.getMinutes());
	}

	taskName.addTextChangedListener(new TextWatcher() {
	    public void onTextChanged(CharSequence charsequence, int i, int j, int k) {
	    }

	    public void beforeTextChanged(CharSequence charsequence, int i, int j, int k) {
	    }

	    public void afterTextChanged(Editable editable) {
		String text = editable.toString().trim();
		okButton.setEnabled(!(text.length() == 0));
	    }
	});

	reminderCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	    public void onCheckedChanged(CompoundButton chkBtn, boolean isChecked) {
		toggleVisibility(timePicker, isChecked);
		toggleVisibility(sendReminderCheckBox, isChecked);
		toggleVisibility(phoneNumber, isChecked && sendReminderCheckBox.isChecked());
		toggleVisibility(reminderText, isChecked && sendReminderCheckBox.isChecked());

		final Calendar cal = Calendar.getInstance();
		timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
	    }
	});

	sendReminderCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	    public void onCheckedChanged(CompoundButton chkBtn, boolean isChecked) {
		toggleVisibility(phoneNumber, isChecked);
		toggleVisibility(reminderText, isChecked);
	    }
	});
    }

    private View.OnClickListener getCancelHandler() {
	return new View.OnClickListener() {
	    public void onClick(View view) {
		dismiss();
	    }
	};
    }

    private View.OnClickListener getSaveHandler() {
	return new View.OnClickListener() {
	    public void onClick(View view) {
		if (!taskName.getText().equals(task.getText())) {
		    task.setText(taskName.getText().toString());

		    task.setReminderTime(null);
		    if (reminderCheckBox.isChecked()) {
			timePicker.clearFocus();
			final long msTime = Util.convertToMilliseconds(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
			task.setReminderTime(new java.util.Date(msTime));
		    }

		    task.setPhoneNumber(null);
		    task.setReminderText(null);
		    if (sendReminderCheckBox.isChecked()) {
			task.setPhoneNumber(Integer.parseInt(phoneNumber.getText().toString()));
			task.setReminderText(reminderText.getText().toString());
		    }

		    handler.onUpdate(task);
		}
	    }
	};
    }

    private void toggleVisibility(View view, boolean isChecked) {
	view.setEnabled(isChecked);
	view.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }

}
