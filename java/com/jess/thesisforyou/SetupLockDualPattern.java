package com.jess.thesisforyou;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class SetupLockDualPattern extends AppCompatActivity {

    String lock = "";
    DatabaseHelper dbHelper;

    PatternLockView plvTop, plvBottom;
    String firstPattern = "", secondPattern= "";
    String combination = "";
    Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_lock_dual_pattern);
        dbHelper = new DatabaseHelper(this);

        plvTop = (PatternLockView) findViewById(R.id.plvTop);
        plvBottom = (PatternLockView) findViewById(R.id.plvBottom);

        plvTop.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                firstPattern = PatternLockUtils.patternToString(plvTop, pattern);
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
                secondPattern = PatternLockUtils.patternToString(plvBottom, pattern);
            }

            @Override
            public void onCleared() {

            }
        });
        btnDone = (Button)findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firstPattern.isEmpty()){
                    plvTop.requestFocus();
                    Toast.makeText(SetupLockDualPattern.this, "First pattern is empty!",Toast.LENGTH_LONG).show();
                }else if(secondPattern.isEmpty()){
                    plvBottom.requestFocus();
                    Toast.makeText(SetupLockDualPattern.this, "Second pattern is empty!",Toast.LENGTH_LONG).show();
                }else{
                    nextView(view);
                }
            }
        });
    }

    public void nextView(View view){

        combination = firstPattern + secondPattern;
        Toast.makeText(getApplicationContext(), combination, Toast.LENGTH_SHORT).show();

        lock = dbHelper.checkSecondLock();
        if(lock.equalsIgnoreCase("")){
            System.out.println(lock);
        }else if(lock.equalsIgnoreCase("fingerprint lock")){
            dbHelper.setFirstPass("1", combination);
            Intent newActivity;
            newActivity = new Intent(this, SetupQuestions.class);
            startActivity(newActivity);
        }else if(lock.equalsIgnoreCase("pin lock")){
            dbHelper.setFirstPass("1", combination);
            Intent newActivity;
            newActivity = new Intent(this, SetupLockPin.class);
            startActivity(newActivity);
        }else if(lock.equalsIgnoreCase("color pattern lock")){
            dbHelper.setFirstPass("1", combination);
            Intent newActivity;
            newActivity = new Intent(this, SetupLockColor.class);
            startActivity(newActivity);
        }else{
            dbHelper.setSecondPass("1", combination);
            Intent newActivity;
            newActivity = new Intent(this, SetupQuestions.class);
            startActivity(newActivity);
        }

    }
}
