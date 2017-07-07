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
    Context context;
    private OnItemSelectedListener listener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        openComposeModal(getContext());

    }

    public static ModalFragment newInstance(long reply_id, String tag) {
        ModalFragment fragmentModal = new ModalFragment();
        Bundle args = new Bundle();
        args.putLong("reply_id", reply_id);
        args.putString("tag", tag);
        fragmentModal.setArguments(args);
        return fragmentModal;
    }

    public interface OnItemSelectedListener {
        // This can be any number of events to be sent to the activity
        public void updateTimeline(Tweet tweet);
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ModalFragment.OnItemSelectedListener");
        }
    }

    public void openComposeModal(Context context) {
        client = TwitterApp.getRestClient();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final long reply_id = getArguments().getLong("reply_id", -1);
        String tag = getArguments().getString("tag", "");

        boolean wrapInScrollView = false;

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
                                    if (listener != null) {
                                        listener.updateTimeline(tweet);
                                    }
                                    else {
                                        Log.d("listener", "couldn't update");
                                    }

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



}
