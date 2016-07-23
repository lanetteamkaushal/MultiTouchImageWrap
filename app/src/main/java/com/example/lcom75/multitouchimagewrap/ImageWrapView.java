package com.example.lcom75.multitouchimagewrap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;

public class ImageWrapView extends View {

    private static final String TAG = "ImageWrapView";
    private static final int WIDTH = 4;
    private static final int HEIGHT = 3;
    private static final int COUNT = (WIDTH + 1) * (HEIGHT + 1);
    private static float spaceX, spaceY;
    private final float[] mVerts = new float[COUNT * 2];
    private final float[] mOrig = new float[COUNT * 2];
    private final Matrix mMatrix = new Matrix();
    private final Matrix mInverse = new Matrix();
    PointF redoEvent, undoEvent;
    RectF topRect, leftRect, bottomRect, rightRect;
    private Bitmap mBitmap;
    private float clickX, clickY;
    private Paint linePaint, p2;
    private float[] dst, undo; //Global
    private float w, h;
    private int count = 0;
    /***
     * Allowed max distance till do not show point
     */
    private float toleranceWidth, toleranceHeight;
    private int xPosition = 0;
    private int yPosition = 0;
    private Path path1 = new Path();
    private Path path2 = new Path();
    private Path path3 = new Path();
    private Path path4 = new Path();
    private Path path5 = new Path();
    private int mLastWarpX = -9999; // don't match a touch coordinate
    private int mLastWarpY;

    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(800, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(720, MeasureSpec.EXACTLY));
    }*/
    private Paint redArea;

    public ImageWrapView(Context context) {
        super(context);
        init();
    }

    public ImageWrapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageWrapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private static void setXY(float[] array, int index, float x, float y) {

        if (index == 1)
            spaceX = x;

        if (index == 5)
            spaceY = y;

        array[index * 2 + 0] = x;
        array[index * 2 + 1] = y;

    }

    private void init() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.autumn);

        w = mBitmap.getWidth();
        h = mBitmap.getHeight();
        toleranceWidth = Math.max((((w - (16 * WIDTH)) / (WIDTH)) / 4), 16);
        toleranceHeight = Math.max((((h - (16 * HEIGHT)) / (HEIGHT)) / 4), 16);
        Log.d(TAG, "init: " + toleranceWidth + "::" + toleranceHeight);
        // construct our mesh
        int index = 0;
        for (int y = 0; y <= HEIGHT; y++) {
            float fy = h * y / HEIGHT;
            for (int x = 0; x <= WIDTH; x++) {
                float fx = w * x / WIDTH;
                Log.e(TAG, "setX " + fx + "setY " + fy + "index " + index);
                setXY(mVerts, index, fx, fy);
                setXY(mOrig, index, fx, fy);
                index += 1;
                Log.d(TAG, "SampleView: " + x + ":" + y + ":" + w + ":" + WIDTH + ":fx:" + fx + ":" + fy);
                dst = mVerts;//Assign dst here just once
            }
        }
        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(5);

        redArea = new Paint();
        redArea.setColor(Color.argb(0, 0, 0, 0));
        redArea.setStyle(Paint.Style.FILL);

