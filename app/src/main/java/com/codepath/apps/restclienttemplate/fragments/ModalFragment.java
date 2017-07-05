package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by anyazhang on 7/5/17.
 */

public class ModalFragment extends Fragment {

    TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
        openComposeModal();

    }

    public static ModalFragment newInstance(int reply_id, String tag) {
        ModalFragment fragmentModal = new ModalFragment();
        Bundle args = new Bundle();
        args.putLong("reply_id", reply_id);
        args.putString("tag", tag);
        fragmentModal.setArguments(args);
        return fragmentModal;
    }


    public void openComposeModal() {
        final long reply_id = getArguments().getLong("reply_id", -1);
        String tag = getArguments().getString("tag", "");

        Context context = getContext();
        boolean wrapInScrollView = false;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final int MAX_TWEET_LENGTH = 140;

        Log.d("clicked", "reply");

        // inflate compose layout into view
        final View activity_compose = inflater.inflate(R.layout.activity_compose, null);

        // load data into view
        EditText etTweetBody = (EditText) activity_compose.findViewById(R.id.etTweetBody);
        etTweetBody.setText(tag);
        etTweetBody.setSelection(tag.length());
        final TextView tvCharCount = (TextView) activity_compose.findViewById(R.id.tvCharCount);
        tvCharCount.setText(String.valueOf(MAX_TWEET_LENGTH - tag.length()));


        // build modal
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
                        client.sendTweet(tweetText, reply_id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Log.d("Success", String.valueOf(reply_id));
                                try {
                                    Tweet tweet = Tweet.fromJSON(response);
//                                    if (instance != null) {
////                                            instance.updateTimeline(tweet);
//                                    }
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
                    }
                })
                .show();
    }


//    private void composeMessage() {
//        final Context context = getContext();
//        boolean wrapInScrollView = false;
//        LayoutInflater inflater = (LayoutInflater)context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
//        final View activity_compose = inflater.inflate(R.layout.activity_compose, null);
//        final long reply_id = -1;
//
//        new MaterialDialog.Builder(context)
//                .title("Compose Tweet")
//                .customView(activity_compose, wrapInScrollView)
//                .positiveText("TWEET")
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        EditText etTweet = (EditText) activity_compose.findViewById(R.id.etTweetBody);
//                        String tweetText = etTweet.getText().toString();
//                        Log.d("onSubmit", tweetText);
//                        Log.d("onSubmit", "#" + String.valueOf(reply_id));
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
//                    }
//                })
//                .show();
//    }
}
