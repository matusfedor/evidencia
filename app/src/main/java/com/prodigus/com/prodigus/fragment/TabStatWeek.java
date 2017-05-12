package com.prodigus.com.prodigus.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
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

    MySQLiteHelper db;

    private OnFragmentInteractionListener mListener;
    private String logUser;
    private String selectedUser;
    private int stepCount = 30;

    private LinearLayout chartLyt;
    private Animation fadeAnim;
    private GraphicalView chartView;

    private boolean serieCheckedAfa = true;
    private boolean serieCheckedTelk = true;
    private boolean serieCheckedTerm = false;
    private boolean serieCheckedFa = false;
    private boolean serieCheckedPk = false;
    private boolean serieCheckedKlient = false;

    public TabStatWeek() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TabStatWeek newInstance(String selectedUser, String logUser) {
        TabStatWeek fragment = new TabStatWeek();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, selectedUser);
        args.putString(ARG_PARAM2, logUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            selectedUser = getArguments().getString(ARG_PARAM1);
            logUser = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myFragmentView = inflater.inflate(R.layout.fragment_tab_stat_week, container, false);
        chartLyt = (LinearLayout) myFragmentView.findViewById(R.id.chartWeek);
        db = new MySQLiteHelper(getActivity());

        setHasOptionsMenu(true);
        //chartLyt.addView(createTempGraph(), 0);
        addViewChart(logUser.equals(selectedUser));

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

    private double getStatistics(TimeSeries timeSeries, int attribute, double maxValues, String selectedUserDdl)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("ww.yyyy");
        double maxValue = maxValues;

        timeSeries.clear();

        for (int s = 0; s < stepCount; s++) {
            Cursor c = selectedUserDdl == null ? db.getWeekStatistics(attribute, s) : db.getWeekStatistics(8, s, selectedUserDdl);
            while (c.moveToNext()) {
                try {
                    timeSeries.add(sdf.parse(c.getString(c.getColumnIndex("week"))), c.getDouble(c.getColumnIndex("cnt")));

                    if (c.getDouble(c.getColumnIndex("cnt")) > maxValue) {
                        maxValue = c.getDouble(c.getColumnIndex("cnt"));
                    }
                } catch (Exception e) {
                    Log.i("", e.getMessage());
                }
                finally {
                    c.close();
                    db.close();
                }
            }
            c.close();
            db.close();
        }

        return maxValue;
    }

    private XYSeriesRenderer setRenderer(int color, XYSeriesRenderer xySeriesRenderer)
    {
        xySeriesRenderer.setLineWidth(2);
        xySeriesRenderer.setColor(color);
        xySeriesRenderer.setDisplayBoundingPoints(true);
        xySeriesRenderer.setPointStyle(PointStyle.CIRCLE);
        xySeriesRenderer.setPointStrokeWidth(3);
        xySeriesRenderer.setShowLegendItem(false);

        return xySeriesRenderer;
    }

    private View createTempGraph() {

        TimeSeries seriesAfa = new TimeSeries("Denný graf Anketa finančná analýza");
        TimeSeries seriesTelk = new TimeSeries("Denný graf Telefonický hovor s kontaktom");
        TimeSeries seriesTerm = new TimeSeries("Denný graf Termín");
        TimeSeries seriesFa = new TimeSeries("Denný graf Finančná analýza");
        TimeSeries seriesPk = new TimeSeries("Denný graf Potenciálny klienti");
        TimeSeries seriesKlient = new TimeSeries("Denný graf Klienti");

        double maxValue = 0;

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

        // Now we create the renderer
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer = setRenderer(Color.RED, renderer);

        XYSeriesRenderer renderer2 = new XYSeriesRenderer();
        renderer2 = setRenderer(Color.BLUE, renderer2);

        XYSeriesRenderer renderer3 = new XYSeriesRenderer();
        renderer3 = setRenderer(Color.GREEN, renderer3);

        XYSeriesRenderer renderer4 = new XYSeriesRenderer();
        renderer4 = setRenderer(Color.GRAY, renderer4);

        XYSeriesRenderer renderer5 = new XYSeriesRenderer();
        renderer5 = setRenderer(Color.BLACK, renderer5);

        XYSeriesRenderer renderer6 = new XYSeriesRenderer();
        renderer6 = setRenderer(Color.CYAN, renderer6);

        if(serieCheckedAfa) {
            maxValue = getStatistics(seriesAfa, 8, maxValue, null);
            dataset.addSeries(seriesAfa);
            mRenderer.addSeriesRenderer(renderer);
        }
        if(serieCheckedTelk)
        {
            maxValue = getStatistics(seriesTelk, 6, maxValue, null);
            dataset.addSeries(seriesTelk);
            mRenderer.addSeriesRenderer(renderer2);
        }
        if(serieCheckedTerm)
        {
            maxValue = getStatistics(seriesTerm, 15, maxValue, null);
            dataset.addSeries(seriesTerm);
            mRenderer.addSeriesRenderer(renderer3);
        }
        if(serieCheckedFa)
        {
            maxValue = getStatistics(seriesFa, 16, maxValue, null);
            dataset.addSeries(seriesFa);
            mRenderer.addSeriesRenderer(renderer4);
        }
        if(serieCheckedPk)
        {
            maxValue = getStatistics(seriesPk, 2, maxValue, null);
            dataset.addSeries(seriesPk);
            mRenderer.addSeriesRenderer(renderer5);
        }
        if(serieCheckedKlient)
        {
            maxValue = getStatistics(seriesKlient, 1, maxValue, null);
            dataset.addSeries(seriesKlient);
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

    private View createTempUserGraph() {
        TimeSeries seriesAfa = new TimeSeries("Denný graf Anketa finančná analýza");
        TimeSeries seriesTelk = new TimeSeries("Denný graf Telefonický hovor s kontaktom");
        TimeSeries seriesTerm = new TimeSeries("Denný graf Termín");
        TimeSeries seriesFa = new TimeSeries("Denný graf Finančná analýza");
        TimeSeries seriesPk = new TimeSeries("Denný graf Potenciálny klienti");
        TimeSeries seriesKlient = new TimeSeries("Denný graf Klienti");

        double maxValue = 0;

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

        // Now we create the renderer
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer = setRenderer(Color.RED, renderer);

        XYSeriesRenderer renderer2 = new XYSeriesRenderer();
        renderer2 = setRenderer(Color.BLUE, renderer2);

        XYSeriesRenderer renderer3 = new XYSeriesRenderer();
        renderer3 = setRenderer(Color.GREEN, renderer3);

        XYSeriesRenderer renderer4 = new XYSeriesRenderer();
        renderer4 = setRenderer(Color.GRAY, renderer4);

        XYSeriesRenderer renderer5 = new XYSeriesRenderer();
        renderer5 = setRenderer(Color.BLACK, renderer5);

        XYSeriesRenderer renderer6 = new XYSeriesRenderer();
        renderer6 = setRenderer(Color.CYAN, renderer6);

        if(serieCheckedAfa) {
            maxValue = getStatistics(seriesAfa, 8, maxValue, selectedUser);
            dataset.addSeries(seriesAfa);
            mRenderer.addSeriesRenderer(renderer);
        }
        if(serieCheckedTelk)
        {
            maxValue = getStatistics(seriesTelk, 6, maxValue, selectedUser);
            dataset.addSeries(seriesTelk);
            mRenderer.addSeriesRenderer(renderer2);
        }
        if(serieCheckedTerm)
        {
            maxValue = getStatistics(seriesTerm, 15, maxValue, selectedUser);
            dataset.addSeries(seriesTerm);
            mRenderer.addSeriesRenderer(renderer3);
        }
        if(serieCheckedFa)
        {
            maxValue = getStatistics(seriesFa, 16, maxValue, selectedUser);
            dataset.addSeries(seriesFa);
            mRenderer.addSeriesRenderer(renderer4);
        }
        if(serieCheckedPk)
        {
            maxValue = getStatistics(seriesPk, 2, maxValue, selectedUser);
            dataset.addSeries(seriesPk);
            mRenderer.addSeriesRenderer(renderer5);
        }
        if(serieCheckedKlient)
        {
            maxValue = getStatistics(seriesKlient, 1, maxValue, selectedUser);
            dataset.addSeries(seriesKlient);
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

    public void addViewChart(Boolean logged)
    {
        if(logged)
        {
            chartLyt.addView(createTempGraph(), 0);
        }
        else
        {
            chartLyt.addView(createTempUserGraph(), 0);
            //chartLyt.addView(createTempGraphFromWS(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.red:
                if (item.isChecked())
                {
                    serieCheckedAfa = false;
                    addViewChart(logUser.equals(selectedUser));
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedAfa = true;
                    addViewChart(logUser.equals(selectedUser));
                    chartView.repaint();
                    item.setChecked(true);
                }

                return true;
            case R.id.blue:
                if (item.isChecked())
                {
                    serieCheckedTelk = false;
                    addViewChart(logUser.equals(selectedUser));
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedTelk = true;
                    addViewChart(logUser.equals(selectedUser));
                    chartView.repaint();
                    item.setChecked(true);
                }
                return true;

            case R.id.green:
                if (item.isChecked())
                {
                    serieCheckedTerm = false;
                    addViewChart(logUser.equals(selectedUser));
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedTerm = true;
                    addViewChart(logUser.equals(selectedUser));
                    chartView.repaint();
                    item.setChecked(true);
                }
                return true;

            case R.id.yellow:
                if (item.isChecked())
                {
                    serieCheckedFa = false;
                    addViewChart(logUser.equals(selectedUser));
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedFa = true;
                    addViewChart(logUser.equals(selectedUser));
                    chartView.repaint();
                    item.setChecked(true);
                }
                return true;

            case R.id.black:
                if (item.isChecked())
                {
                    serieCheckedPk = false;
                    addViewChart(logUser.equals(selectedUser));
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedPk = true;
                    addViewChart(logUser.equals(selectedUser));
                    chartView.repaint();
                    item.setChecked(true);
                }
                return true;

            case R.id.cyan:
                if (item.isChecked())
                {
                    serieCheckedKlient = false;
                    addViewChart(logUser.equals(selectedUser));
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedKlient = true;
                    addViewChart(logUser.equals(selectedUser));
                    chartView.repaint();
                    item.setChecked(true);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setSelectedUser(String selectedUser){
        this.selectedUser = selectedUser;
        //Toast.makeText(getActivity(), "Test week: " + selectedUser, Toast.LENGTH_SHORT).show();
    }

    public void setLogUser(String logUser){
        this.logUser = logUser;
    }

    @Override
    public void onResume() {
        super.onResume();
        addViewChart(logUser.equals(selectedUser));
    }
}
