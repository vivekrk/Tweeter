package com.company.tweeter;

import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.company.tweeter.accountmanager.AccountManager;
import com.company.tweeter.accountmanager.TwitterAccount;
import com.company.tweeter.database.TweeterDbHelper;

public class TimelineActivity extends Activity {
    /** Called when the activity is first created. */
	
	private AccountManager manager;
	private TwitterAccount account;
	
	private TweeterDbHelper dbHelper;
	
	private CursorAdapter adapter;
	
	private List<Status> statuses;
	
	private ListView timelineList;
	private ImageView userImageView;
	private TextView username;
	private TextView time;
	private TextView tweetText;
	private TextView retweetedBy;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        dbHelper = new TweeterDbHelper(this);
        dbHelper.getWritableDatabase();
        
        manager = AccountManager.getInstance();
        account = manager.getAccount();
        
        if(!account.isUserLoggedIn(this)) {
        	login();
        } else {
        	setContentView(R.layout.timeline_layout);
        	initializeUI();
        	getStatuses();
        	updateTimelineUI();
        }
    }
    
	private void updateTimelineUI() {
		Cursor data = dbHelper.query(Constants.TABLE_NAME, null, null);
		adapter = new SimpleCursorAdapter(this, R.layout.tweet_row, data, 
				new String[] {Constants.CREATED_TIME, Constants.USERNAME, Constants.PROFILE_IMAGE, Constants.TWEET}, 
				new int[] {R.id.time, R.id.username, R.id.userImageView, R.id.tweetMessage});
		timelineList.setAdapter(adapter);
	}

	private void getStatuses() {
		try {
			statuses = account.getHomeTimeline();
			for (Status status : statuses) {
				dbHelper.addStatus(status);
			}
		} catch (TwitterException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private void initializeUI() {
    	timelineList = (ListView) findViewById(R.id.tweetList);
    	userImageView = (ImageView) findViewById(R.id.userImageView);
    	username = (TextView) findViewById(R.id.username);
    	time = (TextView) findViewById(R.id.time);
    	tweetText = (TextView) findViewById(R.id.tweetMessage);
    	retweetedBy = (TextView) findViewById(R.id.retweetedBy);
    }
    
    private void login() {
    	WebView webView = new WebView(this);
    	webView.getSettings().setJavaScriptEnabled(true);
    	webView.setWebViewClient(new WebViewClient() {
    		@Override
    		public void onPageFinished(WebView view, String url) {
    			super.onPageFinished(view, url);
    			if(url.contains(Constants.CALLBACK_URL)) {
    				Uri uri = Uri.parse(url);
    				Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
//    				Log.d(Constants.TAG, url);
    				
    				String oAuthVerifier = uri.getQueryParameter(Constants.OAUTH_VERIFIER);
    				AccessToken token;
					try {
						token = account.getAccessToken(oAuthVerifier);
						Log.d(Constants.TAG, token.getToken());
						account.setAccessToken(token);
					} catch (TwitterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				
    				setContentView(R.layout.timeline_layout);
    				initializeUI();
    				getStatuses();
    				updateTimelineUI();
    			}
    		}
    		
    		@Override
    		public void onReceivedError(WebView view, int errorCode,
    				String description, String failingUrl) {
    			super.onReceivedError(view, errorCode, description, failingUrl);
    			Toast.makeText(getApplicationContext(), description, Toast.LENGTH_LONG).show();
    		}
    	});
    	try {
			webView.loadUrl(account.getAuthenticationUrl());
		} catch (TwitterException e) {
			Toast.makeText(getApplicationContext(), e.getErrorMessage(), Toast.LENGTH_LONG).show();
		}
    	setContentView(webView);
    }
}