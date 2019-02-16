package com.jess.thesisforyou;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jess.thesisforyou.adapters.BlockedContactsSmsAdapter;
import com.jess.thesisforyou.models.Contact;

import java.util.ArrayList;

import static com.jess.thesisforyou.DatabaseHelper.COLUMN_FK_CONTACT_ID;

/**
 * This class is used to display the contacts with blocked sms
 */

public class BlockedContactsSms extends AppCompatActivity {
    public static final String TAG = "BlockedContactsSms";

    DatabaseHelper dbHelper;
    ListView lvBlockedContacts;
    ArrayList<Contact> mContactArrayList;
    BlockedContactsSmsAdapter mBlockedContactsSmsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_contacts_sms);
        init();
    }

    private void init(){
        dbHelper = new DatabaseHelper(this);

        lvBlockedContacts = (ListView)findViewById(R.id.lvBlockedContacts);
        mContactArrayList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = dbHelper.readSms("1",db);

        while(cursor.moveToNext()){
            String contactID = cursor.getString(cursor.getColumnIndex(COLUMN_FK_CONTACT_ID));
            String contactName = dbHelper.readContactName(contactID);
            Contact contact = new Contact(contactID,contactName);
            mContactArrayList.add(contact);
        }

        mBlockedContactsSmsAdapter = new BlockedContactsSmsAdapter(this,R.layout.adapter_blocked_contacts_sms,mContactArrayList);
        lvBlockedContacts.setAdapter(mBlockedContactsSmsAdapter);

        lvBlockedContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Contact selected = (Contact)adapterView.getItemAtPosition(i);
                String id = selected.getId();
                Bundle bundle = new Bundle();
                bundle.putString("id",id);
                Intent intent = new Intent(BlockedContactsSms.this, BlockedContactsSmsThread.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
