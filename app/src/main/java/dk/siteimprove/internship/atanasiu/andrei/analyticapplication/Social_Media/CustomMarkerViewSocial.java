package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.social_media;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class CustomMarkerViewSocial extends MarkerView
{
    private TextView tvContent;

    public CustomMarkerViewSocial (Context context, int layoutResource) {
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

        Log.i("EEEEEEE", e.toString() + highlight.toString());

        if(e instanceof BarEntry)
        {
            BarEntry be = (BarEntry) e;

            if(MainActivity.currentFragment.equals("Today"))
            {
                SocialMediaFragment.textViewInfo.setText("" + SocialMediaFragment.xAxisLabels.get(be.getXIndex()));

                if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
                {
                    SocialMediaFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visit");
                }else
                {
                    SocialMediaFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");
                }
            }else if(MainActivity.currentFragment.equals("Week"))
            {
                SocialMediaWeekFragment.textViewInfo.setText("" + SocialMediaWeekFragment.xAxisLabels.get(be.getXIndex()));

                if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
                {
                    SocialMediaWeekFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0 ,true) + " Visit");
                }else
                {
                    SocialMediaWeekFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");
                }

            }else if(MainActivity.currentFragment.equals("Month"))
            {
                SocialMediaMonthFragment.textViewInfo.setText("" + SocialMediaMonthFragment.xAxisLabels.get(be.getXIndex()));

                if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
                {
                    SocialMediaMonthFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0 ,true) + " Visit");
                }else
                {
                    SocialMediaMonthFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");
                }

            }else  if(MainActivity.currentFragment.equals("Year"))
            {
                SocialMediaYearFragment.textViewInfo.setText("" + "" + SocialMediaYearFragment.xAxisLabels.get(be.getXIndex()));

                if(Utils.formatNumber(e.getVal(), 0, true).equals("1"))
                {
                    SocialMediaYearFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0 ,true) + " Visit");
                }else
                {
                    SocialMediaYearFragment.textViewTotal.setText(Utils.formatNumber(be.getVal(), 0, true) + " Visits");
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
