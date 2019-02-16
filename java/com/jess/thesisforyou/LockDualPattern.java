package com.jess.thesisforyou;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class LockDualPattern extends AppCompatActivity {

    String lock = "";
    DatabaseHelper dbHelper;

    PatternLockView plvTop, plvBottom;
    String combination = "";
    Button btnDone;

    SharedPreferences mPref;
    SharedPreferences.Editor editor;
    int attempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_dual_pattern);
        mPref = this.getSharedPreferences("appLockPrefs", MODE_PRIVATE);

        dbHelper = new DatabaseHelper(this);

        plvTop = (PatternLockView) findViewById(R.id.plvTop);
        plvBottom = (PatternLockView) findViewById(R.id.plvBottom);
        plvTop.setInStealthMode(true);
        plvBottom.setInStealthMode(true);
        plvBottom.setVisibility(View.INVISIBLE);
        plvTop.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }
            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
            }
            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                combination = PatternLockUtils.patternToString(plvTop, pattern);
                plvBottom.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCleared() {
            }
        });

        plvBottom.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }
            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }
            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                combination = combination + PatternLockUtils.patternToString(plvBottom, pattern);
                correctPass();
            }
            @Override
            public void onCleared() {

            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
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

    public void correctPass(){


        String savedPass = dbHelper.checkFirstPass();
        editor = mPref.edit();

        if (combination.equalsIgnoreCase(savedPass)) {
            lock = dbHelper.checkSecondLock();
            if (lock.equalsIgnoreCase("")) {
                System.out.println(lock);
            } else if (lock.equalsIgnoreCase("fingerprint lock")) {
                Intent newActivity;
                newActivity = new Intent(this, LockFingerprint.class);
                newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(newActivity);
            } else if (lock.equalsIgnoreCase("pin lock")) {
                Intent newActivity;
                newActivity = new Intent(this, LockPin.class);
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
            if (combination.equalsIgnoreCase(savedPass)) {

                editor.putInt("SysCounter", 1);
                editor.commit();
                System.exit(0);

            }else{
                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                plvTop.setInStealthMode(false);
                plvBottom.setInStealthMode(false);
                plvTop.setViewMode(PatternLockView.PatternViewMode.WRONG);
                plvBottom.setViewMode(PatternLockView.PatternViewMode.WRONG);
                plvTop.setEnabled(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        plvTop.setEnabled(true);
                        plvBottom.setVisibility(View.INVISIBLE);
                        plvTop.clearPattern();
                        plvBottom.clearPattern();
                        plvTop.setInStealthMode(true);
                        plvBottom.setInStealthMode(true);
                    }
                }, 1000);

                attempts++;

                if(attempts == 3){
                    editor.putInt("SysCounter", 3);
                    editor.commit();
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                }
            }
        }
    }
}
