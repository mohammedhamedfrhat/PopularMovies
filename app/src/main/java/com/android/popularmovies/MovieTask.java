package com.android.popularmovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.android.popularmovies.adapters.MovieAdapter;
import com.android.popularmovies.fragments.DetailsFragment;
import com.android.popularmovies.fragments.MoviesFragment;
import com.android.popularmovies.models.Constants;
import com.android.popularmovies.models.Model;
import com.android.popularmovies.utils.NetworkUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by sagar_000 on 10/8/2016.
 */
public class MovieTask extends AsyncTask<String, Void, String[]> {

    private static final String LOG_TAG = MovieTask.class.getSimpleName();


    public static ProgressDialog mProgressDialog;

    Context mContext;
    String Category = null;
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String movieJsonStr = null;


    public MovieTask(Context context ) {
        mContext = context;
    }

    @Override
    protected void onPreExecute(){
        mProgressDialog = new ProgressDialog(mContext);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String categoryType = preferences.getString(mContext.getString(R.string.pref_key_category) ,mContext.getString(R.string.pref_category_default_value));
        if(categoryType.equals(Constants.TOP_RATED_KEY)){
            Category = Constants.TOP_RATED_KEY;
            mProgressDialog.setTitle("Top Rated Movies");
        } else if (categoryType.equals(Constants.POPULARITY_KEY)){
            Category = Constants.POPULARITY_KEY;
            mProgressDialog.setTitle("Popular Movies");

        }
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }

    @Override
    protected String[] doInBackground(String... strings) {

        try {

            URL url = new URL(Constants.MOVIE_URL + Category + Constants.API_KEY);

            Log.d(LOG_TAG, String.valueOf(url));

            // Create the request to OpenWeatherMap, and open the connection
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
            movieJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
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
            return getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.d(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String[] result){
        if(result != null) {
            mProgressDialog.dismiss();

            if(NetworkUtility.isOnline(mContext)){

                List<Model> favoriteMovie = Model.listAll(Model.class);
                MovieAdapter movieAdapter = new MovieAdapter(mContext, favoriteMovie, new MovieAdapter.Callback() {
                    @Override
                    public void onItemSelected(String title, String poster, String plot, double ratings, String release, long id) {
                        if (MainActivity.mTwoPane) {
                            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.details_fragment_container, DetailsFragment.newInstance(title, poster, plot, ratings, release, id))
                                    .commit();
                        } else {
                            Intent intent = new Intent(mContext, DetailsActivity.class);
                            intent.putExtra("title", title);
                            intent.putExtra("poster", poster);
                            intent.putExtra("plot",plot);
                            intent.putExtra("ratings", ratings);
                            intent.putExtra("release", release);
                            intent.putExtra("id", id);
                            mContext.startActivity(intent);

                        }
                    }

                });
                MoviesFragment.gridView = (GridView) MoviesFragment.rootView.findViewById(R.id.grid_view);
                MoviesFragment.gridView.setAdapter(movieAdapter);
                SharedPreferences preferences = mContext.getSharedPreferences("SCROLL", 0);
                int scroll = preferences.getInt("ScrollValue", 0);
                MoviesFragment.gridView.smoothScrollToPosition(scroll);
            } else {
                Toast.makeText(mContext,"Please Enable Internet Services",Toast.LENGTH_SHORT).show();
            }

        }
    }


    private String[] getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String IMAGE_URL = "poster_path";
        final String RATING_COUNT = "vote_average";
        final String RESULTS = "results";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_RELEASE = "release_date";
        final String MOVIE_ID = "id";

        JSONObject posterJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = posterJson.getJSONArray(RESULTS);

        String[] resultStrs = new String[movieArray.length()];
        for(int i = 0; i < movieArray.length(); i++) {

            JSONObject poster = movieArray.getJSONObject(i);

            Model model = new Model(poster.getString(IMAGE_URL), poster.getString(MOVIE_TITLE),poster.getString(MOVIE_OVERVIEW), poster.getDouble(RATING_COUNT),poster.getString(MOVIE_RELEASE));
            model.setId(Long.parseLong(poster.getString(MOVIE_ID)));
            model.save();

        }

        return resultStrs;
    }

}
