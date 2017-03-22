/*package com.prodigus.com.prodigus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TabContactDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_contact_detail);
    }
}*/

package com.prodigus.com.prodigus.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prodigus.com.prodigus.MySQLiteHelper;
import com.prodigus.com.prodigus.R;
import com.prodigus.com.prodigus.SecondActivity;
import com.prodigus.com.prodigus.ThirdActivity;

public class TabContactDetail extends Fragment {

    MySQLiteHelper db;
    String personId;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        personId = getActivity().getIntent().getStringExtra("personId");
        db = new MySQLiteHelper(getActivity());
        Cursor cursor = db.getRow(personId);
        cursor.moveToFirst();

        setHasOptionsMenu(true);

        View v = (RelativeLayout)inflater.inflate(R.layout.activity_tab_contact_detail, container, false);

        TextView title = (TextView) v.findViewById(R.id.degree);
        setVisibility(title, cursor.getString( cursor.getColumnIndex("title")), true);
        TextView name = (TextView) v.findViewById(R.id.name);
        setVisibility(name, cursor.getString( cursor.getColumnIndex("name")), true);
        TextView surname = (TextView) v.findViewById(R.id.surname);
        setVisibility(surname, cursor.getString( cursor.getColumnIndex("surname")), true);
        TextView email = (TextView) v.findViewById(R.id.email);
        email.setText(cursor.getString( cursor.getColumnIndex("email")));
        TextView phone = (TextView) v.findViewById(R.id.phone);
        phone.setText(cursor.getString( cursor.getColumnIndex("phone")));
        //TextView borndate = (TextView) v.findViewById(R.id.dateBirth);
        //borndate.setText(cursor.getString( cursor.getColumnIndex("borndate")));
        TextView city = (TextView) v.findViewById(R.id.city);
        setVisibility(city, cursor.getString( cursor.getColumnIndex("city")), false);
        TextView street = (TextView) v.findViewById(R.id.street);
        setVisibility(street, cursor.getString( cursor.getColumnIndex("street")), false);

        return v;

        //return (RelativeLayout)inflater.inflate(R.layout.activity_tab_contact_detail, container, false);
    }

    public void setVisibility(TextView tv, String value, boolean emptySpace)
    {
        String emptySpaceValue = emptySpace ? " " : "";
        if(!value.equals(""))
        {
            String tVvalue = value + emptySpaceValue;
            tv.setText(tVvalue);
        }
        else {
            tv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contact_detail, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.edit_contact:
                Intent nextScreen = new Intent(getActivity(), ThirdActivity.class);
                nextScreen.putExtra("personId",personId);
                startActivity(nextScreen);
                return true;
            case R.id.delete_contact:
                AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                ab.setMessage("Are you sure to delete?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
        }
        return super.onOptionsItemSelected(item);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    db.deleteContact(Integer.parseInt(personId));
                    startActivity(new Intent(getActivity(), SecondActivity.class));
                    break;

                case DialogInterface.BUTTON_NEGATIVE:

                    break;
            }
        }
    };
}
