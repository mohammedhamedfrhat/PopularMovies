package com.android.popularmovies;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.android.popularmovies.Fragments.DetailsFragment;
import com.android.popularmovies.Fragments.MoviesFragment;

public class MainActivity extends AppCompatActivity {

    public static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LandscapeView(savedInstanceState);

    }

    public boolean LandscapeView(Bundle savedInstanceState){
        if (findViewById(R.id.details_fragment_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.details_fragment_container, new DetailsFragment(), DetailsFragment.LOG_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new MoviesFragment())
                    .addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        }

        return mTwoPane;
    }

}


