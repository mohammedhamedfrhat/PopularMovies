package com.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    // These two need to be declared outside the try/catch
    // so that they can be closed in the finally block.
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;

    // Will contain the raw JSON response as a string.
    String posterJsonStr = null;

    String Category = null;

    private List<Model> mMovieArrayList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onStart(){
        super.onStart();
        if (NetworkUtility.isOnline(this)){
            updateMovies();
        } else {
            Toast.makeText(MainActivity.this,"Please Enable Internet Services",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings){
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public class MovieTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute(){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String categoryType = preferences.getString(getString(R.string.pref_key_category),Constants.POPULARITY_KEY);
            if(categoryType.equals(Constants.TOP_RATED_KEY)){
                Category = Constants.TOP_RATED_KEY;
            } else if (categoryType.equals(Constants.POPULARITY_KEY)){
                Category = Constants.POPULARITY_KEY;
            }
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
                return getWeatherDataFromJson(posterJsonStr);
            } catch (JSONException e) {
                Log.d(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result){
            if(result != null) {
                MovieAdapter movieAdapter = new MovieAdapter(MainActivity.this,mMovieArrayList);

                GridView gridView = (GridView) findViewById(R.id.grid_view);
                gridView.setAdapter(movieAdapter);
            }
        }

    }

    public void updateMovies(){
        MovieTask movieTask = new MovieTask();
        movieTask.execute();

    }

    private String[] getWeatherDataFromJson(String posterJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String IMAGE_URL = "poster_path";
        final String RATING_COUNT = "vote_average";
        final String RESULTS = "results";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_RELEASE = "release_date";

        JSONObject posterJson = new JSONObject(posterJsonStr);
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

            mMovieArrayList.add(model);

        }

        return resultStrs;
    }
}


