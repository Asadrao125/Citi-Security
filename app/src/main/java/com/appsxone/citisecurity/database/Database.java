package com.appsxone.citisecurity.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.appsxone.citisecurity.models.OfflineLocationModel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Database {
    String DB_PATH = "data/data/com.appsxone.citisecurity/databases/";
    String DB_NAME = "location_db.sqlite";
    Context activity;
    SQLiteDatabase sqLiteDatabase;

    public Database(Context activity) {
        this.activity = activity;
    }

    public void createDatabase() {
        boolean dBExist = false;

        try {
            dBExist = checkDatabase();
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (dBExist) {

        } else {
            try {

                sqLiteDatabase = activity.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
                sqLiteDatabase.close();
                copyDatabaseTable();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
    }

    private void copyDatabaseTable() throws IOException {

        //open your local database as input stream
        InputStream myInput = activity.getAssets().open(DB_NAME);

        //path to the created empty database
        String outFileName = DB_PATH + DB_NAME;

        //open the empty database as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    private boolean checkDatabase() {

        SQLiteDatabase checkDB = null;
        String myPath = DB_PATH + DB_NAME;
        try {
            try {
                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            //no database exists...
        }


        if (checkDB != null) {

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    public void open() {
        sqLiteDatabase = activity.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
    }

    public void close() {
        sqLiteDatabase.close();
    }

    //============================ START CUSTOM METHODS FOR CATEGORIES TABLE ====================================

    /*
    CREATE TABLE "offline_location" (
	"userid"	TEXT,
	"udid"	TEXT,
	"lat_lng_date_time"	TEXT,
	"id"	INTEGER,
	PRIMARY KEY("id" AUTOINCREMENT));
    */

    public long saveLocation(OfflineLocationModel offlineLocationModel) {
        long rowId = -1;
        try {
            open();
            ContentValues cv = new ContentValues();
            cv.put("userid", offlineLocationModel.userId);
            cv.put("udid", offlineLocationModel.udid);
            cv.put("lat_lng_date_time", offlineLocationModel.lat_lng_date_time);
            rowId = sqLiteDatabase.insert("offline_location", null, cv);
            close();
        } catch (SQLiteException e) {
            Toast.makeText(activity, "Database exception", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        System.out.println("-- Record inserted rowId : " + rowId);
        return rowId;
    }

    public ArrayList<OfflineLocationModel> getAllLocations() {
        open();
        ArrayList<OfflineLocationModel> categoryBeans = new ArrayList<>();
        OfflineLocationModel temp;
        String query = "select * from offline_location";

        System.out.println("--query in getAllLocations : " + query);
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String userId = cursor.getString(cursor.getColumnIndex("userid"));
                String uDID = cursor.getString(cursor.getColumnIndex("udid"));
                String lat_lng_date_time = cursor.getString(cursor.getColumnIndex("lat_lng_date_time"));
                temp = new OfflineLocationModel(id, userId, uDID, lat_lng_date_time);
                categoryBeans.add(temp);
                temp = null;
            }
            while (cursor.moveToNext());
            close();
            return categoryBeans;
        }
        close();
        return null;
    }

    public void deleteData() {
        open();
        String query = "delete from offline_location";
        sqLiteDatabase.execSQL(query);
        close();
    }

}