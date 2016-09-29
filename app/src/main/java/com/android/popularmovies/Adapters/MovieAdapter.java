package com.android.popularmovies.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.popularmovies.Models.Constants;
import com.android.popularmovies.Models.Model;
import com.android.popularmovies.R;
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
    boolean mUseLandscapeLayout;
    Callback mListener;

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();


    public interface Callback {

        void onItemSelected(String title, String poster, String plot, double ratings, String release, String id);
    }


    public MovieAdapter(Context context, List<Model> MovieArrayList, Callback mListener) {
        this.context = context;
        this.MovieArrayList = MovieArrayList;
        inflater = LayoutInflater.from(context);
        this.mArrayList = new ArrayList<Model>();
        this.mArrayList.addAll(MovieArrayList);
        this.mListener = mListener;

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
                mListener.onItemSelected(MovieArrayList.get(i).getTitle(), Constants.IMAGE_URL + MovieArrayList.get(i).getImage(), MovieArrayList.get(i).getPlot(), MovieArrayList.get(i).getRating(), MovieArrayList.get(i).getRelease(), MovieArrayList.get(i).getId() );
                /*Bundle args = new Bundle();
                args.putString("title", MovieArrayList.get(i).getTitle());
                args.putString("poster",Constants.IMAGE_URL + MovieArrayList.get(i).getImage());
                args.putString("plot",MovieArrayList.get(i).getPlot());
                args.putDouble("rating",MovieArrayList.get(i).getRating());
                args.putString("release", MovieArrayList.get(i).getRelease());
                args.putString("id",MovieArrayList.get(i).getId());
                Fragment newFragment = new DetailsFragment();
                newFragment.setArguments(args);
                android.support.v4.app.FragmentTransaction transaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.commit(); */
            }
        });
        return view;
    }

    public void setUseLandscapeLayout(boolean useLandscapeLayout) {
        mUseLandscapeLayout = useLandscapeLayout;
    }
}
