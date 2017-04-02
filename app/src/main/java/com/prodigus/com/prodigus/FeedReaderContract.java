package com.prodigus.com.prodigus;

import android.provider.BaseColumns;

import java.util.Date;

public final class FeedReaderContract {
    public FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "clients";
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
        public static final String COLUMN_ATTRIBUTE = "attribute";
        public static final String COLUMN_CLIENT_ID = "clientId";
    }

    public static abstract class AccessEntry implements BaseColumns {
        public static final String TABLE_NAME = "settAccess";
        public static final String COLUMN_LOGNAME = "logname";
        public static final String COLUMN_PIN = "pin";
    }

    public static abstract class Notes implements BaseColumns {
        public static final String TABLE_NAME = "notes";
        public static final String COLUMN_NOTE_TEXT = "notetext";
        public static final String COLUMN_NOTE_DATEC = "datec";
        public static final String COLUMN_NOTE_PERSON = "person";
        public static final String COLUMN_NOTE_ATTRIBUTE = "attribute";
    }
}
