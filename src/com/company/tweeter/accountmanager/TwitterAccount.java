package com.company.tweeter.accountmanager;

import twitter4j.DirectMessage;
import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.api.DirectMessageMethods;
import twitter4j.api.StatusMethods;
import twitter4j.api.TimelineMethods;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.util.Log;

import com.company.tweeter.Constants;

public class TwitterAccount extends Account implements TimelineMethods, DirectMessageMethods, StatusMethods {
	private Twitter twitter;
	
	public static final int TIMELINE = 1;
	public static final int MENTIONS = 2;
	public static final int DIRECT_MESSAGES = 3;
	
	private RequestToken requestToken = null;
	
	public TwitterAccount() {
		twitter = getTwitterInstance();
		twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
	}
	
	private Twitter getTwitterInstance() {
		twitter = new TwitterFactory().getInstance();
		return twitter;
	}
	
	public RequestToken getRequestToken() throws TwitterException {
		requestToken = twitter.getOAuthRequestToken(Constants.CALLBACK_URL);
		return requestToken;
	}
	
	public String getAuthenticationUrl() throws TwitterException {
		RequestToken token = getRequestToken();
		return token.getAuthenticationURL();
	}
	
	public AccessToken getAccessToken(String oAuthVerifier) throws TwitterException {
		return twitter.getOAuthAccessToken(requestToken, oAuthVerifier);
	}
	
	
	public void setAccessToken(AccessToken accessToken) {
		twitter.setOAuthAccessToken(accessToken);
	}

	public ResponseList<Status> getFriendsTimeline() throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getFriendsTimeline(Paging arg0)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getHomeTimeline() throws TwitterException {
		// TODO Auto-generated method stub
		return twitter.getHomeTimeline();
	}

	public ResponseList<Status> getHomeTimeline(Paging page)
			throws TwitterException {
		return twitter.getHomeTimeline(page);
	}

	public ResponseList<Status> getMentions() throws TwitterException {
		return twitter.getMentions();
	}

	public ResponseList<Status> getMentions(Paging arg0)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getPublicTimeline() throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getRetweetedByMe() throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getRetweetedByMe(Paging arg0)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getRetweetedByUser(String arg0, Paging arg1)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getRetweetedByUser(long arg0, Paging arg1)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getRetweetedToMe() throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getRetweetedToMe(Paging arg0)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getRetweetedToUser(String arg0, Paging arg1)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getRetweetedToUser(long arg0, Paging arg1)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getRetweetsOfMe() throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getRetweetsOfMe(Paging arg0)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getUserTimeline() throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getUserTimeline(String arg0)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getUserTimeline(long arg0)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getUserTimeline(Paging arg0)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getUserTimeline(String arg0, Paging arg1)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getUserTimeline(long arg0, Paging arg1)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<DirectMessage> getDirectMessages()
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<DirectMessage> getDirectMessages(Paging paging)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<DirectMessage> getSentDirectMessages()
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<DirectMessage> getSentDirectMessages(Paging paging)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public DirectMessage sendDirectMessage(String screenName, String text)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public DirectMessage sendDirectMessage(long userId, String text)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public DirectMessage destroyDirectMessage(long id) throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public DirectMessage showDirectMessage(long id) throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public Status showStatus(long id) throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public Status updateStatus(String status) throws TwitterException {
		return twitter.updateStatus(status);
	}

	public Status updateStatus(StatusUpdate latestStatus)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public Status destroyStatus(long statusId) throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public Status retweetStatus(long statusId) throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<Status> getRetweets(long statusId)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<User> getRetweetedBy(long statusId)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseList<User> getRetweetedBy(long statusId, Paging paging)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDs getRetweetedByIDs(long statusId) throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDs getRetweetedByIDs(long statusId, Paging paging)
			throws TwitterException {
		// TODO Auto-generated method stub
		return null;
	}
}
