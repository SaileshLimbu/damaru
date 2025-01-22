package com.powersoft.damaruserver.dispatcher;


import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * The type Inject base.
 */
abstract class InjectBase implements InjectInterface {
    abstract WeakReference<Context> getWeakReference();

}
