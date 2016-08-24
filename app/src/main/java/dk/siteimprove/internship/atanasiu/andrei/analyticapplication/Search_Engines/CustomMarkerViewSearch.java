package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Search_Engines;


import android.content.Context;
import android.graphics.Color;
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
        if(highlight.getDataSetIndex() == 1)
        {
            tvContent.setTextColor(Color.rgb(181, 0, 97));
            tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, true));
        }else
        {
            tvContent.setTextColor(Color.rgb(5, 184, 198));
            tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, true));
        }

        if(e instanceof BarEntry)
        {
            BarEntry be = (BarEntry) e;

            if(MainActivity.currentFragment.equals("Today"))
            {
                SearchEnginesFragment.textViewInfo.setText("" + SearchEnginesFragment.xAxisLabels.get(be.getXIndex()));

                if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
                {
                    SearchEnginesFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0 ,true) + " Visit");
                }else
                {
                    SearchEnginesFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");
                }

            }else if(MainActivity.currentFragment.equals("Week"))
            {
                SearchEnginesWeekFragment.textViewInfo.setText("" + SearchEnginesWeekFragment.xAxisLabels.get(be.getXIndex()));

                if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
                {
                    SearchEnginesWeekFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0 ,true) + " Visit");
                }else
                {
                    SearchEnginesWeekFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");
                }

            }else if(MainActivity.currentFragment.equals("Month"))
            {
                SearchEnginesMonthFragment.textViewInfo.setText("" + SearchEnginesMonthFragment.xAxisLabels.get(be.getXIndex()));

                if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
                {
                    SearchEnginesMonthFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visit");
                }else
                {
                    SearchEnginesMonthFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");
                }

            }else  if(MainActivity.currentFragment.equals("Year"))
            {
                SearchEnginesYearFragment.textViewInfo.setText("" + SearchEnginesYearFragment.xAxisLabels.get(be.getXIndex()));

                if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
                {
                    SearchEnginesYearFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visit");
                }else
                {
                    SearchEnginesYearFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");
                }

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
