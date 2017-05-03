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

    //region Contact
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
    public static final String COLUMN_CLIENT_ID = "clientId";
    //endregion

    //region Access
    public static final String TABLE_ACCESS = "settAccess";
    public static final String COLUMN_LOG_ID = "_id";
    public static final String COLUMN_LOGNAME = "logname";
    public static final String COLUMN_PIN = "pin";
    //endregion

    //region Note
    public static final String TABLE_NOTES = "notes";
    public static final String TABLE_ATTRIBUTE = "cl_attribute";
    public static final String COLUMN_NOTE_ID = "_id";
    public static final String COLUMN_NOTE_TEXT = "notetext";
    public static final String COLUMN_NOTE_DATEC = "datec";
    public static final String COLUMN_NOTE_PERSON = "person";
    public static final String COLUMN_NOTE_ATTRIBUTE = "attribute";
    //endregion

    //region Contact State History
    public static final String TABLE_conStateHistory = "contactStateHistory";
    public static final String COLUMN_HIS_ID = "_id";
    public static final String COLUMN_CON_ID = "con_id";
    public static final String COLUMN_CON_STATE = "con_state";
    public static final String COLUMN_CHANGE_DATE = "change_date";
    //endregion

    //region Statistics
    public static final String TABLE_STATS = "statistics";
    public static final String COLUMN_STAT_ID = "_id";
    public static final String COLUMN_DATE = "stat_date";
    public static final String COLUMN_CNT = "stat_count";
    public static final String COLUMN_USER = "stat_user";
    public static final String COLUMN_ATTRIBUTE = "stat_attribute";
    public static final String COLUMN_STAT_TYPE = "stat_type";
    //endregion

    //region Users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USR_ID = "_id";
    public static final String COLUMN_USR_NICK = "usr_nick";
    public static final String COLUMN_USR_NAME = "usr_name";
    //endregion

    private static final String DATABASE_NAME = "contact.db";
    private static final int DATABASE_VERSION = 17;

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //region Basic
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CONTACTS);
        database.execSQL(TABLE_SETACCESS);
        database.execSQL(TABLE_NOTESS);
        database.execSQL(TABLE_ATTRIBUTES);
        database.execSQL(TABLE_ContactStateHistory);
        database.execSQL(TABLE_STATISTICS);
        database.execSQL(TABLE_CREATE_USERS);

        /*database.execSQL("Insert into cl_attribute values (1,'AFA','Finan. analyza','C',1)");
        database.execSQL("Insert into cl_attribute values (2,'TEL','Telef. kontakt','N',2)");
        database.execSQL("Insert into cl_attribute values (3,'OSO','Osobné stretnutie','N',3)");
        database.execSQL("Insert into cl_attribute values (4,'SKO','Školenie','N',4)");*/

        //database.execSQL("Insert into settAccess values (1,'Ditte','9595')");

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_conStateHistory);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
    //endregion

    //region Table creation
    // Database creation sql statements
    private static final String TABLE_CONTACTS = "create table "
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
            + COLUMN_ATT + " text,"
            + COLUMN_CLIENT_ID + " integer)";

    private static final String TABLE_ContactStateHistory = "create table " + TABLE_conStateHistory
            + "(" + COLUMN_HIS_ID + " integer primary key autoincrement, "
            + COLUMN_CON_ID + " integer, "
            + COLUMN_CON_STATE + " integer, "
            + COLUMN_CHANGE_DATE +  " numeric)";

    private static final String TABLE_SETACCESS =  "create table "
            + TABLE_ACCESS + "(" + COLUMN_LOG_ID + " integer primary key autoincrement, "
            + COLUMN_LOGNAME + " text not null,"
            + COLUMN_PIN + " text not null ) ";

    private static final String TABLE_NOTESS =  "create table "
            + TABLE_NOTES + "(" + COLUMN_NOTE_ID + " integer primary key autoincrement, "
            + COLUMN_NOTE_TEXT + " text not null,"
            + COLUMN_NOTE_DATEC + " numeric,"
            + COLUMN_NOTE_PERSON + " text not null, "
            + COLUMN_NOTE_ATTRIBUTE + " text not null) ";

    private static final String TABLE_STATISTICS =  "create table "
            + TABLE_STATS + "(" + COLUMN_STAT_ID + " integer primary key autoincrement, "
            + COLUMN_DATE + " text not null,"
            + COLUMN_CNT + " integer not null,"
            + COLUMN_USER + " text not null,"
            + COLUMN_ATTRIBUTE + " integer not null,"
            + COLUMN_STAT_TYPE + " text null )";

    private static final String TABLE_CREATE_USERS =  "create table "
            + TABLE_USERS + "(" + COLUMN_USR_ID + " integer primary key autoincrement, "
            + COLUMN_USR_NICK + " text not null,"
            + COLUMN_USR_NAME + " text not null )";

    ////+ COLUMN_NOTE_DATEC + " text not null,"

    private static final String TABLE_ATTRIBUTES =  "create table "
            + "cl_attribute" + "( _id integer primary key, "
            + "att_sc" + " text not null,"
            + "att_full" + " text not null,"
            + "att_type" + " text not null,"
            + "att_status_order" + " integer)";
    //endregion

    //region Select SQL
    public Cursor getAuth()
    {
        String selectQuery = "SELECT logname, pin FROM " + TABLE_ACCESS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public int getPersonsStatuses(Integer personID) {
        List<Integer> statusList = new ArrayList<Integer>();
        // Select All Query
        String selectQuery = "SELECT MAX(con_state) FROM " + TABLE_conStateHistory + " WHERE con_id = " + personID;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if(cursor.getCount() != 0)
        {
            return cursor.getInt(0);
        }
        return 0;

        // looping through all rows and adding to list
        /*if (cursor.moveToFirst()) {
            do {
                statusList.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }*/

        // return contact list
    }

    public int GetAttributeOrder(int statusId) {
        String selectQuery = "SELECT att_status_order FROM " + TABLE_ATTRIBUTE + " WHERE _id = " + statusId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if(cursor.getCount() != 0)
        {
            return cursor.getInt(0);
        }
        return 0;
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

    public Cursor selectAllContacts()
    {
        String selectQuery = "SELECT _id, title, name, surname, borndate, city, street, number, gender, email, phone, attribute, clientId FROM " + TABLE_COMMENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getContacts(String attribute, String searchText)
    {
        String selectQuery;
        if(searchText != null)
        {
            selectQuery = "SELECT _id, name, surname, title, city FROM " + TABLE_COMMENTS + " WHERE attribute like '%" + attribute + "%' and (name like '%" + searchText + "%' or surname like '%" + searchText + "%')";
        }
        else {
            selectQuery = "SELECT _id, name, surname, title, city FROM " + TABLE_COMMENTS + " WHERE attribute like '%" + attribute + "%'";
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getNotesByAttribute(String attribute, int personId)
    {
        //String selectQuery = "SELECT _id, datec, notetext FROM " + TABLE_NOTES + " WHERE attribute like '%" + attribute + "%' and person = " + personId;
        String selectQuery = "SELECT _id, datec, notetext FROM notes WHERE attribute = " + attribute + " and person = " + personId +
        " UNION SELECT _id, datec, notetext FROM notes WHERE attribute = " + attribute + " and person = (SELECT clientId FROM clients WHERE _id = " + personId + ")";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getAllMarks(String searchText)
    {
        String selectQuery;
        if(searchText != null)
        {
            selectQuery = "SELECT distinct cl_attribute._id, att_sc, att_full FROM " + TABLE_ATTRIBUTE + " INNER JOIN " + TABLE_COMMENTS + " ON cl_attribute._id = clients.attribute WHERE " + COLUMN_SURNAME + " LIKE '%" + searchText + "%' or " + COLUMN_NAME + " LIKE '%" + searchText + "%'";
        }
        else {
            selectQuery = "SELECT distinct cl_attribute._id, att_sc, att_full FROM " + TABLE_ATTRIBUTE + " INNER JOIN " + TABLE_COMMENTS + " ON cl_attribute._id = clients.attribute";
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getAllNoteMarks(int personID)
    {
        String selectQuery = "SELECT distinct cl_attribute._id _id, att_sc, att_full FROM " + TABLE_ATTRIBUTE + " INNER JOIN " + TABLE_NOTES + " ON cl_attribute._id = notes.attribute" + " WHERE notes.person = " + personID +
                " UNION SELECT distinct cl_attribute._id _id, att_sc, att_full FROM cl_attribute INNER JOIN notes ON cl_attribute._id = notes.attribute WHERE notes.person = (SELECT clientId from clients where _id = " + personID + ")";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getAllStatMarks()
    {
        String selectQuery = "SELECT _id, att_sc, att_full FROM " + TABLE_ATTRIBUTE + " WHERE att_type = 'N' and att_status_order != 0 order by att_status_order";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getAllContactAttHistory()
    {
        String selectQuery = "SELECT con_id, con_state, change_date FROM contactStateHistory";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getStatCounts(int days)
    {
        String selectQuery = "SELECT count(contactStateHistory.con_state) pocet, cl_attribute._id FROM cl_attribute LEFT JOIN contactStateHistory ON cl_attribute._id = contactStateHistory.con_state and contactStateHistory.change_date > date( julianday(date('now'))- " + days + " )" +
                " WHERE att_type = 'N' and att_status_order != 0 GROUP BY cl_attribute._id";
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

    public Cursor getAllNotes(String personid)
    {
        String selectQuery = "SELECT _id, substr(notetext,0,10) || '...' notetext, datec, person FROM " + TABLE_NOTES + " WHERE person = " + personid;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getDayStatistics(int attribute, int step)
    {
        String selectQuery = "SELECT strftime('%d.%m.%Y',date('now','" + step * (-1) + " day')) datum , count(*) cnt FROM contactStateHistory WHERE con_state = " + attribute + " AND date(change_date) = date('now','" + step * (-1) + " day')";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getDayStatistics(int attribute, int step, String user)
    {
        String selectQuery = "SELECT strftime('%d.%m.%Y',date('now','" + step * (-1) + " day')) datum , count(*) cnt FROM " + TABLE_STATS + " WHERE " + COLUMN_ATTRIBUTE + " = " + attribute + " AND date(" + COLUMN_DATE + ") = date('now','" + step * (-1) + " day') AND " + COLUMN_USER + " = '" + user + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getWeekStatistics(int attribute) {
        String selectQuery = "SELECT strftime('%W',date('now')) week, count(*) cnt FROM contactStateHistory WHERE con_state = " + attribute + " AND date(change_date)  <  DATE('now', 'weekday 1') AND date(change_date)  >  DATE('now', 'weekday 1', '-7 days')";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getWeekStatistics(int attribute, int step) {
        String selectQuery = "SELECT strftime('%W.%Y',date('now','" + step * (-7) + " days')) week, count(*) cnt FROM contactStateHistory WHERE con_state = " + attribute + " AND date(change_date)  <  DATE('now', 'weekday 1','" + step * (-7) + " days') AND date(change_date)  >  DATE('now', 'weekday 1', ' " + -7 * (step + 1) + " days')";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getWeekStatistics(int attribute, int step, String user)
    {
        String selectQuery = "SELECT strftime('%W.%Y',date('now','" + step * (-7) + " days')) week , count(*) cnt FROM " + TABLE_STATS + " WHERE " + COLUMN_ATTRIBUTE + " = " + attribute + " AND date(" + COLUMN_DATE + ") <  DATE('now', 'weekday 1','" + step * (-7) + " days') AND date(" + COLUMN_DATE + ")  >  DATE('now', 'weekday 1', ' " + -7 * (step + 1) + " days') AND " + COLUMN_USER + " = '" + user + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getMonthStatistics(int attribute, int step) {
        String selectQuery = "SELECT strftime('%m.%Y',date('now','start of month','" + step * (-1) + " month')) month, count(*) cnt FROM contactStateHistory WHERE con_state = " + attribute + " AND date(change_date)  <  DATE('now', 'start of month','" + step * (-1) + " month', '-1 day') AND date(change_date)  >  DATE('now', 'start of month', ' " + step * (-1) + " month')";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getMonthStatistics(int attribute, int step, String user)
    {
        String selectQuery = "SELECT strftime('%m.%Y',date('now','start of month','" + step * (-1) + " month')) month, count(*) cnt FROM " + TABLE_STATS + " WHERE " + COLUMN_ATTRIBUTE + " = " + attribute + " AND date(" + COLUMN_DATE + ") <  DATE('now', 'start of month','" + step * (-1) + " month', '-1 day') AND date(" + COLUMN_DATE + ")  >  DATE('now', 'start of month', ' " + step * (-1) + " month') AND " + COLUMN_USER + " = '" + user + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getSyncNotes()
    {
        String selectQuery = "SELECT _id, notetext, datec, person, attribute FROM " + TABLE_NOTES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getRow(String id)
    {
        String selectQuery = "SELECT _id,title,name,surname,borndate,city,street,number,gender,email,phone FROM " + TABLE_COMMENTS + " WHERE _id = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getNote(int id)
    {
        String selectQuery = "SELECT _id, notetext, datec, person, attribute FROM " + TABLE_NOTES + " WHERE _id = " + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public List<Genders> getAllNoteTypes() {
        List<Genders> noteTypesList = new ArrayList<Genders>();
        String selectQuery = "SELECT _id, att_full FROM " + TABLE_ATTRIBUTE + " WHERE att_type = 'N'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Genders contact = new Genders(Integer.parseInt(cursor.getString(0)),cursor.getString(1));
                noteTypesList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return noteTypesList;
    }

    public Cursor getUsers() {
        List<Users> usersList = new ArrayList<Users>();
        String selectQuery = "SELECT usr_nick, usr_name FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        /*if (cursor.moveToFirst()) {
            do {
                Users user = new Users(cursor.getString(0),cursor.getString(1));
                usersList.add(user);
            } while (cursor.moveToNext());
        }*/

        // return contact list
        return cursor;
    }

    //endregion

    //region Insert SQL
    public long createToDo(String title, String name, String surname, Date borndate, String city, String street, String number, String email, String phone, String gender, String attribute, int clientId) {
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
        values.put(FeedEntry.COLUMN_CLIENT_ID, clientId);
        // insert row

        long todo_id = db.insert(FeedEntry.TABLE_NAME, null, values);

        return todo_id;
    }

    public long createContactHistory(long conID, int state, Date creation) {
        SQLiteDatabase db = this.getWritableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
        String cDate = sdf.format(new Date());

        ContentValues values = new ContentValues();
        values.put(COLUMN_CON_ID, conID);
        values.put(COLUMN_CON_STATE, state);
        if(creation != null) {
            values.put(COLUMN_CHANGE_DATE, sdf.format(creation));
        }
        else
        {values.put(COLUMN_CHANGE_DATE, cDate);}
        // insert row

        long todo_id = db.insert(TABLE_conStateHistory, null, values);

        return todo_id;
    }

    public long createAttribute(int id, String att_sc, String att_full, int att_con_order, String att_type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("_id", id);
        values.put("att_sc",att_sc);
        values.put("att_full",att_full);
        values.put("att_type",att_type);
        values.put("att_status_order",att_con_order);

        long todo_id = db.insert("cl_attribute", null, values);

        return todo_id;
    }

    public long createUsers(String nick, String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("usr_nick",nick);
        values.put("usr_name",name);

        long todo_id = db.insert("users", null, values);

        return todo_id;
    }

    public long createStats(String stat_date, int cnt, String user, int attribute, String type) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("stat_date",stat_date);
        values.put("stat_count",cnt);
        values.put("stat_user",user);
        values.put("stat_attribute",attribute);
        values.put("stat_type",type);


        long todo_id = db.insert(TABLE_STATS, null, values);

        return todo_id;
    }

    public long createAuth(String name, String pin) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("logname",name);
        values.put("pin",pin);

        long todo_id = db.insert(TABLE_ACCESS, null, values);

        return todo_id;
    }

    public long createNote(String text, String person, long attribute)
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

        /*Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.DAY_OF_MONTH);
        int minutes = calendar.get(Calendar.MONTH)+1;
        int seconds = calendar.get(Calendar.YEAR);
        String curTime = hours + "." + minutes + "." + seconds;*/

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
        Date now = new Date();
        now.getDate();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.Notes.COLUMN_NOTE_TEXT , text);
        values.put(FeedReaderContract.Notes.COLUMN_NOTE_DATEC, sdf.format(now));
        values.put(FeedReaderContract.Notes.COLUMN_NOTE_PERSON, person);
        values.put(FeedReaderContract.Notes.COLUMN_NOTE_ATTRIBUTE, attribute);

        long todo_id = db.insert(FeedReaderContract.Notes.TABLE_NAME, null, values);

        return todo_id;
    }

    public long createSyncNote(String text, int person, int attribute, Date dateNote)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.Notes.COLUMN_NOTE_TEXT , text);
        //values.put(FeedReaderContract.Notes.COLUMN_NOTE_DATEC, dateNote.toString());
        values.put(FeedReaderContract.Notes.COLUMN_NOTE_DATEC, sdf.format(dateNote));
        values.put(FeedReaderContract.Notes.COLUMN_NOTE_PERSON, person);
        values.put(FeedReaderContract.Notes.COLUMN_NOTE_ATTRIBUTE, attribute);

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
    //endregion

    //region Update SQL
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

    public long updateContactClientId(int personId, int clientId) {
        SQLiteDatabase db = this.getWritableDatabase();

        if(String.valueOf(clientId).isEmpty() && clientId > 0)
        { return 0;}

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_CLIENT_ID, clientId);

        long todo_id = db.update(FeedEntry.TABLE_NAME, values, "_id=" + personId, null);

        //update of client id in contact attribute history
        ContentValues historyValues = new ContentValues();
        historyValues.put(COLUMN_CON_ID, clientId);

        db.update(TABLE_conStateHistory, historyValues, "con_id=" + personId, null);

        //update of client id in notes
        ContentValues noteValues = new ContentValues();
        noteValues.put(COLUMN_NOTE_PERSON, clientId);

        db.update(TABLE_NOTES, noteValues, "person=" + personId, null);

        return todo_id;
    }

    public long updateStatus(int personId, int attribute) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_ATTRIBUTE, attribute);
        long todo_id = db.update(FeedEntry.TABLE_NAME, values, "_id=" + personId, null);

        createContactHistory(personId, attribute, new Date());

        return todo_id;
    }

    public long updateNote(int nodeId, String text, long attribute) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.Notes.COLUMN_NOTE_TEXT, text);
        values.put(FeedReaderContract.Notes.COLUMN_NOTE_ATTRIBUTE, attribute);
        long todo_id = db.update(FeedReaderContract.Notes.TABLE_NAME, values, "_id=" + nodeId, null);
        return todo_id;
    }

    public void updAccess(String logName, String pin) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AccessEntry.COLUMN_LOGNAME, logName);
        values.put(AccessEntry.COLUMN_PIN, pin);
        // insert row

        String strSQL = "UPDATE settAccess SET logName = '" + logName + "', pin = '" + pin + "'";
        db.execSQL(strSQL);
    }

    public boolean updateAccess(String logName, String pin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_LOGNAME, logName);
        args.put(COLUMN_PIN, pin);
        return db.update(TABLE_ACCESS, args, null, null) > 0;
    }

    //endregion

    //region Delete SQL
    public int deleteContact(int personId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_TITLE, personId);

        int todo_id = db.delete(FeedEntry.TABLE_NAME, "_id=" + personId, null);

        return todo_id;
    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FeedReaderContract.Notes.TABLE_NAME, null, null);
        db.delete(FeedEntry.TABLE_NAME, null, null);
        db.delete(TABLE_conStateHistory, null, null);
        db.delete(TABLE_USERS, null, null);
        db.delete(TABLE_STATS, null, null);
    }

    public void deleteAllAttributes()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cl_attribute", null, null);
    }

    public void delNote(String noteid)
    {
        String deleteQuery = "_id = " + noteid;
        SQLiteDatabase db = this.getWritableDatabase();
        long todo_id = db.delete(FeedReaderContract.Notes.TABLE_NAME, deleteQuery, null);
    }

    //endregion
}
