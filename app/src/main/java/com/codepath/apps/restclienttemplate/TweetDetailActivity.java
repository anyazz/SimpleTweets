package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity {
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);


        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_twitter_bird);
        getSupportActionBar().setTitle("");

        TextView tvTweetBody = (TextView) findViewById(R.id.tvTweetBody);
        TextView tvUsername = (TextView) findViewById(R.id.tvUsername);
        TextView tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        TextView tvTimeStamp = (TextView) findViewById(R.id.tvTimeStamp);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ImageView ivReply = (ImageView) findViewById(R.id.ivReply);
        ImageView ivFavorite = (ImageView) findViewById(R.id.ivFavorite);
        ImageView ivRetweet = (ImageView) findViewById(R.id.ivRetweet);


        Tweet tweet = getIntent().getParcelableExtra("tweet");
        if (tweet != null) {
            tvTweetBody.setText(tweet.body);
            tvUsername.setText(tweet.user.name);
            tvScreenName.setText(tweet.user.screenName);
            tvTimeStamp.setText(tweet.createdAt);

            Glide.with(context)
                    .load(tweet.user.profileImageUrl)
                    .bitmapTransform(new RoundedCornersTransformation(context, 5, 0))
                    .into(ivProfileImage);

        }
        else {
        }
    }
}
