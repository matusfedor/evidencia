package com.prodigus.com.prodigus;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.prodigus.com.prodigus.R;
import com.prodigus.com.prodigus.activity.TabContactMain;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNote extends AppCompatActivity implements View.OnClickListener {
    String personID;
    MySQLiteHelper db;
    private EditText bornDateEtxt;
    private DatePickerDialog bornDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        toolbar = (Toolbar) findViewById(R.id.toolbarCon);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        setDateTimeField();
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String itemText = intent.getStringExtra("item");

        //EditText title = (EditText) findViewById(R.id.NoteText);
        //title.setText(itemText);
        personID = itemText;
        db = new MySQLiteHelper(getApplicationContext());

        /*ulozenie poznamky*/
        Button btnAdd = (Button) findViewById(R.id.btnAddN);
        btnAdd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                String note = ((EditText) findViewById(R.id.NoteText)).getText().toString();

                long noteId = db.createNote(note,personID);
                Log.i("Note id: ", Long.toString(noteId));

                ((EditText) findViewById(R.id.NoteText)).setText("");
                Toast.makeText(getApplicationContext(), "Poznámka bola uložená", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menuAdd_Note) {
            Intent nextScreen = new Intent(getApplicationContext(), ShowNotes.class);
            nextScreen.putExtra("item",personID);
            startActivity(nextScreen);
            return true;
        }

        if (id == android.R.id.home) {
            Intent nextScreen = new Intent(getApplicationContext(), TabContactMain.class);
            nextScreen.putExtra("item",personID);
            startActivity(nextScreen);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setDateTimeField() {

        bornDateEtxt = (EditText) findViewById(R.id.edMeetingDate);
        bornDateEtxt.setInputType(InputType.TYPE_NULL);
        bornDateEtxt.requestFocus();

        bornDateEtxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        bornDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                bornDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void onClick(View view) {
        if(view == bornDateEtxt) {
            bornDatePickerDialog.show();
        }
    }
}
