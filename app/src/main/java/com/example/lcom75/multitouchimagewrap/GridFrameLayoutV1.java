package com.example.lcom75.multitouchimagewrap;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by lcom75 on 5/7/16.
 */

public class GridFrameLayoutV1 extends FrameLayout implements GridButton.MoveListener {
    private static final String TAG = "ImageWrapView";
    private static final int VERTICAL_LINE = 4;
    private static final int HORIZONTAL_LINE = 3;
    private static final int COUNT = (VERTICAL_LINE + 1) * (HORIZONTAL_LINE + 1);
    public static final int LINE_SIZE_IN_DP = 32;
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
    GridButton button00, button01, button10, button11, button20, button21;
    private int mLeftWidth;
    private int mRightWidth;
    private final Rect mTmpContainerRect = new Rect();
    private final Rect mTmpChildRect = new Rect();
    Paint redArea;

    public GridFrameLayoutV1(Context context) {
        super(context);
        init(context);
    }

    public GridFrameLayoutV1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GridFrameLayoutV1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GridFrameLayoutV1(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.autumn);

        w = mBitmap.getWidth();
        h = mBitmap.getHeight();
        toleranceWidth = Math.max((((w - (16 * HORIZONTAL_LINE)) / (HORIZONTAL_LINE)) / 4), 16);
        toleranceHeight = Math.max((((h - (16 * VERTICAL_LINE)) / (VERTICAL_LINE)) / 4), 16);
        Log.d(TAG, "init: " + toleranceWidth + "::" + toleranceHeight);
        // construct our mesh
        int index = 0;
        for (int y = 0; y <= VERTICAL_LINE; y++) {
            float fy = h * y / VERTICAL_LINE;
            for (int x = 0; x <= HORIZONTAL_LINE; x++) {
                float fx = w * x / HORIZONTAL_LINE;
                Log.e(TAG, "setX " + fx + "setY " + fy + "index " + index);
                setXY(mVerts, index, fx, fy);
                setXY(mOrig, index, fx, fy);
                index += 1;
                Log.d(TAG, "SampleView: " + x + ":" + y + ":" + w + ":" + HORIZONTAL_LINE + ":fx:" + fx + ":" + fy);
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

        button00 = new GridButton(context);
        button00.setId(R.id.button00);
        button00.setBackgroundResource(R.drawable.grid_button_bg);
        addView(button00);
        button01 = new GridButton(context);
        button01.setId(R.id.button01);
        button01.setBackgroundResource(R.drawable.grid_button_bg);
        addView(button01);
        button10 = new GridButton(context);
        button10.setId(R.id.button10);
        button10.setBackgroundResource(R.drawable.grid_button_bg);
        addView(button10);
        button11 = new GridButton(context);
        button11.setId(R.id.button11);
        button11.setBackgroundResource(R.drawable.grid_button_bg);
        addView(button11);
        button20 = new GridButton(context);
        button20.setId(R.id.button20);
        button20.setBackgroundResource(R.drawable.grid_button_bg);
        addView(button20);
        button21 = new GridButton(context);
        button21.setId(R.id.button21);
        button21.setBackgroundResource(R.drawable.grid_button_bg);
        addView(button21);

        button00.setListener(this);
        button01.setListener(this);
        button10.setListener(this);
        button11.setListener(this);
        button20.setListener(this);
        button21.setListener(this);

    }

    private static void setXY(float[] array, int index, float x, float y) {

        if (index == 1)
            spaceX = x;

        if (index == 5)
            spaceY = y;

        array[index * 2 + 0] = x;
        array[index * 2 + 1] = y;

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
        int leftPos;
        int rightPos = right - left - getPaddingRight();
        leftPos = (rightPos - (VERTICAL_LINE * AndroidUtilities.dp(LINE_SIZE_IN_DP))) / (VERTICAL_LINE);
        int singlePiece = leftPos;

        final int parentTop =0; getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();

        int topPos = (bottom - (HORIZONTAL_LINE * AndroidUtilities.dp(LINE_SIZE_IN_DP))) / (HORIZONTAL_LINE);
        int hSinglePiece = topPos;

        Log.d(TAG, "onLayout" + ":ParentTop:" + parentTop + ":parentBottom:" + parentBottom
                + ":TopPost :" + topPos + ":parentLeft:" + parentLeft + ":ParentRight:"
                + parentRight + ": SinglePiece:" + singlePiece);

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child instanceof GridButton) {
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
//            if (line1 != null) {
//                line1.onAnchorPositionChanged(0, dX, dY);
//                line1.invalidate();
//            }
//            if (hLine1 != null) {
//                hLine1.onAnchorPositionChanged(0, dX, dY);
//                hLine1.invalidate();
//            }
        } else if (id == R.id.button01) {
//            if (line1 != null) {
//                line1.onAnchorPositionChanged(1, dX, dY);
//                line1.invalidate();
//            }
//            if (hLine2 != null) {
//                hLine2.onAnchorPositionChanged(1, dX, dY);
//                hLine2.invalidate();
//            }
        } else if (id == R.id.button10) {
//            if (line2 != null) {
//                line2.onAnchorPositionChanged(1, dX, dY);
//                line2.invalidate();
//            }
//            if (hLine1 != null) {
//                hLine1.onAnchorPositionChanged(0, dX, dY);
//                hLine1.invalidate();
//            }
        } else if (id == R.id.button11) {
//            if (line2 != null) {
//                line2.onAnchorPositionChanged(1, dX, dY);
//                line2.invalidate();
//            }
//            if (hLine2 != null) {
//                hLine2.onAnchorPositionChanged(1, dX, dY);
//                hLine2.invalidate();
//            }
        } else if (id == R.id.button20) {
//            if (line3 != null) {
//                line3.onAnchorPositionChanged(1, dX, dY);
//                line3.invalidate();
//            }
//            if (hLine1 != null) {
//                hLine1.onAnchorPositionChanged(2, dX, dY);
//                hLine1.invalidate();
//            }
        }
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Paint paintSimple = new Paint();
        paintSimple.setColor(Color.RED);
        paintSimple.setStyle(Paint.Style.STROKE);
        paintSimple.setStrokeJoin(Paint.Join.ROUND);
        paintSimple.setStrokeWidth(2);
        paintSimple.setAntiAlias(true);
        if (VerticalLine.firstPoint != null) {
            canvas.drawCircle((float) ((198 + VerticalLine.firstPoint.x) - AndroidUtilities.dp(8)), VerticalLine.firstPoint.y - AndroidUtilities.dp(8), AndroidUtilities.dp(8), paintSimple);
        }
    }
}
