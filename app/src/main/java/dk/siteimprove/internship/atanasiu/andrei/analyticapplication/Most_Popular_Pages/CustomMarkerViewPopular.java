package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.most_popular_pages;


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

public class CustomMarkerViewPopular extends MarkerView
{
    private TextView tvContent;

    public CustomMarkerViewPopular (Context context, int layoutResource) {
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
                PopPagesFragment.textViewInfo.setText("" + PopPagesFragment.xAxis.get(be.getXIndex()));

                if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
                {
                    PopPagesFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visit");
                }else
                {
                    PopPagesFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");
                }

            }else if(MainActivity.currentFragment.equals("Week"))
            {
                PopPagesWeekFragment.textViewInfo.setText("" + PopPagesWeekFragment.xAxis.get(be.getXIndex()));

                if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
                {
                    PopPagesWeekFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0 ,true) + " Visit");
                }else
                {
                    PopPagesWeekFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");
                }

            }else if(MainActivity.currentFragment.equals("Month"))
            {
                PopPagesMonthFragment.textViewInfo.setText("" + PopPagesMonthFragment.xAxis.get(be.getXIndex()));

                if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
                {
                    PopPagesMonthFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visit");
                }else
                {
                    PopPagesMonthFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");
                }

            }else  if(MainActivity.currentFragment.equals("Year"))
            {
                PopPagesYearFragment.textViewInfo.setText("" + PopPagesYearFragment.xAxis.get(be.getXIndex()));

                if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
                {
                    PopPagesYearFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0 ,true) + " Visit");
                }else
                {
                    PopPagesYearFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");
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
