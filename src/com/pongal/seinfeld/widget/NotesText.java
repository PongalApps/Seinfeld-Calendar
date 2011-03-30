package com.pongal.seinfeld.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

public class NotesText extends EditText {

    public NotesText(Context context) {
	super(context);
    }

    public NotesText(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
    }

    public NotesText(Context context, AttributeSet attrs) {
	super(context, attrs);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
	InputConnection connection = super.onCreateInputConnection(outAttrs);
	int imeActions = outAttrs.imeOptions & EditorInfo.IME_MASK_ACTION;
	if ((imeActions & EditorInfo.IME_ACTION_DONE) != 0) {
	    // clear the existing action
	    outAttrs.imeOptions ^= imeActions;
	    // set the DONE action
	    outAttrs.imeOptions |= EditorInfo.IME_ACTION_DONE;
	}
	if ((outAttrs.imeOptions & EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0) {
	    outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
	}
	return connection;
    }
}
