package com.example.lcom75.multitouchimagewrap;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
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

/**
 * Created by lcom75 on 5/7/16.
 */

public class VerticalLine extends View {//} implements ScaleGestureDetector.OnScaleGestureListener {

    private int mActivePointerId = INVALID_POINTER_ID;
    private ScaleGestureDetector mScaleDetector;
    private float mLastTouchX = 0;
    private float mLastTouchY = 0;
    private float mPosX, mPosY;
    private static final String TAG = "VerticalLine";
    Path path;
    Paint paintSimple;
    PointF[] pathPoint = new PointF[HORIZONTAL_LINE];
    public static PointF firstPoint = null;

    public void onAnchorPositionChanged(int index, float rawX, float rawY) {
        if (index < pathPoint.length) {
            pathPoint[index] = new PointF(rawX, rawY);
            Log.d(TAG, "onAnchorPositionChanged() called with: index = [" + index + "], rawX = [" + rawX + "], rawY = [" + rawY + "]");
        }
    }


    public VerticalLine(Context context) {
        super(context);
        init(context);

    }

    public VerticalLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VerticalLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VerticalLine(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
//        mScaleDetector = new ScaleGestureDetector(context, this);
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
        pathPoint[0] = new PointF(202.05478f, 310.05103f);
//        pathPoint[0] = new PointF(96f, 450.0f);
        firstPoint = pathPoint[0];
//        pathPoint[1] = new PointF(120.00557f, 450.0f);

    }

    int l, t, r, b;

    private static Path generateWaveShape() {
        Path wave = new Path();
        wave.moveTo(-15, 0);
        wave.cubicTo(-15, 10, -5, 10, -5, 0);
        wave.cubicTo(-5, 10, 5, 10, 5, 0);
        wave.cubicTo(5, 10, 15, 10, 15, 0);
        wave.lineTo(15, 35);
        wave.lineTo(-15, 35);
        wave.close();

        return wave;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        l = getLeft();
        t = getTop();
        r = getRight();
        b = getBottom();
        Log.d(TAG, "onDraw: " + l + ":" + t + ":" + r + ":" + b);
        path.reset();
//        path = generateWaveShape();
        path.moveTo((r - l) / 2, t);
//        for (int i = 0; i < pathPoint.length; i++) {
        mLastTouchX = pathPoint[0].x;
        mLastTouchY = pathPoint[0].y - l;
        Log.d(TAG, "onDraw Point: " + mLastTouchX + ":" + mLastTouchY);
        if ((pathPoint[1].x == 0 || pathPoint[1].y == 0) && (pathPoint[0].x == 0 || pathPoint[0].y == 0)) {
            path.lineTo((r - l) / 2, b);
        } else if (pathPoint[1].x == 0 || pathPoint[1].y == 0) {
            path.lineTo(mLastTouchX, mLastTouchY);
            path.lineTo((r - l) / 2, b);
        } else {
//            int diff = 30;
//            path.quadTo(mLastTouchX, mLastTouchY, pathPoint[1].x, pathPoint[1].y - diff);
//            path.quadTo(pathPoint[1].x, pathPoint[1].y - diff, pathPoint[1].x, pathPoint[1].y + diff);
//            path.quadTo(pathPoint[1].x, pathPoint[1].y + diff, (r - l) / 2, b);
            path.lineTo(mLastTouchX, mLastTouchY);
            path.lineTo(pathPoint[1].x, pathPoint[1].y);
            path.lineTo((r - l) / 2, b);
        }
        path.close();
        canvas.drawPath(path, paintSimple);

    }

    int[] interpolate(int x0, int y0, int x1, int y1, int x2, int y2, double t) {
        double t1 = 1.0 - t;
        double tSq = t * t;
        double denom = 2.0 * t * t1;
        int cx = (int) ((x1 - t1 * t1 * x0 - tSq * x2) / denom);
        int cy = (int) ((y1 - t1 * t1 * y0 - tSq * y2) / denom);
        return new int[]{cx, cy};
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        // Let the ScaleGestureDetector inspect all events.
//        mScaleDetector.onTouchEvent(ev);
//        final int action = MotionEventCompat.getActionMasked(ev);
//        switch (action) {
//            case MotionEvent.ACTION_DOWN: {
//                path = new Path();
//                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
//                final float x = MotionEventCompat.getX(ev, pointerIndex);
//                final float y = MotionEventCompat.getY(ev, pointerIndex);
//                // Remember where we started (for dragging)
//                mLastTouchX = x;
//                mLastTouchY = y;
//                // Save the ID of this pointer (for dragging)
//                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
////                path.moveTo(0, 0);
////                path.moveTo(mLastTouchX, mLastTouchY);
//                break;
//            }
//            case MotionEvent.ACTION_UP: {
//                mActivePointerId = INVALID_POINTER_ID;
//                break;
//            }
//
//            case MotionEvent.ACTION_MOVE: {
//                // Find the index of the active pointer and fetch its position
//                final int pointerIndex =
//                        MotionEventCompat.findPointerIndex(ev, mActivePointerId);
//
//                final float x = MotionEventCompat.getX(ev, pointerIndex);
//                final float y = MotionEventCompat.getY(ev, pointerIndex);
//                // Calculate the distance moved
//                final float dx = x - mLastTouchX;
//                final float dy = y - mLastTouchY;
//                mPosX += dx;
//                mPosY += dy;
//                invalidate();
//                // Remember this touch position for the next move event
//                mLastTouchX = x;
//                mLastTouchY = y;
////                path.lineTo(mLastTouchX, mLastTouchY);
//                break;
//            }
//
//
//            case MotionEvent.ACTION_CANCEL: {
//                mActivePointerId = INVALID_POINTER_ID;
//                break;
//            }
//
//            case MotionEvent.ACTION_POINTER_UP: {
//                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
//                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
//                if (pointerId == mActivePointerId) {
//                    // This was our active pointer going up. Choose a new
//                    // active pointer and adjust accordingly.
//                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
//                    mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
//                    mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
//                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
//                }
//                break;
//            }
//        }
//        return true;
//    }
//
//
//    @Override
//    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
//        Log.d(TAG, "onScale() called with: scaleGestureDetector = [" + scaleGestureDetector.getCurrentSpanX() + "," + scaleGestureDetector.getCurrentSpanY() + "]");
//        return false;
//    }
//
//    @Override
//    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
//        Log.d(TAG, "onScaleBegin() called with: scaleGestureDetector = [" + scaleGestureDetector.getCurrentSpanX() + "," + scaleGestureDetector.getCurrentSpanY() + "]");
//        return false;
//    }
//
//    @Override
//    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
//        Log.d(TAG, "onScaleEnd() called with: scaleGestureDetector = [" + scaleGestureDetector.getCurrentSpanX() + "," + scaleGestureDetector.getCurrentSpanY() + "]");
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.displayMetrics.heightPixels - AndroidUtilities.getCurrentActionBarHeight(), MeasureSpec.AT_MOST));
    }
}
