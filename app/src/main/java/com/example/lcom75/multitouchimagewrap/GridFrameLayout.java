package com.example.lcom75.multitouchimagewrap;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Created by lcom75 on 5/7/16.
 */

public class GridFrameLayout extends FrameLayout implements GridButton.MoveListener {
    VerticalLine line1, line2, line3;
    HorizontalLine hLine1, hLine2, hLine3;
    GridButton button00, button01, button10, button11, button20, button21;
    PointF pButton00, pButton01, pButton10, pButton11, pButton20, pButton21;
    private final Rect mTmpContainerRect = new Rect();
    private final Rect mTmpChildRect = new Rect();
    private int mLeftWidth;
    private int mRightWidth;
    private static final String TAG = "GridFrameLayout";
    public static final int VERTICAL_LINE = 3;
    public static final int HORIZONTAL_LINE = 2;
    public static final int LINE_SIZE_IN_DP = 32;
    boolean isDraw = false;


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
//        line1.setBackgroundColor(Color.YELLOW);
        addView(line1);//, new FrameLayout.LayoutParams(100, LayoutParams.MATCH_PARENT, Gravity.CENTER));
//        line2 = new VerticalLine(context);
////        line2.setBackgroundColor(Color.BLUE);
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, LayoutParams.MATCH_PARENT);
//        params.setMargins(20, 0, 0, 0);
//        addView(line2);//, params);
//        line3 = new VerticalLine(context);
////        line3.setBackgroundColor(Color.LTGRAY);
//        params = new FrameLayout.LayoutParams(100, LayoutParams.MATCH_PARENT, Gravity.RIGHT);
//        params.setMargins(0, 0, 20, 0);
//        addView(line3);//, params);
//
//        hLine1 = new HorizontalLine(context);
////        hLine1.setBackgroundColor(Color.YELLOW);
//        addView(hLine1);
//        hLine2 = new HorizontalLine(context);
////        hLine2.setBackgroundColor(Color.GREEN);
//        addView(hLine2);
//        hLine3 = new HorizontalLine(context);
////        hLine3.setBackgroundColor(Color.MAGENTA);
//        addView(hLine3);

        button00 = new GridButton(context);
        button00.setId(R.id.button00);
        button00.setBackgroundResource(R.drawable.grid_button_bg);
        addView(button00);
        button01 = new GridButton(context);
        button01.setId(R.id.button01);
        button01.setBackgroundResource(R.drawable.grid_button_bg);
        addView(button01);
//        button10 = new GridButton(context);
//        button10.setId(R.id.button10);
//        button10.setBackgroundResource(R.drawable.grid_button_bg);
//        addView(button10);
//        button11 = new GridButton(context);
//        button11.setId(R.id.button11);
//        button11.setBackgroundResource(R.drawable.grid_button_bg);
//        addView(button11);
//        button20 = new GridButton(context);
//        button20.setId(R.id.button20);
//        button20.setBackgroundResource(R.drawable.grid_button_bg);
//        addView(button20);
//        button21 = new GridButton(context);
//        button21.setId(R.id.button21);
//        button21.setBackgroundResource(R.drawable.grid_button_bg);
//        addView(button21);

        button00.setListener(this);
        button01.setListener(this);
