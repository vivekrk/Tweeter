package com.company.tweeter.accountmanager;

public class AccountManager {
	private static AccountManager manager;
	private TwitterAccount account;
	
	private AccountManager() {
		account = new TwitterAccount();
	}
	
	public static AccountManager getInstance() {
		if(manager == null) {
			manager = new AccountManager();
		}
		
		return manager;
	}
	
	public TwitterAccount getAccount() {
			return account;
	}
}
