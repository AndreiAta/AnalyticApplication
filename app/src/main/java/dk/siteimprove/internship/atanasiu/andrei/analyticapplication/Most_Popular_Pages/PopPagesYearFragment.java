package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Most_Popular_Pages;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
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

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

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

public class PopPagesYearFragment extends Fragment implements View.OnClickListener
{
    String API_URL = "";
    boolean landscapeMode, apiIdSelected;
    boolean tableIsVisible = false;
    boolean secondCall = false;
    int totalVisits, totalPopPages;
    ArrayList<BarEntry> valueSet1, valueSet2;
    ArrayList<IBarDataSet> dataSets;
    public static ArrayList<String> xAxis, xAxisLabels;
    ArrayList<Integer> tableValues;
    int[] tempValSet2 = new int[100]; // This should be instantiated in RetriveFeedTask or simply use ArrayList instead?
    String lastYear;

    HorizontalBarChart chart;
    ProgressBar progressBar;
    public static TextView textViewDate, textViewInfo, textViewTotal, tableToggler, columnOne, columnTwo;
    TableLayout table;
    CustomMarkerViewPopular mv;
    private OnFragmentInteractionListener mListener;

    public PopPagesYearFragment() { } //Required empty constructor

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        MainActivity.currentFragment = "Year";

        if(!MainActivity.API_ID.equalsIgnoreCase(""))
        {
            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                    "/analytics/content/most_popular_pages?page=1&page_size=10&period=thisyear";
            apiIdSelected = true;
        }else
        {
            apiIdSelected = false;
        }

        View rootView = inflater.inflate(R.layout.fragment_barchart, container, false);// Inflate the layout for this fragment
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        chart = (HorizontalBarChart) rootView.findViewById(R.id.chart);
        textViewDate = (TextView) rootView.findViewById(R.id.textViewDate);
        textViewInfo = (TextView) rootView.findViewById(R.id.textViewInfo);
        textViewTotal = (TextView) rootView.findViewById(R.id.textViewTotal);
        tableToggler = (TextView) rootView.findViewById(R.id.tableToggler);
        columnOne = (TextView) rootView.findViewById(R.id.columnOne);
        columnTwo = (TextView) rootView.findViewById(R.id.columnTwo);
        table = (TableLayout) rootView.findViewById(R.id.table);
        mv = new CustomMarkerViewPopular(getActivity().getApplicationContext(), R.layout.custom_marker_view);

