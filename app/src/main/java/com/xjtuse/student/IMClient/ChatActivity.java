package com.xjtuse.student.IMClient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private List<Vmsg> msgList = new ArrayList<Vmsg>();

    private EditText inputText;

    private Button send;

    private RecyclerView msgRecyclerView;

    private VmsgAdapter adapter;

    private IMService.MyBinder binder;

    private ChatBinder chatBinder;

    String friendid="";
    String friendname="";



    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){

                adapter.notifyItemInserted(msgList.size() - 1);
                msgRecyclerView.scrollToPosition(msgList.size() - 1);

            }

            if(msg.what==2){
                adapter.notifyDataSetChanged();
            }



        }
    };


    private ServiceConnection connection=new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {


        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder=(IMService.MyBinder) service;

            chatBinder=new ChatBinder();
            binder.setChatBinder(chatBinder);


        }
    };







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        Intent sintent = new Intent(this,IMService.class);

        //chatBinder=new ChatBinder();
       // sintent.putExtra("ChatBinder",chatBinder);
        bindService(sintent,connection,BIND_AUTO_CREATE);

        inputText =  findViewById(R.id.input_text);
        send = findViewById(R.id.send);
        msgRecyclerView =  findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new VmsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    Vmsg msg = new Vmsg(content, Vmsg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                    inputText.setText(""); // 清空输入框中的内容



                    binder.sendMessage(friendid,content);
                }
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();

        Intent intent=getIntent();
        friendid=intent.getStringExtra("id");
        friendname=intent.getStringExtra("name");

        setTitle(friendname);

        List<Msg> list = new MsgDAO(this).query(friendid);

        msgList.clear();


        for(Msg m:list){

            if(m.getFrom().equals(friendid))
            {
                msgList.add(new Vmsg(m.getText(),0));
            }
            else if(m.getTo().equals(friendid)){
                msgList.add(new Vmsg(m.getText(),1));
            }

        }
        adapter.notifyDataSetChanged();





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }




    class ChatBinder implements Serializable{

        public void recieve(Msg msg){

            if(msg.getFrom().equals(friendid)){

                msgList.add(new Vmsg(msg.getText(),0));
                handler.sendEmptyMessage(1);

            }

        }

    }

}
