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
import android.util.Log;

import com.company.tweeter.Constants;

public class TwitterAccount extends Account {
	private Twitter twitter;
	private SharedPreferences mPrefs;
	
	private RequestToken requestToken = null;
	
	public TwitterAccount() {
		twitter = getTwitterInstance();
		twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
	}
	
	public Twitter getTwitterInstance() {
		twitter = new TwitterFactory().getInstance();
		return twitter;
	}
	
	public RequestToken getRequestToken() throws TwitterException {
		Log.d(Constants.TAG, "Before getOAuthRequestToken");
		requestToken = twitter.getOAuthRequestToken(Constants.CALLBACK_URL);
		Log.d(Constants.TAG, requestToken.getToken());
		return requestToken;
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
		if(mPrefs.getString(Constants.ACCESS_TOKEN, null) != null) {
			Log.d(Constants.TAG, Constants.ACCESS_TOKEN + " found");
			return true;
		}
		else {
			return false;
		}
	}
	
	public AccessToken getAccessToken(String oAuthVerifier) throws TwitterException {
		Log.d(Constants.TAG, "getAccessToken start");
		Log.d(Constants.TAG, requestToken.getToken());
		AccessToken aToken = null;
		aToken = twitter.getOAuthAccessToken(requestToken, oAuthVerifier);
		Log.d(Constants.TAG, "after getOAuthAccessToken");
		return aToken;
	}
	
	public void writeTokenToPrefs(AccessToken accessToken) {
		SharedPreferences.Editor editor = mPrefs.edit();
		String token = accessToken.getToken();
		String tokenSecret = accessToken.getTokenSecret();
		editor.putString(Constants.ACCESS_TOKEN, token);
		editor.putString(Constants.ACCESS_TOKEN_SECRET, tokenSecret);
		editor.commit();
		
		Log.d(Constants.TAG, mPrefs.getString(Constants.ACCESS_TOKEN, null));
	}
	
	public void setAccessToken(AccessToken accessToken) {
		Log.d(Constants.TAG, "setAccessToken called");
		twitter.setOAuthAccessToken(accessToken);
	}

	public AccessToken getTokenFromPreferences() {
		Log.d(Constants.TAG, "getTokenFromPreferences called");
		String token = mPrefs.getString(Constants.ACCESS_TOKEN, null);
		String tokenSecret = mPrefs.getString(Constants.ACCESS_TOKEN_SECRET, null);
		AccessToken accessToken = null;
		accessToken = new AccessToken(token, tokenSecret);
		return accessToken;
	}
}
