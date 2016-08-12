package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Search_Engines;


import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class CustomMarkerViewSearch extends MarkerView
{
    private TextView tvContent;

    public CustomMarkerViewSearch (Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerView only displays a textView
        tvContent = (TextView) findViewById(R.id.tvContent);

    }

    @Override
    public void refreshContent(Entry e, Highlight highlight)
    {
        tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, true)); // set the entry-value as the display text

        if(e instanceof BarEntry)
        {
            BarEntry be = (BarEntry) e;

            if(MainActivity.currentFragment.equals("Today"))
            {
                SearchEnginesFragment.textViewInfo.setText("" + SearchEnginesFragment.xAxisLabels.get(be.getXIndex()));
                SearchEnginesFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");

            }else if(MainActivity.currentFragment.equals("Week"))
            {
                SearchEnginesWeekFragment.textViewInfo.setText("" + SearchEnginesWeekFragment.xAxisLabels.get(be.getXIndex()));
                SearchEnginesWeekFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");

            }else if(MainActivity.currentFragment.equals("Month"))
            {
                SearchEnginesMonthFragment.textViewInfo.setText("" + SearchEnginesMonthFragment.xAxisLabels.get(be.getXIndex()));
                SearchEnginesMonthFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");

            }else  if(MainActivity.currentFragment.equals("Year"))
            {
                SearchEnginesYearFragment.textViewInfo.setText("" + SearchEnginesYearFragment.xAxisLabels.get(be.getXIndex()));
                SearchEnginesYearFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");

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
