package com.example.lcom75.multitouchimagewrap;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by lcom75 on 5/7/16.
 */

public class GridFrameLayout extends FrameLayout {
    VerticalLine line1, line2, line3;
    HorizontalLine hLine1, hLine2, hLine3;
    private final Rect mTmpContainerRect = new Rect();
    private final Rect mTmpChildRect = new Rect();
    private int mLeftWidth;
    private int mRightWidth;
    private static final String TAG = "GridFrameLayout";

    public GridFrameLayout(Context context) {
        super(context);
        init(context);
    }

    public GridFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GridFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GridFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        line1 = new VerticalLine(context);
        line1.setBackgroundColor(Color.YELLOW);
        addView(line1);//, new FrameLayout.LayoutParams(100, LayoutParams.MATCH_PARENT, Gravity.CENTER));
        line2 = new VerticalLine(context);
        line2.setBackgroundColor(Color.BLUE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, LayoutParams.MATCH_PARENT);
        params.setMargins(20, 0, 0, 0);
        addView(line2);//, params);
        line3 = new VerticalLine(context);
        line3.setBackgroundColor(Color.LTGRAY);
        params = new FrameLayout.LayoutParams(100, LayoutParams.MATCH_PARENT, Gravity.RIGHT);
        params.setMargins(0, 0, 20, 0);
        addView(line3);//, params);

        hLine1 = new HorizontalLine(context);
        hLine1.setBackgroundColor(Color.YELLOW);
        addView(hLine1);
        hLine2 = new HorizontalLine(context);
        hLine2.setBackgroundColor(Color.GREEN);
        addView(hLine2);
        hLine3 = new HorizontalLine(context);
        hLine3.setBackgroundColor(Color.MAGENTA);
        addView(hLine3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInEditMode()) {

        }
        super.onDraw(canvas);
    }

    int vChildCount = 0, hChildCount = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        mLeftWidth = 0;
        mRightWidth = 0;
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (i == 0) {
                    mLeftWidth += Math.max(maxWidth,
                            child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                } else if (i == 2) {
                    mRightWidth += Math.max(maxWidth,
                            child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                } else {
                    maxWidth = Math.max(maxWidth,
                            child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                }
                maxHeight = Math.max(maxHeight,
                        child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }

        // Total width is the maximum width of all inner children plus the gutters.
        maxWidth += mLeftWidth + mRightWidth;

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        // Report our final dimensions.
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        int leftPos = getPaddingLeft();
        int rightPos = right - left - getPaddingRight();
        leftPos = (rightPos - (3 * AndroidUtilities.dp(16))) / 4;
        int singlePiece = leftPos;
        Log.d(TAG, "onLayout: SinglePiece:" + singlePiece);
        final int middleLeft = leftPos + mLeftWidth;
        final int middleRight = rightPos - mRightWidth;
        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();
//                if (i == 0) {
                mTmpContainerRect.left = leftPos + lp.leftMargin;
                mTmpContainerRect.right = leftPos + width + lp.rightMargin;
                leftPos = mTmpContainerRect.right + ((i + 1) * singlePiece);
                Log.d(TAG, "onLayout: SinglePiece:" + singlePiece + ":" + leftPos);
//                } else if (i == 2) {
//                    mTmpContainerRect.right = rightPos - lp.rightMargin;
//                    mTmpContainerRect.left = rightPos - width - lp.leftMargin;
//                    rightPos = mTmpContainerRect.left;
//                } else {
//                    mTmpContainerRect.left = middleLeft + lp.leftMargin;
//                    mTmpContainerRect.right = middleRight - lp.rightMargin;
//                }
                mTmpContainerRect.top = parentTop + lp.topMargin;
                mTmpContainerRect.bottom = parentBottom - lp.bottomMargin;
                // Use the child's gravity and size to determine its final
                // frame within its container.
                Gravity.apply(lp.gravity, width, height, mTmpContainerRect, mTmpChildRect);
                // Place the child.
                Log.d(TAG, "onLayout: " + mTmpChildRect.toString() + ":Position:" + i);
                child.layout(mTmpChildRect.left, mTmpChildRect.top,
                        mTmpChildRect.right, mTmpChildRect.bottom);
            }
        }

    }

    //    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        final int count = getChildCount();
//        mLeftWidth = 0;
//        mRightWidth = 0;
//        int leftPos = getPaddingLeft();
//        int rightPos = right - left - getPaddingRight();
//
//        final int middleLeft = leftPos + mLeftWidth;
//        final int middleRight = rightPos - mRightWidth;
//
//        for (int i = 0; i < count; i++) {
//            final View child = getChildAt(i);
//            if (child.getVisibility() != GONE) {
//                LayoutParams lp = (LayoutParams) child.getLayoutParams();
//                if (lp.p)
//            }
//        }
//        super.onLayout(changed, left, top, right, bottom);
//    }
}
