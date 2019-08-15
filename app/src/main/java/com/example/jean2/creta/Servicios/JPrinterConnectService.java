package com.example.jean2.creta.Servicios;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.jean2.creta.JPrinterBluetoothSingleton;
import com.lvrenyang.io.BTPrinting;
import com.lvrenyang.io.IOCallBack;
import com.lvrenyang.io.Pos;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JPrinterConnectService extends Service implements IOCallBack {
    static JPrinterBluetoothSingleton jPrinterBluetoothSingleton = JPrinterBluetoothSingleton.getInstance();
    ExecutorService es = Executors.newScheduledThreadPool(30);
    Handler handler;

    static Pos mPos = jPrinterBluetoothSingleton.getmPos();
    BTPrinting mBt = jPrinterBluetoothSingleton.getmBt();
    String address;
    String name;
    public void onCreate(){
        handler = new Handler();
        jPrinterBluetoothSingleton.setCallBack(this);
        Log.d("JPrinterConnectS", "create name:" + address + " address:" + address);
        super.onCreate();
        //verificarSesion();
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
         address = intent.getStringExtra("address");
         name = intent.getStringExtra("name");
        Log.d("JPrinterConnectS", "name:" + address + " address:" + address);
        es.submit(new TaskOpen(address, name));
       // verificarSesion();
        return START_STICKY;
    }

    public void onDestroy(){
        es.submit(new TaskClose());
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public class TaskOpen implements Runnable
    {
        //        BTPrinting bt = null;
        String address = null;
        String name = null;
        Context context = null;

        public TaskOpen(String address, String name)
        {
            this.address = address;
            this.name = name;
            this.context = context;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            jPrinterBluetoothSingleton.setName(name);
            jPrinterBluetoothSingleton.setAddress(address);
            jPrinterBluetoothSingleton.mBtOpen(JPrinterConnectService.this);
//            bt.Open(address,context);
            Log.d("JPrinterConnectS", "name:" + address + " address:" + address);
//            mBt.Open(address, JPrinterConnectService.this);
            mBt.Listen(address, 1000, JPrinterConnectService.this);
        }
    }


    @Override
    public void OnOpen() {
        // TODO Auto-generated method stub
        handler.post(new Runnable(){

            @Override
            public void run() {
                Toast.makeText(JPrinterConnectService.this, "Conectado", Toast.LENGTH_SHORT).show();
                jPrinterBluetoothSingleton.setConnected(true);

            }
        });
    }

    @Override
    public void OnOpenFailed() {
        // TODO Auto-generated method stub
        handler.post(new Runnable(){

            @Override
            public void run() {

                Toast.makeText(JPrinterConnectService.this, "Failed", Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        });
    }



    public class TaskClose implements Runnable
    {
        BTPrinting bt = null;

        public TaskClose(BTPrinting bt)
        {
            this.bt = bt;
        }
        public TaskClose(){};

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if(jPrinterBluetoothSingleton.getmBt().IsOpened())
                jPrinterBluetoothSingleton.mBtClose();
            //bt.Close();
        }

    }

    @Override
    public void OnClose() {
        // TODO Auto-generated method stub
        handler.post(new Runnable(){

            @Override
            public void run() {
                Toast.makeText(JPrinterConnectService.this, "Desconectado", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
