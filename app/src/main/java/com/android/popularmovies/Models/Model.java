package com.android.popularmovies.Models;

/**
 * Created by sagar_000 on 9/6/2016.
 */
public class Model  {

    String Image;
    String Title;
    String Plot;
    double Rating;
    String Release;
    String Id;
    String trailer;
    String author;
    String review;

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

    public String getId(){
        return Id;
    }

    public void setId(String Id){
        this.Id = Id;
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
