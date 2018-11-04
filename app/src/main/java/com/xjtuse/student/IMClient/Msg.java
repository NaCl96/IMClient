package com.xjtuse.student.IMClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Msg implements Serializable{

    String from;
    String to;
    String text;
    String time;

    public Msg(String from, String to, String text) {
        this.from = from;
        this.to = to;
        this.text = text;
    }

    public Msg(String from, String to, String text, String time) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.time = time;
    }

    public String getJSON(){
        JSONArray ja=new JSONArray();
        JSONObject jo=new JSONObject();

        try {
            jo.put("from",from);
            jo.put("to",to);
            jo.put("text",text);
            ja.put(0,jo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ja.toString();


    }



    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
