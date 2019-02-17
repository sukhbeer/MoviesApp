package com.example.android.movies.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.example.android.movies.Database.MoviesDatabase;
import com.example.android.movies.ModelClasses.MoviesResult;

public class MainViewModel extends ViewModel {

    private LiveData<MoviesResult> moviesEntity;

    public MainViewModel(@NonNull MoviesDatabase movieDatabase , String id) {
        moviesEntity = movieDatabase.moviesDao().getMoviesLiveData(id);
    }

    public LiveData<MoviesResult> getMoviesResults() {
        return moviesEntity;
    }

}