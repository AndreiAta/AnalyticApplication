package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.social_media;

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
import java.util.Collections;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.MainActivity;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.R;


public class SocialMediaMonthFragment extends Fragment implements View.OnClickListener
{
    HorizontalBarChart chart;
    ArrayList<IBarDataSet> dataSets;
    ArrayList<String> xAxis;
    public static ArrayList<String> xAxisLabels;
    public static ProgressBar progressBar;
    String API_URL = "";
    String period;
    private OnFragmentInteractionListener mListener;
    public static TextView textViewDate, textViewInfo, textViewTotal, tableToggler, columnOne;
    TableLayout table;
    ArrayList<Integer> tableValues;
    ArrayList<BarEntry> valueSet1;
    ArrayList<BarEntry> valueSet2;
    boolean secondCall = false;
    boolean tableIsVisible = false;
    boolean landscapeMode, apiIdSelected;
    int totalVisits, totalSocialMedia, periodCounter;
    int[] tempValSet2 = new int[100];
    CustomMarkerViewSocial mv;
    Button moreInfoButton;
    ImageButton imgBtnBack, imgBtnForward;
    TableRow defaultTableRow;

    public SocialMediaMonthFragment()
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
        periodCounter = 0;

        View rootView = inflater.inflate(R.layout.fragment_barchart, container, false); // Inflate the layout for this fragment
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        chart = (HorizontalBarChart) rootView.findViewById(R.id.chart);
        textViewDate = (TextView) rootView.findViewById(R.id.textViewDate);
        textViewInfo = (TextView) rootView.findViewById(R.id.textViewInfo);
        textViewTotal = (TextView) rootView.findViewById(R.id.textViewTotal);
        tableToggler = (TextView) rootView.findViewById(R.id.tableToggler);
        columnOne = (TextView) rootView.findViewById(R.id.columnOne);
        table = (TableLayout) rootView.findViewById(R.id.table);
        defaultTableRow = (TableRow) rootView.findViewById(R.id.defaultTableRow);
        moreInfoButton = (Button) rootView.findViewById(R.id.moreInfoButton);
        imgBtnBack = (ImageButton) rootView.findViewById(R.id.imgBtnBack);
        imgBtnForward = (ImageButton) rootView.findViewById(R.id.imgBtnForward);
        mv = new CustomMarkerViewSocial(getActivity().getApplicationContext(), R.layout.custom_marker_view);

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

