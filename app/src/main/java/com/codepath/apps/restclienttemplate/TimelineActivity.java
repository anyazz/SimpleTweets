package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.fragments.ModalFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsPagerAdapter;


public class TimelineActivity extends AppCompatActivity {





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






    }



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
                ModalFragment modalFragment = ModalFragment.newInstance(-1, "");

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                // make change
                ft.replace(R.id.flContainer, modalFragment);

                // commit
                ft.commit();

                return true;
            case R.id.miProfile:
                Intent i = new Intent(this, ProfileActivity.class);
                i.putExtra("origin", "menu");
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




}
