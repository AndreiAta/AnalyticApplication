package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Social_Media;


import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class CustomMarkerViewSocial extends MarkerView
{
    private TextView tvContent;

    public CustomMarkerViewSocial (Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerView only displays a textView
        tvContent = (TextView) findViewById(R.id.tvContent);

    }

    @Override
    public void refreshContent(Entry e, Highlight highlight)
    {

        tvContent.setText("" + e.getVal()); // set the entry-value as the display text
        Log.i("XXXXXX", "METHOD CALLED");

        if(e instanceof BarEntry)
        {
            BarEntry be = (BarEntry) e;

            if(MainActivity.currentFragment.equals("Today"))
            {
                SocialMediaFragment.textViewInfo.setText("" + SocialMediaFragment.xAxisLabels.get(e.getXIndex()));
                SocialMediaFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");
            }else if(MainActivity.currentFragment.equals("Week"))
            {
                SocialMediaWeekFragment.textViewInfo.setText("" + SocialMediaWeekFragment.xAxisLabels.get(e.getXIndex()));
                SocialMediaWeekFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");

            }else if(MainActivity.currentFragment.equals("Month"))
            {
                SocialMediaMonthFragment.textViewInfo.setText("" + SocialMediaMonthFragment.xAxisLabels.get(e.getXIndex()));
                SocialMediaMonthFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");

            }else  if(MainActivity.currentFragment.equals("Year"))
            {
                SocialMediaYearFragment.textViewInfo.setText("" + "" + SocialMediaYearFragment.xAxisLabels.get(e.getXIndex()));
                SocialMediaYearFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");
            }
        }
    }


    @Override
    public int getXOffset() {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset() {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }
}
