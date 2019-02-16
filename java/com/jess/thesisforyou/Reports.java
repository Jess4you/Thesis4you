package com.jess.thesisforyou;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Reports extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

    }
    public void openReportsMessages(View view){
        startActivity(new Intent(Reports.this, ReportsMessages.class));
    }
    public void openReportsCalls(View view){
        startActivity(new Intent(Reports.this, ReportsCalls.class));
    }
}
