package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Visits;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class VisitsWeekFragment extends Fragment
{
    private OnFragmentInteractionListener mListener;
    ProgressBar progressBar;
    LineChart chart;
    ArrayList<LineDataSet> dataSets;
    ArrayList<Entry> valueSet1;
    ArrayList<Entry> valueSet2;
    String API_URL = "";
    Boolean secondCall = false;
    int dayOfWeek;
    String thisWeekCompareMonDate;
    String lastWeekCompareMonDate;
    DateTime startOfWeek;
    String lastSunday;

    public VisitsWeekFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        dayOfWeek = new DateTime().getDayOfWeek();
        DateTime currentDay = new DateTime();
        String currentDate = currentDay.toString("yyyy-MM-dd");
        currentDate = currentDate.replace("-","");

        startOfWeek = new DateTime().minusDays(dayOfWeek - 1);
        lastSunday = startOfWeek.minusDays(1).toString("dd");
        lastWeekCompareMonDate = startOfWeek.minusDays(7).toString("dd");
        String mondayDate = startOfWeek.toString("yyyy-MM-dd");
        mondayDate = mondayDate.replace("-", "");
        thisWeekCompareMonDate = startOfWeek.toString("dd");
        String period = mondayDate + "_" + currentDate;

        Log.i("DAY OF WEEK", String.valueOf(period));

        if(MainActivity.API_ID != null)
        {
            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                    "/analytics/behavior/visits_by_monthday?page=1&page_size=10&period=" + period;

        }else
        {
            //TODO error message no Site selected
        }
        View rootView = inflater.inflate(R.layout.fragment_visits_week, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        new RetrieveFeedTask().execute();
        chart = (LineChart) rootView.findViewById(R.id.chart);

        return  rootView;

    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }
    class RetrieveFeedTask extends AsyncTask<Void, Void, String> //This is a Class
    {
        private Exception exception;

        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls)
        {

            try
            {
                URL url = new URL(API_URL );
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String credentials = MainActivity.API_EMAIL + ":" + MainActivity.API_KEY;
                String auth = "Basic" + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                urlConnection.setRequestProperty("Authorization",auth);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept", "application/json");
                try
                {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null)
                    {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }
        private ArrayList<String> getXAxisValues() {
            ArrayList<String> xAxis = new ArrayList<>();

            xAxis.add("Mon");
            xAxis.add("Tue");
            xAxis.add("Wed");
            xAxis.add("Thu");
            xAxis.add("Fri");
            xAxis.add("Sat");
            xAxis.add("Sun");

            return xAxis;
        }

        private void drawGraph()
        {
            LineData data = new LineData(getXAxisValues(), dataSets);
            Log.i("DATA SETS", dataSets.toString());
            chart.setData(data);
            chart.setDescription("");
            chart.animateXY(2000, 2000);
            chart.invalidate();
            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setSpaceBetweenLabels(0);
            xAxis.setAdjustXLabels(false);
            data.setValueTextSize(8f);
        }

        protected void onPostExecute(String response)
        {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);

            try
            {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray items = object.getJSONArray("items");
                Integer thisMonDate = Integer.parseInt(thisWeekCompareMonDate);
                Integer lastMonDate = Integer.parseInt(lastWeekCompareMonDate);
                Log.i("COMPARECOUNTER", String.valueOf(thisMonDate));
                int totalDays = items.length();
                int placementOnXAxis = 0;
                if(secondCall)
                {
                    valueSet2 = new ArrayList<>();
                }else
                {
                    valueSet1 = new ArrayList<>();
                }

                if(totalDays == 0)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
                }else
                {
                    for (Integer i = 0; i < totalDays; i++)
                    {
                        int visits = items.getJSONObject(i).getInt("visits");
                        int day_of_month = items.getJSONObject(i).getInt("day_of_month");

                        //Check if you are doing the current week or last week
                        // then check if any entries are missing and create then
                        if (secondCall)
                        {
                            //Last Week
                            while(day_of_month != lastMonDate)
                            {
                                int stopValue = lastMonDate;
                                for(int j = stopValue; j < day_of_month; j++)
                                {
                                    Entry entry = new Entry(0, placementOnXAxis);
                                    valueSet2.add(entry);
                                    lastMonDate++;
                                    placementOnXAxis++;
                                }
                            }
                            if(day_of_month == lastMonDate)
                            {
                                Entry entry = new Entry((float)visits, placementOnXAxis);
                                valueSet2.add(entry);
                                lastMonDate++;
                                placementOnXAxis++;
                            }

                            while(lastMonDate <= Integer.parseInt(lastSunday) && i == (totalDays - 1))
                            {
                                Entry entry = new Entry(0, placementOnXAxis);
                                valueSet2.add(entry);
                                lastMonDate++;
                                placementOnXAxis++;
                            }
                        } else  //Current Week
                        {
                           while(day_of_month != thisMonDate)
                           {
                                int stopValue = thisMonDate;
                               for(int j = stopValue; j < day_of_month; j++)
                               {
                                   Entry entry = new Entry(0, placementOnXAxis);
                                   valueSet1.add(entry);
                                   thisMonDate++;
                                   placementOnXAxis++;
                               }
                           }
                            if(day_of_month == thisMonDate)
                            {
                                Entry entry = new Entry((float)visits, placementOnXAxis);
                                valueSet1.add(entry);
                                thisMonDate++;
                                placementOnXAxis++;
                            }
                        }
                    }

                    if (secondCall)
                    {
                        LineDataSet lineDataSet2 = new LineDataSet(valueSet2, "LAST WEEK");
                        lineDataSet2.setColor(Color.rgb(5, 184, 198)); //TODO USE R.COLOR
                        lineDataSet2.setDrawFilled(true);
                        lineDataSet2.setFillColor(Color.rgb(5, 184, 198));
                        lineDataSet2.setFillAlpha(15);
                        dataSets.add(lineDataSet2);
                        drawGraph();

                        secondCall = false;
                    } else
                    {
                        dataSets = new ArrayList<>();
                        LineDataSet lineDataSet1 = new LineDataSet(valueSet1, "THIS WEEK");
                        lineDataSet1.setColor(Color.rgb(181, 0, 97));
                        lineDataSet1.setDrawFilled(true);
                        lineDataSet1.setFillColor(Color.rgb(181, 0, 97));
                        lineDataSet1.setFillAlpha(15);
                        dataSets.add(lineDataSet1);
                        secondCall = true;
                        API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                                "/analytics/behavior/visits_by_monthday?page=1&page_size=10&period=lastweek";
                        new RetrieveFeedTask().execute();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
