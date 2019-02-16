package com.jess.thesisforyou.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jess.thesisforyou.DatabaseHelper;
import com.jess.thesisforyou.R;
import com.jess.thesisforyou.models.Contact;
import com.jess.thesisforyou.models.ContactGroup;

import java.util.ArrayList;

/**
 * Created by USER on 2/8/2019.
 */

public class ContactGroupsAdapter extends ArrayAdapter<ContactGroup>{

    public static final String TAG = "ContactGroupsAdapter";

    private Context mContext;
    private int mResource;
    private DatabaseHelper dbHelper;

    public ContactGroupsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ContactGroup> objects) {
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

        //Retrieve variables from ContactGroup object
        final String id = getItem(position).getId();
        final String name = getItem(position).getName();

        //Manipulate initialized views with retrieved variables
        tvName.setText(name);

        //Display current status of contacts
        String state = dbHelper.readContactGroupState(id);
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
                    dbHelper.changeContactGroupState(id,"1");
                }else if(!isChecked && !cbCall.isChecked()){
                    dbHelper.changeContactGroupState(id,"0");
                }else if(!isChecked && cbCall.isChecked()) {
                    dbHelper.changeContactGroupState(id, "2");
                }else{
                    //both are checked
                    dbHelper.changeContactGroupState(id,"3");
                }
            }
        });
        cbCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked && !cbText.isChecked()){
                    //Check if contact is already stored first
                    dbHelper.changeContactGroupState(id,"2");
                }else if(!isChecked && !cbText.isChecked()){
                    dbHelper.changeContactGroupState(id,"0");
                }else if(!isChecked && cbText.isChecked()) {
                    dbHelper.changeContactGroupState(id, "1");
                }else{
                    //both are checked
                    dbHelper.changeContactGroupState(id,"3");
                }
            }
        });
        return convertView;
    }
}
