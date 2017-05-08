package com.prodigus.com.prodigus;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.prodigus.com.prodigus.R;
import com.prodigus.com.prodigus.activity.TabContactMain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.parseInt;

public class AddNote extends AppCompatActivity implements View.OnClickListener {
    String personID;
    MySQLiteHelper db;
    private EditText bornDateEtxt;
    private DatePickerDialog bornDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private Toolbar toolbar;
    protected List<Genders> meetingTypes = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        db = new MySQLiteHelper(getApplicationContext());
        toolbar = (Toolbar) findViewById(R.id.toolbarCon);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat dateFormatterRead = new SimpleDateFormat("yyyy-MM-dd");
        setDateTimeField();

        Intent intent = getIntent();
        String itemText = intent.getStringExtra("personId");
        String noteId = intent.getStringExtra("noteId");

        //EditText title = (EditText) findViewById(R.id.NoteText);
        //title.setText(itemText);

        Spinner spinner = (Spinner) findViewById(R.id.meetingType);

        meetingTypes = db.getAllNoteTypes();

        ArrayAdapter<Genders> adapter = new ArrayAdapter<Genders>(this, android.R.layout.simple_spinner_item,meetingTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        personID = itemText;

        db = new MySQLiteHelper(getApplicationContext());

        if(noteId != null && noteId != ""){
            db = new MySQLiteHelper(getApplicationContext());
            Cursor cursor = db.getNote(parseInt(noteId));
            cursor.moveToFirst();

            TextView personId = ((TextView) findViewById(R.id.personID));
            personId.setText(cursor.getString(cursor.getColumnIndex("person")));

            TextView noteIdd = ((TextView) findViewById(R.id.noteID));
            noteIdd.setText(cursor.getString(cursor.getColumnIndex("_id")));

            EditText noteText = ((EditText) findViewById(R.id.NoteText));
            noteText.setText(cursor.getString(cursor.getColumnIndex("notetext")));

            EditText meetingDate = ((EditText) findViewById(R.id.edMeetingDate));
            String meetDate = "";
            try {
                meetDate = dateFormatter.format(dateFormatterRead.parse(cursor.getString(cursor.getColumnIndex("datec"))));
            }
            catch(Exception ex){}

            meetingDate.setText(meetDate);

            Spinner attribute = (Spinner) findViewById(R.id.meetingType);
            attribute.setSelection(getIndex(attribute,cursor.getString(cursor.getColumnIndex("attribute"))));
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addFloatButtonNote);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat dateFormatterStatus = new SimpleDateFormat("yyyy-MM-dd");

                String noteIdScreen = ((TextView) findViewById(R.id.noteID)).getText().toString();
                String note = ((EditText) findViewById(R.id.NoteText)).getText().toString();
                Spinner meetingType = (Spinner) findViewById(R.id.meetingType);
                String meetingDate = ((TextView) findViewById(R.id.edMeetingDate)).getText().toString();
                int meetingTypeInt = ((Genders) meetingType.getSelectedItem()).getId();

                Date meetingDateValue = new Date();
                try{
                    meetingDateValue = dateFormatter.parse(meetingDate);
                }
                catch(Exception ex){}

                if(noteIdScreen.isEmpty()) {
                    long noteId = db.createNote(note, personID, meetingTypeInt, meetingDateValue, 2);
                }
                else {
                    long noteId = db.updateNote(Integer.parseInt(noteIdScreen), note, meetingTypeInt, 1);
                }

                ((EditText) findViewById(R.id.NoteText)).setText("");
                Toast.makeText(getApplicationContext(), "Poznámka bola uložená", Toast.LENGTH_LONG).show();

                //List<Integer> statusList = new ArrayList<Integer>();
                Genders statusList = db.getPersonsStatuses(parseInt(personID));
                //Date statusListDate = new Date();
                Calendar statusListDate = Calendar.getInstance();
                statusListDate.add(Calendar.YEAR, -100);
                Date statusListDate2 = statusListDate.getTime();
                try
                {
                    statusListDate2 = dateFormatterStatus.parse(statusList.getGen());
                }
                catch (ParseException pe)
                    {}
                catch (Exception ex)
                {}
                int newNoteStatus = db.GetAttributeOrder(meetingTypeInt);
                int numberOfGreater = 0;

                if(newNoteStatus > statusList.getId()) {
                    db.updateStatus(parseInt(personID), meetingTypeInt, meetingDateValue);
                    Toast.makeText(getApplicationContext(), "Status bol zmeneny", Toast.LENGTH_LONG).show();
                }

                if(statusList.getGen() != null)
                {
                    if(newNoteStatus == statusList.getId() && meetingDateValue.before(statusListDate2)) {
                        db.updateStatus(parseInt(personID), meetingTypeInt, meetingDateValue);
                        Toast.makeText(getApplicationContext(), "Status bol zmeneny", Toast.LENGTH_LONG).show();
                    }
                }

                /*
                if(todo1_id > 0)
                    Snackbar.make(view, "Záznam uložený", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();*/
            }
        });
    }

    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (((Genders)spinner.getItemAtPosition(i)).getId() == parseInt(myString)){
                index = i;
                break;
            }
        }
        return index;
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
            nextScreen.putExtra("currentTab",'1');
            startActivity(nextScreen);
            return true;
        }

        if (id == android.R.id.home) {
            Intent nextScreen = new Intent(getApplicationContext(), TabContactMain.class);
            nextScreen.putExtra("tabId","1");
            nextScreen.putExtra("personId",personID);
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
