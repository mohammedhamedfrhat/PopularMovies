package com.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LandscapeView();

    }

    public boolean LandscapeView(){
        if (findViewById(R.id.details_fragment_container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

        return mTwoPane;
    }

}


