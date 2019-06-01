package com.mehdi.mygarden;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.mehdi.mygarden.ui.MainActivity;
import com.mehdi.mygarden.ui.PlantDetailActivity;
import com.mehdi.mygarden.utils.PlantUtils;


public class PlanetWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int img, int appWidgetId, int id,  boolean isNeedWater, int ti) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.planet_widget_provider);

        String time = PlantUtils.getDisplayAgeInt( ti) + " " + PlantUtils.getDisplayAgeUnit(context, ti);

        views.setTextViewText(R.id.time, time);

        if (!isNeedWater) views.setViewVisibility(R.id.widget_water_button, View.GONE);

        views.setImageViewResource(R.id.nabta, img);

        views.setTextViewText(R.id.count, String.valueOf(id));

        Intent i = new Intent(context, PlantDetailActivity.class);
        i.putExtra(PlantDetailActivity.EXTRA_PLANT_ID, id);
        PendingIntent PI = PendingIntent.getActivity(context, 0, i, 0);
        views.setOnClickPendingIntent(R.id.nabta, PI);

        Intent wateringIntent = new Intent(context, PlantWateringService.class);
        wateringIntent.setAction(PlantWateringService.ACTION_WATER);
        wateringIntent.putExtra("id", id);
        PendingIntent wateringPendingIntent = PendingIntent.getService(context, 0, wateringIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_water_button, wateringPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        PlantWateringService.BadelNabta(context);
    }

    public static void updatePlantWidget(Context context, AppWidgetManager appWidgetManager, int img, int[] appWidgetIds, int id, boolean isNeedWater, int ti){
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, img, appWidgetId, id, isNeedWater, ti);
        }
    }
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

