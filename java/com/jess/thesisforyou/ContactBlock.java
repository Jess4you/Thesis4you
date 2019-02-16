package com.jess.thesisforyou;

import android.content.Context;
import android.content.DialogInterface;
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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.utils.Utils;
import com.jess.thesisforyou.adapters.SectionsPagerAdapter;
import com.jess.thesisforyou.fragments.AppGroupsFragment;
import com.jess.thesisforyou.fragments.AppsFragment;
import com.jess.thesisforyou.fragments.ContactGroupsFragment;
import com.jess.thesisforyou.fragments.ContactsFragment;
import com.jess.thesisforyou.models.Contact;
import com.jess.thesisforyou.models.ContactGroup;

import java.util.ArrayList;

public class ContactBlock extends AppCompatActivity {

    private static final String TAG = "ContactBlock";

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

    private LinearLayout llOverlayAddGroup;
    private ListView lvItemsToBeGrouped;
    private Button btnCancelAdd;
    private Button btnConfirmGroup;

    FloatingActionButton fabAddGroup;

    ArrayList<Contact> mContactArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactblock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    /**
     * Method attaches fragments to view pager
     * @param viewPager Attach fragments to specified ViewPager
     */

    private void setupViewPager(ViewPager viewPager) {
        ContactsFragment contactsFragment = new ContactsFragment();

        //TODO: Warning: this method slows loading of this activity
        mContactArrayList = contactsFragment.retrieveContacts(getApplicationContext());

        mSectionsPagerAdapter.addFragment(contactsFragment, "Contacts");

        ContactGroupsFragment contactGroupsFragment = new ContactGroupsFragment();
        mSectionsPagerAdapter.addFragment(contactGroupsFragment, "Contact Groups");
        viewPager.setAdapter(mSectionsPagerAdapter);
    }

    /**
     * Initializer method
     */

    private void init() {
        dbHelper = new DatabaseHelper(getApplicationContext());

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

        //Add group overlay views
        llOverlayAddGroup = (LinearLayout)findViewById(R.id.llOverlayAddGroup);
        lvItemsToBeGrouped = (ListView)findViewById(R.id.lvItemsToBeGrouped);
        btnCancelAdd = (Button)findViewById(R.id.btnCancelAdd);

        btnCancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llOverlayAddGroup.setVisibility(View.GONE);
                mViewPager.setVisibility(View.VISIBLE);
                fabAddGroup.setVisibility(View.VISIBLE);
            }
        });

        btnConfirmGroup = (Button)findViewById(R.id.btnConfirmGroup);

        fabAddGroup = (FloatingActionButton)findViewById(R.id.fabAddGroup);

        fabAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Returns the tab to the ContactsFragment if not focused
                if(tlTabs.getSelectedTabPosition()!=0)
                    tlTabs.getTabAt(0).select();

                llOverlayAddGroup.setVisibility(View.VISIBLE);
                mViewPager.setVisibility(View.GONE);
                fabAddGroup.setVisibility(View.GONE);
                //Create a new adapter list with items with active checkboxes
                final ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(ContactBlock.this,android.R.layout.simple_list_item_1);
                //Create a new ArrayList to store future blocked contacts
                final ArrayList<Contact> blockArrayList = new ArrayList<>();
                for(int i = 0; i < mContactArrayList.size(); i++){
                    String contactName = mContactArrayList.get(i).getName();
                    String contactID = mContactArrayList.get(i).getId();
                    String state = dbHelper.readContactState(contactID);

                    //Will add the contact to the list if state is = 1, 2, or 3
                    if(state.equals("1")||state.equals("2")||state.equals("3")){
                        nameAdapter.add(contactName);
                        blockArrayList.add(mContactArrayList.get(i));
                    }
                }
                lvItemsToBeGrouped.setAdapter(nameAdapter);

                //Display list of contacts to be added in a dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(ContactBlock.this);
                //Add this EditText for naming the group
                final EditText etGroupName = new EditText(ContactBlock.this);
                etGroupName.setHint("Group Name");
                builder.setTitle("Contacts to be added");
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
                        String groupName = etGroupName.getText().toString();
                        long id = dbHelper.insertContactGroup(groupName,blockArrayList);
                        ContactGroupsFragment.getmContactGroupsAdapter().add(new ContactGroup(String.valueOf(id),groupName));
                        Toast.makeText(ContactBlock.this,"Group "+groupName+" added!",Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
            }
        });
    }
}