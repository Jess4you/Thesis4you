package com.jess.thesisforyou;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import static com.jess.thesisforyou.DatabaseHelper.COLUMN_CONTACT_ID;

public class MainMenu extends AppCompatActivity {
    public static final String TAG = "MainMenu";
    DatabaseHelper dbHelper;
    LinearLayout llOverlayLoadScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        dbHelper = new DatabaseHelper(this);
        llOverlayLoadScreen = (LinearLayout)findViewById(R.id.llOverlayLoadScreen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        llOverlayLoadScreen = (LinearLayout)findViewById(R.id.llOverlayLoadScreen);
        llOverlayLoadScreen.setVisibility(View.GONE);

    }

    public void openAppLock(View view){
        llOverlayLoadScreen.setVisibility(View.VISIBLE);
        startActivity(new Intent(this,AppLock.class));
    }
    public void openContactBlock(View view){
        llOverlayLoadScreen.setVisibility(View.VISIBLE);
        startActivity(new Intent(this,ContactBlock.class));
    }
    public void openReports(View view){
        llOverlayLoadScreen.setVisibility(View.VISIBLE);
        startActivity(new Intent(this,Reports.class));
    }
    public void openSettings(View view){
        llOverlayLoadScreen.setVisibility(View.VISIBLE);
        startActivity(new Intent(this, Settings.class));
    }

    /**
     * TODO: Use this only for test populating data in the reports
     */
    public void populateBlockedMessages(){
        Log.d(TAG,"populateBlockedMessages: START");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //Retrieve current datetime
        long dateTime = System.currentTimeMillis();

        Cursor cursor = dbHelper.readContacts(db);

        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            String contactID = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_ID));
            int count = (int)(Math.random()*10);
            for (int i = 0; i < count; i++){
                if(dbHelper.insertSms(contactID,"Sample text","1",dateTime)){
                    Log.d(TAG, "Block sms inserted");
                }
            }
        }
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            String contactID = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_ID));
            int count = (int)(Math.random()*10);
            for (int i = 0; i < count; i++){
                if(dbHelper.insertSms(contactID,"Sample text","0",dateTime)){
                    Log.d(TAG, "Non block sms inserted");
                }
            }
        }
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            String contactID = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_ID));
            int count = (int)(Math.random()*10);
            for (int i = 0; i < count; i++){
                long duration = 0; // milliseconds since blocked is = 0
                if(dbHelper.insertCalls(contactID,"1",dateTime,duration)){
                    Log.d(TAG, "Block calls inserted");
                }
            }
        }
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            String contactID = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_ID));
            int count = (int)(Math.random()*10);
            for (int i = 0; i < count; i++){
                long duration = (long)(Math.random()*10000); // milliseconds = 10s
                if(dbHelper.insertCalls(contactID,"0",dateTime,duration)){
                    Log.d(TAG, "Non blocked calls inserted");
                }
            }
        }
        Log.d(TAG,"populateBlockedMessages: FINISH");
    }

}
