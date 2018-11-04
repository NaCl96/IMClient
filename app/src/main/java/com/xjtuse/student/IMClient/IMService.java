package com.xjtuse.student.IMClient;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class IMService extends Service {
    String id;
    String name;
    Socket socket;

    //ChatActivity.ChatBinder chatBinder;

    MyBinder binder;

    InputStream in;


    SharedPreferences pref ;

    Object lock=new Object();






    public IMService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        id = pref.getString("id","NULL");
        name = pref.getString("name","NULL");


        if(!id.equals("NULL")){
            new Thread(new login()).start();
        }



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(id==null||id.equals("NULL"))
        {
            pref = PreferenceManager.getDefaultSharedPreferences(this);


            id = pref.getString("id", "NULL");

            name = pref.getString("name", "NULL");

            if(!id.equals("NULL")){
                if(socket==null||socket.isClosed())
                    new Thread(new login()).start();
            }
        }




        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        //chatBinder=(ChatActivity.ChatBinder) intent.getSerializableExtra("ChatBinder");

        if(id==null||id.equals("NULL"))
        {
            pref = PreferenceManager.getDefaultSharedPreferences(this);


            id = pref.getString("id", "NULL");

            name = pref.getString("name", "NULL");
        }

        if(!id.equals("NULL")){
            if(socket==null||socket.isClosed())
                new Thread(new login()).start();
        }



        binder=new MyBinder();
        return binder;

    }

    public class MyBinder extends Binder{

        ChatActivity.ChatBinder chatBinder;

        public void sendMessage(String toid,String content){

            Msg msg = new Msg(id,toid,content);
            new MsgDAO(IMService.this).add(msg);
            String json = msg.getJSON();
            new Thread(new sendM(json)).start();




        }

        public void setChatBinder(ChatActivity.ChatBinder chatBinder) {
            this.chatBinder = chatBinder;
        }

        public ChatActivity.ChatBinder getChatBinder() {
            return chatBinder;
        }
    }





    class login implements Runnable {

        @Override
        public void run() {


                try {
                    InetAddress addr = InetAddress.getByName(MyValues.ip);
                    socket=new Socket(addr,MyValues.port);

                    //PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

                       // out.println("Login:" + id);

                    synchronized (lock) {
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                        out.write("Login:" + id + "\n");
                        out.flush();
                    }


                    new Thread(new Check()).start();
                    new Thread(new recieveM()).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    class sendM implements Runnable{

        String text;

        public sendM(String text) {
            this.text = text;
        }

        @Override
        public void run() {

            if (socket != null) {
                try {
                    //PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

                        //out.println("Message:" + text);


                    synchronized (lock) {
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
                    String str=text.replaceAll("\n","\t");

                    out.write("Message:" + text+"\n");
                    out.flush();
                    }





                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    class recieveM implements Runnable{




        @Override
        public void run() {

            if (socket != null) {
                try {
                in= socket.getInputStream();
                BufferedReader br=new BufferedReader(new InputStreamReader(in,"UTF-8"));
                String line;

                while((line=br.readLine())!=null) {

                    String str =line.replaceAll("\t","\n");
                    Log.i("xyz",str);

                    JSONArray jsa=new JSONArray(str);
                    JSONObject jo=jsa.getJSONObject(0);
                    String from = jo.getString("from");
                    String to=jo.getString("to");
                    String text = jo.getString("text");

                    Msg msg = new Msg(from,to,text);
                    new MsgDAO(IMService.this).add(msg);

                    binder.getChatBinder().recieve(msg);

                }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
    }


    class  Check implements Runnable
    {
        @Override
        public void run() {

            try{

                while(true){

                    synchronized (lock) {
                        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
                        out.write("Ping:" + id+"\n");
                        out.flush();
                    }

                    Thread.sleep(5000);
                }


            } catch (Exception e){
                e.printStackTrace();
                new Thread(new login()).start();

            }



        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
        if(in!=null)
            in.close();

        if(socket!=null)
            socket.close();


        }catch (IOException e) {
                e.printStackTrace();
            }

    }
}
