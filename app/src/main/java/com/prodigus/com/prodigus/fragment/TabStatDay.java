package com.prodigus.com.prodigus.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.ProgressBar;
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
import com.prodigus.com.prodigus.StatisticInsert;
import com.prodigus.com.prodigus.Stats;
import com.prodigus.com.prodigus.Users;
import com.prodigus.com.prodigus.activity.LoginActivity;
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

import static android.content.ContentValues.TAG;

public class TabStatDay extends Fragment {
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

    public TabStatDay() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TabStatDay newInstance(String selectedUser, String logUser) {
        TabStatDay fragment = new TabStatDay();
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

        myFragmentView = inflater.inflate(R.layout.fragment_tab_stat_day, container, false);
        mProgressView = myFragmentView.findViewById(R.id.statDay_progress);

        chartLyt = (LinearLayout) myFragmentView.findViewById(R.id.chart);
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

    private double getStatistics(TimeSeries timeSeries, int[] attribute, double maxValues, String selectedUser)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        double maxValue = maxValues;

        timeSeries.clear();

        for (int s = 0; s < stepCount; s++) {
            Cursor c = selectedUser == null ? db.getDayStatistics(attribute, s) : db.getDayStatistics(attribute, s, selectedUser);
            while (c.moveToNext()) {
                try {
                    timeSeries.add(sdf.parse(c.getString(c.getColumnIndex("datum"))), c.getDouble(c.getColumnIndex("cnt")));

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

        // Now we add our series
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        if(serieCheckedAfa) {
            maxValue = getStatistics(seriesAfa, new int[]{10,24}, maxValue, null);
            dataset.addSeries(seriesAfa);
            mRenderer.addSeriesRenderer(renderer);
        }
        if(serieCheckedTelk)
        {
            maxValue = getStatistics(seriesTelk, new int[]{14}, maxValue, null);
            dataset.addSeries(seriesTelk);
            mRenderer.addSeriesRenderer(renderer2);
        }
        if(serieCheckedTerm)
        {
            maxValue = getStatistics(seriesTerm, new int[]{15}, maxValue, null);
            dataset.addSeries(seriesTerm);
            mRenderer.addSeriesRenderer(renderer3);
        }
        if(serieCheckedFa)
        {
            maxValue = getStatistics(seriesFa, new int[]{16}, maxValue, null);
            dataset.addSeries(seriesFa);
            mRenderer.addSeriesRenderer(renderer4);
        }
        if(serieCheckedPk)
        {
            maxValue = getStatistics(seriesPk, new int[]{2}, maxValue, null);
            dataset.addSeries(seriesPk);
            mRenderer.addSeriesRenderer(renderer5);
        }
        if(serieCheckedKlient)
        {
            maxValue = getStatistics(seriesKlient, new int[]{1}, maxValue, null);
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

    private View createTempUserGraph() {
        TimeSeries seriesAfa = new TimeSeries("Denný graf Anketa finančná analýza");
        TimeSeries seriesTelk = new TimeSeries("Denný graf Telefonický hovor s kontaktom");
        TimeSeries seriesTerm = new TimeSeries("Denný graf Termín");
        TimeSeries seriesFa = new TimeSeries("Denný graf Finančná analýza");
        TimeSeries seriesPk = new TimeSeries("Denný graf Potenciálny klienti");
        TimeSeries seriesKlient = new TimeSeries("Denný graf Klienti");

        double maxValue = 0;

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

        // Now we add our series
        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        if(serieCheckedAfa) {
            maxValue = getStatistics(seriesAfa, new int[]{10,24}, maxValue, selectedUser);
            dataset.addSeries(seriesAfa);
            mRenderer.addSeriesRenderer(renderer);
        }
        if(serieCheckedTelk)
        {
            maxValue = getStatistics(seriesTelk, new int[]{14}, maxValue, selectedUser);
            dataset.addSeries(seriesTelk);
            mRenderer.addSeriesRenderer(renderer2);
        }
        if(serieCheckedTerm)
        {
            maxValue = getStatistics(seriesTerm, new int[]{15}, maxValue, selectedUser);
            dataset.addSeries(seriesTerm);
            mRenderer.addSeriesRenderer(renderer3);
        }
        if(serieCheckedFa)
        {
            maxValue = getStatistics(seriesFa, new int[]{16}, maxValue, selectedUser);
            dataset.addSeries(seriesFa);
            mRenderer.addSeriesRenderer(renderer4);
        }
        if(serieCheckedPk)
        {
            maxValue = getStatistics(seriesPk, new int[]{2}, maxValue, selectedUser);
            dataset.addSeries(seriesPk);
            mRenderer.addSeriesRenderer(renderer5);
        }
        if(serieCheckedKlient)
        {
            maxValue = getStatistics(seriesKlient, new int[]{1}, maxValue, selectedUser);
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

            case R.id.refresh:
                Toast.makeText(getContext(), "Refresh statistics for user: " + selectedUser, Toast.LENGTH_SHORT).show();
                showProgress(true);
                db.deleteStatsByUser(selectedUser, "D");
                AsyncCallStatsWS task = new AsyncCallStatsWS();
                task.execute(10);

                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setSelectedUser(String selectedUser){
        this.selectedUser = selectedUser;
        //Toast.makeText(getActivity(), "Test day: " + selectedUser, Toast.LENGTH_SHORT).show();
    }

    public void setLogUser(String logUser){
        this.logUser = logUser;
    }

    @Override
    public void onResume() {
        super.onResume();
        addViewChart(logUser.equals(selectedUser));
    }

    private class AsyncCallStatsWS extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            int progress = 0;
           // db = new MySQLiteHelper(getActivity());
            loadUserStatistics("D", 30, 0, selectedUser);

            return "Štatistiky načítané.";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            addViewChart(logUser.equals(selectedUser));
            chartView.repaint();
            showProgress(false);
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i(TAG, "onProgressUpdate");
        }

    }

    private final String NAMESPACE = "http://microsoft.com/webservices/";
    private final String URL = "http://evidencia.prodigus.sk/EvidenceService.asmx";
    private final String SOAP_ACTION_STATS = "http://microsoft.com/webservices/GetStatistics";
    private final String METHOD_NAME_STAT = "GetStatistics";

    public void loadUserStatistics(String type, int steps, int attribute, String user) {
        List<Stats> gens = null;

        SoapSerializationEnvelope envelope = setEnvelope();

        //Create request
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_STAT);

        PropertyInfo usernameProp = new PropertyInfo();
        usernameProp.setName("username");
        usernameProp.setValue(user);
        usernameProp.setType(String.class);
        request.addProperty(usernameProp);

        PropertyInfo typeProp = new PropertyInfo();
        typeProp.setName("userType");
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

        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        Log.i("loading stats", "" + envelope.bodyOut.toString());

        try {
            androidHttpTransport.call(SOAP_ACTION_STATS, envelope);
            SoapObject result = (SoapObject)envelope.getResponse();

            ArrayList<StatisticInsert> listOfStat = new ArrayList<StatisticInsert>();

            for (int i = 0; i < result.getPropertyCount(); i++)
            {
                SoapObject s_deals = (SoapObject) result.getProperty(i);

                String datum = (s_deals.getProperty(0).toString());
                int cnt = Integer.parseInt(s_deals.getProperty(1).toString());
                String typeOfStat = (s_deals.getProperty(2).toString());
                int att = Integer.parseInt(s_deals.getProperty(3).toString());

                listOfStat.add(new StatisticInsert(datum, cnt, user,  att, typeOfStat));
            }

            db.createStats(listOfStat);

            /*for (int i = 0; i < result.getPropertyCount(); i++)
            {
                SoapObject s_deals = (SoapObject) result.getProperty(i);

                String datum = (s_deals.getProperty(0).toString());
                int cnt = Integer.parseInt(s_deals.getProperty(1).toString());
                String typeOfStat = (s_deals.getProperty(2).toString());
                int att = Integer.parseInt(s_deals.getProperty(3).toString());
                long userId = db.createStats(datum, cnt, user, att, typeOfStat);

                //gens.add(new Stats(datum, cnt));
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SoapSerializationEnvelope setEnvelope()
    {
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
        envelope.dotNet = true;

        return envelope;
    }


    private View mProgressView;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            chartLyt.setVisibility(show ? View.GONE : View.VISIBLE);
            chartLyt.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    chartLyt.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            chartLyt.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
