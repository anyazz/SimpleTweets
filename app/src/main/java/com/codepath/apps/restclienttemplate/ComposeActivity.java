package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class ComposeActivity extends AppCompatActivity {
    private TwitterClient client;
    public static final int MAX_TWEET_LENGTH = 140;
    private long reply_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // initialize reply_id constant
        reply_id = -1;

        Log.d("composeactivity", "begun");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient();

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_twitter_bird);
        getSupportActionBar().setTitle("");

        final TextView tvCharCount = (TextView) findViewById(R.id.tvCharCount);
        EditText etTweetBody = (EditText) findViewById(R.id.etTweetBody);

        Tweet replyTweet;
        replyTweet = getIntent().getParcelableExtra("replyTweet");
        if (replyTweet != null) {
            Log.d("composeactivity", "reply");
            etTweetBody.setText("@" + replyTweet.user.screenName);
            reply_id = replyTweet.uid;
        }
        else {
            Log.d("composeactivity", "reply null");

        }


        // Tweet body character counter: adapted from
        // https://stackoverflow.com/questions/3013791/live-character-count-for-edittext


        final TextWatcher charCounter = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                tvCharCount.setText(String.valueOf(MAX_TWEET_LENGTH - s.length()));
            }

            public void afterTextChanged(Editable s) {
            }
        };
        etTweetBody.addTextChangedListener(charCounter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    public void onSubmit(View v) {
        EditText etTweet = (EditText) findViewById(R.id.etTweetBody);
        String tweetText = etTweet.getText().toString();
        Log.d("onSubmit", tweetText);
        client.sendTweet(tweetText, reply_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Success", String.valueOf(reply_id));
                Tweet tweet = null;
                try {
                    tweet = Tweet.fromJSON(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Prepare data intent
                Intent data = new Intent();
                // Pass relevant data back as a result
//                User user = tweet.user;
//                data.putExtra("user", user);
                data.putExtra("tweet", tweet);
//                data.putExtra("createdAt", tweet.createdAt);
                //
                // Activity finished ok, return the data
                setResult(RESULT_OK, data); // set result code and bundle data for response
                finish(); // closes the activity, pass data to parent            }
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

}
