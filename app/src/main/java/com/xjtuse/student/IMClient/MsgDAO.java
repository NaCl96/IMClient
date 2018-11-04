package com.xjtuse.student.IMClient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MsgDAO {

    OpenHelper helper=null;

    private MsgDAO(){}

    public MsgDAO(Context context){
        helper=new OpenHelper(context);

    }

    public long add(Msg msg){
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("fromid", msg.getFrom());
        values.put("toid", msg.getTo());
        values.put("msg", msg.getText());
        //values.put("time",msg.getTime() );
        long id = db.insert("message",null, values);
        db.close();
        return id;

    }

    public List<Msg> query(String id){

        List<Msg> list=new ArrayList<Msg>();
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "select fromid,toid,msg from message where fromid=? or toid=? order by id ASC ";
        Cursor cursor=db.rawQuery(sql,new String[]{id,id});
        while (cursor.moveToNext()){
            String from = cursor.getString(cursor.getColumnIndex("fromid"));
            String to = cursor.getString(cursor.getColumnIndex("toid"));
            String content = cursor.getString(cursor.getColumnIndex("msg"));
            Msg msg = new Msg(from,to,content);
            list.add(msg);
        }
        db.close();

        return list;




    }




    public void clear(){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from message");
        db.close();
    }



}
