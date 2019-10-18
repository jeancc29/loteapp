package com.example.jean2.creta.Servicios;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.jean2.creta.DialogGuardarPrinter;
import com.example.jean2.creta.JPrinterBluetoothSingleton;
import com.example.jean2.creta.Main2Activity;
import com.example.jean2.creta.R;
import com.example.jean2.creta.Utilidades;
import com.lvrenyang.io.BLEPrinting;
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

//    static Pos mPos = jPrinterBluetoothSingleton.getmPos();
//    BTPrinting mBt = jPrinterBluetoothSingleton.getmBt();

    BTPrinting mBt = new BTPrinting();
    public static Pos mPos = new Pos();
    public static boolean detenidoPresionandoBotonDesconectar = false;

    public static String address;
    public static String name;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    public void onCreate(){
        try{
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//
//
//                Intent notificationIntent = new Intent(this, JPrinterConnectService.class);
//                PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                        0, notificationIntent, 0);
//
//                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                        .setContentTitle("Foreground Service")
//                        .setContentText("Servicio impresora")
//                        .setContentIntent(pendingIntent)
//                        .build();
//
//                startForeground(1, notification);
//            }
            //startForeground(1, "Impresora conectada");
//        else
//            mContext.startService(serviceIntent);
            handler = new Handler();
            //jPrinterBluetoothSingleton.setCallBack(this);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
                String channelName = "My Background Service";
                NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
                chan.setLightColor(Color.BLUE);
                chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                assert manager != null;
                manager.createNotificationChannel(chan);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
                Notification notification = notificationBuilder.setOngoing(true)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("App is running in background")
                        .setPriority(NotificationManager.IMPORTANCE_MIN)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .build();
                startForeground(2, notification);
            }



            mPos.Set(mBt);
            mBt.SetCallBack(this);
            Log.d("JPrinterConnectS", "create name:" + address + " address:" + address);
        }catch (Exception e){
            Toast.makeText(this, "Error servicio: " + e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

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
        Log.d("JprinterConnectService", "Servicio destruido");
//        if(detenidoPresionandoBotonDesconectar == false){
//            Intent serviceIntent = new Intent(JPrinterConnectService.this, JPrinterConnectService.class);
//            serviceIntent.putExtra("address", address);
//            serviceIntent.putExtra("name", name);
//            startService(serviceIntent);
//        }else{
//            es.submit(new TaskClose());
//            super.onDestroy();
//        }


       try{
           es.submit(new TaskClose());
           if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
               stopForeground(true);
           }
       }catch (Exception e){
           e.printStackTrace();
       }
        super.onDestroy();

    }

    public static boolean isPrinterConnected(){
        return mPos.GetIO().IsOpened();
    }

//    private void startMyOwnForeground(){
//        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
//        String channelName = "My Background Service";
//        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
//        chan.setLightColor(Color.BLUE);
//        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        assert manager != null;
//        manager.createNotificationChannel(chan);
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
//        Notification notification = notificationBuilder.setOngoing(true)
//                .setSmallIcon(R.drawable.icon_1)
//                .setContentTitle("App is running in background")
//                .setPriority(NotificationManager.IMPORTANCE_MIN)
//                .setCategory(Notification.CATEGORY_SERVICE)
//                .build();
//        startForeground(2, notification);
//    }


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
            //jPrinterBluetoothSingleton.mBtOpen(JPrinterConnectService.this);
//            bt.Open(address,context);
            Log.d("JPrinterConnectS", "name:" + address + " address:" + address);
            mBt.Open(address, JPrinterConnectService.this);
            //mBt.Listen(address, 1000, JPrinterConnectService.this);
        }
    }


    @Override
    public void OnOpen() {
        // TODO Auto-generated method stub
        handler.post(new Runnable(){

            @Override
            public void run() {
                Toast.makeText(JPrinterConnectService.this, "Conectado", Toast.LENGTH_SHORT).show();
//                if(Utilidades.getImpresora(JPrinterConnectService.this, address).equals("")){
//
//                    //Main2Activity.abrirDialogGuardarPrinter(address);
//
//                }

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
//            if(jPrinterBluetoothSingleton.getmBt().IsOpened())
//                jPrinterBluetoothSingleton.mBtClose();
            if(mBt.IsOpened()) {
                mBt.Close();
            }
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
                detenidoPresionandoBotonDesconectar = true;
                stopSelf();
            }
        });
    }

}
