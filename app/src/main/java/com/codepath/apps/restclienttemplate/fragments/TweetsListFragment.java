package com.codepath.apps.restclienttemplate.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by anyazhang on 7/3/17.
 */

public class TweetsListFragment extends Fragment {
    private TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;

    // inflation happens inside onCreateView

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        client = TwitterApp.getRestClient();

        // inflate layout
        View v = inflater.inflate(R.layout.fragments_tweets_list, container, false);
        // find the RecyclerView
        rvTweets = (RecyclerView) v.findViewById(R.id.rvTweet);
        // init the arraylist (datasource)
        tweets = new ArrayList<>();
        // construct the adapter from this datasource
        tweetAdapter = new TweetAdapter(tweets);
        // RecyclerView setup (layout manager, use adapter)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        // set the adapter
        rvTweets.setAdapter(tweetAdapter);

//         add delimiters between tweets
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTweets.getContext(), Configuration.ORIENTATION_PORTRAIT);
        rvTweets.addItemDecoration(dividerItemDecoration);

//         Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

//         Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("refreshing", "1");

                // Refresh the list here.
                populateTimeline();
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);

//        TextView tvBody = (TextView) v.findViewById(R.id.tvBody);
//        // Style clickable spans based on pattern
//        new PatternEditableBuilder().
//            addPattern(Pattern.compile("\\@(\\w+)"), Color.BLUE,
//                new PatternEditableBuilder.SpannableClickedListener() {
//                @Override
//                public void onSpanClicked(String text) {
//                    //
//                }
//            }).into(tvBody);

        return v;
    }

    public void addItems(JSONArray response) {
        // iterate through JSON array
        // for each entry, deserialize the JSON object
        Log.d("addItems", String.valueOf(response.length()));
        for (int i = 0; i < response.length(); i++) {
            // convert each object to a Tweet model
            // add that Tweet model to our data source
            // notify the adapter that we've added an item
            Tweet tweet;
            try {
                tweet = Tweet.fromJSON(response.getJSONObject(i));
                tweets.add(tweet);
                tweetAdapter.notifyItemInserted(tweets.size() - 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void addTweet(Tweet tweet) {
        tweets.add(0, tweet);
        tweetAdapter.notifyItemInserted(0);
        rvTweets.scrollToPosition(0);
    }

    public void populateTimeline() {
    }

    // Append the next page of data into the adapter
    public void loadNextDataFromApi(int offset) {

    }



    public void onExtendTimeline(String timelineType) {
        long last_tweet_id = tweets.get(tweets.size() - 1).uid;
        client.extendTimeline(timelineType, last_tweet_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                addItems(response);
                swipeContainer.setRefreshing(false);
                scrollListener.resetState();

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
