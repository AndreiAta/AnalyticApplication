package dk.siteimprove.internship.atanasiu.andrei.analyticapplication;


import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginChecker extends AsyncTask<Void, Void, String>
{
    final String API_URL = "https://api.siteimprove.com/v2/sites";

    private Exception exception;

    protected void onPreExecute() {


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
            MainActivity.loginAlert.setText("Login Failed - Email & Key doesn't match");
        }
        else
        {
            try {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray items = object.getJSONArray("items");
                for(int i = 0; i < items.length(); i++)
                {
                    String site_name = items.getJSONObject(i).getString("site_name");
                    Integer id = items.getJSONObject(i).getInt("id");
                }

                // Login is a sucess?
                MainActivity.initialLogin = "Logged in!";
                MainActivity.dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClassCastException ce){
                // Handle No Login -- or response?
                MainActivity.loginAlert.setText("Login Failed - ClassCastException");
            }
        }
        Log.i("INFO", response);





    }
}

