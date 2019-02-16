package com.jess.thesisforyou;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetupLockPin extends AppCompatActivity {

    String lock = "";
    DatabaseHelper dbHelper;

    EditText etPin;
    Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnClear, btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_lock_pin);
        dbHelper = new DatabaseHelper(this);
        etPin = (EditText) findViewById(R.id.etPin);

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

    public void nextView(View view){
        String toastMessage = "Password: " + etPin.getText().toString();
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
        lock = dbHelper.checkSecondLock();
        if(lock.equalsIgnoreCase("")){
            System.out.println(lock);
        }else if(lock.equalsIgnoreCase("fingerprint lock")){
            dbHelper.setFirstPass("1",etPin.getText().toString());
            Intent newActivity;
            newActivity = new Intent(this, SetupQuestions.class);
            startActivity(newActivity);
        }else if(lock.equalsIgnoreCase("dual pattern lock")){
            dbHelper.setFirstPass("1",etPin.getText().toString());
            Intent newActivity;
            newActivity = new Intent(this, SetupLockDualPattern.class);
            startActivity(newActivity);
        }else if(lock.equalsIgnoreCase("color pattern lock")){
            dbHelper.setFirstPass("1",etPin.getText().toString());
            Intent newActivity;
            newActivity = new Intent(this, SetupLockColor.class);
            startActivity(newActivity);
        }else{
            dbHelper.setSecondPass("1", etPin.getText().toString());
            Intent newActivity;
            newActivity = new Intent(this, SetupQuestions.class);
            startActivity(newActivity);
        }
    }
}
