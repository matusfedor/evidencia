package com.prodigus.com.prodigus;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.NodeList;

public class TodoCursorAdapter extends CursorAdapter {
    public TodoCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);this.mContext = context;
    }

    MySQLiteHelper db;
    private Context mContext;
    private Cursor mCursor;
    String posN;

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.notelistview, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.

    private View.OnClickListener btnButtonOFFclicked = new View.OnClickListener() {
        public void onClick(View view) {
            int position = (Integer) view.getTag();
            mCursor.moveToPosition(position);
            String idnote = mCursor.getString(mCursor. getColumnIndexOrThrow("_id"));

            Log.d("TAG", "POSITION: " + String.valueOf(position) + "ID: " + idnote );

            db = new MySQLiteHelper(mContext);
            db.delNote(idnote);

            //this.changeCursor(DB.listCompanies(context));

            mCursor.requery();

            //ss.changeCursor(mCursor);
            //.swapCursor(mCursor);
            notifyDataSetChanged();

            //mCursor.requery();

            //Toast.makeText(mContext,mCursor.getString(mCursor.getColumnIndex(FeedReaderContract.Notes._ID)), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        this.mCursor = cursor;

        TextView id = (TextView) view.findViewById(R.id.noteID);
        TextView text = (TextView) view.findViewById(R.id.tvText);
        TextView date = (TextView) view.findViewById(R.id.tvDate);
        ImageButton button = (ImageButton) view.findViewById(R.id.delNote);

        // Extract properties from cursor
        String s_id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        String s_text = cursor.getString(cursor.getColumnIndexOrThrow("notetext"));
        String s_date = cursor.getString(cursor.getColumnIndexOrThrow("datec"));

        //int priority = cursor.getInt(cursor.getColumnIndexOrThrow("priority"));
        // Populate fields with extracted properties
        id.setText(s_id);
        text.setText(s_text);
        date.setText(s_date);

        button.setOnClickListener(btnButtonOFFclicked);
        button.setTag(cursor.getPosition());
/*
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TextView id = (TextView) view.findViewById(R.id.noteID);
                posN = id.toString();

            }
            });*/

        /*
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TextView id = (TextView) view.findViewById(R.id.noteID);
                String note = ((TextView) view.findViewById(R.id.noteID)).getText().toString();
               // Toast.makeText(context, "button clicked: " + id.getText(), Toast.LENGTH_SHORT).show();
                TextView notetext = (TextView) view.findViewById(R.id.tvText);
                notetext.setText("AAA");

               // Log.d("TAG", "pos: " + note );

            }
        });*/

    }


}