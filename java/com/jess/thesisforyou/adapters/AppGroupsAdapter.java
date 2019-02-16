package com.jess.thesisforyou.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.jess.thesisforyou.DatabaseHelper;
import com.jess.thesisforyou.R;
import com.jess.thesisforyou.models.App;
import com.jess.thesisforyou.models.AppGroup;

import java.util.ArrayList;

/**
 * Created by USER on 2/8/2019.
 */

public class AppGroupsAdapter extends ArrayAdapter<AppGroup>{

    public static final String TAG = "AppGroupsAdapter";
    private Context mContext;
    private int mResource;
    private DatabaseHelper dbHelper;

    //Exclusive for application lock feature system counter
    private SharedPreferences mPref;

    public AppGroupsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<AppGroup> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        dbHelper = new DatabaseHelper(context);
        mPref = context.getApplicationContext().getSharedPreferences("appLockPrefs", context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Use LayoutInflater to populate ListView
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        //Initialize Views to manipulate
        final TextView tvName = (TextView)convertView.findViewById(R.id.tvName);
        final Switch swLock = (Switch) convertView.findViewById(R.id.swLock);

        //Retrieve variables from AppGroups object
        final String id = getItem(position).getId();
        final String name = getItem(position).getName();

        //Manipulate initialized views with retrieved variables
        tvName.setText(name);

        //TODO: set new db app group commands
        //Display current status of apps
        String state = dbHelper.readAppGroupState(id);
        switch(state){
            case "0":
                swLock.setChecked(false);
                break;
            case "1":
                swLock.setChecked(true);
        }

        //Add listener to Switch for each corresponding combination a certain state is passed
        swLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){
                    dbHelper.changeAppGroupState(id,"1",mPref,mContext);
                }else{
                    dbHelper.changeAppGroupState(id,"0",mPref,mContext);
                }
            }
        });
        return convertView;
    }
}
