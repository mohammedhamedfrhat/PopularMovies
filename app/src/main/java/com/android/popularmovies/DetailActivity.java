package com.android.popularmovies;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.popularmovies.Adapters.ReviewsAdapter;
import com.android.popularmovies.Adapters.VideoAdapter;
import com.android.popularmovies.Models.Constants;
import com.android.popularmovies.Models.FavoriteMovie;
import com.android.popularmovies.Models.Model;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sagar_000 on 9/4/2016.
 */
public class DetailActivity extends BaseActivity {


    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    private static int REQUEST_STORAGE = 1;

    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;

    String posterJsonStr = null;

    String reviewJsonStr = null;

    private List<Model> mReviewArrayList = null;

    private List<Model> mVideoArrayList = null;


    @BindView(R.id.movie_title)
    TextView title;
    @BindView(R.id.detail_image)
    ImageView poster;
    @BindView(R.id.plot_details)
    TextView plot;
    @BindView(R.id.release_date)
    TextView release;
    @BindView(R.id.favorite_button)
    ImageButton favorite_button;
    @BindView(R.id.rating_bar_detail)
    RatingBar ratingBar;

    double ratings = 0;
    Context context;
    File mediaStorageDir;
    File file;

    double SugarRatings;

    String movieTitle, moviePlot, movieRelease, movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        ratingBar.setEnabled(false);
        movieTitle = getIntent().getStringExtra("title");
        moviePlot = getIntent().getStringExtra("plot");
        movieRelease = getIntent().getStringExtra("release");
        movieId = getIntent().getStringExtra("id");
        title.setText(movieTitle);
        Picasso.with(this).load(getIntent().getStringExtra("poster")).into(poster);
        plot.setText(moviePlot);
        release.setText(movieRelease);
        setFavoriteButtonToggle();
        ratingBar.setRating((float) getIntent().getDoubleExtra("rating",ratings));
        SugarRatings = getIntent().getDoubleExtra("rating",ratings);


        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermissions();
                }
                if (isExternalStorageAvailable()) {
                    mediaStorageDir = new File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                            "PopularMovies");
                    if (!mediaStorageDir.exists()) {
                        if (!mediaStorageDir.mkdirs()) {
                            Log.e(LOG_TAG, "Failed to create directory.");
                        }
                    }
                }
                checkIfMovieFavoured();
                Log.d(LOG_TAG, movieId);
            }
        });

    }

    public void onStart(){
        super.onStart();
        MovieTrailerTask movieTask = new MovieTrailerTask();
        movieTask.execute();

        MovieReviewsTask reviewsTask = new MovieReviewsTask();
        reviewsTask.execute();
    }



    private void AddFavorite() {
        String moviePoster = getIntent().getStringExtra("poster");
        final String imageName = getIntent().getStringExtra("poster");
        int index = imageName.lastIndexOf("/");
        final String fileName = imageName.substring(index + 1);
        Picasso.with(context).load(moviePoster).into(new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                            file = new File(mediaStorageDir.getAbsolutePath() + "/" + fileName);
                            Log.d(LOG_TAG, String.valueOf(file));
                            try {
                                file.createNewFile();
                                FileOutputStream saveStream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, saveStream);
                                saveStream.flush();
                                saveStream.close();
                                saveToLocalDatabase(String.valueOf(file));
                            } catch (IOException e) {
                                Log.e("IOException", e.getLocalizedMessage());
                            }
                        }
                }).start();
                }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

                Toast.makeText(getApplicationContext(),"Could not Added Movie to Favorite Movies", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

        });

    }

    private void saveToLocalDatabase(String path){
        FavoriteMovie favoriteMovie = new FavoriteMovie(movieTitle, moviePlot, movieRelease, SugarRatings, path);
        favoriteMovie.setId(Long.getLong(movieId));
        favoriteMovie.save();
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();

        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private void requestStoragePermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

            Toast.makeText(this,"Please Accept the Permissions to Add Favorite Movies",Toast.LENGTH_LONG).show();

            ActivityCompat
                    .requestPermissions(this, PERMISSIONS_STORAGE,
                            REQUEST_STORAGE);
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE) {
            // Received permission result for storage permission.
            Log.i(LOG_TAG, "Received response for Storage permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Storage Permission Denied!",Toast.LENGTH_LONG).show();
            }
            else {
                AddFavorite();
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setFavoriteButtonToggle(){
        List<FavoriteMovie> results = findFromSugar(Constants.SUGAR_WHERE_ID, movieId);
        if(results.size() > 0){
            favorite_button.setImageDrawable(getResources().getDrawable(R.mipmap.star_on));
        }
    }

    public boolean checkIfMovieFavoured(){
            List<FavoriteMovie> results = findFromSugar(Constants.SUGAR_WHERE_ID, movieId);
            if(results.size() > 0){
                favorite_button.setEnabled(false);
                Toast.makeText(getApplicationContext(),"Already added to Favorite Movies!",Toast.LENGTH_SHORT).show();
                return true;
            }else {
                AddFavorite();
                favorite_button.setImageDrawable(getResources().getDrawable(R.mipmap.star_on));
                return false;
            }
    }

    private static List<FavoriteMovie> findFromSugar(String whereClause, String movieId){

        return Select.from(FavoriteMovie.class)
                .where(Condition.prop(whereClause)
                        .eq(movieId))
                .list(); // Line of crash
    }

    public class MovieTrailerTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... strings) {

            try {

                URL url = new URL(Constants.VIDEO_URL + movieId + Constants.VIDEO + Constants.API_KEY);

                Log.d(LOG_TAG, String.valueOf(url));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                posterJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                return getTrailerKeyFromJson(posterJsonStr);
            } catch (JSONException e) {
                Log.d(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String[] result){
            if(result != null) {
                VideoAdapter videoAdapter = new VideoAdapter(DetailActivity.this, mVideoArrayList);
                GridView gridView = (GridView) findViewById(R.id.trailers_layout);
                gridView.setAdapter(videoAdapter);
            }
        }


    }

    private String[] getTrailerKeyFromJson(String posterJsonStr)
            throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String TRAILER_KEY = "key";
        final String RESULTS = "results";

        JSONObject posterJson = new JSONObject(posterJsonStr);
        JSONArray movieArray = posterJson.getJSONArray(RESULTS);

        String[] resultStrs = new String[movieArray.length()];
        mVideoArrayList = new ArrayList<Model>();
        for(int i = 0; i < movieArray.length(); i++) {

            JSONObject poster = movieArray.getJSONObject(i);

            Model model = new Model();
            model.setTrailer(poster.getString(TRAILER_KEY));

            mVideoArrayList.add(model);

        }

        return resultStrs;
    }


    public class MovieReviewsTask extends AsyncTask<String, Void, String[]> {


        @Override
        protected String[] doInBackground(String... strings) {
            try {

                URL url = new URL(Constants.VIDEO_URL + movieId + Constants.REVIEWS + Constants.API_KEY);

                Log.d(LOG_TAG, String.valueOf(url));

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                reviewJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("DetailActivity: ", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("DetailActivity: ", "Error closing stream", e);
                    }
                }
            }

            try {
                return getReviewsKeyFromJson(reviewJsonStr);
            } catch (JSONException e) {
                Log.d(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String[] result){
            if(result != null) {
                ReviewsAdapter reviewsAdapter = new ReviewsAdapter(DetailActivity.this, mReviewArrayList);
                ListView listView = (ListView) findViewById(R.id.reviews_list);
                listView.setAdapter(reviewsAdapter);
            }
        }
    }

    private String[] getReviewsKeyFromJson(String reviewsJsonStr)
            throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String AUTHOR_KEY = "author";
        final String REVIEW_KEY = "content";
        final String RESULTS = "results";


        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        JSONArray movieArray = reviewsJson.getJSONArray(RESULTS);

        String[] resultStrs = new String[movieArray.length()];
        mReviewArrayList = new ArrayList<Model>();
        for(int i = 0; i < movieArray.length(); i++) {

            JSONObject poster = movieArray.getJSONObject(i);

            Model model = new Model();
            model.setAuthor(poster.getString(AUTHOR_KEY));
            model.setReview(poster.getString(REVIEW_KEY));

            mReviewArrayList.add(model);

        }

        return resultStrs;
    }

}
