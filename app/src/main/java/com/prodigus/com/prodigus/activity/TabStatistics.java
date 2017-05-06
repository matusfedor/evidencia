package com.prodigus.com.prodigus.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.prodigus.com.prodigus.Genders;
import com.prodigus.com.prodigus.MySQLiteHelper;
import com.prodigus.com.prodigus.R;
import com.prodigus.com.prodigus.SecondActivity;
import com.prodigus.com.prodigus.Users;
import com.prodigus.com.prodigus.ViewPagerAdapter;
import com.prodigus.com.prodigus.fragment.TabContactDetail;
import com.prodigus.com.prodigus.fragment.TabStatDay;
import com.prodigus.com.prodigus.fragment.TabStatMonth;
import com.prodigus.com.prodigus.fragment.TabStatQrt;
import com.prodigus.com.prodigus.fragment.TabStatWeek;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TabStatistics extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    MySQLiteHelper db;
    Spinner spinner;

    private final String NAMESPACE = "http://microsoft.com/webservices/";
    private final String URL = "http://evidencia.prodigus.sk/EvidenceService.asmx";
    private final String SOAP_ACTION = "http://microsoft.com/webservices/GetUsers";
    private final String METHOD_NAME = "GetUsers";

    private String spinnerSelected;

    private TabStatDay tabStatDay;
    private TabStatWeek tabStatWeek;
    private TabStatMonth tabStatMonth;

    protected List<Users> gens = null;

    private String logUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_statistics);

        db = new MySQLiteHelper(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        gens = new ArrayList<Users>();

        Cursor cUsers = db.getUsers();
        while (cUsers.moveToNext()) {
            gens.add(new Users(cUsers.getString(cUsers.getColumnIndex("usr_nick")),cUsers.getString(cUsers.getColumnIndex("usr_name"))));
        }
        cUsers.close();

        Cursor authCursor = db.getAuth();
        if(Integer.valueOf(authCursor.getCount()) > 0) {

            if (authCursor.moveToFirst()) {
                logUser = authCursor.getString(authCursor.getColumnIndex("logname"));
            }
            authCursor.close();
        }
        else {
            logUser = "";
        }

        Spinner spinner = (Spinner) findViewById(R.id.userSpinner);
        ArrayAdapter<Users> adapter = new ArrayAdapter<Users>(this, R.layout.text_spinner, gens);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinnerSelected = ((Users)spinner.getSelectedItem()).getNick();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);
                /*if(getFragmentRefreshListener()!=null){
                    getFragmentRefreshListener().onRefresh(((Users)item).getNick());
                }*/
                String name = ((Users)item).getNick();

                //if (tabStatDay != null && tabStatDay.isVisible())

                int currItem = mViewPager.getCurrentItem();

                switch(currItem)
                {
                    case 0:
                        tabStatDay.setSelectedUser(name);
                        tabStatDay.setLogUser(logUser);
                        tabStatDay.addViewChart(logUser.equals(name));

                        tabStatWeek.setSelectedUser(name);
                        tabStatWeek.setLogUser(logUser);
                        tabStatMonth.setSelectedUser(name);
                        tabStatMonth.setLogUser(logUser);
                        break;
                    case 1:
                        tabStatWeek.setSelectedUser(name);
                        tabStatWeek.setLogUser(logUser);
                        tabStatWeek.addViewChart(true);

                        tabStatDay.setSelectedUser(name);
                        tabStatDay.setLogUser(logUser);
                        tabStatDay.onResume();
                        tabStatMonth.setSelectedUser(name);
                        tabStatMonth.setLogUser(logUser);
                        break;
                    case 2:
                        tabStatMonth.setSelectedUser(name);
                        tabStatMonth.setLogUser(logUser);
                        tabStatMonth.addViewChart(true);

                        tabStatDay.setSelectedUser(name);
                        tabStatDay.setLogUser(logUser);
                        tabStatWeek.setSelectedUser(name);
                        tabStatWeek.setLogUser(logUser);
                        break;
                    default: break;
                }

                viewPagerAdapter.notifyDataSetChanged();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        //mViewPager.setAdapter(mSectionsPagerAdapter);
        setupViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        tabStatDay = new TabStatDay().newInstance(spinnerSelected, logUser);
        tabStatWeek = new TabStatWeek().newInstance(spinnerSelected, logUser);
        tabStatMonth = new TabStatMonth().newInstance(spinnerSelected, logUser);

        viewPagerAdapter.addFragment(tabStatDay, "Denne");
        viewPagerAdapter.addFragment(tabStatWeek, "Týždenne");
        viewPagerAdapter.addFragment(tabStatMonth, "Mesačne");

        //viewPager.setCurrentItem(spinner.getSelectedItem().toString());
        //adapter.addFragment(new TabStatQrt(), "Quarter");

        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tab_statistics, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_contacts) {
            startActivity(new Intent(TabStatistics.this, MainActivity.class));
        } else if (id == R.id.nav_pzp) {

        } else if (id == R.id.nav_statistics) {
            startActivity(new Intent(TabStatistics.this, TabStatistics.class));
        } else if (id == R.id.nav_sync) {
            startActivity(new Intent(TabStatistics.this, Synchronize.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            startActivity(new Intent(TabStatistics.this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    private FragmentRefreshListener fragmentRefreshListener;

    public interface FragmentRefreshListener{
        void onRefresh(String logname);
    }

    public void loadUsers() {
        String logname = "";
        String pin = "";

        Cursor c = db.getAuth();

        if(Integer.valueOf(c.getCount()) > 0) {

            if (c.moveToFirst()) {
                logname = c.getString(c.getColumnIndex("logname"));
                pin = c.getString(c.getColumnIndex("pin"));
            }
            c.close();
        }
        else {
            return;
        }

        //Create request
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        PropertyInfo celsiusPI = new PropertyInfo();
        //Set Name
        celsiusPI.setName("userName");
        //Set Value
        celsiusPI.setValue(logname);
        //Set dataType
        //celsiusPI.setType(double.class);
        celsiusPI.setType(String.class);
        //Add the property to request object
        request.addProperty(celsiusPI);
        //Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        /*header*/
        Element h = new Element().createElement(NAMESPACE, "UserCredentials");
        Element Username = new Element().createElement(NAMESPACE, "userName");
        Username.addChild(Node.TEXT, logname);
        h.addChild(Node.ELEMENT, Username);
        Element wssePassword = new Element().createElement(NAMESPACE, "password");
        wssePassword.addChild(Node.TEXT, pin);
        h.addChild(Node.ELEMENT, wssePassword);

        envelope.headerOut = new Element[]{h};

        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        Log.i("loading users", "" + envelope.bodyOut.toString());

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject result = (SoapObject)envelope.getResponse();

            for (int i = 0; i < result.getPropertyCount(); i++)
            {
                SoapObject s_deals = (SoapObject) result.getProperty(i);

                String nick = (s_deals.getProperty(0).toString());
                String name = (s_deals.getProperty(1).toString());

                gens.add(new Users(nick, name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        spinner = (Spinner) findViewById(R.id.userSpinner);
        ArrayAdapter<Users> adapter = new ArrayAdapter<Users>(this, R.layout.text_spinner, gens);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinnerSelected = spinner.getSelectedItem().toString();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = adapterView.getItemAtPosition(i);

                if(getFragmentRefreshListener()!=null){
                    getFragmentRefreshListener().onRefresh(item.toString());
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private class AsyncCallWS extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            loadUsers();
            return "Task Completed.";
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

    }
}
