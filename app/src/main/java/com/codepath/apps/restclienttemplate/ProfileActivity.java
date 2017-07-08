package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileActivity extends AppCompatActivity {

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String screenName = getIntent().getStringExtra("screen_name");
        String origin = getIntent().getStringExtra("origin");
        Log.d("origin=", origin);

        // create the user fragment
        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);

        // display the user timeline fragment inside the container dynamically

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        // make change
        ft.replace(R.id.flContainer, userTimelineFragment);

        // commit
        ft.commit();

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_twitter_bird);


        client = TwitterApp.getRestClient();
        if (origin.equals("menu")) {
            Log.d("origin", "menu");
            client.getProfileInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // deserialize the User obj
                    User user = null;
                    try {
                        user = User.fromJSON(response);
                        // set title of action bar based on user info
                        getSupportActionBar().setTitle(user.screenName);

                        // populate user headline
                        populateUserHeadline(user);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
        else if (origin.equals("tweet")) {
            Log.d("origin", "tweet");
            client.getUserInfo(screenName, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // deserialize the User obj
                    User user = null;
                    try {
                        user = User.fromJSON(response);
                        // set title of action bar based on user info
                        getSupportActionBar().setTitle(user.screenName);

                        // populate user headline
                        populateUserHeadline(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
        else {
            Log.e("origin", "NOT FOUND");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    public void populateUserHeadline(User user) {
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);

        tvName.setText(user.name);

        // load profile image with Glide
        Glide.with(this)
                .load(user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(this, 5, 0))
                .into(ivProfileImage);

        tvTagline.setText(user.tagLine);
        tvFollowers.setText(user.followersCount + " Followers");
        tvFollowing.setText(user.followingCount + " Following");
    }
}
