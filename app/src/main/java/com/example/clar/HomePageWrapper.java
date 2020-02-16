package com.example.clar;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.clar.Utility.DisplayUtility;

import java.util.ArrayList;

public class HomePageWrapper extends ViewGroup {

    Context mContext;
    Resources mResources;
    int mFirstPagePaddingTop;
    int mFirstPagePaddingLeft;
    int mPageMarginRight;
    int mChildW;
    int mChildH;
    int mTouchBeginPX;
    int mTouchCurPX;
    int mTouchPrevPX;
    int mPageIndexCur;
    boolean firstTime = true;
    boolean isHandleTouch;

    public void setHandleTouch(boolean handleTouch) {
        isHandleTouch = handleTouch;
    }

    public HomePageWrapper(Context context) {
        super(context);
    }

    public HomePageWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Context context){
        mContext = context;
        mResources = context.getResources();
        mFirstPagePaddingLeft = (int) mResources.getInteger(R.integer.firstPagePaddingLeft);
        mFirstPagePaddingTop = (int) mResources.getInteger(R.integer.firstPagePaddingTop);
        mPageIndexCur = 0;
    }

    public void setCurrentPageIndex(int mPageIndexCur) {
        this.mPageIndexCur = mPageIndexCur;
    }

    public int getCurrentPageIndex() {
        return mPageIndexCur;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
//        canvas.drawColor(Color.RED);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        View child;
        int cx = getPaddingLeft() + mFirstPagePaddingLeft;
        int cy = getPaddingTop() + mFirstPagePaddingTop;

        for(int i=0;i<count;i++){
            child = getChildAt(i);
            child.layout(cx, cy, cx + child.getMeasuredWidth(), cy + child.getMeasuredHeight());
            cx += child.getMeasuredWidth() + mPageMarginRight;
        }
    }

    public void setCoorForAllChild(){
        int count = getChildCount();
        View child;
        int cx = getPaddingLeft() + mFirstPagePaddingLeft;
        int cy = getPaddingTop() + mFirstPagePaddingTop;

        for(int i=0;i<count;i++){
            child = getChildAt(i);
            child.layout(cx, cy, cx + child.getMeasuredWidth(), cy + child.getMeasuredHeight());
            cx += child.getMeasuredWidth() + mPageMarginRight;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        View child;
        int wScr = MeasureSpec.getSize(widthMeasureSpec);
        int hScr = MeasureSpec.getSize(heightMeasureSpec);
        mChildW = (int) mResources.getFraction(R.fraction.pageWidth, wScr, 1);
        mChildH = (int) mResources.getFraction(R.fraction.pageHeigh, hScr, 1);
        mPageMarginRight = (int) mResources.getFraction(R.fraction.pageMarginRight, wScr, 1);

        for(int i=0;i< count;i++){
            child = getChildAt(i);
            child.measure(mChildW, mChildH);
        }

        setMeasuredDimension(wScr, hScr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                isHandleTouch = false;
                break;
            case MotionEvent.ACTION_MOVE:
                mTouchBeginPX = mTouchPrevPX = (int) ev.getRawX();
                return isHandleTouch;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mTouchBeginPX = mTouchPrevPX = (int) event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                mTouchCurPX = (int) event.getRawX();
                setPadding(getPaddingLeft() + mTouchCurPX - mTouchPrevPX, getPaddingTop(), getPaddingRight(), getPaddingBottom());
                mTouchPrevPX = mTouchCurPX;
                break;
            case MotionEvent.ACTION_UP:
                moveToChildAt(getMovedPage(mTouchBeginPX,mTouchCurPX,mPageIndexCur));
                break;
        }
        return true;
    }

    public int getMovedPage(int touchBeginPX, int touchCurPX, int pageIndexCur){
        int distanceStand = mChildW/2;
        int distanceCur = Math.abs(touchBeginPX - touchCurPX);

        if(distanceCur < distanceStand) return pageIndexCur;
        if(touchBeginPX - touchCurPX > 0 && pageIndexCur < getChildCount() -1) return pageIndexCur + 1;
        if(touchBeginPX - touchCurPX < 0 && pageIndexCur > 0) return pageIndexCur - 1;
        return pageIndexCur;
    }

    public void moveToChildAt(int index){
        if(index < 0 || index >= getChildCount()) return;

        DisplayMetrics displayInfo = DisplayUtility.getDisplayInfo(mContext);
        Point p = getChildPos(index);
        p.x += (displayInfo.widthPixels/2) - mChildW/2;
        ValueAnimator moveAllPage = ValueAnimator.ofInt(getPaddingLeft(), p.x);
        moveAllPage.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int newpadding = (int) animation.getAnimatedValue();
                setPadding(newpadding, getPaddingTop(), getPaddingRight(), getPaddingBottom());
            }
        });
        moveAllPage.setDuration(200);
        moveAllPage.start();
        mPageIndexCur = index;
    }

    public Point getChildPos(int index){
        Point p = new Point();
        p.x = -mFirstPagePaddingLeft + (index * ( mChildW + mPageMarginRight) * -1);
        p.y = -mFirstPagePaddingTop + getPaddingTop();
        return p;
    }

    public Point getChildPosCorrespondingWithScreen(int index){
        Point p = getChildPos(index);
        p.x = getPaddingLeft() - p.x;
        return p;
    }
}
