package dk.siteimprove.internship.atanasiu.andrei.analyticapplication;

import static dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Constants.FIRST_COLUMN;
import static dk.siteimprove.internship.atanasiu.andrei.analyticapplication.Constants.SECOND_COLUMN;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.HashMap;
import java.util.Locale;

import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.entity.Site;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.most_popular_pages.PopPagesFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.most_popular_pages.PopPagesMainFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.most_popular_pages.PopPagesMonthFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.most_popular_pages.PopPagesWeekFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.most_popular_pages.PopPagesYearFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.page_views.PageViewsFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.page_views.PageViewsMainFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.page_views.PageViewsMonthFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.page_views.PageViewsWeekFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.page_views.PageViewsYearFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.search_engines.SearchEnginesFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.search_engines.SearchEnginesMonthFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.search_engines.SearchEnginesMainFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.search_engines.SearchEnginesWeekFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.search_engines.SearchEnginesYearFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.social_media.SocialMediaFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.social_media.SocialMediaMainFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.social_media.SocialMediaMonthFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.social_media.SocialMediaWeekFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.social_media.SocialMediaYearFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.traffic_sources.TrafficSourcesFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.traffic_sources.TrafficSourcesMainFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.traffic_sources.TrafficSourcesMonthFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.traffic_sources.TrafficSourcesWeekFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.traffic_sources.TrafficSourcesYearFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.visits.MainVisitsFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.visits.VisitsFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.visits.VisitsMonthFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.visits.VisitsWeekFragment;
import dk.siteimprove.internship.atanasiu.andrei.analyticapplication.visits.VisitsYearFragment;

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
    public static int todayPeriodCounter, weekPeriodCounter, monthPeriodCounter, yearPeriodCounter = 0;
    NavigationView navigationView;
    DrawerLayout drawer;
    public static ArrayList<Site> websites;
  //  public static ArrayList<Integer> siteIds;
    View header;
    ListView lv;
    public static Button signInButton;
    Button headerBtn;
    public static ProgressBar progressBarSignIn;
    public static float screenHeight;
    boolean showRotateDialog;
    LinearLayout headerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Locale.setDefault(new Locale("en", "US")); // Sets the Locale to show all dates in english.
        currentFragment = "Today";
        todayPeriodCounter = 0;
        weekPeriodCounter = 0;
        monthPeriodCounter = 0;
        yearPeriodCounter = 0; //TODO maybe a method for counters, so we can reset?
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;

        super.onCreate(savedInstanceState);
        if(savedInstanceState != null)
        {
            todayPeriodCounter = savedInstanceState.getInt("Today");
            weekPeriodCounter = savedInstanceState.getInt("Week");
            monthPeriodCounter = savedInstanceState.getInt("Month");
            yearPeriodCounter = savedInstanceState.getInt("Year");
            currentFragment = savedInstanceState.getString("CurrentFragment");
        }
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        headerLayout = (LinearLayout) header.findViewById(R.id.headerLayout);
        menuEmailTxt = (TextView) header.findViewById(R.id.menuMail);
        menuSiteName = (TextView) header.findViewById(R.id.menuSiteName);

        headerLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createSitePickDialog();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        if (initialLogin == null)
        {

            drawer.openDrawer(GravityCompat.START);

            showRotateDialog = true;

            dialog = new Dialog(this,R.style.full_screen_dialog);
            dialog.setContentView(R.layout.popup);
            dialog.setCancelable(false);
            setupUI(dialog.findViewById(R.id.signInLayout));

            TextView contactTxt = (TextView) dialog.findViewById(R.id.contactTxt);
            emailText = (EditText) dialog.findViewById(R.id.emailTextField);
            apiKeyText = (EditText) dialog.findViewById(R.id.apiKeyTextField);
            headerTxt = (TextView) dialog.findViewById(R.id.headerTxt);
            loginAlert = (TextView) dialog.findViewById(R.id.loginAlert);
            progressBarSignIn  = (ProgressBar) dialog.findViewById(R.id.progressBarSignIn);
            progressBarSignIn.setVisibility(View.GONE);
            signInButton = (Button) dialog.findViewById(R.id.SignInButton);
            final TextInputLayout usernameWrapper = (TextInputLayout) dialog.findViewById(R.id.usernameWrapper);
            final TextInputLayout passwordWrapper = (TextInputLayout) dialog.findViewById(R.id.passwordWrapper);
            usernameWrapper.setHint("Enter your Email");
            passwordWrapper.setHint("Enter you API-key");

            readFromFile("credentials_file");
            readFromFile("rotate_check_file");
            contactTxt.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.setType("plain/text");
                    sendIntent.setData(Uri.parse("contact@siteimprove.com"));
                    sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                    sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@siteimprove.com" });
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Sign me up!");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello! \n I've seen your cool app! sign me up! :-)");
                    startActivity(sendIntent);
                }
            });
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
                        if(!API_EMAIL.equals("") && !API_KEY.equals(""))
                        {
                            writeToFile("credentials_file", totalString);
                        }

                        // Sets drawer menuMail textview to current user mail.
                        menuEmailTxt.setText(API_EMAIL);
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

            menuSiteName.setText((getSiteById(Integer.parseInt(API_ID))));
            signInButton.setAlpha(1);
            signInButton.setClickable(true);
            progressBarSignIn.setVisibility(View.GONE);
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

    }

    private void writeToFile(String fileName, String message)
    {
        try
        {
            FileOutputStream fileOutputStream = openFileOutput(fileName, MODE_PRIVATE);
            fileOutputStream.write(message.getBytes());
            fileOutputStream.close();
        }
        catch (FileNotFoundException e) {e.printStackTrace();} // TODO: Handle somehow?
        catch (IOException e) {e.printStackTrace();} // TODO: Handle somehow?
    }

    private void readFromFile(String fileName)
    {
        try
        {
            String message;
            FileInputStream fileInputStream = openFileInput(fileName);
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
            else if(tempString.contains("=-=SHOW_ME_NOT=-="))
            {
                showRotateDialog = false;
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
            changeFragment = false;
            createSitePickDialog();
        }
        else if (id == R.id.visits)
        {
            if(showRotateDialog)
            {
                createRotateDialog();
                showRotateDialog = false;
            }
            fragmentClass = MainVisitsFragment.class;

        }
        else if (id == R.id.pageViews)
        {
            if(showRotateDialog)
            {
                createRotateDialog();
                showRotateDialog = false;
            }
            fragmentClass = PageViewsMainFragment.class;
        }
        else if (id == R.id.popularPages)
        {
            if(showRotateDialog)
            {
                createRotateDialog();
                showRotateDialog = false;
            }
            fragmentClass = PopPagesMainFragment.class;
        }
        else if (id == R.id.trafficSources)
        {
            if(showRotateDialog)
            {
                createRotateDialog();
                showRotateDialog = false;
            }
            fragmentClass = TrafficSourcesMainFragment.class;
        }
        else if (id == R.id.entryPages)
        {
            //TODO Handle entry pages request
        }
        else if (id == R.id.socialMedia)
        {
            if(showRotateDialog)
            {
                createRotateDialog();
                showRotateDialog = false;
            }
            fragmentClass = SocialMediaMainFragment.class;
        }
        else if (id == R.id.searchEngines)
        {
            if(showRotateDialog)
            {
                createRotateDialog();
                showRotateDialog = false;
            }
            fragmentClass = SearchEnginesMainFragment.class;
        }
        else if (id == R.id.countries)
        {
            //TODO Handle countries request
        }
        else if (id == R.id.signOut)
        {
            todayPeriodCounter = 0;
            weekPeriodCounter = 0;
            monthPeriodCounter = 0;
            yearPeriodCounter = 0;
            currentFragment = "Today";
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

    //Method to hide the keyboard inside the login popup
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(v);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putInt("Today", todayPeriodCounter);
        savedInstanceState.putInt("Week", weekPeriodCounter);
        savedInstanceState.putInt("Month", monthPeriodCounter);
        savedInstanceState.putInt("Year", yearPeriodCounter);
        savedInstanceState.putString("CurrentFragment", currentFragment);

        // etc.
    }

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

    private void createRotateDialog()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        LayoutInflater inflater = getLayoutInflater();
        final View view = inflater.inflate(R.layout.notification_dialog, null);
        builder.setView(view);

        TextView title = new TextView(this);
        // You Can Customise your Title here
        title.setText("Rotate to compare to last period");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(40, 40, 40, 40);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.rgb(5, 184, 198));
        title.setTextSize(18);

        builder .setCancelable(false)
                .setCustomTitle(title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        writeToFile("rotate_check_file", "=-=SHOW_ME_NOT=-=");
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void createSitePickDialog()
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        LayoutInflater inflater = getLayoutInflater();
        final View convertView = inflater.inflate(R.layout.site_picker_dialog, null);
        alertDialog.setView(convertView);

        ArrayList<HashMap<String, String>> list;
        ListView listView = (ListView) convertView.findViewById(R.id.listView1);
        list = new ArrayList<HashMap<String,String>>();

        for (int i = 0; i < websites.size(); i++)
        {
            HashMap<String,String> temp = new HashMap<String, String>();
            temp.put(FIRST_COLUMN, websites.get(i).getSiteName());
            temp.put(SECOND_COLUMN, String.valueOf(websites.get(i).getVisits()));
            list.add(temp);
        }

        ListViewAdapter adapter = new ListViewAdapter(this, list);
        listView.setAdapter(adapter);

        TextView title = new TextView(this);
        // You Can Customise your Title here
        title.setText("Choose Site");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(40, 40, 40, 40);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.rgb(5, 184, 198));
        title.setTextSize(20);

        View titleView = inflater.inflate(R.layout.site_picker_dialog_title, null);
        alertDialog.setCustomTitle(titleView);
        //alertDialog.setCustomTitle(title);

        final AlertDialog ad = alertDialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
            {
                Toast.makeText(MainActivity.this, "API site changed to: " + websites.get(position).getSiteName(), Toast.LENGTH_SHORT).show();
                API_ID = websites.get(position).getId().toString();
                menuSiteName.setText(websites.get(position).getSiteName());
                writeToFile("choosen_website", websites.get(position) + "=-=SITE_URL=-=");
                ad.dismiss();
            }
        });
    }


    public String getSiteById(int id)
    {
        for(Site s : websites)
        {
            if(s.getId().equals(id))
                //return websites.indexOf(s);
                return s.getSiteName();
        }
        return "Site name not found";
    }
}
