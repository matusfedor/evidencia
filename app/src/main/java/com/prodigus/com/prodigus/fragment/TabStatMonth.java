package com.prodigus.com.prodigus.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.prodigus.com.prodigus.MySQLiteHelper;
import com.prodigus.com.prodigus.R;
import com.prodigus.com.prodigus.activity.TabStatistics;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabStatMonth.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabStatMonth#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabStatMonth extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View myFragmentView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MySQLiteHelper db;

    private OnFragmentInteractionListener mListener;

    public TabStatMonth() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabStatMonth.
     */
    // TODO: Rename and change types and number of parameters
    public static TabStatMonth newInstance(String param1, String param2) {
        TabStatMonth fragment = new TabStatMonth();
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

        myFragmentView = inflater.inflate(R.layout.fragment_tab_stat_month, container, false);
        db = new MySQLiteHelper(getActivity());

        ((TabStatistics)getActivity()).setFragmentRefreshListener(new TabStatistics.FragmentRefreshListener() {
            @Override
            public void onRefresh() {
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
        Cursor cValue = db.getStatCounts(30);
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

        BarChart myChart = (BarChart) myFragmentView.findViewById(R.id.chartMonth);
        BarData data = new BarData(labels, dataset);
        myChart.setData(data);
        myChart.setDescription("");

        myChart.invalidate();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
