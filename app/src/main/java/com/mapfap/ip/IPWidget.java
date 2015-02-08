package com.mapfap.ip;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class IPWidget extends AppWidgetProvider {
	private static final String ACTION_RELOAD = "com.mapfap.action.RELOAD";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		for (int appWidgetId : appWidgetIds) {
			drawWidget(context, appWidgetId);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		String action = intent.getAction();

		if (ACTION_RELOAD.equals(action)) {
			int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, IPWidget.class));
			for (int appWidgetId : appWidgetIds) {
				getWanIP(context, appWidgetId);
			}
		}
	}

	@Override
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
		drawWidget(context, appWidgetId);
	}

	private void drawWidget(Context context, int appWidgetId) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
		rv.setTextViewText(R.id.ip_label, "WAN IP");

		rv.setOnClickPendingIntent(R.id.reload_button, PendingIntent.getBroadcast(context, 0,
				new Intent(context, IPWidget.class).setAction(ACTION_RELOAD), PendingIntent.FLAG_UPDATE_CURRENT));

		appWidgetManager.updateAppWidget(appWidgetId, rv);
		
		getWanIP(context, appWidgetId);
	}

	public void getWanIP(Context context, int appWidgetId) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			
			try {
				new DownloadWebpageTask(context, appWidgetId).execute("http://ip.mapfap.com");
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("errr", e.toString());
				setText(e.toString(), context, appWidgetId);
			}
		} else {
			setText("n/a", context, appWidgetId);
		}
	}

	private void setText(String text, Context context, int appWidgetId) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    	RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
		rv.setTextViewText(R.id.ip_label, text);
		appWidgetManager.updateAppWidget(appWidgetId, rv);
	}

}
