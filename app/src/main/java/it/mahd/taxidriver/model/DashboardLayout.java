package it.mahd.taxidriver.model;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by salem on 2/13/16.
 */
public class DashboardLayout extends ViewGroup {
    public static final int UNEVEN_GRID_PENALTY_MULTIPLIER = 10;
    public int mMaxChildWidth = 0;
    public int mMaxChildHeight = 0;

    public DashboardLayout(Context context) {
        super(context, null);
    }

    public DashboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public DashboardLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMaxChildWidth = 0;
        mMaxChildHeight = 0;
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) { continue; }
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            mMaxChildWidth = Math.max(mMaxChildWidth, child.getMeasuredWidth());
            mMaxChildHeight = Math.max(mMaxChildHeight, child.getMeasuredHeight());
        }

        childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxChildWidth, MeasureSpec.EXACTLY);
        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxChildHeight, MeasureSpec.EXACTLY);

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) { continue; }
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }

        setMeasuredDimension(resolveSize(mMaxChildWidth, widthMeasureSpec), resolveSize(mMaxChildHeight, heightMeasureSpec));
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        final int count = getChildCount();
        int visibleCount = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            ++visibleCount;
        }
        if (visibleCount == 0) { return; }
        int bestSpaceDifference = Integer.MAX_VALUE;
        int spaceDifference;
        int hSpace = 0;
        int vSpace = 0;
        int cols = 1;
        int rows;

        while (true) {
            rows = (visibleCount - 1) / cols + 1;
            hSpace = ((width - mMaxChildWidth * cols) / (cols + 1));
            vSpace = ((height - mMaxChildHeight * rows) / (rows + 1));
            spaceDifference = Math.abs(vSpace - hSpace);
            if (rows * cols != visibleCount) { spaceDifference *= UNEVEN_GRID_PENALTY_MULTIPLIER; }

            if (spaceDifference < bestSpaceDifference) {
                bestSpaceDifference = spaceDifference;
                if (rows == 1) { break; }
            } else {
                --cols;
                rows = (visibleCount - 1) / cols + 1;
                hSpace = ((width - mMaxChildWidth * cols) / (cols + 1));
                vSpace = ((height - mMaxChildHeight * rows) / (rows + 1));
                break;
            }
            ++cols;
        }

        hSpace = Math.max(0, hSpace);
        vSpace = Math.max(0, vSpace);
        width = (width - hSpace * (cols + 1)) / cols;
        height = (height - vSpace * (rows + 1)) / rows;
        int left, top;
        int col, row;
        int visibleIndex = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) { continue; }
            row = visibleIndex / cols;
            col = visibleIndex % cols;
            left = hSpace * (col + 1) + width * col;
            top = vSpace * (row + 1) + height * row;
            child.layout(left, top, (hSpace == 0 && col == cols - 1) ? r : (left + width), (vSpace == 0 && row == rows - 1) ? b : (top + height));
            ++visibleIndex;
        }
    }
}
