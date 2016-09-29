package com.android.popularmovies.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.android.popularmovies.Adapters.MovieAdapter;
import com.android.popularmovies.FavoriteActivity;
import com.android.popularmovies.MainActivity;
import com.android.popularmovies.Models.Constants;
import com.android.popularmovies.Models.Model;
import com.android.popularmovies.R;
import com.android.popularmovies.SettingsActivity;
import com.android.popularmovies.Utils.NetworkUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagar_000 on 9/22/2016.
 */
public class MoviesFragment extends Fragment {

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();

    // These two need to be declared outside the try/catch
    // so that they can be closed in the finally block.
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;

    // Will contain the raw JSON response as a string.
    String movieJsonStr = null;

    String Category = null;

    private List<Model> mMovieArrayList = null;

    ProgressDialog mProgressDialog;

    View rootView;

    boolean mUseLandscapeLayout;

    MovieAdapter mMovieAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){

        rootView = inflater.inflate(R.layout.fragment_movies, null);

        setHasOptionsMenu(true);



        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        if(NetworkUtility.isOnline(getActivity())){
            updateMovies();
        } else {
            Toast.makeText(getActivity(),"Please Enable Internet Services",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        inflater.inflate(R.menu.menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_favorite){
            Intent intent = new Intent(getActivity(),FavoriteActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_settings){
            Intent intent = new Intent(getActivity(),SettingsActivity.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public class MovieTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute(){
            mProgressDialog = new ProgressDialog(getActivity());
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String categoryType = preferences.getString(getString(R.string.pref_key_category) ,getString(R.string.pref_category_default_value));
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
                MovieAdapter movieAdapter = new MovieAdapter(getActivity(), mMovieArrayList, new MovieAdapter.Callback() {
                    @Override
                    public void onItemSelected(String title, String poster, String plot, double ratings, String release, String id) {
                        if (MainActivity.mTwoPane) {
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.details_fragment_container, DetailsFragment.newInstance(title, poster, plot, ratings, release, id))
                                    .commit();
                            Log.d(LOG_TAG, "Movie Title: " +title);
                        } else {
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,DetailsFragment.newInstance(title, poster, plot, ratings, release, id))
                                    .addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                        }
                    }
                });

                GridView gridView = (GridView) rootView.findViewById(R.id.grid_view);
                gridView.setAdapter(movieAdapter);

                mProgressDialog.dismiss();
            }
        }

    }

    public void updateMovies(){
        MovieTask movieTask = new MovieTask();
        movieTask.execute();

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
        mMovieArrayList = new ArrayList<Model>();
        for(int i = 0; i < movieArray.length(); i++) {

            JSONObject poster = movieArray.getJSONObject(i);

            Model model = new Model();
            model.setImage(poster.getString(IMAGE_URL));
            model.setTitle(poster.getString(MOVIE_TITLE));
            model.setPlot(poster.getString(MOVIE_OVERVIEW));
            model.setRating(poster.getDouble(RATING_COUNT));
            model.setRelease(poster.getString(MOVIE_RELEASE));
            model.setId(poster.getString(MOVIE_ID));

            mMovieArrayList.add(model);

        }

        return resultStrs;
    }

    public void setLandscapeLayout(boolean useLandscapeLayout) {
        mUseLandscapeLayout = useLandscapeLayout;
        if (mMovieAdapter != null) {
            mMovieAdapter.setUseLandscapeLayout(mUseLandscapeLayout);
        }
    }

}
