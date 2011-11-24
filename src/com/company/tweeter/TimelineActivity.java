package com.company.tweeter;

import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.company.tweeter.accountmanager.Account;
import com.company.tweeter.accountmanager.AccountManager;
import com.company.tweeter.accountmanager.TwitterAccount;
import com.company.tweeter.database.TweeterDbHelper;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends Activity implements OnScrollListener, OnClickListener, OnItemClickListener {
	/** Called when the activity is first created. */

	private AccountManager manager;
	private Account account;
	
	private TweeterDbHelper dbHelper;
	
	private SimpleCursorAdapter adapter;
	
	private int activeFeed = TwitterAccount.TIMELINE;
	
	private Cursor data;
	
	private boolean isFetchingData = false;
	
	private ListView timelineList;
	
	private ImageButton showTweets;
	private ImageButton showMentions;
	private ImageButton newTweet;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        dbHelper = new TweeterDbHelper(this);
        dbHelper.getWritableDatabase();
        
        manager = AccountManager.getInstance();
        account = manager.getAccount();
        
        if(account.isUserLoggedIn(this)) {
        	Log.d(Constants.TAG, "user is logged in");
        	
        	AccessToken token = account.getTokenFromPreferences();
        	account.setAccessToken(token);
        	
        	setContentView(R.layout.timeline_layout);
        	initializeUI();
        	
        	updateTimelineUI(TwitterAccount.TIMELINE);
        	new GetStatuses().execute();
        } else {
        	try {
				login();
			} catch (TwitterException e) {
				Log.d(Constants.TAG, "Login failed");
			}
        	Log.d(Constants.TAG, "user is not logged in");
        }
    }
    
	/**
     * Updates the status messages in the ListView. 
     * 
     * The cursor data is set to the SimpleCursorAdapter and all the text field data is populated.
     * ViewBinder object handles the setting of the image to the ImageView.
     */
    
	private void updateTimelineUI(int timelineType) {
		String selection = null;
		adapter = null;
		switch (timelineType) {
		case TwitterAccount.TIMELINE:
			selection = Constants.TIMELINE + "=" + "'" + TwitterAccount.TIMELINE + "'";
			data = dbHelper.query(Constants.TABLE_NAME, null, selection);
			break;
			
		case TwitterAccount.MENTIONS:
			selection = Constants.TIMELINE + "=" + "'" + TwitterAccount.MENTIONS + "'";
			data = dbHelper.query(Constants.TABLE_NAME, null, selection);
			break;

		default:
			break;
		}
		
		
		if (data.moveToFirst()) {
			adapter = new TimelineAdapter(this, R.layout.tweet_row, data, 
					new String[] {Constants.CREATED_TIME, Constants.USERNAME, Constants.PROFILE_IMAGE, Constants.TWEET, Constants.RETWEETED_BY}, 
					new int[] {R.id.time, R.id.username, R.id.userImageView, R.id.tweetMessage, R.id.retweetedBy});
			
			timelineList.setAdapter(adapter);
		}
	}

	/**
	 * Initializes all the UI elements.
	 */
	
	private void initializeUI() {
    	timelineList = (PullToRefreshListView) findViewById(R.id.tweetList);
    	
    	newTweet = (ImageButton) findViewById(R.id.newStatus);
    	newTweet.setOnClickListener(this);
    	
    	showTweets = (ImageButton) findViewById(R.id.showTweets);
    	showTweets.setOnClickListener(this);
    	
    	showMentions = (ImageButton) findViewById(R.id.showMentions);
    	showMentions.setOnClickListener(this);
    	
    	timelineList.setOnScrollListener(this);
    	timelineList.setOnItemClickListener(this);
    	((PullToRefreshListView) timelineList).setOnRefreshListener(new OnRefreshListener() {
			
			public void onRefresh() {
				if(ImageDownloader.isNetworkConnected(TimelineActivity.this)) {
					new GetStatuses().execute();
				}
				else {
					Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_LONG).show();
					((PullToRefreshListView) timelineList).onRefreshComplete();
				}
			}
		});
    	
    }
	
	class GetStatuses extends AsyncTask<Void, Integer, List<Status>> {

		@Override
		protected List<twitter4j.Status> doInBackground(Void... params) {
			List<twitter4j.Status> newStatuses = null;
			List<twitter4j.Status> newMentions = null;
			
			if(account instanceof TwitterAccount) {
				try {
					isFetchingData = true;
					Log.d(Constants.TAG, "Inside GetTimelineStatus AsyncTask");
					newStatuses = ((TwitterAccount) account).getHomeTimeline();
					newMentions = ((TwitterAccount) account).getMentions();
					for (twitter4j.Status status : newStatuses) {
						dbHelper.addStatus(status, TwitterAccount.TIMELINE);
					}
					
					for (twitter4j.Status status : newMentions) {
						dbHelper.addStatus(status, TwitterAccount.MENTIONS);
					}
				} catch (TwitterException e) {
					Log.d(Constants.TAG, "Twitter exception");
				}
			}
			
			return newStatuses;
		}
		
		@Override
		protected void onPostExecute(List<twitter4j.Status> result) {
			
			if(adapter != null) {
				data.requery();
				adapter.notifyDataSetChanged();
			}
			else {
				switch (activeFeed) {
				case TwitterAccount.TIMELINE:
					updateTimelineUI(TwitterAccount.TIMELINE);
					break;
					
				case TwitterAccount.MENTIONS:
					updateTimelineUI(TwitterAccount.MENTIONS);
					break;

				default:
					break;
				}
				
			}
			isFetchingData = false;
			Log.d(Constants.TAG, "Fetching new statuses");
			
			((PullToRefreshListView) timelineList).onRefreshComplete();
			super.onPostExecute(result);
		}
		
	}
    
	/**
	 * Check if the user is logged in by checking if there is a preference key related to the 
	 * AccessToken. If not, then a WebView is displayed and authentication is completed and AccessToken is set.
	 * The AccessToken is then written to the Preferences.
	 * @throws TwitterException 
	 */
	
    private void login() throws TwitterException {
    	WebView webView = new WebView(this);
    	webView.getSettings().setJavaScriptEnabled(true);
    	webView.setWebViewClient(new WebViewClient() {
    		@Override
    		public void onPageFinished(WebView view, String url) {
    			super.onPageFinished(view, url);
    			if(url.contains(Constants.CALLBACK_URL)) {
    				Uri uri = Uri.parse(url);
    				
    				String oAuthVerifier = uri.getQueryParameter(Constants.OAUTH_VERIFIER);
    				AccessToken token = null;
    				try {
						token = account.getAccessToken(oAuthVerifier);
					} catch (TwitterException e) {
						// TODO Auto-generated catch block
						Log.d(Constants.TAG, "Failed to get Access Token");
					}
    				
    				account.setAccessToken(token);
    				account.writeTokenToPrefs(token);
    				
    				setContentView(R.layout.timeline_layout);
    				initializeUI();
    				
    				new GetStatuses().execute();
    				
    			}
    		}
    		
    		@Override
    		public void onReceivedError(WebView view, int errorCode,
    				String description, String failingUrl) {
    			super.onReceivedError(view, errorCode, description, failingUrl);
    			Toast.makeText(getApplicationContext(), description, Toast.LENGTH_LONG).show();
    			Log.d(Constants.TAG, "onReceivedError: " + description);
    		}
    	});

    	webView.loadUrl(account.getAuthenticationUrl());
    	setContentView(webView);
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	if(data != null && !data.isClosed()) {
    		data.close();
    	}
    	
    	dbHelper.close();
    }

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
		if(loadMore) {
			Log.d(Constants.TAG, "Loading more tweets");
		}
		
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.showTweets:
			activeFeed = TwitterAccount.TIMELINE;
			updateTimelineUI(TwitterAccount.TIMELINE);
			if(!isFetchingData) {
				new GetStatuses().execute();
			}
			break;
			
		case R.id.showMentions:
			activeFeed = TwitterAccount.MENTIONS;
			updateTimelineUI(TwitterAccount.MENTIONS);
			if(!isFetchingData) {
				new GetStatuses().execute();
			}
			break;
			
		case R.id.newStatus:
			Intent intent = new Intent(this, NewTweetActivity.class);
			startActivityForResult(intent, Constants.NEW_TWEET);
			break;
			
		default:
			break;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.NEW_TWEET) {
			
			if (resultCode == RESULT_OK) {
				Toast.makeText(getApplicationContext(), "RESULT_OK",
						Toast.LENGTH_LONG).show();
				adapter.notifyDataSetChanged();
			}
			if(resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(), "RESULT_CANCELED",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void onItemClick(AdapterView<?> view, View v, int position, long id) {
		Intent intent = new Intent(getApplicationContext(), TweetDetailsActivity.class);
		
		Bundle extras = new Bundle();
		extras.putString(Constants.USERNAME, ((TextView) v.findViewById(R.id.username)).getText().toString());
		extras.putString(Constants.TWEET, ((TextView) v.findViewById(R.id.tweetMessage)).getText().toString());
		extras.putString(Constants.CREATED_TIME, ((TextView) v.findViewById(R.id.time)).getText().toString());
		
		intent.putExtras(extras);
		
		
		startActivity(intent);
	}
    
}