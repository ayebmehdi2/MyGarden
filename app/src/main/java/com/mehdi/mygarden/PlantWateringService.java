package com.mehdi.mygarden;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mehdi.mygarden.provider.PlantContract;
import com.mehdi.mygarden.utils.PlantUtils;

import static com.mehdi.mygarden.provider.PlantContract.BASE_CONTENT_URI;
import static com.mehdi.mygarden.provider.PlantContract.PATH_PLANTS;

public class PlantWateringService extends IntentService {

    public static final String ACTION_WATER = "water";
    public static final String ACTION_UPDATE_PLANT = "update";

    public PlantWateringService() {
        super("Fuucking START");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) return;
        final String ac = intent.getAction();
        if (ac == null) return;

        if (ac.equals(ACTION_WATER)){
            AbdaServiceSa9yeen(this, intent.getIntExtra("id", -1));
        } else if (ac.equals(ACTION_UPDATE_PLANT)){
            AbdaServiceNabta();
        }

    }

    public void AbdaServiceSa9yeen(Context context, int id){
        long timeNow = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        Uri uri = PlantContract.PlantEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
        Log.e("PlantWateringService", " URI  : " + uri);
        context.getContentResolver().update(uri, values, null, null);
        BadelNabta(context);
    }

    public static void BadelNabta(Context context){
        Intent in = new Intent(context, PlantWateringService.class);
        in.setAction(ACTION_UPDATE_PLANT);
        context.startService(in);
    }


    private void AbdaServiceNabta() {
        Uri PLANT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
        Cursor cursor = getContentResolver().query(PLANT_URI, null, null, null, PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);

        int imgRes = R.drawable.grass;
        long id = -1;
        long tii = 0;
        boolean isNeedWater = true;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int createTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
            int waterTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
            int plantTypeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);
            long timeNow = System.currentTimeMillis();
            long wateredAt = cursor.getLong(waterTimeIndex);
            if ((timeNow - wateredAt) < PlantUtils.MIN_AGE_BETWEEN_WATER) isNeedWater = false;
            long createdAt = cursor.getLong(createTimeIndex);
            int plantType = cursor.getInt(plantTypeIndex);
            id = cursor.getLong(cursor.getColumnIndex(PlantContract.PlantEntry._ID));
            cursor.close();
            tii = timeNow - wateredAt;
            imgRes = PlantUtils.getPlantImageRes(this, timeNow - createdAt, tii, plantType);
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, PlanetWidgetProvider.class));

        PlanetWidgetProvider.updatePlantWidget(this, appWidgetManager, imgRes, appWidgetIds,(int) id, isNeedWater, (int) tii);
    }




}

