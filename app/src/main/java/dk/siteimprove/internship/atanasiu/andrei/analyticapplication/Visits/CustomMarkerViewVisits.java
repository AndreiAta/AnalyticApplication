package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Visits;


import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class CustomMarkerViewVisits extends MarkerView
{
    private TextView tvContent;
    private TextView textViewInfo;

    public CustomMarkerViewVisits (Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerView only displays a textView
        tvContent = (TextView) findViewById(R.id.tvContent);

    }

    @Override
    public void refreshContent(Entry e, Highlight highlight)
    {
        tvContent.setText("" + e.getVal()); // set the entry-value as the display text

        if(MainActivity.currentFragment.equals("Today"))
        {
            VisitsFragment.textViewInfo.setText("Hour" + e.getXIndex());
            VisitsFragment.textViewTotal.setText(e.getVal() + " Visits");
        }else if(MainActivity.currentFragment.equals("Week"))
        {
            VisitsWeekFragment.textViewInfo.setText("Day " + e.getXIndex());
            VisitsWeekFragment.textViewTotal.setText(e.getVal() + " Visits");
        }else if(MainActivity.currentFragment.equals("Month"))
        {
            VisitsMonthFragment.textViewInfo.setText("Day of month " + e.getXIndex());
            VisitsMonthFragment.textViewTotal.setText(e.getVal() + " Visits");
        }else if(MainActivity.currentFragment.equals("Year"))
        {
            VisitsYearFragment.textViewInfo.setText("Month " + e.getXIndex());
            VisitsYearFragment.textViewTotal.setText(e.getVal() + " Visits");
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
