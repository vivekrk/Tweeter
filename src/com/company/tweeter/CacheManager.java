package com.company.tweeter;

import java.util.Hashtable;

public class CacheManager {

	private static CacheManager manager = null;
	private Hashtable<String, String> imageList;
	
	private CacheManager() {
		imageList = new Hashtable<String, String>();
	}
	
	public static CacheManager getInstance() {
		if(manager == null) {
			manager = new CacheManager();
		}
		return manager;
	}
	
	public void setImageForKey(String imageKey, String filePath) {
		imageList.put(imageKey, filePath);
	}
	
	public String getImageForKey(String imageKey) {
		return imageList.get(imageKey);
	}

}
