package com.xjtuse.student.IMClient;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OpenHelper extends SQLiteOpenHelper {

    //private String sql1="create table friend(id integer primary key autoincrement,name text) ";
    private String sql2="create table message(id integer primary key autoincrement, fromid text , toid text ,msg text)";

    public OpenHelper(Context context) {
        super(context,"myIM.db", null, 1);

    }

    public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //db.execSQL(sql1);
        db.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
