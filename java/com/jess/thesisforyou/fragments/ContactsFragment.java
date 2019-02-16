package com.jess.thesisforyou.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jess.thesisforyou.R;
import com.jess.thesisforyou.adapters.ContactsAdapter;
import com.jess.thesisforyou.models.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2/7/2019.
 */

public class ContactsFragment extends Fragment {

    LinearLayout llOverlayLoadScreen;
    ListView lvContacts;
    ContactsAdapter mContactsAdapter;

    //Exclusive to single item activities
    Button btnAddGroup;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts,container,false);
        init(view);
        new LoadContacts().execute();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mContactsAdapter!=null)
            mContactsAdapter.notifyDataSetChanged();
    }

    /**
     * Initializer method
     * @param view pass parent view for child view initialization
     */

    public void init(View view){
        llOverlayLoadScreen = (LinearLayout)view.findViewById(R.id.llOverlayLoadScreen);
        lvContacts = (ListView)view.findViewById(R.id.lvContacts);
    }

    /**
     * This method is used by the background thread LoadContacts to retrieve the Contact ArrayList
     * @return contactArrayList retrieved using ContentResolver
     */

    public ArrayList<Contact> retrieveContacts(Context context){
        ArrayList<Contact> contactArrayList = new ArrayList<>();

        Cursor contactCursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        //START CONTACT RETRIEVAL
        while(contactCursor.moveToNext()){

            final String contactID = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
            final String contactName = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.d("contact",contactName);

            Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",new String[]{contactID},null);

            //Retrieve numbers of each contact
            String[] contactNums = new String[phoneCursor.getCount()];

            //-START CONTACT NUMBERS RETRIEVAL
            for(int i = 0;phoneCursor.moveToNext();i++){
                String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if(number.substring(0,3).equals("+63")){
                    number = "0"+number.substring(3,number.length());
                    Log.v("substring","success");
                }
                contactNums[i] = number;
                Log.d("number",contactNums[i]);
            }
            //-END OF CONTACT NUMBERS RETRIEVAL

            Contact contact = new Contact(contactID,contactName,contactNums);
            contactArrayList.add(contact);
        }
        //END OF CONTACT RETRIEVAL
        return contactArrayList;
    }

    /**
     * Separate thread for retrieving of contacts
     */

    public class LoadContacts extends AsyncTask<Void,Void,Void> {
        ArrayList<Contact> ContactList;
        @Override
        protected Void doInBackground(Void... voids) {
            ContactList = retrieveContacts(getContext());
            mContactsAdapter = new ContactsAdapter(getActivity(), R.layout.adapter_contact_item, ContactList);
            mContactsAdapter.notifyDataSetChanged();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            llOverlayLoadScreen.setVisibility(View.GONE);
            lvContacts.setAdapter(mContactsAdapter);
        }
    }
}
