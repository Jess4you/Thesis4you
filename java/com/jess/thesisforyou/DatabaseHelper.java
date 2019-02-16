package com.jess.thesisforyou;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jess.thesisforyou.models.App;
import com.jess.thesisforyou.models.Contact;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;

/**
 * Created by USER on 2/7/2019.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    public static final String DATABASE_NAME = "thesisDB";

    /**
     *CALL AND TEXT FILTER TABLES
     */

    //Table for contacts
    public static final String TABLE_CONTACT = "contact";
    public static final String COLUMN_CONTACT_ID = "_id";
    public static final String COLUMN_CONTACT_NAME = "name";
    public static final String COLUMN_CONTACT_STATE = "state";
    public static final String COLUMN_CONTACT_BLOCK_FREQ_CALL = "freqCall";
    public static final String COLUMN_CONTACT_BLOCK_FREQ_SMS = "freqSms";

    //Table for contact numbers
    public static final String TABLE_CONTACTNUM = "contactNum";
    public static final String COLUMN_CONTACTNUM_ID = "_id";
    public static final String COLUMN_CONTACTNUM_NUMBER = "number";

    //Table for contact groups
    public static final String TABLE_CONTACTGROUP = "contactGroup";
    public static final String COLUMN_CONTACTGROUP_ID = "_id";
    public static final String COLUMN_CONTACTGROUP_NAME = "name";
    public static final String COLUMN_CONTACTGROUP_STATE = "state";

    //Contact foreign keys
    public static final String COLUMN_FK_CONTACT_ID = "contact"+COLUMN_CONTACT_ID;
    public static final String COLUMN_FK_CONTACTNUM_ID = "contactNum"+COLUMN_CONTACTNUM_ID;
    public static final String COLUMN_FK_CONTACTGROUP_ID = "contactGroup"+COLUMN_CONTACTGROUP_ID;

    //Contact composite keys tables
    public static final String TABLE_CONTACT_DETAIL = "contactDetails";
    public static final String TABLE_CONTACT_NUM_DETAIL = "contactNumDetails";

    /**
     *APPLICATION LOCK TABLES
     */

    //Table for applications
    public static final String TABLE_APP = "app";
    public static final String COLUMN_APP_ID = "_id";        //package name of application
    public static final String COLUMN_APP_NAME = "name";
    public static final String COLUMN_APP_STAMP = "stamp";   //timestamp
    public static final String COLUMN_APP_ACCESS = "access"; //times of access
    public static final String COLUMN_APP_USED = "use";      //last use
    public static final String COLUMN_APP_STATE = "state";

    //Table for application groups
    public static final String TABLE_APPGROUP = "appGroup";
    public static final String COLUMN_APPGROUP_ID = "_id";
    public static final String COLUMN_APPGROUP_NAME = "name";
    public static final String COLUMN_APPGROUP_STATE = "state";

    //Application Lock foreign keys
    public static final String COLUMN_FK_APP_ID = "app"+COLUMN_APP_ID;
    public static final String COLUMN_FK_APPGROUP_ID = "appGroup"+COLUMN_APPGROUP_ID;

    //Application Lock composite keys table
    public static final String TABLE_APP_DETAIL = "appDetail";

    /**
     * CONFIGURATION TABLES
     */

    //Table for lock configurations
    public static final String TABLE_LOCK = "lock";
    public static final String COLUMN_LOCK_ID = "_id";
    public static final String COLUMN_LOCK_ONE = "firstLock";
    public static final String COLUMN_LOCK_ONE_PASS = "firstPass";
    public static final String COLUMN_LOCK_TWO = "secondLock";
    public static final String COLUMN_LOCK_TWO_PASS = "secondPass";
    public static final String COLUMN_LOCK_STATE = "state";

    //table for security questions
    public static final String TABLE_SQ= "securityQuestion";
    public static final String COLUMN_SQ_ID = "_id";
    public static final String COLUMN_SQ_ONE = "firstQuestion";
    public static final String COLUMN_SQ_ONE_ANSWER= "firstAnswer";
    public static final String COLUMN_SQ_TWO = "secondQuestion";
    public static final String COLUMN_SQ_TWO_ANSWER = "secondAnswer";
    public static final String COLUMN_SQ_STATE = "state";

    //TODO: Add Configuration Lines here


    /**
     * OTHERS
     */

    //table for messages
    public static final String TABLE_SMS_INBOX = "sms";
    public static final String COLUMN_SMS_ID = "_id";
    public static final String COLUMN_SMS_BODY = "body";
    public static final String COLUMN_SMS_DATE = "date";
    public static final String COLUMN_SMS_TIME = "time";
    public static final String COLUMN_SMS_STATE = "state"; //0 or 1 with 1 as blocked and 0 as otherwise

    //table for calls
    public static final String TABLE_CALL_LOGS = "call";
    public static final String COLUMN_CALL_ID = "_id";
    public static final String COLUMN_CALL_STATE = "state";
    public static final String COLUMN_CALL_DATE = "date";
    public static final String COLUMN_CALL_DURATION = "duration";













    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        /**
         * Create Contact Tables
         */

        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_CONTACT +" ("
                +COLUMN_CONTACT_ID+" VARCHAR PRIMARY KEY, "
                +COLUMN_CONTACT_NAME+" VARCHAR, "
                +COLUMN_CONTACT_STATE+" VARCHAR, "
                +COLUMN_CONTACT_BLOCK_FREQ_CALL+" VARCHAR, "
                +COLUMN_CONTACT_BLOCK_FREQ_SMS+" VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_CONTACTNUM +" ("
                +COLUMN_CONTACTNUM_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +COLUMN_CONTACTNUM_NUMBER+" VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_CONTACTGROUP +" ("
                +COLUMN_CONTACTGROUP_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +COLUMN_CONTACTGROUP_NAME+" VARCHAR, "
                +COLUMN_CONTACTGROUP_STATE+" VARCHAR);");

        //This is for associating contacts with contact groups
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_CONTACT_DETAIL +" ("
                +COLUMN_FK_CONTACTGROUP_ID+" INTEGER, "
                +COLUMN_FK_CONTACT_ID+" VARCHAR, "
                +"FOREIGN KEY("+COLUMN_FK_CONTACTGROUP_ID+") REFERENCES "+TABLE_CONTACTGROUP+"("+COLUMN_CONTACTGROUP_ID+"));");

        //This is for associating contact numbers with contacts
        db.execSQL("CREATE TABLE IF NOT EXISTS "+ TABLE_CONTACT_NUM_DETAIL +" ("
                +COLUMN_FK_CONTACT_ID+" VARCHAR, "
                +COLUMN_FK_CONTACTNUM_ID+" INTEGER, "
                +"FOREIGN KEY("+COLUMN_FK_CONTACT_ID+") REFERENCES "+TABLE_CONTACT+"("+COLUMN_CONTACT_ID+"), "
                +"FOREIGN KEY("+COLUMN_FK_CONTACTNUM_ID+") REFERENCES "+TABLE_CONTACTNUM+"("+COLUMN_CONTACTNUM_ID+"));");

        /**
         * Create Application Lock Tables
         */

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_APP + "("
                + COLUMN_APP_ID + " VARCHAR PRIMARY KEY, "
                + COLUMN_APP_NAME + " VARCHAR, "
                + COLUMN_APP_STAMP + " INTEGER, "
                + COLUMN_APP_ACCESS + " INTEGER, "
                + COLUMN_APP_USED + " VARCHAR, "
                + COLUMN_APP_STATE + " VARCHAR); ");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_APPGROUP + "("
                + COLUMN_APPGROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_APPGROUP_NAME + " VARCHAR, "
                + COLUMN_APPGROUP_STATE + " VARCHAR);");

        //This is for associating apps with app groups
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_APP_DETAIL + "("
                + COLUMN_FK_APPGROUP_ID + " INTEGER, "
                + COLUMN_FK_APP_ID + " VARCHAR, "
                + "FOREIGN KEY(" + COLUMN_FK_APPGROUP_ID + ") REFERENCES "+TABLE_APPGROUP+"("+COLUMN_APPGROUP_ID + "));");

        /**
         * Create Configuration Tables
         */

        //This is the table for the lock configurations
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LOCK + "("
                + COLUMN_LOCK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LOCK_ONE + " VARCHAR, "
                + COLUMN_LOCK_ONE_PASS + " VARCHAR, "
                + COLUMN_LOCK_TWO + " VARCHAR, "
                + COLUMN_LOCK_TWO_PASS + " VARCHAR, "
                + COLUMN_LOCK_STATE + " VARCHAR); ");

        //This is the table for the security questions
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SQ + "("
                + COLUMN_SQ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SQ_ONE + " VARCHAR, "
                + COLUMN_SQ_ONE_ANSWER + " VARCHAR, "
                + COLUMN_SQ_TWO + " VARCHAR, "
                + COLUMN_SQ_TWO_ANSWER + " VARCHAR, "
                + COLUMN_SQ_STATE+ " VARCHAR); ");

        /**
         * Create Other Tables
         */

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SMS_INBOX + "("
                + COLUMN_SMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FK_CONTACT_ID + " VARCHAR, "
                + COLUMN_SMS_BODY + " TEXT, "
                + COLUMN_SMS_DATE + " VARCHAR, "
                + COLUMN_SMS_TIME + " VARCHAR, "
                + COLUMN_SMS_STATE + " VARCHAR); ");


        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CALL_LOGS + "("
                + COLUMN_CALL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FK_CONTACT_ID + " VARCHAR, "
                + COLUMN_CALL_STATE + " VARCHAR, "
                + COLUMN_CALL_DATE + " VARCHAR, "
                + COLUMN_CALL_DURATION + " VARCHAR); ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop Contact Tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTNUM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTGROUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT_DETAIL);

        //Drop Application Lock Tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPGROUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_DETAIL);

        //Drop Config Tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SQ);

        //Drop Others
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS_INBOX);
        onCreate(db);
    }






    public void closeDB(SQLiteDatabase db){
        db.close();
    }






    /**
     * [METHODS]********************************CONTACT*********************************************
     */

    /**
     * This method is used to insert new contact into the Contact table
     *
     * @param id to insert
     * @param name to insert
     * @param contactNums to insert
     * @param state to insert
     * @return true if insertion is successful and false if otherwise
     */

    public boolean insertContact(String id, String name, String[] contactNums, int state){
        Log.d(TAG,"insertContact("+id+","+name+","+contactNums.toString()+","+state+": START");
        SQLiteDatabase db = this.getWritableDatabase();

        //Insert new values to Contact table
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CONTACT_ID,id);
        cv.put(COLUMN_CONTACT_NAME,name);
        cv.put(COLUMN_CONTACT_STATE,state);
        cv.put(COLUMN_CONTACT_BLOCK_FREQ_CALL,0);
        cv.put(COLUMN_CONTACT_BLOCK_FREQ_SMS,0);

        if(db.insert(TABLE_CONTACT,null,cv) == -1){

            //Stop insertion of numbers if contact did not insert
            cv.clear();
            db.close();
            Log.d(TAG, "insertContact(" + id + "," + name + "," + contactNums.toString() + "," + state + ": FAILED");
            return false;
        }else {

            //Insert contact numbers of inserted contact to ContactNum table
            cv.clear();
            for (int i = 0; i < contactNums.length; i++) {
                cv.put(COLUMN_CONTACTNUM_NUMBER, contactNums[i]);
                long contactNumId = db.insert(TABLE_CONTACTNUM, null, cv);
                cv.clear();
                cv.put(COLUMN_FK_CONTACT_ID, id);
                cv.put(COLUMN_FK_CONTACTNUM_ID, contactNumId);
                db.insert(TABLE_CONTACT_NUM_DETAIL, null, cv);
                cv.clear();
            }
            db.close();
            Log.d(TAG,"insertContact("+id+","+name+","+contactNums.toString()+","+state+": FINISH SUCCESS");
            return true;
        }
    }

    /**
     * This method checks if the contact id is present in the Contact table
     *
     * @param id retrieve contact id to find
     * @return true if present in the Contact table and false if otherwise
     */

    public boolean isContactStored(String id){
        Log.d(TAG,"isContactStored("+id+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_CONTACT_ID+" FROM "+TABLE_CONTACT+" WHERE "+COLUMN_CONTACT_ID+" = "+id+";", null);
        if(cursor.getCount() <= 0){
            cursor.close();
            db.close();
            Log.d(TAG,"isContactStored("+id+"): FINISH return false");
            return false;
        }else{
            cursor.close();
            db.close();
            Log.d(TAG,"isContactStored("+id+"): FINISH return true");
            return true;
        }
    }

    /**
     * This method is to read all contacts in the database
     *
     * @param db as database reference
     * @return cursor result value
     */
    public Cursor readContacts(SQLiteDatabase db){
        Log.d(TAG,"readContacts: START");
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_CONTACT,null,null);
        Log.d(TAG,"readContacts: FINISH returned cursor (may or may not be null)");
        return result;
    }

    public Cursor readContactByContactGroupID(String groupID, SQLiteDatabase db){
        Log.d(TAG,"readContactsByContactGroupID("+groupID+",SQLiteDatabase db): START");
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_CONTACT_DETAIL+" WHERE "+COLUMN_FK_CONTACTGROUP_ID+" = "+groupID,null);
        Log.d(TAG,"readContactsByContactGroupID("+groupID+",SQLiteDatabase db): FINISH returned cursor result");
        return result;
    }

    /**
     * This method is used in order to retrieve the Contact name via the id
     * @param id as reference
     * @return name of contact
     */

    public String readContactName(String id){
        Log.d(TAG,"readContactName("+id+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        String name = id;
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_CONTACT_NAME+" FROM "+TABLE_CONTACT+" WHERE "+COLUMN_CONTACT_ID+" = "+id+";", null);
        while (cursor.moveToNext()){
            name = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_NAME));
        }
        cursor.close();
        db.close();
        Log.d(TAG,"readContactState("+id+"): FINISH return "+name);
        return name;
    }

    /**
     * This method changes the state of the contact
     *
     * @param id retrieve contact id for state change reference
     * @param state retrieve new state
     * @return true if change state is successful and false if otherwise
     */

    public boolean changeContactState(String id, int state){
        Log.d(TAG,"changeContactState("+id+","+state+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        int freq;
        cv.put(COLUMN_CONTACT_STATE,state);
        switch (state){
            case 1:
                freq = readContactSmsFreq(id, db);
                freq++;
                cv.put(COLUMN_CONTACT_BLOCK_FREQ_SMS,freq);
                break;
            case 2:
                freq = readContactCallFreq(id,db);
                freq++;
                cv.put(COLUMN_CONTACT_BLOCK_FREQ_CALL,freq);
                break;
            case 3:
                freq = readContactCallFreq(id,db);
                freq++;
                cv.put(COLUMN_CONTACT_BLOCK_FREQ_CALL,freq);
                db = this.getWritableDatabase();
                freq = readContactSmsFreq(id, db);
                freq++;
                cv.put(COLUMN_CONTACT_BLOCK_FREQ_SMS,freq);
                break;
            default:
        }
        if(db.update(TABLE_CONTACT,cv,COLUMN_CONTACT_ID+"= ?", new String[]{id})>0) {
            Log.d(TAG, "changeContactState("+id+","+state+"): FINISH SUCCESS");
            return true;
        }
        db.close();
        Log.d(TAG, "changeContactState("+id+","+state+"): FINISH FAILED");
        return false;
    }

    /**
     * This method is used to check the current status of the contact in the Contact tables
     *
     * @param id retrieve contact id for state read reference
     * @return state of id
     */

    public String readContactState(String id){
        Log.d(TAG,"readContactState("+id+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        String state = "0";
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_CONTACT_STATE+" FROM "+TABLE_CONTACT+" WHERE "+COLUMN_CONTACT_ID+" = "+id+";", null);
        while (cursor.moveToNext()){
            state = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_STATE));
        }
        cursor.close();
        db.close();
        Log.d(TAG,"readContactState("+id+"): FINISH return "+state);
        return state;
    }

    /**
     * This method is used in order to check whether a certain number is stored in the contactNum table
     * (Used in CallReceiver)
     * @param number as reference
     * @return true if number is stored and false if otherwise
     */

    public boolean isNumberStored(String number){
        Log.d(TAG,"isNumberStored("+number+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_CONTACTNUM_NUMBER+" FROM "+TABLE_CONTACTNUM+" WHERE "+COLUMN_CONTACTNUM_NUMBER+" = '"+number+"';", null);
        if(cursor.getCount() <= 0){
            cursor.close();
            db.close();
            Log.d(TAG,"isNumberStored("+number+"): FINISH return false");
            return false;
        }else{
            cursor.close();
            db.close();
            Log.d(TAG,"isNumberStored("+number+"): FINISH return true");
            return true;
        }
    }

    /**
     * This method is to retrieve the current frequency of blocking calls from this contact
     * (Used in changeContactState)
     * @param id as reference
     * @return integer call frequency
     */
    public int readContactCallFreq(String id, SQLiteDatabase db){
        Log.d(TAG,"readContactCallFreq("+id+"): START");
        int frequency = 0;
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_CONTACT_BLOCK_FREQ_CALL+" FROM "+TABLE_CONTACT+" WHERE "+COLUMN_CONTACT_ID+" = "+id+";", null);
        while (cursor.moveToNext()){
            frequency = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_BLOCK_FREQ_CALL)));
        }
        cursor.close();
        Log.d(TAG,"readContactCallFreq("+id+"): FINISH return "+frequency);
        return frequency;
    }

    /**
     * This method is to retrieve the current frequency of blocking sms from this contact
     * (Used in changeContactState)
     * @param id as reference
     * @return integer sms frequency
     */
    public int readContactSmsFreq(String id, SQLiteDatabase db){
        Log.d(TAG,"readContactSmsFreq("+id+"): START");
        int frequency = 0;
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_CONTACT_BLOCK_FREQ_SMS+" FROM "+TABLE_CONTACT+" WHERE "+COLUMN_CONTACT_ID+" = "+id+";", null);
        while (cursor.moveToNext()){
            frequency = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_CONTACT_BLOCK_FREQ_SMS)));
        }
        cursor.close();
        Log.d(TAG,"readContactSmsFreq("+id+"): FINISH return "+frequency);
        return frequency;
    }

    /**
     * This method will return the array string of names that have the highest frequency with the
     * frequency value appended to each name by "_" separator ie: 402_4 with 402 being the id and 4
     * being the highest frequency
     * @return highest call frequency contact or multiple highest frequency contacts
     */

    public String[] readHighestCallFreq(){
        Log.d(TAG,"readHighestCallFreq(): START");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor maxCursor = db.rawQuery("SELECT "+COLUMN_CONTACT_NAME+",MAX("+COLUMN_CONTACT_BLOCK_FREQ_CALL+") AS maxFreq FROM "+TABLE_CONTACT,null);
        String[] names = new String[1];
        int freq = 0;
        while (maxCursor.moveToNext()){
            if(maxCursor.getString(maxCursor.getColumnIndex("maxFreq"))!= null)
                freq = Integer.parseInt(maxCursor.getString(maxCursor.getColumnIndex("maxFreq")));
            names[0] = maxCursor.getString(maxCursor.getColumnIndex(COLUMN_CONTACT_NAME))+"_"+freq;
        }
        Cursor contactsCursor = db.rawQuery("SELECT "+COLUMN_CONTACT_NAME+" FROM "+TABLE_CONTACT+" WHERE "+COLUMN_CONTACT_BLOCK_FREQ_CALL+" = "+freq,null);
        if(contactsCursor.getCount() > 1){
            names = new String[contactsCursor.getCount()];
            int index = 0;
            for(contactsCursor.moveToFirst(); !contactsCursor.isAfterLast(); contactsCursor.moveToNext()){
                names[index] = contactsCursor.getString(contactsCursor.getColumnIndex(COLUMN_CONTACT_NAME))+"_"+freq;
            }
        }
        maxCursor.close();
        contactsCursor.close();
        db.close();
        Log.d(TAG,"readHighestCallFreq(): FINISH returned names[] result");
        return names;
    }


    /**
     * This method will return the array string of names that have the highest frequency with the
     * frequency value appended to each name by "_" separator ie: 402_4 with 402 being the id and 4
     * being the highest frequency
     * @return highest call frequency contact or multiple highest frequency contacts
     */

    public String[] readHighestSmsFreq(){
        Log.d(TAG,"readHighestSmsFreq(): START");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor maxCursor = db.rawQuery("SELECT "+COLUMN_CONTACT_NAME+",MAX("+COLUMN_CONTACT_BLOCK_FREQ_SMS+") AS maxFreq FROM "+TABLE_CONTACT,null);
        String[] names = new String[1];
        int freq = 0;
        while (maxCursor.moveToNext()){
            if(maxCursor.getString(maxCursor.getColumnIndex("maxFreq"))!= null)
                freq = Integer.parseInt(maxCursor.getString(maxCursor.getColumnIndex("maxFreq")));
            names[0] = maxCursor.getString(maxCursor.getColumnIndex(COLUMN_CONTACT_NAME))+"_"+freq;
        }
        Cursor contactsCursor = db.rawQuery("SELECT "+COLUMN_CONTACT_NAME+" FROM "+TABLE_CONTACT+" WHERE "+COLUMN_CONTACT_BLOCK_FREQ_SMS+" = "+freq,null);
        if(contactsCursor.getCount() > 1){
            names = new String[contactsCursor.getCount()];
            int index = 0;
            for(contactsCursor.moveToFirst(); !contactsCursor.isAfterLast(); contactsCursor.moveToNext()){
                names[index] = contactsCursor.getString(contactsCursor.getColumnIndex(COLUMN_CONTACT_NAME))+"_"+freq;
            }
        }
        maxCursor.close();
        contactsCursor.close();
        db.close();
        Log.d(TAG,"readHighestSmsFreq(): FINISH returned names[] result");
        return names;
    }
    /**
     * This method is used to read the contact number
     * @param number as reference
     * @param db as database reference (close after use)
     * @return cursor result from query
     */

    public Cursor readContactNums(String number, SQLiteDatabase db){
        Log.d(TAG,"readContactNums("+number+",SQLiteDatabase db): START");
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTNUM + " WHERE " + COLUMN_CONTACTNUM_NUMBER + " = '" +number+"';", null);
        Log.d(TAG,"readContactNums("+number+",SQLiteDatabase db): FINISH return cursor result");
        return cursor;
    }

    /**
     * This method is used to retrieve the Contact ID the number is associated with
     *
     * @param numId as the contact number id from the contactNum table
     * @param db as the database reference
     * @return cursor result;
     */

    public Cursor readNumDetails(String numId, SQLiteDatabase db){
        Log.d(TAG,"readNumDetails("+numId+",SQLiteDatabase db): START");
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACT_NUM_DETAIL + " WHERE " + COLUMN_FK_CONTACTNUM_ID + " = " +numId, null);
        Log.d(TAG,"readNumDetails("+numId+",SQLiteDatabase db): FINISH return cursor result");
        return cursor;
    }

    /**
     * This method is used to insert a new contact group into the ContactGroup table
     *
     * @param name of the group to insert
     * @param contactArrayList for contact id insertion reference
     * @return latest inserted row id in long
     */

    public long insertContactGroup(String name, ArrayList<Contact> contactArrayList){
        Log.d(TAG,"insertContactGroup("+name+","+contactArrayList.toString()+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CONTACTGROUP_NAME,name);
        cv.put(COLUMN_CONTACTGROUP_STATE,"0");

        //Retrieve the latest row id after insert
        long insertID = db.insert(TABLE_CONTACTGROUP,null,cv);
        if(insertID == -1) {
            cv.clear();
            db.close();
            Log.d(TAG,"insertContactGroup("+name+","+contactArrayList.toString()+"): FAILED returned "+insertID);
            return insertID;
        }else {
            //Insert contact ids from retrieved ArrayList to ContactDetail
            cv.clear();
            Log.v(TAG,name+" group created with contacts:");
            for (int i = 0; i < contactArrayList.size(); i++) {
                //Clear cv for each new record on the details for fresh new insert
                cv.clear();
                cv.put(COLUMN_FK_CONTACT_ID, contactArrayList.get(i).getId());
                cv.put(COLUMN_FK_CONTACTGROUP_ID, insertID);
                db.insert(TABLE_CONTACT_DETAIL, null, cv);
                Log.v(TAG,"["+ i +"]:"+contactArrayList.get(i).getName());
            }
            cv.clear();
            db.close();
            Log.d(TAG,"insertContactGroup("+name+","+contactArrayList.toString()+"): SUCCESS returned "+insertID);
            return insertID;
        }
    }

    /**
     * This method is for changing the list of contacts associated with the group
     *
     * @param id for contact group reference
     * @param state for contact group state
     */

    public void changeContactGroupState(String id, String state){
        Log.d(TAG,"changeContactGroupState("+id+","+state+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CONTACTGROUP_ID,id);
        cv.put(COLUMN_CONTACTGROUP_STATE,state);
        db.update(TABLE_CONTACTGROUP, cv, COLUMN_CONTACTGROUP_ID + " = " +id,null);

        String[] columns = {COLUMN_FK_CONTACTGROUP_ID,COLUMN_FK_CONTACT_ID};

        //Retrieve contact ids associated with retrieved group id
        Cursor contactDetail = db.query(TABLE_CONTACT_DETAIL,columns,COLUMN_FK_CONTACTGROUP_ID+"=?",new String[]{id},null,null,null);
        while (contactDetail.moveToNext()){
            String contactID = contactDetail.getString(contactDetail.getColumnIndex(COLUMN_FK_CONTACT_ID));
            changeContactState(contactID,Integer.parseInt(state));
        }
        contactDetail.close();
        db.close();
    }


    /**
     * This method is used to check the current status of the contact group in the ContactGroup tables
     *
     * @param id retrieve contact group id for state read reference
     * @return state of id
     */

    public String readContactGroupState(String id){
        Log.d(TAG,"readContactGroupState("+id+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        String state = "0";
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_CONTACTGROUP_STATE+" FROM "+TABLE_CONTACTGROUP+" WHERE "+COLUMN_CONTACTGROUP_ID+" = "+id+";", null);
        while (cursor.moveToNext()){
            state = cursor.getString(cursor.getColumnIndex(COLUMN_CONTACTGROUP_STATE));
        }
        cursor.close();
        db.close();
        Log.d(TAG,"readContactGroupState("+id+"): FINISH return "+state);
        return state;
    }

    /**
     * This method is used to read all ContactGroup items [Accompany with openDB]
     * @param db as reference (Must be opened first and closed after items retrieved)
     * @return Cursor
     */

    public Cursor readContactGroups(SQLiteDatabase db){
        Log.d(TAG,"readContactGroups: START");
        Cursor result;
        result = db.rawQuery("SELECT * FROM "+TABLE_CONTACTGROUP,null,null);
        Log.d(TAG,"readContactGroups: FINISH returned cursor (may or may not be null)");
        return result;
    }

    /**
     * [METHODS]***********************************APPS*********************************************
     */

    /**
     * This method is used to insert new app into the App table
     *
     * @param id to insert
     * @param name to insert
     * @param stamp to insert
     * @return true if insertion is successful and false if otherwise
     */

    public boolean insertApp(String id,String name, int stamp){
        Log.d(TAG,"insertApp("+id+","+name+","+stamp+"): START");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_APP_ID,id);
        cv.put(COLUMN_APP_NAME,name);
        cv.put(COLUMN_APP_STAMP,stamp);
        cv.put(COLUMN_APP_ACCESS,0);
        cv.put(COLUMN_APP_STATE,"1");

        if(db.insert(TABLE_APP, null, cv)==-1) {
            cv.clear();
            db.close();
            Log.d(TAG,"insertApp("+id+","+name+","+stamp+"): FAILED");
            return false;
        }else{
            cv.clear();
            db.close();
            Log.d(TAG,"insertApp("+id+","+name+","+stamp+"): SUCCESS");
            return true;
        }
    }

    /**
     * This method checks if the app id is present in the App table
     *
     * @param id retrieve app id to find
     * @return true if present in the App table and false if otherwise
     */

    public boolean isAppStored(String id){
        Log.d(TAG,"isAppStored("+id+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_APP_ID+" FROM "+TABLE_APP+" WHERE "+COLUMN_APP_ID+" = '"+id+"';", null);
        if(cursor.getCount() <= 0){
            cursor.close();
            db.close();
            Log.d(TAG,"isAppStored("+id+"): FINISH return false");
            return false;
        }else{
            cursor.close();
            db.close();
            Log.d(TAG,"isAppStored("+id+"): FINISH return true");
            return true;
        }
    }

    /**
     * This method changes the state of the app
     *
     * @param id retrieve app id for state change reference
     * @param state retrieve new state
     * @param context retrieve context for getAppLastUsed method parameter passing
     * @return true if change state is successful and false if otherwise
     */

    public boolean changeAppState(String id, String state, Context context){
        Log.d(TAG,"changeAppState("+id+","+state+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_APP_STATE,state);
        if(state.equals("1"))
            cv.put(COLUMN_APP_STAMP,getAppLastUsed(id,context));
        if(db.update(TABLE_APP,cv,COLUMN_APP_ID+"= ?", new String[]{id})>0) {
            cv.clear();
            db.close();
            Log.d(TAG, "changeAppState("+id+","+state+"): FINISH SUCCESS");
            return true;
        }else{
            cv.clear();
            db.close();
            Log.d(TAG, "changeAppState("+id+","+state+"): FINISH FAILED");
            return false;
        }
    }

    /**
     * This is a non-database method. It is used by changeAppState to set the last time used using system stats.
     * @param id as application reference
     * @param context for getSystemService calling
     * @return 0 if no stats available and seconds value if otherwise
     */

    public int getAppLastUsed(String id, Context context){
        String packageName = "Nothing";

        long timeInForeground = 500, time = System.currentTimeMillis();
        int hours = 500, minutes = 500, seconds = 500;

        UsageStatsManager usm = (UsageStatsManager)context.getSystemService(context.USAGE_STATS_SERVICE);

        List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 10000, time);
        if(stats != null){
            for (UsageStats usageStats : stats){
                if(usageStats.getPackageName().equals(id)){
                    timeInForeground = usageStats.getLastTimeUsed();
                    packageName = usageStats.getPackageName();

                    minutes = (int)((timeInForeground/(1000*60))%60);
                    seconds = (int)(timeInForeground/1000)%60;
                    hours = (int)((timeInForeground/(1000*60*60))%24);

                    return seconds;
                }
            }
        }
        return 0;
    }
    /**
     * This method is used to read timestamp of specified application
     * @param id as reference
     * @return int stamp value
     */

    public int readAppStamp(String id){
        Log.d(TAG,"readAppStamp("+id+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        int stamp = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_APP + " WHERE " + COLUMN_APP_ID+ " LIKE '%"+id+"%'", null);
        while (cursor.moveToNext()) {
            stamp = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_APP_STAMP)));
        }
        cursor.close();
        db.close();
        Log.d(TAG,"readAppStamp("+id+"): FINISH return "+stamp);
        return stamp;
    }

    /**
     * This method is used to update the current time stamp of the application
     *
     * @param stamp as retrieved time stamp of application
     * @param id as reference
     * @return true if app stamp was successfully updated and false if otherwise
     */

    public boolean updateAppStamp(String id, int stamp){
        Log.d(TAG,"updateAppStamp("+id+","+stamp+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_APP_STAMP, stamp);
        if(db.update(TABLE_APP,cv,COLUMN_APP_ID+" LIKE '%"+id+"%'" + " AND "+COLUMN_APP_STATE+" = 1", null)>0){
            Log.d(TAG,"updateAppStamp("+id+","+stamp+"): SUCCESS");
            cv.clear();
            db.close();
            return true;
        }
        cv.clear();
        db.close();
        Log.d(TAG,"updateAppStamp("+id+","+stamp+"): FAILED");
        return false;
    }

    /**
     * This method reads the current access value of an application
     *
     * @param id as reference
     * @return int access value
     */

    public int readAppAccess(String id){
        Log.d(TAG, "readAppAccess("+id+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        int access = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_APP + " WHERE " + COLUMN_APP_ID+ " LIKE '%"+id+"%'", null);
        while (cursor.moveToNext()) {
            access = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_APP_ACCESS)));
        }
        cursor.close();
        db.close();
        Log.d(TAG,"readAppAccess("+id+"): FINISH return "+access);
        return access;
    }

    /**
     * This method is used to check the current status of the app in the App table
     *
     * @param id retrieve app id for state read reference
     * @return state of id
     */

    public String readAppState(String id){
        Log.d(TAG,"readAppState("+id+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        String state = "0";
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_APP_STATE+" FROM "+TABLE_APP+" WHERE "+COLUMN_APP_ID+" = '"+id+"';", null);
        while (cursor.moveToNext()){
            state = cursor.getString(cursor.getColumnIndex(COLUMN_APP_STATE));
        }
        cursor.close();
        db.close();
        Log.d(TAG,"readAppState("+id+"): FINISH return "+state);
        return state;
    }

    /**
     * This method is used to update the last use of the application
     *
     * @param id as reference
     * @param time of last use
     * @param access value retrieved
     * @return true if successfully updated last use and false if otherwise
     */
    public boolean updateAppUsed(String id, String time, int access){
        Log.d(TAG,"updateAppUsed("+id+","+time+","+access+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_APP_USED, time);
        cv.put(COLUMN_APP_ACCESS, access);
        if(db.update(TABLE_APP,cv,COLUMN_APP_ID+" LIKE '%"+id+"%'" + " AND "+COLUMN_APP_STATE+" = 1", null)>0){
            db.close();
            Log.d(TAG,"updateAppUsed("+id+","+time+","+access+"): SUCCESS");
            return true;
        }
        db.close();
        Log.d(TAG,"updateAppUsed("+id+","+time+","+access+"): FAILED");
        return false;
    }
    /**
     * This method is used to retrieve all locked applications
     * (close db after retrieval)
     * @return cursor with query result
     */

    public Cursor readApps(SQLiteDatabase db){
        Log.d(TAG,"readApps: START");
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_APP + " WHERE " + COLUMN_APP_STATE + " = 1", null);
        Log.d(TAG,"readApps: FINISH return cursor result");
        return cursor;
    }
    /**
     * This method is used to insert a new app group into the AppGroup table
     *
     * @param name of the group to insert
     * @param appArrayList for app id insertion reference
     * @return latest inserted row id in long
     */

    public long insertAppGroup(String name, ArrayList<App> appArrayList, SharedPreferences preferences){
        Log.d(TAG,"insertAppGroup("+name+","+appArrayList.toString()+"): START");
        SQLiteDatabase db = this.getWritableDatabase();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("SysCounter", 2);
        editor.commit();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_APPGROUP_NAME,name);
        cv.put(COLUMN_APPGROUP_STATE,"1"); // 0 and 1 only for on and off

        //Retrieve the latest row id after insert
        long insertID = db.insert(TABLE_APPGROUP,null,cv);
        if(insertID == -1) {
            cv.clear();
            db.close();
            Log.d(TAG,"insertAppGroup("+name+","+appArrayList.toString()+"): FAILED returned "+insertID);
            return insertID;
        }else {
            //Insert app ids from retrieved ArrayList to AppDetail
            cv.clear();
            Log.v(TAG,name+" group created with apps:");
            for (int i = 0; i < appArrayList.size(); i++) {
                //Clear cv for each new record on the details for fresh new insert
                cv.clear();
                cv.put(COLUMN_FK_APP_ID, appArrayList.get(i).getId());
                cv.put(COLUMN_FK_APPGROUP_ID, insertID);
                db.insert(TABLE_APP_DETAIL, null, cv);
                Log.v(TAG,"["+ i +"]:"+appArrayList.get(i).getName());
            }
            cv.clear();
            db.close();
            Log.d(TAG,"insertAppGroup("+name+","+appArrayList.toString()+"): SUCCESS returned "+insertID);
            return insertID;
        }
    }

    /**
     * This method is for changing the list of apps associated with the group
     *
     * @param id for app group reference
     * @param state for app group state
     * @param preferences for SysCounter manipulation
     * @param context retrieve context for changeAppState method parameter passing
     */

    public void changeAppGroupState(String id, String state, SharedPreferences preferences, Context context){
        Log.d(TAG,"changeAppGroupState("+id+","+state+"): START");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_APPGROUP_ID,id);
        cv.put(COLUMN_APPGROUP_STATE,state);
        db.update(TABLE_APPGROUP, cv, COLUMN_APPGROUP_ID + " = " +id,null);

        String[] columns = {COLUMN_FK_APPGROUP_ID,COLUMN_FK_APP_ID};

        //Retrieve app ids associated with retrieved group id
        Cursor appDetail = db.query(TABLE_APP_DETAIL,columns,COLUMN_FK_APPGROUP_ID+"=?",new String[]{id},null,null,null);
        while (appDetail.moveToNext()){
            String appID = appDetail.getString(appDetail.getColumnIndex(COLUMN_FK_APP_ID));
            if(state.equals("1")){
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("SysCounter", 2);
                editor.commit();
            }
            changeAppState(appID,state,context);
        }
        appDetail.close();
        db.close();
        Log.d(TAG,"changeAppGroupState("+id+","+state+"): FINISH");
    }

    /**
     * This method is used to check the current status of the app group in the AppGroup tables
     *
     * @param id retrieve app group id for state read reference
     * @return state of app group id
     */

    public String readAppGroupState(String id){
        Log.d(TAG,"readAppGroupState("+id+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        String state = "0";
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_APPGROUP_STATE+" FROM "+TABLE_APPGROUP+" WHERE "+COLUMN_APPGROUP_ID+" = "+id+";", null);
        while (cursor.moveToNext()){
            state = cursor.getString(cursor.getColumnIndex(COLUMN_APPGROUP_STATE));
        }
        cursor.close();
        db.close();
        Log.d(TAG,"readAppGroupState("+id+"): FINISH return "+state);
        return state;
    }

    public boolean isAppGroupStored(String groupname){
        Log.d(TAG,"isAppGroupStored("+groupname+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_APPGROUP_NAME+" FROM "+TABLE_APPGROUP+" WHERE "+COLUMN_APPGROUP_NAME+" = '"+groupname+"';", null);
        if(cursor.getCount() <= 0){
            cursor.close();
            db.close();
            Log.d(TAG,"isAppGroupStored("+groupname+"): NO");
            return false;
        }else{
            cursor.close();
            db.close();
            Log.d(TAG,"isAppGroupStored("+groupname+"): YES");
            return true;
        }
    }

    /**
     * This method is used to read all AppGroup items [Accompany with openDB]
     * @param db as reference (Must be opened first and closed after items retrieved)
     * @return Cursor result
     */

    public Cursor readAppGroups(SQLiteDatabase db){
        Log.d(TAG,"readAppGroups: START");
        Cursor result;
        result = db.rawQuery("SELECT * FROM "+TABLE_APPGROUP,null,null);
        Log.d(TAG,"readAppGroups: FINISH returned cursor (may or may not be null)");
        return result;
    }

    /**
     * [METHODS]*******************************CONFIGURATIONS***************************************
     */

    /**
     * This method is the initial lock setup when choosing an item in the spinner
     * @param lock1 retrieve first spinner lock
     * @param lock2 retrieve second spinner lock
     * @return true if inserted and false if otherwise
     */

    public boolean insertToLockConfig(String lock1, String lock2) {
        Log.d(TAG, "insertToLockConfig("+lock1+","+lock2+",)");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LOCK_ONE, lock1);
        cv.put(COLUMN_LOCK_ONE_PASS, "");
        cv.put(COLUMN_LOCK_TWO, lock2);
        cv.put(COLUMN_LOCK_TWO_PASS, "");
        cv.put(COLUMN_LOCK_STATE, "1");

        if(db.insert(TABLE_LOCK, null, cv)!=1){
            Log.d(TAG, "insertToLockConfig("+lock1+","+lock2+"): SUCCESS");
            cv.clear();
            db.close();
            return true;
        }else{
            Log.d(TAG, "insertToLockConfig("+lock1+","+lock2+"): FAILED");
            cv.clear();
            db.close();
            return false;
        }
    }

    /**
     * This method is for checking the current lock passwords
     *
     * @return pass1 or pass2
     */

    public String checkLocks() {
        Log.d(TAG,"checkLocks: START");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOCK + " WHERE " + COLUMN_LOCK_STATE + " = 1", null);

        //Check if pass1 is empty
        while (cursor.moveToNext()) {
            String pass1 = cursor.getString(cursor.getColumnIndex(COLUMN_LOCK_ONE_PASS));
            if (pass1.equalsIgnoreCase("")) {
                db.close();
                cursor.close();
                Log.d(TAG,"checkLocks: FINISH return pass1");
                return "pass1";
            }
        }
        db.close();
        cursor.close();
        Log.d(TAG,"checkLocks: FINISH return pass2");
        return "pass2";
    }

    /**
     * TODO: add description
     * @return
     */

    public String checkFirstLock() {
        Log.d(TAG,"checkFirstLock: START");
        SQLiteDatabase db = this.getWritableDatabase();
        String lock = "";
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOCK + " WHERE " + COLUMN_LOCK_STATE + " = 1", null);
        while (cursor.moveToNext()) {
            lock = cursor.getString(cursor.getColumnIndex(COLUMN_LOCK_ONE));
            if (!lock.equalsIgnoreCase("")) {
                cursor.close();
                db.close();
                Log.d(TAG,"checkFirstLock: FINISH return "+lock);
                return lock;
            }
        }
        cursor.close();
        db.close();
        Log.d(TAG,"checkFirstLock: FINISH return "+lock);
        return lock;
    }

    /**
     * TODO: add description
     * @return
     */

    public String checkSecondLock() {
        Log.d(TAG,"checkSecondLock: START");
        SQLiteDatabase db = this.getWritableDatabase();
        String lock = "";
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOCK + " WHERE " + COLUMN_LOCK_STATE + " = 1", null);
        while (cursor.moveToNext()) {
            lock = cursor.getString(cursor.getColumnIndex(COLUMN_LOCK_TWO));
            if (!lock.equalsIgnoreCase("")) {
                cursor.close();
                db.close();
                Log.d(TAG,"checkSecondLock: FINISH return "+lock);
                return lock;
            }
        }
        cursor.close();
        db.close();
        Log.d(TAG,"checkSecondLock: FINISH return "+lock);
        return lock;
    }

    /**
     * This methods retrieves the first saved password in the database
     * (Method used for matching input passwords)
     * @return pass value
     */

    public String checkFirstPass() {
        Log.d(TAG,"checkFirstPass: START");
        SQLiteDatabase db = this.getWritableDatabase();
        String pass="";
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOCK + " WHERE " + COLUMN_LOCK_STATE + " = 1", null);
        while (cursor.moveToNext()) {
            pass = cursor.getString(cursor.getColumnIndex(COLUMN_LOCK_ONE_PASS));
            if (!pass.equalsIgnoreCase("")) {
                cursor.close();
                db.close();
                Log.d(TAG,"checkFirstPass: FINISH return "+pass);
                return pass;
            }
        }
        cursor.close();
        db.close();
        Log.d(TAG,"checkFirstPass: FINISH return "+pass);
        return pass;

    }

    /**
     * This methods retrieves the second saved password in the database
     * (Method used for matching input passwords)
     * @return pass value
     */

    public String checkSecondPass() {
        Log.d(TAG,"checkSecondPass: START");
        SQLiteDatabase db = this.getWritableDatabase();
        String pass="";
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOCK + " WHERE " + COLUMN_LOCK_STATE + " = 1", null);
        while (cursor.moveToNext()) {
            pass = cursor.getString(cursor.getColumnIndex(COLUMN_LOCK_TWO_PASS));
            if (!pass.equalsIgnoreCase("")) {
                cursor.close();
                db.close();
                Log.d(TAG,"checkSecondPass: FINISH return "+pass);
                return pass;
            }
        }
        cursor.close();
        db.close();
        Log.d(TAG,"checkSecondPass: FINISH return "+pass);
        return pass;
    }

    /**
     * This method is used to change the current first password
     * @param state of password
     * @param firstPass as new password
     * @return true if update successful and false if otherwise
     */
    public boolean setFirstPass(String state, String firstPass){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LOCK_ONE_PASS, firstPass);
        if(db.update(TABLE_LOCK,contentValues,COLUMN_LOCK_STATE+"= ?", new String[]{state})>0){
            return true;
        }
        return false;
    }

    /**
     * This method is used to change the current second password
     * @param state of password
     * @param secondPass as new password
     * @return true if update successful and false if otherwise
     */

    public boolean setSecondPass(String state, String secondPass){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_LOCK_TWO_PASS, secondPass);
        if(db.update(TABLE_LOCK,contentValues,COLUMN_LOCK_STATE+"= ?", new String[]{state})>0){
            return true;
        }
        return false;
    }

    /**
     * This method inserts new questions to the SQ table
     *
     * @param question1 retrieve user's first question
     * @param answer1 answer to question1
     * @param question2 retrieve user's second question
     * @param answer2 answer to question2
     * @return true if inserted and false if otherwise
     */

    public boolean insertToSQ(String question1, String answer1, String question2, String answer2){
        Log.d(TAG, "insertToSQ("+question1+","+answer1+","+question2+","+answer2+"): START");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SQ_ONE, question1);
        cv.put(COLUMN_SQ_ONE_ANSWER, answer1);
        cv.put(COLUMN_SQ_TWO, question2);
        cv.put(COLUMN_SQ_TWO_ANSWER, answer2);
        cv.put(COLUMN_SQ_STATE, "1");

        if(db.insert(TABLE_SQ, null, cv)!=1){
            Log.d(TAG, "insertToSQ("+question1+","+answer1+","+question2+","+answer2+"): SUCCESS");
            cv.clear();
            db.close();
            return true;
        }else{
            Log.d(TAG, "insertToSQ("+question1+","+answer1+","+question2+","+answer2+"): FAILED");
            cv.clear();
            db.close();
            return false;
        }
    }

    /**
     * [METHODS]************************************OTHERS******************************************
     */

    /**
     * This method is used to insert the received Sms into an inbox
     *
     * @param contactID the originating address
     * @param body as the text body
     * @param state as the status if blocked or not
     * @return true if successful insert and false if otherwise
     */

    public boolean insertSms(String contactID, String body, String state, long datetime){
        Log.d(TAG,"insertSms("+contactID+","+body+","+state+","+datetime+"): START");
        SQLiteDatabase db = this.getWritableDatabase();

        //Insert new values to Contact table
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FK_CONTACT_ID,contactID);
        cv.put(COLUMN_SMS_BODY,body);
        cv.put(COLUMN_SMS_STATE,state);

        //Convert millisecond datetime to format MM/dd/yyyy for date and hh:mm AM/PM for time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(datetime);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH)+1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMin = calendar.get(Calendar.MINUTE);
        int mAmPm = calendar.get(Calendar.AM_PM);

        String date = mMonth+"/"+mDay+"/"+mYear;
        cv.put(COLUMN_SMS_DATE,date);

        //TODO: add time aswell

        if(db.insert(TABLE_SMS_INBOX,null,cv) == -1){

            //Stop insertion of numbers if contact did not insert
            cv.clear();
            db.close();
            Log.d(TAG,"insertSms("+contactID+","+body+","+state+","+datetime+"): FAILED");
            return false;
        }else {

            cv.clear();
            db.close();
            Log.d(TAG,"insertSms("+contactID+","+body+","+state+","+datetime+"): SUCCESS");
            return true;
        }
    }


    public boolean insertCalls(String contactID, String state, long datetime, long duration){
        Log.d(TAG,"insertCalls("+contactID+","+state+","+datetime+","+duration+"): START");
        SQLiteDatabase db = this.getWritableDatabase();

        //Insert new values to Contact table
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FK_CONTACT_ID,contactID);
        cv.put(COLUMN_CALL_STATE,state);

        //Convert millisecond datetime to format MM/dd/yyyy for date and hh:mm AM/PM for time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(datetime);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH)+1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMin = calendar.get(Calendar.MINUTE);
        int mAmPm = calendar.get(Calendar.AM_PM);

        String date = mMonth+"/"+mDay+"/"+mYear;
        cv.put(COLUMN_CALL_DATE,date);

        //TODO: add time as well

        cv.put(COLUMN_CALL_DURATION,duration);

        if(db.insert(TABLE_CALL_LOGS,null,cv) == -1){

            //Stop insertion of numbers if contact did not insert
            cv.clear();
            db.close();
            Log.d(TAG,"insertCalls("+contactID+","+state+","+datetime+","+duration+"): FAILED");
            return false;
        }else {

            cv.clear();
            db.close();
            Log.d(TAG,"insertCalls("+contactID+","+state+","+datetime+","+duration+"): SUCCESS");
            return true;
        }
    }

    /**
     *
     * This method is used in order to retrieve the list of blocked messages by retrieving records with
     * @param state as reference
     * @param date as reference
     * @param db as database reference
     * @return cursor result
     */

    public Cursor readSms(String state, String date, SQLiteDatabase db){
        Log.d(TAG, "readSms("+state+",SQLiteDatabase db): START");
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_SMS_INBOX + " WHERE " + COLUMN_SMS_STATE + " = '"+state+"' AND "+COLUMN_CALL_DATE+" = '"+date+"' GROUP BY " + COLUMN_FK_CONTACT_ID,null);
        Log.d(TAG, "readSms(SQLiteDatabase db): FINISH return cursor value");
        return cursor;
    }

    /**
     * This method is used in order to retrieve the list of blocked messages by retrieving records with
     * @param state as reference
     * @param db as database reference
     * @return cursor result
     */

    public Cursor readSms(String state, SQLiteDatabase db){
        Log.d(TAG, "readSms("+state+",SQLiteDatabase db): START");
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_SMS_INBOX + " WHERE " + COLUMN_SMS_STATE + " = '"+state+"' GROUP BY " + COLUMN_FK_CONTACT_ID,null);
        Log.d(TAG, "readSms(SQLiteDatabase db): FINISH return cursor value");
        return cursor;
    }

    /**
     * This method is used to read all sms using
     * @param contact id as reference
     * @param state as reference
     * @param db as database reference
     * @return cursor result
     */

    public Cursor readSmsByContact(String contact, String state, SQLiteDatabase db){
        Log.d(TAG, "readSms("+contact+",SQLiteDatabase db): START");
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_SMS_INBOX + " WHERE " + COLUMN_FK_CONTACT_ID+ " = '"+contact+"' AND " + COLUMN_SMS_STATE + " = '"+state+"'",null);
        Log.d(TAG, "readSms("+contact+",SQLiteDatabase db): FINISH return cursor value");
        return cursor;
    }

    public Cursor countSmsByContact(String state, String date, SQLiteDatabase db){
        Log.d(TAG, "countSmsBlockedByContact(SQLiteDatabase db): START");
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_FK_CONTACT_ID+", COUNT("+COLUMN_SMS_ID+") AS amount FROM "+TABLE_SMS_INBOX + " WHERE " + COLUMN_SMS_STATE + " = '"+state+"' AND "+COLUMN_SMS_DATE+" = '"+date+"' GROUP BY " + COLUMN_FK_CONTACT_ID,null);
        Log.d(TAG, "countSmsBlockedByContact(SQLiteDatabase db): FINISH return cursor value");
        return cursor;
    }

    public Cursor readCalls(String state, String date, SQLiteDatabase db){
        Log.d(TAG, "readCalls("+state+",SQLiteDatabase db): START");
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_CALL_LOGS + " WHERE " + COLUMN_CALL_STATE + " = '"+state+"' AND "+COLUMN_CALL_DATE+" = '"+date+"' GROUP BY " + COLUMN_FK_CONTACT_ID,null);
        Log.d(TAG, "readCalls("+state+",SQLiteDatabase db): FINISH return cursor value");
        return cursor;
    }

    public Cursor countCallsByContact(String state, String date, SQLiteDatabase db){
        Log.d(TAG, "countCallsByContact("+date+",SQLiteDatabase db): START");
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_FK_CONTACT_ID+", COUNT("+COLUMN_CALL_ID+") AS amount FROM "+TABLE_CALL_LOGS+" WHERE "+COLUMN_CALL_STATE+" = '"+state+"' AND "+COLUMN_CALL_DATE+" = '"+date+"' GROUP BY " + COLUMN_FK_CONTACT_ID,null);
        Log.d(TAG, "countCallsByContact("+date+"SQLiteDatabase db): FINISH return cursor value");
        return cursor;
    }
}
