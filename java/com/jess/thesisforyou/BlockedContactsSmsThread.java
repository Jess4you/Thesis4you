package com.jess.thesisforyou;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jess.thesisforyou.adapters.BlockedContactsSmsAdapter;
import com.jess.thesisforyou.adapters.BlockedContactsSmsThreadAdapter;
import com.jess.thesisforyou.models.Sms;

import java.util.ArrayList;

import static com.jess.thesisforyou.DatabaseHelper.COLUMN_SMS_BODY;
import static com.jess.thesisforyou.DatabaseHelper.COLUMN_SMS_DATE;
import static com.jess.thesisforyou.DatabaseHelper.COLUMN_SMS_ID;
import static com.jess.thesisforyou.DatabaseHelper.COLUMN_SMS_STATE;
import static com.jess.thesisforyou.DatabaseHelper.COLUMN_SMS_TIME;

public class BlockedContactsSmsThread extends AppCompatActivity {

    DatabaseHelper dbHelper;

    String mContactID;
    String mContactName;

    TextView tvContactName;
    ListView lvBlockedSms;
    ArrayList<Sms> mBlockedSms;
    ArrayAdapter<Sms> mBlockedSmsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_contacts_sms_thread);
        Bundle extras = getIntent().getExtras();
        mContactID = extras.getString("id","0");
        init();
    }
    private void init(){
        dbHelper = new DatabaseHelper(this);

        tvContactName = (TextView)findViewById(R.id.tvContactName);
        mContactName = dbHelper.readContactName(mContactID);
        tvContactName.setText(mContactName);

        lvBlockedSms = (ListView)findViewById(R.id.lvBlockedSms);
        mBlockedSms = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = dbHelper.readSmsByContact(mContactID,"1",db);

        while(cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex(COLUMN_SMS_ID));
            String body = cursor.getString(cursor.getColumnIndex(COLUMN_SMS_BODY));
            String date = cursor.getString(cursor.getColumnIndex(COLUMN_SMS_DATE));
            String time = cursor.getString(cursor.getColumnIndex(COLUMN_SMS_TIME));
            String state = cursor.getString(cursor.getColumnIndex(COLUMN_SMS_STATE));
            Sms sms = new Sms(id,mContactID,body,date,time,state);
            mBlockedSms.add(sms);
        }

        mBlockedSmsAdapter = new BlockedContactsSmsThreadAdapter(this,R.layout.adapter_blocked_contacts_sms_thread,mBlockedSms);
        lvBlockedSms.setAdapter(mBlockedSmsAdapter);



    }
}
