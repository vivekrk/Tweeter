package com.company.tweeter;

import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.company.tweeter.accountmanager.Account;
import com.company.tweeter.accountmanager.AccountManager;
import com.company.tweeter.accountmanager.TwitterAccount;
import com.company.tweeter.database.TweeterDbHelper;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends Activity implements OnScrollListener, OnClickListener {
	/** Called when the activity is first created. */

	private AccountManager manager;
	private Account account;
	
	private TweeterDbHelper dbHelper;
	
	private SimpleCursorAdapter adapter;
	
	private Cursor data;
	
	private ListView timelineList;
//	private ImageView userImageView;
//	private TextView username;
//	private TextView time;
//	private TextView tweetText;
//	private TextView retweetedBy;
	
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
        	new GetTimelineStatus().execute();
        } else {
        	login();
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
//    	userImageView = (ImageView) findViewById(R.id.userImageView);
//    	username = (TextView) findViewById(R.id.username);
//    	time = (TextView) findViewById(R.id.time);
//    	tweetText = (TextView) findViewById(R.id.tweetMessage);
//    	retweetedBy = (TextView) findViewById(R.id.retweetedBy);
    	
    	newTweet = (ImageButton) findViewById(R.id.newStatus);
    	newTweet.setOnClickListener(this);
    	
    	showTweets = (ImageButton) findViewById(R.id.showTweets);
    	showTweets.setOnClickListener(this);
    	
    	showMentions = (ImageButton) findViewById(R.id.showMentions);
    	showMentions.setOnClickListener(this);
    	
    	timelineList.setOnScrollListener(this);
    	((PullToRefreshListView) timelineList).setOnRefreshListener(new OnRefreshListener() {
			
			public void onRefresh() {
				// TODO Auto-generated method stub
				new GetTimelineStatus().execute();
			}
		});
    	
    }
	
	class GetTimelineStatus extends AsyncTask<Void, Integer, List<Status>> {

		@Override
		protected List<twitter4j.Status> doInBackground(Void... params) {
			List<twitter4j.Status> newStatuses = null;
			
			if(account instanceof TwitterAccount) {
				try {
					newStatuses = ((TwitterAccount) account).getHomeTimeline();
					for (twitter4j.Status status : newStatuses) {
						dbHelper.addStatus(status, TwitterAccount.TIMELINE);
					}
				} catch (TwitterException e) {
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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
				updateTimelineUI(TwitterAccount.TIMELINE);
			}
			
			Log.d(Constants.TAG, "Fetching new data");
			
			((PullToRefreshListView) timelineList).onRefreshComplete();
			super.onPostExecute(result);
		}
		
	}
    
	class GetMentionsStatus extends AsyncTask<Void, Integer, List<Status>> {

		@Override
		protected List<twitter4j.Status> doInBackground(Void... params) {
			List<twitter4j.Status> mentions = null;
			
			if(account instanceof TwitterAccount) {
				try {
					mentions = ((TwitterAccount) account).getMentions();
					for (twitter4j.Status status : mentions) {
						dbHelper.addStatus(status, TwitterAccount.MENTIONS);
					}
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
			
			return mentions;
		}
		
		@Override
		protected void onPostExecute(List<twitter4j.Status> result) {
			if(adapter != null) {
				data.requery();
				adapter.notifyDataSetChanged();
			}
			else {
				updateTimelineUI(TwitterAccount.MENTIONS);
			}
			
			Log.d(Constants.TAG, "Fetching new data");
			
			((PullToRefreshListView) timelineList).onRefreshComplete();
			super.onPostExecute(result);
		}
	}
	
	/**
	 * Check if the user is logged in by checking if there is a preference key related to the 
	 * AccessToken. If not, then a WebView is displayed and authentication is completed and AccessToken is set.
	 * The AccessToken is then written to the Preferences.
	 */
	
    private void login() {
    	WebView webView = new WebView(this);
    	webView.getSettings().setJavaScriptEnabled(true);
    	webView.setWebViewClient(new WebViewClient() {
    		@Override
    		public void onPageFinished(WebView view, String url) {
    			super.onPageFinished(view, url);
    			if(url.contains(Constants.CALLBACK_URL)) {
    				Uri uri = Uri.parse(url);
//    				Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
    				Log.d(Constants.TAG, "onPageFinished: " + url);
    				
    				String oAuthVerifier = uri.getQueryParameter(Constants.OAUTH_VERIFIER);
    				AccessToken token;
    				token = account.getAccessToken(oAuthVerifier);
    				Log.d(Constants.TAG, token.getToken());
    				account.setAccessToken(token);
    				account.writeTokenToPrefs(token);
    				
    				setContentView(R.layout.timeline_layout);
    				initializeUI();
    				
    				new GetTimelineStatus().execute();
    				
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
    	data.close();
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
			updateTimelineUI(TwitterAccount.TIMELINE);
			new GetTimelineStatus().execute();
			break;
			
		case R.id.showMentions:
			updateTimelineUI(TwitterAccount.MENTIONS);
			new GetMentionsStatus().execute();
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
			}
			if(resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(), "RESULT_CANCELED",
						Toast.LENGTH_LONG).show();
			}
		}
	}
    
}