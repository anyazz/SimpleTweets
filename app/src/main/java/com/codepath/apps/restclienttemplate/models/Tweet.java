package com.codepath.apps.restclienttemplate.models;

import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by anyazhang on 6/26/17.
 */

@Parcel
public class Tweet implements Parcelable {
    // list out attributes
    public String body;
    public long uid;  // database ID for tweet
    public User user;
    public String createdAt;

    private Tweet(android.os.Parcel in) {
        body = in.readString();
        uid = in.readLong();
        createdAt = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(android.os.Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };

    public Tweet() {

    }

    // deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // extract values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        return tweet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(body);
        dest.writeLong(uid);
        dest.writeString(createdAt);
        dest.writeParcelable(user, flags);

    }

}
