package com.jess.thesisforyou;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jess.thesisforyou.adapters.SectionsPagerAdapter;
import com.jess.thesisforyou.fragments.AppGroupsFragment;
import com.jess.thesisforyou.fragments.AppsFragment;
import com.jess.thesisforyou.fragments.ContactGroupsFragment;
import com.jess.thesisforyou.models.App;
import com.jess.thesisforyou.models.AppGroup;
import com.jess.thesisforyou.models.Contact;
import com.jess.thesisforyou.models.ContactGroup;

import java.util.ArrayList;

public class AppLock extends AppCompatActivity {

    private static final String TAG = "AppLock";

    DatabaseHelper dbHelper;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private ViewPager mViewPager;

    SharedPreferences mPref;

    FloatingActionButton fabAddGroup;

    ArrayList<App> mAppArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();

    }

    /**
     * Method attaches fragments to view pager
     * @param viewPager Attach fragments to specified ViewPager
     */

    private void setupViewPager(ViewPager viewPager){
        AppsFragment appsFragment = new AppsFragment();

        //TODO: Slows down process
        mAppArrayList = appsFragment.retrieveApps(getPackageManager());
        if(mAppArrayList.isEmpty()){
            Log.d(TAG,"mAppArrayList: empty");
        }
        if(mAppArrayList == null){
            Log.d(TAG,"mAppArrayList: null");
        }

        mSectionsPagerAdapter.addFragment(appsFragment,"Applications");

        AppGroupsFragment appGroupsFragment = new AppGroupsFragment();
        mSectionsPagerAdapter.addFragment(appGroupsFragment,"Application Groups");
        viewPager.setAdapter(mSectionsPagerAdapter);
    }

    /**
     * Initializer method
     */

    private void init(){
        dbHelper = new DatabaseHelper(getApplicationContext());

        mPref = this.getSharedPreferences("appLockPrefs",MODE_PRIVATE);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Use SectionsPagerAdapter to setup ViewPager
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        setupViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Fragment fragment = ((SectionsPagerAdapter)mViewPager.getAdapter()).getFragment(position);
                if(fragment != null){
                    fragment.onResume();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Setup tabs with ViewPager
        final TabLayout tlTabs = (TabLayout) findViewById(R.id.tlTabs);
        tlTabs.setupWithViewPager(mViewPager);


        fabAddGroup = (FloatingActionButton)findViewById(R.id.fabAddGroup);
        fabAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Returns the tab to the ContactsFragment if not focused
                if(tlTabs.getSelectedTabPosition()!=0)
                    tlTabs.getTabAt(0).select();

                //Create a new adapter list with items with active switches
                final ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(AppLock.this,android.R.layout.simple_list_item_1);
                //Create a new ArrayList to store future blocked contacts
                final ArrayList<App> lockArrayList = new ArrayList<>();
                for(int i = 0; i < mAppArrayList.size(); i++){
                    String appName = mAppArrayList.get(i).getName();
                    String appID = mAppArrayList.get(i).getId();
                    String state = dbHelper.readAppState(appID);

                    //Will add the app to the list if state is = 1
                    if(state.equals("1")){
                        nameAdapter.add(appName);
                        lockArrayList.add(mAppArrayList.get(i));
                    }
                }

                //Display list of contacts to be added in a dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(AppLock.this);
                //Add this EditText for naming the group
                final EditText etGroupName = new EditText(AppLock.this);
                etGroupName.setHint("Group Name");
                builder.setTitle("Apps to be added");
                builder.setView(etGroupName);
                builder.setAdapter(nameAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int selected) {{
                        Log.i(TAG,"Possible addition of delete function for selected items");
                    }}
                });
                builder.setPositiveButton("Add Group", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String groupName = etGroupName.getText().toString();
                        if(groupName.isEmpty()){
                            etGroupName.setError("Insert a group name");
                        }else if(dbHelper.isAppGroupStored(groupName)){
                            etGroupName.setError("This group already is exists");
                        }else{
                            long id = dbHelper.insertAppGroup(groupName,lockArrayList,mPref);
                            AppGroupsFragment.getmAppGroupsAdapter().add(new AppGroup(String.valueOf(id),groupName));
                            Toast.makeText(AppLock.this,"Group "+groupName+" added!",Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    }
}
