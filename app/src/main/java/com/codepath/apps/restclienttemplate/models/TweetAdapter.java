package com.codepath.apps.restclienttemplate.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
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
    boolean wrapInScrollView = false;
    private TwitterClient client = TwitterApp.getRestClient();


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
            LayoutInflater inflater = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);

            int position = getAdapterPosition();
            Tweet tweet = mTweets.get(position);
            Log.d("clicked", String.valueOf(v.getId()));

            switch (v.getId()) {

                // if reply button clicked
                case R.id.ivReply:
                    // constants for compose activity
                    final int MAX_TWEET_LENGTH = 140;

                    Log.d("clicked", "reply");

                    // inflate compose layout into view
                    final View activity_compose = inflater.inflate(R.layout.activity_compose, null);

                    // load data into view
                    EditText etTweetBody = (EditText) activity_compose.findViewById(R.id.etTweetBody);
                    String tag = "@" + tweet.user.screenName + ' ';
                    etTweetBody.setText(tag);
                    etTweetBody.setSelection(tag.length());
                    final long reply_id = tweet.uid;
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
                                            Tweet tweet = null;
                                            try {
                                                tweet = Tweet.fromJSON(response);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            Toast.makeText(context, "replied", Toast.LENGTH_LONG).show();

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

//                    // set on click listeners
//                    activity_compose.findViewById(R.id.ivReply).setOnClickListener(this);


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

                break;

               default:
                   Log.d("clicked", "tweet");

                   // get views
                   View details = inflater.inflate(R.layout.activity_tweet_detail, null);
                   TextView tvTweetBody = (TextView) details.findViewById(R.id.tvTweetBody);
                   TextView tvUsername = (TextView) details.findViewById(R.id.tvUsername);
                   TextView tvScreenName = (TextView) details.findViewById(R.id.tvScreenName);
                   TextView tvTimeStamp = (TextView) details.findViewById(R.id.tvTimeStamp);
                   ImageView ivProfileImage = (ImageView) details.findViewById(R.id.ivProfileImage);
                   ImageView ivReply = (ImageView) details.findViewById(R.id.ivReply);
                   ImageView ivFavorite = (ImageView) details.findViewById(R.id.ivFavorite);
                   ImageView ivRetweet = (ImageView) details.findViewById(R.id.ivRetweet);
                   ImageView ivFirstImage = (ImageView) details.findViewById(R.id.ivFirstImage);

                   // update details
                   tvTweetBody.setText(tweet.body);
                   tvUsername.setText(tweet.user.name);
                   tvScreenName.setText(tweet.user.screenName);
                   tvTimeStamp.setText(tweet.createdAt);

                   Glide.with(context)
                           .load(tweet.user.profileImageUrl)
                           .bitmapTransform(new RoundedCornersTransformation(context, 5, 0))
                           .into(ivProfileImage);

                   // load media if applicable
                   if (tweet.firstImageUrl.equals("")) {
                       ivFirstImage.setVisibility(View.GONE);
                   }
                   else {

                       ivFirstImage.setVisibility(View.VISIBLE);
                       Glide.with(context)
                               .load(tweet.firstImageUrl)
                               .placeholder(R.drawable.default_placeholder)
                               .dontAnimate()
                               .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                               .into(ivFirstImage);
                   }

                   // show modal
                   new MaterialDialog.Builder(context)
                           .customView(details, wrapInScrollView)
                           .positiveText("text")
                           .show();

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
