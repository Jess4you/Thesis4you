package com.jess.thesisforyou.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jess.thesisforyou.DatabaseHelper;
import com.jess.thesisforyou.R;
import com.jess.thesisforyou.models.Contact;
import com.jess.thesisforyou.models.Sms;

import java.util.ArrayList;

/**
 * Created by USER on 2/16/2019.
 */

public class BlockedContactsSmsThreadAdapter extends ArrayAdapter<Sms>{

    private Context mContext;
    private int mResource;
    private DatabaseHelper dbHelper;

    public BlockedContactsSmsThreadAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Sms> objects) {
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
        TextView tvBody = (TextView)convertView.findViewById(R.id.tvBody);
        TextView tvDate = (TextView)convertView.findViewById(R.id.tvDate);

        //Retrieve variables from Contact object
        final String id = getItem(position).getId();
        final String body = getItem(position).getBody();
        final String date = getItem(position).getDate();

        //Manipulate initialized views with retrieved variables
        tvBody.setText(body);
        tvDate.setText(date);

        return convertView;
    }


}
