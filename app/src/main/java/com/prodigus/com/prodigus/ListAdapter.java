package com.prodigus.com.prodigus;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Shahab
 * Date: 8/22/12
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListAdapter extends BaseAdapter {

    private static final String TAG = ListAdapter.class.getSimpleName();
    ArrayList<DataModel> listArray;

    public ListAdapter() {
        listArray = new ArrayList<DataModel>(5);
        listArray.add(new DataModel("name1", 5, 1.8, "Java"));
        listArray.add(new DataModel("name2", 10, 2.8, "Python"));
        listArray.add(new DataModel("name3", 15, 3.8, "Django"));
        listArray.add(new DataModel("name4", 20, 4.8, "Groovy"));
        listArray.add(new DataModel("name5", 25, 5.8, "Maven"));
    }


    public int getCount() {
        return listArray.size();    // total number of elements in the list
    }


    public Object getItem(int i) {
        return listArray.get(i);    // single item in the list
    }


    public long getItemId(int i) {
        return i;                   // index number
    }

    public View getView(int index, View view, final ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.notelistview, parent, false);
        }

        final DataModel dataModel = listArray.get(index);

        TextView id = (TextView) view.findViewById(R.id.noteID);
        id.setText(dataModel.getName());

        TextView text = (TextView) view.findViewById(R.id.tvText);
        text.setText(dataModel.getName());

        TextView date = (TextView) view.findViewById(R.id.tvDate);
        date.setText(dataModel.getName());

        ImageButton button = (ImageButton) view.findViewById(R.id.delNote);

        // button click listener
        // this chunk of code will run, if user click the button
        // because, we set the click listener on the button only

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d(TAG, "string: " + dataModel.getName());
                Log.d(TAG, "int: " + dataModel.getAnInt());
                Log.d(TAG, "double: " + dataModel.getaDouble());
                Log.d(TAG, "otherData: " + dataModel.getOtherData());

                Toast.makeText(parent.getContext(), "button clicked: " + dataModel.getAnInt(), Toast.LENGTH_SHORT).show();
            }
        });


        // if you commented out the above chunk of code and
        // activate this chunk of code
        // then if user click on any view inside the list view (except button)
        // this chunk of code will execute
        // because we set the click listener on the whole view

        /*
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d(TAG, "string: " + dataModel.getName());
                Log.d(TAG, "int: " + dataModel.getAnInt());
                Log.d(TAG, "double: " + dataModel.getaDouble());
                Log.d(TAG, "otherData: " + dataModel.getOtherData());

                Toast.makeText(parent.getContext(), "view clicked: " + dataModel.getOtherData(), Toast.LENGTH_SHORT).show();
            }
        });*/

        return view;
    }
}