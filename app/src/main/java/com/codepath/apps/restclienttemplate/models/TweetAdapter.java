package com.codepath.apps.restclienttemplate.models;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.ComposeActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * Created by anyazhang on 6/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    public final static int IMAGE_HEIGHT = 150;
    private List<Tweet> mTweets;
    Context context;
    // pass in Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    // for each row, inflate the layout and cache references into ViewHolder

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // convert dp to px - needed for image display
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    // bind values based on the position of the element
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // get the data according to position
        final Tweet tweet = mTweets.get(position);

        // populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvScreenName.setText("@" + tweet.user.screenName);
        holder.tvTimestamp.setText(getRelativeTimeAgo(tweet.createdAt));

        // load profile image with Glide
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 5, 0))
                .into(holder.ivProfileImage);
        if (tweet.firstImageUrl == "") {
            holder.ivFirstImage.setVisibility(View.GONE);
        }
        else {
            holder.ivFirstImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(tweet.firstImageUrl)
                    .placeholder(R.drawable.default_placeholder)
                    .dontAnimate()
                    .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                    .into(holder.ivFirstImage);
        }
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // create ViewHolder class

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvScreenName;
        public TextView tvTimestamp;
        public ImageView ivReply;
        public RelativeLayout itemTweet;
        public ImageView ivFirstImage;

        public ViewHolder (View itemView) {
            super(itemView);

            // perform findViewById lookups

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            tvTimestamp = (TextView) itemView.findViewById(R.id.tvTimestamp);
            ivReply = (ImageView) itemView.findViewById(R.id.ivReply);
            itemTweet = (RelativeLayout) itemView.findViewById(R.id.itemTweet);
            ivFirstImage = (ImageView) itemView.findViewById(R.id.ivFirstImage);

            // set on click listeners
            itemTweet.setOnClickListener(this);
            ivReply.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Tweet tweet = mTweets.get(position);
            Log.d("clicked", String.valueOf(v.getId()));
            switch (v.getId()) {
                case R.id.ivReply:
                    Log.d("clicked", "reply");
                    Intent i = new Intent(context, ComposeActivity.class);
                    i.putExtra("replyTweet", tweet);
                    //               holder.ivReply.setColorFilter(ContextCompat.getColor(context, Color.rgb(29,161,242)));
                    context.startActivity(i); // brings up the second activity
                    break;

               default:
                   Log.d("clicked", "tweet");
                   i = new Intent(context, TweetDetailActivity.class);
                   i.putExtra("tweet", tweet);
                   context.startActivity(i); // brings up the second activity
                   break;
            }
        }

    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }



    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

}
