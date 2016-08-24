package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Page_Views;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.Button;
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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.ArrayList;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class PageViewsYearFragment extends Fragment implements View.OnClickListener
{
    ProgressBar progressBar;
    LineChart chart;
    ArrayList<ILineDataSet> dataSets;
    ArrayList<Entry> valueSet1;
    ArrayList<Entry> valueSet2;
    ArrayList<Integer> tableValues;
    boolean landscapeMode, apiIdSelected;
    boolean secondCall = false;
    boolean tableIsVisible = false;
    private OnFragmentInteractionListener mListener;
    String API_URL = "";
    String lastYear;
    TableLayout table;
    public static TextView textViewDate, textViewInfo, textViewTotal, tableToggler, columnOne, columnTwo;
    int totalVisits, totalMonths;
    CustomMarkerViewPage mv;
    ArrayList<String>monthList;
    Button moreInfoButton;

    public PageViewsYearFragment() {    }  // Required empty public constructor

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
        MainActivity.currentFragment = "Year";

        DateTime thisYear = new DateTime().minusYears(1);
        lastYear = thisYear.toString("yyyy");

        if(!MainActivity.API_ID.equalsIgnoreCase("")) //Check if the user has selected a website
        {
            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                    "/analytics/overview/history?period=thisyear";
            apiIdSelected = true;

        }else
        {
            apiIdSelected = false;
        }
        View rootView = inflater.inflate(R.layout.fragment_linechart, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        table = (TableLayout) rootView.findViewById(R.id.table);
        textViewDate = (TextView) rootView.findViewById(R.id.textViewDate);
        textViewInfo = (TextView) rootView.findViewById(R.id.textViewInfo);
        textViewTotal = (TextView) rootView.findViewById(R.id.textViewTotal);
        tableToggler = (TextView) rootView.findViewById(R.id.tableToggler);
        columnOne = (TextView) rootView.findViewById(R.id.columnOne);
        columnTwo = (TextView) rootView.findViewById(R.id.columnTwo);
        moreInfoButton = (Button) rootView.findViewById(R.id.moreInfoButton);
        mv = new CustomMarkerViewPage(getActivity().getApplicationContext(), R.layout.custom_marker_view);

        columnOne.setText("Month of Year");
        columnTwo.setText("Page Views");
        textViewInfo.setText("PAGE VIEWS THIS YEAR");
        tableToggler.setCompoundDrawablesWithIntrinsicBounds(null, null,
                getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_white_18dp), null);

        moreInfoButton.setOnClickListener(this);

        table = (TableLayout) rootView.findViewById(R.id.table);
        table.setVisibility(View.GONE);
        totalVisits = 0;

        //Get all the months for the table
        monthList = new ArrayList<>();
        String[] months = new DateFormatSymbols().getMonths();
        for (int i = 0; i < months.length; i++) {
            String month = months[i];
            monthList .add(months[i]);
        }
        Log.i("XXXXXX", monthList.toString());

        //Get date period for text view
        int dayOfYear = new DateTime().getDayOfYear();
        DateTime firstDayOfMonth = new DateTime().minusDays(dayOfYear - 1);
        DateTime today = new DateTime();
        String textDatePeriod = firstDayOfMonth.toString("MMMMM yyyy") + " - " + today.toString("MMMMM yyyy");
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
            moreInfoButton.setVisibility(View.GONE);
        }

        chart = (LineChart) rootView.findViewById(R.id.chart);
        return rootView;
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

    public void createTable()
    {
        while(tableValues.size() < 12)
        {
            tableValues.add(0);
        }

        int tableSize = tableValues.size();
        for (int i = 0; i <tableSize ; i++)
        {
            TableRow[] tableRow = new TableRow[tableSize];
            tableRow[i] = new TableRow(getActivity());
            tableRow[i].setPadding(40,40,40,40);


            TextView monthDay = new TextView(getActivity());
            monthDay.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            monthDay.setText(monthList.get(i));
            monthDay.setTextColor(Color.WHITE);

            TextView visits = new TextView(getActivity());
            visits.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            visits.setGravity(Gravity.RIGHT);
            //Calculates the % of totalVisits.
            float tempPercent = ((float)tableValues.get(i)/(float)totalVisits * 100);
            DecimalFormat numberFormat = new DecimalFormat("0.00");
            String percentVisits = numberFormat.format(tempPercent)+ " %";

            //Adds two different text sizes for the visitsTxt
            String visitsString = tableValues.get(i).toString() + "\n" + percentVisits;
            SpannableString spanString2 =  new SpannableString(visitsString);
            spanString2.setSpan(new RelativeSizeSpan(0.7f), tableValues.get(i).toString().length() , spanString2.length(), 0); // set size, start-stop
            visits.setText(spanString2);
            visits.setTextColor(Color.WHITE);

            tableRow[i].addView(monthDay);
            tableRow[i].addView(visits);
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

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();

        for (Integer i = 1; i <= 12 ; i++)
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
                int compareCounter = 1;
                totalMonths = items.length();
                int placementOnXAxis = 0;

                if(secondCall)
                {
                    valueSet2 = new ArrayList<>();
                }else
                {
                    valueSet1 = new ArrayList<>();
                    tableValues = new ArrayList<>();
                }
                if(totalMonths == 0)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
                }else
                {
                    for (Integer i = 0; i < totalMonths; i++)
                    {
                        String timestamp = items.getJSONObject(i).getString("timestamp");
                        int visits = items.getJSONObject(i).getInt("page_views");
                        String tempString = timestamp.substring(5, 7);
                        int month_of_year = Integer.parseInt(tempString);

                        if(secondCall) //Last Year
                        {
                                Entry entry = new Entry((float)visits, i);
                                valueSet2.add(entry);
                        }else //Current Year
                        {
                                Entry entry = new Entry((float) visits, i);
                                valueSet1.add(entry);
                                tableValues.add(visits);
                                totalVisits = totalVisits + visits;

                        }
                    }

                    if(secondCall)
                    {
                        LineDataSet lineDataSet2 = new LineDataSet(valueSet2, "LAST YEAR");
                        lineDataSet2.setColor(Color.rgb(181, 0, 97)); //TODO USE R.COLOR
                        Drawable drawable = ContextCompat.getDrawable(getActivity().getApplication(), R.drawable.chart_lastperiod_background);
                        lineDataSet2.setFillDrawable(drawable);
                        lineDataSet2.setDrawFilled(true);
                        lineDataSet2.setHighLightColor(Color.rgb(255,255,255));
                        dataSets.add(lineDataSet2);
                        drawGraph();

                        secondCall = false;

                    }else
                    {
                        dataSets = new ArrayList<>();
                        LineDataSet lineDataSet1 = new LineDataSet(valueSet1, "THIS YEAR");
                        lineDataSet1.setColor(Color.rgb(5, 184, 198));
                        Drawable drawable = ContextCompat.getDrawable(getActivity().getApplication(), R.drawable.chart_thisperiod_background);
                        lineDataSet1.setFillDrawable(drawable);
                        lineDataSet1.setDrawFilled(true);
                        lineDataSet1.setHighLightColor(Color.rgb(255,255,255));
                        dataSets.add(lineDataSet1);

                        // Setting Header Text to match VisitsYear Fragment.
                        textViewTotal.setText(String.valueOf(totalVisits));

                        if(landscapeMode)
                        {
                            secondCall = true;
                            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                                    "/analytics/overview/history?period=" + lastYear + "0101_" + lastYear + "1231";
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

