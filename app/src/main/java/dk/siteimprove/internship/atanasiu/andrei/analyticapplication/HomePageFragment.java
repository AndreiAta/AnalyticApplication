package dk.siteimprove.internship.atanasiu.andrei.analyticapplication;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HomePageFragment extends Fragment
{
    public TextView textView;
    Spinner spinner;
    static final String API_KEY = "ebd8cdc10745831de07c286a9c6d967d";
    static final String API_URL = "https://api.siteimprove.com/v2/sites";
    ArrayList<String> siteNames = new ArrayList<>();
    ArrayList<String> siteIds = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public HomePageFragment() {   }   // Required empty public constructor


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        getActivity().setTitle("Home Page");
        View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);
        new RetrieveFeedTask().execute();

        siteNames.add("Select Site");
        siteIds.add("Test");
        spinner = (Spinner) rootView.findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, siteNames);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
        textView = (TextView) rootView.findViewById(R.id.homePageText);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Integer myInteger = spinner.getSelectedItemPosition();
                textView.setText(siteIds.get(myInteger));
                MainActivity.API_ID = siteIds.get(myInteger);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });



        // Inflate the layout for this fragment
        return rootView;
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

    class RetrieveFeedTask extends AsyncTask<Void, Void, String>
    {

        private Exception exception;

        protected void onPreExecute() {


        }

        protected String doInBackground(Void... urls) {
            String email = "andrei.atanasiu1994@gmail.com";


            try {
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String credentials = email + ":" + API_KEY;
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
            Log.i("INFO", response);

            try {


                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray items = object.getJSONArray("items");
                for(int i = 0; i < items.length(); i++)
                {
                    String site_name = items.getJSONObject(i).getString("site_name");
                    Integer id = items.getJSONObject(i).getInt("id");

                    siteNames.add(site_name);
                    siteIds.add(id.toString());

                }
                spinner.setSelection(1);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }




}
