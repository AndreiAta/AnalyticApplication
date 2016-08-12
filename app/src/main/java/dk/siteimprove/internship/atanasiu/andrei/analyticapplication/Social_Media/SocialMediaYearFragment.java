package dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Social_Media;

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


public class SocialMediaYearFragment extends Fragment implements View.OnClickListener
{
    HorizontalBarChart chart;
    ArrayList<BarDataSet> dataSets;
    ArrayList<String> xAxis;
    public static ArrayList<String> xAxisLabels;
    public static ProgressBar progressBar;
    String API_URL = "";
    private OnFragmentInteractionListener mListener;
    public static TextView textViewDate, textViewInfo, textViewTotal, tableToggler, columnOne;
    TableLayout table;
    ArrayList<Integer> tableValues;
    ArrayList<BarEntry> valueSet1;
    ArrayList<BarEntry> valueSet2;
    boolean secondCall = false;
    boolean tableIsVisible = false;
    boolean landscapeMode, apiIdSelected;
    int totalVisits, totalSocialMedia;
    int[] tempValSet2 = new int[100];
    String lastYear;
    CustomMarkerViewSocial mv;


    public SocialMediaYearFragment()
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
        DateTime thisYear = new DateTime().minusYears(1);
        lastYear = thisYear.toString("yyyy");

        MainActivity.currentFragment = "Year";

        if(!MainActivity.API_ID.equalsIgnoreCase(""))
        {
            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                    "/analytics/traffic_sources/social_media_organisations?page=1&page_size=10&period=thisyear";
            apiIdSelected = true;
        }else
        {
            apiIdSelected = false;
        }
        View rootView = inflater.inflate(R.layout.fragment_barchart, container, false); // Inflate the layout for this fragment
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        chart = (HorizontalBarChart) rootView.findViewById(R.id.chart);
        textViewDate = (TextView) rootView.findViewById(R.id.textViewDate);
        textViewInfo = (TextView) rootView.findViewById(R.id.textViewInfo);
        textViewTotal = (TextView) rootView.findViewById(R.id.textViewTotal);
        tableToggler = (TextView) rootView.findViewById(R.id.tableToggler);
        columnOne = (TextView) rootView.findViewById(R.id.columnOne);
        table = (TableLayout) rootView.findViewById(R.id.table);
        mv = new CustomMarkerViewSocial(getActivity().getApplicationContext(), R.layout.custom_marker_view);

        textViewDate.setText("0 - 0");
        textViewInfo.setText("VISITS THIS YEAR");
        tableToggler.setGravity(Gravity.LEFT);
        tableToggler.setCompoundDrawablesWithIntrinsicBounds(null, null,
                getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_white_18dp), null);
        columnOne.setText("Social Media");
        tableToggler.setOnClickListener(this);
        table.setVisibility(View.GONE);

        totalVisits = 0;

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
            DecimalFormat numberFormat = new DecimalFormat("#.00");
            String percentVisits = "% " + numberFormat.format(tempPercent);

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

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void drawGraph()
    {
        BarData data = new BarData(xAxisLabels, dataSets);
        chart.setData(data);
        chart.setDescription("");
        chart.animateXY(1000, 1000);
        chart.invalidate();
        chart.setBackgroundColor(Color.rgb(68, 68, 68));
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
            Log.i("ERROR", response);

            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);

            try
            {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray items = object.getJSONArray("items");
                totalSocialMedia = items.length();
                int numberorg = 0;

                if(secondCall)
                {
                    valueSet2 = new ArrayList<>();
                    //Filling the array with 0
                    for (int i = 0; i < totalSocialMedia ; i++)
                    {
                        tempValSet2[i] = 0;
                    }
                }else
                {
                    valueSet1 = new ArrayList<>();
                    xAxis = new ArrayList<>();
                    xAxisLabels = new ArrayList<>();
                    tableValues = new ArrayList<>();
                }

                if(totalSocialMedia == 0)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "No Data Available", Toast.LENGTH_LONG).show();
                }
                else
                {
                    for (int i = 0; i < totalSocialMedia; i++)
                    {
                        Integer visits = items.getJSONObject(i).getInt("visits");
                        String organisation = items.getJSONObject(i).getString("organisation");

                        if (secondCall) //LAST YEAR
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
                                for (int j = 0; j < totalSocialMedia; j++)
                                {
                                    BarEntry entry = new BarEntry(tempValSet2[j], j);
                                    valueSet2.add(entry);
                                }
                            }
                        } else //THIS YEAR
                        {
                            if (i < 10)
                            {
                                BarEntry entry = new BarEntry((float) visits, numberorg);
                                valueSet1.add(entry);
                                xAxis.add(organisation);
                                if (organisation.length() > 20)
                                {
                                    xAxisLabels.add(organisation.substring(0, 19) + "...");
                                } else
                                {
                                    xAxisLabels.add(organisation);
                                }
                                numberorg++;
                                tableValues.add(visits);
                                totalVisits = totalVisits + visits;
                            } else
                            {
                                if (!secondCall)
                                {
                                    totalVisits = totalVisits + visits;
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
                        drawGraph();

                        secondCall = false;
                    }else
                    {
                        dataSets = new ArrayList<>();
                        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "THIS YEAR");
                        barDataSet1.setColor(Color.rgb(5, 184, 198));
                        barDataSet1.setBarSpacePercent(50f);
                        dataSets.add(barDataSet1);
                        textViewTotal.setText(String.valueOf(totalVisits));

                        if(landscapeMode)
                        {
                            secondCall = true;
                            API_URL = "https://api.siteimprove.com/v2/sites/" + MainActivity.API_ID +
                                    "/analytics/traffic_sources/social_media_organisations?page=1&page_size=10&period="
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
