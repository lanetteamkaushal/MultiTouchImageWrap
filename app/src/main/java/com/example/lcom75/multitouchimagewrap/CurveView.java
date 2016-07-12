package com.example.lcom75.multitouchimagewrap;

/**
 * Created by lcom75 on 12/7/16.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class CurveView extends View {

    private static final String TAG = "CurveView";

    public static final int MODE_LEFT = 0x00000008;
    public static final int MODE_TOP = 0x00000004;
    public static final int MODE_RIGHT = 0x00000002;
    public static final int MODE_BOTTOM = 0x00000001;

    private int mode = MODE_TOP;

    private int width, height;

    private Paint paint;
    private Paint circlePaint;
    private Path path, path1;

    private float offset;

    public CurveView(Context context) {
        this(context, null);
    }

    public CurveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CurveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        path = new Path();
        path1 = new Path();
        paint.setColor(Color.RED);
        setMode(MODE_LEFT);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.GREEN);
        circlePaint.setStyle(Paint.Style.FILL);
        setOffset(300);
    }

    public void setMode(int mode) {
        this.mode = mode;
        invalidate();
    }

    public int getMode() {
        return mode;
    }

    public void setOffset(float f) {
        this.offset = f;
        invalidate();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    public void onDraw(Canvas canvas) {
        float bottomLeftX = 0;
        float bottomLeftY = height;
        float topLeftX = 0;
        float topLeftY = 0;
        float topRightX = width;
        float topRightY = 0;
        float bottomRightX = width;
        float bottomRightY = height;

        if ((mode & MODE_LEFT) > 0) {
            bottomLeftX = offset;
            topLeftX = offset;
        }
        if ((mode & MODE_TOP) > 0) {
            topLeftY = offset;
            topRightY = offset;
        }
        if ((mode & MODE_RIGHT) > 0) {
            topRightX = width - offset;
            bottomRightX = width - offset;
        }
        if ((mode & MODE_BOTTOM) > 0) {
            bottomRightY = height - offset;
            bottomLeftY = height - offset;
        }

//        path.reset();
//        path.moveTo(0,0);
//        path.quadTo(width/3,height/2,width/3,height/2);
//        path.moveTo(0, bottomLeftY);
//        path.quadTo(0, height / 2, topLeftX, topLeftY);
//
//        circlePaint.setColor(Color.GREEN);
//        canvas.drawCircle(topLeftX, topLeftY, AndroidUtilities.dp(8), circlePaint);
//        circlePaint.setColor(Color.BLACK);
//        canvas.drawCircle(0, height / 2, AndroidUtilities.dp(8), circlePaint);
//        circlePaint.setColor(Color.BLACK);
//        canvas.drawCircle(topLeftX, topLeftY,AndroidUtilities.dp(8), circlePaint);

//        path.quadTo(width / 2, 0, topRightX, topRightY);
//        path.quadTo(width, height / 2, bottomRightX, bottomRightY);
//        path.quadTo(width / 2, height, bottomLeftX, bottomLeftY);
//        path.close();

//        canvas.drawPath(path, paint);
        path1.reset();
        path1.moveTo(bottomRightX / 2, 0);
        circlePaint.setColor(Color.GREEN);
        canvas.drawCircle(bottomRightX / 2, 0, AndroidUtilities.dp(8), circlePaint);

        path1.quadTo(0, 0,width / 2, height / 2);
        canvas.drawPath(path1, paint);

        circlePaint.setColor(Color.BLUE);
        canvas.drawCircle(width / 2, height / 2, AndroidUtilities.dp(8), circlePaint);
        circlePaint.setColor(Color.YELLOW);
        canvas.drawCircle(0, 0, AndroidUtilities.dp(8), circlePaint);
//
//        circlePaint.setColor(Color.MAGENTA);
//        canvas.drawCircle(width, height / 2,AndroidUtilities.dp(8), circlePaint);
//        canvas.drawCircle(bottomRightX, bottomRightY,AndroidUtilities.dp(8), circlePaint);
    }
}

