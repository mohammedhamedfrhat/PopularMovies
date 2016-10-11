package com.android.popularmovies.models;

import android.os.Parcel;

import com.orm.SugarRecord;

/**
 * Created by sagar_000 on 9/6/2016.
 */
public class Model extends SugarRecord  {

    String Image;
    String Title;
    String Plot;
    double Rating;
    String Release;
    long Id;
    String trailer;
    String author;
    String review;


    public Model() {}

    public Model(Parcel in) {
        Image = in.readString();
        Title = in.readString();
        Plot = in.readString();
        Rating = in.readDouble();
        Release = in.readString();
        Id = in.readLong();
        trailer = in.readString();
        author = in.readString();
        review = in.readString();
    }

    public Model(String Image, String Title,String Plot, double Rating, String Release) {
        this.Image = Image;
        this.Title = Title;
        this.Plot = Plot;
        this.Rating = Rating;
        this.Release = Release;
    }


    public String getImage(){
        return Image;
    }

    public void setImage(String Image){
        this.Image = Image;
    }

    public String getTrailer(){return trailer;}

    public void setTrailer(String trailer){this.trailer = trailer;}

    public String getRelease(){
        return Release;
    }

    public void setRelease(String Release){
        this.Release = Release;
    }

    public String getTitle(){
        return Title;
    }

    public void setTitle(String Title){
        this.Title = Title;
    }

    public String getPlot(){
        return Plot;
    }

    public void setPlot(String Plot){
        this.Plot = Plot;
    }

    public double getRating() {
        return Rating;
    }

    public void setRating(double Rating){
        this.Rating = Rating;
    }

    public String getAuthor(){return author;}

    public void setAuthor(String author){
        this.author = author;
    }

    public String getReview()
    { return  review;}

    public void setReview(String review){
        this.review = review;
    }

}
