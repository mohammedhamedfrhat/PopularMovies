package com.android.popularmovies.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.popularmovies.R;
import com.android.popularmovies.adapters.ReviewsAdapter;
import com.android.popularmovies.adapters.VideoAdapter;
import com.android.popularmovies.models.Constants;
import com.android.popularmovies.models.FavoriteMovie;
import com.android.popularmovies.models.Model;
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
 * Created by sagar_000 on 9/22/2016.
 */
public class DetailsFragment extends Fragment {

    public static final String LOG_TAG = DetailsFragment.class.getSimpleName();

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
    @BindView(R.id.fragment_scrollView)
    ScrollView scrollView;

    View rootView;
    double ratings = 0;
    Context context;
    File mediaStorageDir;
    File file;

    double SugarRatings;

    long id;

    String movieTitle, moviePlot, movieRelease, movieId;

    public DetailsFragment(){}

    public static Fragment newInstance(String title, String poster, String plot, double ratings, String release, long id) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("poster",poster);
        args.putString("plot", plot);
        args.putDouble("rating", ratings);
        args.putString("release",release);
        args.putLong("id",id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){

        rootView = inflater.inflate(R.layout.fragment_details, null);

        ButterKnife.bind(this,rootView);
        ratingBar.setEnabled(false);
        Bundle bundle = this.getArguments();
        movieTitle = bundle.getString("title");
        moviePlot = bundle.getString("plot");
        movieRelease = bundle.getString("release");
        movieId = String.valueOf(bundle.getLong("id", id));
        title.setText(movieTitle);
        Picasso.with(getActivity()).load(bundle.getString("poster")).into(poster);
        plot.setText(moviePlot);
        release.setText(movieRelease);
        setFavoriteButtonToggle();
        ratingBar.setRating((float) bundle.getDouble("rating",ratings));
        SugarRatings = bundle.getDouble("rating",ratings);

        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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

        setHasOptionsMenu(true);

        return rootView;
    }


    public void onStart(){
        super.onStart();
        MovieTrailerTask movieTask = new MovieTrailerTask();
        movieTask.execute();

        MovieReviewsTask reviewsTask = new MovieReviewsTask();
        reviewsTask.execute();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        inflater.inflate(R.menu.details_menu,menu);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share){
            int i = 0;
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

            share.putExtra(Intent.EXTRA_SUBJECT, "Watch the Trailer of " + movieTitle);
            share.putExtra(Intent.EXTRA_TEXT, moviePlot + "\r\n" + "\r\n" + Constants.YOUTUBE_LINK_CONSTRUCTOR + mVideoArrayList.get(i).getTrailer());

            startActivity(Intent.createChooser(share, "Share link!"));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }



    private void AddFavorite() {
        Bundle bundle = this.getArguments();
        final String imageName = bundle.getString("poster");
        assert imageName != null;
        int index = imageName.lastIndexOf("/");
        final String fileName = imageName.substring(index + 1);
        Picasso.with(context).load(imageName).into(new Target() {
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

                Toast.makeText(getContext(),"Could not Added Movie to Favorite Movies", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

        });

    }

    private void saveToLocalDatabase(String path){
        FavoriteMovie favoriteMovie = new FavoriteMovie(movieTitle, moviePlot, movieRelease, SugarRatings, path, movieId);
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

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

            Toast.makeText(getActivity(),"Please Accept the Permissions to Add Favorite Movies",Toast.LENGTH_LONG).show();

            ActivityCompat
                    .requestPermissions(getActivity(), PERMISSIONS_STORAGE,
                            REQUEST_STORAGE);
        } else {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, REQUEST_STORAGE);
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
                Toast.makeText(getActivity(),"Storage Permission Denied!",Toast.LENGTH_LONG).show();
            }
            else {
                AddFavorite();
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setFavoriteButtonToggle(){
        try{
            List<FavoriteMovie> results = findFromSugar(Constants.SUGAR_WHERE_ID, movieId);
            if(results.size() > 0){
                favorite_button.setImageDrawable(getResources().getDrawable(R.drawable.star_on));
            }
        }  catch (Exception e){
            e.printStackTrace();
        }
    }

    public void checkIfMovieFavoured(){
        try {
            List<FavoriteMovie> results = findFromSugar(Constants.SUGAR_WHERE_ID, movieId);
             if(results.size() > 0){
                favorite_button.setEnabled(false);
                Toast.makeText(getContext(),"Already added to Favorite Movies!",Toast.LENGTH_SHORT).show();
            }else {
                AddFavorite();
                favorite_button.setImageDrawable(getResources().getDrawable(R.drawable.star_on));
            }
        } catch (Exception e){
            AddFavorite();
            favorite_button.setImageDrawable(getResources().getDrawable(R.drawable.star_on));
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
                VideoAdapter videoAdapter = new VideoAdapter(getActivity(), mVideoArrayList);
                GridView gridView = (GridView) rootView.findViewById(R.id.trailers_layout);
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

            Model model = new Model(Parcel.obtain());
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
                Log.e("DetailFragment: ", "Error ", e);
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
                        Log.e("DetailFragment: ", "Error closing stream", e);
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
                ReviewsAdapter reviewsAdapter = new ReviewsAdapter(getActivity(), mReviewArrayList);
                ListView listView = (ListView) rootView.findViewById(R.id.reviews_list);
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

            Model model = new Model(Parcel.obtain());
            model.setAuthor(poster.getString(AUTHOR_KEY));
            model.setReview(poster.getString(REVIEW_KEY));

            mReviewArrayList.add(model);

        }

        return resultStrs;
    }

}
