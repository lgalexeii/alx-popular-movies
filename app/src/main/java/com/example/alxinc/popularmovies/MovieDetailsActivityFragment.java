package com.example.alxinc.popularmovies;

import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.alxinc.model.Movie;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsActivityFragment extends Fragment {

    public static String LOG_VIEW = MovieDetailsActivityFragment.class.getSimpleName();

    public MovieDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);

        Movie movieDetails = getActivity(). getIntent().getParcelableExtra("movieDetails");

        ImageView imageView = (ImageView) rootView.findViewById( R.id.posterImg );

        String imageToLoad = "http://image.tmdb.org/t/p/w500" + movieDetails.getPoster();
        Log.v("imageToLoad +-->", imageToLoad);

        Picasso.with(getActivity()).load(imageToLoad).fit().into(imageView);

        setText(rootView,R.id.title_text ,movieDetails.getTitle() );
        //setText(rootView,R.id.id_text ,movieDetails.getId().toString() );
        setText(rootView,R.id.overview_text ,movieDetails.getOverview() );
        setText(rootView,R.id.user_rating_text ,movieDetails.getUserRating());
        setText(rootView,R.id.release_date_text ,movieDetails.getReleaseDate());
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //set the correct layout according to orientation
        detectOrientation(view);
    }

    private void setText(View view, int textId, String text){
        TextView overviewView = (TextView) view.findViewById( textId);
        overviewView.setText(text );
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            changeOverviewLand(getView());
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            changeOverviewPort(getView());
        }

    }

    private void changeOverviewLand(View view){

        final RelativeLayout.LayoutParams paramsText = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsText.addRule(RelativeLayout.END_OF, R.id.posterImg);
        paramsText.addRule(RelativeLayout.BELOW, R.id.synopsis_label);

        view.findViewById(R.id.overview_text).setLayoutParams(paramsText);

        final RelativeLayout.LayoutParams paramsLabel = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLabel.addRule(RelativeLayout.BELOW, R.id.release_date_text);
        paramsLabel.addRule(RelativeLayout.END_OF, R.id.posterImg);
        view.findViewById(R.id.synopsis_label).setLayoutParams(paramsLabel);
    }

    private void changeOverviewPort(View view){

        final RelativeLayout.LayoutParams paramsText = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsText.addRule(RelativeLayout.BELOW, R.id.synopsis_label);

        view.findViewById(R.id.overview_text).setLayoutParams(paramsText);

        final RelativeLayout.LayoutParams paramsLabel = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLabel.addRule(RelativeLayout.BELOW, R.id.posterImg);
        view.findViewById(R.id.synopsis_label).setLayoutParams(paramsLabel);
    }

    private void detectOrientation(View view){
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            changeOverviewLand(view);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            changeOverviewPort(view);
        }
    }

}
