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
import java.text.DecimalFormat;
import java.util.ArrayList;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;

public class PageViewsWeekFragment extends Fragment implements View.OnClickListener
{
    private OnFragmentInteractionListener mListener;
    ProgressBar progressBar;
    LineChart chart;
    ArrayList<ILineDataSet> dataSets;
    ArrayList<Entry> valueSet1;
    ArrayList<Entry> valueSet2;
    String API_URL = "";
    Boolean secondCall = false;
    int dayOfWeek;
    Integer totalVisits;
    String thisWeekCompareMonDate;
    String lastWeekCompareMonDate;
    DateTime startOfWeek;
    String lastSunday;
    boolean landscapeMode;
    public static TextView textViewDate, textViewInfo, textViewTotal, tableToggler, columnTwo;
    boolean apiIdSelected;
    TableLayout table;
    ArrayList<Integer> tableValues;
    public static ArrayList<String> tableWeekDays = new ArrayList<>();
    boolean tableIsVisible = false;
    CustomMarkerViewPage mv;
    Button moreInfoButton;

    public PageViewsWeekFragment()
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
        MainActivity.currentFragment = "Week";

        //Get time period for the API Call
        dayOfWeek = new DateTime().getDayOfWeek();
        DateTime currentDay = new DateTime();
        String currentDate = currentDay.toString("yyyy-MM-dd");
        currentDate = currentDate.replace("-", "");
        startOfWeek = new DateTime().minusDays(dayOfWeek - 1);
        lastSunday = startOfWeek.minusDays(1).toString("dd");
        lastWeekCompareMonDate = startOfWeek.minusDays(7).toString("dd");
        String mondayDate = startOfWeek.toString("yyyy-MM-dd");
        mondayDate = mondayDate.replace("-", "");
        thisWeekCompareMonDate = startOfWeek.toString("dd");
        String period = mondayDate + "_" + currentDate;
        //Get Time Period for the Text View
        String textDatePeriod = startOfWeek.toString("dd MMMM") + " - " + currentDay.toString("dd MMMM");



        if(!MainActivity.API_ID.equalsIgnoreCase(""))
        {
            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                    "/analytics/behavior/visits_by_weekday?page=1&page_size=10&period=" + period;
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
        columnTwo = (TextView) rootView.findViewById(R.id.columnTwo);
        moreInfoButton = (Button) rootView.findViewById(R.id.moreInfoButton);
        mv = new CustomMarkerViewPage(getActivity().getApplicationContext(), R.layout.custom_marker_view);

        tableToggler.setCompoundDrawablesWithIntrinsicBounds(null, null,
                getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_white_18dp), null);
        moreInfoButton.setOnClickListener(this);

        table = (TableLayout) rootView.findViewById(R.id.table);
        table.setVisibility(View.GONE);
        tableWeekDays.add("Monday");
        tableWeekDays.add("Tuesday");
        tableWeekDays.add("Wednesday");
        tableWeekDays.add("Thursday");
        tableWeekDays.add("Friday");
        tableWeekDays.add("Saturday");
        tableWeekDays.add("Sunday");

        textViewDate.setText(textDatePeriod);
        textViewInfo.setText("PAGE VIEWS THIS WEEK");
        columnTwo.setText("Page Views");
        totalVisits = 0;

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

    public void createTable()
    {
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

    private ArrayList<String> getXAxisValues()
    {
        ArrayList<String> xAxis = new ArrayList<>();

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
//                Integer thisMonDate = Integer.parseInt(thisWeekCompareMonDate);
//                Integer lastMonDate = Integer.parseInt(lastWeekCompareMonDate);
                int totalDays = items.length();
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
                        int visits = items.getJSONObject(i).getInt("page_views");

                        //Check if you are doing the current week or last week
                        //then check if any entries are missing and create them
                        if (secondCall)
                        {
                            //Last Week
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
                                    "/analytics/behavior/visits_by_weekday?page=1&page_size=10&period=lastweek";
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