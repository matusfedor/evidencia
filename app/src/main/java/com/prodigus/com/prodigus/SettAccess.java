package com.prodigus.com.prodigus;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SettAccess extends Activity {
    MySQLiteHelper db;
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = new MySQLiteHelper(getApplicationContext());
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Cursor c = db.getAuth();
        if(c.getCount() > 0) {
            if (c.moveToFirst()) {
                ((EditText) findViewById(R.id.edLogName)).setText(c.getString(c.getColumnIndex("logname")));
                ((EditText) findViewById(R.id.edPin)).setText(c.getString(c.getColumnIndex("pin")));
            }
            c.close();
        }
        c.close();

        Button btnAdd = (Button) findViewById(R.id.btnSaveAccess);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                Cursor c = db.getAuth();
                if(c.getCount() == 0) {
                    String LogName = ((EditText) findViewById(R.id.edLogName)).getText().toString();
                    String Pin = ((EditText) findViewById(R.id.edPin)).getText().toString();

                    long todo1_id = db.setAccess(LogName, Pin);
                }
                else
                {
                    Boolean isOK;
                    String LogName = ((EditText) findViewById(R.id.edLogName)).getText().toString();
                    String Pin = ((EditText) findViewById(R.id.edPin)).getText().toString();
                    //db.updAccess(LogName, Pin);
                    isOK = db.updateAccess(LogName, Pin);
                    if(isOK) {
                        Toast.makeText(getApplicationContext(), "Údaje boli aktualizované", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
            return true;
        }

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
