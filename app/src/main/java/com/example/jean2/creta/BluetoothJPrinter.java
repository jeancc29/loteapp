package com.example.jean2.creta;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lvrenyang.io.BTPrinting;
import com.lvrenyang.io.IOCallBack;
import com.lvrenyang.io.Pos;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class BluetoothJPrinter {
    BluetoothAdapter adaptador;
    private static final int REQUEST_ENABLE_BT = 0;
    private LinearLayout linearlayoutdevices;
    private ProgressBar progressBarSearchStatus;

    private BroadcastReceiver broadcastReceiver = null;
    private IntentFilter intentFilter = null;

    Button btnSearch,btnDisconnect,btnPrint;


    ExecutorService es = Executors.newScheduledThreadPool(30);
    Pos mPos = new Pos();
    BTPrinting mBt = new BTPrinting();

    private static String TAG = "SearchBTActivity";


}
