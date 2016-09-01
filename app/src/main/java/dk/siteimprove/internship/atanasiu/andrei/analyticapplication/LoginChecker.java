package dk.siteimprove.internship.atanasiu.andrei.analyticapplication;


import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.View;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.entity.Site;

public class LoginChecker extends AsyncTask<Void, Void, String>
{
    private Exception exception;
    String API_URL = "https://api.siteimprove.com/v2/sites";

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
            MainActivity.signInButton.setClickable(true);
            MainActivity.signInButton.setAlpha(1);
            MainActivity.progressBarSignIn.setVisibility(View.GONE);
        }
        else
        {
            try {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray items = object.getJSONArray("items");
                MainActivity.websites = new ArrayList<>();

                for(int i = 0; i < items.length(); i++)
                {
                    if(items.get(i).toString().contains("\"visits\":"))
                    {
                        String site_name = items.getJSONObject(i).getString("site_name");
                        Integer id = items.getJSONObject(i).getInt("id");
                        int visits = items.getJSONObject(i).getInt("visits");

                        //MainActivity.websites.add(new Site(id, site_name, visits));

                        if(MainActivity.websites.size() == 0)
                        {
                            MainActivity.websites.add(new Site(id, site_name, visits));
                        }
                        else
                        {
                            for (int j = 0; j < MainActivity.websites.size() ; j++)
                            {
                                if(MainActivity.websites.get(j).getVisits() < visits)
                                {
                                    MainActivity.websites.add(j, new Site(id, site_name, visits));
                                    break;
                                }
                                else if(j == MainActivity.websites.size()-1)
                                {
                                    MainActivity.websites.add(new Site(id, site_name, visits));
                                    break;
                                }
                            }
                        }


                    }

                }

                // Login is a success?
                MainActivity.initialLogin = "Logged in!";
                MainActivity.dialog.dismiss();
                MainActivity.API_ID = String.valueOf(MainActivity.websites.get(0).getId());
                MainActivity.menuSiteName.setText(MainActivity.websites.get(0).getSiteName());


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClassCastException ce){
                // Handle No Login -- or response?
                MainActivity.loginAlert.setText("Login Failed - ClassCastException");
                MainActivity.signInButton.setClickable(true);
                MainActivity.signInButton.setAlpha(1);
                MainActivity.progressBarSignIn.setVisibility(View.GONE);
            }
        }
        Log.i("INFO", response);
    }
}

