package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity implements View.OnClickListener {
    Context context = this;
    private TwitterClient client;
    private Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        client = TwitterApp.getRestClient();


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
        ImageView ivFirstImage = (ImageView) findViewById(R.id.ivFirstImage);

        // Set on click listeners
        ivReply.setOnClickListener(this);
        ivFavorite.setOnClickListener(this);
        ivRetweet.setOnClickListener(this);

        tweet = getIntent().getParcelableExtra("tweet");
        if (tweet != null) {
            tvTweetBody.setText(tweet.body);
            tvUsername.setText(tweet.user.name);
            tvScreenName.setText(tweet.user.screenName);
            tvTimeStamp.setText(tweet.createdAt);

            Glide.with(context)
                    .load(tweet.user.profileImageUrl)
                    .bitmapTransform(new RoundedCornersTransformation(context, 5, 0))
                    .into(ivProfileImage);
            if (tweet.firstImageUrl.equals("")) {
                ivFirstImage.setVisibility(View.GONE);
            }
            else {

                ivFirstImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(tweet.firstImageUrl)
                        .placeholder(R.drawable.default_placeholder)
                        .dontAnimate()
                        .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                        .into(ivFirstImage);
            }
        }
        else {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivReply:
                Log.d("clicked", "reply");
                Intent i = new Intent(context, ComposeActivity.class);
                i.putExtra("replyTweet", tweet);
//                ivReply.setColorFilter(ContextCompat.getColor(context, Color.rgb(29,161,242)));
                context.startActivity(i); // brings up the second activity
                break;

            case R.id.ivRetweet:
                Log.d("clicked", "retweet");
                client.retweet(tweet.uid, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Toast.makeText(context, "retweeted", Toast.LENGTH_LONG).show();
                        Log.d("retweet", "success");

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Log.d("TwitterClient", response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("TwitterClient", responseString);
                        throwable.printStackTrace();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("TwitterClient", errorResponse.toString());
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Log.d("TwitterClient", errorResponse.toString());
                        throwable.printStackTrace();
                    }
                });
                break;

            case R.id.ivFavorite:
                client.favorite(tweet.uid, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Toast.makeText(context, "favorited", Toast.LENGTH_LONG).show();
                        Log.d("favorite", "success");

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Log.d("TwitterClient", response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("TwitterClient", responseString);
                        throwable.printStackTrace();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d("TwitterClient", errorResponse.toString());
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Log.d("TwitterClient", errorResponse.toString());
                        throwable.printStackTrace();
                    }
                });
                break;


        }

    }
}
