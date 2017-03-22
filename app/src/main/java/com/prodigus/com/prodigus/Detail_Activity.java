package com.prodigus.com.prodigus;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class Detail_Activity extends Activity {
    MySQLiteHelper db;
    String person_id;
    String surname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String itemText = intent.getStringExtra("item");

        db = new MySQLiteHelper(getApplicationContext());
        Cursor cursor = db.getRow(itemText);
        cursor.moveToFirst();

        person_id = cursor.getString(cursor.getColumnIndex("_id"));

        TextView title = (TextView) findViewById(R.id.edTitle);
        title.setText(cursor.getString( cursor.getColumnIndex("title")));
        TextView name = (TextView) findViewById(R.id.edName);
        name.setText(cursor.getString( cursor.getColumnIndex("name")));
        TextView surname = (TextView) findViewById(R.id.edSurname);
        surname.setText(cursor.getString( cursor.getColumnIndex("surname")));
        TextView email = (TextView) findViewById(R.id.edEmail);
        email.setText(cursor.getString( cursor.getColumnIndex("email")));
        TextView phone = (TextView) findViewById(R.id.edPhone);
        phone.setText(cursor.getString( cursor.getColumnIndex("phone")));
        TextView borndate = (TextView) findViewById(R.id.edBornDate);
        borndate.setText(cursor.getString( cursor.getColumnIndex("borndate")));
        TextView city = (TextView) findViewById(R.id.edCity);
        city.setText(cursor.getString( cursor.getColumnIndex("city")));
        TextView street = (TextView) findViewById(R.id.edStreet);
        street.setText(cursor.getString( cursor.getColumnIndex("street")));
        TextView number = (TextView) findViewById(R.id.edNumber);
        number.setText(cursor.getString( cursor.getColumnIndex("number")));
        TextView gender = (TextView) findViewById(R.id.Gender_spinner);
        gender.setText(cursor.getString( cursor.getColumnIndex("gender")));

        /*spusti obrazovku na pridanie kontaktu*/
        /*
        Button btnAdd = (Button) findViewById(R.id.btnAddNote);
        btnAdd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //Starting a new Intent

                Intent nextScreen = new Intent(getApplicationContext(), AddNote.class);
                nextScreen.putExtra("item",person_id);
                Log.i("RRperson",person_id);
                startActivity(nextScreen);
            }
        });

        Button btnShow = (Button) findViewById(R.id.btnSeeNotes);
        btnShow.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //Starting a new Intent

                Intent nextScreen = new Intent(getApplicationContext(), ShowNotes.class);
                nextScreen.putExtra("item",person_id);
                Log.i("RRperson2",person_id);
                startActivity(nextScreen);
            }
        });*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_, menu);
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
            nextScreen.putExtra("item",person_id);
            startActivity(nextScreen);
            return true;
        }

        if (id == R.id.menuAdd_Note) {
            Intent nextScreen = new Intent(getApplicationContext(), ShowNotes.class);
            nextScreen.putExtra("item",person_id);
            startActivity(nextScreen);
            return true;
        }

        if (id == android.R.id.home) {
            //NavUtils.navigateUpFromSameTask(this);
            Intent nextScreen = new Intent(getApplicationContext(), SecondActivity.class);
            nextScreen.putExtra("item",surname);
            startActivity(nextScreen);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
