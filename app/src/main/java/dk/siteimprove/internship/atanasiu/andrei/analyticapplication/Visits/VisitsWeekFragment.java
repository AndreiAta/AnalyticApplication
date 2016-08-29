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
import java.text.DecimalFormat;
import java.util.ArrayList;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class VisitsWeekFragment extends Fragment implements View.OnClickListener
{
    private OnFragmentInteractionListener mListener;
    ProgressBar progressBar;
    LineChart chart;
    ArrayList<ILineDataSet> dataSets;
    ArrayList<Entry> valueSet1;
    ArrayList<Entry> valueSet2;
    String API_URL = "";
    Boolean secondCall = false;
    int periodCounter;
    Integer totalVisits;
    DateTime startOfWeek;
    String period;
    boolean landscapeMode;
    public static TextView textViewDate, textViewInfo, textViewTotal, tableToggler;
    boolean apiIdSelected;
    TableLayout table;
    ArrayList<Integer> tableValues;
    public static ArrayList<String> tableWeekDays = new ArrayList<>();
    boolean tableIsVisible = false;
    CustomMarkerViewVisits mv;
    Button moreInfoButton;
    ImageButton imgBtnBack, imgBtnForward;
    TableRow defaultTableRow;
    ArrayList<String> xAxis;

    public VisitsWeekFragment()
    {
        // Required empty public constructor
    }

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
        MainActivity.currentFragment = "Week";

        View rootView = inflater.inflate(R.layout.fragment_linechart, container, false);
        chart = (LineChart) rootView.findViewById(R.id.chart);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        textViewDate = (TextView) rootView.findViewById(R.id.textViewDate);
        textViewInfo = (TextView) rootView.findViewById(R.id.textViewInfo);
        textViewTotal = (TextView) rootView.findViewById(R.id.textViewTotal);
        tableToggler = (TextView) rootView.findViewById(R.id.tableToggler);
        moreInfoButton = (Button) rootView.findViewById(R.id.moreInfoButton);
        defaultTableRow = (TableRow) rootView.findViewById(R.id.defaultTableRow);
        imgBtnBack = (ImageButton) rootView.findViewById(R.id.imgBtnBack);
        imgBtnForward = (ImageButton) rootView.findViewById(R.id.imgBtnForward);
        mv = new CustomMarkerViewVisits(getActivity().getApplicationContext(), R.layout.custom_marker_view);

        imgBtnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getNextPeriod();
            }
        });
        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPreviousPeriod();
            }
        });

        moreInfoButton.setOnClickListener(this);
        tableToggler.setCompoundDrawablesWithIntrinsicBounds(null, null,
                getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_white_18dp), null);

        table = (TableLayout) rootView.findViewById(R.id.table);
        table.setVisibility(View.GONE);

        totalVisits = 0;

        if(!MainActivity.API_ID.equalsIgnoreCase(""))
        {
            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                    "/analytics/behavior/visits_by_weekday?page=1&page_size=10&period=" +
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
            Toast.makeText(getActivity().getApplicationContext(), "You have no Internet", Toast.LENGTH_SHORT).show();
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
        if(periodCounter != 0)
        {
            imgBtnBack.setClickable(false);
            imgBtnBack.setAlpha(0.5f);
            imgBtnForward.setClickable(false);
            imgBtnForward.setAlpha(0.5f);
            chart.setVisibility(View.INVISIBLE);
            textViewInfo.setText("VISITS THIS WEEK");
            periodCounter--;
            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                    "/analytics/behavior/visits_by_weekday?page=1&page_size=10&period="
                    + calculatePeriod(periodCounter);
            new RetrieveFeedTask().execute();
        }
    }

    private void getPreviousPeriod()
    {
        imgBtnBack.setClickable(false);
        imgBtnBack.setAlpha(0.5f);
        imgBtnForward.setClickable(false);
        imgBtnForward.setAlpha(0.5f);
        chart.setVisibility(View.INVISIBLE);
        textViewInfo.setText("VISITS THIS WEEK");
        periodCounter ++;
        API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                "/analytics/behavior/visits_by_weekday?page=1&page_size=10&period="
                + calculatePeriod(periodCounter);
        new RetrieveFeedTask().execute();
    }

    private String calculatePeriod(int periodCounter)
    {
        period = "";
        DateTime currentPeriod = new DateTime();
        String startPeriod = "";
        int dayOfWeek = new DateTime().getDayOfWeek();
        startPeriod = currentPeriod.minusDays(dayOfWeek - 1).toString("yyyyMMdd");
        String stopPeriod = currentPeriod.toString("yyyyMMdd");
        textViewDate.setText(currentPeriod.minusDays(dayOfWeek - 1).toString("dd MMM yyyy")
                + " - " + currentPeriod.toString("dd MMM yyyy"));
        if(periodCounter != 0)
        {
            startPeriod = currentPeriod.minusDays(dayOfWeek - 1).minusWeeks(periodCounter).toString("yyyyMMdd");
            stopPeriod = currentPeriod.minusDays(dayOfWeek - 1).plusDays(6).minusWeeks(periodCounter).toString("yyyyMMdd");
            textViewDate.setText(currentPeriod.minusDays(dayOfWeek - 1).minusWeeks(periodCounter).toString("dd MMM yyyy")
                    + " - " + currentPeriod.minusDays(dayOfWeek - 1).plusDays(6).minusWeeks(periodCounter).toString("dd MMM yyyy"));
            if(secondCall)
            {
                if(periodCounter == 1)
                {
                    textViewDate.setText(currentPeriod.minusDays(dayOfWeek - 1).toString("dd MMM yyyy")
                            + " - " + currentPeriod.toString("dd MMM yyyy"));
                }else
                {
                    textViewDate.setText(currentPeriod.minusDays(dayOfWeek - 1).minusWeeks(periodCounter - 1).toString("dd MMM yyyy")
                            + " - "
                            + currentPeriod.minusDays(dayOfWeek - 1).plusDays(6).minusWeeks(periodCounter -1).toString("dd MMM yyyy"));
                }
            }
        }

        period = startPeriod + "_" + stopPeriod;
        return period;
    }

    public void createTable()
    {
        tableWeekDays.add("Monday");
        tableWeekDays.add("Tuesday");
        tableWeekDays.add("Wednesday");
        tableWeekDays.add("Thursday");
        tableWeekDays.add("Friday");
        tableWeekDays.add("Saturday");
        tableWeekDays.add("Sunday");

        for (int i = tableValues.size(); i < 7; i++)
        {
            tableValues.add(0);
        }

        for (int i = 0; i <7 ; i++)
        {
            TableRow[] tableRow = new TableRow[7];
            tableRow[i] = new TableRow(getActivity());
            tableRow[i].setPadding(40,40,40,40);


            TextView weekDay = new TextView(getActivity());
            weekDay.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            weekDay.setText(tableWeekDays.get(i));
            weekDay.setTextColor(Color.WHITE);

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

            tableRow[i].addView(weekDay);
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

    private ArrayList<String> getXAxisValues()
    {
        xAxis = new ArrayList<>();

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
        chart.animateXY(1000, 1000);
        chart.invalidate();
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
                int totalDays = items.length();

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

                if(totalDays == 0)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
                    handleNoData(); //Reenable forward button and reset graph arrays
                }
                else
                {
                    for (Integer i = 0; i < totalDays; i++)
                    {
                        int visits = items.getJSONObject(i).getInt("visits");
                        int day_of_week = items.getJSONObject(i).getInt("day_of_week");

                        //Check if you are doing the current week or last week
                        //then check if any entries are missing and create them
                        if (secondCall)//Last Week
                        {
                            Entry entry = new Entry((float)visits, i);
                            valueSet2.add(entry);

                        } else  //Current Week
                        {
                            Entry entry = new Entry((float)visits, i);
                            valueSet1.add(entry);
                            tableValues.add(visits);
                            totalVisits = totalVisits + visits;
                        }
                    }

                    if (secondCall)
                    {
                        LineDataSet lineDataSet2 = new LineDataSet(valueSet2, "LAST WEEK");
                        lineDataSet2.setColor(Color.rgb(181, 0, 97)); //TODO USE R.COLOR
                        Drawable drawable = ContextCompat.getDrawable(getActivity().getApplication(), R.drawable.chart_lastperiod_background);
                        lineDataSet2.setFillDrawable(drawable);
                        lineDataSet2.setDrawFilled(true);
                        lineDataSet2.setHighLightColor(Color.rgb(255,255,255));
                        dataSets.add(lineDataSet2);
                        drawGraph();

                        secondCall = false;
                    } else
                    {
                        Log.i("XXXXX", String.valueOf(totalVisits));
                        dataSets = new ArrayList<>();
                        LineDataSet lineDataSet1 = new LineDataSet(valueSet1, "THIS WEEK");
                        lineDataSet1.setColor(Color.rgb(5, 184, 198));
                        Drawable drawable = ContextCompat.getDrawable(getActivity().getApplication(), R.drawable.chart_thisperiod_background);
                        lineDataSet1.setFillDrawable(drawable);
                        lineDataSet1.setDrawFilled(true);
                        lineDataSet1.setHighLightColor(Color.rgb(255,255,255));
                        dataSets.add(lineDataSet1);
                        textViewTotal.setText(totalVisits.toString());
                        if(landscapeMode)
                        {
                            secondCall = true;
                            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                                    "/analytics/behavior/visits_by_weekday?page=1&page_size=10&period=" + calculatePeriod(periodCounter+1);
                            new RetrieveFeedTask().execute();
                        }else
                        {
                            createTable();
                            drawGraph();
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
                Toast.makeText(getActivity().getApplicationContext(), "Invalid Data from API", Toast.LENGTH_SHORT).show();
                handleNoData(); //Reenable forward button and reset graph arrays

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