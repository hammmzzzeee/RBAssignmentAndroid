package com.example.hamza.rbassignmentandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;

public class DBCommon {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    public DBCommon() {
    }


    /* Inner class that defines the table contents */
    public static class User implements BaseColumns {
        Context context;
        DBHelper dbHelper;

        User(Context context) {
            this.context = context;
            dbHelper = new DBHelper(context);

        }


        public static final String TABLE_NAME = "User";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";


        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + User.TABLE_NAME + " (" +
                        User._ID + " INTEGER PRIMARY KEY," +
                        User.COLUMN_NAME + " TEXT," +
                        User.COLUMN_EMAIL + " TEXT)";


        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + User.TABLE_NAME;


        public void insert(String name, String email) {
            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(User.COLUMN_NAME, name);
            values.put(User.COLUMN_EMAIL, email);


            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(User.TABLE_NAME, null, values);
        }

        public ArrayList<Location> getFromDB() {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            ArrayList list = new ArrayList<Location>();


            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            Cursor cursor = db.rawQuery("select * from User", null);


            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String name = cursor.getString(cursor.getColumnIndex(User.COLUMN_NAME));
                    String email = cursor.getString(cursor.getColumnIndex(User.COLUMN_EMAIL));


                    Location loc = new Location();
                    loc.username = name;
                    loc.email = email;


                    list.add(loc);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return list;
        }


        public  boolean CheckIsDataAlreadyInDBorNot(String email) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.rawQuery("select * from User where email = '" + email +"'", null);

            if(cursor.getCount() <= 0){
                cursor.close();
                return false;
            }
            cursor.close();
            return true;
        }


    }
}

