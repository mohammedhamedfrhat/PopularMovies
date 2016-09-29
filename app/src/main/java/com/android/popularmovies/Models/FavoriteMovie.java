package com.android.popularmovies.Models;

import com.orm.SugarRecord;

/**
 * Created by sagar_000 on 9/12/2016.
 */

public class FavoriteMovie extends SugarRecord {

    String movieTitle;
    String moviePlot;
    double movieRatings;
    String moviePoster;
    String releaseDate;
    String movieId;


    public FavoriteMovie() {}

    public FavoriteMovie(String movieTitle, String moviePlot,String releaseDate, double movieRatings, String moviePoster, String movieId) {
        this.movieTitle = movieTitle;
        this.moviePlot = moviePlot;
        this.releaseDate = releaseDate;
        this.movieRatings = movieRatings;
        this.moviePoster = moviePoster;
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getMoviePlot() {
        return moviePlot;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public double getMovieRatings() {
        return movieRatings;
    }

    public String getMoviePoster() {
        return moviePoster;
    }
}
