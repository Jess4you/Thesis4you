package com.jess.thesisforyou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LockColor extends AppCompatActivity {

    String lock = "";
    DatabaseHelper dbHelper;
    SharedPreferences mPref;
    SharedPreferences.Editor editor;

    EditText etColor;
    Button btnRed, btnGreen, btnBlue, btnYellow, btnClear, btnDone;

    //Counter for failed attempts
    int attempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_color);
        dbHelper = new DatabaseHelper(this);
        etColor = (EditText) findViewById(R.id.etColor);
        mPref = this.getSharedPreferences("appLockPrefs", getApplicationContext().MODE_PRIVATE);

        btnRed = (Button)findViewById(R.id.btnRed);
        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etColor.setText(etColor.getText()+"r");
            }
        });

        btnGreen = (Button)findViewById(R.id.btnGreen);
        btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etColor.setText(etColor.getText()+"g");
            }
        });

        btnBlue = (Button)findViewById(R.id.btnBlue);
        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etColor.setText(etColor.getText()+"b");
            }
        });

        btnYellow = (Button)findViewById(R.id.btnYellow);
        btnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etColor.setText(etColor.getText()+"y");
            }
        });

        btnDone = (Button)findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextView(view);
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        editor = mPref.edit();

        lock = dbHelper.checkFirstLock();
        if(lock.equalsIgnoreCase("color pattern lock")){
            editor.putInt("SysCounter", 3);
            editor.commit();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        editor = mPref.edit();
        editor.putInt("SysCounter", 3);
        editor.commit();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void nextView(View view) {
        String pass = etColor.getText().toString();
        String savedPass = dbHelper.checkFirstPass();
        editor = mPref.edit();

        if (pass.equalsIgnoreCase(savedPass)) {
            editor.commit();
            finish();

            lock = dbHelper.checkSecondLock();
            if (lock.equalsIgnoreCase("")) {
                System.out.println(lock);
            } else if (lock.equalsIgnoreCase("fingerprint lock")) {
                Intent newActivity;
                newActivity = new Intent(this, SetupLockFingerprint.class);
                newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(newActivity);
            } else if (lock.equalsIgnoreCase("pin lock")) {
                Intent newActivity;
                newActivity = new Intent(this, LockPin.class);
                newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(newActivity);
            } else if (lock.equalsIgnoreCase("dual pattern lock")) {
                Intent newActivity;
                newActivity = new Intent(this, LockDualPattern.class);
                newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(newActivity);
            }
        } else {
            savedPass = dbHelper.checkSecondPass();
            if (pass.equalsIgnoreCase(savedPass)) {
                Calendar currentTime = Calendar.getInstance();
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                String actualTime = timeFormat.format(currentTime.getTime());
                int access = dbHelper.readAppAccess(mPref.getString("package", null));
                access++;
                dbHelper.updateAppUsed(mPref.getString("package", null), actualTime, access);
                editor.putInt("SysCounter", 1);
                editor.commit();
                finish();

            }else{
                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                attempts++;
                if(attempts == 3){
                    editor.putInt("SysCounter", 3);
                    editor.commit();
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    System.exit(0);
                }
            }
        }
    }
}
