package com.jess.thesisforyou.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jess.thesisforyou.R;

/**
 * Created by USER on 2/8/2019.
 */

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    
    public SliderAdapter(Context context){
        this.context = context;
    }

    /**
     * Layout variables instantiation
     */

    public int[] slide_images = {
            R.mipmap.ic_slider_lock,
            R.mipmap.ic_slider_pass,
            R.mipmap.ic_slider_security
    };
    public String[] slide_headings = {
            "Choose Lock Types",
            "Set Password",
            "Set Security Question"
    };
    public String[] slide_description = {
            "Choose lock types that are available to use in Mobile Guard that would be prompted when locked applications are accessed",
            "Set the password for the chosen lock types in step one that would be used to open the locked applications when accessed",
            "Set the security questions which would be asked upon changing or resetting passwords that was set for specific lock types"
    };
    public  String[] slide_step = {
            "STEP ONE",
            "STEP TWO",
            "STEP THREE"
    };

    /**
     * This method returns number of layouts on the slider
     * @return int value of number of layouts
     */

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout) object;
    }

    /**
     * This method instantiates the new layouts for the slider
     *
     * @param container retrieves the container to inflate
     * @param position as reference for the next layout
     * @return new view;
     */

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout, container, false);

        //Retrieve and instantiate layout views
        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.heading1);
        TextView slideDescription = (TextView) view.findViewById(R.id.textdesc);
        TextView slideStep = (TextView) view.findViewById(R.id.step);

        //Manipulate views using current position as basis
        slideStep.setText(slide_step[position]);
        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_description[position]);

        //Add the new view to the container
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((RelativeLayout)object);
    }
}
