package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.visits;

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
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class VisitsFragment extends Fragment implements View.OnClickListener
{
    ProgressBar progressBar;
    LineChart chart;
    ArrayList<ILineDataSet> dataSets;
    ArrayList<Entry> valueSet1;
    ArrayList<Entry> valueSet2;
    String API_URL = "";
    String period;
    int totalHours, totalVisits, periodCounter;
    boolean apiIdSelected, landscapeMode;
    boolean secondCall = false;
    boolean tableIsVisible = true;
    public static TextView textViewDate, textViewInfo, textViewTotal, tableToggler, columnOne;
    TableLayout table;
    ArrayList<Integer> tableValues;
    CustomMarkerViewVisits mv;
    Button moreInfoButton;
    ImageButton imgBtnBack, imgBtnForward;
    TableRow defaultTableRow;
    ArrayList<String> xAxis;


    private OnFragmentInteractionListener mListener;

    public VisitsFragment() {    } // Required empty public constructor

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        landscapeMode = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        MainActivity.currentFragment = "Today";
        DateTime today = new DateTime();

        View rootView = inflater.inflate(R.layout.fragment_linechart, container, false);
        chart = (LineChart) rootView.findViewById(R.id.chart);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        textViewDate = (TextView) rootView.findViewById(R.id.textViewDate);
        textViewInfo = (TextView) rootView.findViewById(R.id.textViewInfo);
        textViewTotal = (TextView) rootView.findViewById(R.id.textViewTotal);
        tableToggler = (TextView) rootView.findViewById(R.id.tableToggler);
        columnOne = (TextView) rootView.findViewById(R.id.columnOne);
        moreInfoButton = (Button) rootView.findViewById(R.id.moreInfoButton);
        imgBtnBack = (ImageButton) rootView.findViewById(R.id.imgBtnBack);
        imgBtnForward = (ImageButton) rootView.findViewById(R.id.imgBtnForward);
        table = (TableLayout) rootView.findViewById(R.id.table);
        defaultTableRow = (TableRow) rootView.findViewById(R.id.defaultTableRow);
        mv = new CustomMarkerViewVisits(getActivity().getApplicationContext(), R.layout.custom_marker_view);


        imgBtnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getNextPeriod();
            }
        });
        imgBtnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getPreviousPeriod();
            }
        });

        textViewDate.setText(today.toString("dd MMMM"));
        textViewInfo.setText("VISITS TODAY");
        tableToggler.setGravity(Gravity.LEFT);
        tableToggler.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_keyboard_arrow_up_white_18dp, null), null);
        columnOne.setText("Hour of Day");

        moreInfoButton.setOnClickListener(this);
        periodCounter = 0;

        if(!MainActivity.API_ID.equalsIgnoreCase(""))
        {
            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                    "/analytics/behavior/visits_by_hour?page=1&page_size=10&period=" +
                    calculatePeriod(periodCounter);
            apiIdSelected = true;

        }else
        {
            apiIdSelected = false;
        }

        if(hasNetworkConnection())
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
            Toast.makeText(getActivity().getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        if(landscapeMode)
        {
            table.setVisibility(View.GONE);
            tableToggler.setVisibility(View.GONE);
            moreInfoButton.setVisibility(View.GONE);
        }

        return  rootView;
    }

    private void getNextPeriod()
    {
        if(hasNetworkConnection())
        {
            if(periodCounter != 0)
            {
                imgBtnBack.setClickable(false);
                imgBtnBack.setAlpha(0.5f);
                imgBtnForward.setClickable(false);
                imgBtnForward.setAlpha(0.5f);
                chart.setVisibility(View.INVISIBLE);
                textViewInfo.setText("VISITS TODAY");
                periodCounter--;
                API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                        "/analytics/behavior/visits_by_hour?page=1&page_size=10&period=" +
                        calculatePeriod(periodCounter);
                new RetrieveFeedTask().execute();
            }
        }
        else
        {
            Toast.makeText(getActivity().getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void getPreviousPeriod()
    {
        if(hasNetworkConnection())
        {
            imgBtnBack.setClickable(false);
            imgBtnBack.setAlpha(0.5f);
            imgBtnForward.setClickable(false);
            imgBtnForward.setAlpha(0.5f);
            chart.setVisibility(View.INVISIBLE);
            textViewInfo.setText("VISITS TODAY");
            periodCounter ++;
            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                    "/analytics/behavior/visits_by_hour?page=1&page_size=10&period=" +
                    calculatePeriod(periodCounter);
            new RetrieveFeedTask().execute();
        }
        else
        {
            Toast.makeText(getActivity().getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    private String calculatePeriod(int periodCounter)
    {
        period = "";
        DateTime currentPeriod = new DateTime();
        String currentDay = currentPeriod.toString("yyyyMMdd");
        textViewDate.setText(currentPeriod.minusDays(periodCounter).toString("dd MMM yyyy"));
        if(periodCounter != 0)
        {
            currentDay = currentPeriod.minusDays(periodCounter).toString("yyyyMMdd");

            if(secondCall)
            {
                textViewDate.setText(currentPeriod.minusDays(periodCounter - 1).toString("dd MMM yyyy"));
            }
        }

        period = currentDay;
        return period;
    }

    @Override
    public void onClick(View v)
    {
        if(!tableIsVisible)
        {
            table.setVisibility(View.GONE);
            tableIsVisible = true;
            tableToggler.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    ResourcesCompat.getDrawable(getResources(), R.drawable.ic_keyboard_arrow_down_white_18dp, null), null);

        }else
        {
            table.setVisibility(View.VISIBLE);
            tableIsVisible = false;
            tableToggler.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    ResourcesCompat.getDrawable(getResources(), R.drawable.ic_keyboard_arrow_up_white_18dp, null), null);
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
            DecimalFormat numberFormat = new DecimalFormat("0.00");
            String percentVisits = numberFormat.format(tempPercent) + " %";

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

    public boolean hasNetworkConnection()
    {
        boolean isConnectedWifi = false;
        boolean isConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null) // connected to the internet
        {
            if (netInfo.getType() == ConnectivityManager.TYPE_WIFI)
            {
                isConnectedWifi = true;
            }
            if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                isConnectedMobile = true;
            }
        }
        return isConnectedWifi || isConnectedMobile;
    }

    private ArrayList<String> getXAxisValues() {
        xAxis = new ArrayList<>();

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
        chart.setVisibility(View.VISIBLE);
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
            if(getActivity() != null)
            {
                if(response == null) {
                    response = "THERE WAS AN ERROR";
                }
                if(landscapeMode)
                {
                    if(secondCall)
                    {
                        progressBar.setVisibility(View.GONE);
                    }
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                }

                try
                {
                    JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray items = object.getJSONArray("items");
                    totalHours = items.length();

                    if(secondCall)
                    {
                        valueSet2 = new ArrayList<>();
                    }else
                    {
                        valueSet1 = new ArrayList<>();
                        tableValues = new ArrayList<>();
                        xAxis = new ArrayList<>();
                        totalVisits = 0;
                        table.removeAllViews();
                        table.addView(defaultTableRow);
                    }
                    if(totalHours == 0)
                    {
                        Toast.makeText(getActivity().getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
                        handleNoData(); //Reenable forward button and reset graph arrays
                    }
                    else
                    {
                        for (Integer i = 0; i < totalHours; i++)
                        {
                            int visits = items.getJSONObject(i).getInt("visits");

                            if(secondCall) //Yesterday
                            {

                                Entry entry = new Entry((float)visits, i);
                                valueSet2.add(entry);
                            }
                            else //Current Day
                            {

                                Entry entry = new Entry((float) visits, i);
                                valueSet1.add(entry);
                                tableValues.add(visits);
                                totalVisits = totalVisits + visits;
                            }

                        }

                        if(secondCall)
                        {
                            LineDataSet lineDataSet2 = new LineDataSet(valueSet2, "YESTERDAY");
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
                            Log.i("GGGGGGGG", tableValues.toString());
                            dataSets = new ArrayList<>();
                            LineDataSet lineDataSet1 = new LineDataSet(valueSet1, "TODAY");
                            Drawable drawable = ContextCompat.getDrawable(getActivity().getApplication(), R.drawable.chart_thisperiod_background);
                            lineDataSet1.setFillDrawable(drawable);
                            lineDataSet1.setColor(Color.rgb(5, 184, 198));
                            lineDataSet1.setDrawFilled(true);
                            lineDataSet1.setHighLightColor(Color.rgb(255,255,255));
                            dataSets.add(lineDataSet1);

                            // Setting Header Text to match VisitsToday Fragment.
                            textViewTotal.setText(String.valueOf(totalVisits));

                            if(landscapeMode)
                            {
                                secondCall = true;
                                API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                                        "/analytics/behavior/visits_by_hour?page=1&page_size=10&period=" + calculatePeriod(periodCounter+1);
                                new RetrieveFeedTask().execute();
                            }else
                            {
                                drawGraph();
                                createTable();
                            }
                        }
                        imgBtnBack.setClickable(true);
                        imgBtnBack.setAlpha(1f);
                        if(periodCounter != 0)
                        {
                            imgBtnForward.setClickable(true);
                            imgBtnForward.setAlpha(1f);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ClassCastException ce){
                    try{
                        Toast.makeText(getActivity().getApplicationContext(), "Invalid Data from API", Toast.LENGTH_SHORT).show();
                        handleNoData(); //Reenable forward button and reset graph arrays
                    }catch (NullPointerException nPE) {Log.i("DDDDDDDD","blaaa");}

                }
            }
        }

        private void handleNoData()
        {
            imgBtnForward.setClickable(true);
            imgBtnForward.setAlpha(1f);
            chart.setVisibility(View.VISIBLE);
            xAxis = new ArrayList<>();
            dataSets = new ArrayList<>();
            chart.clear();
        }
    }
}
