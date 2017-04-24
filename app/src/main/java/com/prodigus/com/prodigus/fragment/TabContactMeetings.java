/*package com.prodigus.com.prodigus;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class TabContactMeetings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_contact_meetings);
    }
}*/

package com.prodigus.com.prodigus.fragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.prodigus.com.prodigus.AddNote;
import com.prodigus.com.prodigus.ChildItems;
import com.prodigus.com.prodigus.ExpandableListAdapter;
import com.prodigus.com.prodigus.MySQLiteHelper;
import com.prodigus.com.prodigus.R;
import com.prodigus.com.prodigus.SecondActivity;
import com.prodigus.com.prodigus.ThirdActivity;
import com.prodigus.com.prodigus.activity.TabContactMain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.parseInt;

public class TabContactMeetings extends Fragment {

    private View myFragmentView;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<ChildItems>> listDataChild;
    MySQLiteHelper db;
    String personId = "";
    String noteId;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        db = new MySQLiteHelper(getActivity());
        myFragmentView = (RelativeLayout)inflater.inflate(R.layout.activity_tab_contact_meetings, container, false);
        personId = getActivity().getIntent().getStringExtra("personId");
        setHasOptionsMenu(true);

        expListView = (ExpandableListView) myFragmentView.findViewById(R.id.expLvMeeting);

        if(personId != null) {
            prepareListData();

            listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
        }
        registerForContextMenu(expListView);

        return myFragmentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contact_meeting, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Toast.makeText(getActivity(), "called " + item.getItemId(), Toast.LENGTH_SHORT).show();
        //return super.onOptionsItemSelected(item);
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_meeting:
                Intent nextScreen = new Intent(getActivity(), AddNote.class);
                nextScreen.putExtra("personId",personId);
                nextScreen.putExtra("noteId",noteId);
                startActivity(nextScreen);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        ExpandableListView.ExpandableListContextMenuInfo info= (ExpandableListView.ExpandableListContextMenuInfo)menuInfo;
        int type=ExpandableListView.getPackedPositionType(info.packedPosition);
        Log.e("type",""+type);
        menu.setHeaderTitle("Options");
        menu.add(1, 1, 1, "Edit");
        menu.add(1, 2, 2, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo)item.getMenuInfo();
        int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);

        noteId = String.valueOf(listDataChild.get(listDataHeader.get(groupPos)).get(childPos).getId());

        switch(item.getItemId())
        {
            case 1:
                //Toast.makeText(getActivity(), "Clicked edit", Toast.LENGTH_SHORT).show();
                Intent nextScreen = new Intent(getActivity(), AddNote.class);
                nextScreen.putExtra("personId",personId);
                nextScreen.putExtra("noteId",noteId);
                startActivity(nextScreen);
                break;
            case 2:
                //Toast.makeText(getActivity(), "Clicked delete", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                ab.setMessage("Are you sure to delete?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;
            default: break;
        }

        return super.onContextItemSelected(item);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    db.delNote(noteId);

                    getActivity().recreate();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:

                    break;
            }
        }
    };


    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<ChildItems>>();

        Cursor c = db.getAllNoteMarks(parseInt(personId));
        int i = 0;

        while (c.moveToNext())
        {
            try
            {
                listDataHeader.add(c.getString(c.getColumnIndex("att_full")));
                listDataChild.put(listDataHeader.get(i), getNotesList(c.getString(c.getColumnIndex("_id"))));
                i++;
            }
            catch (Exception e) {
                Log.e("LogMarks", "Error " + e.toString());
            }
        }

        // Adding child data
        /*listDataHeader.add("Telefonický kontakt");
        listDataHeader.add("Osobné stretnutie");
        listDataHeader.add("Školenie");

        // Adding child data
        List<ChildItems> top250 = new ArrayList<ChildItems>();
        top250.add(new ChildItems(1, "10.1.2017 sme spolu telefonovali a dohodli sme sa na dalsom stretnuti"));

        List<ChildItems> nowShowing = new ArrayList<ChildItems>();
        nowShowing.add(new ChildItems(2, "15.1.2017 sme sa stretli v hoteli Carlton a dohodli sme sa na absolvovani školenia"));

        List<ChildItems> comingSoon = new ArrayList<ChildItems>();
        comingSoon.add(new ChildItems(3, "20.1.2017 absolvoval školenie zo základou finančnej gramotnosti"));

        listDataChild.put(listDataHeader.get(0), top250);
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);*/
    }

    private List getNotesList(String attribute)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        List<ChildItems> notes = new ArrayList<ChildItems>();
        Cursor c = db.getNotesByAttribute(attribute, parseInt(personId));
        while (c.moveToNext())
        {
            try
            {
                notes.add(new ChildItems(c.getInt(c.getColumnIndex("_id")), c.getString(c.getColumnIndex("datec")) + " " + c.getString(c.getColumnIndex("notetext"))));
            }
            catch (Exception e) {
                Log.e("LogMarks", "Error " + e.toString());
            }

        }
        return notes;
    }
}
