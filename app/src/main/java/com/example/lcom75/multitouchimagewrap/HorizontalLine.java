package com.example.lcom75.multitouchimagewrap;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import static android.view.MotionEvent.INVALID_POINTER_ID;
import static com.example.lcom75.multitouchimagewrap.GridFrameLayout.HORIZONTAL_LINE;
import static com.example.lcom75.multitouchimagewrap.GridFrameLayout.LINE_SIZE_IN_DP;
import static com.example.lcom75.multitouchimagewrap.GridFrameLayout.VERTICAL_LINE;

/**
 * Created by lcom75 on 5/7/16.
 */

public class HorizontalLine extends View implements ScaleGestureDetector.OnScaleGestureListener {

    private int mActivePointerId = INVALID_POINTER_ID;
    private ScaleGestureDetector mScaleDetector;
    private float mLastTouchX = 0;
    private float mLastTouchY = 0;
    private float mPosX, mPosY;
    private static final String TAG = "HorizontalLine";
    Path path;
    Paint paintSimple;
    PointF[] pathPoint = new PointF[VERTICAL_LINE];

    public void onAnchorPositionChanged(int index, float rawX, float rawY) {
        if (index < pathPoint.length) {
            pathPoint[index] = new PointF(rawX, rawY);
        }
    }

    public HorizontalLine(Context context) {
        super(context);
        init(context);
    }

    public HorizontalLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HorizontalLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HorizontalLine(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mScaleDetector = new ScaleGestureDetector(context, this);
        path = new Path();
        paintSimple = new Paint();
        paintSimple.setColor(Color.RED);
        paintSimple.setStyle(Paint.Style.STROKE);
        paintSimple.setStrokeJoin(Paint.Join.ROUND);
        paintSimple.setStrokeWidth(5);
        paintSimple.setAntiAlias(true);
        for (int i = 0; i < pathPoint.length; i++) {
            pathPoint[i] = new PointF(0, 0);
        }
    }

    int l, t, r, b;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        l = getLeft();
        t = getTop();
        r = getRight();
        b = getBottom();
        Log.d(TAG, "onDraw: " + l + ":" + t + ":" + r + ":" + b);
        Log.d(TAG, "onDraw Point: " + mLastTouchX + ":" + mLastTouchY);
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        path.reset();
//        if (mLastTouchX < 0 || mLastTouchY < 0) {
        path.moveTo(l, (b - t) / 2);
//        path.lineTo(r, (b - t) / 2);
//        path.quadTo(30, 736, (r - l) / 2, b);
        float lastPointX = 0, lastPointY = 0;
        lastPointX = l;
        lastPointY = (b - t) / 2;
        for (int i = 0; i < pathPoint.length; i++) {
            mLastTouchX = pathPoint[i].x;
            mLastTouchY = pathPoint[i].y;
            if (mLastTouchX == 0 || mLastTouchY == 0) {
//            path.quadTo(0,24,1080,24);
                path.quadTo(l, (b - t) / 2, r, (b - t) / 2);
                Log.d(TAG, "onDraw() Quad called with: left = [" + l + "], top = [" + ((b - t) / 2) + "], right = [" + r + "], bottom = [" + ((b - t) / 2) + "]");
            } else {
                path.quadTo(lastPointX, lastPointY, mLastTouchX, mLastTouchY);
                Log.d(TAG, "onDraw: Quad called with: left = [" + mLastTouchX + "], top = [" + mLastTouchY + "], right = [" + r + "], bottom = [" + ((b - t) / 2) + "]");
            }
            lastPointX = mLastTouchX;
            lastPointY = mLastTouchY;
        }
        path.close();
        canvas.drawPath(path, paintSimple);
//        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                path = new Path();
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                // Remember where we started (for dragging)
                mLastTouchX = x;
                mLastTouchY = y;
                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
//                path.moveTo(0, 0);
//                path.moveTo(mLastTouchX, mLastTouchY);
                break;
            }
            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                final int pointerIndex =
                        MotionEventCompat.findPointerIndex(ev, mActivePointerId);

                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;
                mPosX += dx;
                mPosY += dy;
                invalidate();
                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;
//                path.lineTo(mLastTouchX, mLastTouchY);
                break;
            }


            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
                    mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                break;
            }
        }
        return true;
    }


    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        Log.d(TAG, "onScale() called with: scaleGestureDetector = [" + scaleGestureDetector.getCurrentSpanX() + "," + scaleGestureDetector.getCurrentSpanY() + "]");
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        Log.d(TAG, "onScaleBegin() called with: scaleGestureDetector = [" + scaleGestureDetector.getCurrentSpanX() + "," + scaleGestureDetector.getCurrentSpanY() + "]");
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        Log.d(TAG, "onScaleEnd() called with: scaleGestureDetector = [" + scaleGestureDetector.getCurrentSpanX() + "," + scaleGestureDetector.getCurrentSpanY() + "]");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.displayMetrics.widthPixels, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(LINE_SIZE_IN_DP), MeasureSpec.EXACTLY));
    }
}
