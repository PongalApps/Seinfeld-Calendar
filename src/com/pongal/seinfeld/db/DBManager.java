package com.pongal.seinfeld.db;

import java.util.LinkedHashSet;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pongal.seinfeld.data.Date;
import com.pongal.seinfeld.data.Task;

public class DBManager {

    DBHelper helper;
    SQLiteDatabase db;

    public DBManager(Context context) {
	helper = new DBHelper(context);
	this.db = helper.getWritableDatabase();
    }

    public void close() {
	helper.close();
	db.close();
    }

    public void createTask(String text) {
	db.execSQL("insert into task(name) values (?)", new String[] { text });
    }

    public Set<Task> getTasks() {
	Set<Task> tasks = new LinkedHashSet<Task>();
	Cursor result = db.rawQuery("select * from Task", null);
	while (result.moveToNext()) {
	    tasks.add(new Task(result.getInt(0), result.getString(1)));
	}
	result.close();
	return tasks;
    }

    public Task getTaskDetails(int taskId) {
	Task task = null;
	Cursor result = db.rawQuery("select * from Task where id = ?", new String[] { taskId + "" });
	while (result.moveToNext()) {
	    task = new Task(result.getInt(0), result.getString(1));
	    Cursor dates = db.rawQuery("select date from Status where task_id = ?", new String[] { taskId + "" });
	    while (dates.moveToNext()) {
		Date date = new Date(dates.getString(0));
		task.addAccomplishedDates(date);
	    }
	    dates.close();
	}
	result.close();
	return task;
    }

    public void updateTaskCalendar(int taskId, Date date, boolean accomplished) {
	String[] args = new String[] { taskId + "", date.toString() };
	if (accomplished) {
	    if (!checkExistence(taskId, date)) {
		db.execSQL("insert into Status(task_id,date) values (?,?)", args);
	    }
	} else {
	    db.execSQL("delete from Status where task_id = ? and date = ?", args);
	}
    }

    private boolean checkExistence(int taskId, Date date) {
	boolean exists = false;
	String query = "select * from Status where task_id = ? and date = ?";
	Cursor result = db.rawQuery(query, new String[] { taskId + "", date.toString() });
	while (result.moveToNext()) {
	    exists = true;
	}
	result.close();
	return exists;
    }

    public void updateTask(Task task) {
	String insertOrUpdateQuery = "insert or replace into task(id, name) values (?, ?);";
	db.execSQL(insertOrUpdateQuery, new Object[] { task.getId(), task.getText() });
    }

    public void deleteTask(Task task) {
	Object[] params = new Object[] { task.getId() };
	String dateDeleteQuery = "delete from Status where task_id= ?";
	db.execSQL(dateDeleteQuery, params);
	String deleteQuery = "delete from task where id= ?";
	db.execSQL(deleteQuery, params);
    }

    private class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "SeinfeldCalendar";
	private static final int DB_VERSION = 1;

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
	    String createTaskTable = "create table if not exists Task (id integer primary key autoincrement not null, name text);";
	    db.execSQL(createTaskTable);

	    String createStatusTable = "create table if not exists Status(id integer primary key autoincrement not null, date text, task_id integer, FOREIGN KEY(task_id) REFERENCES Task(id));";
	    db.execSQL(createStatusTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	    // Nothing as of now for the first version
	}
    }

}
