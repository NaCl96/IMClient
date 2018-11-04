package com.xjtuse.student.IMClient;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class FriendDAO {

    OpenHelper helper=null;

    private FriendDAO(){}

    public FriendDAO(Context context){
        helper=new OpenHelper(context);

    }

    public long add(Friend friend){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", friend.getId());
        values.put("name", friend.getName());
        long id = db.insert("Message",null, values);
        db.close();
        return id;

    }





    public void clear(){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from friend");
        db.close();
    }

}
