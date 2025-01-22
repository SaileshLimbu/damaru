package com.powersoft.damaruserver.overlay;

import static android.content.Context.WINDOW_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * The type Overlay view.
 */
 class OverlayViewRemote extends OverlayView {


    private CustomTimeAnimator animatedView = null;

    private boolean viewAdded;

    private boolean f5255e;

    private int pointerWidth;

    private int pointerHeight;

    private int pointerX;

    private int pointerY;

    private final Runnable hideCallback = new HideCallbackRunnable();

    /**
     * The type Hide callback runnable.
     */
    class HideCallbackRunnable implements Runnable {
        /**
         * Instantiates a new Hide callback runnable.
         */
        HideCallbackRunnable() {
        }

        public void run() {
            removeView();
        }
    }

    /**
     * The type Layout runnable.
     */
    class LayoutRunnable implements Runnable {
        /**
         * Instantiates a new Layout runnable.
         */
        LayoutRunnable() {
        }

        public void run() {
            setAnimatedViewParam();
        }
    }

    /**
     * Instantiates a new Overlay view.
     *
     * @param context the context
     */
    public OverlayViewRemote(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        Log.d("TAG", "init: Start");
        this.viewAdded = false;
        Point pointerSize = getPointerSize();
        this.pointerWidth = pointerSize.x;
        this.pointerHeight = pointerSize.y;
        animatedView = new CustomTimeAnimator(context);
        this.animatedView.setVisibility(View.INVISIBLE);
        addView(this.animatedView, new LayoutParams(this.pointerWidth, this.pointerHeight));
        this.animatedView.setHideCallback(this.hideCallback);
        Log.d("TAG", "init: End");
    }

    @SuppressLint("WrongConstant")
    private void addView() {
        try {
            Point pointerSize = getPointerSize();
            if (!(pointerSize.x == this.pointerWidth && pointerSize.y == this.pointerHeight)) {
                this.pointerWidth = pointerSize.x;
                this.pointerHeight = pointerSize.y;
                CustomTimeAnimator gVar = this.animatedView;
                if (gVar != null) {
                    LayoutParams layoutParams = (LayoutParams) gVar.getLayoutParams();
                    layoutParams.width = this.pointerWidth;
                    layoutParams.height = this.pointerHeight;
                    gVar.setLayoutParams(layoutParams);
                }
            }
            WindowManager.LayoutParams layoutParams2 = new WindowManager.LayoutParams(-1, -1, 0, 0, getOverlayWindowType(), 792, -3);
            layoutParams2.gravity = 51;
            layoutParams2.setTitle("Pointer");
            this.f5255e = true;
            ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).addView(this, layoutParams2);
            this.viewAdded = true;
        } catch (Exception e) {

            Log.d("helo", "");
        }
    }


    /**
     * Sets animated view param.
     */
    public void setAnimatedViewParam() {
        CustomTimeAnimator gVar = this.animatedView;
        if (gVar != null) {
            int[] iArr = new int[2];
            getLocationOnScreen(iArr);
            int i = this.pointerX - iArr[0];
            int i2 = this.pointerY - iArr[1];
            int width = gVar.getWidth();
            int height = gVar.getHeight();
            LayoutParams layoutParams = (LayoutParams) gVar.getLayoutParams();
            layoutParams.leftMargin = i - (width / 2);
            layoutParams.topMargin = i2 - (height / 2);
            layoutParams.rightMargin = -width;
            layoutParams.bottomMargin = -height;
            gVar.setLayoutParams(layoutParams);
        }
    }

    private int getOverlayWindowType() {
        return VERSION.SDK_INT >= 26 ? 2038 : 2006;
    }

    private Point getPointerSize() {
        WindowManager wm = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        float b = displayMetrics.widthPixels;
        float c = displayMetrics.heightPixels;
        Point point = new Point();
        point.x = (int) ((b * 48.0f) / 160.0f);
        point.y = (int) ((c * 48.0f) / 160.0f);
        return point;
    }

    /**
     * Add overlay view.
     */
    public void addOverlayView() {
        if (!this.viewAdded) {
            addView();
        }
        CustomTimeAnimator gVar = this.animatedView;
        if (gVar != null) {
            gVar.mo6746b();
        }
    }

    /**
     * Mo 6740 c.
     */
    public void mo6740c() {
        CustomTimeAnimator gVar = this.animatedView;
        if (gVar != null) {
            gVar.mo6748d();
        }
    }

    /**
     * Remove view.
     */
    public void removeView() {
        try {
            ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).removeView(this);
        } catch (Exception e) {

        }
        this.viewAdded = false;
    }

    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.f5255e) {
            this.f5255e = false;
            post(new LayoutRunnable());
        }
    }

    /**
     * Destroy view.
     */
    public void destroyView() {
        removeView();
        CustomTimeAnimator gVar = this.animatedView;
        this.animatedView = null;
        if (gVar != null) {
            gVar.destroyAnimator();
        }
    }

    /**
     * Dispatch key.
     *
     * @param i  the
     * @param i2 the 2
     */
    public void dispatchKey(int i, int i2) {
        this.pointerX = i;
        this.pointerY = i2;
        if (!this.viewAdded) {
            addView();
        }
        setAnimatedViewParam();
        CustomTimeAnimator gVar = this.animatedView;
        if (gVar != null) {
            gVar.mo6747c();
        }
    }
}
