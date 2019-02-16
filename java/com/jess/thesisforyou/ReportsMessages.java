package com.jess.thesisforyou;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jess.thesisforyou.adapters.SpinnerDateAdapter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.jess.thesisforyou.DatabaseHelper.COLUMN_FK_CONTACT_ID;

public class ReportsMessages extends AppCompatActivity {

    public static final String TAG = "MessagesPerContact";
    DatabaseHelper dbHelper;
    TextView tvDate;
    Spinner spnDate;
    SpinnerDateAdapter mSpinnerDateAdapter;
    BarChart bcBlockMessages;
    PieChart pcBlockMessages;
    Button btnHisto, btnPie;
    TextView tvMostBlocked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_messages);

        dbHelper = new DatabaseHelper(this);

        tvDate = (TextView)findViewById(R.id.tvDate);
        spnDate = (Spinner)findViewById(R.id.spnDate);
        ArrayList<Date> dates = getDatesBetween("2/1/2019","2/14/2019");
        mSpinnerDateAdapter = new SpinnerDateAdapter(this,R.layout.adapter_spinner_date_item,dates);
        spnDate.setAdapter(mSpinnerDateAdapter);
        spnDate.setSelection(mSpinnerDateAdapter.getCount()-1);
        spnDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Date selectDate = (Date)spnDate.getSelectedItem();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selectDate.getTime());

                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH)+1;
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                String date = mMonth+"/"+mDay+"/"+mYear;

                //Repopulate based on set date
                barChart(date);
                pieChart(date);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnHisto = (Button)findViewById(R.id.btnHisto);
        btnHisto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bcBlockMessages.setVisibility(View.VISIBLE);
                pcBlockMessages.setVisibility(View.GONE);
            }
        });
        btnPie = (Button)findViewById(R.id.btnPie);
        btnPie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pcBlockMessages.setVisibility(View.VISIBLE);
                bcBlockMessages.setVisibility(View.GONE);
            }
        });

        tvMostBlocked = (TextView)findViewById(R.id.tvMostBlocked);
        String[] hSmsFreqs = dbHelper.readHighestSmsFreq();
        if(hSmsFreqs.length <= 1){
            String hSmsLabel = "Most blocked contact of all time: \n"+hSmsFreqs[0];
            tvMostBlocked.setText(hSmsLabel);
        }else{
            String hSmsLabel = "Most blocked contacts of all time: "+hSmsFreqs.length+" items";
            String listed = "\n";
            for(int i = 0; i < hSmsFreqs.length; i++){
                listed = listed + hSmsFreqs[i] + "\n";
            }
            String combined = hSmsLabel + listed;
            tvMostBlocked.setText(combined);
        }
    }

    /**
     * BarChart initialization
     */

    public void barChart(String date){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        bcBlockMessages = (BarChart)findViewById(R.id.bcBlockMessages);

        bcBlockMessages.setDrawBarShadow(false);
        bcBlockMessages.setPinchZoom(true);
        bcBlockMessages.setDrawGridBackground(true);
        bcBlockMessages.animateY(5000,Easing.EaseInOutQuad);
        Description description = new Description();
        description.setTextColor(Color.GRAY);
        description.setText("Messages");
        bcBlockMessages.setDescription(description);

        //Adding the values (Number of messages)
        Cursor cursor = dbHelper.countSmsByContact("1",date,db);
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        int i = 0;
        while(cursor.moveToNext()){
            int newEntry = Integer.parseInt(cursor.getString(cursor.getColumnIndex("amount")));
            entries.add(new BarEntry(i,newEntry));
            i++;
        }
        Log.d(TAG,"entries size: "+entries.size());
        BarDataSet barDataSet = new BarDataSet(entries, "Blocked");

        //Adding the labels (Contact Names)
        ArrayList<String> labels = new ArrayList<>();
        if(cursor.moveToFirst()){
            String contactID = cursor.getString(cursor.getColumnIndex(COLUMN_FK_CONTACT_ID));
            String contactName = dbHelper.readContactName(contactID);
            labels.add(contactName);
            while(cursor.moveToNext()){
                contactID = cursor.getString(cursor.getColumnIndex(COLUMN_FK_CONTACT_ID));
                contactName = dbHelper.readContactName(contactID);
                labels.add(contactName);
            }
        }
        Object[] oLabels = labels.toArray();
        String[] sLabels = new String[oLabels.length];
        for(i = 0; i < oLabels.length;i++){
            sLabels[i] = (String)oLabels[i];
        }
        Log.d(TAG,"labels size: "+labels.size());
        BarData barData = new BarData(barDataSet);
        XAxis xAxis = bcBlockMessages.getXAxis();
        xAxis.setValueFormatter(new XAxisValueFormatter(sLabels));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        //Used to set values to integer on specific DataSet
        barDataSet.setValueFormatter(new IntegerValueFormatter());
        barDataSet.setColor(Color.argb(255,193,0,0));
        //Used to set values to integer on whole DataSets
        barData.setValueFormatter(new IntegerValueFormatter());

        //Stating the barChart attributes
        bcBlockMessages.setData(barData);


        int chartWidth = cursor.getCount()*50;
        LinearLayout.LayoutParams bcBlockMessagesParams = (LinearLayout.LayoutParams) bcBlockMessages.getLayoutParams();
        bcBlockMessagesParams.width = pxToDp(chartWidth);
        bcBlockMessages.setLayoutParams(bcBlockMessagesParams);
    }

    /**
     * This class is used to convert X number labels to String labels by passing String array of label names
     */

    public class XAxisValueFormatter implements IAxisValueFormatter{

        private String[] values;
        public XAxisValueFormatter(String[] values) {
            this.values = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return values[(int)value];
        }
    }

    /**
     * This class is used to convert values in the chart into Integer format instead of the Decimal
     */

    public class IntegerValueFormatter implements IValueFormatter{
        private DecimalFormat format;

        public IntegerValueFormatter() {
            this.format = new DecimalFormat("###,###,##0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return format.format(value);
        }
    }

    /**
     * This method converts pixel to dp values
     * (Used to adjust width of BarCharts for the horizontal scroll view)
     * @param px receives the pixel value
     * @return dp value
     */

    public int pxToDp(int px){
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float)px*density);
    }

    public void pieChart(String date){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        pcBlockMessages = (PieChart) findViewById(R.id.pcBlockMessages);

        //Attributing the pie chart
        pcBlockMessages.setUsePercentValues(true);
        pcBlockMessages.setDrawHoleEnabled(true);
        pcBlockMessages.setCenterText("Messages");
        pcBlockMessages.setTransparentCircleColor(Color.WHITE);
        pcBlockMessages.setTransparentCircleAlpha(110);
        pcBlockMessages.animateY(5000, Easing.EaseInOutQuad);

        //Retrieve data from the database
        Cursor pieCursorNBlocked = dbHelper.readSms("0",date,db);
        float nBlockedSms = (float)pieCursorNBlocked.getCount();
        Cursor pieCursorBlocked = dbHelper.readSms("1",date,db);
        float blockedSms = (float)pieCursorBlocked.getCount();
        float smsTotal = (float)(nBlockedSms + blockedSms);
        float nBlockedPercent = (nBlockedSms/smsTotal)*100;
        float blockedPercent = (blockedSms/smsTotal)*100;
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(nBlockedPercent,"Non-blocked"));
        entries.add(new PieEntry(blockedPercent,"Blocked"));

        //Initializing attributes of the data set
        PieDataSet dataSet = new PieDataSet(entries,"messages");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        dataSet.setValueFormatter(new PercentageValueFormatter());

        //Initializing data into the chart
        PieData pieData = new PieData();
        pieData.setDataSet(dataSet);
        pcBlockMessages.setData(pieData);
    }
    /**
     * This class is used to convert values in the chart to percentage format
     */
    public class PercentageValueFormatter implements IValueFormatter{
        private DecimalFormat format;

        public PercentageValueFormatter() {
            this.format = new DecimalFormat("###,###,##0.0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return format.format(value)+" %";
        }
    }

    private static ArrayList<Date> getDatesBetween(String from, String to){
        ArrayList<Date> dates = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date dateFrom = null;
        Date dateTo = null;
        try{
            dateFrom = dateFormat.parse(from);
            dateTo = dateFormat.parse(to);
        }catch(ParseException e){
            e.printStackTrace();
        }
        Calendar calFrom = Calendar.getInstance();
        calFrom.setTime(dateFrom);

        Calendar calTo = Calendar.getInstance();
        calTo.setTime(dateTo);

        while(!calFrom.after(calTo)){
            dates.add(calFrom.getTime());
            calFrom.add(Calendar.DATE,1);
        }
        return dates;
    }
}
