package dk.siteimprove.internship.atanasiu.andrei.analyticapplication;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Highlight;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Visits.VisitsFragment;

public class CustomMarkerView extends MarkerView
{

    private TextView tvContent;
    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);

    }

    @Override
    public void refreshContent(Entry e, int dataSetIndex)
    {
        tvContent.setText("" + e.getVal()); // set the entry-value as the display text
        VisitsFragment.textViewInfo.setText("Hour " + e.getXIndex());
        VisitsFragment.textViewTotal.setText("" + e.getVal());
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
