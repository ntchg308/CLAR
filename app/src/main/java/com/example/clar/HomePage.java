package com.example.clar;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Debug;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.clar.Utility.AnimationUtility;
import com.example.clar.Utility.DisplayUtility;

public class HomePage extends View {

    Context mContext;
    Resources mResources;
    int mId;
    Paint mPaint;
    int mTimeHoldEditPage;
    int mDistanceBeginDetectMove;
    int mDistanceMoveToNextEditPage;
    int dX;
    int dY;
    int mTouchBeginPX;
    int mTouchCurPX;
    long mTimeTouch;
    boolean mCheckPageMovedWhileEditPage;
    boolean mUpdateCoorFromParent;
    HomePageWrapper parentView;
    Glob.DragPageState mDragState;


    public HomePage(Context context) {
        super(context);
    }

    public HomePage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Context context, int id, HomePageWrapper parentView){
        mContext = context;
        mResources = context.getResources();
        mId = id;
        mPaint = new Paint();
        mTimeHoldEditPage = mResources.getInteger(R.integer.timeHoldEditPage);
        mDistanceBeginDetectMove = mResources.getInteger(R.integer.distanceBeginDetectMove);
        mDistanceMoveToNextEditPage = mResources.getInteger(R.integer.distanceMoveToNextEditPage);
        this.parentView = parentView;
        mCheckPageMovedWhileEditPage = false;
        mUpdateCoorFromParent = true;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.GRAY);
        mPaint.setTextSize(50);
        mPaint.setColor(Color.WHITE);
        canvas.drawText(String.valueOf(mId), 50, 50, mPaint);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        if(mUpdateCoorFromParent){
            super.layout(l, t, r, b);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("ccc","dragState : " + mDragState);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                dX = (int) (event.getRawX() - getX());
                dY = (int) (event.getRawY() - getY());
                mTouchBeginPX = (int) event.getRawX();
                mTimeTouch = System.currentTimeMillis();
                mDragState = Glob.DragPageState.TOUCH_HOLD;
                break;
            case MotionEvent.ACTION_MOVE:
                mTouchCurPX = (int) event.getRawX();

                if(mDragState == Glob.DragPageState.TOUCH_HOLD && Math.abs(mTouchCurPX - mTouchBeginPX) > mDistanceBeginDetectMove){
                    parentView.setHandleTouch(true);
                    mDragState = Glob.DragPageState.HOME_PAGE_WRAPPER_HANDLE;
                }

                if(mDragState == Glob.DragPageState.TOUCH_HOLD  && System.currentTimeMillis() - mTimeTouch > mTimeHoldEditPage) {
                    mDragState = Glob.DragPageState.MOVE_PAGE_WITH_POINTER;
                    mCheckPageMovedWhileEditPage = false;
                }

                if(mDragState == Glob.DragPageState.MOVE_PAGE_WITH_POINTER){
                    checkMoveToNextPageWhileEditPage(event);
                    setX(event.getRawX() - dX);
                    setY(event.getRawY() - dY);
                }

                break;
            case MotionEvent.ACTION_UP:
                if(mDragState == Glob.DragPageState.MOVE_PAGE_WITH_POINTER){
                    dropEditPageToCurrentPageIndex(event);
                }
                break;
        }
        return true;
    }

    public void dropEditPageToCurrentPageIndex(MotionEvent ev){
        parentView.removeView(this);
        parentView.addView(this, parentView.getCurrentPageIndex());

        Point posCurPage = parentView.getChildPosCorrespondingWithScreen(parentView.getCurrentPageIndex());
        AnimationUtility.moveViewToPosition(this, (int) getX(),(int) getY(), posCurPage.x, posCurPage.y, ()->{
            mUpdateCoorFromParent = true;
        }, ()->{
            mUpdateCoorFromParent = true;

//            parentView.setCoorForAllChild();
        });
    }

    public boolean isTheCenterEditPage(){
        return parentView.getChildAt(parentView.getCurrentPageIndex()) == this;
    }

    public void checkMoveToNextPageWhileEditPage(MotionEvent ev){
        DisplayMetrics dis = DisplayUtility.getDisplayInfo(mContext);
        int rightSideX = dis.widthPixels - mDistanceMoveToNextEditPage;
        int leftSideX = mDistanceMoveToNextEditPage;
        int touchCurPX = (int) ev.getRawX();

        if(mCheckPageMovedWhileEditPage == false && touchCurPX >= rightSideX){
            mCheckPageMovedWhileEditPage = true;
//            Point posCurView = parentView.getChildPosCorrespondingWithScreen(parentView.getCurrentPageIndex() -  1);
//            View viewNext = parentView.getChildAt(parentView.getCurrentPageIndex() + 1);
//            AnimationUtility.moveViewToPosition(viewNext, posCurView.x, posCurView.y);
            parentView.moveToChildAt(parentView.getCurrentPageIndex() + 1);
        }

        if(mCheckPageMovedWhileEditPage == false && touchCurPX <= leftSideX){
            mCheckPageMovedWhileEditPage = true;
//            Point posCurView = parentView.getChildPosCorrespondingWithScreen(parentView.getCurrentPageIndex() +  1);
//            View viewPrev = parentView.getChildAt(parentView.getCurrentPageIndex() - 1);
//            AnimationUtility.moveViewToPosition(viewPrev, posCurView.x, posCurView.y);
            parentView.moveToChildAt(parentView.getCurrentPageIndex() - 1);
        }

        if(mCheckPageMovedWhileEditPage == true && touchCurPX < rightSideX && touchCurPX > leftSideX){
            mCheckPageMovedWhileEditPage = false;
        }
    }
}
