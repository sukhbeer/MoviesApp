package com.example.android.movies.Adapters;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movies.Database.MoviesDatabase;
import com.example.android.movies.DetailActivity;
import com.example.android.movies.MainActivity;
import com.example.android.movies.ModelClasses.MoviesResult;
import com.example.android.movies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapterHomeScreen extends RecyclerView.Adapter<MovieAdapterHomeScreen.myViewHolder> {
    private final List<MoviesResult> mResultList;
    private final Context mContext;

    public @BindView(R.id.tv_fav_zero) TextView tv_fav_zero;

    public static class myViewHolder extends RecyclerView.ViewHolder  {
        public @BindView(R.id.row_item_iv) ImageView mImageView;
        public @BindView(R.id.tv_main_screen_movie_name) TextView movieName;
        public @BindView(R.id.tv_main_screen_star_rating) TextView rating;
        public myViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public MovieAdapterHomeScreen(Context context, List<MoviesResult> resultList) {
        mResultList = resultList;
        mContext = context;
    }

    @NonNull
    @Override
    public MovieAdapterHomeScreen.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movies_row_item, parent, false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, int position) {
        MoviesDatabase moviesDatabase = MoviesDatabase.getInstance(mContext);

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(mContext, DetailActivity.class);
                myIntent.putExtra("movie_id" , mResultList.get(holder.getAdapterPosition()).getId());
                myIntent.putExtra("nav_item_selected" , MainActivity.nav_item_selected);
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(mContext, R.anim.fade_in, R.anim.fade_out);
                mContext.startActivity(myIntent, options.toBundle());

            }
        });

        if(MainActivity.nav_item_selected == MainActivity.INT_POPULAR_MOVIES || MainActivity.nav_item_selected == MainActivity.INT_TOP_RATED_MOVIES) {
            Picasso.get()
                    .load("http://image.tmdb.org/t/p/w780/" + mResultList.get(position).getBackdropPath())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(holder.mImageView);
            holder.movieName.setText(mResultList.get(position).getTitle());
            holder.rating.setText(mResultList.get(position).getRating().toString());
        }else if(MainActivity.nav_item_selected == MainActivity.INT_FAVOURITES){
            if(moviesDatabase.moviesDao().getAllMovies().size() != 0) {
                Picasso.get()
                        .load("http://image.tmdb.org/t/p/w780/" + moviesDatabase.moviesDao().getAllMovies().get(position).getBackdropPath())
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .into(holder.mImageView);
                holder.movieName.setText(moviesDatabase.moviesDao().getAllMovies().get(position).getTitle());
                holder.rating.setText(moviesDatabase.moviesDao().getAllMovies().get(position).getRating().toString());
            }else if(moviesDatabase.moviesDao().getAllMovies().size() == 0){
                tv_fav_zero.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public int getItemCount() {
        return mResultList.size();
    }
}