package dk.siteimprove.internship.atanasiu.andrei.analyticapplication;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Most_Popular_Pages.PopPagesFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Most_Popular_Pages.PopPagesMainFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Most_Popular_Pages.PopPagesMonthFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Most_Popular_Pages.PopPagesWeekFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Most_Popular_Pages.PopPagesYearFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Page_Views.PageViewsFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Page_Views.PageViewsMainFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Page_Views.PageViewsMonthFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Page_Views.PageViewsWeekFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Page_Views.PageViewsYearFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Search_Engines.SearchEnginesFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Search_Engines.SearchEnginesMonthFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Search_Engines.SearchEnginesMainFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Search_Engines.SearchEnginesWeekFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Search_Engines.SearchEnginesYearFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Social_Media.SocialMediaFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Social_Media.SocialMediaMainFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Social_Media.SocialMediaMonthFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Social_Media.SocialMediaWeekFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Social_Media.SocialMediaYearFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Traffic_Sources.TrafficSourcesFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Traffic_Sources.TrafficSourcesMainFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Traffic_Sources.TrafficSourcesMonthFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Traffic_Sources.TrafficSourcesWeekFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Traffic_Sources.TrafficSourcesYearFragment;
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
        SearchEnginesMonthFragment.OnFragmentInteractionListener,
        SearchEnginesYearFragment.OnFragmentInteractionListener,
        SearchEnginesMainFragment.OnFragmentInteractionListener,
        MainVisitsFragment.OnFragmentInteractionListener,
        VisitsWeekFragment.OnFragmentInteractionListener,
        VisitsMonthFragment.OnFragmentInteractionListener,
        VisitsFragment.OnFragmentInteractionListener,
        VisitsYearFragment.OnFragmentInteractionListener,
        PopPagesMainFragment.OnFragmentInteractionListener,
        PopPagesFragment.OnFragmentInteractionListener,
        PopPagesWeekFragment.OnFragmentInteractionListener,
        PopPagesMonthFragment.OnFragmentInteractionListener,
        PopPagesYearFragment.OnFragmentInteractionListener,
        PageViewsMainFragment.OnFragmentInteractionListener,
        PageViewsFragment.OnFragmentInteractionListener,
        PageViewsWeekFragment.OnFragmentInteractionListener,
        PageViewsMonthFragment.OnFragmentInteractionListener,
        PageViewsYearFragment.OnFragmentInteractionListener,
        TrafficSourcesMainFragment.OnFragmentInteractionListener,
        TrafficSourcesFragment.OnFragmentInteractionListener,
        TrafficSourcesWeekFragment.OnFragmentInteractionListener,
        TrafficSourcesMonthFragment.OnFragmentInteractionListener,
        TrafficSourcesYearFragment.OnFragmentInteractionListener

