package com.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagar_000 on 9/6/2016.
 */
public class MovieAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    private List<Model> MovieArrayList = null;
    private ArrayList<Model> mArrayList;

    public MovieAdapter(Context context, List<Model> MovieArrayList) {
        this.context = context;
        this.MovieArrayList = MovieArrayList;
        inflater = LayoutInflater.from(context);
        this.mArrayList = new ArrayList<Model>();
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
            view = inflater.inflate(R.layout.grid_item_layout, null);
            holder.posterView = (ImageView) view.findViewById(R.id.poster_view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Picasso.with(context).load(Constants.IMAGE_URL + MovieArrayList.get(i).getImage()).into(holder.posterView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,DetailActivity.class);
                intent.putExtra("title", MovieArrayList.get(i).getTitle());
                intent.putExtra("poster",MovieArrayList.get(i).getImage());
                intent.putExtra("plot",MovieArrayList.get(i).getPlot());
                intent.putExtra("rating",MovieArrayList.get(i).getRating());
                intent.putExtra("release", MovieArrayList.get(i).getRelease());
                context.startActivity(intent);
            }
        });
        return view;
    }
}
