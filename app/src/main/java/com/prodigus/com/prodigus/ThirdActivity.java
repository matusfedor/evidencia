package com.prodigus.com.prodigus;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import com.prodigus.com.prodigus.MySQLiteHelper;
import com.prodigus.com.prodigus.FeedReaderContract.FeedEntry;
import com.prodigus.com.prodigus.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.view.View.OnClickListener;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import com.prodigus.com.prodigus.Genders;
import android.support.v7.app.AppCompatActivity;

public class ThirdActivity extends AppCompatActivity implements OnClickListener {

    MySQLiteHelper db;
    EditText et;
    private EditText bornDateEtxt;
    private Spinner spGender;

    private DatePickerDialog bornDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    protected List<Genders> gens = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        setDateTimeField();

        Intent intent = getIntent();
        final String personId = intent.getStringExtra("personId");

        /* spinner  - gender*/
        Spinner spinner = (Spinner) findViewById(R.id.Gender_spinner);

        /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);*/

        gens = new ArrayList<Genders>();
        gens.add(new Genders(5,"Muž"));
        gens.add(new Genders(9,"Žena"));

        ArrayAdapter<Genders> adapter = new ArrayAdapter<Genders>(this, android.R.layout.simple_spinner_item,gens);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                et = (EditText) findViewById(R.id.edName);

                Genders gen = (Genders) adapterView.getItemAtPosition(i);
                Object tag = gen.getId();
                //et.setText(tag.toString());
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        db = new MySQLiteHelper(getApplicationContext());

        if(personId != null && personId != ""){

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

            db = new MySQLiteHelper(getApplicationContext());
            Cursor cursor = db.getRow(personId);
            cursor.moveToFirst();

            EditText title = ((EditText) findViewById(R.id.edTitle));
            title.setText(cursor.getString(cursor.getColumnIndex("title")));

            EditText name = ((EditText) findViewById(R.id.edName));
            name.setText(cursor.getString(cursor.getColumnIndex("name")));

            EditText surname = ((EditText) findViewById(R.id.edSurname));
            surname.setText(cursor.getString(cursor.getColumnIndex("surname")));

            EditText city = ((EditText) findViewById(R.id.edCity));
            city.setText(cursor.getString(cursor.getColumnIndex("city")));

            EditText street = ((EditText) findViewById(R.id.edStreet));
            street.setText(cursor.getString(cursor.getColumnIndex("street")));

            EditText number = ((EditText) findViewById(R.id.edNumber));
            number.setText(cursor.getString(cursor.getColumnIndex("number")));

            EditText email = ((EditText) findViewById(R.id.edEmail));
            email.setText(cursor.getString(cursor.getColumnIndex("email")));

            EditText phone = ((EditText) findViewById(R.id.edPhone));
            phone.setText(cursor.getString(cursor.getColumnIndex("phone")));

            Log.i("datum",cursor.getString(cursor.getColumnIndex("borndate")));

            Date borndate = new Date();
            try {
                borndate = sdf.parse(cursor.getString((cursor.getColumnIndex("borndate"))));
            } catch (ParseException e) {
                e.printStackTrace();}
            Log.i("datum2",borndate.toString());

            EditText bdate = ((EditText) findViewById(R.id.edBornDate));
            bdate.setText(borndate.toString());
        }
        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                FeedEntry todo;

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

                String title = ((EditText) findViewById(R.id.edTitle)).getText().toString();
                String name = ((EditText) findViewById(R.id.edName)).getText().toString();
                String surname = ((EditText) findViewById(R.id.edSurname)).getText().toString();
                String bdate = ((EditText) findViewById(R.id.edBornDate)).getText().toString();
                Date borndate = new Date();
                try {
                    borndate = sdf.parse(bdate);
                    } catch (ParseException e) {
                    e.printStackTrace();}
                //String gender = ((EditText) findViewById(R.id.edGender)).getText().toString();
                Spinner gender = (Spinner) findViewById(R.id.Gender_spinner);
                String genderS = gender.getSelectedItem().toString();
                String city = ((EditText) findViewById(R.id.edCity)).getText().toString();
                String street = ((EditText) findViewById(R.id.edStreet)).getText().toString();
                String number = ((EditText) findViewById(R.id.edNumber)).getText().toString();
                String email = ((EditText) findViewById(R.id.edEmail)).getText().toString();
                String phone = ((EditText) findViewById(R.id.edPhone)).getText().toString();

                if(personId == null || personId == ""){
                    long todo1_id = db.createToDo(title, name, surname, borndate, city, street, number, email, phone, genderS, "1", 0);

                    if(todo1_id > 0){
                        db.createContactHistory(todo1_id, 1);
                    }
                }
                else
                {
                    long todo1_id = db.updateToDo(Integer.parseInt(personId), title, name, surname, borndate, city, street, number, email, phone, genderS, "1");
                }
                et = (EditText) findViewById(R.id.edSurname);
                //et.setText(Long.toString(todo1_id));

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAdd);
        setSupportActionBar(toolbar);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDateTimeField() {

        bornDateEtxt = (EditText) findViewById(R.id.edBornDate);
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