package com.android.popularmovies;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.android.popularmovies.Adapters.FavoriteMovieAdapter;
import com.android.popularmovies.Models.FavoriteMovie;

import java.util.List;

/**
 * Created by sagar_000 on 9/13/2016.
 */
public class FavoriteActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        loadFavoriteMovies();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.fav_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        if (id == R.id.action_delete){
            FavoriteMovie.deleteAll(FavoriteMovie.class);
            loadFavoriteMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFavoriteMovies(){
        List <FavoriteMovie> favoriteMovie = FavoriteMovie.listAll(FavoriteMovie.class);
        FavoriteMovieAdapter movieAdapter = new FavoriteMovieAdapter(this, favoriteMovie);
        GridView gridView = (GridView) findViewById(R.id.fav_grid_view);
        gridView.setAdapter(movieAdapter);
    }


}
