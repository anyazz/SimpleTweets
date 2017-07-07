package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by anyazhang on 7/3/17.
 */

public class HomeTimelineFragment extends TweetsListFragment {
    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
        populateTimeline();


    }

    public static HomeTimelineFragment newInstance(Tweet tweet) {
        HomeTimelineFragment fragmentTimeline = new HomeTimelineFragment();
        Bundle args = new Bundle();
        args.putParcelable("tweet", tweet);
        fragmentTimeline.setArguments(args);
//        fragmentTimeline.tweets.add(tweet);
//        fragmentTimeline.addItems(getTweets());
        return fragmentTimeline;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Lookup the swipe container view
//        swipeContainer = (SwipeRefreshLayout) view.findViewById(swipeContainer);

        // Setup refresh listener which triggers new data loading
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Log.d("refreshing", "1");
//
//                // Refresh the list here.
//                populateTimeline();
//                // Make sure you call swipeContainer.setRefreshing(false)
//                // once the network request has completed successfully.
//            }
//        });
//        // Configure the refreshing colors
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
    }

    @Override
    public void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                tweetAdapter.clear();
                addItems(response);
                HomeTimelineFragment.super.swipeContainer.setRefreshing(false);
                HomeTimelineFragment.super.scrollListener.resetState();


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
