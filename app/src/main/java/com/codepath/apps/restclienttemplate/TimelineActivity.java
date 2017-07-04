package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codepath.apps.restclienttemplate.fragments.TweetsPagerAdapter;


public class TimelineActivity extends AppCompatActivity {

    // TwitterClient client;

    private EndlessRecyclerViewScrollListener scrollListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // get view pager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);

        // set the adapter for the pager
        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager(), this));

        // setup the TabLayout to use the view pager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_twitter_bird);



//        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                // Triggered only when new data needs to be appended to the list
//                // Add whatever code is needed to append new items to the bottom of the list
//                loadNextDataFromApi(page);
//            }

//        };
        // Adds the scroll listener to RecyclerView
//        rvTweets.addOnScrollListener(scrollListener);




    }

    // Append the next page of data into the adapter
//    public void loadNextDataFromApi(int offset) {
//        Log.d("loadnext", "api");
////        long last_tweet_id = tweets.get(tweets.size() - 1).uid;
//        client.extendTimeline(last_tweet_id, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.d("TwitterClient", response.toString());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//
//                fragmentTweetsList.addItems(response);
////                // iterate through JSON array
////                // for each entry, deserialize the JSON object
////                for (int i = 0; i < response.length(); i++) {
////                    // convert each object to a Tweet model
////                    // add that Tweet model to our data source
////                    // notify the adapter that we've added an item
////                    Tweet tweet;
////                    try {
////                        tweet = Tweet.fromJSON(response.getJSONObject(i));
////                        tweets.add(tweet);
////                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////
////                }
////                swipeContainer.setRefreshing(false);
//                scrollListener.resetState();
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Log.d("TwitterClient", responseString);
//                throwable.printStackTrace();
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.d("TwitterClient", errorResponse.toString());
//                throwable.printStackTrace();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                Log.d("TwitterClient", errorResponse.toString());
//                throwable.printStackTrace();
//            }
//        });
//
//        // Send an API request to retrieve appropriate paginated data
//        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
//        //  --> Deserialize and construct new model objects from the API response
//        //  --> Append the new data objects to the existing set of items inside the array of items
//        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miCompose:
//                tweetAdapter.openComposeModal(-1, "", this);

                composeMessage();
                return true;
            case R.id.miProfile:
                Intent i = new Intent(this, ProfileActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//
//    public void updateTimeline(Tweet tweet) {
//        tweets.add(0, tweet);
//        tweetAdapter.notifyItemInserted(0);
//        rvTweets.scrollToPosition(0);
//    }

    private void composeMessage() {
        final Context context = this;
        boolean wrapInScrollView = false;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        final View activity_compose = inflater.inflate(R.layout.activity_compose, null);
        final long reply_id = -1;

        new MaterialDialog.Builder(context)
                .title("Compose Tweet")
                .customView(activity_compose, wrapInScrollView)
                .positiveText("TWEET")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        EditText etTweet = (EditText) activity_compose.findViewById(R.id.etTweetBody);
                        String tweetText = etTweet.getText().toString();
                        Log.d("onSubmit", tweetText);
                        Log.d("onSubmit", "#" + String.valueOf(reply_id));
//                        client.sendTweet(tweetText, reply_id, new JsonHttpResponseHandler() {
//                            @Override
//                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                                Log.d("Success", String.valueOf(reply_id));
//                                Tweet tweet = null;
//                                try {
//                                    tweet = Tweet.fromJSON(response);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
////
////                                tweets.add(0, tweet);
////                                tweetAdapter.notifyItemInserted(0);
////                                rvTweets.scrollToPosition(0);
//                            }
//
//                            @Override
//                            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                                Log.d("TwitterClient", response.toString());
//                            }
//
//                            @Override
//                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                                Log.d("TwitterClient", responseString);
//                                throwable.printStackTrace();
//
//                            }
//
//                            @Override
//                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                                Log.d("TwitterClient", errorResponse.toString());
//                                throwable.printStackTrace();
//                            }
//
//                            @Override
//                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                                Log.d("TwitterClient", errorResponse.toString());
//                                throwable.printStackTrace();
//                            }
//                        });
                    }
                })
                .show();
    }


}
