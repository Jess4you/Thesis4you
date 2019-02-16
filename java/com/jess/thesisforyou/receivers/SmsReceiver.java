package com.jess.thesisforyou.receivers;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.jess.thesisforyou.DatabaseHelper;
import com.jess.thesisforyou.R;

import static com.jess.thesisforyou.MainActivity.CHANNEL_1_ID;
import static com.jess.thesisforyou.MainActivity.CHANNEL_2_ID;

/**
 * Created by USER on 2/7/2019.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSReceiver";
    private final int NOTIFICATION_ID = 001;

    boolean blockMessage = false;
    SmsMessage mMessage;
    DatabaseHelper dbHelper;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        HandlerThread handlerThread =  new HandlerThread("database_helper");
        handlerThread.start();
        Handler handler =  new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bundle pudsBundle = intent.getExtras();
                Object[] pdus = (Object[]) pudsBundle.get("pdus");

                mMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);

                String messageAddress = mMessage.getOriginatingAddress();

                SQLiteDatabase db = context.openOrCreateDatabase("thesisDB", 0, null);
                dbHelper = new DatabaseHelper(context);

                messageAddress = convertAddress(messageAddress);

                //Match by name
                Cursor contactCursor = db.rawQuery("SELECT * From contact", null);
                if(contactCursor.moveToFirst()) {
                    do {
                        if(contactCursor.getString(contactCursor.getColumnIndex("name"))
                                .equalsIgnoreCase(messageAddress)){
                            Log.v(TAG,"Match by name: SUCCESS");
                            Toast.makeText(context,"BLOCKED",Toast.LENGTH_SHORT).show();
                            abortBroadcast();
                            blockMessage = true;
                        }
                    } while(contactCursor.moveToNext());
                }
                contactCursor.close();

                //Match by number
                if(db.rawQuery("SELECT * From contactNum where number = '"+messageAddress+"'",null)!= null) {
                    Log.v(TAG,"numQuery: Success");
                    Cursor contactNumCursor = db.rawQuery("SELECT * From contactNum where number = '"+messageAddress+"'", null);
                    while (contactNumCursor.moveToNext()) {
                        String dbNumber = contactNumCursor.getString(contactNumCursor.getColumnIndex("number"));
                        Log.v(TAG,"Database number to match = "+dbNumber);
                        if (dbNumber.equalsIgnoreCase(messageAddress)) {
                            Log.v(TAG, "Number Matched: TRUE");
                            String dbNumID = contactNumCursor.getString(contactNumCursor.getColumnIndex("_id"));
                            Log.v(TAG, "Number ID =" + dbNumID);
                            Cursor numDetailCursor = db.rawQuery("SELECT * FROM contactNumDetails WHERE contactNum_id = " + dbNumID  , null);
                            while (numDetailCursor.moveToNext()) {
                                String contactID = numDetailCursor.getString(numDetailCursor.getColumnIndex("contact_id"));
                                Log.v(TAG, "check number contact ID: " + contactID);
                                String contactName = dbHelper.readContactName(contactID);
                                String contactState = dbHelper.readContactState(contactID);
                                switch (contactState) {
                                    case "1":
                                        blockMessage = true;
                                        Toast.makeText(context, "Blocked message from: " + contactName, Toast.LENGTH_SHORT).show();
                                        dbHelper.insertSms(contactID,mMessage.getMessageBody(),"1",System.currentTimeMillis());
                                        break;
                                    case "2":
                                        blockMessage = false;
                                        dbHelper.insertSms(contactID,mMessage.getMessageBody(),"0",System.currentTimeMillis());
                                        break;
                                    case "3":
                                        blockMessage = true;
                                        Toast.makeText(context, "Blocked message from: " + contactName, Toast.LENGTH_SHORT).show();
                                        dbHelper.insertSms(contactID,mMessage.getMessageBody(),"1",System.currentTimeMillis());
                                        break;
                                    default:
                                        blockMessage = false;
                                        dbHelper.insertSms(contactID,mMessage.getMessageBody(),"0",System.currentTimeMillis());
                                }
                                break;
                            }
                            break;
                        }
                    }
                    contactNumCursor.close();
                }

                //Display unblocked message
                String smallText = mMessage.getMessageBody();
                if(smallText.length()>15){
                    smallText = smallText.substring(0, 15) + "...";
                }
                if(!blockMessage){
                    Notification notification = new NotificationCompat.Builder(context,CHANNEL_1_ID)
                            .setContentTitle("Message from a: "+mMessage.getDisplayOriginatingAddress())
                            .setContentText(smallText)
                            .setSmallIcon(R.drawable.ic_sms)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(mMessage.getDisplayMessageBody()))
                            .build();

                    mMessage.getTimestampMillis();
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                    notificationManagerCompat.notify(NOTIFICATION_ID,notification);
                }
                db.close();
            }
        });
    }

    /**
     * This method converts the address to match the address policy of the application
     * @param address to convert
     * @return new address
     */

    public String convertAddress(String address){
        Log.d(TAG,"convertAddress("+address+"): START");
        if(address.contains("-"))
            address = address.replace("-","");
        if(address.contains(" "))
            address = address.replace(" ","");
        if(address.substring(0,3).equals("+63")){
            address = "0"+address.substring(3,address.length());
        }
        Log.d(TAG,"convertAddress("+address+"): FINISH returned new address: "+address);
        return address;
    }

}
