package com.android.popularmovies.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.android.popularmovies.FavoriteActivity;
import com.android.popularmovies.MovieTask;
import com.android.popularmovies.R;
import com.android.popularmovies.SettingsActivity;
import com.android.popularmovies.models.Model;

/**
 * Created by sagar_000 on 9/22/2016.
 */
public class MoviesFragment extends Fragment {

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();

    public static View rootView;
    public static GridView gridView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        rootView = inflater.inflate(R.layout.fragment_movies, null);

        if (savedInstance != null) {
            MovieTask.mProgressDialog.onRestoreInstanceState(savedInstance);
       }

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        MovieTask.mProgressDialog.onSaveInstanceState();
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause(){
        super.onPause();
        if (gridView != null){
            SharedPreferences preferences = getActivity().getSharedPreferences("SCROLL", 0);
            SharedPreferences.Editor editor = preferences.edit();
            int scroll = gridView.getFirstVisiblePosition();
            editor.putInt("ScrollValue", scroll);
            editor.apply();
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


    public void updateMovies(){
        Model.deleteAll(Model.class);
        MovieTask movieTask = new MovieTask(getActivity());
        movieTask.execute();
    }

}
