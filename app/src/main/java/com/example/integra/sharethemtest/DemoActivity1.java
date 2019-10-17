package com.example.integra.sharethemtest;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class DemoActivity1 extends AppCompatActivity {

    Handler mHandler;
    private Button startOrStop,reset;
    TextView tvTimer;
    MyThread mMyThread;
    Runnable runnable;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1);
        startOrStop=findViewById(R.id.bt_start);
        reset=findViewById(R.id.bt_reset);
        tvTimer=findViewById(R.id.tv_timer);
        mMyThread = new MyThread();
        mHandler= new Handler();
    }


    public void onStartOrStop(View view) {
        Button bt= (Button) view;
        String name=bt.getText().toString();
        if(name.equals("Start")){

            long time= SystemClock .uptimeMillis();
            mHandler.postDelayed(runnable=new Runnable() {
                @Override
                public void run() {
                    tvTimer.setText(""+SystemClock.uptimeMillis());
                    mHandler.postDelayed(runnable,1);


                }
            },1);



            bt.setText("Stop");
        }else {
            bt.setText("Start");
            mHandler.removeCallbacks(runnable);
        }
    }



    public void onReset(View view) {
    }

    class MyHandler extends Handler{


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    class  MyThread extends Thread{
        @Override
        public void run() {

              for (int i=0;i<20;i++){
                  try {
                      Thread.sleep(1000);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
                  final int finalI = i;
                  mHandler.post(new Runnable() {
                      @Override
                      public void run() {
                          tvTimer.setText(""+ finalI);
                      }
                  });

              }



        }
    }
}
