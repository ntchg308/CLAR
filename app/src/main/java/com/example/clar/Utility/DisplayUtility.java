package com.example.clar.Utility;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtility {

    public static DisplayMetrics getDisplayInfo(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }
}
