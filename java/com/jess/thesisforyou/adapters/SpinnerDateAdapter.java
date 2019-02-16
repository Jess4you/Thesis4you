package com.jess.thesisforyou.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jess.thesisforyou.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by USER on 2/14/2019.
 */

public class SpinnerDateAdapter extends ArrayAdapter<Date> {
    private Context mContext;
    int mResource;

    public SpinnerDateAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Date> dates) {
        super(context, resource, dates);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    public View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        convertView = layoutInflater.inflate(mResource, parent, false);
        Log.d("POWER","HELLO");
        long longDate =getItem(position).getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(longDate);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH)+1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        String date = mMonth+"/"+mDay+"/"+mYear;

        TextView tvDate = convertView.findViewById(R.id.tvDate);
        tvDate.setText(date);
        return convertView;
    }
}