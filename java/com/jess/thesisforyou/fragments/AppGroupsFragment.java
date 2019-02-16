package com.jess.thesisforyou.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jess.thesisforyou.DatabaseHelper;
import com.jess.thesisforyou.R;
import com.jess.thesisforyou.adapters.AppGroupsAdapter;
import com.jess.thesisforyou.adapters.ContactGroupsAdapter;
import com.jess.thesisforyou.models.AppGroup;
import com.jess.thesisforyou.models.ContactGroup;

import java.util.ArrayList;

/**
 * Created by USER on 2/7/2019.
 */

public class AppGroupsFragment extends Fragment {

    DatabaseHelper dbHelper;

    ListView lvAppGroups;
    static AppGroupsAdapter mAppGroupsAdapter;

    //All Contact Groups are stored here during init
    ArrayList<ContactGroup> mContactGroupsArrayList;

    @Nullable
    @Override//INFLATER
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appgroups,container,false);
        init(view);
        new LoadAppGroups().execute();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mAppGroupsAdapter!=null)
            mAppGroupsAdapter.notifyDataSetChanged();
    }

    public static ArrayAdapter<AppGroup> getmAppGroupsAdapter(){
        return mAppGroupsAdapter;
    }

    /**
     * Initializer method
     * @param view pass parent view for child view initialization
     */

    public void init(View view){
        dbHelper = new DatabaseHelper(getContext());
        lvAppGroups = (ListView)view.findViewById(R.id.lvAppGroups);
    }

    /**
     * This method is used by the background thread LoadContactGroups to retrieve the ContactGroup ArrayList
     * @return contactGroupArrayList using database retrieval
     */

    private ArrayList<AppGroup> retrieveAppGroups(){
        ArrayList<AppGroup> appGroupArrayList = new ArrayList<>();

        //Open the database to read data via cursor
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor appGroupCursor = dbHelper.readAppGroups(db);
        while(appGroupCursor.moveToNext()){
            String id = appGroupCursor.getString(appGroupCursor.getColumnIndex("_id"));
            String name = appGroupCursor.getString(appGroupCursor.getColumnIndex("name"));
            appGroupArrayList.add(new AppGroup(id,name));
        }
        //Close after retrieval
        db.close();

        return appGroupArrayList;
    }

    /**
     * Separate thread for retrieving of contact groupings
     */

    public class LoadAppGroups extends AsyncTask<Void,Void,Void> {
        ArrayList<AppGroup> AppGroupList;
        @Override
        protected Void doInBackground(Void... voids) {
            AppGroupList = retrieveAppGroups();
            mAppGroupsAdapter = new AppGroupsAdapter(getActivity(), R.layout.adapter_appgroup_item, AppGroupList);
            mAppGroupsAdapter.notifyDataSetChanged();
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            lvAppGroups.setAdapter(mAppGroupsAdapter);
        }
    }
}
