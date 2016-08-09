package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Page_Views;


import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class CustomMarkerViewPage extends MarkerView
{
    private TextView tvContent;

    public CustomMarkerViewPage (Context context, int layoutResource) {
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
            PageViewsFragment.textViewInfo.setText("Hour " + e.getXIndex());
            PageViewsFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");

        }else if(MainActivity.currentFragment.equals("Week"))
        {
            PageViewsWeekFragment.textViewInfo.setText("Day " + (e.getXIndex() + 1));
            PageViewsWeekFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");

        }else if(MainActivity.currentFragment.equals("Month"))
        {
            PageViewsMonthFragment.textViewInfo.setText("Day of Month " + (e.getXIndex() + 1));
            PageViewsMonthFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");

        }else  if(MainActivity.currentFragment.equals("Year"))
        {
            PageViewsYearFragment.textViewInfo.setText("Month " + (e.getXIndex() + 1));
            PageViewsYearFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
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
