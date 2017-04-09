package com.prodigus.com.prodigus.activity;

import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;

import org.json.JSONObject;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.prodigus.com.prodigus.MySQLiteHelper;
import com.prodigus.com.prodigus.R;
import com.prodigus.com.prodigus.SecondActivity;

public class Synchronize extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final String NAMESPACE = "http://microsoft.com/webservices/";
    private final String URL = "http://evidencia.prodigus.sk/EvidenceService.asmx";
    private final String SOAP_ACTION = "http://microsoft.com/webservices/GetPerson";
    private final String SOAP_ACTION_Attribute = "http://microsoft.com/webservices/GetAttributes";
    private final String METHOD_NAME = "GetPerson";
    private final String METHOD_NAME_Attribute = "GetAttributes";

    private String TAG = "PGGURU";
    private StringReader xmlReader;

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

        db = new MySQLiteHelper(getApplicationContext());

        final Button btnAkt = (Button) findViewById(R.id.btnAkt);
        btnAkt.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
              /*AsyncCallWS_send task_new = new AsyncCallWS_send();
              task_new.execute();*/
                Log.i("WS","1");
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
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

    public void loadContacts() {
        //vyber ulozene nastavenie pre autentifikaciu
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
            return;
            //Toast.makeText(getApplicationContext(), "Nie je nastavenĂˇ autentifikĂˇcia", Toast.LENGTH_LONG).show();
        }

        Log.i("LIVE","stale zijem");

        //delete all data in db
        db.deleteAll();

        //Create request
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        //Property which holds input parameters
        PropertyInfo celsiusPI = new PropertyInfo();
        //Set Name
        celsiusPI.setName("username");
        //Set Value
        celsiusPI.setValue(logname);
        //Set dataType
        //celsiusPI.setType(double.class);
        celsiusPI.setType(String.class);
        //Add the property to request object
        request.addProperty(celsiusPI);
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

        Log.i("bodyout", "" + envelope.bodyOut.toString());

        try {
            androidHttpTransport.call(SOAP_ACTION, envelope);

            //Get the response
            //SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
            SoapObject result = (SoapObject)envelope.getResponse();

            //SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            Log.i(TAG, String.valueOf(result.getPropertyCount()));

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

            for (int i = 0; i < result.getPropertyCount(); i++)
            {
                    SoapObject s_deals = (SoapObject) result.getProperty(i);
                    Date now = new Date();
                    now.getDay();
                    String titul = "";
                    Log.i(TAG, s_deals.getProperty(0).toString());
                    if(s_deals.getProperty(0).toString() != "anyType{}") {
                        titul = s_deals.getProperty(0).toString();
                    }
                    String meno = s_deals.getProperty(1).toString();
                    String priezvisko = s_deals.getProperty(2).toString();
                    String mesto = s_deals.getProperty(4).toString();
                    String ulica = s_deals.getProperty(5).toString();
                    String pc = s_deals.getProperty(6).toString();
                    String email = s_deals.getProperty(7).toString();
                    String telefon = s_deals.getProperty(8).toString();
                    int pohlavie = Integer.parseInt(s_deals.getProperty(9).toString());
                    String attribute = s_deals.getProperty(10).toString();
                    int clientId = Integer.parseInt(s_deals.getProperty(11).toString());

                    long todo1_id = db.createToDo(titul,meno,priezvisko,now, mesto, ulica, pc, email, telefon, "", attribute, clientId);
                    Log.i(TAG, Long.toString(todo1_id));

            }

            //Toast.makeText(getApplicationContext(), "Údaje boli aktualizované", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

            //borndate = cursor.getString(cursor.getColumnIndexOrThrow("borndate")));
            //borndate = "";
            /*try {
            borndate = form.parse(myTimestamp).toString();
            }
            catch(ParseException pe) {
                System.out.println("ERROR: Cannot parse");
            }*/

            sendContacts(clientId, clientInternalId, surname, name, city, street, number, email, phone, "M", null, title, "", attribute);
        }
        cursor.close();
    }

    public void loadAttributes() {
        //vyber ulozene nastavenie pre autentifikaciu
        String logname = "";
        String pin = "";

        Cursor c = db.getAuth();

        Log.i(TAG, "attributes");

        if(Integer.valueOf(c.getCount()) > 0) {

            if (c.moveToFirst()) {
                logname = c.getString(c.getColumnIndex("logname"));
                pin = c.getString(c.getColumnIndex("pin"));
            }
            c.close();
        }
        else {
            return;
            //Toast.makeText(getApplicationContext(), "Nie je nastavenĂˇ autentifikĂˇcia", Toast.LENGTH_LONG).show();
        }

        //delete all data in db
        db.deleteAllAttributes();

        //Create request
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_Attribute);
        //Property which holds input parameters
        PropertyInfo celsiusPI = new PropertyInfo();
        //Set Name
        celsiusPI.setName("username");
        //Set Value
        celsiusPI.setValue(logname);
        //Set dataType
        //celsiusPI.setType(double.class);
        celsiusPI.setType(String.class);
        //Add the property to request object
        request.addProperty(celsiusPI);

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

    /***
     public void sendAll()
     {
     String druhVozidla = "";
     String ucelVozidla = "";
     String znacka = "";
     String model = "";
     String zdvihObjem = "";
     String vykon = "";
     String hmotnost = "";
     String rokVyroby = "";
     String druhPaliva = "";
     String typDrzitela = "";
     String psc = "";
     String bydlisko = "";
     String bdate = "";
     String skodovost = "";
     String poistovna = "";
     String opravnenie = "";
     String meno = "";
     String priezvisko = "";
     String email = "";
     String telefon = "";
     Integer PZPID;

     Date brndate = null;

     String pattern = "MM/dd/yyyy";
     SimpleDateFormat format = new SimpleDateFormat(pattern);

     String myTimestamp="2014/02/17 20:49";
     SimpleDateFormat form = new SimpleDateFormat("MM/dd/yyyy");

     Cursor cursor = db.getAllPZP();
     while (cursor.moveToNext()) {
     PZPID = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
     druhVozidla = cursor.getString(cursor.getColumnIndexOrThrow("druhVozidla"));
     ucelVozidla = cursor.getString(cursor.getColumnIndexOrThrow("ucelVozidla"));
     znacka = cursor.getString(cursor.getColumnIndexOrThrow("znacka"));
     model = cursor.getString(cursor.getColumnIndexOrThrow("model"));
     zdvihObjem = cursor.getString(cursor.getColumnIndexOrThrow("zdvihObjem"));
     vykon = cursor.getString(cursor.getColumnIndexOrThrow("vykon"));
     hmotnost = cursor.getString(cursor.getColumnIndexOrThrow("hmotnost"));
     rokVyroby = cursor.getString(cursor.getColumnIndexOrThrow("rokVyroby"));
     druhPaliva = cursor.getString(cursor.getColumnIndexOrThrow("druhPaliva"));
     typDrzitela = cursor.getString(cursor.getColumnIndexOrThrow("typDrzitela"));
     psc = cursor.getString(cursor.getColumnIndexOrThrow("psc"));
     bydlisko = cursor.getString(cursor.getColumnIndexOrThrow("bydlisko"));
     bdate = cursor.getString(cursor.getColumnIndexOrThrow("bdate"));
     skodovost = cursor.getString(cursor.getColumnIndexOrThrow("skodovost"));
     poistovna = cursor.getString(cursor.getColumnIndexOrThrow("poistovna"));
     opravnenie = cursor.getString(cursor.getColumnIndexOrThrow("opravnenie"));
     meno = cursor.getString(cursor.getColumnIndexOrThrow("meno"));
     priezvisko = cursor.getString(cursor.getColumnIndexOrThrow("priezvisko"));
     email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
     telefon = cursor.getString(cursor.getColumnIndexOrThrow("telefon"));


     //borndate = cursor.getString(cursor.getColumnIndexOrThrow("borndate")));
     //borndate = "";
     /*try {
     borndate = form.parse(myTimestamp).toString();
     }
     catch(ParseException pe) {
     System.out.println("ERROR: Cannot parse");
     }*/

            /*sendData(PZPID, druhVozidla,ucelVozidla,znacka,model,zdvihObjem,vykon,hmotnost,rokVyroby,druhPaliva,typDrzitela,psc,bydlisko,bdate,skodovost,poistovna,opravnenie, meno, priezvisko, email, telefon);
        }
        cursor.close();
    }*/

    public void sendData( Integer PZPID, String druhVozidla,String ucelVozidla, String znacka, String model, String zdvihObjem, String vykon,
                          String hmotnost,String rokVyroby, String druhPaliva, String typDrzitela, String psc, String bydlisko, String bdate,
                          String skodovost, String poistovna, String opravnenie, String meno, String priezvisko, String email, String telefon) {

        //vyber ulozeneho nastavenia pre autentifikaciu
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
            return;
        }

        //delete all data in db
        //db.deleteAll();

        //Create request
        SoapObject request = new SoapObject(NAMESPACE, "InsertPZP");

        PropertyInfo personInfo = new PropertyInfo();
        personInfo.setName("druhVozidla");
        personInfo.setValue(druhVozidla);
        personInfo.setType(String.class);

        PropertyInfo personInfo2 = new PropertyInfo();
        personInfo2.setName("ucelVozidla");
        personInfo2.setValue(ucelVozidla);
        personInfo2.setType(String.class);

        PropertyInfo personInfo3 = new PropertyInfo();
        personInfo3.setName("znacka");
        personInfo3.setValue(znacka);
        personInfo3.setType(String.class);

        PropertyInfo personInfo4 = new PropertyInfo();
        personInfo4.setName("model");
        personInfo4.setValue(model);
        personInfo4.setType(String.class);

        PropertyInfo personInfo5 = new PropertyInfo();
        personInfo5.setName("zdvihObjem");
        personInfo5.setValue(zdvihObjem);
        personInfo5.setType(String.class);

        PropertyInfo personInfo6 = new PropertyInfo();
        personInfo6.setName("email");
        personInfo6.setValue(email);
        personInfo6.setType(String.class);

        PropertyInfo personInfo7 = new PropertyInfo();
        personInfo7.setName("vykon");
        personInfo7.setValue(vykon);
        personInfo7.setType(String.class);

        PropertyInfo personInfo8 = new PropertyInfo();
        personInfo8.setName("hmotnost");
        personInfo8.setValue(hmotnost);
        personInfo8.setType(String.class);

        PropertyInfo personInfo9 = new PropertyInfo();
        personInfo9.setName("rokVyroby");
        personInfo9.setValue(rokVyroby);
        personInfo9.setType(String.class);

        PropertyInfo personInfo10 = new PropertyInfo();
        personInfo10.setName("druhPaliva");
        personInfo10.setValue(druhPaliva);
        personInfo10.setType(String.class);

        PropertyInfo personInfo11 = new PropertyInfo();
        personInfo11.setName("typDrzitela");
        personInfo11.setValue(typDrzitela);
        personInfo11.setType(String.class);

        PropertyInfo personInfo12 = new PropertyInfo();
        personInfo12.setName("psc");
        personInfo12.setValue(psc);
        personInfo12.setType(String.class);

        PropertyInfo personInfo13 = new PropertyInfo();
        personInfo13.setName("bydlisko");
        personInfo13.setValue(bydlisko);
        personInfo13.setType(String.class);

        PropertyInfo personInfo14 = new PropertyInfo();
        personInfo14.setName("bdate");
        personInfo14.setValue(bdate);
        personInfo14.setType(String.class);

        PropertyInfo personInfo15 = new PropertyInfo();
        personInfo15.setName("skodovost");
        personInfo15.setValue(skodovost);
        personInfo15.setType(String.class);

        PropertyInfo personInfo16 = new PropertyInfo();
        personInfo16.setName("poistovna");
        personInfo16.setValue(poistovna);
        personInfo16.setType(String.class);

        PropertyInfo personInfo17 = new PropertyInfo();
        personInfo17.setName("opravnenie");
        personInfo17.setValue(opravnenie);
        personInfo17.setType(String.class);

        PropertyInfo personInfo18 = new PropertyInfo();
        personInfo18.setName("meno");
        personInfo18.setValue(meno);
        personInfo18.setType(String.class);

        PropertyInfo personInfo19 = new PropertyInfo();
        personInfo19.setName("priezvisko");
        personInfo19.setValue(priezvisko);
        personInfo19.setType(String.class);

        PropertyInfo personInfo20 = new PropertyInfo();
        personInfo20.setName("email");
        personInfo20.setValue(email);
        personInfo20.setType(String.class);

        PropertyInfo personInfo21 = new PropertyInfo();
        personInfo21.setName("telefon");
        personInfo21.setValue(telefon);
        personInfo21.setType(String.class);

        //Add the property to request object
        request.addProperty(personInfo);
        request.addProperty(personInfo2);
        request.addProperty(personInfo3);
        request.addProperty(personInfo4);
        request.addProperty(personInfo5);
        request.addProperty(personInfo6);
        request.addProperty(personInfo7);
        request.addProperty(personInfo8);
        request.addProperty(personInfo9);
        request.addProperty(personInfo10);
        request.addProperty(personInfo11);
        request.addProperty(personInfo12);
        request.addProperty(personInfo13);
        request.addProperty(personInfo14);
        request.addProperty(personInfo15);
        request.addProperty(personInfo16);
        request.addProperty(personInfo17);
        request.addProperty(personInfo18);
        request.addProperty(personInfo19);
        request.addProperty(personInfo20);
        request.addProperty(personInfo21);

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
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        Log.i("bodyout2", "" + envelope.bodyOut.toString());
        try {
            androidHttpTransport.call("http://microsoft.com/webservices/InsertPZP", envelope);
            //SoapObject result = (SoapObject)envelope.getResponse();
            // Object  response=  (SoapObject)envelope.getResponse();
            SoapPrimitive results = (SoapPrimitive)envelope.getResponse();

            Integer ert = results.toString().length();
            if(ert == 2)
            {
                //db.delPZP(PZPID.toString());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendContacts(int id, int clientId, String surname, String name, String city, String street, String pc, String email, String phone, String sex, String bornDate, String degree_bef, String degree_aft, int attribute) {

        //vyber ulozeneho nastavenia pre autentifikaciu
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
            return;
        }

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
        //Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        Log.i("--Insert", "" + envelope.bodyOut.toString());
        try {
            androidHttpTransport.call("http://microsoft.com/webservices/InsertUpdatePerson", envelope);
            //SoapObject result = (SoapObject)envelope.getResponse();
            // Object  response=  (SoapObject)envelope.getResponse();
            SoapPrimitive results = (SoapPrimitive)envelope.getResponse();

            Integer ert = Integer.parseInt(results.toString());
            //update contact client id
                //db.delPZP(PZPID.toString());

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class AsyncCallWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground");

            sendAllContacts();
            loadAttributes();
            loadContacts();

            /*
            String title = "";
            String name = "";
            String surname = "";
            String borndate = "";
            String city = "";
            String street = "";
            String number = "";
            String gender = "";
            String email = "";
            String phone = "";

            Cursor cursor = db.getAllRows("");
            while (cursor.moveToNext()) {
                title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                surname = cursor.getString(cursor.getColumnIndexOrThrow("surname"));
                borndate = cursor.getString(cursor.getColumnIndexOrThrow("borndate"));
                city = cursor.getString(cursor.getColumnIndexOrThrow("city"));
                street = cursor.getString(cursor.getColumnIndexOrThrow("street"));
                number = cursor.getString(cursor.getColumnIndexOrThrow("number"));
                gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
                email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));

                //sendData(title, name, surname, borndate, city, street, number, gender, email, phone);
            }*/


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
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
        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "onProgressUpdate");
        }

    }

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
            startActivity(new Intent(Synchronize.this, SecondActivity.class));
        } else if (id == R.id.nav_pzp) {
            startActivity(new Intent(Synchronize.this, TabContactMain.class));
        } else if (id == R.id.nav_statistics) {
            startActivity(new Intent(Synchronize.this, TabStatistics.class));
        } else if (id == R.id.nav_sync) {
            startActivity(new Intent(Synchronize.this, Synchronize.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.layout_synchronize);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
