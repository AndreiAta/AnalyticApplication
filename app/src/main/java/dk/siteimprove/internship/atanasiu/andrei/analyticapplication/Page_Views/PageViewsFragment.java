package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Page_Views;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.components.XAxis;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class PageViewsFragment extends Fragment implements View.OnClickListener
{
    ProgressBar progressBar;
    LineChart chart;
    ArrayList<LineDataSet> dataSets;
    ArrayList<Entry> valueSet1;
    ArrayList<Entry> valueSet2;
    String API_URL = "";
    int totalHours, totalVisits;
    boolean apiIdSelected, landscapeMode;
    boolean secondCall = false;
    boolean tableIsVisible = false;
    public static TextView textViewDate, textViewInfo, textViewTotal, tableToggler, columnOne;
    TableLayout table;
    ArrayList<Integer> tableValues;
    CustomMarkerViewPage mv;

    private OnFragmentInteractionListener mListener;

    public PageViewsFragment() {    } // Required empty public constructor

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            landscapeMode = true;
        }
        else
        {
            landscapeMode = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        MainActivity.currentFragment = "Today";
        totalVisits = 0;
        DateTime today = new DateTime();

        if(!MainActivity.API_ID.equalsIgnoreCase(""))
        {
            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                    "/analytics/behavior/visits_by_hour?page=1&page_size=10&period=Today";
            apiIdSelected = true;

        }else
        {
            apiIdSelected = false;
        }
        View rootView = inflater.inflate(R.layout.fragment_linechart, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        textViewDate = (TextView) rootView.findViewById(R.id.textViewDate);
        textViewInfo = (TextView) rootView.findViewById(R.id.textViewInfo);
        textViewTotal = (TextView) rootView.findViewById(R.id.textViewTotal);
        tableToggler = (TextView) rootView.findViewById(R.id.tableToggler);
        columnOne = (TextView) rootView.findViewById(R.id.columnOne);
        mv = new CustomMarkerViewPage(getActivity().getApplicationContext(), R.layout.custom_marker_view);

        textViewDate.setText(today.toString("dd MMMM"));
        textViewInfo.setText("PAGE VIEWS TODAY");
        tableToggler.setGravity(Gravity.LEFT);
        tableToggler.setCompoundDrawablesWithIntrinsicBounds(null, null,
                getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_white_36dp), null);
        columnOne.setText("Hour of Day");

        tableToggler.setOnClickListener(this);

        table = (TableLayout) rootView.findViewById(R.id.table);
        table.setVisibility(View.GONE);

        if(haveNetworkConnection())
        {
            if(apiIdSelected)
            {
                new RetrieveFeedTask().execute();
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(), "Please select a Site", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getActivity().getApplicationContext(), "You have no Internet", Toast.LENGTH_SHORT).show();
        }
        if(landscapeMode)
        {
            table.setVisibility(View.GONE);
            tableToggler.setVisibility(View.GONE);
        }

        chart = (LineChart) rootView.findViewById(R.id.chart);
        return  rootView;

    }

    @Override
    public void onClick(View v)
    {
        if(tableIsVisible)
        {
            table.setVisibility(View.GONE);
            tableIsVisible = false;
            tableToggler.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_white_36dp), null);
        }else
        {
            table.setVisibility(View.VISIBLE);
            tableIsVisible = true;
            tableToggler.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_white_36dp), null);
        }
    }

    public void createTable()
    {
        int tableSize = tableValues.size();
        for (int i = 0; i < tableSize ; i++)
        {
            TableRow[] tableRow = new TableRow[tableSize];
            tableRow[i] = new TableRow(getActivity());
            tableRow[i].setPadding(40, 40, 40, 40);

            TextView hourOfDayTxt = new TextView(getActivity());
            hourOfDayTxt.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            if(i < 10) hourOfDayTxt.setText("0" + String.valueOf(i));
            else hourOfDayTxt.setText(String.valueOf(i));
            hourOfDayTxt.setTextColor(Color.WHITE);

            TextView visitsTxt = new TextView(getActivity());
            visitsTxt.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            visitsTxt.setGravity(Gravity.RIGHT);
            //Calculates the % of totalVisits.
            float tempPercent = ((float)tableValues.get(i)/(float)totalVisits * 100);
            DecimalFormat numberFormat = new DecimalFormat("#.00");
            String percentVisits = "% " + numberFormat.format(tempPercent) ;
            visitsTxt.setText(tableValues.get(i).toString() + "\n" + percentVisits);
            visitsTxt.setTextColor(Color.WHITE);

            tableRow[i].addView(hourOfDayTxt);
            tableRow[i].addView(visitsTxt);
            table.addView(tableRow[i]);
        }
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

    public boolean haveNetworkConnection()
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();

        for (Integer i = 0; i < 24 ; i++)
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
        chart.animateXY(1000, 1000);
        chart.invalidate();
        chart.setBackgroundColor(Color.rgb(68, 68, 68));
        chart.setGridBackgroundColor(R.color.White);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.setTouchEnabled(true);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setMarkerView(mv);

        if(landscapeMode)
        {
            chart.getLayoutParams().height = (int)MainActivity.screenHeight/2;
        }
        else
        {
            chart.getLayoutParams().height = (int)MainActivity.screenHeight/3;
        }
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setTextColor(Color.WHITE);
        if(landscapeMode)
        {
            data.setValueTextSize(0f);
            chart.getAxisRight().setDrawLabels(false);
            chart.getAxisLeft().setTextColor(Color.WHITE);
        }else
        {
            data.setValueTextSize(0f);
            chart.getAxisLeft().setDrawLabels(false);
            chart.getAxisRight().setDrawLabels(false);
        }
    }

    // ===============================
    //        INTERNAL CLASS
    // ===============================
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
                int compareCounter = 0;
                totalHours = items.length();
                int placementOnXAxis = 0;

                if(secondCall)
                {
                    valueSet2 = new ArrayList<>();
                }else
                {
                    valueSet1 = new ArrayList<>();
                    tableValues = new ArrayList<>();
                }
                if(totalHours == 0)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
                }else
                {
                    for (Integer i = 0; i < totalHours; i++)
                    {
                        int hour_of_day = items.getJSONObject(i).getInt("hour_of_day");
                        int visits = items.getJSONObject(i).getInt("page_views");

                        if(secondCall) //Yesterday
                        {
                            while(hour_of_day != compareCounter)
                            {
                                int stopValue = compareCounter;
                                for (int j = stopValue; j < hour_of_day; j++)
                                {
                                    Entry entry = new Entry(0, placementOnXAxis);
                                    valueSet2.add(entry);
                                    placementOnXAxis++;
                                    compareCounter++;
                                }
                            }
                            if(compareCounter == hour_of_day)
                            {
                                Entry entry = new Entry((float)visits, placementOnXAxis);
                                valueSet2.add(entry);
                                compareCounter++;
                                placementOnXAxis++;
                            }

                            while(compareCounter <= 23 && i == (totalHours - 1))
                            {
                                Entry entry = new Entry(0, placementOnXAxis);
                                valueSet2.add(entry);
                                compareCounter++;
                                placementOnXAxis++;
                            }
                        }else //Current Day
                        {
                            while (hour_of_day != compareCounter)
                            {

                                int stopValue = compareCounter;
                                for (int j = stopValue; j < hour_of_day; j++)
                                {
                                    Entry entry = new Entry(0, placementOnXAxis);
                                    valueSet1.add(entry);
                                    tableValues.add(0);
                                    compareCounter++;
                                    placementOnXAxis++;
                                }

                            }
                            if (hour_of_day == compareCounter)
                            {
                                Entry entry = new Entry((float) visits, placementOnXAxis);
                                valueSet1.add(entry);
                                tableValues.add(visits);
                                compareCounter++;
                                placementOnXAxis++;
                                totalVisits = totalVisits + visits;
                            }
                        }


                    }

                    if(secondCall)
                    {
                        LineDataSet lineDataSet2 = new LineDataSet(valueSet2, "YESTERDAY");
                        lineDataSet2.setColor(Color.rgb(181, 0, 97)); //TODO USE R.COLOR
                        lineDataSet2.setDrawFilled(true);
                        lineDataSet2.setFillColor(Color.rgb(181, 0, 97));
                        lineDataSet2.setFillAlpha(40);
                        dataSets.add(lineDataSet2);
                        drawGraph();

                        secondCall = false;

                    }else
                    {
                        dataSets = new ArrayList<>();
                        LineDataSet lineDataSet1 = new LineDataSet(valueSet1, "TODAY");
                        lineDataSet1.setColor(Color.rgb(5, 184, 198));
                        lineDataSet1.setDrawFilled(true);
                        lineDataSet1.setFillColor(Color.rgb(5, 184, 198));
                        lineDataSet1.setFillAlpha(40);
                        dataSets.add(lineDataSet1);

                        // Setting Header Text to match VisitsToday Fragment.
                        textViewTotal.setText(String.valueOf(totalVisits));

                        if(landscapeMode)
                        {
                            secondCall = true;
                            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                                    "/analytics/behavior/visits_by_hour?page=1&page_size=10&period=Yesterday";
                            new RetrieveFeedTask().execute();
                        }else
                        {
                            drawGraph();
                            createTable();
                        }
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClassCastException ce){
                Toast.makeText(getActivity().getApplicationContext(), "Invalid Data from API", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
