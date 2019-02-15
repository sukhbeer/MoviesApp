package com.example.android.movies;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.android.movies.Adapter.MovieAdapter;
import com.example.android.movies.Model.MovieResult;
import com.example.android.movies.Model.Movies;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    public @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;
    public @BindView(R.id.content_frame)
    RelativeLayout relativeLayout;
    public @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    public @BindView(R.id.bottom_nav_bar)
    BottomNavigationView bottomNavigationView;
    public @BindView(R.id.tv_fav_zero)
    TextView tv_fav_zero;
    public @BindView(R.id.tv_toolbar) TextView tv_toolbar;

    private Call<Movies> call;
    public static final String API_KEY = "b0cb77ae989f5a61434edb0bc96792dc";
    private Movies movieModel;
    private List<MovieResult> moviesResultList;
    private MovieAdapter adapter;
    public static int nav_item_selected;
    //private MoviesDatabase moviesDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int spanCount=1;
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        adapter=new MovieAdapter(moviesResultList,this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,spanCount));
        recyclerView.setAdapter(adapter);
    }
    private void makeApiRequest(Call<Movies> call){
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                movieModel=null;
                movieModel=response.body();

                if(moviesResultList!=null){
                    moviesResultList.clear();
                }
                if(movieModel!=null){
                    moviesResultList.addAll(movieModel.getResults());
                }

                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {

            }
        });
    }

    private void loadPopularMovies(){

    }

    private void loadTopRatedMovies(){

    }

    private void loadFavourites(){

    }

    private void setUpBottomBar(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                switch (menuItem.getItemId()){
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
