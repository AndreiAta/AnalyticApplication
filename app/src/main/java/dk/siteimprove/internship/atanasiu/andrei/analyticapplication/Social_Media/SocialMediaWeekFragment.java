package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Social_Media;

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

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

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


public class SocialMediaWeekFragment extends Fragment
{
    HorizontalBarChart chart;
    ArrayList<BarDataSet> dataSets;
    ArrayList<String> xAxis;
    ProgressBar progressBar;
    static final String API_KEY = "ebd8cdc10745831de07c286a9c6d967d";
    String API_URL = "";
    private OnFragmentInteractionListener mListener;

    public SocialMediaWeekFragment()
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
                    "/analytics/traffic_sources/social_media_organisations?page=1&page_size=10&period=Lastsevendays";
        }else
        {
            //TODO error message no Site selected
        }
        View rootView = inflater.inflate(R.layout.fragment_social_media_week, container, false); // Inflate the layout for this fragment
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        chart = (HorizontalBarChart) rootView.findViewById(R.id.chart);

        new RetrieveFeedTask().execute();
        return rootView;
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

    private void drawGraph()
    {
        BarData data = new BarData(xAxis, dataSets);
        chart.setData(data);
        chart.setDescription("");
        chart.animateXY(2000, 2000);
        chart.invalidate();
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        data.setValueTextSize(10f);
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> // THIS IS A CLASS
    {
        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {
            String email = "andrei.atanasiu1994@gmail.com";

            try {
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String credentials = email + ":" + API_KEY;
                String auth = "Basic" + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                urlConnection.setRequestProperty("Authorization",auth);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept", "application/json");
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
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
            Log.i("ERROR", response);

            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);

            try
            {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray items = object.getJSONArray("items");
                ArrayList<BarEntry> valueSet1 = new ArrayList<>();
                xAxis = new ArrayList<>();
                int numberorg = 0;
                for(int i = 0; i < items.length(); i++)
                {
                    Integer visits = items.getJSONObject(i).getInt("visits");
                    String organisation = items.getJSONObject(i).getString("organisation");

                    BarEntry entry = new BarEntry((float)visits, numberorg);
                    valueSet1.add(entry);
                    xAxis.add(organisation);
                    numberorg = numberorg + 1;

                }
                BarDataSet barDataSet1 = new BarDataSet(valueSet1, "VISITS");
                barDataSet1.setColor(Color.rgb(49, 79, 79));
                barDataSet1.setBarSpacePercent(50f);
                dataSets = new ArrayList<>();
                dataSets.add(barDataSet1);

                drawGraph();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
