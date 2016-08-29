package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.page_views;

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

public class PageViewsMonthFragment extends Fragment implements View.OnClickListener
{
    ProgressBar progressBar;
    LineChart chart;
    ArrayList<ILineDataSet> dataSets;
    ArrayList<Entry> valueSet1;
    ArrayList<Entry> valueSet2;
    String API_URL = "";
    String period;
    private OnFragmentInteractionListener mListener;
    Integer totalMonthDays = 0, totalVisits;
    boolean landscapeMode;
    public static TextView textViewDate, textViewInfo, textViewTotal, tableToggler, columnOne, columnTwo;
    boolean apiIdSelected;
    TableLayout table;
    ArrayList<Integer> tableValues;
    ArrayList<String> tableWeekDays = new ArrayList<>();
    ArrayList<String> xAxis;
    boolean tableIsVisible = false;
    boolean secondCall = false;
    int totalDays, periodCounter;
    CustomMarkerViewPage mv;
    Button moreInfoButton;
    ImageButton imgBtnBack, imgBtnForward;
    TableRow defaultTableRow;

    public PageViewsMonthFragment()
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
        MainActivity.currentFragment = "Month";

        View rootView = inflater.inflate(R.layout.fragment_linechart, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        textViewDate = (TextView) rootView.findViewById(R.id.textViewDate);
        textViewInfo = (TextView) rootView.findViewById(R.id.textViewInfo);
        textViewTotal = (TextView) rootView.findViewById(R.id.textViewTotal);
        tableToggler = (TextView) rootView.findViewById(R.id.tableToggler);
        columnOne = (TextView) rootView.findViewById(R.id.columnOne);
        columnTwo = (TextView) rootView.findViewById(R.id.columnTwo);
        table = (TableLayout) rootView.findViewById(R.id.table);
        defaultTableRow = (TableRow) rootView.findViewById(R.id.defaultTableRow);
        moreInfoButton = (Button) rootView.findViewById(R.id.moreInfoButton);
        imgBtnBack = (ImageButton) rootView.findViewById(R.id.imgBtnBack);
        imgBtnForward = (ImageButton) rootView.findViewById(R.id.imgBtnForward);
        mv = new CustomMarkerViewPage(getActivity().getApplicationContext(), R.layout.custom_marker_view);

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

        columnOne.setText("Day of Month");
        columnTwo.setText("Page Views");
        textViewInfo.setText("PAGE VIEWS THIS MONTH");
        tableToggler.setCompoundDrawablesWithIntrinsicBounds(null, null,
                getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_white_18dp), null);
        moreInfoButton.setOnClickListener(this);

        table.setVisibility(View.GONE);

        periodCounter = 0;
        //Get date period for text view
        int daysOfMonth = new DateTime().getDayOfMonth();
        DateTime firstDayOfMonth = new DateTime().minusDays(daysOfMonth - 1);
        DateTime today = new DateTime().minusDays(1);
        String textDatePeriod = firstDayOfMonth.toString("dd MMMM") + " - " + today.toString("dd MMMM");
        textViewDate.setText(textDatePeriod);

