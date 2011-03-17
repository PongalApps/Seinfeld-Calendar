package com.pongal.seinfeld;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.splashscreen);
	LinearLayout parent = (LinearLayout) findViewById(R.id.splash);
	parent.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(View v) {
		finish();
	    }
	});
    }
}
