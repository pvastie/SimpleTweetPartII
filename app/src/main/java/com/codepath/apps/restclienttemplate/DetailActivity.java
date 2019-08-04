package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    private TextView tvName;
    private ImageView ivProfileImage;
    private TextView tvScreenName2;
    private TextView tvCreatedAt2;
    private TextView tvBody2;

    Tweet tweet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail );

        tvName = findViewById(R.id.tvName2);
        ivProfileImage = findViewById(R.id.ivProfileImage2);
        tvScreenName2 = findViewById(R.id.tvScreenName2);
        tvCreatedAt2 = findViewById(R.id.tvCreadteAt2);
        tvBody2 = findViewById(R.id.tvBody2);


        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        tvName.setText(tweet.user.name);
        tvScreenName2.setText("@" +tweet.user.screenName);
        tweet.getFormatedTimestamp();
        tvCreatedAt2.setText(tweet.createdAt);
        tvBody2.setText(tweet.body);
        Glide.with(this).load(tweet.user.profileImageUrl)
                .into(ivProfileImage);


    }
}