//        mMatrix.setTranslate(10, 10);
//        mMatrix.invert(mInverse);

        p2 = new Paint();
        p2.setColor(Color.WHITE);

        undoEvent = new PointF(0, 0);
        redoEvent = new PointF(0, 0);

        topRect = new RectF();
        leftRect = new RectF();
        bottomRect = new RectF();
        rightRect = new RectF();

        topRect.set(-16, -16, w, toleranceHeight);
        leftRect.set(-16, -16, toleranceWidth, h);
        rightRect.set((w - toleranceWidth), -16, w + 16, h + 16);
        bottomRect.set(-16, (h - toleranceHeight), w + 16, h + 16);
    }


    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawColor(0xFFCCCCCC);
        if (getWidth() > 0 && getHeight() > 0) {
            Log.d(TAG, "onDraw: Height :" + getHeight() + ":" + getWidth()
                    + ":Bitmap:" + w + ":" + h);
        }

        path1.reset();
        path2.reset();
        path3.reset();
        path4.reset();
        path5.reset();

        canvas.concat(mMatrix);
        if (mBitmap != null)
            canvas.drawBitmapMesh(mBitmap, WIDTH, HEIGHT, mVerts, 0, null, 0,
                    null);
        Paint p1 = new Paint();
        p1.setColor(Color.TRANSPARENT);

        Paint p3 = new Paint();
        p3.setColor(Color.TRANSPARENT);

        for (int i = 0; i < COUNT * 2; i += 2) {

            count++;
            float x = mVerts[i + 0];
            float y = mVerts[i + 1];


            if ((int) (toleranceWidth - x) > 0
                    || (int) (toleranceHeight - y) > 0
                    || (Math.abs(w - x) <= toleranceWidth)
                    || Math.abs(h - y) <= toleranceHeight) {
                if (i > 10 && i < 30) {
                    Log.e(TAG, ":i:" + i + "X:" + x + " Y : " + y + ":toleranceWidth:" + toleranceWidth + ":toleranceHeight:" + toleranceHeight);
                    Log.d(TAG, "onDraw: Skip");
                }
            } else {
                canvas.drawCircle(x, y, 16, p2);
            }
        }

        for (int i = 0; i < COUNT * 2; i += 2) {
            count++;
            xPosition = i;
            yPosition = i + 1;
            float x = mVerts[i + 0];
            float y = mVerts[i + 1];
//            Log.e(TAG, ":i:" + i + "X:" + x + " Y : " + y);
            int remain = xPosition / (10);
            int pos = xPosition / 2;
            switch (remain) {
                case 0:
                    break;
                case 1:
                    if (pos == 5) {
                        if (path1 == null) {
                            path1 = new Path();
                        }
                        path1.reset();
                        path1.moveTo(x, y);
                    } else {
                        path1.lineTo(x, y);
                    }
                    break;
                case 2:
                    if (pos == 10) {
                        if (path2 == null) {
                            path2 = new Path();
                        }
                        path2.reset();
                        path2.moveTo(x, y);
                    } else {
                        path2.lineTo(x, y);
                    }
                    break;
                case 3:
                    break;
            }
        }


        for (int i = 0; i < COUNT * 2; i += 2) {
            count++;
            xPosition = i;
            yPosition = i + 1;
            float x = mVerts[i + 0];
            float y = mVerts[i + 1];
//            Log.e(TAG, ":i:" + i + "X:" + x + " Y : " + y);
            int remain = xPosition % (10);
            int pos = xPosition / 2;
            switch (remain) {
                case 0:
                    break;
                case 2:
                    if (pos == 1) {
                        if (path3 == null) {
                            path3 = new Path();
                        }
                        path3.reset();
                        path3.moveTo(x, y);
                    } else {
                        path3.lineTo(x, y);
                    }
                    break;
                case 4:
                    if (pos == 2) {
                        if (path4 == null) {
                            path4 = new Path();
                        }
                        path4.reset();
                        path4.moveTo(x, y);
                    } else {
                        path4.lineTo(x, y);
                    }
                    break;
                case 6:
                    if (pos == 3) {
                        if (path5 == null) {
                            path5 = new Path();
                        }
                        path5.reset();
                        path5.moveTo(x, y);
                    } else {
                        path5.lineTo(x, y);
                    }
                    break;
            }
        }
        canvas.drawPath(path1, linePaint);
        canvas.drawPath(path2, linePaint);
        canvas.drawPath(path3, linePaint);
        canvas.drawPath(path4, linePaint);
        canvas.drawPath(path5, linePaint);

        canvas.drawRect(topRect, redArea);
        canvas.drawRect(leftRect, redArea);
        canvas.drawRect(rightRect, redArea);
        canvas.drawRect(bottomRect, redArea);

        //For Log Only
        printDst(dst);
    }

    private void printDst(float[] dst) {
        StringBuilder printDstval = new StringBuilder();
        for (int i = 0; i < dst.length; i++) {
            printDstval.append("\"").append(dst[i]).append("\",");
        }
        Log.d(TAG, "printDst: " + printDstval.toString());
    }

    private void warp(float cx, float cy, float[] array, boolean doNotUndo) {
        final float K = 800;
        float[] src = array; //now you are applying wrap effect on the last effected pixels
        Log.d(TAG, "warp: Before Wrap");
        printDst(src);
        Log.d(TAG, "warp: " + cx + ":" + cy);
        for (int i = 0; i < COUNT * 2; i += 2) {
            float x = src[i + 0];
            float y = src[i + 1];
            float dx = cx - x;
            float dy = cy - y;
            float dd = dx * dx + dy * dy;
            float d = (float) Math.sqrt(dd);
            float pull = K / (dd + 0.000001f);

            pull /= (d + 0.000001f);

            if (pull >= 1) {
                dst[i + 0] = cx;
                dst[i + 1] = cy;
            } else {
                dst[i + 0] = x + dx * pull;
                dst[i + 1] = y + dy * pull;
            }
        }

        if (!doNotUndo)
            undo = dst;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        clickX = event.getX();
        clickY = event.getY();
        if (topRect.contains(clickX, clickY)) {
            Log.d(TAG, "onTouchEvent: Ignore TOp REct Touch :" + clickX + ":" + clickY);
        } else if (leftRect.contains(clickX, clickY)) {
            Log.d(TAG, "onTouchEvent: Ignore Left Rect Touch :" + clickX + ":" + clickY);
        } else if (rightRect.contains(clickX, clickY)) {
            Log.d(TAG, "onTouchEvent: Ignore Right Rect Touch :" + clickX + ":" + clickY);
        } else if (bottomRect.contains(clickX, clickY)) {
            Log.d(TAG, "onTouchEvent: Ignore Bottom Rect Touch :" + clickX + ":" + clickY);
        } else {
            if (mLastWarpX != clickX || mLastWarpY != clickY) {
                mLastWarpX = (int) clickX;
                mLastWarpY = (int) clickY;

                warp(clickX, clickY, dst, false);
                undoEvent.set(clickX, clickY);
                invalidate();
            }
        }
        return true;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        mBitmap = imageBitmap;
        w = mBitmap.getWidth();
        h = mBitmap.getHeight();
        toleranceWidth = Math.max(((w - (16 * WIDTH)) / (WIDTH)) / 4, 16);
        toleranceHeight = Math.max(((h - (16 * HEIGHT)) / (HEIGHT)) / 4, 16);
        // construct our mesh
        int index = 0;
        for (int y = 0; y <= HEIGHT; y++) {
            float fy = h * y / HEIGHT;
            for (int x = 0; x <= WIDTH; x++) {
                float fx = w * x / WIDTH;
                setXY(mVerts, index, fx, fy);
                setXY(mOrig, index, fx, fy);
                index += 1;
                Log.d(TAG, "SampleView: " + x + ":" + y + ":" + w + ":" + WIDTH + ":fx:" + fx + ":" + fy);
                dst = mVerts;//Assign dst here just once
            }
        }

//        mMatrix.setTranslate(10, 10);
//        mMatrix.invert(mInverse);

        p2.setColor(Color.WHITE);
        linePaint.setColor(Color.WHITE);
        invalidate();
    }

    public void clearGrid() {
        p2.setColor(Color.TRANSPARENT);
        linePaint.setColor(Color.TRANSPARENT);

        invalidate();
    }

    public void redoEffect() {
        Log.d(TAG, "redoEffect: In REDO");
        if (redoEvent.x != 0 || redoEvent.y != 0)
            warp(redoEvent.x, redoEvent.y, undo, true);
        invalidate();

        redoEvent.set(0, 0);
    }

    public void undoEffect() {
        Log.d(TAG, "undoEffect: In ENDO");
        redoEvent.set(undoEvent);
        warpToOriginal(mLastWarpX, mLastWarpY, mOrig);
        invalidate();
    }

    private void warpToOriginal(float cx, float cy, float[] array) {
        undo = Arrays.copyOf(dst, dst.length);
        final float K = 800;
        float[] src = array; //now you are applying wrap effect on the last effected pixels
        for (int i = 0; i < COUNT * 2; i += 2) {
            float x = src[i + 0];
            float y = src[i + 1];
            float dx = cx - x;
            float dy = cy - y;
            float dd = dx * dx + dy * dy;
            float d = (float) Math.sqrt(dd);
            float pull = K / (dd + 0.000001f);
            pull /= (d + 0.000001f);
            // android.util.Log.d("skia", "index " + i + " dist=" + d +
            // " pull=" + pull);
            if (pull >= 1) {
                dst[i + 0] = cx;
                dst[i + 1] = cy;
            } else {
                dst[i + 0] = x + dx * pull;
                dst[i + 1] = y + dy * pull;
            }
        }
    }

}
