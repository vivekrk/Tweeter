package com.company.tweeter.accountmanager;

/**
 * Handles the creation and maintainance of accounts.
 * @author vivek
 *
 */

public class AccountManager {
	private static AccountManager manager;
	private TwitterAccount account;
	
	/**
	 * Private constructor that returns an instance of TwitterAccount.
	 */
	
	private AccountManager() {
		account = new TwitterAccount();
	}
	
	/**
	 * Creates an instance of AccountManager.
	 * @return
	 * Returns a singleton instance of AccountManager
	 */
	
	public static AccountManager getInstance() {
		if(manager == null) {
			manager = new AccountManager();
		}
		
		return manager;
	}
	
	/**
	 * Returns an instance of account.
	 * @return
	 */
	
	public Account getAccount() {
			return account;
	}
}