        textViewDate.setText("0 - 0");
        textViewInfo.setText("VISITS THIS MONTH");
        tableToggler.setGravity(Gravity.LEFT);
        tableToggler.setCompoundDrawablesWithIntrinsicBounds(null, null,
                getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_white_18dp), null);

        columnOne.setText("Social Media");
        moreInfoButton.setOnClickListener(this);
        table.setVisibility(View.GONE);

        if(!MainActivity.API_ID.equalsIgnoreCase(""))
        {
            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                    "/analytics/traffic_sources/social_media_organisations?page=1&page_size=10&period=" +
                    calculatePeriod(periodCounter);
            apiIdSelected = true;
        }else
        {
            apiIdSelected= false;
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

        return rootView;
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
                    "/analytics/traffic_sources/social_media_organisations?page=1&page_size=10&period="
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
                "/analytics/traffic_sources/social_media_organisations?page=1&page_size=10&period="
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

    public void createTable()
    {

        for (int i = 0; i < xAxis.size() ; i++)
        {
            TableRow[] tableRow = new TableRow[xAxis.size()];
            tableRow[i] = new TableRow(getActivity());
            tableRow[i].setPadding(40, 40, 40, 40);

            TextView hourOfDayTxt = new TextView(getActivity());
            hourOfDayTxt.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            hourOfDayTxt.setText(xAxis.get(i).toString());
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
        Collections.reverse(xAxisLabels);
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

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setTextColor(Color.WHITE);
        data.setValueTextSize(0f);
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
                    totalSocialMedia = items.length();
                    int pos = 0;

                    if(secondCall)
                    {
                        valueSet2 = new ArrayList<>();

                        for (int i = 0; i < totalSocialMedia ; i++)
                        {
                            tempValSet2[i] = 0;
                        }
                    }else
                    {
                        totalVisits = 0;
                        valueSet1 = new ArrayList<>();
                        xAxis = new ArrayList<>();
                        xAxisLabels = new ArrayList<>();
                        tableValues = new ArrayList<>();
                        table.removeAllViews();
                        table.addView(defaultTableRow);
                    }
                    if(totalSocialMedia == 0)
                    {
                        Toast.makeText(getActivity().getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
                        handleNoData(); //Reenable forward button and reset graph arrays
                    }
                    else
                    {
                        for (int i = 0; i < totalSocialMedia; i++)
                        {
                            Integer visits = items.getJSONObject(i).getInt("visits");
                            String organisation = items.getJSONObject(i).getString("organisation");

                            if (!secondCall) //This Period
                            {
                                if (i < 10)
                                {
                                    BarEntry entry = new BarEntry((float) visits, pos);
                                    valueSet1.add(entry);
                                    xAxis.add(organisation);
                                    if (organisation.length() > 20)
                                    {
                                        xAxisLabels.add(organisation.substring(0, 19) + "...");
                                    } else
                                    {
                                        xAxisLabels.add(organisation);
                                    }
                                    pos++;
                                    tableValues.add(visits);
                                    totalVisits = totalVisits + visits;
                                } else
                                {
                                    totalVisits = totalVisits + visits;
                                }

                            }
                            else //Last Period
                            {
                                if (xAxis.contains(organisation))
                                {
                                    tempValSet2[xAxis.indexOf(organisation)] = visits;
                                } else if (!xAxis.contains(organisation) && xAxis.size() < 10)
                                {
                                    xAxis.add(organisation);
                                    if (organisation.length() > 20)
                                    {
                                        xAxisLabels.add(organisation.substring(0, 19) + "...");
                                    } else
                                    {
                                        xAxisLabels.add(organisation);
                                    }
                                    tempValSet2[xAxis.indexOf(organisation)] = visits;
                                }
                                if (i == totalSocialMedia - 1)
                                {
                                    for (int j = 0; j < xAxis.size(); j++)
                                    {
                                        BarEntry entry = new BarEntry(tempValSet2[j], j);
                                        valueSet2.add(entry);
                                    }
                                }
                            }
                        }
                        if(!secondCall)
                        {
                            textViewTotal.setText(String.valueOf(totalVisits));

                            if(landscapeMode)
                            {
                                secondCall = true;
                                API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                                        "/analytics/traffic_sources/social_media_organisations?page=1&page_size=10&period="
                                        + calculatePeriod(periodCounter + 1);
                                new RetrieveFeedTask().execute();
                            }else
                            {
                                reverseXPosInList(valueSet1);
                                Collections.reverse(valueSet1);
                                dataSets = new ArrayList<>();
                                BarDataSet barDataSet1 = new BarDataSet(valueSet1, "THIS MONTH");
                                barDataSet1.setColor(Color.rgb(5, 184, 198));
                                barDataSet1.setBarSpacePercent(50f);
                                dataSets.add(barDataSet1);

                                createTable();
                                drawGraph();
                            }

                        }else
                        {
                            reverseXPosInList(valueSet1);
                            Collections.reverse(valueSet1);
                            dataSets = new ArrayList<>();
                            BarDataSet barDataSet1 = new BarDataSet(valueSet1, "THIS MONTH");
                            barDataSet1.setColor(Color.rgb(5, 184, 198));
                            barDataSet1.setBarSpacePercent(50f);
                            dataSets.add(barDataSet1);


                            reverseXPosInList(valueSet2);
                            Collections.reverse(valueSet2);
                            BarDataSet barDataSet2 = new BarDataSet(valueSet2, "LAST MONTH");
                            barDataSet2.setColor(Color.rgb(181, 0, 97)); //TODO USE R.COLOR
                            barDataSet2.setBarSpacePercent(50f);
                            dataSets.add(barDataSet2);
                            drawGraph();

                            secondCall = false;
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
        }

        private void handleNoData()
        {
            imgBtnForward.setClickable(true);
            imgBtnForward.setAlpha(1f);
            chart.setVisibility(View.VISIBLE);
            xAxisLabels = new ArrayList<>();
            dataSets = new ArrayList<>();
            chart.clear();
        }

        private void reverseXPosInList(ArrayList<BarEntry> list)
        {
            int xLabelSize = xAxisLabels.size()-1;
            int listSize = list.size()-1;

            for (int i = 0; i <= listSize; i++)
            {
                int pos = list.get(i).getXIndex();
                list.get(i).setXIndex(xLabelSize-pos);
            }
        }
    }
}
