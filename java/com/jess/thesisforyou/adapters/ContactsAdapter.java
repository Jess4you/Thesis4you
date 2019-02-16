package com.jess.thesisforyou.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.jess.thesisforyou.DatabaseHelper;
import com.jess.thesisforyou.R;
import com.jess.thesisforyou.models.Contact;

import java.util.ArrayList;

/**
 * Created by USER on 2/7/2019.
 */

public class ContactsAdapter extends ArrayAdapter<Contact> {

    private Context mContext;
    private int mResource;
    private DatabaseHelper dbHelper;

    public ContactsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Contact> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Use LayoutInflater to populate ListView
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        //Initialize Views to manipulate
        TextView tvName = (TextView)convertView.findViewById(R.id.tvName);
        final CheckBox cbText = (CheckBox)convertView.findViewById(R.id.cbText);
        final CheckBox cbCall = (CheckBox)convertView.findViewById(R.id.cbCall);

        //Retrieve variables from Contact object
        final String id = getItem(position).getId();
        final String name = getItem(position).getName();
        final String[] numbers = getItem(position).getNumbers();

        //Manipulate initialized views with retrieved variables
        tvName.setText(name);

        //Display current status of contacts
        String state = dbHelper.readContactState(id);
        switch(state){
            case "1":
                cbText.setChecked(true);
                break;
            case "2":
                cbCall.setChecked(true);
                break;
            case "3":
                cbText.setChecked(true);
                cbCall.setChecked(true);
                break;
            default:
        }


        //Add listeners to CheckBoxes for each corresponding combination a certain state is passed
        cbText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked && !cbCall.isChecked()){
                    //Check if contact is already stored first
                    if(dbHelper.isContactStored(id)){
                        dbHelper.changeContactState(id,1);
                    }else{
                        dbHelper.insertContact(id,name,numbers,1);
                    }
                }else if(!isChecked && !cbCall.isChecked()){
                    dbHelper.changeContactState(id,0);
                }else if(!isChecked && cbCall.isChecked()) {
                    dbHelper.changeContactState(id, 2);
                }else{
                    //both are checked
                    dbHelper.changeContactState(id,3);
                }
            }
        });
        cbCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked && !cbText.isChecked()){
                    //Check if contact is already stored first
                    if(dbHelper.isContactStored(id)){
                        dbHelper.changeContactState(id,2);
                    }else{
                        dbHelper.insertContact(id,name,numbers,2);
                    }
                }else if(!isChecked && !cbText.isChecked()){
                    dbHelper.changeContactState(id,0);
                }else if(!isChecked && cbText.isChecked()) {
                    dbHelper.changeContactState(id,1);
                }else{
                    //both are checked
                    dbHelper.changeContactState(id,3);
                }
            }
        });
        return convertView;
    }
}
