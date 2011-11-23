package com.company.tweeter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetDetailsActivity extends Activity {
	
	private TextView friendScreenname;
	private TextView tweetMessage;
	private TextView createdAt;
	
	private ImageView friendProfileImage;
	
	private CacheManager manager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_tweet_layout);
		
		manager = CacheManager.getInstance();
		
		friendScreenname = (TextView) findViewById(R.id.friendScreenName);
		tweetMessage = (TextView) findViewById(R.id.statusMessage);
		createdAt = (TextView) findViewById(R.id.statusTime);
		
		friendProfileImage = (ImageView) findViewById(R.id.friendProfileImage);
		
		setStatusInfo(getIntent());
	}

	private void setStatusInfo(Intent intent) {
		Bundle data = intent.getExtras();
		friendScreenname.setText(data.getString(Constants.USERNAME));
		tweetMessage.setText(data.getString(Constants.TWEET));
		createdAt.setText(data.getString(Constants.CREATED_TIME));
		
		Bitmap image = BitmapFactory.decodeFile(manager.getImageForKey(data.getString(Constants.USERNAME)));
		if(image != null) {
			friendProfileImage.setImageBitmap(ImageHelper.getRoundedCornerBitmap(image));
		}
		
	}
}
