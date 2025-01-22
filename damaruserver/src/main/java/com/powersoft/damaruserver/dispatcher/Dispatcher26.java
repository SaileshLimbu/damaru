package com.powersoft.damaruserver.dispatcher;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_POINTER_DOWN;
import static android.view.MotionEvent.ACTION_POINTER_UP;
import static android.view.MotionEvent.ACTION_UP;

import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.graphics.Path;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;

import com.powersoft.damaruserver.service.DeviceControlService;

import java.lang.ref.WeakReference;

/**
 * The type Dispatcher 26.
 */
class Dispatcher26 extends DispatcherBase {

    WeakReference<Context> context;

    private boolean f2145a = false;

    private final StrokeDescription[] strokeDescriptons = new StrokeDescription[10];

    private static class StrokeDescription {

        /**
         * The Has stroke.
         */
        public boolean hasStroke;

        /**
         * The F 2148 b.
         */
        public int pointerId;

        /**
         * The Path.
         */
        public final Path path = new Path();

        /**
         * The Stroke description.
         */
        public GestureDescription.StrokeDescription strokeDescription;

        /**
         * The F 2151 e.
         */
        public float xAxis;

        /**
         * The F 2152 f.
         */
        public float yAxis;

        /**
         * Instantiates a new Stroke description.
         *
         * @param dVar the d var
         */
        public StrokeDescription(Dispatcher26 dVar) {
            reset();
        }

        /**
         * Reset.
         */
        public void reset() {
            this.hasStroke = false;
            this.pointerId = 0;
            this.path.reset();
            this.strokeDescription = null;
            this.xAxis = 0.0f;
            this.yAxis = 0.0f;
        }

        /**
         * Click.
         *
         * @param i  the
         * @param f  the f
         * @param f2 the f 2
         */
        public void click(int i, float f, float f2) {
            this.hasStroke = true;
            this.pointerId = i;
            this.path.reset();
            this.path.moveTo(f, f2);
            this.xAxis = f;
            this.yAxis = f2;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.strokeDescription = new GestureDescription.StrokeDescription(this.path, 0, 10, true);
            }
        }

        /**
         * Drag.
         *
         * @param f  the f
         * @param f2 the f 2
         * @param z  the z
         */
        public void drag(float f, float f2, boolean z) {
            this.path.reset();
            this.path.moveTo(this.xAxis, this.yAxis);
            this.path.lineTo(f, f2);
            this.xAxis = f;
            this.yAxis = f2;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
                try {
                    this.strokeDescription = this.strokeDescription.continueStroke(this.path, 0, 10, z);
                }catch (Exception e){
                    //ignore
                }
            }
        }
    }

    /**
     * Instantiates a new Dispatcher 26.
     */
    public Dispatcher26(WeakReference<Context> context) {
        for (int i = 0; i < 10; i++) {
            this.strokeDescriptons[i] = new StrokeDescription(this);
        }
        this.context = context;

    }

    @Override
    WeakReference<Context> getWeakReference() {
        return context;
    }

    private StrokeDescription getStrokeDescription() {
        for (StrokeDescription aVar : this.strokeDescriptons) {
            if (!aVar.hasStroke) {
                return aVar;
            }
        }
        return null;
    }


    public boolean dispatch(MotionEvent motionEvent) {
        Log.i("Dispatcher", "dispatch: " + motionEvent);
        DeviceControlService a = DeviceControlService.Companion.getInstance();
        int i = 0;
        if (a == null) {
            return false;
        }
        int actionMasked = motionEvent.getActionMasked();
        int actionIndex = motionEvent.getActionIndex();
        int pointerId = motionEvent.getPointerId(actionIndex);
        float x = motionEvent.getX(actionIndex);
        float y = motionEvent.getY(actionIndex);
        if (actionMasked == ACTION_DOWN) {
            resetAll();
            StrokeDescription a2 = getStrokeDescription();
            if (a2 != null) {
                this.f2145a = true;
                a2.click(pointerId, x, y);
                GestureDescription.Builder builder = new GestureDescription.Builder();
                builder.addStroke(a2.strokeDescription);
                return a.dispatchGesture(builder.build(), null, null);
            }
        } else if (actionMasked == ACTION_POINTER_DOWN) {
            StrokeDescription a3 = getStrokeDescription();
            if (this.f2145a && a3 != null) {
                GestureDescription.Builder builder2 = new GestureDescription.Builder();
                StrokeDescription[] aVarArr = this.strokeDescriptons;
                int length = aVarArr.length;
                while (i < length) {
                    StrokeDescription aVar = aVarArr[i];
                    if (aVar.hasStroke) {
                        aVar.click(aVar.pointerId, aVar.xAxis, aVar.yAxis);
                        builder2.addStroke(aVar.strokeDescription);
                    }
                    i++;
                }
                a3.click(pointerId, x, y);
                builder2.addStroke(a3.strokeDescription);
                return a.dispatchGesture(builder2.build(), null, null);
            }
        } else if (actionMasked == ACTION_MOVE) {
            if (this.f2145a) {
                GestureDescription.Builder builder3 = new GestureDescription.Builder();
                int pointerCount = motionEvent.getPointerCount();
                while (i < pointerCount) {
                    StrokeDescription b = m2883b(motionEvent.getPointerId(i));
                    if (b != null) {
                        b.drag(motionEvent.getX(i), motionEvent.getY(i), true);
                        builder3.addStroke(b.strokeDescription);
                    }
                    i++;
                }
                return a.dispatchGesture(builder3.build(), null, null);
            }
        } else if (actionMasked == ACTION_POINTER_UP) {
            StrokeDescription b2 = m2883b(pointerId);
            if (this.f2145a && b2 != null) {
                b2.drag(x, y, false);
                GestureDescription.Builder builder4 = new GestureDescription.Builder();
                builder4.addStroke(b2.strokeDescription);
                StrokeDescription[] aVarArr2 = this.strokeDescriptons;
                int length2 = aVarArr2.length;
                while (i < length2) {
                    StrokeDescription aVar2 = aVarArr2[i];
                    if (aVar2.hasStroke && aVar2 != b2) {
                        aVar2.drag(aVar2.xAxis, aVar2.yAxis, true);
                        builder4.addStroke(aVar2.strokeDescription);
                    }
                    i++;
                }
                b2.reset();
                return a.dispatchGesture(builder4.build(), null, null);
            }
        } else if (actionMasked == ACTION_UP) {
            StrokeDescription b3 = m2883b(pointerId);
            if (this.f2145a && b3 != null) {
                b3.drag(x, y, false);
                GestureDescription.Builder builder5 = new GestureDescription.Builder();
                builder5.addStroke(b3.strokeDescription);
                this.f2145a = false;
                resetAll();
                return a.dispatchGesture(builder5.build(), null, null);
            }
        } else if (actionMasked == ACTION_CANCEL) {
            this.f2145a = false;
            resetAll();
            return true;
        }
        return false;
    }

    private void resetAll() {
        for (StrokeDescription a : this.strokeDescriptons) {
            a.reset();
        }
    }

    private StrokeDescription m2883b(int i) {
        for (StrokeDescription aVar : this.strokeDescriptons) {
            if (aVar.hasStroke && aVar.pointerId == i) {
                return aVar;
            }
        }
        return null;
    }
}
