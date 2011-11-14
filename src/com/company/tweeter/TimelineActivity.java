package com.company.tweeter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.company.tweeter.accountmanager.Account;
import com.company.tweeter.accountmanager.AccountManager;
import com.company.tweeter.accountmanager.TwitterAccount;
import com.company.tweeter.database.TweeterDbHelper;

public class TimelineActivity extends Activity {
    /** Called when the activity is first created. */
	
	private AccountManager manager;
	private Account account;
	
	private TweeterDbHelper dbHelper;
	
	private SimpleCursorAdapter adapter;
	
	private List<Status> statuses;
	
	private ListView timelineList;
	private ImageView userImageView;
//	private TextView username;
//	private TextView time;
//	private TextView tweetText;
//	private TextView retweetedBy;
	
//	private ImageButton showTweets;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        dbHelper = new TweeterDbHelper(this);
        dbHelper.getWritableDatabase();
        
        manager = AccountManager.getInstance();
        account = manager.getAccount();
        Log.d(Constants.TAG, "Return value: " + account.isUserLoggedIn(this));
        if(account.isUserLoggedIn(this)) {
        	Log.d(Constants.TAG, "user is logged in");
        	
        	AccessToken token = account.getTokenFromPreferences();
        	account.setAccessToken(token);
        	
        	setContentView(R.layout.timeline_layout);
        	initializeUI();
//        	showTweets.setOnClickListener(this);
        	getStatuses();
        	updateTimelineUI();
        } else {
        	login();
        	Log.d(Constants.TAG, "user is not logged in");
        }
    }
    
    /**
     * Saves the bitmap image got from the input stream in the specified path.
     * 
     * @param is
     * Input stream of the image.
     * 
     * @param path
     * Path where the image file is to be stored.
     * @return
     * Bitmap image
     */
    
    private Bitmap saveImageFile(InputStream is, String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
				FileOutputStream fo = new FileOutputStream(file);
				byte[] buffer = new byte[1000];
				int n = is.read(buffer, 0, 1000);
				int size = n;
				while (n > 0) {
					fo.write(buffer, 0, n);
					n = is.read(buffer, 0, 1000);
					size += n;
				}
				Log.d("Downloading..", "total size: " + size);
			}
			Bitmap bmp = BitmapFactory.decodeFile(path);
			return bmp;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
    
    /**
     * AsyncTask calss that downloads the profile images.
     * @author vivek
     *
     */
    
    class ImageDownloader extends AsyncTask<String, Integer, Bitmap> {

//    	private String imageUrlString = null;

    	/**
    	 * Sets the image url string of the image to be downloaded.
    	 * @param imageUrlString
    	 */

//		public void setImageUrlString(String imageUrlString) {
//			this.imageUrlString = imageUrlString;
//		}

		/**
		 * Sets the file path where the image needs to be saved.
		 * @param filePath
		 */

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		/**
		 * Sets the image view returned from the ViewBinder
		 * @param imageView
		 */

		public void setImageView(View imageView) {
			this.imageView = imageView;
		}

		

		private String filePath = null;
    	private View imageView = null;
    	
    	
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bmp = null;
			URL imageUrl = null;
			
			try {
				for (int i = 0; i < params.length; i++) {
					imageUrl = new URL(params[i]);
				}
				HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
				connection.connect();
				
				InputStream is = connection.getInputStream();
				bmp = saveImageFile(is, filePath);
				return bmp;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			return null;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			if (imageView instanceof ImageView) {
				((ImageView) imageView).setImageBitmap(result);
			}
			super.onPostExecute(result);
		}
    	
    }
    
    /**
     * Updates the status messages in the ListView. 
     * 
     * The cursor data is set to the SimpleCursorAdapter and all the text field data is populated.
     * ViewBinder object handles the setting of the image to the ImageView.
     */
    
	private void updateTimelineUI() {
		Cursor data = dbHelper.query(Constants.TABLE_NAME, null, null);
		
		if (data.moveToFirst()) {
			adapter = new SimpleCursorAdapter(this, R.layout.tweet_row, data, 
					new String[] {Constants.CREATED_TIME, Constants.USERNAME, Constants.PROFILE_IMAGE, Constants.TWEET}, 
					new int[] {R.id.time, R.id.username, R.id.userImageView, R.id.tweetMessage});
			
			SimpleCursorAdapter.ViewBinder viewBinder = new SimpleCursorAdapter.ViewBinder() {
				public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
					if(view != null && view.getId() != R.id.userImageView) {
						return false;
					}
					String imageUrlString = cursor.getString(columnIndex);
					String username = cursor.getString(cursor.getColumnIndex(Constants.USERNAME));
					String path = getDir("images", MODE_PRIVATE).getAbsolutePath() + "/" + username + ".png";
					
					ImageDownloader downloader = new ImageDownloader();
//					downloader.setImageUrlString(imageUrlString);
					downloader.setImageView(view);
					downloader.setFilePath(path);

					downloader.execute(imageUrlString);

					return true;
				}
			};
			
			int index = data.getColumnIndex(Constants.PROFILE_IMAGE);
//			Log.d(Constants.TAG, "" + index);
			
			adapter.setViewBinder(viewBinder);
			
			viewBinder.setViewValue(userImageView, data, index);
			
			timelineList.setAdapter(adapter);
		}
	}

	/**
	 * Fetches the status data from the users home timeline and stores it in the database.
	 */
	
	private void getStatuses() {
		try {
			if(account instanceof TwitterAccount) {
				statuses = ((TwitterAccount) account).getHomeTimeline();
			}
			
			for (Status status : statuses) {
				dbHelper.addStatus(status);
			}
		} catch (TwitterException e) {
			Log.d(Constants.TAG, e.getErrorMessage());
		}
	}

	/**
	 * Initializes all the UI elements.
	 */
	
	private void initializeUI() {
    	timelineList = (ListView) findViewById(R.id.tweetList);
    	userImageView = (ImageView) findViewById(R.id.userImageView);
//    	username = (TextView) findViewById(R.id.username);
//    	time = (TextView) findViewById(R.id.time);
//    	tweetText = (TextView) findViewById(R.id.tweetMessage);
//    	retweetedBy = (TextView) findViewById(R.id.retweetedBy);
    	
//    	showTweets = (ImageButton) findViewById(R.id.showTweets);
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
    				getStatuses();
    				updateTimelineUI();
    			}
    		}
    		
    		@Override
    		public void onReceivedError(WebView view, int errorCode,
    				String description, String failingUrl) {
    			super.onReceivedError(view, errorCode, description, failingUrl);
//    			Toast.makeText(getApplicationContext(), description, Toast.LENGTH_LONG).show();
    			Log.d(Constants.TAG, "onReceivedError: " + description);
    		}
    	});

    	webView.loadUrl(account.getAuthenticationUrl());
    	setContentView(webView);
    }
}