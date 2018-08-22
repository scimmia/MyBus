package com.scimmia.mybus.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lk on 2017/8/12.
 */
public class BusDBHelper extends SQLiteOpenHelper{
    public BusDBHelper(Context context) {
        super(context, "busFav.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        String strSQL = "CREATE TABLE \"favstation\" (\n" +
//                "\"keyvalue\"  varchar NOT NULL,\n" +
//                "\"tag\"  varchar NOT NULL,\n" +
//                "\"stationainfo\"  varchar,\n" +
//                "PRIMARY KEY (\"keyvalue\", \"tag\")\n" +
//                ")";
        String strSQL = "CREATE TABLE \"favstation\" (\n" +
                "\"lineid\"  varchar NOT NULL,\n" +
                "\"attach\"  varchar NOT NULL,\n" +
                "\"stationid\"  varchar NOT NULL,\n" +
                "\"linestatus\"  varchar NOT NULL,\n" +
                "\"tag\"  varchar NOT NULL,\n" +
                "\"linename\"  varchar,\n" +
                "\"stationa\"  varchar,\n" +
                "\"stationb\"  varchar,\n" +
                "\"stationname\"  varchar,\n" +
                "\"lat\"  float,\n" +
                "\"lon\"  float,\n" +
                "PRIMARY KEY (\"lineid\", \"attach\", \"stationid\", \"linestatus\", \"tag\")\n" +
                ")";
        sqLiteDatabase.execSQL(strSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
