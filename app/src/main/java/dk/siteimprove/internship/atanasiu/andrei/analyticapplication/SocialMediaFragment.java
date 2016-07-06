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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SocialMediaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SocialMediaFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SocialMediaFragment extends Fragment implements View.OnClickListener
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    EditText emailText;
    TextView responseView;
    ProgressBar progressBar;
    static final String API_KEY = "ebd8cdc10745831de07c286a9c6d967d";
    static final String API_URL = "https://api.siteimprove.com/v2/sites/73617/analytics/visitors/browsers";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SocialMediaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SocialMediaFragment newInstance(String param1, String param2) {
        SocialMediaFragment fragment = new SocialMediaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public SocialMediaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String>
    {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("LOADING");
        }

        protected String doInBackground(Void... urls) {
            String email = "andrei.atanasiu1994@gmail.com";
            // emailText.getText().toString();
            // Do some validation here


            try {
                URL url = new URL(API_URL  /*"email=" + email + "&apiKey=" + API_KEY*/);
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
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            //responseView.setText(response);

            // TODO: check this.exception
            // TODO: do something with the feed

            try {


                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray items = object.getJSONArray("items");
                String allitems = "";
                for(int i = 0; i < items.length(); i++)
                {
                    String browser_name = items.getJSONObject(i).getString("browser_name");
                    String visits = items.getJSONObject(i).getString("visits");
                    allitems = allitems +"Browser name: " + browser_name + "\n" + "Visits: " + visits + "\n\n" ;
                }



                responseView.setText(allitems);


                Log.i("IMPORTANT BBBBLLAAAAAA:",items.toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View rootView = inflater.inflate(R.layout.fragment_visits, container, false);
        responseView = (TextView) rootView.findViewById(R.id.responseView);
        //  emailText = (EditText) rootView.findViewById(R.id.emailText);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);


        new RetrieveFeedTask().execute();
        Button queryButton = (Button) rootView.findViewById(R.id.queryButton);

        queryButton.setOnClickListener(this);
        // Inflate the layout for this fragment
        return  rootView;
    }

    @Override
    public void onClick(View v)
    {
        new RetrieveFeedTask().execute();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
