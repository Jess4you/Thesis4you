package com.jess.thesisforyou.fragments;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import com.jess.thesisforyou.R;
import com.jess.thesisforyou.adapters.AppsAdapter;
import com.jess.thesisforyou.adapters.ContactsAdapter;
import com.jess.thesisforyou.models.App;
import com.jess.thesisforyou.models.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2/7/2019.
 */

public class AppsFragment extends Fragment {

    PackageManager mPackageManager;

    //Application list
    LinearLayout llOverlayLoadScreen;
    ListView lvApps;
    AppsAdapter mAppsAdapter;

    //Text for search
    String searchText;

    @Nullable
    @Override//INFLATER
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps,container,false);
        mPackageManager = inflater.getContext().getPackageManager();
        init(view);

        new LoadApps().execute();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mAppsAdapter!=null)
            mAppsAdapter.notifyDataSetChanged();
    }

    /**
     * Initializer method
     * @param view pass parent view for child view initialization
     */

    public void init(View view){
        searchText = "";
        llOverlayLoadScreen = (LinearLayout)view.findViewById(R.id.llOverlayLoadScreen);
        lvApps = (ListView)view.findViewById(R.id.lvApps);
    }

    /**
     * This method is used by the background thread LoadApps to retrieve the App ArrayList
     * @return appArrayList retrieved using PackageManager
     */

    public ArrayList<App> retrieveApps(PackageManager packageManager){

        //Use initialize PackageManager to retrieve list of installed applications and info
        List<ApplicationInfo> appInfo = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<App> appArrayList = new ArrayList<>();
        for(int i = 0;i < appInfo.size(); i++){
            String packageName = appInfo.get(i).packageName;
            String name = packageManager.getApplicationLabel(appInfo.get(i)).toString();
            if (packageManager.getLaunchIntentForPackage(packageName) != null) {
                Drawable icon = appInfo.get(i).loadIcon(packageManager);
                App application = new App(packageName, name, icon);
                appArrayList.add(application);
            }
        }
        return appArrayList;
    }

    /**
     * Separate thread for retrieving of apps
     */

    public class LoadApps extends AsyncTask<Void,Void,Void> {
        ArrayList<App> AppList;
        @Override
        protected Void doInBackground(Void... voids) {
            AppList = retrieveApps(mPackageManager);
            mAppsAdapter = new AppsAdapter(getActivity(),R.layout.adapter_app_item,AppList);
            mAppsAdapter.notifyDataSetChanged();

            //Listens to text and rewinds the whole thread
            final SearchView s = (SearchView)getActivity().findViewById(R.id.svSearch);
            s.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchText = query;
                    reExecute();
                    return true;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    searchText = newText;
                    reExecute();
                    return false;
                }
            });
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            llOverlayLoadScreen.setVisibility(View.GONE);
            lvApps.setAdapter(mAppsAdapter);
        }
        protected void reExecute(){
            new LoadApps().execute();
        }
    }
}
