package com.example.android.movies.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieResult {



    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("voter_average")
    @Expose
    private Double rating;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("favourites")
    @Expose
    private boolean favourite;

    public MovieResult(Integer id, Double rating, String title, String posterPath, String backdropPath, String overview, String releaseDate, boolean favourite) {
        this.id = id;
        this.rating = rating;
        this.title = title;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.favourite = favourite;
    }

    public Integer getId() {
        return id;
    }

    public Double getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public boolean isFavourite() {
        return favourite;
    }
}
