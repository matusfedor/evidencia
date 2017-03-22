package com.prodigus.com.prodigus;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.prodigus.com.prodigus.FeedReaderContract.FeedEntry;
import com.prodigus.com.prodigus.FeedReaderContract.AccessEntry;
import android.content.ContentValues;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_COMMENTS = "clients";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SURNAME = "surname";
    public static final String COLUMN_BORNDATE = "borndate";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_STREET = "street";
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_ATT = "attribute";

    public static final String TABLE_ACCESS = "settAccess";
    public static final String COLUMN_LOG_ID = "_id";
    public static final String COLUMN_LOGNAME = "logname";
    public static final String COLUMN_PIN = "pin";

    public static final String TABLE_NOTES = "notes";
    public static final String TABLE_ATTRIBUTE = "cl_attribute";
    public static final String COLUMN_NOTE_ID = "_id";
    public static final String COLUMN_NOTE_TEXT = "notetext";
    public static final String COLUMN_NOTE_DATEC = "datec";
    public static final String COLUMN_NOTE_PERSON = "person";

    private static final String DATABASE_NAME = "contact.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_COMMENTS + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text,"
            + COLUMN_NAME + " text not null,"
            + COLUMN_SURNAME + " text not null,"
            + COLUMN_BORNDATE + " text, "
            + COLUMN_CITY + " text, "
            + COLUMN_STREET + " text, "
            + COLUMN_NUMBER + " text, "
            + COLUMN_GENDER + " text, "
            + COLUMN_EMAIL + " text, "
            + COLUMN_PHONE + " text, "
            + COLUMN_ATT + " text)";

    private static final String TABLE_SETACCESS =  "create table "
            + TABLE_ACCESS + "(" + COLUMN_LOG_ID + " integer primary key autoincrement, "
            + COLUMN_LOGNAME + " text not null,"
            + COLUMN_PIN + " text not null ) ";

    private static final String TABLE_NOTESS =  "create table "
            + TABLE_NOTES + "(" + COLUMN_NOTE_ID + " integer primary key autoincrement, "
            + COLUMN_NOTE_TEXT + " text not null,"
            + COLUMN_NOTE_DATEC + " text not null,"
            + COLUMN_NOTE_PERSON + " text not null) ";

    private static final String TABLE_ATTRIBUTES =  "create table "
            + "cl_attribute" + "( _id integer primary key autoincrement, "
            + "att_sc" + " text not null,"
            + "att_full" + " text not null) ";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE); database.execSQL(TABLE_SETACCESS); database.execSQL(TABLE_NOTESS); database.execSQL(TABLE_ATTRIBUTES);
        database.execSQL("Insert into cl_attribute values (1,'AFA','Finan. analyza')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTRIBUTE);
        onCreate(db);
    }

    //insert comment
    public long createToDo(String title, String name, String surname, Date borndate, String city, String street, String number, String email, String phone, String gender, String attribute) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_TITLE, title);
        values.put(FeedEntry.COLUMN_NAME,name);
        values.put(FeedEntry.COLUMN_SURNAME,surname);
        values.put(FeedEntry.COLUMN_BORNDATE,borndate.toString());
        values.put(FeedEntry.COLUMN_CITY,city);
        values.put(FeedEntry.COLUMN_STREET,street);
        values.put(FeedEntry.COLUMN_NUMBER,number);
        values.put(FeedEntry.COLUMN_EMAIL,email);
        values.put(FeedEntry.COLUMN_PHONE,phone);
        values.put(FeedEntry.COLUMN_GENDER,gender);
        values.put(FeedEntry.COLUMN_ATTRIBUTE, attribute);
        // insert row

        long todo_id = db.insert(FeedEntry.TABLE_NAME, null, values);

        return todo_id;
    }

    public long updateToDo(int personId, String title, String name, String surname, Date borndate, String city, String street, String number, String email, String phone, String gender, String attribute) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_TITLE, title);
        values.put(FeedEntry.COLUMN_NAME,name);
        values.put(FeedEntry.COLUMN_SURNAME,surname);
        values.put(FeedEntry.COLUMN_BORNDATE,borndate.toString());
        values.put(FeedEntry.COLUMN_CITY,city);
        values.put(FeedEntry.COLUMN_STREET,street);
        values.put(FeedEntry.COLUMN_NUMBER,number);
        values.put(FeedEntry.COLUMN_EMAIL,email);
        values.put(FeedEntry.COLUMN_PHONE,phone);
        values.put(FeedEntry.COLUMN_GENDER,gender);
        values.put(FeedEntry.COLUMN_ATTRIBUTE, attribute);
        // insert row

        long todo_id = db.update(FeedEntry.TABLE_NAME, values, "_id=" + personId, null);

        return todo_id;
    }

    public long createNote(String text, String person)
    {
        /*
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

        Date now = new Date();
        now.getDate();

        SimpleDateFormat format =
                new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

        //Date parsed = format.parse(now.toString());*/
/*
        Date dt = new Date();
        int hours = dt.getDay();
        int minutes = dt.getMonth();
        int seconds = dt.getYear();
        String curTime = hours + ":" + minutes + ":" + seconds;*/

        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.DAY_OF_MONTH);
        int minutes = calendar.get(Calendar.MONTH)+1;
        int seconds = calendar.get(Calendar.YEAR);
        String curTime = hours + "." + minutes + "." + seconds;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.Notes.COLUMN_NOTE_TEXT , text);
        values.put(FeedReaderContract.Notes.COLUMN_NOTE_DATEC, curTime);
        values.put(FeedReaderContract.Notes.COLUMN_NOTE_PERSON, person);

        long todo_id = db.insert(FeedReaderContract.Notes.TABLE_NAME, null, values);

        return todo_id;
    }

    public long setAccess(String logName, String pin) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AccessEntry.COLUMN_LOGNAME, logName);
        values.put(AccessEntry.COLUMN_PIN, pin);
         // insert row

        long todo_id = db.insert(AccessEntry.TABLE_NAME, null, values);

        return todo_id;
    }

    public void updAccess(String logName, String pin) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AccessEntry.COLUMN_LOGNAME, logName);
        values.put(AccessEntry.COLUMN_PIN, pin);
        // insert row

        String strSQL = "UPDATE settAccess SET logName = " + logName + ", pin = " + pin;
        db.execSQL(strSQL);
    }

    public boolean updateAccess(String logName, String pin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_LOGNAME, logName);
        args.put(COLUMN_PIN, pin);
        return db.update(TABLE_ACCESS, args, null, null) > 0;
    }

    public Cursor getAuth()
    {
        String selectQuery = "SELECT logname, pin FROM " + TABLE_ACCESS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public List<Clients> getAllContacts() {
        List<Clients> contactList = new ArrayList<Clients>();
        // Select All Query
        String selectQuery = "SELECT _id, name, surname FROM " + TABLE_COMMENTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Clients contact = new Clients(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    public Cursor getAllRows(String surname)
    {
        String selectQuery = "SELECT _id, name, surname FROM " + TABLE_COMMENTS + " WHERE surname like '%" + surname + "%'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getContacts(String attribute)
    {
        String selectQuery = "SELECT _id, name, surname, title, city FROM " + TABLE_COMMENTS + " WHERE attribute like '%" + attribute + "%'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getAllMarks()
    {
        String selectQuery = "SELECT distinct cl_attribute._id, att_sc, att_full FROM " + TABLE_ATTRIBUTE + " INNER JOIN " + TABLE_COMMENTS + " ON cl_attribute._id = clients.attribute";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getAllPersons()
    {
        String selectQuery = "SELECT  title, name,  surname,  borndate,  city,  street,  number,  gender,  email,  phone FROM " + TABLE_COMMENTS ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FeedReaderContract.Notes.TABLE_NAME, null, null);
        db.delete(FeedEntry.TABLE_NAME, null, null);
    }

    public Cursor getAllNotes(String personid)
    {
        String selectQuery = "SELECT _id, substr(notetext,0,10) || '...' notetext, datec, person FROM " + TABLE_NOTES + " WHERE person = " + personid;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public void delNote(String noteid)
    {
        String deleteQuery = "_id = " + noteid;
        SQLiteDatabase db = this.getWritableDatabase();
        long todo_id = db.delete(FeedReaderContract.Notes.TABLE_NAME, deleteQuery, null);
        Log.d("DELNOTE", "noteA: " + noteid );
        Log.d("DELNOTE", "noteB: " + todo_id );
    }

    public Cursor getRow(String id)
    {
        String selectQuery = "SELECT _id,title,name,surname,borndate,city,street,number,gender,email,phone FROM " + TABLE_COMMENTS + " WHERE _id = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }
}