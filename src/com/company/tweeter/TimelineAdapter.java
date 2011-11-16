package com.company.tweeter;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TimelineAdapter extends SimpleCursorAdapter {

	private Activity activity;
	private Cursor data;
	
	private CacheManager cacheManager;
	
	private ArrayList<String> imageUrlList;
	
	public TimelineAdapter(Activity activity, int layout, Cursor c,
			String[] from, int[] to) {
		super(activity, layout, c, from, to);
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.data = c;
		
		cacheManager = CacheManager.getInstance();
		imageUrlList = new ArrayList<String>();
		
		Log.d(Constants.TAG, "Inside TimelineAdapter Constructor");
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = convertView;
		if(v == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			v = inflater.inflate(R.layout.tweet_row, null);
		}
		
		TextView username = (TextView) v.findViewById(R.id.username);
		TextView time = (TextView) v.findViewById(R.id.time);
		TextView tweetMessage = (TextView) v.findViewById(R.id.tweetMessage);
		TextView retweetedBy = (TextView) v.findViewById(R.id.retweetedBy);
		
		ImageView userProfileImageView = (ImageView) v.findViewById(R.id.userImageView);
		
		if(data.moveToPosition(position)) {
			String usernameString = data.getString(data.getColumnIndex(Constants.USERNAME));
			String imageUrl = data.getString(data.getColumnIndex(Constants.PROFILE_IMAGE));
			
			imageUrlList.add(imageUrl);
			
			username.setText(usernameString);
			time.setText(data.getString(data.getColumnIndex(Constants.CREATED_TIME)));
			tweetMessage.setText(data.getString(data.getColumnIndex(Constants.TWEET)));
			retweetedBy.setText(data.getString(data.getColumnIndex(Constants.RETWEETED_BY)));
			
			if(cacheManager.getImageForKey(usernameString) == null) {
				ImageDownloader downloader = new ImageDownloader();
				downloader.execute(imageUrlList);
			}
			
		}
		
		return v;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return super.getCount();
	}

}
