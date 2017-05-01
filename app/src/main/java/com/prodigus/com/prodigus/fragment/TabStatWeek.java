package com.prodigus.com.prodigus.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabStatWeek.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabStatWeek#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabStatWeek extends Fragment {
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

    private LinearLayout chartLyt;
    private Animation fadeAnim;
    private GraphicalView chartView;

    private boolean serieCheckedAfa = true;
    private boolean serieCheckedTelk = true;
    private boolean serieCheckedTerm = false;
    private boolean serieCheckedFa = false;
    private boolean serieCheckedPk = false;
    private boolean serieCheckedKlient = false;

    private String logUser;

    public TabStatWeek() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TabStatWeek newInstance(String selectedUser) {
        TabStatWeek fragment = new TabStatWeek();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, selectedUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            logUser = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myFragmentView = inflater.inflate(R.layout.fragment_tab_stat_week, container, false);
        chartLyt = (LinearLayout) myFragmentView.findViewById(R.id.chartWeek);
        db = new MySQLiteHelper(getActivity());

        setHasOptionsMenu(true);

        ((TabStatistics)getActivity()).setFragmentRefreshListener(new TabStatistics.FragmentRefreshListener() {
            @Override
            public void onRefresh(String logname) {
                logUser = logname;
                //prepareStatData();
            }
        });

        //prepareStatData();
        chartLyt.addView(createTempGraph(), 0);

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
        Cursor cValue = db.getStatCounts(7);
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

        /*BarChart myChart = (BarChart) myFragmentView.findViewById(R.id.chartWeek);
        BarData data = new BarData(labels, dataset);
        myChart.setData(data);
        myChart.setDescription("");

        myChart.invalidate();*/
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

    private View createTempGraph() {
        // We start creating the XYSeries to plot the temperature
        SimpleDateFormat sdf = new SimpleDateFormat("ww.yyyy");

        TimeSeries seriesAfa = new TimeSeries("Denný graf Anketa finančná analýza");
        TimeSeries seriesTelk = new TimeSeries("Denný graf Telefonický hovor s kontaktom");
        TimeSeries seriesTerm = new TimeSeries("Denný graf Termín");
        TimeSeries seriesFa = new TimeSeries("Denný graf Finančná analýza");
        TimeSeries seriesPk = new TimeSeries("Denný graf Potenciálny klienti");
        TimeSeries seriesKlient = new TimeSeries("Denný graf Klienti");

        double maxValue = 0;

        if(serieCheckedAfa)
        {
            for(int k=0; k < 30; k++)
            {
                Cursor c = db.getWeekStatistics(8, k);
                while (c.moveToNext())
                {
                    try
                    {
                        seriesAfa.add(sdf.parse(c.getString(c.getColumnIndex("week"))),c.getDouble(c.getColumnIndex("cnt")));

                        if(c.getDouble(c.getColumnIndex("cnt")) > maxValue)
                        {
                            maxValue = c.getDouble(c.getColumnIndex("cnt"));
                        }
                    }
                    catch (Exception e) {
                        Log.i("",e.getMessage());
                    }
                }
            }
        }

        if(serieCheckedTelk)
        {
            for(int k=0; k < 30; k++) {
                Cursor c = db.getWeekStatistics(6, k);
                while (c.moveToNext()) {
                    try {
                        seriesTelk.add(sdf.parse(c.getString(c.getColumnIndex("week"))), c.getDouble(c.getColumnIndex("cnt")));

                        if (c.getDouble(c.getColumnIndex("cnt")) > maxValue) {
                            maxValue = c.getDouble(c.getColumnIndex("cnt"));
                        }
                    } catch (Exception e) {
                        Log.i("", e.getMessage());
                    }
                }
            }
        }

        if(serieCheckedTerm)
        {
            for(int k=0; k < 30; k++) {
                Cursor c = db.getWeekStatistics(15,k);
                while (c.moveToNext()) {
                    try {
                        seriesTerm.add(sdf.parse(c.getString(c.getColumnIndex("week"))), c.getDouble(c.getColumnIndex("cnt")));

                        if (c.getDouble(c.getColumnIndex("cnt")) > maxValue) {
                            maxValue = c.getDouble(c.getColumnIndex("cnt"));
                        }
                    } catch (Exception e) {
                        Log.i("", e.getMessage());
                    }
                }
            }
        }

        if(serieCheckedFa)
        {
            for(int k=0; k < 30; k++) {
                Cursor c = db.getWeekStatistics(16, k);
                while (c.moveToNext()) {
                    try {
                        seriesFa.add(sdf.parse(c.getString(c.getColumnIndex("week"))), c.getDouble(c.getColumnIndex("cnt")));
                        if (c.getDouble(c.getColumnIndex("cnt")) > maxValue) {
                            maxValue = c.getDouble(c.getColumnIndex("cnt"));
                        }
                    } catch (Exception e) {
                        Log.i("", e.getMessage());
                    }
                }
            }
        }

        if(serieCheckedPk)
        {
            for(int k=0; k < 30; k++) {
                Cursor c = db.getWeekStatistics(2, k);
                while (c.moveToNext()) {
                    try {
                        seriesPk.add(sdf.parse(c.getString(c.getColumnIndex("week"))), c.getDouble(c.getColumnIndex("cnt")));
                        if (c.getDouble(c.getColumnIndex("cnt")) > maxValue) {
                            maxValue = c.getDouble(c.getColumnIndex("cnt"));
                        }
                    } catch (Exception e) {
                        Log.i("", e.getMessage());
                    }
                }
            }
        }

        if(serieCheckedKlient)
        {
            for(int k=0; k < 30; k++) {
                Cursor c = db.getWeekStatistics(1, k);
                while (c.moveToNext()) {
                    try {
                        seriesKlient.add(sdf.parse(c.getString(c.getColumnIndex("week"))), c.getDouble(c.getColumnIndex("cnt")));
                        if (c.getDouble(c.getColumnIndex("cnt")) > maxValue) {
                            maxValue = c.getDouble(c.getColumnIndex("cnt"));
                        }
                    } catch (Exception e) {
                        Log.i("", e.getMessage());
                    }
                }
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
        renderer.setShowLegendItem(false);

        //second serie
        XYSeriesRenderer renderer2 = new XYSeriesRenderer();
        renderer2.setLineWidth(2);
        renderer2.setColor(Color.BLUE);
        // Include low and max value
        renderer2.setDisplayBoundingPoints(true);
        // we add point markers
        renderer2.setPointStyle(PointStyle.CIRCLE);
        renderer2.setPointStrokeWidth(3);
        renderer2.setShowLegendItem(false);

        XYSeriesRenderer renderer3 = new XYSeriesRenderer();
        renderer3.setLineWidth(2);
        renderer3.setColor(Color.GREEN);
        // Include low and max value
        renderer3.setDisplayBoundingPoints(true);
        // we add point markers
        renderer3.setPointStyle(PointStyle.CIRCLE);
        renderer3.setPointStrokeWidth(3);
        renderer3.setShowLegendItem(false);

        XYSeriesRenderer renderer4 = new XYSeriesRenderer();
        renderer4.setLineWidth(2);
        renderer4.setColor(Color.YELLOW);
        // Include low and max value
        renderer4.setDisplayBoundingPoints(true);
        // we add point markers
        renderer4.setPointStyle(PointStyle.CIRCLE);
        renderer4.setPointStrokeWidth(3);
        renderer4.setShowLegendItem(false);

        XYSeriesRenderer renderer5 = new XYSeriesRenderer();
        renderer5.setLineWidth(2);
        renderer5.setColor(Color.BLACK);
        renderer5.setDisplayBoundingPoints(true);
        renderer5.setPointStyle(PointStyle.CIRCLE);
        renderer5.setPointStrokeWidth(3);
        renderer5.setShowLegendItem(false);

        XYSeriesRenderer renderer6 = new XYSeriesRenderer();
        renderer6.setLineWidth(2);
        renderer6.setColor(Color.CYAN);
        // Include low and max value
        renderer6.setDisplayBoundingPoints(true);
        // we add point markers
        renderer6.setPointStyle(PointStyle.CIRCLE);
        renderer6.setPointStrokeWidth(3);
        renderer6.setShowLegendItem(false);

        // Now we add our series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        if(serieCheckedAfa) {
            dataset.addSeries(seriesAfa);
        }
        if(serieCheckedTelk)
        {
            dataset.addSeries(seriesTelk);
        }
        if(serieCheckedTerm)
        {
            dataset.addSeries(seriesTerm);
        }
        if(serieCheckedFa)
        {
            dataset.addSeries(seriesFa);
        }
        if(serieCheckedPk)
        {
            dataset.addSeries(seriesPk);
        }
        if(serieCheckedKlient)
        {
            dataset.addSeries(seriesKlient);
        }
        // Finaly we create the multiple series renderer to control the graph
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        if(serieCheckedAfa) {
            mRenderer.addSeriesRenderer(renderer);
        }
        if(serieCheckedTelk)
        {
            mRenderer.addSeriesRenderer(renderer2);
        }
        if(serieCheckedTerm)
        {
            mRenderer.addSeriesRenderer(renderer3);
        }
        if(serieCheckedFa)
        {
            mRenderer.addSeriesRenderer(renderer4);
        }
        if(serieCheckedPk)
        {
            mRenderer.addSeriesRenderer(renderer5);
        }
        if(serieCheckedKlient)
        {
            mRenderer.addSeriesRenderer(renderer6);
        }

        // We want to avoid black border
        //mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
        mRenderer.setMarginsColor(Color.WHITE); // transparent margins
        mRenderer.setPanEnabled(true, true);
        mRenderer.setYAxisMax(maxValue + 2);
        mRenderer.setYAxisMin(0);
        mRenderer.setShowGrid(true);
        mRenderer.setLabelsTextSize(30);
        mRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        mRenderer.setMargins(new int[]{40,50,20,20});
        mRenderer.setYLabelsPadding(10);
        mRenderer.setAxesColor(Color.BLACK);
        mRenderer.setXLabelsColor(Color.BLACK);
        mRenderer.setYLabelsColor(0, Color.BLACK);
        mRenderer.setZoomEnabled(true, true);
        mRenderer.setInScroll(true);

        chartView = ChartFactory.getTimeChartView(getActivity(), dataset, mRenderer, "ww.yyyy");

        // Enable chart click
        mRenderer.setClickEnabled(true);
        chartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //applyAnim(v, createPressGraph());
            }
        });

        return chartView;
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
                    serieCheckedAfa = false;
                    chartLyt.addView(createTempGraph(), 0);
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedAfa = true;
                    chartLyt.addView(createTempGraph(), 0);
                    chartView.repaint();
                    item.setChecked(true);
                }

                return true;
            case R.id.blue:
                if (item.isChecked())
                {
                    serieCheckedTelk = false;
                    chartLyt.addView(createTempGraph(), 0);
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedTelk = true;
                    chartLyt.addView(createTempGraph(), 0);
                    chartView.repaint();
                    item.setChecked(true);
                }
                return true;

            case R.id.green:
                if (item.isChecked())
                {
                    serieCheckedTerm = false;
                    chartLyt.addView(createTempGraph(), 0);
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedTerm = true;
                    chartLyt.addView(createTempGraph(), 0);
                    chartView.repaint();
                    item.setChecked(true);
                }
                return true;

            case R.id.yellow:
                if (item.isChecked())
                {
                    serieCheckedFa = false;
                    chartLyt.addView(createTempGraph(), 0);
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedFa = true;
                    chartLyt.addView(createTempGraph(), 0);
                    chartView.repaint();
                    item.setChecked(true);
                }
                return true;

            case R.id.black:
                if (item.isChecked())
                {
                    serieCheckedPk = false;
                    chartLyt.addView(createTempGraph(), 0);
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedPk = true;
                    chartLyt.addView(createTempGraph(), 0);
                    chartView.repaint();
                    item.setChecked(true);
                }
                return true;

            case R.id.cyan:
                if (item.isChecked())
                {
                    serieCheckedKlient = false;
                    chartLyt.addView(createTempGraph(), 0);
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedKlient = true;
                    chartLyt.addView(createTempGraph(), 0);
                    chartView.repaint();
                    item.setChecked(true);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
