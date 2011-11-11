package com.company.tweeter.accountmanager;

import com.company.tweeter.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;


public abstract class Account {
	
	private SharedPreferences mPrefs;
	
	/**
	 * Returns the request token for the account.
	 * @return
	 * RequestToken
	 */
	public abstract RequestToken getRequestToken();
	
	/**
	 * Returns the Authentication Url for the account.
	 * @return
	 * Url String which can be used to Authenticate the user.
	 */
	
	public abstract String getAuthenticationUrl();
	
	/**
	 * Generates the Access Token for the account. This can be used to authenticate the account. 
	 * @param oAuthVerifier
	 * Verifier string which can be used for generating the AccessToken
	 * @return
	 * Returns the access token.
	 */
	public abstract AccessToken getAccessToken(String oAuthVerifier);
	
	
	/**
	 * Sets the Access Token received from the response to the account instance.
	 * @param token
	 * AccessToken obtained after authentication.
	 */
	
	public abstract void setAccessToken(AccessToken token);
	
	/**
	 * Checks if the user is logged in with proper authentication details.
	 * @param context
	 * Context of in which the method is called. Ideally this would be the instance of the Activity 
	 * in which it is called.
	 * @return
	 * Returns true if the user is logged in and false otherwise.
	 */
	
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
	
	/**
	 * Writes the Access Token and Access Token secret to the Shared Preferences.
	 * @param accessToken
	 * Access Token received after authentication.
	 */
	
	public void writeTokenToPrefs(AccessToken accessToken) {
		SharedPreferences.Editor editor = mPrefs.edit();
		String token = accessToken.getToken();
		String tokenSecret = accessToken.getTokenSecret();
		editor.putString(Constants.ACCESS_TOKEN, token);
		editor.putString(Constants.ACCESS_TOKEN_SECRET, tokenSecret);
		editor.commit();
		
		Log.d(Constants.TAG, mPrefs.getString(Constants.ACCESS_TOKEN, null));
	}
	
	/**
	 * Retrieves the Access Token and Access Token secret from the Shared Preferences.
	 * @return
	 * Returns the Access Token generated from the Access Token and Access Token Secret.
	 */
	
	public AccessToken getTokenFromPreferences() {
		Log.d(Constants.TAG, "getTokenFromPreferences called");
		String token = mPrefs.getString(Constants.ACCESS_TOKEN, null);
		String tokenSecret = mPrefs.getString(Constants.ACCESS_TOKEN_SECRET, null);
		AccessToken accessToken = null;
		accessToken = new AccessToken(token, tokenSecret);
		return accessToken;
	}
}
