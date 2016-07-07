package dk.siteimprove.internship.atanasiu.andrei.analyticapplication;

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
import android.widget.Button;
import android.widget.ProgressBar;
import com.github.mikephil.charting.charts.BarChart;
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



public class SocialMediaFragment extends Fragment implements View.OnClickListener
{
    BarChart chart;
    ArrayList<BarDataSet> dataSets;
    ArrayList<String> xAxis;
    ProgressBar progressBar;
    static final String API_KEY = "ebd8cdc10745831de07c286a9c6d967d";
    static final String API_URL = "https://api.siteimprove.com/v2/sites/73617/analytics/traffic_sources/social_media_organisations";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    // TODO: Rename and change types and number of parameters
    public static SocialMediaFragment newInstance(String param1, String param2) {
        SocialMediaFragment fragment = new SocialMediaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public SocialMediaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String>
    {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        protected String doInBackground(Void... urls) {
            String email = "andrei.atanasiu1994@gmail.com";
            // emailText.getText().toString();
            // Do some validation here


            try {
                URL url = new URL(API_URL  /*"email=" + email + "&apiKey=" + API_KEY*/);
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
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            //responseView.setText(response);

            // TODO: check this.exception
            // TODO: do something with the feed

            try {


                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray items = object.getJSONArray("items");
                ArrayList<BarEntry> valueSet1 = new ArrayList<>();
                xAxis = new ArrayList<>();
                int numberorg = 0;
                for(int i = 0; i < items.length(); i++)
                {

                    Integer pages = items.getJSONObject(i).getInt("pages");
                    Integer visits = items.getJSONObject(i).getInt("visits");
                    String organisation = items.getJSONObject(i).getString("organisation");

                    BarEntry entry = new BarEntry((float)visits, numberorg);
                    valueSet1.add(entry);
                    xAxis.add(organisation);
                    numberorg = numberorg + 1;


                }

                BarDataSet barDataSet1 = new BarDataSet(valueSet1, "VISITS");
                barDataSet1.setColor(Color.rgb(0, 155, 0));
                dataSets = new ArrayList<>();
                dataSets.add(barDataSet1);

                drawGraph();








            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_visits, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        new RetrieveFeedTask().execute();
        chart = (BarChart) rootView.findViewById(R.id.chart);

        new RetrieveFeedTask().execute();
        Button queryButton = (Button) rootView.findViewById(R.id.queryButton);

        queryButton.setOnClickListener(this);
        // Inflate the layout for this fragment
        return  rootView;
    }

    @Override
    public void onClick(View v)
    {
        new RetrieveFeedTask().execute();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void drawGraph()
    {
        BarData data = new BarData(xAxis, dataSets);
        Log.i("DATA SETS", dataSets.toString());
        chart.setData(data);
        //chart.setDescription("My Chart");
        chart.animateXY(2000, 2000);
        chart.invalidate();
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
    }
}
