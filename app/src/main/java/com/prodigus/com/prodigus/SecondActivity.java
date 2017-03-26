package com.prodigus.com.prodigus;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.view.MenuItem;

import com.prodigus.com.prodigus.FeedReaderContract.FeedEntry;
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
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String itemText = intent.getStringExtra("item");
        personID = itemText;

        if(personID != null)
        {
            Log.i("XXX",personID);
        }

        if(personID != "")
        {
            expListView = (ExpandableListView) findViewById(R.id.lvExp);

            // preparing list data
            prepareListData();

            listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

            // setting list adapter
            expListView.setAdapter(listAdapter);

            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                personID = String.valueOf(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getId());
                Intent nextScreen = new Intent(getApplicationContext(), TabContactMain.class);
                nextScreen.putExtra("personId",personID);
                startActivity(nextScreen);

                Log.i("QQQidd",personID);
                //startActivity(new Intent(SecondActivity.this, TabContactMain.class));

                /*Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();*/
                return false;
            }
        });
        }

        Button btnNextScreen = (Button) findViewById(R.id.button);
        todoAdapter = new TodoCursorAdapter(this, todoCursor);
/*
        adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, contactList);*/

        btnNextScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //Starting a new Intent
                /*Intent nextScreen = new Intent(getApplicationContext(), ThirdActivity.class);
                startActivity(nextScreen);*/
                /*
                contactList = db.getAllContacts();
                SQLiteDatabase db_c = db.getWritableDatabase();
                Cursor todoCursor = db_c.rawQuery("SELECT  * FROM " + FeedEntry.TABLE_NAME, null);
                lw = (ListView) findViewById(R.id.listView);
                lw.setAdapter(todoAdapter);
                */
                surname = ((EditText) findViewById(R.id.editText)).getText().toString();
                Cursor cursor = db.getAllRows(surname);
                String[] fromFieldNames = new String[] {FeedEntry._ID, FeedEntry.COLUMN_NAME,FeedEntry.COLUMN_SURNAME};
                int[] toViewIDs = new int[] {R.id.tvID, R.id.tvName, R.id.tvSurname};
                SimpleCursorAdapter myCursorAdapter;
                myCursorAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.clistview, cursor, fromFieldNames, toViewIDs, 0);
                ListView myList = (ListView) findViewById(R.id.lvExp);
                myList.setAdapter(myCursorAdapter);

                myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> listView, View view,
                                            int position, long id) {
                        // Get the cursor, positioned to the corresponding row in the result set
                        Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                        // Get the state's capital from this row in the database.
                        String countryCode = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
                        surname = ((EditText) findViewById(R.id.editText)).getText().toString();

                        Log.i("QQQid",countryCode);

                        Intent nextScreen = new Intent(getApplicationContext(), Detail_Activity.class);
                        nextScreen.putExtra("item",countryCode);
                        nextScreen.putExtra("surname",surname);
                        startActivity(nextScreen);
                    }
                });
            }
        });

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

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<ChildItems>>();

        //List<String> childs = new ArrayList<String>();

        Cursor c = db.getAllMarks();
        int i = 0;

        while (c.moveToNext())
        {
            try
            {
                listDataHeader.add(c.getString(c.getColumnIndex("att_full")));
                listDataChild.put(listDataHeader.get(i), getContactList(c.getString(c.getColumnIndex("_id"))));
                i++;
            }
            catch (Exception e) {
                Log.e("LogMarks", "Error " + e.toString());
            }
        }
    }

    private List getContactList(String attribute)
    {
        List<ChildItems> contacts = new ArrayList<ChildItems>();
        Cursor c = db.getContacts(attribute);
        while (c.moveToNext())
        {
            try
            {
                Log.i("zz-id",c.getString(c.getColumnIndex("_id")));
                contacts.add(new ChildItems(c.getInt(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("title")) + " " + c.getString(c.getColumnIndex("name")) + " " + c.getString(c.getColumnIndex("surname")) + ", " + c.getString(c.getColumnIndex("city"))));
            }
            catch (Exception e) {
                Log.e("LogMarks", "Error " + e.toString());
            }

        }
        return contacts;
    }
}
