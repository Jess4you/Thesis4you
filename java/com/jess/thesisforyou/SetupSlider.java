package com.jess.thesisforyou;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jess.thesisforyou.adapters.SliderAdapter;

/**
 * Created by USER on 2/8/2019.
 */

public class SetupSlider extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private SliderAdapter sliderAdapter;

    private Button finish;

    private int mCurrentPage;

    private TextView[] mdots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_slider);

        finish = (Button) findViewById(R.id.finish);

        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextView();
            }
        });
    }

    /**
     * This method sets the current slider position dot to white
     *
     * @param position retrieves current slider position
     */
    public void addDotsIndicator(int position){
        mdots = new TextView[3];
        mDotLayout.removeAllViews();

        for(int i = 0; i < mdots.length; i++){
            mdots[i] = new TextView(this);
            mdots[i].setText(Html.fromHtml("&#8226;"));
            mdots[i].setTextSize(35);
            mdots[i].setTextColor(getResources().getColor(R.color.colorWhite, null));
            mDotLayout.addView(mdots[i]);
        }
        if(mdots.length>0){
            mdots[position].setTextColor(getResources().getColor(R.color.colorGray, null));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener(){
        @Override
        public void onPageScrolled(int i , float v, int i1){

        }
        @Override
        public void onPageSelected(int i){
            addDotsIndicator(i);
            mCurrentPage = i;

            if(mCurrentPage == 2){
                finish.setEnabled(true);
            }
            else{
                finish.setEnabled(false);
            }
        }
        @Override
        public void onPageScrollStateChanged(int i){

        }
    };
    public void nextView(){
        Intent newActivity;
        newActivity = new Intent(this, SetupLocks.class);
        startActivity(newActivity);
    }
}
