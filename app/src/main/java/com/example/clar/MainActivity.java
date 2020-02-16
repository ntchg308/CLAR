package com.example.clar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    HomePageWrapper homePageWrapper;
    Button btnMoveToPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homePageWrapper = findViewById(R.id.homePageWrapper);
//        btnMoveToPage = findViewById(R.id.btnMoveToPage);

        homePageWrapper.init(this);
        for(int i=0;i<5;i++){
            HomePage h = new HomePage(this);
            h.init(this, i,homePageWrapper);
            homePageWrapper.addView(h);
        }

        homePageWrapper.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                homePageWrapper.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                homePageWrapper.moveToChildAt(0);
            }
        });

//        btnMoveToPage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                homePageWrapper.moveToChildAt(4);
//            }
//        });
    }
}
