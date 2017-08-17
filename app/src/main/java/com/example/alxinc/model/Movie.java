package com.example.alxinc.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by B942272 on 07/08/2017.
 */

public class Movie implements Parcelable {
    private Integer id;
    private String title;
    private String overview;
    private String poster;
    private String userRating;
    private String releaseDate;


    protected Movie(Parcel in) {
        this.id = in.readInt();
        title = in.readString();
        overview = in.readString();
        poster = in.readString();
        userRating  = in.readString();
        releaseDate  = in.readString();
    }

    public Movie(Integer id,String title, String overview, String poster
            , String userRating, String releaseDate){
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.poster = poster;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.id);
        parcel.writeString(this.title);
        parcel.writeString(this.overview);
        parcel.writeString(this.poster);
        parcel.writeString(this.userRating);
        parcel.writeString(this.releaseDate);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
