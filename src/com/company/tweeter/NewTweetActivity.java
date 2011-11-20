package com.company.tweeter;

import twitter4j.TwitterException;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.company.tweeter.accountmanager.Account;
import com.company.tweeter.accountmanager.AccountManager;
import com.company.tweeter.accountmanager.TwitterAccount;

public class NewTweetActivity extends Activity implements OnClickListener {
	
	private TextView newTweetTextField;
	private ImageButton newTweetButton;
	
	private AccountManager manager;
	private Account account;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		manager = AccountManager.getInstance();
		account = manager.getAccount();
		
		setContentView(R.layout.post_tweet);
		
		initializeUI();
		
	}

	private void initializeUI() {
		newTweetTextField = (TextView) findViewById(R.id.newTweetTextField);
		
		newTweetButton = (ImageButton) findViewById(R.id.newTweetButton);
		newTweetButton.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.newTweetButton:
			
			if(newTweetTextField.getText().toString() != null) {
				new UpdateStatus().execute(newTweetTextField.getText().toString());
			}
			else {
				Toast.makeText(getApplicationContext(), "Post message is empty", Toast.LENGTH_LONG).show();
			}
			
			break;

		default:
			break;
		}
		
	}
	
	class UpdateStatus extends AsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... params) {
			for(int i = 0; i < params.length; i++) {
				if(account instanceof TwitterAccount) {
					try {
						((TwitterAccount) account).updateStatus(params[i]);
						
					} catch (TwitterException e) {
						Log.d(Constants.TAG, e.getErrorMessage());
					}
				}
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			finish();
			super.onPostExecute(result);
		}
		
	}
	
}
