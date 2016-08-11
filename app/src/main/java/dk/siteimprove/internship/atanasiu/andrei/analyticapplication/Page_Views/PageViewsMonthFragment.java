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
import java.text.DecimalFormat;
import java.util.ArrayList;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class PageViewsMonthFragment extends Fragment implements View.OnClickListener
{
    ProgressBar progressBar;
    LineChart chart;
    ArrayList<LineDataSet> dataSets;
    ArrayList<Entry> valueSet1;
    ArrayList<Entry> valueSet2;
    String API_URL = "";
    private OnFragmentInteractionListener mListener;
    Integer totalMonthDays = 0, totalVisits;
    boolean landscapeMode;
    public static TextView textViewDate, textViewInfo, textViewTotal, tableToggler, columnOne;
    boolean apiIdSelected;
    TableLayout table;
    ArrayList<Integer> tableValues;
    ArrayList<String> tableWeekDays = new ArrayList<>();
    boolean tableIsVisible = false;
    boolean secondCall = false;
    int totalDays;
    CustomMarkerViewPage mv;

    public PageViewsMonthFragment()
    {
        // Required empty public constructor
    }

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
        MainActivity.currentFragment = "Month";

        if(!MainActivity.API_ID.equalsIgnoreCase(""))
        {
            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                    "/analytics/behavior/visits_by_monthday?page=1&page_size=10&period=ThisMonth";
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

        columnOne.setText("Day of Month");
        textViewInfo.setText("PAGE VIEWS THIS MONTH");
        tableToggler.setOnClickListener(this);
        tableToggler.setCompoundDrawablesWithIntrinsicBounds(null, null,
                getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_white_36dp), null);

        table = (TableLayout) rootView.findViewById(R.id.table);
        table.setVisibility(View.GONE);

        totalVisits = 0;
        //Get date period for text view
        int daysOfMonth = new DateTime().getDayOfMonth();
        DateTime firstDayOfMonth = new DateTime().minusDays(daysOfMonth - 1);
        DateTime today = new DateTime().minusDays(1);
        String textDatePeriod = firstDayOfMonth.toString("dd MMMM") + " - " + today.toString("dd MMMM");
        textViewDate.setText(textDatePeriod);

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

    public void createTable()
    {
        int tableSize = tableValues.size();
        for (int i = 0; i <tableSize ; i++)
        {
            TableRow[] tableRow = new TableRow[tableSize];
            tableRow[i] = new TableRow(getActivity());
            tableRow[i].setPadding(40,40,40,40);


            TextView monthDay = new TextView(getActivity());
            monthDay.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            monthDay.setText(String.valueOf(i+1));
            monthDay.setTextColor(Color.WHITE);

            TextView visits = new TextView(getActivity());
            visits.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            visits.setGravity(Gravity.RIGHT);
            //Calculates the % of totalVisits.
            float tempPercent = ((float)tableValues.get(i)/(float)totalVisits * 100);
            DecimalFormat numberFormat = new DecimalFormat("#.00");
            String percentVisits = "% " + numberFormat.format(tempPercent) ;
            visits.setText(tableValues.get(i).toString() + "\n" + percentVisits);
            visits.setTextColor(Color.WHITE);

            tableRow[i].addView(monthDay);
            tableRow[i].addView(visits);
            table.addView(tableRow[i]);
        }
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

    public boolean haveNetworkConnection() {
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

        for (Integer i = 1; i <= 31 ; i++)
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
        xAxis.setTextColor(Color.WHITE);
        if(landscapeMode)
        {
            data.setValueTextSize(0f);
            chart.getAxisRight().setDrawLabels(false);
            chart.getAxisLeft().setTextColor(Color.WHITE);
        }else
        {
            xAxis.setSpaceBetweenLabels(1);
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
                int compareCounter = 1;
                totalDays = items.length();
                int placementOnXAxis = 0;
                if(secondCall)
                {
                    valueSet2 = new ArrayList<>();
                }else
                {
                    valueSet1 = new ArrayList<>();
                    tableValues = new ArrayList<>();
                }
                if(totalDays == 0)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
                    totalVisits = 0;
                }else
                {
                    for (Integer i = 0; i < totalDays; i++)
                    {
                        int day_of_month = items.getJSONObject(i).getInt("day_of_month");
                        int visits = items.getJSONObject(i).getInt("page_views");


                        if(secondCall)
                        {
                            while(day_of_month != compareCounter)
                            {
                                int startValue = compareCounter;
                                for(int j = startValue; j < day_of_month; j++)
                                {
                                    Entry entry = new Entry(0, placementOnXAxis);
                                    valueSet2.add(entry);
                                    compareCounter++;
                                    placementOnXAxis++;
                                }
                            }
                            if(day_of_month == compareCounter)
                            {
                                Entry entry = new Entry((float)visits, placementOnXAxis);
                                valueSet2.add(entry);
                                compareCounter++;
                                placementOnXAxis++;
                            }
                            while(compareCounter <= 31 && i == (totalDays - 1))
                            {
                                Entry entry = new Entry(0, placementOnXAxis);
                                valueSet2.add(entry);
                                placementOnXAxis++;
                                compareCounter++;
                            }
                        }else
                        {
                            while (day_of_month != compareCounter)
                            {
                                int stopValue = compareCounter;
                                for (int j = stopValue; j < day_of_month; j++)
                                {
                                    Entry entry = new Entry(0, placementOnXAxis);
                                    valueSet1.add(entry);
                                    tableValues.add(0);
                                    placementOnXAxis++;
                                    compareCounter++;
                                }
                            }
                            if (day_of_month == compareCounter)
                            {
                                Entry entry = new Entry((float) visits, placementOnXAxis);
                                valueSet1.add(entry);
                                tableValues.add(visits);
                                placementOnXAxis++;
                                compareCounter++;
                                totalVisits = totalVisits + visits;
                            }
                        }
                    }

                    if (secondCall)
                    {
                        LineDataSet lineDataSet2 = new LineDataSet(valueSet2, "LAST MONTH");
                        lineDataSet2.setColor(Color.rgb(181, 0, 97)); //TODO USE R.COLOR
                        lineDataSet2.setDrawFilled(true);
                        lineDataSet2.setFillColor(Color.rgb(181, 0, 97));
                        lineDataSet2.setFillAlpha(40);
                        dataSets.add(lineDataSet2);
                        drawGraph();

                        secondCall = false;
                    } else
                    {
                        dataSets = new ArrayList<>();
                        LineDataSet lineDataSet1 = new LineDataSet(valueSet1, "THIS MONTH");
                        lineDataSet1.setColor(Color.rgb(5, 184, 198));
                        lineDataSet1.setDrawFilled(true);
                        lineDataSet1.setFillColor(Color.rgb(5, 184, 198));
                        lineDataSet1.setFillAlpha(40);
                        dataSets.add(lineDataSet1);
                        Log.i("Total VISISTS", totalVisits.toString());
                        textViewTotal.setText(totalVisits.toString());
                        if(landscapeMode)
                        {
                            secondCall = true;
                            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                                    "/analytics/behavior/visits_by_monthday?page=1&page_size=10&period=LastMonth";
                            new RetrieveFeedTask().execute();
                        }else
                        {
                            Log.i("I AM CALLEDNOLANDSCAPE","XXXXXX");
                            createTable();
                            drawGraph();
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
