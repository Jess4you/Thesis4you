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

import java.util.ArrayList;

/**
 * Created by USER on 2/16/2019.
 */

public class BlockedContactsSmsAdapter extends ArrayAdapter<Contact> {

    private Context mContext;
    private int mResource;
    private DatabaseHelper dbHelper;

    public BlockedContactsSmsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Contact> objects) {
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

        //Retrieve variables from Contact object
        final String id = getItem(position).getId();
        final String name = getItem(position).getName();
        final String[] numbers = getItem(position).getNumbers();

        //Manipulate initialized views with retrieved variables
        tvName.setText(name);

        return convertView;
    }

}
