package com.company.tweeter.accountmanager;

import com.company.tweeter.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;


public abstract class Account {
	
	private SharedPreferences mPrefs;
	
	public abstract RequestToken getRequestToken();
	public abstract String getAuthenticationUrl();
	public abstract AccessToken getAccessToken(String oAuthVerifier);
	
	public boolean isUserLoggedIn(Context context) {
		mPrefs = context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE);
		if(mPrefs.getString(Constants.ACCESS_TOKEN, null) != null) {
			Log.d(Constants.TAG, Constants.ACCESS_TOKEN + " found");
			return true;
		}
		else {
			return false;
		}
	}
	
	public void writeTokenToPrefs(AccessToken accessToken) {
		SharedPreferences.Editor editor = mPrefs.edit();
		String token = accessToken.getToken();
		String tokenSecret = accessToken.getTokenSecret();
		editor.putString(Constants.ACCESS_TOKEN, token);
		editor.putString(Constants.ACCESS_TOKEN_SECRET, tokenSecret);
		editor.commit();
		
		Log.d(Constants.TAG, mPrefs.getString(Constants.ACCESS_TOKEN, null));
	}
	
	public AccessToken getTokenFromPreferences() {
		Log.d(Constants.TAG, "getTokenFromPreferences called");
		String token = mPrefs.getString(Constants.ACCESS_TOKEN, null);
		String tokenSecret = mPrefs.getString(Constants.ACCESS_TOKEN_SECRET, null);
		AccessToken accessToken = null;
		accessToken = new AccessToken(token, tokenSecret);
		return accessToken;
	}
}
