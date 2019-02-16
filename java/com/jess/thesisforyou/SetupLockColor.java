package com.jess.thesisforyou;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetupLockColor extends AppCompatActivity {

    String lock = "";
    DatabaseHelper dbHelper;

    EditText etColor;
    Button btnRed, btnGreen, btnBlue, btnYellow, btnClear, btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_lock_color);
        dbHelper = new DatabaseHelper(this);
        etColor = (EditText) findViewById(R.id.etColor);

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

    public void nextView(View view){
        Toast.makeText(this, etColor.getText(), Toast.LENGTH_SHORT).show();
        lock = dbHelper.checkSecondLock();
        if(lock.equalsIgnoreCase("")){
            System.out.println(lock);
        }else if(lock.equalsIgnoreCase("fingerprint lock")){
            dbHelper.setFirstPass("1", etColor.getText().toString());
            Intent newActivity;
            newActivity = new Intent(this, SetupQuestions.class);
            startActivity(newActivity);
        }else if(lock.equalsIgnoreCase("pin lock")){
            dbHelper.setFirstPass("1", etColor.getText().toString());
            Intent newActivity;
            newActivity = new Intent(this, SetupLockPin.class);
            startActivity(newActivity);
        }else if(lock.equalsIgnoreCase("dual pattern lock")){
            dbHelper.setFirstPass("1", etColor.getText().toString());
            Intent newActivity;
            newActivity = new Intent(this, SetupLockDualPattern.class);
            startActivity(newActivity);
        }else{
            dbHelper.setSecondPass("1", etColor.getText().toString());
            Intent newActivity;
            newActivity = new Intent(this, SetupQuestions.class);
            startActivity(newActivity);
        }
    }
}
