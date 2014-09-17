package com.mapfap.ip;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

public class DownloadWebpageTask extends AsyncTask<String, Void, String> {
	
        private Context context;
		private int appWidgetId;

		public DownloadWebpageTask(Context context, int appWidgetId) {
        	this.context= context;
        	this.appWidgetId = appWidgetId;
        }

		@Override
        protected String doInBackground(String... urls) {
			
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        	RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
    		rv.setTextViewText(R.id.ip_label, "Loading");
    		appWidgetManager.updateAppWidget(appWidgetId, rv);
			
			
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        	RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
    		rv.setTextViewText(R.id.ip_label, result);
    		appWidgetManager.updateAppWidget(appWidgetId, rv);
       }
        
    	private String downloadUrl(String myurl) throws IOException {
    	    InputStream is = null;
    	    // Only display the first 500 characters
    	    int len = 500;
    	        
    	    try {
    	        URL url = new URL(myurl);
    	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    	        conn.setReadTimeout(10000 /* milliseconds */);
    	        conn.setConnectTimeout(15000 /* milliseconds */);
    	        conn.setRequestMethod("GET");
    	        conn.setDoInput(true);
    	        
    	        conn.connect();
    	        int response = conn.getResponseCode();
    	        Log.d("dfv", "The response is: " + response);
    	        is = conn.getInputStream();

    	        String contentAsString = readIt(is, len);
    	        return contentAsString;
    	        
    	    } finally {
    	        if (is != null) {
    	            is.close();
    	        } 
    	    }
    	}
    	
    	public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
    	    Reader reader = null;
    	    reader = new InputStreamReader(stream, "UTF-8");        
    	    char[] buffer = new char[len];
    	    reader.read(buffer);
    	    return new String(buffer);
    	}

    }