package com.xjtuse.student.IMClient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Message;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private Handler handler;
    private InformationAdapter adapter;
    private ListView lv;



    private String id;
    private String name;

    private List<Friend> friends = new ArrayList<Friend>();

    SharedPreferences pref ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pref = PreferenceManager.getDefaultSharedPreferences(this);
        id = pref.getString("id","");
        name = pref.getString("name","");

        Intent intent = new Intent(this,IMService.class);
        startService(intent);



        lv = findViewById(R.id.lv);
        adapter = new InformationAdapter(this,R.layout.information,friends);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==1){
                    adapter.notifyDataSetChanged();
                }
            }
        };
        query();

    }



    public void query() {
        new Thread(new Runnable() {

            @Override
            public void run() {

                OkHttpClient client = new OkHttpClient();

                RequestBody rb=new FormBody.Builder()
                        .add("id",id)
                        .build();

                Request request=new Request.Builder()
                        .url("http://"+MyValues.ip+":8080/IMServer/FriendServlet")
                        .post(rb)
                        .build();

                Response response = null;
                String data;

                try {
                    response=client.newCall(request).execute();
                    data = response.body().string();
                    Gson gson = new Gson();
                    List<Friend> list=gson.fromJson(data,new TypeToken<List<Friend>>(){}.getType());
                    friends.addAll(list);
                    handler.sendEmptyMessage(1);



                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        Friend friend = (Friend) parent.getItemAtPosition(position);

        Intent intent = new Intent();
        intent.setClass(this,ChatActivity.class);
        intent.putExtra("id",friend.getId());
        intent.putExtra("name",friend.getName());
        this.startActivity(intent);

    }

    public void logout(View view) {

        SharedPreferences.Editor editor=pref.edit();
        editor.remove("id");
        editor.remove("name");
        editor.remove("pwd");
        editor.putString("isLogin","false");
        editor.apply();

        new MsgDAO(this).clear();

        Intent intent = new Intent();
        intent.setAction("com.xjtuse.student.IMClient.IMService");
        stopService(intent);
        finish();

        //System.exit(0);





    }
}
