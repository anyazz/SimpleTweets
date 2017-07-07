package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.fragments.ModalFragment;
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
            Log.e("TweetDetailActivity", "Unable to obtain tweet ID");

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

        } else {
            Log.e("TweetDetailActivity", "Unable to load tweet");
        }


        // UPDATE ICON COLORS
        if (tweet.favorited) {
            ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.icon_pink), PorterDuff.Mode.SRC_IN);
        } else {
            ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.icon_gray), PorterDuff.Mode.SRC_IN);

        }

        if (tweet.retweeted) {
            ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.icon_green), PorterDuff.Mode.SRC_IN);
        } else {
            ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.icon_gray), PorterDuff.Mode.SRC_IN);

        }
    }



    // IMPLEMENT ICON CLICK LISTENERS
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivReply:
                ModalFragment modalFragment = ModalFragment.newInstance(tweet.uid, "@" + tweet.user.screenName + " ");
                modalFragment.onAttach(context);
                modalFragment.openComposeModal(context);
                break;

            case R.id.ivRetweet:
                if (!tweet.retweeted) {
                    client.retweet(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(context, "retweeted", Toast.LENGTH_LONG).show();
                            ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.icon_green), PorterDuff.Mode.SRC_IN);
                            tweet.retweeted = true;

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
                    client.unretweet(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(context, "unretweeted", Toast.LENGTH_LONG).show();
                            ivRetweet.setColorFilter(ContextCompat.getColor(context, R.color.icon_gray), PorterDuff.Mode.SRC_IN);
                            tweet.retweeted = false;

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
                        Toast.makeText(context, "favorited", Toast.LENGTH_LONG).show();
                            ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.icon_pink), PorterDuff.Mode.SRC_IN);
                            tweet.favorited = true;

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
                        Toast.makeText(context, "unfavorited", Toast.LENGTH_LONG).show();
                            ivFavorite.setColorFilter(ContextCompat.getColor(context, R.color.icon_gray), PorterDuff.Mode.SRC_IN);
                            tweet.favorited = false;

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