        if(!MainActivity.API_ID.equalsIgnoreCase(""))
        {
            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                    "/analytics/behavior/visits_by_monthday?page=1&page_size=10&period="
                    + calculatePeriod(periodCounter);
            apiIdSelected = true;
        }else
        {
            apiIdSelected = false;
        }

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
            textViewInfo.setText("VISITS THIS MONTH");
            periodCounter--;
            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                    "/analytics/behavior/visits_by_monthday?page=1&page_size=10&period="
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
        textViewInfo.setText("VISITS THIS MONTH");
        periodCounter ++;
        API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                "/analytics/behavior/visits_by_monthday?page=1&page_size=10&period="
                + calculatePeriod(periodCounter);
        new RetrieveFeedTask().execute();
    }

    private String calculatePeriod(int periodCounter)
    {
        period = "";
        DateTime currentPeriod = new DateTime();
        String stopPeriod;
        int daysOfMonth = new DateTime().getDayOfMonth();
        DateTime firstDayOfMonth = new DateTime().minusDays(daysOfMonth - 1);

        if(periodCounter == 0)
        {
            stopPeriod = currentPeriod.minusMonths(periodCounter).toString("yyyyMMdd");
            textViewDate.setText(firstDayOfMonth.toString("dd MMM yyyy") + " - "
                    + currentPeriod.minusMonths(periodCounter).toString("dd MMM yyyy"));
        }else
        {
            stopPeriod = currentPeriod.minusMonths(periodCounter).dayOfMonth().withMaximumValue().toString("yyyyMMdd");
            if(!secondCall)
            {
                textViewDate.setText(firstDayOfMonth.minusMonths(periodCounter).toString("dd MMM yyyy") + " - "
                        + currentPeriod.minusMonths(periodCounter).dayOfMonth().withMaximumValue().toString("dd MMM yyyy"));
            }else
            {
                if(periodCounter == 1)
                {
                    textViewDate.setText(firstDayOfMonth.minusMonths(periodCounter - 1).toString("dd MMM yyyy") + " - "
                            + currentPeriod.toString("dd MMM yyyy"));
                }else
                {
                    textViewDate.setText(firstDayOfMonth.minusMonths(periodCounter - 1).toString("dd MMM yyyy") + " - "
                            + currentPeriod.minusMonths(periodCounter - 1).dayOfMonth().withMaximumValue().toString("dd MMM yyyy"));
                }
            }
        }

        String startPeriod = currentPeriod.minusMonths(periodCounter).toString("yyyyMM") + "01";
        period = startPeriod + "_" + stopPeriod;
        return period;
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
                    totalDays = items.length();

                    if(secondCall)
                    {
                        valueSet2 = new ArrayList<>();
                    }else
                    {
                        valueSet1 = new ArrayList<>();
                        tableValues = new ArrayList<>();
                        totalVisits = 0;
                        table.removeAllViews();
                        table.addView(defaultTableRow);
                        xAxis = new ArrayList<>();
                    }
                    if(totalDays == 0)
                    {
                        Toast.makeText(getActivity().getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
                        handleNoData(); //Reenable forward button and reset graph arrays
                    }else
                    {
                        for (Integer i = 0; i < totalDays; i++)
                        {
                            int visits = items.getJSONObject(i).getInt("page_views");


                            if(secondCall)
                            {
                                Entry entry = new Entry((float) visits, i);
                                valueSet2.add(entry);

                            }else
                            {
                                Entry entry = new Entry((float) visits, i);
                                valueSet1.add(entry);
                                tableValues.add(visits);
                                totalVisits = totalVisits + visits;
                            }
                        }

                        if (secondCall)
                        {
                            LineDataSet lineDataSet2 = new LineDataSet(valueSet2, "LAST MONTH");
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
                            dataSets = new ArrayList<>();
                            LineDataSet lineDataSet1 = new LineDataSet(valueSet1, "THIS MONTH");
                            lineDataSet1.setColor(Color.rgb(5, 184, 198));
                            Drawable drawable = ContextCompat.getDrawable(getActivity().getApplication(), R.drawable.chart_thisperiod_background);
                            lineDataSet1.setFillDrawable(drawable);
                            lineDataSet1.setDrawFilled(true);
                            lineDataSet1.setHighLightColor(Color.rgb(255,255,255));
                            dataSets.add(lineDataSet1);
                            Log.i("Total VISISTS", totalVisits.toString());
                            textViewTotal.setText(totalVisits.toString());
                            if(landscapeMode)
                            {
                                secondCall = true;
                                API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                                        "/analytics/behavior/visits_by_monthday?page=1&page_size=10&period="
                                        + calculatePeriod(periodCounter + 1);
                                new RetrieveFeedTask().execute();
                            }else
                            {
                                createTable();
                                drawGraph();
                            }
                            imgBtnBack.setClickable(true);
                            imgBtnBack.setAlpha(1f);
                            if(periodCounter != 0)
                            {
                                imgBtnForward.setClickable(true);
                                imgBtnForward.setAlpha(1f);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ClassCastException ce){
                    Toast.makeText(getActivity().getApplicationContext(), "Invalid Data from API", Toast.LENGTH_SHORT).show();
                    handleNoData(); //Reenable forward button and reset graph arrays
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
