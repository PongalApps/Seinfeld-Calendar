package com.pongal.paid.seinfeld.db;

import java.util.LinkedHashSet;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pongal.paid.seinfeld.data.Constants;
import com.pongal.paid.seinfeld.data.Date;
import com.pongal.paid.seinfeld.data.Task;

public class DBManager {

    DBHelper helper;
    SQLiteDatabase database;

    public DBManager(Context context) {
	helper = new DBHelper(context);
	this.database = helper.getWritableDatabase();
    }

    public void close() {
	helper.close();
	database.close();
    }

    public void createTask(String text) {
	database.execSQL("insert into task(name) values (?)", new String[] { text });
    }
    
    public void createTask(String taskName, java.util.Date reminderTime) {
	final String milliSecondsText = reminderTime == null ? "" : String.valueOf(reminderTime.getTime());
	Log.d(Constants.LogTag, "Create Task: " + String.format("insert into task(name, reminder) values (%s, %s)", taskName, milliSecondsText));
	database.execSQL("insert into task(name, reminder) values (?, ?)", new String[] { taskName, milliSecondsText });
    }

    public Set<Task> getTasks() {
	Set<Task> tasks = new LinkedHashSet<Task>();
	Cursor result = database.rawQuery("select * from Task", null);
	while (result.moveToNext()) {
	    tasks.add(getTaskDetails(result.getInt(0)));
	}
	result.close();
	return tasks;
    }

    public Task getTaskDetails(int taskId) {
	Task task = null;
	String[] taskIds = new String[] { taskId + "" };
	Cursor result = database.rawQuery("select * from Task where id = ?", taskIds);
	while (result.moveToNext()) {
	    task = new Task(result.getInt(0), result.getString(1));
	    
	    final String reminderTimeText = result.getString(2);
	    // Log.d(Constants.LogTag, "Reminder: " + reminderTimeText);
	    if (reminderTimeText != null && reminderTimeText.length() != 0) {
		Log.d(Constants.LogTag, "Task: " + task.getText() + "..." + reminderTimeText);
		long reminderTimeMilliSecs = Long.parseLong(reminderTimeText, 10);
		task.setReminderTime(new java.util.Date(reminderTimeMilliSecs));
	    }
	    
	    task.setPhoneNumber(result.getInt(3));
	    task.setReminderText(result.getString(4));
	    Cursor dates = database.rawQuery("select date from Status where task_id = ? order by date", taskIds);
	    while (dates.moveToNext()) {
		Date date = new Date(dates.getString(0));
		task.addAccomplishedDates(date);
	    }
	    dates.close();

	    Cursor notes = database.rawQuery("select date, notes from Notes where task_id = ?", taskIds);
	    while (notes.moveToNext()) {
		Date date = new Date(notes.getString(0));
		String noteStr = notes.getString(1);
		task.putNote(date, noteStr);
	    }
	    notes.close();
	}
	result.close();
	return task;
    }

    public void updateTaskCalendar(int taskId, Date date, boolean accomplished) {
	String[] args = new String[] { taskId + "", date.toString() };
	if (accomplished) {
	    if (!checkExistence(taskId, date)) {
		database.execSQL("insert into Status(task_id,date) values (?,?)", args);
	    }
	} else {
	    database.execSQL("delete from Status where task_id = ? and date = ?", args);
	}
    }

    public int updateTask(Task task) {
	final java.util.Date reminderTime = task.getReminderTime();
	final String milliSecondsText = reminderTime == null ? "" : String.valueOf(reminderTime.getTime());
	
	final String formatSpec = "insert or replace into task(id, name, reminder, phone_number, reminder_text) values (%s, %s, %s, %s, %s)";
	Log.d(Constants.LogTag, "updateTask: " + String.format(formatSpec, task.getId(), task.getText(), milliSecondsText, task.getPhoneNumber(), task.getReminderText()));
	
	String insertOrUpdateQuery = "insert or replace into task(id, name, reminder, phone_number, reminder_text) values (?, ?, ?, ?, ?);";
	database.execSQL(insertOrUpdateQuery, new Object[] { task.getId(), task.getText(), milliSecondsText, task.getPhoneNumber(), task.getReminderText() });
	
	Cursor taskIds = database.rawQuery("select id from task where name = ?", new String[] { task.getText() });
	taskIds.moveToNext();
	final int taskId = taskIds.getInt(0);
	taskIds.close();
	
	return taskId;
    }

