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

public class LockPin extends AppCompatActivity {

    String lock = "";
    DatabaseHelper dbHelper;
    SharedPreferences mPref;
    SharedPreferences.Editor editor;


    EditText etPin;
    Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnClear, btnDone;

    //Counter for failed attempts
    int attempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_pin);
        dbHelper = new DatabaseHelper(this);
        etPin = (EditText) findViewById(R.id.etPin);
        mPref = this.getSharedPreferences("appLockPrefs", getApplicationContext().MODE_PRIVATE);

        btn0 = (Button) findViewById(R.id.btn0);
        btn0.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                etPin.setText(etPin.getText()+""+0);
            }
        });
        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                etPin.setText(etPin.getText()+""+1);
            }
        });
        btn2 = (Button) findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                etPin.setText(etPin.getText()+""+2);
            }
        });
        btn3 = (Button) findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                etPin.setText(etPin.getText()+""+3);
            }
        });
        btn4 = (Button) findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                etPin.setText(etPin.getText()+""+4);
            }
        });
        btn5 = (Button) findViewById(R.id.btn5);
        btn5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                etPin.setText(etPin.getText()+""+5);
            }
        });
        btn6 = (Button) findViewById(R.id.btn6);
        btn6.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                etPin.setText(etPin.getText()+""+6);
            }
        });
        btn7 = (Button) findViewById(R.id.btn7);
        btn7.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                etPin.setText(etPin.getText()+""+7);
            }
        });
        btn8 = (Button) findViewById(R.id.btn8);
        btn8.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                etPin.setText(etPin.getText()+""+8);
            }
        });
        btn9 = (Button) findViewById(R.id.btn9);
        btn9.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                etPin.setText(etPin.getText()+""+9);
            }
        });
        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(etPin.getText().toString().length()-1==-1){
                    etPin.setText("");
                }else if(etPin.getText().toString().length()-1!=0){
                    String getText = etPin.getText().toString();
                    etPin.setText(getText.substring(0,etPin.length()-1));
                }else if(etPin.getText().toString().length()-1==0){
                    etPin.setText("");
                }
            }
        });
        btnDone = (Button) findViewById(R.id.btnDone);
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
        if(lock.equalsIgnoreCase("pin lock")){
            editor.putInt("SysCounter", 3);
            editor.commit();
        }
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

    public void nextView(View view){
        editor = mPref.edit();

        String pass = etPin.getText().toString();
        String savedPass = dbHelper.checkFirstPass();

        if (pass.equalsIgnoreCase(savedPass)) {
            finish();
            lock = dbHelper.checkSecondLock();
            if (lock.equalsIgnoreCase("")) {
                System.out.println(lock);
            } else if (lock.equalsIgnoreCase("fingerprint lock")) {
                Intent newActivity;
                newActivity = new Intent(this, SetupLockFingerprint.class);
                newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(newActivity);
            } else if (lock.equalsIgnoreCase("dual pattern lock")) {
                Intent newActivity;
                newActivity = new Intent(this, SetupLockDualPattern.class);
                newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(newActivity);
            } else if (lock.equalsIgnoreCase("color pattern lock")) {
                Intent newActivity;
                newActivity = new Intent(this, LockColor.class);
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