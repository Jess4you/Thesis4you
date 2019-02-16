package com.jess.thesisforyou;

import android.app.Notification;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.jess.thesisforyou.DatabaseHelper.COLUMN_APP_ID;
import static com.jess.thesisforyou.DatabaseHelper.COLUMN_APP_NAME;
import static com.jess.thesisforyou.MainActivity.CHANNEL_2_ID;

/**
 * Created by USER on 2/9/2019.
 */

public class MyService extends Service {
    public static final String TAG = "MyService";

    DatabaseHelper dbHelper;
    SharedPreferences mPref;
    int SysCounter;
    int storage = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        //Initialize thesisDB helper
        dbHelper = new DatabaseHelper(this);

        //Initialize SharePreferences appLockPrefs
        mPref = getApplicationContext().getSharedPreferences("appLockPrefs", MODE_PRIVATE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand: START");
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_2_ID)
                .setContentTitle("Application Lock")
                .setContentText("This phone is protected by Mobile Guard.")
                .setSmallIcon(R.drawable.ic_app_lock)
                .build();

        delayCheck();

        startForeground(1,notification);

        Log.d(TAG,"onStartCommand: FINISH return START_STICKY");
        return START_NOT_STICKY;
    }

    private void delayCheck() {
        Log.d(TAG,"delayCheck: START");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.v(TAG,"Start:");
                matchApp();
                delayCheck();
            }
        }, 2000);
        Log.d(TAG,"delayCheck: FINISH");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void matchApp(){
        Log.d(TAG,"matchApp: START");
        String PackageName = "Nothing";

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        SharedPreferences.Editor editor = mPref.edit();
        long time = System.currentTimeMillis();
        String lock;

        Log.d(TAG,"matchApp: time: "+time);

        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 10000, time);
        if (stats != null) {

            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();

            for (UsageStats usageStats : stats) {

                Log.v(TAG,"for(usageStats):"+stats);
//                    if (usageStats.getPackageName().equals("com.google.android.youtube")) {
                try{

                    //Open the database to read data via cursor
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    Cursor cursor = dbHelper.readApps(db);

                    while(cursor.moveToNext()) {
                        String dbPackage = cursor.getString(cursor.getColumnIndex(COLUMN_APP_ID));
                        String usPackage = usageStats.getPackageName();
                        Log.v(TAG,"Package match: "+dbPackage+" = "+usPackage);
                        if (usageStats.getPackageName().equals(cursor.getString(cursor.getColumnIndex(COLUMN_APP_ID)))) {
                            long timeInForeground = usageStats.getLastTimeUsed();

                            Log.v(TAG,"Package match: SUCCESS on: "+dbPackage+" = "+usPackage );

                            PackageName = usageStats.getPackageName();

                            int minutes = (int) ((timeInForeground / (1000 * 60)) % 60);

                            int seconds = (int) (timeInForeground / 1000) % 60;

                            int hours = (int) ((timeInForeground / (1000 * 60 * 60)) % 24);

                            if (((seconds != dbHelper.readAppStamp(PackageName) && mPref.getInt("SysCounter", 0) != 1) || ((seconds != dbHelper.readAppStamp(PackageName) && mPref.getInt("SysCounter", 0) == 0)) && (mPref.getInt("SysCounter", 0) != 2)) && (mPref.getInt("SysCounter", 0) != 2)) {
                                Log.v(TAG,"Lock start");
                                dbHelper.updateAppStamp(cursor.getString(cursor.getColumnIndex(COLUMN_APP_ID)),seconds);
                                dbHelper.close();
                                SysCounter = SysCounter + 1;
                                int a = mPref.getInt("SysCounter", 0);
                                a++;


                                editor.putInt("SysCounter", a);
                                editor.commit();

                                lock = dbHelper.checkFirstLock();
                                if (lock.equalsIgnoreCase("dual pattern lock")) {
                                    editor.putString("package", PackageName);
                                    editor.commit();
                                    db.close();
                                    Intent newActivity;
                                    newActivity = new Intent(this, LockDualPattern.class);
                                    startActivity(newActivity);
                                } else if (lock.equalsIgnoreCase("fingerprint lock")) {
                                    editor.putString("package", PackageName);
                                    editor.commit();
                                    db.close();
                                    Intent newActivity;
                                    newActivity = new Intent(this, SetupLockFingerprint.class);
                                    startActivity(newActivity);
                                } else if (lock.equalsIgnoreCase("pin lock")) {
                                    editor.putString("package", PackageName);
                                    editor.commit();
                                    db.close();
                                    Intent newActivity;
                                    newActivity = new Intent(this, LockPin.class);
                                    startActivity(newActivity);
                                } else if (lock.equalsIgnoreCase("color pattern lock")) {
                                    editor.putString("package", PackageName);
                                    editor.commit();
                                    db.close();
                                    Intent newActivity;
                                    newActivity = new Intent(this, LockColor.class);
                                    startActivity(newActivity);

                                }


                            } else if (mPref.getInt("SysCounter", 0) >= 4) {
                                editor.putInt("SysCounter", 0);
                                editor.commit();
                                dbHelper.updateAppStamp(cursor.getString(cursor.getColumnIndex(COLUMN_APP_ID)), seconds);
                                Log.v("ZERO: ", "HELLO");
                                SysCounter = 0;


                            } else if (seconds != dbHelper.readAppStamp(PackageName)) {
                                dbHelper.updateAppStamp(cursor.getString(cursor.getColumnIndex(COLUMN_APP_ID)), seconds);
                                //dbHelp.getStamp(PackageName)
                                Log.v("IT: ", "HELLO");
                                SysCounter = SysCounter + 1;

                                int a = mPref.getInt("SysCounter", 0);
                                a++;
                                editor.putInt("SysCounter", a);
                                editor.commit();

                                System.out.println(SysCounter);
                            }
                            System.out.println(mPref.getInt("SysCounter", 0));
                            Log.i("BAC", "PackageName is " + PackageName + " Time is: " + hours + "h" + ":" + minutes + "m" + seconds + "s" + " " + storage + " DBHELP STAMP : " + dbHelper.readAppStamp(PackageName));
                        }
                        cursor.close();
                        db.close();
                    }
                }catch (Exception e){
                    Log.d("Database fail: ", "Error");
                }
                dbHelper.close();
            }
        }
    }
}