{
    public static EditText emailText;
    public static String totalString, currentFragment, initialLogin, API_ID, API_EMAIL, API_KEY;
    EditText apiKeyText;
    public static TextView headerTxt, loginAlert, menuEmailTxt, menuSiteName;
    public static Dialog dialog;
    public Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawer;
    public static ArrayList<String> websites;
    public static ArrayList<Integer> siteIds;
    View header;
    ListView lv;
    public static Button signInButton;
    public static ProgressBar progressBarSignIn;


    ArrayList<String> spinnerList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        spinnerList.add("Siteimproving.com");
        spinnerList.add("Somethingelse.dk.uk.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        menuEmailTxt = (TextView) header.findViewById(R.id.menuMail);
        menuSiteName = (TextView) header.findViewById(R.id.menuSiteName);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if (initialLogin == null)
        {

            drawer.openDrawer(GravityCompat.START);
            notificationDialog();
            dialog = new Dialog(this,R.style.full_screen_dialog);
            dialog.setContentView(R.layout.popup);
            dialog.setCancelable(false);
            emailText = (EditText) dialog.findViewById(R.id.emailTextField);
            apiKeyText = (EditText) dialog.findViewById(R.id.apiKeyTextField);
            headerTxt = (TextView) dialog.findViewById(R.id.headerTxt);
            loginAlert = (TextView) dialog.findViewById(R.id.loginAlert);
            progressBarSignIn  = (ProgressBar) dialog.findViewById(R.id.progressBarSignIn);
            progressBarSignIn.setVisibility(View.GONE);
            signInButton = (Button) dialog.findViewById(R.id.SignInButton);

            readFromFile();
            signInButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    //initialLogin = "Logged in";
                    loginAlert.setText("");
                    signInButton.setAlpha(.5f);
                    signInButton.setClickable(false);
                    progressBarSignIn.setVisibility(View.VISIBLE);

                    if(haveNetworkConnection())
                    {
                        final LoginChecker lc = new LoginChecker();
                        lc.execute();
                        API_EMAIL = emailText.getText().toString();
                        API_KEY = apiKeyText.getText().toString();
                        totalString = API_EMAIL + "=-==-" + API_KEY; // Middle string is used to split, in read method.
                        writeToFile(totalString);

                        // Sets drawer menuMail textview to current user mail.

                        menuEmailTxt.setText(API_EMAIL);

                        // Opens the "homepage" fragment.
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
                    else
                    {
                        Toast.makeText(getApplicationContext(), "YOU HAVE NO INTERNET!", Toast.LENGTH_SHORT).show();
                        loginAlert.setText("Login Failed - No Internet");
                        signInButton.setAlpha(1);
                        signInButton.setClickable(true);
                        progressBarSignIn.setVisibility(View.GONE);
                    }

                }
            });
            dialog.show();
        }
        else
        {
            View header = navigationView.getHeaderView(0);
            menuEmailTxt = (TextView) header.findViewById(R.id.menuMail);
            menuEmailTxt.setText(API_EMAIL);
            int tempVal = siteIds.indexOf(Integer.valueOf(API_ID));
            Log.i("XXXXXX", String.valueOf(tempVal));
            menuSiteName.setText(websites.get(tempVal));
            signInButton.setAlpha(1);
            signInButton.setClickable(true);
            progressBarSignIn.setVisibility(View.GONE);
        }

    }

    private void writeToFile(String message)
    {
        String file_name = "test_file";
        try
        {
            FileOutputStream fileOutputStream = openFileOutput(file_name, MODE_PRIVATE);
            fileOutputStream.write(message.getBytes());
            fileOutputStream.close();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        boolean changeFragment = true;

        if (id == R.id.chooseSite)
        {
//            fragmentClass = HomePageFragment.class;
            changeFragment = false;
            setupAlertDialog();
        }
        else if (id == R.id.visits)
        {
            fragmentClass = MainVisitsFragment.class;
        }
        else if (id == R.id.pageViews)
        {
            fragmentClass = PageViewsMainFragment.class;
        }
        else if (id == R.id.popularPages)
        {
            fragmentClass = PopPagesMainFragment.class;
        }
        else if (id == R.id.trafficSources)
        {
            fragmentClass = TrafficSourcesMainFragment.class;
        }
        else if (id == R.id.entryPages)
        {
            //TODO Handle entry pages request
        }
        else if (id == R.id.socialMedia)
        {
            fragmentClass = SocialMediaMainFragment.class;
        }
        else if (id == R.id.searchEngines)
        {
            fragmentClass = SearchEnginesMainFragment.class;
        }
        else if (id == R.id.countries)
        {
            //TODO Handle countries request
        }
        else if (id == R.id.signOut)
        {
            changeFragment = false;
            initialLogin = null;
            this.recreate();
        }
        if(changeFragment)
        {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }



    @Override
    public void onFragmentInteraction(Uri uri) {}

    public boolean haveNetworkConnection()
    {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    private void setupAlertDialog()
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        LayoutInflater inflater = getLayoutInflater();
        final View convertView = inflater.inflate(R.layout.site_picker_dialog, null);
        alertDialog.setView(convertView);

        TextView title = new TextView(this);
        // You Can Customise your Title here
        title.setText("Choose Site");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(40, 40, 40, 40);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.rgb(5, 184, 198));
        title.setTextSize(20);
        alertDialog.setCustomTitle(title);

        lv = (ListView) convertView.findViewById(R.id.listView1);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.list_item,websites);
        lv.setAdapter(adapter);
        final AlertDialog ad = alertDialog.show();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Toast.makeText(MainActivity.this, "API site changed to: " + websites.get(position), Toast.LENGTH_SHORT).show();
                API_ID = siteIds.get(position).toString();
                menuSiteName.setText(websites.get(position));
                ad.dismiss();
            }
        });
    }

    private void notificationDialog()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.notification_dialog, null);
        builder.setView(view);

        TextView title = new TextView(this);
        // You Can Customise your Title here
        title.setText("Rotate for more detailed graphs");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(40, 40, 40, 40);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.rgb(5, 184, 198));
        title.setTextSize(18);

        builder .setCancelable(false)
                .setCustomTitle(title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
