package com.jess.thesisforyou.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jess.thesisforyou.DatabaseHelper;
import com.jess.thesisforyou.R;
import com.jess.thesisforyou.adapters.ContactGroupsAdapter;
import com.jess.thesisforyou.adapters.ContactsAdapter;
import com.jess.thesisforyou.models.Contact;
import com.jess.thesisforyou.models.ContactGroup;

import java.util.ArrayList;

import static com.jess.thesisforyou.DatabaseHelper.COLUMN_FK_CONTACT_ID;

/**
 * Created by USER on 2/7/2019.
 */

public class ContactGroupsFragment extends Fragment {

    DatabaseHelper dbHelper;

    ListView lvContactGroups;
    static ContactGroupsAdapter mContactGroupsAdapter;

    //All Contact Groups are stored here during init
    ArrayList<ContactGroup> mContactGroupsArrayList;

    @Nullable
    @Override//INFLATER
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contactgroups,container,false);
        init(view);
        new LoadContactGroups().execute();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mContactGroupsAdapter!=null)
            mContactGroupsAdapter.notifyDataSetChanged();
    }

    public static ArrayAdapter<ContactGroup> getmContactGroupsAdapter(){
        return mContactGroupsAdapter;
    }

    /**
     * Initializer method
     * @param view pass parent view for child view initialization
     */

    public void init(View view){
        dbHelper = new DatabaseHelper(getContext());
        lvContactGroups = (ListView)view.findViewById(R.id.lvContactGroups);
        lvContactGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ArrayList<Contact> contacts = new ArrayList<>();
                ContactGroup selected  = (ContactGroup)adapterView.getItemAtPosition(i);
                String groupID = selected.getId();

                //Retrieve
                Cursor cursor = dbHelper.readContactByContactGroupID(groupID,db);

                for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
                    String contactID = cursor.getString(cursor.getColumnIndex(COLUMN_FK_CONTACT_ID));
                    String contactName = dbHelper.readContactName(contactID);
                    contacts.add(new Contact(contactID,contactName));
                }
                //TODO: Display list of contacts
            }
        });
    }

    /**
     * This method is used by the background thread LoadContactGroups to retrieve the ContactGroup ArrayList
     * @return contactGroupArrayList using database retrieval
     */

    private ArrayList<ContactGroup> retrieveContactGroups(){
        ArrayList<ContactGroup> contactGroupArrayList = new ArrayList<>();

        //Open the database to read data via cursor
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor contactGroupCursor = dbHelper.readContactGroups(db);
        while(contactGroupCursor.moveToNext()){
            String id = contactGroupCursor.getString(contactGroupCursor.getColumnIndex("_id"));
            String name = contactGroupCursor.getString(contactGroupCursor.getColumnIndex("name"));
            contactGroupArrayList.add(new ContactGroup(id,name));
        }
        //Close after retrieval
        db.close();

        return contactGroupArrayList;
    }

    /**
     * Separate thread for retrieving of contact groupings
     */

    public class LoadContactGroups extends AsyncTask<Void,Void,Void> {
        ArrayList<ContactGroup> ContactGroupList;
        @Override
        protected Void doInBackground(Void... voids) {
            ContactGroupList = retrieveContactGroups();
            mContactGroupsAdapter = new ContactGroupsAdapter(getActivity(), R.layout.adapter_contactgroup_item, ContactGroupList);
            mContactGroupsAdapter.notifyDataSetChanged();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            lvContactGroups.setAdapter(mContactGroupsAdapter);
        }
    }
}
