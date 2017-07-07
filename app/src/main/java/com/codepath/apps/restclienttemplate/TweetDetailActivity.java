package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity implements View.OnClickListener {
    Context context = this;
    private TwitterClient client;
    Tweet tweet;
    TextView tvTweetBody;
    TextView tvUsername;
    TextView tvScreenName;
    TextView tvTimeStamp;
    ImageView ivProfileImage;
    ImageView ivFirstImage;
    ImageView ivReply;
    ImageView ivFavorite;
    ImageView ivRetweet;

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

        tvTweetBody = (TextView) findViewById(R.id.tvTweetBody);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvTimeStamp = (TextView) findViewById(R.id.tvTimeStamp);
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivReply = (ImageView) findViewById(R.id.ivReply);
        ivFavorite = (ImageView) findViewById(R.id.ivFavorite);
        ivRetweet = (ImageView) findViewById(R.id.ivRetweet);
        ivFirstImage = (ImageView) findViewById(R.id.ivFirstImage);

        // Set on click listeners
        ivReply.setOnClickListener(this);
        ivFavorite.setOnClickListener(this);
        ivRetweet.setOnClickListener(this);

        Long tweetId = getIntent().getLongExtra("tweet_id", 0);

        // GET UPDATED TWEET INFO
        if (tweetId != 0) {
            client.getTweetInfo(tweetId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        tweet = Tweet.fromJSON(response);
                        onTweetLoad();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
        } else {
            Log.e("TweetDetailActivity", "null tweetId");

        }
    }


    private void onTweetLoad() {
        // UPDATE PAGE VIEWS
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
            } else {

                ivFirstImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(tweet.firstImageUrl)
                        .placeholder(R.drawable.default_placeholder)
                        .dontAnimate()
                        .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                        .into(ivFirstImage);
            }
            Log.d("bool", String.valueOf(tweet.favorited));
            Log.d("bool", String.valueOf(tweet.retweeted));

        } else {
            Log.d("null", "tweet");
        }


        // UPDATE ICON COLORS
        if (tweet.favorited) {
            ivFavorite.setColorFilter(Color.parseColor("#dd0f5a"), PorterDuff.Mode.SRC_IN);
        } else {
            ivFavorite.setColorFilter(Color.parseColor("#555555"), PorterDuff.Mode.SRC_IN);

        }

        Log.d("status", String.valueOf(tweet.retweeted));
        if (tweet.retweeted) {
            Log.d("status", "retweeted");
            ivRetweet.setColorFilter(Color.parseColor("#23af2c"), PorterDuff.Mode.SRC_IN);
        } else {
            Log.d("status", "unretweeted");
            ivRetweet.setColorFilter(Color.parseColor("#555555"), PorterDuff.Mode.SRC_IN);

        }
    }



    // IMPLEMENT ICON CLICK LISTENERS
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
                if (!tweet.retweeted) {
                    Log.d("clicked", "retweet");
                    client.retweet(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        Toast.makeText(context, "retweeted", Toast.LENGTH_LONG).show();
                            ivRetweet.setColorFilter(Color.parseColor("#23af2c"), PorterDuff.Mode.SRC_IN);
                            tweet.retweeted = true;
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
                } else {
                    Log.d("clicked", "retweet");
                    client.unretweet(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        Toast.makeText(context, "retweeted", Toast.LENGTH_LONG).show();
                            ivRetweet.setColorFilter(Color.parseColor("#555555"), PorterDuff.Mode.SRC_IN);
                            tweet.retweeted = false;
                            Log.d("unretweet", "success");

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
                }
                break;

            case R.id.ivFavorite:
                if (!tweet.favorited) {
                    client.favorite(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        Toast.makeText(context, "favorited", Toast.LENGTH_LONG).show();
                            ivFavorite.setColorFilter(Color.parseColor("#dd0f5a"), PorterDuff.Mode.SRC_IN);
                            tweet.favorited = true;
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
                } else {
                    client.unfavorite(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        Toast.makeText(context, "favorited", Toast.LENGTH_LONG).show();
                            ivFavorite.setColorFilter(Color.parseColor("#555555"), PorterDuff.Mode.SRC_IN);
                            tweet.favorited = false;
                            Log.d("unfavorite", "success");

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
}
