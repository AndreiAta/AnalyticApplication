package dk.siteimprove.internship.atanasiu.andrei.analyticapplication;


import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Search_Engines.SearchEnginesFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Search_Engines.SearchEnginesMainFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Search_Engines.SearchEnginesWeekFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Social_Media.SocialMediaFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Social_Media.SocialMediaMainFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Social_Media.SocialMediaMonthFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Social_Media.SocialMediaWeekFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Social_Media.SocialMediaYearFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Visits.MainVisitsFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Visits.VisitsFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Visits.VisitsMonthFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Visits.VisitsWeekFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Visits.VisitsYearFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomePageFragment.OnFragmentInteractionListener,
        SocialMediaFragment.OnFragmentInteractionListener,
        SocialMediaWeekFragment.OnFragmentInteractionListener,
        SocialMediaMainFragment.OnFragmentInteractionListener,
        SocialMediaMonthFragment.OnFragmentInteractionListener,
        SocialMediaYearFragment.OnFragmentInteractionListener,
        SearchEnginesFragment.OnFragmentInteractionListener,
        SearchEnginesWeekFragment.OnFragmentInteractionListener,
        SearchEnginesMainFragment.OnFragmentInteractionListener,
        MainVisitsFragment.OnFragmentInteractionListener,
        VisitsWeekFragment.OnFragmentInteractionListener,
        VisitsMonthFragment.OnFragmentInteractionListener,
        VisitsFragment.OnFragmentInteractionListener,
        VisitsYearFragment.OnFragmentInteractionListener


{
    public static String API_ID; // Should perhaps have some getter/setter?
    public static String API_EMAIL;
    public static String API_KEY;
    EditText emailText;
    EditText apiKeyText;
    public static String initialLogin;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (initialLogin == null)
        {
            final Dialog dialog = new Dialog(this,R.style.full_screen_dialog);
            dialog.setContentView(R.layout.popup);
            dialog.setCancelable(false);
            Window window = dialog.getWindow();
           // dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

            emailText = (EditText) dialog.findViewById(R.id.emailTextField);
            apiKeyText = (EditText) dialog.findViewById(R.id.apiKeyTextField);
            Button button = (Button) dialog.findViewById(R.id.Button01);
            readFromFile();
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    initialLogin = "Logged in";
                    API_EMAIL = emailText.getText().toString();
                    API_KEY = apiKeyText.getText().toString();
                    String totalString = API_EMAIL + "=-==-" + API_KEY;
                    writeToFile(totalString);
                    readFromFile();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

        if (savedInstanceState == null) {
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = HomePageFragment.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void writeToFile(String message)
    {
        String file_name = "test_file";
        try
        {
            FileOutputStream fileOutputStream = openFileOutput(file_name, MODE_PRIVATE);
            fileOutputStream.write(message.getBytes());
            fileOutputStream.close();
            //Toast.makeText(getApplicationContext(), "Email Saved", Toast.LENGTH_LONG).show();
        }
        catch (FileNotFoundException e) {e.printStackTrace();} // TODO: Handle somehow?
        catch (IOException e) {e.printStackTrace();} // TODO: Handle somehow?
    }

    private void readFromFile()
    {
        try
        {
            String message;
            FileInputStream fileInputStream = openFileInput("test_file");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            while((message = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(message);
            }
            String tempString = stringBuffer.toString();
            if(tempString.contains("=-==-"))
            {
                String[] parts = tempString.split("=-==-");
                API_EMAIL = parts[0];
                API_KEY = parts[1];
                emailText.setText(API_EMAIL);
                apiKeyText.setText(API_KEY);
            }

            Toast.makeText(getApplicationContext(), API_EMAIL, Toast.LENGTH_LONG).show();

        }
        catch (FileNotFoundException e) {e.printStackTrace();}
        catch (IOException e) {e.printStackTrace();}
    }


    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.nav_camera)
        {
            fragmentClass = HomePageFragment.class;

        } else if (id == R.id.nav_gallery)
        {
            fragmentClass = MainVisitsFragment.class;

        } else if (id == R.id.nav_slideshow)
        {
            fragmentClass = SocialMediaMainFragment.class;


        } else if (id == R.id.nav_manage)
        {
            fragmentClass = SearchEnginesMainFragment.class;

        }else if (id == R.id.nav_send)
        {

        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
