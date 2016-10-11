package com.android.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.popularmovies.FavoriteDetailActivity;
import com.android.popularmovies.models.FavoriteMovie;
import com.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FavoriteMovieAdapter extends BaseAdapter {


    private static final String LOG_TAG = FavoriteMovieAdapter.class.getSimpleName();


    Context context;
    LayoutInflater inflater;
    List<FavoriteMovie> MovieArrayList = null;
    private ArrayList<FavoriteMovie> mArrayList;

    public FavoriteMovieAdapter(Context context, List<FavoriteMovie> MovieArrayList) {
        this.context = context;
        this.MovieArrayList = MovieArrayList;
        inflater = LayoutInflater.from(context);
        this.mArrayList = new ArrayList<>();
        this.mArrayList.addAll(MovieArrayList);
    }

    public class ViewHolder {
        ImageView posterView;
    }

    @Override
    public int getCount() {
        return MovieArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return MovieArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.fav_item_layout, null);
            holder.posterView = (ImageView) view.findViewById(R.id.fav_poster_view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Picasso.with(context).load(new File(MovieArrayList.get(i).getMoviePoster())).into(holder.posterView);
        Log.d(LOG_TAG,MovieArrayList.get(i).getMoviePoster());
        Log.d(LOG_TAG,MovieArrayList.get(i).getMovieTitle());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,FavoriteDetailActivity.class);
                intent.putExtra("title", MovieArrayList.get(i).getMovieTitle());
                intent.putExtra("poster",MovieArrayList.get(i).getMoviePoster());
                intent.putExtra("plot",MovieArrayList.get(i).getMoviePlot());
                intent.putExtra("rating",MovieArrayList.get(i).getMovieRatings());
                intent.putExtra("release", MovieArrayList.get(i).getReleaseDate());
                intent.putExtra("id",MovieArrayList.get(i).getId());
                context.startActivity(intent);
            }
        });
        return view;
    }

}
