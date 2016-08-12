package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Traffic_Sources;


import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class CustomMarkerViewTraffic extends MarkerView
{
    private TextView tvContent;

    public CustomMarkerViewTraffic (Context context, int layoutResource) {
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
            TrafficSourcesFragment.textViewInfo.setText("" + TrafficSourcesFragment.xAxisLabels.get(e.getXIndex()));
            TrafficSourcesFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
        }else if(MainActivity.currentFragment.equals("Week"))
        {
            TrafficSourcesWeekFragment.textViewInfo.setText("" + TrafficSourcesWeekFragment.xAxisLabels.get(e.getXIndex()));
            TrafficSourcesWeekFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");

        }else if(MainActivity.currentFragment.equals("Month"))
        {
            TrafficSourcesMonthFragment.textViewInfo.setText("" + TrafficSourcesMonthFragment.xAxisLabels.get(e.getXIndex()));
            TrafficSourcesMonthFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");

        }else  if(MainActivity.currentFragment.equals("Year"))
        {
            TrafficSourcesYearFragment.textViewInfo.setText("" + TrafficSourcesYearFragment.xAxisLabels.get(e.getXIndex()));
            TrafficSourcesYearFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");

        }

    }

    @Override
    public int getXOffset(float xpos)
    {
        return 0;
    }

    @Override
    public int getYOffset(float ypos)
    {
        return 0;
    }

}
