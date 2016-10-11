package com.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.popularmovies.fragments.DetailsFragment;

/**
 * Created by sagar_000 on 10/11/2016.
 */
public class DetailsActivity extends AppCompatActivity {

    String title, poster, plot, release;
    double ratings = 0;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        title = getIntent().getStringExtra("title");
        poster = getIntent().getStringExtra("poster");
        plot = getIntent().getStringExtra("plot");
        ratings = getIntent().getDoubleExtra("ratings", ratings);
        release = getIntent().getStringExtra("release");
        id = getIntent().getLongExtra("id", id);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.details_container, DetailsFragment.newInstance(title, poster, plot, ratings, release, id))
                    .commit();
        }
    }
}
