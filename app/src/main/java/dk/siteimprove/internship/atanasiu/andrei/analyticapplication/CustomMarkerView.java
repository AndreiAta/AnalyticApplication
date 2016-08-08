package dk.siteimprove.internship.atanasiu.andrei.analyticapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Highlight;


import static dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R.id.textViewInfo;

public class CustomMarkerView extends MarkerView
{

    private TextView tvContent;
    private TextView textViewInfo;

    public CustomMarkerView (Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerView only displays a textView
        tvContent = (TextView) findViewById(R.id.tvContent);

    }

    @Override
    public void refreshContent(Entry e, int dataSetIndex)
    {
        tvContent.setText("" + e.getVal()); // set the entry-value as the display text

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
