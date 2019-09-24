package com.example.jean2.creta;

import android.app.Activity;
import android.content.Context;

import com.lvrenyang.io.BTPrinting;
import com.lvrenyang.io.IOCallBack;
import com.lvrenyang.io.Pos;

public class JPrinterBluetoothSingleton {
    private BTPrinting mBt;
    private Pos mPos;
    private static JPrinterBluetoothSingleton jPrinterBluetoothSingleton;
    private boolean connected = false;
    private String name;
    private String address;

    private JPrinterBluetoothSingleton(){
        this.mBt = new BTPrinting();
        mPos = new Pos();
    }


    public void setCallBack(IOCallBack ioCallBack){
        mPos.Set(mBt);
        mBt.SetCallBack(ioCallBack);
    }

    public static JPrinterBluetoothSingleton getInstance(){
        if(jPrinterBluetoothSingleton == null){
            jPrinterBluetoothSingleton = new JPrinterBluetoothSingleton();
        }

        return jPrinterBluetoothSingleton;
    }

    public BTPrinting getmBt() {
        return mBt;
    }

    public Pos getmPos() {
        return mPos;
    }

    public void mBtOpen(Context context){
        mBt.Open(this.address, context);
    }
    public void mBtClose(){
        mBt.Close();
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setAddress(String address) {
        this.address = address;
    }


    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }


    public void Disconnect() {
        this.connected = false;
        this.name = "";
        this.address = "";
    }

    public boolean isConnected(){
        return this.connected;
    }




    public class TaskOpen implements Runnable
    {
//        BTPrinting bt = null;
//        String address = null;
//        String name = null;
        Context context = null;

        public TaskOpen(String address, String name, Context context)
        {
            address = address;
            name = name;
            this.context = context;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
//            jPrinterBluetoothSingleton.setName(name);
//            jPrinterBluetoothSingleton.setAddress(address);
//            bt.Open(address,context);
            mBt.Open(address, context);
        }
    }



}
