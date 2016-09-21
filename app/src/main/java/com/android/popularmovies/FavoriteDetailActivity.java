package com.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sagar_000 on 9/18/2016.
 */
public class FavoriteDetailActivity extends AppCompatActivity {

    @BindView(R.id.fav_movie_title)
    TextView fav_title;
    @BindView(R.id.fav_detail_image)
    ImageView fav_poster;
    @BindView(R.id.fav_plot_details)
    TextView fav_plot;
    @BindView(R.id.fav_release_date)
    TextView fav_release;
    @BindView(R.id.fav_favorite_button)
    ImageButton fav_favorite_button;
    @BindView(R.id.fav_rating_bar_detail)
    RatingBar fav_ratingBar;

    float ratings = 0;

    String movieTitle, moviePlot, movieRelease, movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_detail_activity);
        ButterKnife.bind(this);
        fav_ratingBar.setEnabled(false);
        movieTitle = getIntent().getStringExtra("title");
        moviePlot = getIntent().getStringExtra("plot");
        movieRelease = getIntent().getStringExtra("release");
        movieId = getIntent().getStringExtra("id");
        fav_title.setText(movieTitle);
        Picasso.with(this).load(new File(getIntent().getStringExtra("poster"))).into(fav_poster);
        fav_plot.setText(moviePlot);
        fav_release.setText(movieRelease);
        fav_ratingBar.setRating(getIntent().getFloatExtra("rating",ratings));

    }

}
