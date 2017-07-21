package com.example.android.phoenix.messages;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.phoenix.messages.msgContract;


public class msgDbhelper extends SQLiteOpenHelper{
    // The database name
    private static final String DATABASE_NAME = "phoenix.msgdb";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    // Constructor
    public msgDbhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold the places data
        Log.d("haram","haram");
        final String SQL_CREATE_PLACES_TABLE = "CREATE TABLE " + msgContract.messageentry.TABLE_NAME + " (" +
                msgContract.messageentry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                msgContract.messageentry.COLUMN_MESSAGE + " TEXT NOT NULL, " +
                msgContract.messageentry.COLUMN_USER + " TEXT NOT NULL, " +
                msgContract.messageentry.COLUMN_PIC + " TEXT NOT NULL, " +
                msgContract.messageentry.COLUMN_TIME + " TEXT NOT NULL " +
//                "UNIQUE (" + messageentry.COLUMN_PLACE_ID + ") ON CONFLICT REPLACE" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_PLACES_TABLE);
        Log.d("haram2","haram2");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + msgContract.messageentry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
