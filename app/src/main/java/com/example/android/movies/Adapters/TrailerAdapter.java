package com.example.android.movies.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.android.movies.ModelClasses.TrailerResult;
import com.example.android.movies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerAdapter extends PagerAdapter {

    public Context context;
    public List<TrailerResult> trailerResultList;
    public String TRAILER_BASE_URL = "http://www.youtube.com/watch?v=";
    public String TRAILER_THUMBNAIL_START = "https://img.youtube.com/vi/";
    public String TRAILER_THUMBNAIL_END = "/0.jpg";
    public LayoutInflater mLayoutInflater;

//    public @BindView(R.id.iv_trailer_thumb_item) ImageView thumbnailImage;

    public TrailerAdapter(Context context, List<TrailerResult> trailerResultList) {
        this.context = context;
        this.trailerResultList = trailerResultList;
        mLayoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        Log.e("Trailers Size", String.valueOf(trailerResultList.size()));
        return trailerResultList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.trailers_row_item, container, false);

//        ButterKnife.bind(context, itemView);

        ImageView thumbnailImage = itemView.findViewById(R.id.iv_trailer_thumb_item);

        Picasso.get()
                .load(TRAILER_THUMBNAIL_START + trailerResultList.get(position).getKey() + TRAILER_THUMBNAIL_END)
                .into(thumbnailImage);

        thumbnailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="
                        + trailerResultList.get(position).getKey()));
                context.startActivity(intent);
            }
        });

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((FrameLayout) object);
    }
}