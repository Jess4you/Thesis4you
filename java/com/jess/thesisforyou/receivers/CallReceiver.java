package com.jess.thesisforyou.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


import com.jess.thesisforyou.DatabaseHelper;
import com.jess.thesisforyou.ITelephony;

import java.lang.reflect.Method;

import static com.jess.thesisforyou.DatabaseHelper.COLUMN_CONTACTNUM_ID;
import static com.jess.thesisforyou.DatabaseHelper.COLUMN_CONTACTNUM_NUMBER;
import static com.jess.thesisforyou.DatabaseHelper.COLUMN_FK_CONTACT_ID;

/**
 * Created by USER on 1/14/2019.
 */

public class CallReceiver extends BroadcastReceiver {
    public static final String TAG = "CallReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        SQLiteDatabase db = context.openOrCreateDatabase("thesisDB", 0, null);
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        ITelephony telephonyService;
        try{
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if(number.contains("-")){
                number = number.replace("-","");
            }
            if(number.contains(" ")){
                number = number.replace(" ","");
            }
            if(number.substring(0,3).equals("+63")){
                number = "0"+number.substring(3,number.length());
                Log.v("substring","success");
            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)){
                TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                Toast.makeText(context,"Ring "+number, Toast.LENGTH_SHORT).show();
                try{
                    //https://blog.previewtechs.com/blocking-call-and-sms-in-android-programmatically
                    String serviceManagerName = "android.os.ServiceManager";
                    String serviceManagerNativeName = "android.os.ServiceManagerNative";
                    String telephonyName = "com.android.internal.telephony.ITelephony";
                    Class<?> telephonyClass;
                    Class<?> telephonyStubClass;
                    Class<?> serviceManagerClass;
                    Class<?> serviceManagerNativeClass;
                    Method telephonyEndCall;
                    Object telephonyObject;
                    Object serviceManagerObject;
                    telephonyClass = Class.forName(telephonyName);
                    telephonyStubClass = telephonyClass.getClasses()[0];
                    serviceManagerClass = Class.forName(serviceManagerName);
                    serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
                    Method getService = // getDefaults[29];
                            serviceManagerClass.getMethod("getService", String.class);
                    Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
                    Binder tmpBinder = new Binder();
                    tmpBinder.attachInterface(null, "fake");
                    serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
                    IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
                    Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
                    telephonyObject = serviceMethod.invoke(null, retbinder);

                    //Database Retrieval
                    if(dbHelper.isNumberStored(number)){
                        Cursor numCursor = dbHelper.readContactNums(number,db);
                        Log.d(TAG,"isNumberStored: "+true);
                        while(numCursor.moveToNext()){
                            String dbNumber = numCursor.getString(numCursor.getColumnIndex(COLUMN_CONTACTNUM_NUMBER));
                            Log.d(TAG,"dbNumber: "+dbNumber);
                            if(dbNumber.equalsIgnoreCase(number)){
                                String dbNumID = numCursor.getString(numCursor.getColumnIndex(COLUMN_CONTACTNUM_ID));
                                Cursor numDetailCursor = dbHelper.readNumDetails(dbNumID,db);
                                Log.d(TAG,"dbNumID: "+dbNumID);
                                while(numDetailCursor.moveToNext()){
                                    String contactID = numDetailCursor.getString(numDetailCursor.getColumnIndex(COLUMN_FK_CONTACT_ID));
                                    Log.d(TAG,"contactID: "+contactID);
                                    String contactName = dbHelper.readContactName(contactID);
                                    String contactState = dbHelper.readContactState(contactID);
                                    Log.d(TAG,"state: "+contactState);
                                    //Block call if state is either 2 or 3
                                    switch (contactState) {
                                        case "2":
                                        case "3":
                                            Toast.makeText(context,"Blocked call from: "+contactName,Toast.LENGTH_SHORT).show();
                                            telephonyEndCall = telephonyClass.getMethod("endCall");
                                            telephonyEndCall.invoke(telephonyObject);
                                    }
                                }
                                numDetailCursor.close();
                            }
                        }
                        numCursor.close();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                Toast.makeText(context,"Answered "+number, Toast.LENGTH_SHORT).show();
            }
            if(state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)){
                Toast.makeText(context,"Idle "+number, Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
    }


    // Method to disconnect phone automatically and programmatically
    // Keep this method as it is
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void disconnectPhoneItelephony(Context context)
    {
        ITelephony telephonyService;
        TelephonyManager telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        try
        {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            telephonyService.endCall();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
