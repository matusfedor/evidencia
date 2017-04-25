package com.prodigus.com.prodigus.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.prodigus.com.prodigus.Genders;
import com.prodigus.com.prodigus.MySQLiteHelper;
import com.prodigus.com.prodigus.R;
import com.prodigus.com.prodigus.activity.TabStatistics;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.IOException;
import java.text.SimpleDateFormat;
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

    private LinearLayout chartLyt;
    private Animation fadeAnim;
    private GraphicalView chartView;

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
        chartLyt = (LinearLayout) myFragmentView.findViewById(R.id.chart);

        db = new MySQLiteHelper(getActivity());
        setHasOptionsMenu(true);

        ((TabStatistics)getActivity()).setFragmentRefreshListener(new TabStatistics.FragmentRefreshListener() {
            @Override
            public void onRefresh() {
                Boolean isConnected;
                try{
                    isConnected = isConnected();
                }
                catch(InterruptedException ex) { isConnected = false;}
                catch(IOException ex) { isConnected = false;}


                //prepareStatData();
            }
        });

        chartLyt.addView(createTempGraph(1), 0);
        //prepareStatData();

        return myFragmentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void prepareStatData() {
        ArrayList<Entry> entries = new ArrayList<>();
        Cursor cValue = db.getStatCounts(0);
        int dayStat = 0;
        while (cValue.moveToNext())
        {
            try
            {
                entries.add(new Entry(cValue.getFloat(0), dayStat));
                dayStat++;
            }
            catch (Exception e) {
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setDrawValues(false);
        

        /*BarDataSet dataset = new BarDataSet(entries, "Klienti");
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
        }*/

        /*BarChart myChart = (BarChart) myFragmentView.findViewById(R.id.chart);
        BarData data = new BarData(labels, dataset);
        myChart.setData(data);
        myChart.setDescription("");

        myChart.invalidate();*/
    }

    private View createTempGraph(int line) {
        // We start creating the XYSeries to plot the temperature
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");

        TimeSeries series = new TimeSeries("Denny graf");
        //XYSeries series = new XYSeries("Denny graf");

        Cursor c = db.getDayStatistics(8);
        while (c.moveToNext())
        {
            try
            {
                series.add(sdf.parse(c.getString(c.getColumnIndex("datum"))),c.getDouble(c.getColumnIndex("cnt")));
            }
            catch (Exception e) {
                Log.i("",e.getMessage());
            }
        }




        // Now we create the renderer
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2);
        renderer.setColor(Color.RED);
        // Include low and max value
        renderer.setDisplayBoundingPoints(true);
        // we add point markers
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setPointStrokeWidth(3);

        //second serie
        /*XYSeries series2 = new XYSeries("AFA graf");
        int hours = 0;
        for (int i=10; i< 20; i++) {
            series2.add(hours++, i);
        }
        XYSeriesRenderer renderer2 = new XYSeriesRenderer();
        renderer2.setLineWidth(2);
        renderer2.setColor(Color.BLUE);
        renderer2.setDisplayBoundingPoints(true);
        renderer2.setPointStyle(PointStyle.CIRCLE);
        renderer2.setPointStrokeWidth(3);
*/

        // Now we add our series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        /*if(line == 2) {
            dataset.addSeries(series2);
        }*/

        // Finaly we create the multiple series renderer to control the graph
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);

        /*if(line == 2) {
            mRenderer.addSeriesRenderer(renderer2);
        }*/

        // We want to avoid black border
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
        // Disable Pan on two axis
        mRenderer.setPanEnabled(false, false);
        //mRenderer.setYAxisMax(35);
        mRenderer.setYAxisMin(0);
        mRenderer.setShowGrid(true); // we show the grid
        chartView = ChartFactory.getTimeChartView(getActivity(), dataset, mRenderer, "dd.MM");


        // Enable chart click
        mRenderer.setClickEnabled(true);
        /*chartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyAnim(v, createPressGraph());
            }
        });*/

        return chartView;
    }

    private void applyAnim(final View v, final View nextView) {

        Animation.AnimationListener list = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                chartLyt.removeViewAt(0);
                chartLyt.addView(nextView,0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        fadeAnim.setAnimationListener(list);
        v.startAnimation(fadeAnim);
    }

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_statistics, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.red:
                if (item.isChecked())
                {
                    createTempGraph(2);
                    chartView.repaint();
                    chartLyt.addView(createTempGraph(2), 0);
                    item.setChecked(false);
                }
                else item.setChecked(true);

                return true;
            case R.id.blue:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
