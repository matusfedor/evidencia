package com.prodigus.com.prodigus.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.prodigus.com.prodigus.Genders;
import com.prodigus.com.prodigus.MySQLiteHelper;
import com.prodigus.com.prodigus.R;
import com.prodigus.com.prodigus.activity.TabStatistics;

import java.io.IOException;
import java.util.ArrayList;

public class TabStatDay extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View myFragmentView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private float testfl = 0f;
    MySQLiteHelper db;

    private OnFragmentInteractionListener mListener;

    public TabStatDay() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TabStatDay newInstance(String param1, String param2) {
        TabStatDay fragment = new TabStatDay();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myFragmentView = inflater.inflate(R.layout.fragment_tab_stat_day, container, false);
        db = new MySQLiteHelper(getActivity());

        ((TabStatistics)getActivity()).setFragmentRefreshListener(new TabStatistics.FragmentRefreshListener() {
            @Override
            public void onRefresh() {
                Boolean isConnected;
                try{
                    isConnected = isConnected();
                }
                catch(InterruptedException ex) { isConnected = false;}
                catch(IOException ex) { isConnected = false;}


                prepareStatData();
            }
        });

        prepareStatData();

        return myFragmentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void prepareStatData() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        Cursor cValue = db.getStatCounts(0);
        int dayStat = 0;
        while (cValue.moveToNext())
        {
            try
            {
                entries.add(new BarEntry(cValue.getFloat(0), dayStat));
                dayStat++;
            }
            catch (Exception e) {
            }
        }

        BarDataSet dataset = new BarDataSet(entries, "Klienti");
        ArrayList<String> labels = new ArrayList<String>();
        Cursor c = db.getAllStatMarks();
        while (c.moveToNext())
        {
            try
            {
                labels.add(c.getString(c.getColumnIndex("att_sc")));
            }
            catch (Exception e) {
            }
        }

        BarChart myChart = (BarChart) myFragmentView.findViewById(R.id.chart);
        BarData data = new BarData(labels, dataset);
        myChart.setData(data);
        myChart.setDescription("");

        myChart.invalidate();
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public boolean isConnected() throws InterruptedException, IOException
    {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }
}
