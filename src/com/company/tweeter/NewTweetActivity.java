package com.company.tweeter;

import twitter4j.TwitterException;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.company.tweeter.accountmanager.Account;
import com.company.tweeter.accountmanager.AccountManager;
import com.company.tweeter.accountmanager.TwitterAccount;

public class NewTweetActivity extends Activity implements OnClickListener, TextWatcher {
	
	private EditText newTweetTextField;
	private ImageButton newTweetButton;
	private TextView letterCount;
	
	private AccountManager manager;
	private Account account;
	
	private String username;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		manager = AccountManager.getInstance();
		account = manager.getAccount();
		
		setContentView(R.layout.post_tweet);
		
		initializeUI();
		
		username = getIntent().getStringExtra(Constants.USERNAME);
		
		if(username != null) {
			newTweetTextField.setText("@" + username + " ");
			newTweetTextField.setSelection(newTweetTextField.getText().length());
		}
		
	}

	private void initializeUI() {
		newTweetTextField = (EditText) findViewById(R.id.newTweetTextField);
		newTweetTextField.addTextChangedListener(this);
		
		newTweetButton = (ImageButton) findViewById(R.id.newTweetButton);
		newTweetButton.setOnClickListener(this);
		
		letterCount = (TextView) findViewById(R.id.letterCount);
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
			setResult(RESULT_OK);
			finish();
			super.onPostExecute(result);
		}
		
	}

	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		int charCount = newTweetTextField.getText().length();
		
		letterCount.setTextColor(Color.GREEN);
		
		if(charCount > 100) {
			letterCount.setTextColor(Color.YELLOW);
		}
		
		if(charCount > 140) {
			charCount = 140 - charCount;
			letterCount.setTextColor(Color.RED);
		}
		letterCount.setText(Integer.toString(charCount));
	}
	
}
