package com.prodigus.com.prodigus;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SearchViewCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.view.MenuItem;

import com.prodigus.com.prodigus.FeedReaderContract.FeedEntry;
import com.prodigus.com.prodigus.activity.LoginActivity;
import com.prodigus.com.prodigus.activity.Synchronize;
import com.prodigus.com.prodigus.activity.TabContactMain;
import com.prodigus.com.prodigus.activity.TabStatistics;

import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SecondActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * Called when the activity is first created.
     */
    MySQLiteHelper db;
    ListView lw;
    List<Clients> contactList = new ArrayList<Clients>();
    Cursor todoCursor;
    TodoCursorAdapter todoAdapter;
    String personID;
    String surname;
    //StableArrayAdapter adapter;
    android.support.v7.widget.SearchView searchView;

    //expandable list view
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<ChildItems>> listDataChild;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        db = new MySQLiteHelper(getApplicationContext());

        Intent intent = getIntent();
        String itemText = intent.getStringExtra("item");
        personID = itemText;

        if (personID != null) {
            Log.i("XXX", personID);
        }

        if (personID != "") {
            expListView = (ExpandableListView) findViewById(R.id.lvExp);

            // preparing list data
            prepareListData(null);

            listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

            // setting list adapter
            expListView.setAdapter(listAdapter);

            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    personID = String.valueOf(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getId());
                    Intent nextScreen = new Intent(getApplicationContext(), TabContactMain.class);
                    nextScreen.putExtra("personId", personID);
                    startActivity(nextScreen);
                    return false;
                }
            });
        }

        handleIntent(getIntent());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCon);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (android.support.v7.widget.SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(searchQueryListener);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_add:
                Intent nextScreen = new Intent(getApplicationContext(), ThirdActivity.class);
                startActivity(nextScreen);
                //NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_contacts) {
            startActivity(new Intent(SecondActivity.this, SecondActivity.class));
        } else if (id == R.id.nav_pzp) {
            startActivity(new Intent(SecondActivity.this, TabContactMain.class));
        } else if (id == R.id.nav_statistics) {
            startActivity(new Intent(SecondActivity.this, TabStatistics.class));
        } else if (id == R.id.nav_sync) {
            startActivity(new Intent(SecondActivity.this, Synchronize.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            startActivity(new Intent(SecondActivity.this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void prepareListData(String searchText) {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<ChildItems>>();

        //List<String> childs = new ArrayList<String>();

        Cursor c = db.getAllMarks(searchText);
        int i = 0;

        while (c.moveToNext()) {
            try {
                listDataHeader.add(c.getString(c.getColumnIndex("att_full")));
                listDataChild.put(listDataHeader.get(i), getContactList(c.getString(c.getColumnIndex("_id")), searchText));
                i++;
            } catch (Exception e) {
                Log.e("LogMarks", "Error " + e.toString());
            }
        }
    }

    private List getContactList(String attribute, String searchText) {
        List<ChildItems> contacts = new ArrayList<ChildItems>();
        Cursor c = db.getContacts(attribute, searchText);
        while (c.moveToNext()) {
            try {
                String sdsd = c.getString(c.getColumnIndex("title"));
                if(!c.getString(c.getColumnIndex("title")).isEmpty())
                {
                    contacts.add(new ChildItems(c.getInt(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("title")) + " " + c.getString(c.getColumnIndex("name")) + " " + c.getString(c.getColumnIndex("surname")) + " " + c.getString(c.getColumnIndex("city"))));
                }
                else {
                    contacts.add(new ChildItems(c.getInt(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("name")) + " " + c.getString(c.getColumnIndex("surname")) + " " + c.getString(c.getColumnIndex("city"))));
                }
            } catch (Exception e) {
                Log.e("LogMarks", "Error " + e.toString());
            }

        }
        return contacts;
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            prepareListData(query);
            listAdapter.setDataHeader(listDataHeader);
            listAdapter.setDataChild(listDataChild);
            listAdapter.notifyDataSetChanged();
            expListView.invalidateViews();
            expListView.refreshDrawableState();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private android.support.v7.widget.SearchView.OnQueryTextListener searchQueryListener = new android.support.v7.widget.SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            search(query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (TextUtils.isEmpty(newText)) {
                search("");
            }

            return true;
        }

        public void search(String query) {
            prepareListData(query);
            listAdapter.setDataHeader(listDataHeader);
            listAdapter.setDataChild(listDataChild);
            listAdapter.notifyDataSetChanged();
            expListView.invalidateViews();
            expListView.refreshDrawableState();
        }
    };
}