//        button10.setListener(this);
//        button11.setListener(this);
//        button20.setListener(this);
//        button21.setListener(this);

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
        Log.d(TAG, "onLayout() called with: changed = [" + changed + "], left = [" + left + "], top = [" + top + "], right = [" + right + "], bottom = [" + bottom + "]");
        if (!changed) {
            return;
        }
        hChildCount = 0;
        vChildCount = 0;
        final int count = getChildCount();
        int leftPos = getPaddingLeft();
        int rightPos = right - left - getPaddingRight();
        leftPos = (rightPos - (VERTICAL_LINE * AndroidUtilities.dp(LINE_SIZE_IN_DP))) / (VERTICAL_LINE + 1);
        int singlePiece = leftPos;

        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();

        int topPos = (bottom - (HORIZONTAL_LINE * AndroidUtilities.dp(LINE_SIZE_IN_DP))) / (HORIZONTAL_LINE + 1);
        int hSinglePiece = topPos;

        Log.d(TAG, "onLayout" + ":ParentTop:" + parentTop + ":parentBottom:" + parentBottom
                + ":TopPost :" + topPos + ":parentLeft:" + parentLeft + ":ParentRight:"
                + parentRight + ": SinglePiece:" + singlePiece);

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child instanceof VerticalLine) {
                if (child.getVisibility() != GONE) {
                    final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    final int width = child.getMeasuredWidth();
                    final int height = child.getMeasuredHeight();

                    mTmpContainerRect.left = leftPos + lp.leftMargin;
                    mTmpContainerRect.right = leftPos + width + lp.rightMargin;
                    leftPos = mTmpContainerRect.right + singlePiece;
                    Log.d(TAG, "onLayout: SinglePiece:" + singlePiece + ":" + leftPos);
                    mTmpContainerRect.top = parentTop + lp.topMargin;
                    mTmpContainerRect.bottom = parentBottom - lp.bottomMargin;
                    Gravity.apply(lp.gravity, width, height, mTmpContainerRect, mTmpChildRect);

                    Log.d(TAG, "onLayout: " + mTmpChildRect.toString() + ":Position:" + i);
                    child.layout(mTmpChildRect.left, mTmpChildRect.top,
                            mTmpChildRect.right, mTmpChildRect.bottom);

                    vChildCount++;
                }
            } else if (child instanceof HorizontalLine) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();
                mTmpContainerRect.left = parentLeft + lp.leftMargin;
                mTmpContainerRect.right = parentRight - lp.rightMargin;

                mTmpContainerRect.top = topPos + lp.topMargin;
                mTmpContainerRect.bottom = topPos + height + lp.bottomMargin;

                topPos = mTmpContainerRect.bottom + hSinglePiece;
                Log.d(TAG, "onLayout: SinglePiece Horizontal:" + hSinglePiece + ":" + height + ":" + topPos);

                Gravity.apply(lp.gravity, width, height, mTmpContainerRect, mTmpChildRect);
                Log.d(TAG, "onLayout Horizontal: " + mTmpChildRect.toString() + ":Position:" + i);

                child.layout(mTmpChildRect.left, mTmpChildRect.top,
                        mTmpChildRect.right, mTmpChildRect.bottom);

                hChildCount++;
            } else if (child instanceof GridButton) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();
                if (child.getId() == R.id.button00) {
                    mTmpContainerRect.left = singlePiece + lp.leftMargin;
                    mTmpContainerRect.right = singlePiece + width + lp.rightMargin;

                    mTmpContainerRect.top = hSinglePiece + lp.topMargin;
                    mTmpContainerRect.bottom = hSinglePiece + height + lp.bottomMargin;
                }
                if (child.getId() == R.id.button01) {
                    mTmpContainerRect.left = singlePiece + lp.leftMargin;
                    mTmpContainerRect.right = singlePiece + width + lp.rightMargin;

                    mTmpContainerRect.top = (hSinglePiece * 2) + AndroidUtilities.dp(LINE_SIZE_IN_DP) + lp.topMargin;
                    mTmpContainerRect.bottom = (hSinglePiece * 2) + AndroidUtilities.dp(LINE_SIZE_IN_DP) + height + lp.bottomMargin;
                }
                if (child.getId() == R.id.button10) {
                    mTmpContainerRect.left = (singlePiece * 2) + AndroidUtilities.dp(LINE_SIZE_IN_DP) + lp.leftMargin;
                    mTmpContainerRect.right = (singlePiece * 2) + AndroidUtilities.dp(LINE_SIZE_IN_DP) + width + lp.rightMargin;

                    mTmpContainerRect.top = hSinglePiece + lp.topMargin;
                    mTmpContainerRect.bottom = hSinglePiece + height + lp.bottomMargin;
                }
                if (child.getId() == R.id.button11) {
                    mTmpContainerRect.left = (singlePiece * 2) + AndroidUtilities.dp(LINE_SIZE_IN_DP) + lp.leftMargin;
                    mTmpContainerRect.right = (singlePiece * 2) + AndroidUtilities.dp(LINE_SIZE_IN_DP) + width + lp.rightMargin;

                    mTmpContainerRect.top = (hSinglePiece * 2) + AndroidUtilities.dp(LINE_SIZE_IN_DP) + lp.topMargin;
                    mTmpContainerRect.bottom = (hSinglePiece * 2) + AndroidUtilities.dp(LINE_SIZE_IN_DP) + height + lp.bottomMargin;
                }
                if (child.getId() == R.id.button20) {
                    mTmpContainerRect.left = (singlePiece * 3) + (2 * AndroidUtilities.dp(LINE_SIZE_IN_DP)) + lp.leftMargin;
                    mTmpContainerRect.right = (singlePiece * 3) + (2 * AndroidUtilities.dp(LINE_SIZE_IN_DP)) + width + lp.rightMargin;

                    mTmpContainerRect.top = (hSinglePiece) + lp.topMargin;
                    mTmpContainerRect.bottom = (hSinglePiece) + height + lp.bottomMargin;
                }
                if (child.getId() == R.id.button21) {
                    mTmpContainerRect.left = (singlePiece * 3) + (2 * AndroidUtilities.dp(LINE_SIZE_IN_DP)) + lp.leftMargin;
                    mTmpContainerRect.right = (singlePiece * 3) + (2 * AndroidUtilities.dp(LINE_SIZE_IN_DP)) + width + lp.rightMargin;

                    mTmpContainerRect.top = (hSinglePiece * 2) + AndroidUtilities.dp(LINE_SIZE_IN_DP) + lp.topMargin;
                    mTmpContainerRect.bottom = (hSinglePiece * 2) + AndroidUtilities.dp(LINE_SIZE_IN_DP) + height + lp.bottomMargin;
                }
                Gravity.apply(lp.gravity, width, height, mTmpContainerRect, mTmpChildRect);
                Log.d(TAG, "onLayout Horizontal: " + mTmpChildRect.toString() + ":Position:" + i);

                child.layout(mTmpChildRect.left, mTmpChildRect.top,
                        mTmpChildRect.right, mTmpChildRect.bottom);

            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    /**
     * Change position as per Button new position
     *
     * @param id Id of Button that Changed
     * @param dX New X position (rawX)
     * @param dY New Y postion (rawY)
     */
    @Override
    public void onButtonPostionChanged(int id, float dX, float dY) {
        Log.d(TAG, "onButtonPostionChanged() called with: id = [" + id + "], dX = [" + dX + "], dY = [" + dY + "]");
        if (id == R.id.button00) {
            if (line1 != null) {
                line1.onAnchorPositionChanged(0, dX, dY);
                line1.invalidate();
            }
            if (hLine1 != null) {
                hLine1.onAnchorPositionChanged(0, dX, dY);
                hLine1.invalidate();
            }
        } else if (id == R.id.button01) {
            if (line1 != null) {
                line1.onAnchorPositionChanged(0, dX, dY);
                line1.invalidate();
            }
            if (hLine2 != null) {
                hLine2.onAnchorPositionChanged(1, dX, dY);
                hLine2.invalidate();
            }
        } else if (id == R.id.button10) {
            if (line2 != null) {
                line2.onAnchorPositionChanged(1, dX, dY);
                line2.invalidate();
            }
            if (hLine1 != null) {
                hLine1.onAnchorPositionChanged(0, dX, dY);
                hLine1.invalidate();
            }
        } else if (id == R.id.button11) {
            if (line2 != null) {
                line2.onAnchorPositionChanged(1, dX, dY);
                line2.invalidate();
            }
            if (hLine2 != null) {
                hLine2.onAnchorPositionChanged(1, dX, dY);
                hLine2.invalidate();
            }
        }
    }
}
