package com.example.android.movies;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.movies.Adapters.MovieAdapterHomeScreen;
import com.example.android.movies.Database.MoviesDatabase;
import com.example.android.movies.ModelClasses.Movies;
import com.example.android.movies.ModelClasses.MoviesResult;
import com.example.android.movies.Networking.ApiInterface;
import com.example.android.movies.Networking.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final int INT_POPULAR_MOVIES = 0;
    public static final int INT_TOP_RATED_MOVIES = 1;
    public static final int INT_FAVOURITES = 2;
    public Parcelable mLayoutManagerSavedState;

    //public @BindView(R.id.toolbar) Toolbar toolbar;
    public @BindView(R.id.content_frame) RelativeLayout relativeLayout;
    public @BindView(R.id.recyclerView) RecyclerView recyclerView;
    public @BindView(R.id.progress_bar) ProgressBar progressBar;
    public @BindView(R.id.bottom_nav_bar) BottomNavigationView bottomNavigationView;
    public @BindView(R.id.tv_fav_zero) TextView tv_fav_zero;
   // public @BindView(R.id.tv_toolbar) TextView tv_toolbar;

    private Call<Movies> call;
    public static final String API_KEY = "b0cb77ae989f5a61434edb0bc96792dc";
    private Movies mMovieModel;
    private List<MoviesResult> moviesResultList;
    private MovieAdapterHomeScreen adapter;
    public static int nav_item_selected;
    private MoviesDatabase moviesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        call = null;

        moviesDatabase = MoviesDatabase.getInstance(MainActivity.this);
        tv_fav_zero.setVisibility(View.GONE);

        //setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("");

        moviesResultList = new ArrayList<>();
        setUpBottomNavBar();

//        final int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 5 : 3;
        final int spanCount = 1;

        if(savedInstanceState == null){
            nav_item_selected = INT_POPULAR_MOVIES;
        }else if(savedInstanceState != null){
            nav_item_selected = savedInstanceState.getInt("nav_item_selected");
        }

        adapter = new MovieAdapterHomeScreen(this , moviesResultList);

        if(nav_item_selected == INT_POPULAR_MOVIES){
            loadPopularMovies();
        }else if(nav_item_selected == INT_TOP_RATED_MOVIES){
            loadTopRatedMovies();
        }else if(nav_item_selected == INT_FAVOURITES){
            loadFavourites();
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this , spanCount));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nav_item_selected == INT_FAVOURITES){
            loadFavourites();
        }else if(nav_item_selected == INT_TOP_RATED_MOVIES){
            loadTopRatedMovies();
        }else if(nav_item_selected == INT_POPULAR_MOVIES){
            loadPopularMovies();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {;
        outState.putInt("nav_item_selected" , nav_item_selected);
        outState.putParcelable("recycler_view_position", recyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mLayoutManagerSavedState = savedInstanceState.getParcelable("recycler_view_position");
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void makeApiRequest(Call<Movies> call){
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(@NonNull Call<Movies> call, @NonNull Response<Movies> response) {
                mMovieModel = null;
                mMovieModel = response.body();

                if(moviesResultList != null){
                    moviesResultList.clear();
                }

                if (mMovieModel != null) {
                    moviesResultList.addAll(mMovieModel.getResults());
                }else{
                    Snackbar snackbar = Snackbar
                            .make(relativeLayout, "mMovieModel is null", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();

                if (mLayoutManagerSavedState != null) {
                    recyclerView.getLayoutManager().onRestoreInstanceState(mLayoutManagerSavedState);
                    mLayoutManagerSavedState = null;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Movies> call, @NonNull Throwable t) {
                if(call.isCanceled()){

                }else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar
                            .make(relativeLayout, "Failed Loading List", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    private void loadPopularMovies(){
        if(call != null) {
            call.cancel();
        }
        nav_item_selected = INT_POPULAR_MOVIES;
        tv_fav_zero.setVisibility(View.GONE);
        //tv_toolbar.setText(R.string.popular_movies);
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        call = apiInterface.getPopularMovies(API_KEY);
        makeApiRequest(call);
    }

    private void loadTopRatedMovies(){
        if(call != null) {
            call.cancel();
        }
        tv_fav_zero.setVisibility(View.GONE);
        nav_item_selected = INT_TOP_RATED_MOVIES;
        //tv_toolbar.setText(R.string.top_rated);
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        call = apiInterface.getTopRatedMovies(API_KEY);
        makeApiRequest(call);
    }

    private void loadFavourites(){
        if(call != null) {
            call.cancel();
        }
        nav_item_selected = INT_FAVOURITES;
        //tv_toolbar.setText(R.string.favourites);
        if(moviesResultList != null){
            moviesResultList.clear();
        }

        if(moviesDatabase.moviesDao().getAllMovies().size() == 0){
            tv_fav_zero.setVisibility(View.VISIBLE);
        }else{
            tv_fav_zero.setVisibility(View.GONE);
            for (int i = 0; i < moviesDatabase.moviesDao().getAllMovies().size(); i++) {
                MoviesResult result = new MoviesResult(moviesDatabase.moviesDao().getAllMovies().get(i).getId(),
                        moviesDatabase.moviesDao().getAllMovies().get(i).getTitle(),
                        moviesDatabase.moviesDao().getAllMovies().get(i).getOverview(),
                        moviesDatabase.moviesDao().getAllMovies().get(i).getPosterPath(),
                        moviesDatabase.moviesDao().getAllMovies().get(i).getRating(),
                        moviesDatabase.moviesDao().getAllMovies().get(i).getReleaseDate(),
                        moviesDatabase.moviesDao().getAllMovies().get(i).getBackdropPath(),
                        moviesDatabase.moviesDao().getAllMovies().get(i).getFavourite());

                moviesResultList.add(result);
            }
        }

        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

    private void setUpBottomNavBar(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                switch(item.getItemId()){

                    case R.id.nav_popular_movies:
                        loadPopularMovies();
                        break;

                    case R.id.nav_top_rated:
                        loadTopRatedMovies();
                        break;

                    case R.id.nav_favourites:
                        loadFavourites();
                        break;
                }
                return true;
            }
        });
    }
}