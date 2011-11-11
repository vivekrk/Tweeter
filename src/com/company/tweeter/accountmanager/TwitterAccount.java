package com.company.tweeter.accountmanager;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.util.Log;

import com.company.tweeter.Constants;

public class TwitterAccount extends Account {
	private Twitter twitter;
	
	
	private RequestToken requestToken = null;
	
	public TwitterAccount() {
		twitter = getTwitterInstance();
		twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
	}
	
	public Twitter getTwitterInstance() {
		twitter = new TwitterFactory().getInstance();
		return twitter;
	}
	
	public RequestToken getRequestToken() {
		Log.d(Constants.TAG, "Before getOAuthRequestToken");
		try {
			requestToken = twitter.getOAuthRequestToken(Constants.CALLBACK_URL);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		Log.d(Constants.TAG, requestToken.getToken());
		return requestToken;
	}
	
	public String getAuthenticationUrl() {
		RequestToken token = getRequestToken();
		return token.getAuthenticationURL();
	}
	
	public List<Status> getHomeTimeline() throws TwitterException {
		List<Status> statuses = twitter.getHomeTimeline();
		return statuses;
	}
	
	public AccessToken getAccessToken(String oAuthVerifier) {
		Log.d(Constants.TAG, "getAccessToken start");
		Log.d(Constants.TAG, requestToken.getToken());
		AccessToken aToken = null;
		try {
			aToken = twitter.getOAuthAccessToken(requestToken, oAuthVerifier);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(Constants.TAG, "after getOAuthAccessToken");
		return aToken;
	}
	
	
	public void setAccessToken(AccessToken accessToken) {
		Log.d(Constants.TAG, "setAccessToken called");
		twitter.setOAuthAccessToken(accessToken);
	}
}
