package com.prodigus.com.prodigus.activity;

import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.Cursor;
import android.os.AsyncTask;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.prodigus.com.prodigus.MySQLiteHelper;
import com.prodigus.com.prodigus.R;
import com.prodigus.com.prodigus.StatisticInsert;
import com.prodigus.com.prodigus.Stats;

public class Synchronize extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String NAMESPACE = "http://microsoft.com/webservices/";
    private final String URL = "http://evidencia.prodigus.sk/EvidenceService.asmx";
    private final String SOAP_ACTION = "http://microsoft.com/webservices/GetPerson";
    private final String SOAP_ACTION_Attribute = "http://microsoft.com/webservices/GetAttributes";
    private final String SOAP_ACTION_USER = "http://microsoft.com/webservices/GetUsers";
    private final String SOAP_ACTION_STATS = "http://microsoft.com/webservices/GetStatistics";
    private final String SOAP_ACTION_USERCOUNT = "http://microsoft.com/webservices/GetUserCount";
    private final String METHOD_NAME = "GetPerson";
    private final String METHOD_NAME_Attribute = "GetAttributes";
    private final String METHOD_NAME_USER = "GetUsers";
    private final String METHOD_NAME_STAT = "GetStatistics";
    private final String METHOD_NAME_USERCOUNT = "GetUserCount";

    private String TAG = "PGGURU";
    private StringReader xmlReader;

    private ProgressBar progressBar;
    private ProgressBar progressBarStat;
    private TextView tvResult;
    private TextView tvResultStat;

    private String typeOfSynchro = "P";
    private String logname = "";
    private String pin = "";

    Integer progressBarCount = 1;

    MySQLiteHelper db;
    Button b;
    TextView tv;
    EditText et;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronize);
        Toolbar toolbar = (Toolbar) findViewById(R.id.synchroToolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.layout_synchronize);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_synchro);
        navigationView.setNavigationItemSelectedListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(17);

        progressBarStat = (ProgressBar) findViewById(R.id.progressBarStats);
        progressBarStat.setMax(50);

        tvResult = (TextView) findViewById(R.id.tvResult);
        tvResultStat = (TextView) findViewById(R.id.tvResultStat);

        db = new MySQLiteHelper(getApplicationContext());

        final Button btnAkt = (Button) findViewById(R.id.btnAkt);
        btnAkt.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                progressBarCount = 1;
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);

                AsyncCallWS task = new AsyncCallWS();
                task.execute(10);

            }
        });

        final Button btnAktStat = (Button) findViewById(R.id.btnStats);
        btnAktStat.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                progressBarCount = 1;
                progressBarStat.setVisibility(View.VISIBLE);
                progressBarStat.setProgress(0);

                AsyncCallStatsWS task = new AsyncCallStatsWS();
                task.execute("");

                /*db.deleteStats();

                Cursor cursor = db.getUsers();
                double step = Math.floor(50/cursor.getCount());

                cursor.moveToFirst();
                do{
                    String nick = cursor.getString(cursor.getColumnIndexOrThrow("usr_nick"));

                    AsyncCallStatsWS task = new AsyncCallStatsWS();
                    task.execute(nick);
                }
                while(cursor.moveToNext());

                cursor.close();*/

            }
        });
