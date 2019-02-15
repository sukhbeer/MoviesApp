package com.example.android.movies.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movies.DetailActivity;
import com.example.android.movies.Model.MovieResult;
import com.example.android.movies.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<MovieResult> resultsList;
    private Context context;

    public MovieAdapter(List<MovieResult> resultsList, Context context) {
        this.resultsList = resultsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_movie,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,DetailActivity.class);
                intent.putExtra("movie_id",resultsList.get(viewHolder.getAdapterPosition()).getId());

            }
        });
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public @BindView(R.id.row_item_iv)
        ImageView imageView;
        @BindView(R.id.tv_main_screen_movie_name)
        TextView movieName;
        @BindView(R.id.tv_main_screen_star_rating)
        TextView rating;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
