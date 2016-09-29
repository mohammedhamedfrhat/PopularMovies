package com.android.popularmovies.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.popularmovies.Models.Model;
import com.android.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagar_000 on 9/20/2016.
 */
public class ReviewsAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    private List<Model> ReviewsArrayList = null;
    private ArrayList<Model> mArrayList;

    private static final String LOG_TAG = ReviewsAdapter.class.getSimpleName();

    public ReviewsAdapter(Context context, List<Model> ReviewsArrayList) {
        this.context = context;
        this.ReviewsArrayList = ReviewsArrayList;
        inflater = LayoutInflater.from(context);
        this.mArrayList = new ArrayList<Model>();
        this.mArrayList.addAll(ReviewsArrayList);
    }

    @Override
    public int getCount() {
        return ReviewsArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return ReviewsArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class ViewHolder {
        TextView authorView;
        TextView reviewView;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.reviews_list_item, null);
            holder.authorView = (TextView) view.findViewById(R.id.author);
            holder.reviewView = (TextView) view.findViewById(R.id.review_text);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.authorView.setText(ReviewsArrayList.get(i).getAuthor());
        holder.reviewView.setText(ReviewsArrayList.get(i).getReview());

        return view;
    }
}