/*
        Cursor c = db.getAuth();
        if(c.getCount() == 0)
        {
            btnAkt.setEnabled(false);
        }
        c.close();*/

        /*spusti obrazovku na pridanie kontaktu*/
        //Button btnAdd = (Button) findViewById(R.id.btnAdd);
        //btnAdd.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xffffff00));

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.fullSynchro:
                if (checked)
                    typeOfSynchro = "F";
                    break;
            case R.id.partialSynchro:
                if (checked)
                    typeOfSynchro = "P";
                    break;
        }
    }

    //region GENERAL

    public SoapSerializationEnvelope setEnvelope()
    {
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

    //endregion

    // region GET METHOD
    public String getLastSyncDate()
    {
        String lastSyncDate = null;
        SimpleDateFormat dateFormatSync = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            lastSyncDate = dateFormatSync.format(dateFormatSync.parse(db.getLastSync()));
        }
        catch(ParseException ex) {}

        RadioGroup radioButtonSync = (RadioGroup) findViewById(R.id.radioButtonSync);
        RadioButton radioButton;

        int selectedId = radioButtonSync.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);

        Calendar calLocale = new GregorianCalendar(Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        switch (typeOfSynchro)
        {
            case "F":
                calendar.add(Calendar.YEAR , -5);
                return dateFormatSync.format(calendar.getTime());
            case "P":
                return lastSyncDate;
            default:
                calendar.add(Calendar.YEAR , -5);
                return dateFormatSync.format(calendar.getTime());
        }
    }

    public int getUsersCount() {
        String logname = "";
        String pin = "";

        String lastSyncDate = getLastSyncDate();

        Cursor c = db.getAuth();

        if(Integer.valueOf(c.getCount()) > 0) {

            if (c.moveToFirst()) {
                logname = c.getString(c.getColumnIndex("logname"));
                pin = c.getString(c.getColumnIndex("pin"));
            }
            c.close();
        }
        else {
            return 0;
        }

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_USERCOUNT);

        PropertyInfo personInfo = new PropertyInfo();
        personInfo.setName("lastSync");
        personInfo.setValue(lastSyncDate);
        personInfo.setType(String.class);

        request.addProperty(personInfo);

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
        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        new MarshalDate().register(envelope);
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            androidHttpTransport.call(SOAP_ACTION_USERCOUNT, envelope);
            SoapPrimitive results = (SoapPrimitive)envelope.getResponse();
            return Integer.parseInt(results.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    //endregion

    //region LOAD METHODS

    public void loadContacts(int step) {

        SoapSerializationEnvelope envelope = setEnvelope();

        //Create request
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        PropertyInfo celsiusPI = new PropertyInfo();
        celsiusPI.setName("username");
        celsiusPI.setValue(logname);
        celsiusPI.setType(String.class);
        request.addProperty(celsiusPI);

        PropertyInfo stepProp = new PropertyInfo();
        stepProp.setName("steps");
        stepProp.setValue(step);
        stepProp.setType(int.class);
        request.addProperty(stepProp);

        PropertyInfo lastSyncProp = new PropertyInfo();
        lastSyncProp.setName("lastSync");
        lastSyncProp.setValue(getLastSyncDate());
        lastSyncProp.setType(String.class);
        request.addProperty(lastSyncProp);

        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        Log.i("bodyout", "" + envelope.bodyOut.toString());

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);

            SoapObject result = (SoapObject)envelope.getResponse();
            SimpleDateFormat sdateFormat = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            for (int i = 0; i < result.getPropertyCount(); i++)
            {
                SoapObject s_deals = (SoapObject) result.getProperty(i);
                Date now = new Date();
                now.getDay();

                String titul = s_deals.getProperty(0).toString().replace("anyType{}", "");
                String meno = s_deals.getProperty(1).toString().replace("anyType{}", "");
                String priezvisko = s_deals.getProperty(2).toString().replace("anyType{}", "");
                String datum = "";
                try {
                    datum = sdateFormat.format(dateFormat.parse(s_deals.getProperty(3).toString()));
                }
                catch(Exception ex){/*.i("Err", ex.getMessage());*/}

                //String datum = dateFormat.format(s_deals.getProperty(3).toString().replace("anyType{}", ""));
                String mesto = s_deals.getProperty(4).toString().replace("anyType{}", "");
                String ulica = s_deals.getProperty(5).toString().replace("anyType{}", ""); //getProperty(5).toString();
                String pc = s_deals.getProperty(6).toString().replace("anyType{}", "");
                String email = s_deals.getProperty(7).toString().replace("anyType{}", "");
                String telefon = s_deals.getProperty(8).toString().replace("anyType{}", "");
                int pohlavie = Integer.parseInt(s_deals.getProperty(9).toString().replace("anyType{}", ""));
                String pohlavieText = pohlavie == 5 ? "M" : "F";
                String attribute = s_deals.getProperty(10).toString().replace("anyType{}", "");
                int clientId = Integer.parseInt(s_deals.getProperty(11).toString().replace("anyType{}", ""));

                int currClientId = db.getClientId(clientId);

                if(currClientId == 0) {
                    db.createToDo(titul, meno, priezvisko, datum, mesto, ulica, pc, email, telefon, pohlavieText, attribute, clientId, 0);
                }
                else {
                    db.updateToDo(currClientId, titul, meno, priezvisko, datum, mesto, ulica, pc, email, telefon, pohlavieText , attribute);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadContactsAttHistory() {

        SoapSerializationEnvelope envelope = setEnvelope();

        //Create request
        SoapObject request = new SoapObject(NAMESPACE, "GetAttHistory");

        PropertyInfo lastSyncProp = new PropertyInfo();
        lastSyncProp.setName("lastSync");
        lastSyncProp.setValue(getLastSyncDate());
        lastSyncProp.setType(String.class);
        request.addProperty(lastSyncProp);

        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        Log.i("loading history", "" + envelope.bodyOut.toString());

        try {
            androidHttpTransport.call("http://microsoft.com/webservices/GetAttHistory", envelope);
            SoapObject result = (SoapObject)envelope.getResponse();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            for (int i = 0; i < result.getPropertyCount(); i++)
            {
                SoapObject s_deals = (SoapObject) result.getProperty(i);

                int con_id = Integer.parseInt(s_deals.getProperty(1).toString());
                int att_id = Integer.parseInt(s_deals.getProperty(0).toString());
                Date creation = dateFormat.parse(s_deals.getProperty(2).toString());
                int historyId = Integer.parseInt(s_deals.getProperty(3).toString());

                int currClientId = db.getClientHistoryId(historyId);

                if(currClientId == 0) {
                    db.createContactHistory(con_id, att_id, creation, 0, historyId);
                }
                else {
                    db.updateStatusContactHistory(currClientId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadNotes() {

        SoapSerializationEnvelope envelope = setEnvelope();

        //Create request
        SoapObject request = new SoapObject(NAMESPACE, "GetNotes");

        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        Log.i("loading notes", "" + envelope.bodyOut.toString());

        try {
            androidHttpTransport.call("http://microsoft.com/webservices/GetNotes", envelope);

            //Get the response
            //SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            SoapObject result = (SoapObject)envelope.getResponse();

            //SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            Log.i(TAG, String.valueOf(result.getPropertyCount()));

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

            SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd.MM.yyyy");

            for (int i = 0; i < result.getPropertyCount(); i++)
            {
                SoapObject s_deals = (SoapObject) result.getProperty(i);

                int con_id = Integer.parseInt(s_deals.getProperty(1).toString());
                int att_id = Integer.parseInt(s_deals.getProperty(0).toString());
                String noteText = s_deals.getProperty(2).toString();
                Date creation = dateFormat.parse(s_deals.getProperty(3).toString());
                int serverId = Integer.parseInt(s_deals.getProperty(4).toString());

                //Date creation = dateFormat2.parse(s_deals.getProperty(3).toString());

                long todo1_id = db.createSyncNote(noteText, con_id, att_id, creation, serverId,0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadUsers() {

        SoapSerializationEnvelope envelope = setEnvelope();

        //Create request
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_USER);

        PropertyInfo celsiusPI = new PropertyInfo();
        //Set Name
        celsiusPI.setName("userName");
        //Set Value
        celsiusPI.setValue(logname);
        //Set dataType
        //celsiusPI.setType(double.class);
        celsiusPI.setType(String.class);
        //Add the property to request object
        request.addProperty(celsiusPI);

        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        Log.i("loading users", "" + envelope.bodyOut.toString());

        try {
            androidHttpTransport.call(SOAP_ACTION_USER, envelope);
            SoapObject result = (SoapObject)envelope.getResponse();

            for (int i = 0; i < result.getPropertyCount(); i++)
            {
                SoapObject s_deals = (SoapObject) result.getProperty(i);

                String nick = (s_deals.getProperty(0).toString());
                String name = (s_deals.getProperty(1).toString());

                //gens.add(new Users(nick, name));
                long userId = db.createUsers(nick, name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

                //long userId = db.createStats(datum, cnt, user, att, typeOfStat);

                //gens.add(new Stats(datum, cnt));
            }

            db.createStats(listOfStat);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //return gens;
    }

    public void loadAttributes() {

        SoapSerializationEnvelope envelope = setEnvelope();

        //delete all data in db
        db.deleteAllAttributes();

        //Create request
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_Attribute);
        //Property which holds input parameters
        PropertyInfo celsiusPI = new PropertyInfo();
        celsiusPI.setName("username");
        celsiusPI.setValue(logname);
        celsiusPI.setType(String.class);

        request.addProperty(celsiusPI);

        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        Log.i("bodyoutAttribute", "" + envelope.bodyOut.toString());
        try {
            androidHttpTransport.call(SOAP_ACTION_Attribute, envelope);

            //Get the response
            //SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            SoapObject result = (SoapObject)envelope.getResponse();

            Log.i("bodyoutAttributeCount", "" + result.getPropertyCount());
            for (int i = 0; i < result.getPropertyCount(); i++)
            {
                SoapObject s_deals = (SoapObject) result.getProperty(i);

                int id = Integer.parseInt(s_deals.getProperty(0).toString());
                String att_sc = s_deals.getProperty(1).toString();
                String att_full = s_deals.getProperty(2).toString();
                int att_con_order = Integer.parseInt(s_deals.getProperty(3).toString());
                String att_type = s_deals.getProperty(4).toString();

                long todo1_id = db.createAttribute(id, att_sc, att_full, att_con_order, att_type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region SEND METHODS

    public void sendAllContacts()
    {
        int clientId = 0;
        String title = "";
        String name = "";
        String surname = "";
        String city = "";
        String street = "";
        String number = "";
        String gender = "";
        String email = "";
        String phone = "";
        String bornDate = "";
        int attribute = 0;
        int clientInternalId = 0;

        Date brndate = null;

        String pattern = "MM/dd/yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);

        String myTimestamp="2014/02/17 20:49";
        SimpleDateFormat form = new SimpleDateFormat("MM/dd/yyyy");

        Cursor cursor = db.selectAllContacts();
        while (cursor.moveToNext()) {
            clientId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            surname = cursor.getString(cursor.getColumnIndexOrThrow("surname"));
            city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
            street = cursor.getString(cursor.getColumnIndexOrThrow("street"));
            number = cursor.getString(cursor.getColumnIndexOrThrow("number"));
            gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
            email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            attribute = cursor.getInt(cursor.getColumnIndexOrThrow("attribute"));
            clientInternalId = cursor.getInt(cursor.getColumnIndexOrThrow("clientId"));
            bornDate = cursor.getString(cursor.getColumnIndexOrThrow("borndate"));

            //borndate = cursor.getString(cursor.getColumnIndexOrThrow("borndate")));
            //borndate = "";
            /*try {
            borndate = form.parse(myTimestamp).toString();
            }
            catch(ParseException pe) {
                System.out.println("ERROR: Cannot parse");
            }*/

            sendContacts(clientId, clientInternalId, surname, name, city, street, number, email, phone, "M", bornDate, title, "", attribute);
        }
        cursor.close();
    }

    public void sendContacts(int id, int clientId, String surname, String name, String city, String street, String pc, String email, String phone, String sex, String bornDate, String degree_bef, String degree_aft, int attribute) {

        SoapSerializationEnvelope envelope = setEnvelope();

        //Create request
        SoapObject request = new SoapObject(NAMESPACE, "InsertUpdatePerson");

        PropertyInfo personInfo = new PropertyInfo();
        personInfo.setName("clientId");
        personInfo.setValue(clientId);
        personInfo.setType(int.class);

        PropertyInfo personInfo2 = new PropertyInfo();
        personInfo2.setName("surname");
        personInfo2.setValue(surname);
        personInfo2.setType(String.class);

        PropertyInfo personInfo3 = new PropertyInfo();
        personInfo3.setName("name");
        personInfo3.setValue(name);
        personInfo3.setType(String.class);

        PropertyInfo personInfo4 = new PropertyInfo();
        personInfo4.setName("city");
        personInfo4.setValue(city);
        personInfo4.setType(String.class);

        PropertyInfo personInfo5 = new PropertyInfo();
        personInfo5.setName("street");
        personInfo5.setValue(street);
        personInfo5.setType(String.class);

        PropertyInfo personInfo6 = new PropertyInfo();
        personInfo6.setName("pc");
        personInfo6.setValue(pc);
        personInfo6.setType(String.class);

        PropertyInfo personInfo8 = new PropertyInfo();
        personInfo8.setName("email");
        personInfo8.setValue(email);
        personInfo8.setType(String.class);

        PropertyInfo personInfo9 = new PropertyInfo();
        personInfo9.setName("phone");
        personInfo9.setValue(phone);
        personInfo9.setType(String.class);

        PropertyInfo personInfo10 = new PropertyInfo();
        personInfo10.setName("sex");
        personInfo10.setValue(sex);
        personInfo10.setType(String.class);

        PropertyInfo personInfo11 = new PropertyInfo();
        personInfo11.setName("bornDate");
        personInfo11.setValue(bornDate);
        personInfo11.setType(String.class);

        PropertyInfo personInfo12 = new PropertyInfo();
        personInfo12.setName("degree_bef");
        personInfo12.setValue(degree_bef);
        personInfo12.setType(String.class);

        PropertyInfo personInfo13 = new PropertyInfo();
        personInfo13.setName("degree_aft");
        personInfo13.setValue(degree_aft);
        personInfo13.setType(String.class);

        PropertyInfo personInfo14 = new PropertyInfo();
        personInfo14.setName("attribute");
        personInfo14.setValue(attribute);
        personInfo14.setType(String.class);

        //Add the property to request object
        request.addProperty(personInfo);
        request.addProperty(personInfo2);
        request.addProperty(personInfo3);
        request.addProperty(personInfo4);
        request.addProperty(personInfo5);
        request.addProperty(personInfo6);
        request.addProperty(personInfo8);
        request.addProperty(personInfo9);
        request.addProperty(personInfo10);
        request.addProperty(personInfo11);
        request.addProperty(personInfo12);
        request.addProperty(personInfo13);
        request.addProperty(personInfo14);

        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        Log.i("--Insert", "" + envelope.bodyOut.toString());
        try {
            androidHttpTransport.call("http://microsoft.com/webservices/InsertUpdatePerson", envelope);
            //SoapObject result = (SoapObject)envelope.getResponse();
            // Object  response=  (SoapObject)envelope.getResponse();
            SoapPrimitive results = (SoapPrimitive)envelope.getResponse();

            Integer returnValue = Integer.parseInt(results.toString());

            if(returnValue != null || returnValue > 0)
            {
                db.updateContactClientId(id, returnValue);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAllContactAttHistory()
    {
        int id = 0;
        int clientId = 0;
        int attribute = 0;
        Date creation = null;

        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern);

        SimpleDateFormat form = new SimpleDateFormat("MM/dd/yyyy");

        Cursor cursor = db.getAllContactAttHistory();
        while (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            clientId = cursor.getInt(cursor.getColumnIndexOrThrow("con_id"));
            attribute = cursor.getInt(cursor.getColumnIndexOrThrow("con_state"));
            String s = cursor.getString(cursor.getColumnIndexOrThrow("change_date"));

            try {
                creation = format.parse(s);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //creation = new Date(cursor.getLong(cursor.getColumnIndexOrThrow("change_date")));

            sendContactAttHistory(id, clientId, attribute, creation);
        }
        cursor.close();
    }

    public void sendAllNotes()
    {
        // notetext, datec, person, attribute
        String noteText;
        int clientId = 0;
        int attribute = 0;
        Date creation = null;

        //String pattern = "dd.MM.yyyy";
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");

        Cursor cursor = db.getSyncNotes();
        while (cursor.moveToNext()) {
            noteText = cursor.getString(cursor.getColumnIndexOrThrow("notetext"));
            clientId = cursor.getInt(cursor.getColumnIndexOrThrow("person"));
            attribute = cursor.getInt(cursor.getColumnIndexOrThrow("attribute"));

            String s = cursor.getString(cursor.getColumnIndexOrThrow("datec"));
            int cin_id = cursor.getInt(cursor.getColumnIndexOrThrow("cin_id"));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));

            try {
                creation = format.parse(s);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //creation = new Date(cursor.getLong(cursor.getColumnIndexOrThrow("datec")));

            sendNotes(noteText, clientId, attribute, creation, cin_id, status);
        }
        cursor.close();
    }

    public void sendContactAttHistory(int id, int clientId, int attribute, Date creation) {

        SoapSerializationEnvelope envelope = setEnvelope();

        SoapObject request = new SoapObject(NAMESPACE, "InsertAttHistory");

        PropertyInfo personInfo = new PropertyInfo();
        personInfo.setName("clientId");
        personInfo.setValue(clientId);
        personInfo.setType(int.class);

        PropertyInfo personInfo2 = new PropertyInfo();
        personInfo2.setName("attId");
        personInfo2.setValue(attribute);
        personInfo2.setType(String.class);

        PropertyInfo personInfo3 = new PropertyInfo();
        personInfo3.setName("creation");
        personInfo3.setValue(creation);
        personInfo3.setType(Date.class);

        request.addProperty(personInfo);
        request.addProperty(personInfo2);
        request.addProperty(personInfo3);

        //Set output SOAP object
        envelope.setOutputSoapObject(request);

        new MarshalDate().register(envelope);

        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        Log.i("--Odosiela historiu", "" + envelope.bodyOut.toString());
        try {
            androidHttpTransport.call("http://microsoft.com/webservices/InsertAttHistory", envelope);
            SoapPrimitive results = (SoapPrimitive)envelope.getResponse();

            Integer returnValue = Integer.parseInt(results.toString());

            if(returnValue != null && returnValue > 0)
            {
                //aplikovane v send contacts
                //db.updateContactClientId(id, returnValue);
                db.updateContactHistoryInternalId(id, returnValue);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotes(String noteText, int clientId, int attribute, Date creation, int cin_id, int status) {

        SoapSerializationEnvelope envelope = setEnvelope();

        SoapObject request = new SoapObject(NAMESPACE, "InsertNote");

        PropertyInfo personInfo = new PropertyInfo();
        personInfo.setName("clientId");
        personInfo.setValue(clientId);
        personInfo.setType(int.class);

        PropertyInfo personInfo2 = new PropertyInfo();
        personInfo2.setName("attId");
        personInfo2.setValue(attribute);
        personInfo2.setType(String.class);

        PropertyInfo personInfo3 = new PropertyInfo();
        personInfo3.setName("creation");
        personInfo3.setValue(creation);
        personInfo3.setType(Date.class);

        PropertyInfo personInfo4 = new PropertyInfo();
        personInfo4.setName("notetext");
        personInfo4.setValue(noteText);
        personInfo4.setType(String.class);

        PropertyInfo personInfo5 = new PropertyInfo();
        personInfo5.setName("cinId");
        personInfo5.setValue(cin_id);
        personInfo5.setType(int.class);

        PropertyInfo personInfo6 = new PropertyInfo();
        personInfo6.setName("status");
        personInfo6.setValue(status);
        personInfo6.setType(int.class);

        request.addProperty(personInfo);
        request.addProperty(personInfo2);
        request.addProperty(personInfo3);
        request.addProperty(personInfo4);
        request.addProperty(personInfo5);
        request.addProperty(personInfo6);

        //Set output SOAP object
        envelope.setOutputSoapObject(request);
        new MarshalDate().register(envelope);
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        Log.i("--Odosiela poznamky", "" + envelope.bodyOut.toString());
        try {
            androidHttpTransport.call("http://microsoft.com/webservices/InsertNote", envelope);
            SoapPrimitive results = (SoapPrimitive)envelope.getResponse();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region ASYNC METHODS
    private class AsyncCallWS extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            publishProgress(1);
            sendAllContacts();
            publishProgress(5);
            sendAllContactAttHistory();
            publishProgress(7);
            sendAllNotes();
            publishProgress(9);
            loadAttributes();
            publishProgress(10);

            if(typeOfSynchro.equals("F"))
            {
                db.deleteAllContacts();
                db.deleteAllContactsHistory();
            }

            db.deleteAll();

            int allUsersCount = getUsersCount();
            int userCount = Math.round(allUsersCount / 100);

            for(int k=0; k <= userCount; k++)
            {
                loadContacts(k);
                int loadedUser = k*100 > allUsersCount ? allUsersCount : k*100;
                publishProgress(12,loadedUser,allUsersCount);
            }
            if(allUsersCount < 100)
            {
                loadContacts(0);
            }

            db.setLastSync();

            publishProgress(12);
            loadContactsAttHistory();
            publishProgress(14);
            loadNotes();
            publishProgress(16);
            loadUsers();
            publishProgress(17);

            return "Synchronizácia ukončená.";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            tvResult.setText(result);
            //progressBar.setVisibility(View.GONE);
            //Button btnAkt = (Button) findViewById(R.id.btnAkt);
            //btnAkt.setText("AktualizĂˇcia Ăşdajov");
            //btnAkt.setBackgroundColor(Color.LTGRAY);
            //tv.setText(fahren + " F");
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
            //Button btnAkt = (Button) findViewById(R.id.btnAkt);
            //btnAkt.setText("Odosielam dĂˇta...");
            //btnAkt.setBackgroundColor(Color.RED);
            //tv.setText("Calculating...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i(TAG, "onProgressUpdate");
            progressBar.setProgress(values[0]);
            if(values.length > 1)
            {
                tvResult.setText("Načítaných " + values[1] + " z " + values[2] + " kontaktov");
            }
        }

    }

    private class AsyncCallStatsWS extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            int progress = 0;
            //String nameOfUser = params[0];

            db.deleteStats();

            Cursor cursor = db.getUsers();
            double step = Math.floor(50/cursor.getCount());

            cursor.moveToFirst();
            do {
                String nick = cursor.getString(cursor.getColumnIndexOrThrow("usr_nick"));
                loadUserStatistics("X", 30, 0, nick);

                progress += step;
                publishProgress(progress);
            }
            while(cursor.moveToNext());

            cursor.close();
            publishProgress(50);

            return "Štatistiky načítané.";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute");
            tvResultStat.setText(result);
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i(TAG, "onProgressUpdate");
            progressBarStat.setProgress(values[0]);
        }

    }

    //endregion

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ( db!= null) {
            db.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.layout_synchronize);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_contacts) {
            startActivity(new Intent(Synchronize.this, MainActivity.class));
        } else if (id == R.id.nav_pzp) {

        } else if (id == R.id.nav_statistics) {
            startActivity(new Intent(Synchronize.this, TabStatistics.class));
        } else if (id == R.id.nav_sync) {
            startActivity(new Intent(Synchronize.this, Synchronize.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("userdetails", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.isLogged), 0);
            editor.commit();
            startActivity(new Intent(Synchronize.this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.layout_synchronize);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
