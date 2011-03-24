package com.pongal.seinfeld.homescreen;

import android.app.Activity;
import android.os.Bundle;

import com.pongal.seinfeld.data.Date;
import com.pongal.seinfeld.db.DBManager;

public class HomeScreenActivity extends Activity {

    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	initDBManager();
	final int taskId = getIntent().getExtras().getInt("taskId");
	final boolean marked = getIntent().getExtras().getBoolean("marked");
	dbManager.updateTaskCalendar(taskId, new Date(), marked);
	dbManager.close();
    }

    private void initDBManager() {
	if (dbManager == null)
	    dbManager = new DBManager(getApplicationContext());
    }
}
