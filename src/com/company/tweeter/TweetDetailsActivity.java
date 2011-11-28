package com.company.tweeter;

import twitter4j.TwitterException;
import twitter4j.User;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.company.tweeter.accountmanager.Account;
import com.company.tweeter.accountmanager.AccountManager;
import com.company.tweeter.accountmanager.TwitterAccount;

public class TweetDetailsActivity extends Activity implements OnClickListener {
	
	private TextView friendScreenname;
	private TextView tweetMessage;
	private TextView createdAt;
	
	private ImageView friendProfileImage;
	
	private ImageButton reply;
	private ImageButton retweet;
	
	private AccountManager accountManager;
	private Account account;
	
	private CacheManager manager;
	
	private long statusID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_tweet_layout);
		
		manager = CacheManager.getInstance();
		
		accountManager = AccountManager.getInstance();
		account = accountManager.getAccount();
		
		friendScreenname = (TextView) findViewById(R.id.friendScreenName);
		tweetMessage = (TextView) findViewById(R.id.statusMessage);
		createdAt = (TextView) findViewById(R.id.statusTime);
		
		friendProfileImage = (ImageView) findViewById(R.id.friendProfileImage);
		
		reply = (ImageButton) findViewById(R.id.reply);
		reply.setOnClickListener(this);
		
		retweet = (ImageButton) findViewById(R.id.retweet);
		retweet.setOnClickListener(this);
		
		setStatusInfo(getIntent());
	}
	//Set the Status related details
	private void setStatusInfo(Intent intent) {
		Bundle data = intent.getExtras();
		
		statusID = Long.parseLong(data.getString(Constants.STATUS_ID));
		
		Toast.makeText(getApplicationContext(), Long.toString(statusID), Toast.LENGTH_LONG).show();
		
		friendScreenname.setText(data.getString(Constants.USERNAME));
		tweetMessage.setText(data.getString(Constants.TWEET));
		createdAt.setText(data.getString(Constants.CREATED_TIME));
		
		Bitmap image = BitmapFactory.decodeFile(manager.getImageForKey(data.getString(Constants.USERNAME)));
		if(image != null) {
			friendProfileImage.setImageBitmap(ImageHelper.getRoundedCornerBitmap(image));
		}
		
	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reply:
			Toast.makeText(getApplicationContext(), "Reply clicked", Toast.LENGTH_LONG).show();
			String replyToUser = null;
			Intent intent = new Intent(this, NewTweetActivity.class);
			replyToUser = (String) friendScreenname.getText();
			
			intent.putExtra(Constants.USERNAME, replyToUser);
			startActivity(intent);
			break;
			
		case R.id.retweet:
			if(account instanceof TwitterAccount) {
				new RetweetStatus().execute(statusID);
			}
			break;

		default:
			break;
		}
	}
	
	
	class RetweetStatus extends AsyncTask<Long, Integer, Void> {

		@Override
		protected Void doInBackground(Long... params) {
			try {
				if(account instanceof TwitterAccount) {
					((TwitterAccount)account).retweetStatus(params[0]);
				}
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
	}
}
