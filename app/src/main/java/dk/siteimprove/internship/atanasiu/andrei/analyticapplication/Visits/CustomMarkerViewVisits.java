package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Visits;


import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class CustomMarkerViewVisits extends MarkerView
{
    private TextView tvContent;

    public CustomMarkerViewVisits (Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerView only displays a textView
        tvContent = (TextView) findViewById(R.id.tvContent);

    }

    @Override
    public void refreshContent(Entry e, Highlight highlight)
    {
        tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, true)); // set the entry-value as the display text

        if(MainActivity.currentFragment.equals("Today"))
        {
            VisitsFragment.textViewInfo.setText("Hour " + e.getXIndex());
            VisitsFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
        }
        else if(MainActivity.currentFragment.equals("Week"))
        {
            VisitsWeekFragment.textViewInfo.setText("Day " + (e.getXIndex() + 1));
            VisitsWeekFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
        }
        else if(MainActivity.currentFragment.equals("Month"))
        {
            VisitsMonthFragment.textViewInfo.setText("Day of month " + (e.getXIndex() + 1));
            VisitsMonthFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
        }
        else if(MainActivity.currentFragment.equals("Year"))
        {
            VisitsYearFragment.textViewInfo.setText("Month " + (e.getXIndex() + 1));
            VisitsYearFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
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
