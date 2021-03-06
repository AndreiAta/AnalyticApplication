package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.visits;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import org.joda.time.DateTime;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class CustomMarkerViewVisits extends MarkerView
{
    private TextView tvContent;
    ArrayList<String> monthNames;
    int monthCounter;
    int offset;

    public CustomMarkerViewVisits (Context context, int layoutResource) {
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
            offset = VisitsMonthFragment.previousPeriodOffset;
        }else
        {
            tvContent.setTextColor(Color.rgb(5, 184, 198));
            tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, true));
            monthCounter = MainActivity.monthPeriodCounter;
            offset = VisitsMonthFragment.currentPeriodOffset;
        }

        if(MainActivity.currentFragment.equals("Today"))
        {
            VisitsFragment.textViewInfo.setText("Hour " + e.getXIndex());
            if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
            {
                VisitsFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visit");
            }else
            {
                VisitsFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
            }

        }
        else if(MainActivity.currentFragment.equals("Week"))
        {
            VisitsWeekFragment.textViewInfo.setText("" + VisitsWeekFragment.tableWeekDays.get(e.getXIndex()));

            if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
            {
                VisitsWeekFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visit");
            }else
            {
                VisitsWeekFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
            }
        }
        else if(MainActivity.currentFragment.equals("Month"))
        {
            VisitsMonthFragment.textViewInfo.setText((e.getXIndex() + 1 - offset) + dateFixer(e.getXIndex() + 1 - offset)+ " of "
                    + today.minusMonths(monthCounter).toString("MMMMM"));

            if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
            {
                VisitsMonthFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visit");
            }else
            {
                VisitsMonthFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
                Log.i("OFFSET", String.valueOf(offset));
            }
        }
        else if(MainActivity.currentFragment.equals("Year"))
        {
            VisitsYearFragment.textViewInfo.setText("" + getMonth(e.getXIndex()));

            if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
            {
                VisitsYearFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visit");
            }else
            {
                VisitsYearFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
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
