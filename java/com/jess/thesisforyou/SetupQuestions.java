package com.jess.thesisforyou;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jess.thesisforyou.fragments.ContactGroupsFragment;
import com.jess.thesisforyou.models.ContactGroup;

public class SetupQuestions extends AppCompatActivity {

    public static final String TAG = "SetupQuestions";

    DatabaseHelper dbHelper;

    EditText etFirstQuestion, etFirstAnswer, etSecondQuestion, etSecondAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_questions);
        dbHelper = new DatabaseHelper(this);
    }

    public void nextView(View view){

        etFirstQuestion = (EditText) findViewById(R.id.etFirstQuestion);
        etFirstAnswer = (EditText) findViewById(R.id.etFirstAnswer);
        etSecondQuestion = (EditText) findViewById(R.id.etSecondQuestion);
        etSecondAnswer = (EditText) findViewById(R.id.etSecondAnswer);

        if(!hasError()){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(dbHelper.insertToSQ(etFirstQuestion.getText().toString()
                            , etFirstAnswer.getText().toString()
                            , etSecondQuestion.getText().toString()
                            , etSecondAnswer.getText().toString())){
                        Toast.makeText(SetupQuestions.this,"Security Questions registered!",Toast.LENGTH_LONG).show();

                        //Proceed to MainMenu after successful insert
                        Intent newActivity;
                        newActivity = new Intent(SetupQuestions.this, MainMenu.class);
                        startActivity(newActivity);
                    }else{
                        Toast.makeText(SetupQuestions.this,"Registration failed. Try again later", Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
        }else{
            Toast.makeText(SetupQuestions.this,"Registration failed. Try again later", Toast.LENGTH_LONG).show();
        }

    }
    public boolean hasError(){
        boolean error = false;
        if(etFirstQuestion.getText().toString().isEmpty()){
            etFirstQuestion.setError("Please enter the first question!");
            error = true;
        }
        if(etFirstAnswer.getText().toString().isEmpty()){
            etFirstAnswer.setError("Please enter the first answer!");
            error = true;
        }
        if(etSecondQuestion.getText().toString().isEmpty()){
            etSecondQuestion.setError("Please enter the second question!");
            error = true;
        }
        if(etSecondAnswer.getText().toString().isEmpty()){
            etSecondAnswer.setError("Please enter the second answer!");
            error = true;
        }
        return error;
    }
}
