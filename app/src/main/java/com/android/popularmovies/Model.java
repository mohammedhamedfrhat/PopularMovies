package com.android.popularmovies;

/**
 * Created by sagar_000 on 9/6/2016.
 */
public class Model {

    String Image;
    String Title;
    String Plot;
    double Rating;
    String Release;

    public String getImage(){
        return Image;
    }

    public void setImage(String Image){
        this.Image = Image;
    }

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

}
