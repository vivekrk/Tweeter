package com.company.tweeter.accountmanager;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

import com.company.tweeter.Constants;

public class TwitterAccount extends Account {
	private Twitter twitter;
	
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
	
	public List<Status> getPublicTimeline() throws TwitterException {
		List<Status> statuses = getTwitterInstance().getPublicTimeline();
		return statuses;
	}
}
