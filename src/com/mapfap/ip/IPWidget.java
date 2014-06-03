package com.mapfap.ip;

import java.io.BufferedReader;

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
	private static final String ACTION_REFRESH = "com.mapfap.action.REFRESH";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		for (int appWidgetId : appWidgetIds) {
			drawWidget(context, appWidgetId);
		}
	}

	private void redrawWidgets(Context context) {
		int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, IPWidget.class));
		for (int appWidgetId : appWidgetIds) {
			drawWidget(context, appWidgetId);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		String action = intent.getAction();

		if (ACTION_REFRESH.equals(action)) {
			redrawWidgets(context);
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
		rv.setTextViewText(R.id.month_label, "LOADING");

		rv.setOnClickPendingIntent(R.id.next_month_button, PendingIntent.getBroadcast(context, 0,
				new Intent(context, IPWidget.class).setAction(ACTION_REFRESH), PendingIntent.FLAG_UPDATE_CURRENT));

		appWidgetManager.updateAppWidget(appWidgetId, rv);

		getWanIP(context, appWidgetId);
	}

	public String getWanIP(Context context, int appWidgetId) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			String wanIP = "";
			BufferedReader in = null;
			String data = null;
			try {
				new DownloadWebpageTask(context, appWidgetId).execute("http://ip.mapfap.com");
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("errr", e.toString());
				return "NO NETWORK CONNECTION";
			}
			return wanIP;
		} else {
			return "NO NETWORK CONNECTION";
		}
	}

}
