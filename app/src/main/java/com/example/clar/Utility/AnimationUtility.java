package com.example.clar.Utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;

public class AnimationUtility {

    public static void moveViewToPosition(final View v, int posX, int posY){
        ValueAnimator moveXAnim = ValueAnimator.ofInt((int) v.getX(), posX);
        moveXAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setX((int) animation.getAnimatedValue());
            }
        });

        ValueAnimator moveYAnim = ValueAnimator.ofInt((int) v.getY(), posY);
        moveYAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setY((int) animation.getAnimatedValue());
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(moveXAnim).with(moveYAnim);
        set.start();
    }

    public static void moveViewToPosition(final View v, int posX, int posY, ICallback callbackStart, ICallback callbackEnd){
        ValueAnimator moveXAnim = ValueAnimator.ofInt((int) v.getX(), posX);
        moveXAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setX((int) animation.getAnimatedValue());
            }
        });

        ValueAnimator moveYAnim = ValueAnimator.ofInt((int) v.getY(), posY);
        moveYAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.setY((int) animation.getAnimatedValue());
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                callbackStart.execute();
            }

            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                callbackEnd.execute();
            }
        });
        set.play(moveXAnim).with(moveYAnim);
        set.start();
    }

    public static void moveViewToPosition(final View v,int startPosX, int startPosY, int endPosX, int endPosY, ICallback callbackStart, ICallback callbackEnd){
        ValueAnimator moveXAnim = ValueAnimator.ofInt(startPosX, endPosX);
        moveXAnim.addUpdateListener((animation)->{
            Log.d("ccc"," x : " + (int) animation.getAnimatedValue());
            v.setX((int) animation.getAnimatedValue());});

        ValueAnimator moveYAnim = ValueAnimator.ofInt(startPosY, endPosY);
        moveYAnim.addUpdateListener((animation)->{v.setY((int) animation.getAnimatedValue());});

        AnimatorSet set = new AnimatorSet();
        set.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {
                callbackStart.execute();
            }

            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                callbackEnd.execute();
            }
        });
        set.play(moveXAnim).with(moveYAnim);
        set.start();
    }
}
