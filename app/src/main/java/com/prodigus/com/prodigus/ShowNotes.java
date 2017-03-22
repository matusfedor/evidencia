package com.prodigus.com.prodigus;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.view.View.OnClickListener;

import com.prodigus.com.prodigus.R;

import java.text.SimpleDateFormat;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ShowNotes extends Activity {
    MySQLiteHelper db;
    ListView mListView;
    String personID;
    String surname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notes);

        db = new MySQLiteHelper(getApplicationContext());
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String itemText = intent.getStringExtra("item");
        personID = itemText;

        Cursor cursor = db.getAllNotes(itemText);
        String[] fromFieldNames = new String[]{FeedReaderContract.Notes._ID, FeedReaderContract.Notes.COLUMN_NOTE_TEXT, FeedReaderContract.Notes.COLUMN_NOTE_DATEC};
        int[] toViewIDs = new int[]{R.id.noteID, R.id.tvText, R.id.tvDate};
        /*
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(), R.layout.notelistview, cursor, fromFieldNames, toViewIDs, 0);
        ListView myList = (ListView) findViewById(R.id.NoteView);
        myList.setAdapter(myCursorAdapter);
        db = new MySQLiteHelper(getApplicationContext());

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "TEST", Toast.LENGTH_LONG).show();
//                String countryCode = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
            }
        });*/
        /*ListAdapter myCursorAdapter = new ListAdapter();

        ListView myList = (ListView) findViewById(R.id.NoteView);
        myList.setAdapter(myCursorAdapter);*/

        ListView lvItems = (ListView) findViewById(R.id.NoteView);
        TodoCursorAdapter todoAdapter = new TodoCursorAdapter(this, cursor);
        lvItems.setAdapter(todoAdapter);
        todoAdapter.notifyDataSetChanged();

    }


    private OnClickListener mOnTitleClickListener = new OnClickListener() {
        public void onClick(View v) {
            final int position = mListView.getPositionForView((View) v.getParent());
            Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_LONG).show();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_notes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent nextScreen = new Intent(getApplicationContext(), AddNote.class);
            nextScreen.putExtra("item",personID);
            startActivity(nextScreen);
            return true;
        }

        if (id == android.R.id.home) {
            Intent nextScreen = new Intent(getApplicationContext(), Detail_Activity.class);
            nextScreen.putExtra("item",personID);
            startActivity(nextScreen);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
