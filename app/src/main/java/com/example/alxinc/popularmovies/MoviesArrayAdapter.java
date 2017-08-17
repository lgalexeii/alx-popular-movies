package com.example.alxinc.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.alxinc.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by B942272 on 07/08/2017.
 */

public class MoviesArrayAdapter extends ArrayAdapter<Movie> {
    private Context context;
    private LayoutInflater inflater;

    private List<Movie> imageUrls;

    public MoviesArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Movie> objects) {
        super(context, resource, objects);

        this.context = context;
        this.imageUrls = objects;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if (null == convertView) {
            convertView = inflater.inflate(R.layout.poster_layout, parent, false);
        }
        String imageToLoad = "http://image.tmdb.org/t/p/w185" + imageUrls.get(position).getPoster();
        Picasso
                .with(context)
                .load(imageToLoad )
                .fit() // will explain later
                .into((ImageView) convertView);

        return convertView;
    }
}
