package com.prodigus.com.prodigus.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.prodigus.com.prodigus.Stats;
import com.prodigus.com.prodigus.Users;
import com.prodigus.com.prodigus.activity.Synchronize;
import com.prodigus.com.prodigus.activity.TabStatistics;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private String logUser;
    private String logname;

    private LinearLayout chartLyt;
    private Animation fadeAnim;
    private GraphicalView chartView;

    private boolean serieCheckedAfa = true;
    private boolean serieCheckedTelk = true;
    private boolean serieCheckedTerm = false;
    private boolean serieCheckedFa = false;
    private boolean serieCheckedPk = false;
    private boolean serieCheckedKlient = false;

    private final String NAMESPACE = "http://microsoft.com/webservices/";
    private final String URL = "http://evidencia.prodigus.sk/EvidenceService.asmx";
    private final String SOAP_ACTION = "http://microsoft.com/webservices/GetStatistics";
    private final String METHOD_NAME = "GetStatistics";

    TimeSeries seriesAfa;
    TimeSeries seriesTelk;
    TimeSeries seriesTerm;
    TimeSeries seriesFa;
    TimeSeries seriesPk;
    TimeSeries seriesKlient;

    public TabStatDay() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TabStatDay newInstance(String selectedUser) {
        TabStatDay fragment = new TabStatDay();
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

        myFragmentView = inflater.inflate(R.layout.fragment_tab_stat_day, container, false);
        chartLyt = (LinearLayout) myFragmentView.findViewById(R.id.chart);

        db = new MySQLiteHelper(getActivity());
        setHasOptionsMenu(true);

        /*((TabStatistics)getActivity()).setFragmentRefreshListener(new TabStatistics.FragmentRefreshListener() {
            @Override
            public void onRefresh() {

                Toast.makeText(getActivity(), "Test day", Toast.LENGTH_SHORT).show();
                Boolean isConnected;
                try{
                    isConnected = isConnected();
                }
                catch(InterruptedException ex) { isConnected = false;}
                catch(IOException ex) { isConnected = false;}


                //prepareStatData();
            }
        });*/

        ((TabStatistics)getActivity()).setFragmentRefreshListener(new TabStatistics.FragmentRefreshListener() {
            @Override
            public void onRefresh(String logname) {
                //prepareStatData();
                logUser = logname;
                Toast.makeText(getActivity(), "Test day", Toast.LENGTH_SHORT).show();
            }
        });

        Cursor authCursor = db.getAuth();
        if(Integer.valueOf(authCursor.getCount()) > 0) {

            if (authCursor.moveToFirst()) {
                logname = authCursor.getString(authCursor.getColumnIndex("logname"));
            }
            authCursor.close();
        }
        else {
            return null;
        }

        chartLyt.addView(createTempGraph(), 0);
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

    private View createTempGraph() {

        Cursor authCursor = db.getAuth();

        // We start creating the XYSeries to plot the temperature
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        TimeSeries seriesAfa = new TimeSeries("Denný graf Anketa finančná analýza");
        TimeSeries seriesTelk = new TimeSeries("Denný graf Telefonický hovor s kontaktom");
        TimeSeries seriesTerm = new TimeSeries("Denný graf Termín");
        TimeSeries seriesFa = new TimeSeries("Denný graf Finančná analýza");
        TimeSeries seriesPk = new TimeSeries("Denný graf Potenciálny klienti");
        TimeSeries seriesKlient = new TimeSeries("Denný graf Klienti");

        double maxValue = 0;

        if(serieCheckedAfa)
        {
            for (int s = 0; s < 30; s++) {
                Cursor c = db.getDayStatistics(8, s);
                while (c.moveToNext()) {
                    try {
                        seriesAfa.add(sdf.parse(c.getString(c.getColumnIndex("datum"))), c.getDouble(c.getColumnIndex("cnt")));

                        if (c.getDouble(c.getColumnIndex("cnt")) > maxValue) {
                            maxValue = c.getDouble(c.getColumnIndex("cnt"));
                        }
                    } catch (Exception e) {
                        Log.i("", e.getMessage());
                    }
                }
            }
        }

        if(serieCheckedTelk)
        {
            for(int s = 0; s < 30; s++)
            {
                Cursor c = db.getDayStatistics(6,s);
                while (c.moveToNext())
                {
                    try
                    {
                        seriesTelk.add(sdf.parse(c.getString(c.getColumnIndex("datum"))),c.getDouble(c.getColumnIndex("cnt")));

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

        if(serieCheckedTerm)
        {
            for(int s = 0; s < 30; s++)
            {
                Cursor c = db.getDayStatistics(15,s);
                while (c.moveToNext())
                {
                    try
                    {
                        seriesTerm.add(sdf.parse(c.getString(c.getColumnIndex("datum"))),c.getDouble(c.getColumnIndex("cnt")));

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

        if(serieCheckedFa)
        {
            for(int s = 0; s < 30; s++) {
                Cursor c = db.getDayStatistics(16,s);
                while (c.moveToNext()) {
                    try {
                        seriesFa.add(sdf.parse(c.getString(c.getColumnIndex("datum"))), c.getDouble(c.getColumnIndex("cnt")));

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
            for(int s = 0; s < 30; s++)
            {
                Cursor c = db.getDayStatistics(2,s);
                while (c.moveToNext())
                {
                    try
                    {
                        seriesPk.add(sdf.parse(c.getString(c.getColumnIndex("datum"))),c.getDouble(c.getColumnIndex("cnt")));

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

        if(serieCheckedKlient)
        {
            for(int s = 0; s < 30; s++)
            {
                Cursor c = db.getDayStatistics(1,s);
                while (c.moveToNext())
                {
                    try
                    {
                        seriesKlient.add(sdf.parse(c.getString(c.getColumnIndex("datum"))),c.getDouble(c.getColumnIndex("cnt")));

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

        chartView = ChartFactory.getTimeChartView(getActivity(), dataset, mRenderer, "dd.MM");
        //graphTimeframe

        /*
        mRenderer.setXLabels(0);
        String[] date={"25.04.2017","26.04.2017","27.04.2017"};
        for(int i=0;i<date.length;i++){
            mRenderer.addXTextLabel(i+1,date[i]);
        }
        mRenderer.setShowCustomTextGrid(true);
*/
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

        Cursor authCursor = db.getAuth();

        // We start creating the XYSeries to plot the temperature
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        TimeSeries seriesAfa = new TimeSeries("Denný graf Anketa finančná analýza");
        TimeSeries seriesTelk = new TimeSeries("Denný graf Telefonický hovor s kontaktom");
        TimeSeries seriesTerm = new TimeSeries("Denný graf Termín");
        TimeSeries seriesFa = new TimeSeries("Denný graf Finančná analýza");
        TimeSeries seriesPk = new TimeSeries("Denný graf Potenciálny klienti");
        TimeSeries seriesKlient = new TimeSeries("Denný graf Klienti");

        double maxValue = 0;

        if(serieCheckedAfa)
        {
            for (int s = 0; s < 30; s++) {
                Cursor c = db.getDayStatistics(8, s, logname);
                while (c.moveToNext()) {
                    try {
                        seriesAfa.add(sdf.parse(c.getString(c.getColumnIndex("datum"))), c.getDouble(c.getColumnIndex("cnt")));

                        if (c.getDouble(c.getColumnIndex("cnt")) > maxValue) {
                            maxValue = c.getDouble(c.getColumnIndex("cnt"));
                        }
                    } catch (Exception e) {
                        Log.i("", e.getMessage());
                    }
                }
            }
        }

        if(serieCheckedTelk)
        {
            for(int s = 0; s < 30; s++)
            {
                Cursor c = db.getDayStatistics(6,s, logname);
                while (c.moveToNext())
                {
                    try
                    {
                        seriesTelk.add(sdf.parse(c.getString(c.getColumnIndex("datum"))),c.getDouble(c.getColumnIndex("cnt")));

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

        if(serieCheckedTerm)
        {
            for(int s = 0; s < 30; s++)
            {
                Cursor c = db.getDayStatistics(15,s, logname);
                while (c.moveToNext())
                {
                    try
                    {
                        seriesTerm.add(sdf.parse(c.getString(c.getColumnIndex("datum"))),c.getDouble(c.getColumnIndex("cnt")));

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

        if(serieCheckedFa)
        {
            for(int s = 0; s < 30; s++) {
                Cursor c = db.getDayStatistics(16,s, logname);
                while (c.moveToNext()) {
                    try {
                        seriesFa.add(sdf.parse(c.getString(c.getColumnIndex("datum"))), c.getDouble(c.getColumnIndex("cnt")));

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
            for(int s = 0; s < 30; s++)
            {
                Cursor c = db.getDayStatistics(2,s, logname);
                while (c.moveToNext())
                {
                    try
                    {
                        seriesPk.add(sdf.parse(c.getString(c.getColumnIndex("datum"))),c.getDouble(c.getColumnIndex("cnt")));

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

        if(serieCheckedKlient)
        {
            for(int s = 0; s < 30; s++)
            {
                Cursor c = db.getDayStatistics(1,s, logname);
                while (c.moveToNext())
                {
                    try
                    {
                        seriesKlient.add(sdf.parse(c.getString(c.getColumnIndex("datum"))),c.getDouble(c.getColumnIndex("cnt")));

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

        chartView = ChartFactory.getTimeChartView(getActivity(), dataset, mRenderer, "dd.MM");
        //graphTimeframe

        /*
        mRenderer.setXLabels(0);
        String[] date={"25.04.2017","26.04.2017","27.04.2017"};
        for(int i=0;i<date.length;i++){
            mRenderer.addXTextLabel(i+1,date[i]);
        }
        mRenderer.setShowCustomTextGrid(true);
*/
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

    private View createTempGraphFromWS() {
        // We start creating the XYSeries to plot the temperature
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        TimeSeries seriesAfa = new TimeSeries("Denný graf Anketa finančná analýza");
        TimeSeries seriesTelk = new TimeSeries("Denný graf Telefonický hovor s kontaktom");
        TimeSeries seriesTerm = new TimeSeries("Denný graf Termín");
        TimeSeries seriesFa = new TimeSeries("Denný graf Finančná analýza");
        TimeSeries seriesPk = new TimeSeries("Denný graf Potenciálny klienti");
        TimeSeries seriesKlient = new TimeSeries("Denný graf Klienti");

        double maxValue = 0;

        if(serieCheckedAfa)
        {
            AsyncCallWS task = new AsyncCallWS();
            task.execute(8);
        }

        if(serieCheckedTelk)
        {
            AsyncCallWS task = new AsyncCallWS();
            task.execute(8);
        }

        if(serieCheckedTerm)
        {
            AsyncCallWS task = new AsyncCallWS();
            task.execute(15);
        }

        if(serieCheckedFa)
        {
            AsyncCallWS task = new AsyncCallWS();
            task.execute(16);
        }

        if(serieCheckedPk)
        {
            AsyncCallWS task = new AsyncCallWS();
            task.execute(2);
        }

        if(serieCheckedKlient)
        {
            AsyncCallWS task = new AsyncCallWS();
            task.execute(1);
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

        chartView = ChartFactory.getTimeChartView(getActivity(), dataset, mRenderer, "dd.MM");

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

    private void addViewChart(Boolean logged)
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
                    addViewChart(logUser.equals(logname));
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedAfa = true;
                    addViewChart(logUser.equals(logname));
                    chartView.repaint();
                    item.setChecked(true);
                }

                return true;
            case R.id.blue:
                if (item.isChecked())
                {
                    serieCheckedTelk = false;
                    addViewChart(logUser.equals(logname));
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedTelk = true;
                    addViewChart(logUser.equals(logname));
                    chartView.repaint();
                    item.setChecked(true);
                }
                return true;

            case R.id.green:
                if (item.isChecked())
                {
                    serieCheckedTerm = false;
                    addViewChart(logUser.equals(logname));
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedTerm = true;
                    addViewChart(logUser.equals(logname));
                    chartView.repaint();
                    item.setChecked(true);
                }
                return true;

            case R.id.yellow:
                if (item.isChecked())
                {
                    serieCheckedFa = false;
                    addViewChart(logUser.equals(logname));
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedFa = true;
                    addViewChart(logUser.equals(logname));
                    chartView.repaint();
                    item.setChecked(true);
                }
                return true;

            case R.id.black:
                if (item.isChecked())
                {
                    serieCheckedPk = false;
                    addViewChart(logUser.equals(logname));
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedPk = true;
                    addViewChart(logUser.equals(logname));
                    chartView.repaint();
                    item.setChecked(true);
                }
                return true;

            case R.id.cyan:
                if (item.isChecked())
                {
                    serieCheckedKlient = false;
                    addViewChart(logUser.equals(logname));
                    chartView.repaint();
                    item.setChecked(false);
                }
                else
                {
                    serieCheckedKlient = true;
                    addViewChart(logUser.equals(logname));
                    chartView.repaint();
                    item.setChecked(true);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public List<Stats> loadUserStatistics(String type, int steps, int attribute) {
        List<Stats> gens = null;

        String logname = "";
        String pin = "";

        Cursor c = db.getAuth();

        if(Integer.valueOf(c.getCount()) > 0) {

            if (c.moveToFirst()) {
                logname = c.getString(c.getColumnIndex("logname"));
                pin = c.getString(c.getColumnIndex("pin"));
            }
            c.close();
        }
        else {
            return null;
        }

        //Create request
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        PropertyInfo usernameProp = new PropertyInfo();
        usernameProp.setName("userName");
        usernameProp.setValue(logname);
        usernameProp.setType(String.class);
        request.addProperty(usernameProp);

        PropertyInfo typeProp = new PropertyInfo();
        typeProp.setName("type");
        typeProp.setValue(type);
        typeProp.setType(String.class);
        request.addProperty(typeProp);

        PropertyInfo stepsProp = new PropertyInfo();
        stepsProp.setName("steps");
        stepsProp.setValue(steps);
        stepsProp.setType(int.class);
        request.addProperty(stepsProp);

        PropertyInfo attributeProp = new PropertyInfo();
        attributeProp.setName("attribute");
        attributeProp.setValue(attribute);
        attributeProp.setType(int.class);
        request.addProperty(attributeProp);

        //Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        /*header*/
        Element h = new Element().createElement(NAMESPACE, "UserCredentials");
        Element Username = new Element().createElement(NAMESPACE, "userName");
        Username.addChild(Node.TEXT, logname);
        h.addChild(Node.ELEMENT, Username);
        Element wssePassword = new Element().createElement(NAMESPACE, "password");
        wssePassword.addChild(Node.TEXT, pin);
        h.addChild(Node.ELEMENT, wssePassword);

        envelope.headerOut = new Element[]{h};

        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        Log.i("loading stats", "" + envelope.bodyOut.toString());

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject result = (SoapObject)envelope.getResponse();

            for (int i = 0; i < result.getPropertyCount(); i++)
            {
                SoapObject s_deals = (SoapObject) result.getProperty(i);

                String datum = (s_deals.getProperty(0).toString());
                int cnt = Integer.parseInt(s_deals.getProperty(1).toString());

                gens.add(new Stats(datum, cnt));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gens;
    }

    private class AsyncCallWS extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            List<Stats> statistics = loadUserStatistics("D",30,params[0]);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

            for (int i = 0; i < statistics.size(); i++)
            {
                try {
                    seriesAfa.add(sdf.parse(statistics.get(i).getDatum()), statistics.get(i).getCnt());
                }
                catch(ParseException pe){

                }
            }

            //seriesAfa.add(sdf.parse(c.getString(c.getColumnIndex("datum"))), c.getDouble(c.getColumnIndex("cnt")));

            return "Task Completed.";
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

    }
}
