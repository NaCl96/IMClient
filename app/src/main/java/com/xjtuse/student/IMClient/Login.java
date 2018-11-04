package com.xjtuse.student.IMClient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private Button loginBtn;
    private EditText user;
    private EditText password;
    EditText etip;

    String id;
    String pwd;


    SharedPreferences pref ;
    SharedPreferences.Editor editor;


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                Toast.makeText(Login.this, "用户验证成功", Toast.LENGTH_SHORT).show();
            }
            if(msg.what==2){

                Toast.makeText(Login.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
        }
    };





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();







        //关联组件
        user = (EditText) findViewById(R.id.userId);
        password = (EditText) findViewById(R.id.pass);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);
        etip=findViewById(R.id.ip);


        id=pref.getString("id","NULL");
        pwd=pref.getString("pwd","NULL");

        String ip = pref.getString("ip",MyValues.ip);
        if(!ip.equals(MyValues.ip)){
            MyValues.ip = ip;
            etip.setText(ip);
        }
        else{
            etip.setText(MyValues.ip);
        }

        String isLogin = pref.getString("isLogin","false");
        if(!id.equals("NULL")&&isLogin.equals("true")){
            user.setText(id);
            password.setText(pwd);

            new Thread (new login(id,pwd) ).start();



        }





    }

    @Override
    public void onClick(View v) {

                id = user.getText().toString();
                pwd = password.getText().toString();

                new Thread(new login(id,pwd)).start();



    }

    public void setip(View view) {

        String ip = etip.getText().toString();
        MyValues.ip = ip;
        editor.putString("ip",ip);
        editor.apply();
        Toast.makeText(this,"ip setted",Toast.LENGTH_LONG).show();

    }

    class login implements  Runnable{

        String tid;
        String tpwd;




        public login(String tid, String tpwd) {
            this.tid = id;
            this.tpwd = pwd;
        }

        @Override
        public void run() {

            OkHttpClient client = new OkHttpClient();

            RequestBody rb=new FormBody.Builder()
                    .add("id",tid)
                    .add("pwd",tpwd)
                    .build();

            Request request=new Request.Builder()
                    .url("http://"+MyValues.ip+":8080/IMServer/LoginServlet")
                    .post(rb)
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
                String str = response.body().string();
                if ( str.startsWith("Authorized:")) {

                    //handler.sendEmptyMessage(1);

                    String[] ss=str.split(":");
                    String name = ss[1];


                    editor.putString("id",tid);
                    editor.putString("name",name);
                    editor.putString("pwd",tpwd);
                    editor.putString("isLogin","true");

                    editor.apply();



                    //intent bundle传值
                    Intent intent = new Intent();
                    intent.setClass(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();//退出

                }else {
                    handler.sendEmptyMessage(2);
                }





            } catch (IOException e) {
                e.printStackTrace();
            }

        }





    }



}