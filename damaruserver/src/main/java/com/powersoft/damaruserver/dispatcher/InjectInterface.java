package com.powersoft.damaruserver.dispatcher;

import android.view.KeyEvent;
import android.view.MotionEvent;

public interface InjectInterface {

    boolean injectKeyEvent(KeyEvent keyEvent);

    boolean dispatch(MotionEvent motionEvent);
}
