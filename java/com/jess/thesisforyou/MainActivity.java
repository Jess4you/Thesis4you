package com.jess.thesisforyou;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

import static com.jess.thesisforyou.DatabaseHelper.COLUMN_CONTACT_ID;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    public static final int PERMISSIONS_REQUEST_CODE = 10;
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";

    DatabaseHelper dbHelper;
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Start background service MyService
        if(!isMyServiceRunning()){
            startService(new Intent(this, MyService.class));
        }

        dbHelper = new DatabaseHelper(this);
        //Permissions request
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.PACKAGE_USAGE_STATS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.ANSWER_PHONE_CALLS,
                        Manifest.permission.INSTANT_APP_FOREGROUND_SERVICE},
                PERMISSIONS_REQUEST_CODE);

        //Create channels for android Oreo support and above
        createNotificationChannels();


        Log.d(TAG,"This was passed");
        //If first password is not empty direct automatically to MainMenu Activity
        if(!dbHelper.checkFirstPass().equalsIgnoreCase("")){
            finish();
            Intent directToMain = new Intent(this, MainMenu.class);
            startActivity(directToMain);
        }


        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SetupSlider.class));
            }
        });

        //Open settings for usage stats access
        if (!isUsageStatsPermitted()) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

        checkIfDefaultSmsClient();
    }

    /**
     * This method checks if this application is granted access to usage stats
     * @return
     */

    private boolean isUsageStatsPermitted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * This method creates the notification channels for Android versions Oreo and above
     */

    private void createNotificationChannels(){
        Log.d(TAG,"createNotificationChannels: START");
        //Check version if greater or equals to Oreo
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID,"New messages", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This channel is for SMS receiving.");
            NotificationChannel channel2 = new NotificationChannel(CHANNEL_2_ID,"Lock Service", NotificationManager.IMPORTANCE_DEFAULT);
            channel2.setDescription("This channel is for the persistent Lock service.");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
        Log.d(TAG,"createNotificationChannels: FINISH");
    }

    /**
     * This method checks if specific permissions are granted
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean isMyServiceRunning(){
        Log.d(TAG,"isMyServiceRunning: START");
        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.jess.thesisforyou.MyService".equals(service.service.getClassName())){
                Log.d(TAG,"isMyServiceRunning: YES");
                return true;
            }
        }
        Log.d(TAG,"isMyServiceRunning: NO");
        return false;
    }

    /**
     * Override onResume method, checks if the application is default SMS Client,
     * Note: Application can only block calls and texts if it is set as the default SMS Client.
     */

    private void checkIfDefaultSmsClient(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            if (!Telephony.Sms.getDefaultSmsPackage(this).equals("com.jess.thesisforyou")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("This app is not set as your default messaging app. Do you want to set it as default?")
                        .setCancelable(false)
                        .setTitle("Alert!")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @TargetApi(19)
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                                startActivity(intent);
                            }
                        });
                builder.show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



}
