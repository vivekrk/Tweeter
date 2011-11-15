package com.company.tweeter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * AsyncTask class that downloads the profile images.
 * @author vivek
 *
 */

class ImageDownloader extends AsyncTask<Stack<String>, Integer, Bitmap> {

	private String filePath = null;
	private View imageView = null;
	
	/**
	 * Sets the file path where the image needs to be saved.
	 * @param filePath
	 */

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * Sets the image view returned from the ViewBinder
	 * @param imageView
	 */

	public void setImageView(View imageView) {
		this.imageView = imageView;
	}

	@Override
	protected Bitmap doInBackground(Stack<String>... params) {
		Bitmap bmp = null;
		URL imageUrl = null;
		
		try {
			for (int i = 0; i < params.length; i++) {
				imageUrl = new URL(params[i].pop());
			}
			HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
			connection.connect();
			
			InputStream is = connection.getInputStream();
			bmp = saveImageFile(is, filePath);
			return bmp;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
		if (imageView instanceof ImageView) {
			((ImageView) imageView).setImageBitmap(result);
		}
		super.onPostExecute(result);
	}
	
    /**
     * Saves the bitmap image got from the input stream in the specified path.
     * 
     * @param is
     * Input stream of the image.
     * 
     * @param path
     * Path where the image file is to be stored.
     * @return
     * Bitmap image
     */
    
    private Bitmap saveImageFile(InputStream is, String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
				FileOutputStream fo = new FileOutputStream(file);
				byte[] buffer = new byte[1000];
				int n = is.read(buffer, 0, 1000);
				int size = n;
				while (n > 0) {
					fo.write(buffer, 0, n);
					n = is.read(buffer, 0, 1000);
					size += n;
				}
				Log.d(Constants.TAG, "Downloading..." + "total size: " + size);
			}
			Bitmap bmp = BitmapFactory.decodeFile(path);
			return bmp;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