        textViewInfo.setText("PAGE VIEWS THIS YEAR");
        tableToggler.setGravity(Gravity.LEFT);
        tableToggler.setCompoundDrawablesWithIntrinsicBounds(null, null,
                getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_white_18dp), null);
        columnOne.setText("Popular Pages");
        columnTwo.setText("Page Views");

        tableToggler.setOnClickListener(this);
        table.setVisibility(View.GONE);

        totalVisits = 0;

        //Get date period for text view
        int dayOfYear = new DateTime().getDayOfYear();
        DateTime firstDayOfMonth = new DateTime().minusDays(dayOfYear - 1);
        DateTime today = new DateTime();
        String textDatePeriod = firstDayOfMonth.toString("MMMMM yyyy") + " - " + today.toString("MMMMM yyyy");
        textViewDate.setText(textDatePeriod);
        // Calculate last year period for the API call
        DateTime thisYear = new DateTime().minusYears(1);
        lastYear = thisYear.toString("yyyy");

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

        return rootView;
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

    @Override
    public void onClick(View v)
    {
        if(tableIsVisible)
        {
            table.setVisibility(View.GONE);
            tableIsVisible = false;
            tableToggler.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_white_18dp), null);
        }else
        {
            table.setVisibility(View.VISIBLE);
            tableIsVisible = true;
            tableToggler.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_white_18dp), null);
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

    public void createTable()
    {
        for (int i = 0; i < xAxisLabels.size() ; i++)
        {
            TableRow[] tableRow = new TableRow[xAxis.size()];
            tableRow[i] = new TableRow(getActivity());
            tableRow[i].setPadding(40, 40, 40, 40);

            TextView hourOfDayTxt = new TextView(getActivity());
            hourOfDayTxt.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            //Adds two different text sizes for the hourOfDayTxt
            String titleString = xAxisLabels.get(i).toString() + "\n" + xAxis.get(i).toString().substring(7); // Could shorten more in url length?
            SpannableString spanString1 =  new SpannableString(titleString);
            spanString1.setSpan(new RelativeSizeSpan(0.5f), xAxisLabels.get(i).toString().length(), spanString1.length(), 0); // set size, start-stop
            hourOfDayTxt.setText(spanString1);
            hourOfDayTxt.setTextColor(Color.WHITE);

            TextView visitsTxt = new TextView(getActivity());
            visitsTxt.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            visitsTxt.setGravity(Gravity.RIGHT);

            //Calculates the % of totalVisits.
            float tempPercent = ((float)tableValues.get(i)/(float)totalVisits * 100);
            DecimalFormat numberFormat = new DecimalFormat("0.00");
            String percentVisits = numberFormat.format(tempPercent)+ " %";

            //Adds two different text sizes for the visitsTxt
            String visitsString = tableValues.get(i).toString() + "\n" + percentVisits;
            SpannableString spanString2 =  new SpannableString(visitsString);
            spanString2.setSpan(new RelativeSizeSpan(0.7f), tableValues.get(i).toString().length() , spanString2.length(), 0); // set size, start-stop
            visitsTxt.setText(spanString2);
            visitsTxt.setTextColor(Color.WHITE);

            tableRow[i].addView(hourOfDayTxt);
            tableRow[i].addView(visitsTxt);
            table.addView(tableRow[i]);
        }
    }

    private void drawGraph()
    {
        BarData data = new BarData(xAxisLabels, dataSets);
        chart.setData(data);
        chart.setDescription("");
        chart.animateXY(1000, 1000);
        chart.invalidate();
        chart.setGridBackgroundColor(R.color.White);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);
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

        XAxis chartXAxis = chart.getXAxis();
        chartXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        chartXAxis.setSpaceBetweenLabels(0);
        chartXAxis.setTextColor(Color.WHITE);
        chartXAxis.setTextSize(0f);
        data.setValueTextSize(0f);
        if(landscapeMode)
        {
            data.setValueTextSize(0f);
            data.setValueTextColor(Color.WHITE);
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
    class RetrieveFeedTask extends AsyncTask<Void, Void, String> // THIS IS A CLASS
    {
        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {

            try {
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String credentials = MainActivity.API_EMAIL + ":" + MainActivity.API_KEY;
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

            try
            {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray items = object.getJSONArray("items");
                totalPopPages = items.length();

                if (secondCall) // second time we call the API, prepare lists, and fill tempArray with 0's
                {
                    valueSet2 = new ArrayList<>();
                    //Filling the array with 0
                    for (int i = 0; i < totalPopPages ; i++)
                    {
                        tempValSet2[i] = 0;
                    }

                } else // first time we call API, prepare lists
                {
                    valueSet1 = new ArrayList<>();
                    xAxis = new ArrayList<>();
                    xAxisLabels = new ArrayList<>();
                    tableValues = new ArrayList<>();
                }
                if(totalPopPages == 0)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
                }
                else
                {
                    for (int i = 0; i < totalPopPages; i++) // Loop through all the items in the JSON Array
                    {
                        Integer pageViews = items.getJSONObject(i).getInt("page_views");
                        String title = items.getJSONObject(i).getString("title");
                        String url = items.getJSONObject(i).getString("url");

                        if (secondCall) //Last Period
                        {

                            if (xAxis.contains(url)) // If the name is already in list, don't add it again.
                            {
                                tempValSet2[xAxis.indexOf(url)] = pageViews;
                            } else if (!xAxis.contains(url) && xAxis.size() < 10) // if it isn't in list, and there is space - add it
                            {
                                xAxis.add(url);
                                if (title.length() > 20)
                                {
                                    xAxisLabels.add(title.substring(0, 19) + "...");
                                } else
                                {
                                    xAxisLabels.add(title);
                                }
                                tempValSet2[xAxis.indexOf(url)] = pageViews;
                            }
                            if (i == totalPopPages - 1) // last time through the loop, move from tempArray to valueSet2.
                            {
                                for (int j = 0; j < totalPopPages; j++)
                                {
                                    BarEntry entry = new BarEntry(tempValSet2[j], j);
                                    valueSet2.add(entry);
                                }
                            }
                        } else // Current Period
                        {
                            if (i < 10) // we only want top 10
                            {
                                BarEntry entry = new BarEntry((float) pageViews, i);
                                valueSet1.add(entry);
                                xAxis.add(url);
                                if (title.length() > 20)
                                {
                                    xAxisLabels.add(title.substring(0, 19) + "...");
                                } else
                                {
                                    xAxisLabels.add(title);
                                }
                                tableValues.add(pageViews);
                                totalVisits = totalVisits + pageViews;
                            } else
                            {
                                if (!secondCall) // calculating the totalVisits (after top 10)
                                {
                                    totalVisits = totalVisits + pageViews;
                                }
                            }
                        }
                    }
                    if(secondCall)
                    {
                        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "LAST YEAR");
                        barDataSet2.setColor(Color.rgb(181, 0, 97)); //TODO USE R.COLOR
                        barDataSet2.setBarSpacePercent(50f);
                        dataSets.add(barDataSet2);
                        Log.i("IMPORT2", barDataSet2.toString());
                        drawGraph();

                        secondCall = false;
                    }else
                    {
                        dataSets = new ArrayList<>();
                        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "THIS YEAR");
                        barDataSet1.setColor(Color.rgb(5, 184, 198));
                        barDataSet1.setBarSpacePercent(50f);
                        dataSets.add(barDataSet1);
                        Log.i("IMPORT1", barDataSet1.toString());
                        textViewTotal.setText(String.valueOf(totalVisits));

                        if(landscapeMode)
                        {
                            secondCall = true;
                            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                                    "/analytics/content/most_popular_pages?page=1&page_size=10&period="
                                    + lastYear + "0101_" + lastYear + "1231";
                            new RetrieveFeedTask().execute();
                        }else
                        {
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