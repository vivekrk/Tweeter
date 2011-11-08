package com.company.tweeter.accountmanager;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import android.content.Context;
import android.content.SharedPreferences;

import com.company.tweeter.Constants;

public class TwitterAccount extends Account {
	private Twitter twitter;
	private SharedPreferences mPrefs;
	
	public TwitterAccount() {
		twitter = getTwitterInstance();
		twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
	}
	
	public Twitter getTwitterInstance() {
		twitter = new TwitterFactory().getInstance();
		return twitter;
	}
	
	public RequestToken getRequestToken() throws TwitterException {
		RequestToken token = twitter.getOAuthRequestToken(Constants.CALLBACK_URL);
		return token;
	}
	
	public String getAuthenticationUrl() throws TwitterException {
		RequestToken token = getRequestToken();
		return token.getAuthenticationURL();
	}
	
	public List<Status> getHomeTimeline() throws TwitterException {
		List<Status> statuses = twitter.getHomeTimeline();
		return statuses;
	}
	
	public boolean isUserLoggedIn(Context context) {
		mPrefs = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		if(mPrefs.contains(Constants.ACCESS_TOKEN)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void writeTokenToPrefs(String accessToken, String accessTokenSecret) {
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putString(Constants.ACCESS_TOKEN, accessToken);
		editor.putString(Constants.ACCESS_TOKEN_SECRET, accessTokenSecret);
		editor.commit();
	}
	
	public void setAccessToken(AccessToken accessToken) {
		twitter.setOAuthAccessToken(accessToken);
	}
}
