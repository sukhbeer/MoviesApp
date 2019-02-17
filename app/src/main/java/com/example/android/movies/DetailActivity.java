package com.example.android.movies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movies.Adapters.ReviewsAdapter;
import com.example.android.movies.Adapters.TrailerAdapter;
import com.example.android.movies.Database.MoviesDatabase;
import com.example.android.movies.ModelClasses.MoviesResult;
import com.example.android.movies.ModelClasses.Reviews;
import com.example.android.movies.ModelClasses.ReviewsResult;
import com.example.android.movies.ModelClasses.TrailerResult;
import com.example.android.movies.ModelClasses.Trailers;
import com.example.android.movies.Networking.ApiInterface;
import com.example.android.movies.Networking.RetrofitClient;
import com.example.android.movies.ViewModel.MainViewModel;
import com.example.android.movies.ViewModel.ViewModelFactory;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {


    public String TRAILER_BASE_URL = "http://www.youtube.com/watch?v=";
    private Context mContext;
    private static Reviews reviewModel;
    private MoviesResult moviesResultObject;
    private static Trailers trailersModel;
    private static List<TrailerResult> trailerResultList;
    private static List<ReviewsResult> reviewsResultList;
    private static List<MoviesResult> moviesInDatabaseList;
    private static int movieId;
    private static int position;
    private MoviesDatabase moviesDatabase;
    public Observer observer;
    private MainViewModel mainViewModel;

//    public @BindView(R.id.linear_layout_detail) LinearLayout linearLayout;
    //TODO
    public @BindView(R.id.view_pager) ViewPager viewPager;

    public @BindView(R.id.rv_reviews) RecyclerView recyclerViewReviews;
    public @BindView(R.id.iv_background) ImageView ivBackground;
    public @BindView(R.id.iv_detail_main_image) ImageView ivDetailMainImage;
    public @BindView(R.id.tv_movie_title_detail) TextView movieTitle;
    public @BindView(R.id.tv_date_released_detail) TextView dateReleased;
    public @BindView(R.id.tv_rating_detail) TextView rating;
    public @BindView(R.id.tv_overview_detail) TextView overview;
    public @BindView(R.id.frame_layout) FrameLayout frameLayout;
    public @BindView(R.id.fab_share_detail) FloatingActionButton shareFAB;
    public @BindView(R.id.fab_fav_detail) FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        position = -1;
        ButterKnife.bind(this);
        moviesDatabase = MoviesDatabase.getInstance(DetailActivity.this);

        mContext = getApplicationContext();

        mainViewModel = ViewModelProviders.of(this , new ViewModelFactory(moviesDatabase , Integer.toString(movieId))).get(MainViewModel.class);

        reviewsResultList = new ArrayList<>();
        trailerResultList = new ArrayList<>();
        moviesInDatabaseList = moviesDatabase.moviesDao().getAllMovies();

        movieId = getIntent().getIntExtra("movie_id" , -1);

        if(movieId == -1 || MainActivity.nav_item_selected == -1){
            Snackbar.make(frameLayout , "Error !!!!" , Snackbar.LENGTH_LONG);
            finish();
        }

        makeApiRequest();

        observer = new Observer<MoviesResult>() {
            @Override
            public void onChanged(@Nullable MoviesResult moviesResult) {
                moviesResultObject = moviesResult;
                mainViewModel.getMoviesResults().removeObserver(this);
            }
        };

        mainViewModel.getMoviesResults().observe(DetailActivity.this, observer);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moviesInDatabaseList = moviesDatabase.moviesDao().getAllMovies();
                int i = 0;
                do{
                    if(moviesInDatabaseList.size() == 0){
                        Snackbar.make(frameLayout , "Movie added to Favourites" , Snackbar.LENGTH_SHORT).show();
                        moviesDatabase.moviesDao().insertMovie(moviesResultObject);
                        moviesResultObject.setFavourite(true);
                        if(mainViewModel.getMoviesResults().getValue() != null) {
                            mainViewModel.getMoviesResults().getValue().setFavourite(true);
                        }
                        moviesDatabase.moviesDao().updateMovie(moviesResultObject);
                        setAppropriateFabImage();
                        break;
                    }

                    if(Objects.equals(moviesResultObject.getId(), moviesInDatabaseList.get(i).getId())){
                        Snackbar.make(frameLayout , "Movie deleted from Favourites" , Snackbar.LENGTH_SHORT).show();
                        moviesDatabase.moviesDao().deleteMovies(moviesResultObject);
                        moviesResultObject.setFavourite(false);
                        if(mainViewModel.getMoviesResults().getValue() != null) {
                            mainViewModel.getMoviesResults().getValue().setFavourite(false);
                        }
                        moviesDatabase.moviesDao().updateMovie(moviesResultObject);
                        setAppropriateFabImage();
                        break;
                    }

                    if(i == (moviesInDatabaseList.size() - 1)){
                        Snackbar.make(frameLayout , "Movie added to Favourites" , Snackbar.LENGTH_SHORT).show();
                        moviesDatabase.moviesDao().insertMovie(moviesResultObject);
                        moviesResultObject.setFavourite(true);
                        if(mainViewModel.getMoviesResults().getValue() != null) {
                            mainViewModel.getMoviesResults().getValue().setFavourite(true);
                        }
                        moviesDatabase.moviesDao().updateMovie(moviesResultObject);
                        setAppropriateFabImage();
                        break;
                    }
                    i++;
                }while (i < moviesInDatabaseList.size());
            }
        });

    }

    @OnClick(R.id.fab_share_detail)
    public void shareBtnClick(){
        if(trailerResultList != null){
            trailerResultList.clear();
        }
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<Trailers> call = apiInterface.getMovieTrailers(Integer.toString(moviesResultObject.getId()), MainActivity.API_KEY);
        call.enqueue(new Callback<Trailers>() {
            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                trailersModel = response.body();
                trailerResultList.addAll(trailersModel.getResults());
                if(trailerResultList.size() == 0){
                    Snackbar.make(frameLayout , "No Trailers available !!!" , Snackbar.LENGTH_SHORT).show();
                } else {
                    sendIntent(trailerResultList.get(0).getKey());
                }
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                Snackbar.make(frameLayout , "Connect to the Internet !!!" , Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void sendIntent(String trailerKey){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the trailer of this great movie :\n" + TRAILER_BASE_URL + trailerKey);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void loadTrailers(){
        if(trailerResultList != null){
            trailerResultList.clear();
        }
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<Trailers> call = apiInterface.getMovieTrailers(Integer.toString(moviesResultObject.getId()), MainActivity.API_KEY);
        call.enqueue(new Callback<Trailers>() {
            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                trailersModel = response.body();

                trailerResultList.addAll(trailersModel.getResults());

                if (trailerResultList.size() == 0) {
//                    TextView textView = new TextView(mContext);
//                    textView.setTextColor(Color.parseColor("#FFFFFF"));
//                    textView.setText(R.string.no_trailer);
//                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                    linearLayout.addView(textView, layoutParams);
                    Toast.makeText(DetailActivity.this, "No Trailers Available !!!", Toast.LENGTH_SHORT).show();
                } else {

                    TrailerAdapter adapter = new TrailerAdapter(DetailActivity.this ,trailerResultList);
                    viewPager.setAdapter(adapter);


//                    for (int i = 0; i < trailerResultList.size(); i++) {
//                        Button button = new Button(mContext);
//                        button.setText(trailerResultList.get(i).getName() + " | " + trailerResultList.get(i).getType());
//                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                        linearLayout.addView(button, layoutParams);
//                        final String id = trailerResultList.get(i).getKey();
//                        button.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + id));
//                                mContext.startActivity(intent);
//                            }
//                        });
//                    }
                }
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {

            }
        });
    }

    private void loadReviews(){
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<Reviews> call = apiInterface.getMovieReviews(Integer.toString(moviesResultObject.getId()), MainActivity.API_KEY);
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                reviewModel = response.body();
                if (reviewsResultList != null) {
                    reviewsResultList.clear();
                }

                reviewsResultList.addAll(reviewModel.getResults());

                List<String> nameList, reviewList;
                String stringOriginal, stringUpper;

                nameList = new ArrayList<>();
                reviewList = new ArrayList<>();

                recyclerViewReviews.setNestedScrollingEnabled(false);

                if(reviewModel.getTotalResults() == 0){
                    Toast.makeText(DetailActivity.this, "No Reviews Available", Toast.LENGTH_SHORT).show();
                    nameList.add(null);
                    reviewList.add(null);
                }else{
                    for (int i = 0 ; i < reviewModel.getTotalResults() ; i++) {
                        try {
                            if (reviewsResultList.get(i) != null) {
                                stringOriginal = reviewsResultList.get(i).getAuthor();
                                stringUpper = stringOriginal.substring(0, 1).toUpperCase() + stringOriginal.substring(1);
                                nameList.add(stringUpper);

                                stringOriginal = reviewsResultList.get(i).getContent();
                                stringUpper = stringOriginal.substring(0, 1).toUpperCase() + stringOriginal.substring(1);
                                reviewList.add(stringUpper);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                ReviewsAdapter reviewsAdapter = new ReviewsAdapter(DetailActivity.this, nameList, reviewList);
                recyclerViewReviews.setHasFixedSize(true);
                recyclerViewReviews.setLayoutManager(new LinearLayoutManager(DetailActivity.this));
                recyclerViewReviews.setAdapter(reviewsAdapter);
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DetailActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void setAppropriateFabImage(){
        for (int i = 0; i < moviesDatabase.moviesDao().getAllMovies().size(); i++) {
            if(Objects.equals(moviesResultObject.getId(), moviesDatabase.moviesDao().getAllMovies().get(i).getId())){
                position = i;
                break;
            }else{
                position = -1;
            }
        }

        if(position >=0){
            floatingActionButton.setImageResource(R.drawable.ic_fav);
        }else{
            floatingActionButton.setImageResource(R.drawable.ic_unfav);
        }
    }


    private void makeApiRequest(){
        ApiInterface apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface.class);
        Call<MoviesResult> callResult = apiInterface.getMovieDetailsById
                (Integer.toString(movieId) , MainActivity.API_KEY);

        callResult.enqueue(new Callback<MoviesResult>() {
            @Override
            public void onResponse(Call<MoviesResult> call, Response<MoviesResult> response) {
                moviesResultObject = response.body();

                for (int i = 0; i < moviesInDatabaseList.size(); i++) {
                    if (Objects.equals(moviesResultObject.getId(), moviesInDatabaseList.get(i).getId())){
                        position = i;
                        break;
                    }
                }

                DecimalFormat precision = new DecimalFormat("0.0");
                rating.setText(precision.format(moviesResultObject.getRating()));

                String publishedAt = moviesResultObject.getReleaseDate();

                try {
                    SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
                    Date newDate = null;
                    newDate = spf.parse(publishedAt);
                    spf = new SimpleDateFormat("dd MMM yyyy");
                    publishedAt = spf.format(newDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                dateReleased.setText(publishedAt);

                movieTitle.setText(moviesResultObject.getTitle());

                overview.setText(moviesResultObject.getOverview());

                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        ivBackground.setImageBitmap(BlurImage.fastblur(bitmap, 1f, 80));
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };

                ivBackground.setTag(target);
                Picasso.get()
                        .load("http://image.tmdb.org/t/p/w780/" + moviesResultObject.getBackdropPath())
                        .error(R.drawable.error)
                        .placeholder(R.drawable.loading)
                        .into(target);


                Picasso.get()
                        .load("http://image.tmdb.org/t/p/w500/" + moviesResultObject.getPosterPath())
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .into(ivDetailMainImage);

                setAppropriateFabImage();

                loadReviews();
                loadTrailers();

            }

            @Override
            public void onFailure(Call<MoviesResult> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(frameLayout , "Connect to the Internet !!!" , Snackbar.LENGTH_LONG);
                snackbar.show();
                DecimalFormat precision = new DecimalFormat("0.0");
                rating.setText("Rating : " + precision.format(moviesInDatabaseList.get(position).getRating()) + " / 10");
                dateReleased.setText(moviesInDatabaseList.get(position).getReleaseDate());
                movieTitle.setText(moviesInDatabaseList.get(position).getTitle());
                overview.setText(moviesInDatabaseList.get(position).getOverview());
                Picasso.get()
                        .load("http://image.tmdb.org/t/p/w780/" + moviesInDatabaseList.get(position).getBackdropPath())
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .into(ivBackground);
                Picasso.get()
                        .load("http://image.tmdb.org/t/p/w500/" + moviesInDatabaseList.get(position).getPosterPath())
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .into(ivDetailMainImage);
            }
        });
    }
}