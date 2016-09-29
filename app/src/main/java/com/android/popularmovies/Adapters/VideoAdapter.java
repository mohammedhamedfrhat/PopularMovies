package com.android.popularmovies.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
 * Created by sagar_000 on 9/19/2016.
 */
public class VideoAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<Model> VideoArrayList = null;
    private ArrayList<Model> mArrayList;

    public VideoAdapter(Context context, List<Model> VideoArrayList) {
        this.context = context;
        this.VideoArrayList = VideoArrayList;
        inflater = LayoutInflater.from(context);
        this.mArrayList = new ArrayList<>();
        this.mArrayList.addAll(VideoArrayList);
    }

    public class ViewHolder {
        ImageView thumbView;
    }

    @Override
    public int getCount() {
        return VideoArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return VideoArrayList.get(i);
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
            view = inflater.inflate(R.layout.videos_layout, null);
            holder.thumbView = (ImageView) view.findViewById(R.id.youtube_trailer);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Picasso.with(context).load(String.valueOf(Constants.YOUTUBE_THUMB_LINK + VideoArrayList.get(i).getTrailer() + Constants.YOUTUBE_THUMB_FORMAT)).placeholder(R.drawable.youtube).into(holder.thumbView);

        holder.thumbView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_APP_CONSTRUCTOR + VideoArrayList.get(i).getTrailer()));
                    context.startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(Constants.YOUTUBE_LINK_CONSTRUCTOR + VideoArrayList.get(i).getTrailer()));
                    context.startActivity(intent);
                }
            }
        });
        return view;
    }
}
