package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.page_views;


import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import org.joda.time.DateTime;

import java.text.DateFormatSymbols;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class CustomMarkerViewPage extends MarkerView
{
    private TextView tvContent;
    int monthCounter;
    int offset;

    public CustomMarkerViewPage (Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerView only displays a textView
        tvContent = (TextView) findViewById(R.id.tvContent);

    }

    @Override
    public void refreshContent(Entry e, Highlight highlight)
    {
        DateTime today = new DateTime();
        if(highlight.getDataSetIndex() == 1)
        {
            tvContent.setTextColor(Color.rgb(181, 0, 97));
            tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, true));
            monthCounter = MainActivity.monthPeriodCounter + 1;
            offset = PageViewsMonthFragment.previousPeriodOffset;
        }else
        {
            tvContent.setTextColor(Color.rgb(5, 184, 198));
            tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, true));
            monthCounter = MainActivity.monthPeriodCounter;
            offset = PageViewsMonthFragment.currentPeriodOffset;
        }

        if(MainActivity.currentFragment.equals("Today"))
        {
            PageViewsFragment.textViewInfo.setText("Hour " + e.getXIndex());

            if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
            {
                PageViewsFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0 ,true) + " Page View");
            }else
            {
                PageViewsFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Page Views");
            }

        }else if(MainActivity.currentFragment.equals("Week"))
        {
            PageViewsWeekFragment.textViewInfo.setText(PageViewsWeekFragment.tableWeekDays.get(e.getXIndex()) + "");

            if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
            {
                PageViewsWeekFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0 , true) + " Page View");
            }else
            {
                PageViewsWeekFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Page Views");
            }

        }else if(MainActivity.currentFragment.equals("Month"))
        {
            PageViewsMonthFragment.textViewInfo.setText((e.getXIndex() + 1 - offset) + dateFixer(e.getXIndex() + 1 - offset)
                    + " of " + today.minusMonths(monthCounter).toString("MMMMM"));

            if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
            {
                PageViewsMonthFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0 ,true) + " Page View");
            }else
            {
                PageViewsMonthFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Page Views");
            }

        }else  if(MainActivity.currentFragment.equals("Year"))
        {
            PageViewsYearFragment.textViewInfo.setText("" + getMonth(e.getXIndex()));

            if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
            {
                PageViewsYearFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Page Viewgit add .") ;
            }else
            {
                PageViewsYearFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Page Views");
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

    public String dateFixer(int i)
    {
        if(i == 1)
        {
            return "st";
        }
        else if(i == 2)
        {
            return  "nd";
        }
        else if(i == 3)
        {
            return "rd";
        }
        else return "th";
    }

    public String getMonth(int month)
    {
        return new DateFormatSymbols().getMonths()[month ];
    }

}
