package com.example.alxinc.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.alxinc.model.Movie;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieLsFragment extends Fragment {

    private MoviesArrayAdapter movieLsAdapter;

    public MovieLsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieLsAdapter = new MoviesArrayAdapter(
                getActivity(),
                R.layout.poster_layout,
                new ArrayList<Movie>()
        );

        GridView gridView = (GridView) rootView.findViewById(R.id.list_movies_view);
        gridView.setAdapter(movieLsAdapter);
        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast.makeText(getContext(), movieLsAdapter.getItem(i).getId().toString(),Toast.LENGTH_LONG ).show();

                        Intent movieDetailsIntent = new Intent(getActivity(),MovieDetailsActivity.class);
                        movieDetailsIntent.putExtra("movieDetails", movieLsAdapter.getItem(i)); //movieLsAdapter.getItem(i) );
                                startActivity(movieDetailsIntent);
                    }
                }
        );

        return rootView;
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    void updateMoviesls(){
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String orderBy = sharedPrefs.getString(
                    getString(R.string.pref_order_key),
                    getString(R.string.pref_order_label_popular));

        fetchMoviesTask.execute(orderBy);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(this.isOnline())
            updateMoviesls();
        else {
            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
            Toast.makeText(getActivity(),
                    "This app needs Internet Connection, please verify your service and try again",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Inner class for get data from server
     */
    public class FetchMoviesTask extends AsyncTask<String,Void,List<Movie>> {

        private final String LOG_TAG =  MovieLsFragment.class.getSimpleName() ;

        @Override
        protected List<Movie> doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            int numMovies = 20;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String MOVIES_BASE_URL =
                        "https://api.themoviedb.org/3/movie/";
                final String API_ID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendPath(params[0])  //searchType
                        .appendQueryParameter(API_ID_PARAM, BuildConfig. OPEN_MOVIES_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

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
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
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
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr, numMovies);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> result) {
            Log.v(LOG_TAG, "onPostExecute");
            if (result != null) {
                movieLsAdapter .clear();
                movieLsAdapter.addAll(result);
                // New data is back from the server.  Hooray!
            }
        }



        private List<Movie> getMoviesDataFromJson(String moviesJsonStr, int numMovies)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "results";

            JSONObject forecastJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = forecastJson.getJSONArray(OWM_LIST);

            List<Movie> resultStrs = new ArrayList<>();

            Movie movie;
            for(int i = 0; i < moviesArray.length(); i++) {

                JSONObject movies = moviesArray.getJSONObject(i);

                movie = new Movie(movies.getInt("id"), movies.getString("title"),
                        movies.getString("overview"), movies.getString("poster_path"),
                        movies.getString("vote_average"), movies.getString("release_date")
                );

                resultStrs.add(movie);
            }
            return resultStrs;

        }

    }

}
