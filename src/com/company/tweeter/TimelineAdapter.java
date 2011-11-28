package com.company.tweeter;

import java.util.Date;
import java.util.Hashtable;

import twitter4j.util.TimeSpanConverter;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.company.tweeter.ImageDownloader.OnDownloadCompletedListener;

public class TimelineAdapter extends SimpleCursorAdapter {

	private Activity activity;
	private Cursor data;
	
	private CacheManager cacheManager;
	
	private Hashtable<String, String> imageUrlHastable;
	
	public TimelineAdapter(Activity activity, int layout, Cursor c,
			String[] from, int[] to) {
		super(activity, layout, c, from, to);
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.data = c;
		
		cacheManager = CacheManager.getInstance();
		imageUrlHastable = new Hashtable<String, String>();
		
	}
	
	static class ViewHolder {
		TextView username;
		TextView time;
		TextView tweetMessage;
		TextView retweetedBy;
		
		ImageView userProfileImageView;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if(convertView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.tweet_row, null);
			
			holder = new ViewHolder();
			
			holder.username = (TextView) convertView.findViewById(R.id.username);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.tweetMessage = (TextView) convertView.findViewById(R.id.tweetMessage);
			holder.retweetedBy = (TextView) convertView.findViewById(R.id.retweetedBy);
			holder.userProfileImageView = (ImageView) convertView.findViewById(R.id.userImageView);
			
			convertView.setTag(holder);
		}
		
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(data.moveToPosition(position)) {
			String usernameString = data.getString(data.getColumnIndex(Constants.USERNAME));
			String imageUrl = data.getString(data.getColumnIndex(Constants.PROFILE_IMAGE));
			
			if(!imageUrlHastable.contains(usernameString) && cacheManager.getImageForKey(usernameString) == null) {
				imageUrlHastable.put(usernameString, imageUrl);
			}
			
			holder.username.setText(usernameString);
			
			String dateString = data.getString(data.getColumnIndex(Constants.CREATED_TIME));
			long date = Date.parse(dateString);
			
			TimeSpanConverter convertor = new TimeSpanConverter();
			
			holder.time.setText(convertor.toTimeSpanString(date));
			
			holder.tweetMessage.setText(data.getString(data.getColumnIndex(Constants.TWEET)));
			holder.retweetedBy.setText(data.getString(data.getColumnIndex(Constants.RETWEETED_BY)));
			
			String imagePath = cacheManager.getImageForKey(usernameString);
			
			if(imagePath == null) {
				holder.userProfileImageView.setImageResource(R.drawable.ic_launcher);
//				Log.d(Constants.TAG, "Now downloading image for..." + usernameString);
//				Log.d(Constants.TAG, "###########");
				
				if(ImageDownloader.isNetworkConnected(activity)) {
					ImageDownloader downloader = new ImageDownloader();
					downloader.setContext(activity);
					downloader.setOnDownloadCompletedListener(new OnDownloadCompletedListener() {
						
						public void onDownloadCompleted() {
							if(activity instanceof TimelineActivity) {
								if(!((TimelineActivity) activity).isScrolling()) {
									notifyDataSetChanged();
								}
							}
						}
					});
					downloader.execute(imageUrlHastable);
				}
				else {
					Log.d(Constants.TAG, "Offline");
				}
			}
			else {
				Bitmap bm = getImageBitmapFromPath(imagePath);
//				Animation fade = AnimationUtils.loadAnimation(activity, R.anim.fade);
				
				if(bm != null) {
					holder.userProfileImageView.setImageBitmap(ImageHelper
							.getRoundedCornerBitmap(bm));
				}
				
				else {
					holder.userProfileImageView.setImageResource(R.drawable.ic_launcher);
				}

//				userProfileImageView.setAnimation(fade);
			}
			
		}
		
		return convertView;
	}
	
	@Override
	public Object getItem(int position) {
		String statusID = null;
		
		if(data.moveToPosition(position)) {
			statusID = data.getString(data.getColumnIndex(Constants.STATUS_ID));
		}
		
		return statusID;
	}
	
	private Bitmap getImageBitmapFromPath(String imagePath) {
		return BitmapFactory.decodeFile(imagePath);
	}
	
}
