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

public class VisitsMonthFragment extends Fragment
{
    ProgressBar progressBar;
    LineChart chart;
    ArrayList<LineDataSet> dataSets;
    String API_URL = "";
    private OnFragmentInteractionListener mListener;
    Integer totalMonthDays = 0;

    public VisitsMonthFragment()
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
        if(MainActivity.API_ID != null)
        {
            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                    "/analytics/behavior/visits_by_monthday?page=1&page_size=10&period=ThisMonth";

        }else
        {
            //TODO error message no Site selected
        }
        View rootView = inflater.inflate(R.layout.fragment_visits_month, container, false);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();

        for (Integer i = 1; i <= totalMonthDays ; i++)
        {
            xAxis.add(i.toString());
        }

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
        data.setValueTextSize(9f);
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
                ArrayList<Entry> valueSet1 = new ArrayList<>();
                int compareCounter = 1;

                for(Integer i = 0; i < items.length(); i++) // Shouldnt use a method call
                {
                    int day_of_month = items.getJSONObject(i).getInt("day_of_month");
                    int visits = items.getJSONObject(i).getInt("visits");

                    while(day_of_month != compareCounter)
                    {
                        int stopValue = compareCounter;
                        for(int j = stopValue; j < day_of_month; j++)
                        {
                            Entry entry = new Entry(0, compareCounter-1);
                            valueSet1.add(entry);
                            totalMonthDays++;
                            compareCounter++;
                        }
                    }
                    if(day_of_month == compareCounter)
                    {
                        Entry entry = new Entry((float)visits, day_of_month-1);
                        valueSet1.add(entry);
                        totalMonthDays++;
                        compareCounter++;
                    }

                }
                LineDataSet lineDataSet1 = new LineDataSet(valueSet1, "VISITS PER DAY");
                lineDataSet1.setColor(Color.rgb(49, 79, 49));
                lineDataSet1.setDrawFilled(true);
                dataSets = new ArrayList<>();
                dataSets.add(lineDataSet1);
                drawGraph();
                compareCounter = 0;
                totalMonthDays = 0;

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClassCastException ce){
                Toast.makeText(getActivity().getApplicationContext(), "Invalid Data from API", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