    public void updateNotes(int taskId, Date date, String notes) {
	String[] selector = new String[] { taskId + "", date.toString() };
	ContentValues values = new ContentValues();
	values.put("Notes", notes);
	int updateCnt = database.update("Notes", values, "task_id = ? and date = ?", selector);
	if (updateCnt == 0) {
	    values.put("task_id", taskId);
	    values.put("Date", date.toString());
	    database.insert("Notes", null, values);
	}
    }

    public void deleteTask(Task task) {
	Object[] params = new Object[] { task.getId() };
	String dateDeleteQuery = "delete from Status where task_id= ?";
	database.execSQL(dateDeleteQuery, params);
	String deleteQuery = "delete from task where id= ?";
	database.execSQL(deleteQuery, params);
    }
    
    /*public String queryReminderTime() {
	return queryUserSettings("reminder_time");
    }

    private String queryUserSettings(String string) {
//	Cursor result = database.rawQuery("select * from UserSettings where setting = ?", new String[] { string });	
//	String value = result.moveToNext() ? result.getString(result.getColumnIndex("value")) : null;
//	result.close();
//	return value;
	
	return "09:00";
    }

    public void updateReminderTime(String time) {
	updateUserSettings("reminder_time", time);
    }

    public void updateUserSettings(String setting, String value) {
//	String[] selector = new String[] { setting };
//	ContentValues values = new ContentValues();
//	values.put("value", value);
//	int updateCnt = database.update("UserSettings", values, "setting = ?", selector);
//	if (updateCnt == 0) {
//	    values.put("setting", setting);
//	    values.put("value", value);
//	    database.insert("UserSettings", null, values);
//	}
    }*/

    private boolean checkExistence(int taskId, Date date) {
	boolean exists = false;
	String query = "select * from Status where task_id = ? and date = ?";
	Cursor result = database.rawQuery(query, new String[] { taskId + "", date.toString() });
	while (result.moveToNext()) {
	    exists = true;
	}
	result.close();
	return exists;
    }

    private class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "SeinfeldCalendar";
	private static final int DB_VERSION = 3;

	public DBHelper(Context context) {
	    super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
	    super.onOpen(db);
	    if (!db.isReadOnly()) {
		db.execSQL("PRAGMA foreign_keys=ON;");
	    }
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	    v1Changes(db);
	    v2Changes(db);
	    v3Changes(db);
	    v4Changes(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int existingVersion, int newVersion) {
	    if (existingVersion == 1) {
		v2Changes(db);
		v3Changes(db);
		v4Changes(db);
	    } else if (existingVersion == 2) {
		v3Changes(db);
		v4Changes(db);
	    } else if (existingVersion == 3){
		v4Changes(db);
	    }
	}

	private void v1Changes(SQLiteDatabase db) {
	    String createTaskTable = "create table if not exists Task (id integer primary key autoincrement not null, name text);";
	    db.execSQL(createTaskTable);

	    String createStatusTable = "create table if not exists Status(id integer primary key autoincrement not null, date text, task_id integer, FOREIGN KEY(task_id) REFERENCES Task(id));";
	    db.execSQL(createStatusTable);
	}

	private void v2Changes(SQLiteDatabase db) {
	    String createNotes = "create table if not exists Notes(task_id integer, date text, notes text, primary key(task_id,date));";
	    db.execSQL(createNotes);
	}
	
	private void v3Changes(SQLiteDatabase db) {
	    String createNotes = "alter table TASK add column REMINDER text;";
	    db.execSQL(createNotes);
	}
	
	private void v4Changes(SQLiteDatabase db) {
	    db.execSQL("alter table Task add column phone_number text");
	    db.execSQL("alter table Task add column reminder_text text");
	}
    }

}
