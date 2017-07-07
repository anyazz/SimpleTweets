package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by anyazhang on 7/3/17.
 */

public class TweetsPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] {"Home", "Mentions"};
    private Context context;
    public HomeTimelineFragment timelineFragment;
    public MentionsTimelineFragment mentionsFragment;

    public TweetsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    // return fragment to use depending on position
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            timelineFragment = getTimelineInstance();
            return timelineFragment;
        }
        else if (position == 1) {
            mentionsFragment = getMentionsInstance();
            return mentionsFragment;
        }
        else {
            return null;
        }
    }



    private HomeTimelineFragment getTimelineInstance() {
        if (timelineFragment == null) {
            timelineFragment = new HomeTimelineFragment();
        }
        return timelineFragment;
    }

    private MentionsTimelineFragment getMentionsInstance() {
        if (mentionsFragment == null) {
            mentionsFragment = new MentionsTimelineFragment();
        }
        return mentionsFragment;
    }

    // return the total # of fragments
    @Override
    public int getCount() {
        return 2;
    }

    // return title
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
