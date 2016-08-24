package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Traffic_Sources;


import android.content.Context;
import android.graphics.Color;
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
        if(highlight.getDataSetIndex() == 1)
        {
            tvContent.setTextColor(Color.rgb(181, 0, 97));
            tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, true));
        }else
        {
            tvContent.setTextColor(Color.rgb(5, 184, 198));
            tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, true));
        }

        if(MainActivity.currentFragment.equals("Today"))
        {
            TrafficSourcesFragment.textViewInfo.setText("" + TrafficSourcesFragment.xAxisLabels.get(e.getXIndex()));

            if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
            {
                TrafficSourcesFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0 , true) + " Visit");
            }else
            {
                TrafficSourcesFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
            }
        }else if(MainActivity.currentFragment.equals("Week"))
        {
            TrafficSourcesWeekFragment.textViewInfo.setText("" + TrafficSourcesWeekFragment.xAxisLabels.get(e.getXIndex()));

            if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
            {
                TrafficSourcesWeekFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visit");
            }else
            {
                TrafficSourcesWeekFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
            }

        }else if(MainActivity.currentFragment.equals("Month"))
        {
            TrafficSourcesMonthFragment.textViewInfo.setText("" + TrafficSourcesMonthFragment.xAxisLabels.get(e.getXIndex()));

            if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
            {
                TrafficSourcesMonthFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0 ,true) + " Visit");
            }else
            {
                TrafficSourcesMonthFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
            }

        }else  if(MainActivity.currentFragment.equals("Year"))
        {
            TrafficSourcesYearFragment.textViewInfo.setText("" + TrafficSourcesYearFragment.xAxisLabels.get(e.getXIndex()));

            if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
            {
                TrafficSourcesYearFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visit");
            }else
            {
                TrafficSourcesYearFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
            }
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
