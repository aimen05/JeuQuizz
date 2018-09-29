package com.bochenchleba.mapquiz;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class SeekBarRotator extends ViewGroup {
    public SeekBarRotator(Context context) {
        super(context);
    }
    public SeekBarRotator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SeekBarRotator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final View child = getChildAt(0);
        if (child.getVisibility() != GONE) {
            measureChild(child, heightMeasureSpec, widthMeasureSpec);
            setMeasuredDimension(
                    child.getMeasuredHeightAndState(),
                    child.getMeasuredWidthAndState());
        } else {
            setMeasuredDimension(
                    resolveSizeAndState(0, widthMeasureSpec, 0),
                    resolveSizeAndState(0, heightMeasureSpec, 0));
        }
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final View child = getChildAt(0);
        if (child.getVisibility() != GONE) {

            child.setPivotX(0);
            child.setPivotY(0);
            child.setRotation(-90);

            int mywidth = r - l;
            int myheight = b - t;
            int childwidth = myheight;
            int childheight = mywidth;
            child.layout(0, myheight, childwidth, myheight + childheight);
        }
    }
}