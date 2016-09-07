package com.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by sagar_000 on 9/4/2016.
 */
public class DetailActivity extends AppCompatActivity {


    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        TextView title = (TextView)findViewById(R.id.movie_title);
        ImageView poster = (ImageView)findViewById(R.id.detail_image);
        TextView plot = (TextView)findViewById(R.id.plot_details);
        RatingBar ratingBar = (RatingBar)findViewById(R.id.rating_bar_detail);
        TextView release = (TextView)findViewById(R.id.release_date);
        ratingBar.setEnabled(false);
        double ratings = 0;

        title.setText(getIntent().getStringExtra("title"));
        Picasso.with(this).load(Constants.IMAGE_URL + getIntent().getStringExtra("poster")).into(poster);
        plot.setText(getIntent().getStringExtra("plot"));
        release.setText(getIntent().getStringExtra("release"));
        ratingBar.setRating((float) getIntent().getDoubleExtra("rating",ratings));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
