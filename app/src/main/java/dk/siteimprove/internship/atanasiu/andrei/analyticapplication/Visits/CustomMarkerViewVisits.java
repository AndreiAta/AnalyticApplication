package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Visits;


import android.content.Context;
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

    public CustomMarkerViewVisits (Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerView only displays a textView
        tvContent = (TextView) findViewById(R.id.tvContent);

    }

    @Override
    public void refreshContent(Entry e, Highlight highlight)
    {
        tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, true)); // set the entry-value as the display text
        DateTime today = new DateTime();

        if(MainActivity.currentFragment.equals("Today"))
        {
            VisitsFragment.textViewInfo.setText("Hour " + e.getXIndex());
            VisitsFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
        }
        else if(MainActivity.currentFragment.equals("Week"))
        {
            VisitsWeekFragment.textViewInfo.setText("" + VisitsWeekFragment.tableWeekDays.get(e.getXIndex()));
            VisitsWeekFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
        }
        else if(MainActivity.currentFragment.equals("Month"))
        {
            VisitsMonthFragment.textViewInfo.setText((e.getXIndex() + 1) + dateFixer(e.getXIndex() + 1)+ " of "
                    + today.toString("MMMMM"));
            VisitsMonthFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
        }
        else if(MainActivity.currentFragment.equals("Year"))
        {
            VisitsYearFragment.textViewInfo.setText("" + getMonth(e.getXIndex()));
            VisitsYearFragment.textViewTotal.setText(Utils.formatNumber(e.getVal(), 0, true) + " Visits");
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
