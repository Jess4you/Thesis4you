package com.jess.thesisforyou;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Set;

/**
 * This activity allows the user to setup their desired lock types
 */

public class SetupLocks extends AppCompatActivity {

    //For checking fingerprint hardware support
    private FingerprintManager mFingerprintManager;

    //Database access
    DatabaseHelper dbHelper;

    //Spinner selection
    Spinner spnFirstLock,spnSecondLock;
    String mFLock,mSLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_locks);
        init();
    }

    /**
     * Initializer method
     */

    private void init(){
        //Retrieve system service fingerprint
        mFingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        //Initialize dbHelper
        dbHelper = new DatabaseHelper(this);

        //List available locks
        String[] items = new String[]{"PIN Lock", "Color Pattern Lock", "Dual Pattern Lock", "Fingerprint Lock"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        //Set adapter to first lock
        spnFirstLock = findViewById(R.id.spnFirstLock);
        spnFirstLock.setAdapter(adapter);
        //Set adapter to second lock
        spnSecondLock = findViewById(R.id.spnSecondLock);
        spnSecondLock.setAdapter(adapter);
    }

    /**
     * This method is called when the btnNext is clicked
     *
     * @param view on click view
     */

    public void nextView(View view) {
        mFLock = spnFirstLock.getSelectedItem().toString();
        mSLock = spnSecondLock.getSelectedItem().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(SetupLocks.this);
        builder.setMessage("You will not be able to change lock types until you finish configuring passwords for chosen locks, are you sure?")
                .setCancelable(false)
                .setTitle("Alert!")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Check if selected fingerprint and fingerprint service is available
                        if (mFLock.equalsIgnoreCase("fingerprint lock") || mSLock.equalsIgnoreCase("fingerprint lock")) {
                            if (!mFingerprintManager.isHardwareDetected()) {
                                Toast.makeText(SetupLocks.this, "Fingerprint scanner not detected. Please choose another lock type.", Toast.LENGTH_LONG).show();
                            } else {
                                dbHelper.insertToLockConfig(mFLock, mSLock);
                                String a = dbHelper.checkLocks();
                                nextStep();
                            }
                        } else {
                            //If no fingerprint lock selected
                            dbHelper.insertToLockConfig(mFLock, mSLock);
                            String a = dbHelper.checkLocks();
                            nextStep();
                        }
                    }
                });
        builder.show();
    }

    public void nextStep(){
        if(mFLock.equalsIgnoreCase("pin lock")){
            Intent newActivity;
            newActivity = new Intent(this, SetupLockPin.class);
            startActivity(newActivity);
        }else if(mFLock.equalsIgnoreCase("color pattern lock")){
            Intent newActivity;
            newActivity = new Intent(this, SetupLockColor.class);
            startActivity(newActivity);
        }else if(mFLock.equalsIgnoreCase("dual pattern lock")){
            Intent newActivity;
            newActivity = new Intent(this, SetupLockDualPattern.class);
            startActivity(newActivity);
        }else if(mFLock.equalsIgnoreCase("fingerprint lock")){
            if(mSLock.equalsIgnoreCase("pin lock")){
                Intent newActivity;
                newActivity = new Intent(this, SetupLockPin.class);
                startActivity(newActivity);
            }else if(mSLock.equalsIgnoreCase("color pattern lock")){
                Intent newActivity;
                newActivity = new Intent(this, SetupLockColor.class);
                startActivity(newActivity);
            }else if(mSLock.equalsIgnoreCase("dual pattern lock")){
                Intent newActivity;
                newActivity = new Intent(this, SetupLockDualPattern.class);
                startActivity(newActivity);
            }
        }else{
            System.out.println("none");
        }
    }

    /**
     * This method checks for the errors in inputs
     *
     * @return true if it has errors and false if otherwise
     */

    private boolean hasErrors(){
        boolean error = false;
        if(mFLock.equals(mSLock)){
            Toast.makeText(this, "Please choose two distinct lock types!",Toast.LENGTH_SHORT).show();
            error = true;
        }
        return error;
    }
}
