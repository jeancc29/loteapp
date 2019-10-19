package com.example.jean2.creta;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jean2.creta.Servicios.JPrinterConnectService;
import com.example.jean2.creta.Servicios.PrinterService;
import com.lvrenyang.io.BTPrinting;
import com.lvrenyang.io.IOCallBack;
import com.lvrenyang.io.Pos;
//import com.mazenrashed.printooth.Printooth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluetoothSearchDialog extends AppCompatDialogFragment implements View.OnClickListener, IOCallBack {

    static Context mContext;
    BluetoothAdapter adaptador;
    private static final int REQUEST_ENABLE_BT = 0;
    private static LinearLayout linearlayoutdevices;
    private ProgressBar progressBarSearchStatus;
    private BroadcastReceiver broadcastReceiver = null;
    private IntentFilter intentFilter = null;
    Button btnSearch,btnDisconnect,btnPrint, btnConnected;
    Context mActivity;
    ExecutorService es = Executors.newScheduledThreadPool(30);
    static JPrinterBluetoothSingleton jPrinterBluetoothSingleton = JPrinterBluetoothSingleton.getInstance();
//    BTPrinting mBt = new BTPrinting();
//        static Pos mPos = new Pos();
        static Pos mPos = JPrinterConnectService.mPos;
        BTPrinting mBt = jPrinterBluetoothSingleton.getmBt();
    public static Activity mActivity1;
    private static String TAG = "SearchBTActivity";
    Handler handler;
    String [] dispostivosPareados;
    boolean seEstaPareandoElDispositivoDesdeEstaApp = false;
    String nombreDipositivoPareado;
    String addressDipositivoPareado;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_searchbluetooth, null);
        mActivity1 = getActivity();
    //    progressBar = (ProgressBar)view.findViewById(R.id.progressBarPrincipal);
        progressBarSearchStatus = (ProgressBar) view.findViewById(R.id.progressBarSearchStatus);
        linearlayoutdevices = (LinearLayout) view.findViewById(R.id.linearlayoutdevices);

        handler = new Handler();
        btnSearch = (Button) view.findViewById(R.id.buttonSearch);
        btnDisconnect = (Button) view.findViewById(R.id.buttonDisconnect);
        btnPrint = (Button) view.findViewById(R.id.buttonPrint);
        btnSearch.setOnClickListener(this);
        btnDisconnect.setOnClickListener(this);
        btnPrint.setOnClickListener(this);
        btnSearch.setEnabled(true);
        btnDisconnect.setEnabled(true);
        btnPrint.setEnabled(false);


//        Printooth.INSTANCE.init(mContext);



//        mPos.Set(mBt);
//        mBt.SetCallBack(this);

        //Probar: En vez de utilizar runOnUiThreath usar handler en este archivo para ver si funciona
        //Probar: Copiar todos los datos de SearchBTActivity al archivo JPrinterConnectService





        builder.setView(view)
                .setTitle("Duplicar ticket")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //String codigo = editTextCodigoBarra.getText().toString();
                        //listener.setCodigoBarra(codigo);
                    }
                });



//        initBroadcast();

        if(JPrinterConnectService.isPrinterConnected()){

            Log.d(TAG, "onCreateDialog: " +String.valueOf(jPrinterBluetoothSingleton.getName()));
            mostrarBotonConectado();

        }else{

            btnSearch.performClick();
        }


        return builder.create();
    }

    @Override
    public void onResume() {
        es.isShutdown();
        super.onResume();
    }

    public void getPairedDevice()
    {
        adaptador = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> mPairedDevices = adaptador.getBondedDevices();
        linearlayoutdevices.removeAllViews();
        if (mPairedDevices.size() > 0)
        {
            dispostivosPareados = new String[mPairedDevices.size()];
            int contador = 0;
            for (BluetoothDevice mDevice : mPairedDevices)
            {
                dispostivosPareados[contador] = mDevice.getAddress();
                contador++;
                addButtonToLinearLayout(mDevice.getName(), mDevice.getAddress());
            }
        }
        else
        {
            dispostivosPareados = null;
//            String mNoDevices = "None Paired";//getResources().getText(R.string.none_paired).toString();
//            mPairedDevicesArrayAdapter.add(mNoDevices);
        }
    }

    public void addButtonToLinearLayout(String name, String address)
    {
        Button button = new Button(mContext);
        button.setText(name + ": " + address);
        button.setGravity(android.view.Gravity.CENTER_VERTICAL
                | Gravity.LEFT);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //Toast.makeText(mContext, "Conectando...", Toast.LENGTH_SHORT).show();
                Log.d("pruebaBluetooth", String.valueOf((getActivity() == null)));
//                btnSearch.setEnabled(false);
               // linearlayoutdevices.setEnabled(false);
                for(int i = 0; i < linearlayoutdevices.getChildCount(); ++i)
                {
                    Button btn = (Button)linearlayoutdevices.getChildAt(i);
                    btn.setEnabled(false);
                }
                btnDisconnect.setEnabled(true);
                btnPrint.setEnabled(true);

                /************************ AQUI SE INICIA EL SERVICIO PARA CONECTARSE AL PRINTER **************************/
//                try{
//                    Intent serviceIntent = new Intent(getActivity(), JPrinterConnectService.class);
//                    serviceIntent.putExtra("address", address);
//                    serviceIntent.putExtra("name", name);
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                        mContext.startForegroundService(serviceIntent);
//                    else
//                        mContext.startService(serviceIntent);
//                }catch (Exception e){
//                    Toast.makeText(mContext, "Error servicio: " + e.toString(), Toast.LENGTH_LONG).show();
//                    e.printStackTrace();
//                }



                if(existeEntreLosDispositivosPareados(address))
                    abrirDialogGuardarPrinter(name, address);
                else{
                    emparejarDispositivo(name, address);
//                    if(emparejarDispositivo(name, address)){
//                        abrirDialogGuardarPrinter(name, address);
//                    }else{
//                        Toast.makeText(mActivity, "No se pudo parear el dispostivo", Toast.LENGTH_SHORT).show();
//                    }
                }
            }
        });
        button.getBackground().setAlpha(100);
        linearlayoutdevices.addView(button);
    }

    public boolean emparejarDispositivo(String name, String address)
    {
        Boolean returnValue;
        try {
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mContext.registerReceiver(broadcastReceiverToPairDevice, intentFilter);
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
            Class class1 =Class.forName("android.bluetooth.BluetoothDevice");
            Method createBondMethod = class1.getMethod("createBond");
            returnValue = (Boolean) createBondMethod.invoke(mBluetoothDevice);
            //abrirDialogGuardarPrinter(name, address);
            seEstaPareandoElDispositivoDesdeEstaApp = true;
            nombreDipositivoPareado = name;
            addressDipositivoPareado = address;

        }catch (Exception e)
        {
            returnValue = false;
            e.printStackTrace();
        }

        return returnValue;
    }


    public  void abrirDialogGuardarPrinter(final String name, final String address){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        Utilidades.eliminarImpresoras(mContext);
                        Utilidades.guardarImpresora(mContext, name, address);
                        BluetoothDevices.mostrarCardView();
                        Toast.makeText(mContext, "Se ha guardado correctamente", Toast.LENGTH_SHORT).show();
                        getDialog().dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setMessage("Desea guardar impresora ?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private final BroadcastReceiver broadcastReceiverToPairDevice = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    if(seEstaPareandoElDispositivoDesdeEstaApp){
                        seEstaPareandoElDispositivoDesdeEstaApp = false;
                        abrirDialogGuardarPrinter(nombreDipositivoPareado, addressDipositivoPareado);
                    }
                }
                else if(state == BluetoothDevice.BOND_NONE){
                    seEstaPareandoElDispositivoDesdeEstaApp = false;
                }

                Log.d("broadcastReceiverToPair", "state:" + (state == BluetoothDevice.BOND_NONE));

            }
        }
    };

    public boolean existeEntreLosDispositivosPareados(String address)
    {
        boolean existe = false;
        if(dispostivosPareados == null)
            existe = false;

        if(dispostivosPareados.length > 0){
            for(int c = 0; c < dispostivosPareados.length; c++){
                if(dispostivosPareados[c].equals(address)){
                    existe = true;
                }
            }
        }else{
            existe = false;
        }

        return existe;
    }

    public void mostrarBotonConectado(){
        btnSearch.setEnabled(false);
        Button button = new Button(mContext);
        button.setText(jPrinterBluetoothSingleton.getName() + ": " + jPrinterBluetoothSingleton.getAddress());

        final String nombre = jPrinterBluetoothSingleton.getName();

        for(int i = 0; i < linearlayoutdevices.getChildCount(); ++i)
        {
            Button btn = (Button)linearlayoutdevices.getChildAt(i);
            if(btn.getText().equals(button.getText()))
            {
                return;
            }
        }

        button.setGravity(android.view.Gravity.CENTER_VERTICAL
                | Gravity.LEFT);
//        button.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View arg0) {
//                // TODO Auto-generated method stub
//                Toast.makeText(getActivity(), "Connecting...", Toast.LENGTH_SHORT).show();
//                Log.d("pruebaBluetooth", String.valueOf((getActivity() == null)));
//                btnSearch.setEnabled(false);
//                linearlayoutdevices.setEnabled(false);
//                for(int i = 0; i < linearlayoutdevices.getChildCount(); ++i)
//                {
//                    Button btn = (Button)linearlayoutdevices.getChildAt(i);
//                    btn.setEnabled(false);
//                }
//                btnDisconnect.setEnabled(true);
//                btnPrint.setEnabled(true);
//                es.submit(new TaskOpen(mBt,jPrinterBluetoothSingleton.getAddress(), nombre, getActivity()));
//                //es.submit(new TaskTest(mPos, mBt, address, mActivity));
//            }
//        });
        button.setEnabled(false);
        button.getBackground().setAlpha(100);
        linearlayoutdevices.addView(button);
    }

    @Override
    public void onPause() {
        //uninitBroadcast();
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        mActivity1 = getActivity();
        mActivity = getActivity();
        super.onAttach(context);
        //try {
        //listener = (DuplicarDialog.DuplicarDialogListener) context;
        Log.e("DuplicarDialog", "onAttach:");
//        }catch (ClassCastException e){
//            Log.e("DuplicarDialog", "onAttachError: " + e.toString());
////            throw new ClassCastException(context.toString() + "Must implement DuplicarDialogListener");
//            e.printStackTrace();
//        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            uninitBroadcast();
        }catch (Exception e){
            e.printStackTrace();

        }
       
        //btnDisconnect.performClick();
    }



    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.buttonSearch: {
                initBroadcast();
                if(JPrinterConnectService.isPrinterConnected() == false){
                    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                    if (null == adapter) {
                        return;

                    }


                    if (!adapter.isEnabled()) {
                        if (adapter.enable()) {
                            while (!adapter.isEnabled())
                                ;
                            Log.v(TAG, "Enable BluetoothAdapter");
                        } else {
                            return;

                        }
                    }

                    adapter.cancelDiscovery();
                    linearlayoutdevices.removeAllViews();
                    adapter.startDiscovery();
                }
                //miBT();
                break;
            }

            case R.id.buttonDisconnect:
                //es.submit(new TaskOpen(mBt,jPrinterBluetoothSingleton.getAddress(), jPrinterBluetoothSingleton.getName(), getActivity()));

                mContext.stopService(new Intent(getActivity(), JPrinterConnectService.class));
                // jPrinterBluetoothSingleton.Disconnect();
                btnSearch.setEnabled(true);
                btnSearch.performClick();

//                es.submit(new TaskClose(mBt));
                break;

            case R.id.buttonPrint:
                //btnPrint.setEnabled(false);
                es.submit(new TaskPrint());
                break;
        }
    }

    private void initBroadcast() {
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String action = intent.getAction();
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                getPairedDevice();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    Log.d("Bluetoothsearch:",String.valueOf(device == null) );
                    if (device == null)
                        return;
                    final String address = device.getAddress();
                    String name = device.getName();
                    if (name == null)
                        name = "BT";
                    else if (name.equals(address))
                        name = "BT";
                    Button button = new Button(context);
                    button.setText(name + ": " + address);

                    final String nombre = name;

                    Log.d("DentroBroadcast", "klk");

                    for(int i = 0; i < linearlayoutdevices.getChildCount(); ++i)
                    {
                        Button btn = (Button)linearlayoutdevices.getChildAt(i);
                        if(btn.getText().equals(button.getText()))
                        {
                            return;
                        }
                    }


                    Log.d("DentroSearch", "dentro");
//                    button.setGravity(android.view.Gravity.CENTER_VERTICAL
//                            | Gravity.LEFT);
//                    button.setOnClickListener(new View.OnClickListener() {
//
//                        public void onClick(View arg0) {
//                            // TODO Auto-generated method stub
//                            Toast.makeText(mContext, "Conectando...", Toast.LENGTH_SHORT).show();
//                            Log.d("pruebaBluetooth", String.valueOf((getActivity() == null)));
//                            btnSearch.setEnabled(false);
//                            linearlayoutdevices.setEnabled(false);
//                            for(int i = 0; i < linearlayoutdevices.getChildCount(); ++i)
//                            {
//                                Button btn = (Button)linearlayoutdevices.getChildAt(i);
//                                btn.setEnabled(false);
//                            }
//                            btnDisconnect.setEnabled(true);
//                            btnPrint.setEnabled(true);
//
//                            try{
//                                Intent serviceIntent = new Intent(getActivity(), JPrinterConnectService.class);
//                                serviceIntent.putExtra("address", address);
//                                serviceIntent.putExtra("name", nombre);
//                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//                                    mContext.startForegroundService(serviceIntent);
//                                else
//                                    mContext.startService(serviceIntent);
//                            }catch (Exception e){
//                                Toast.makeText(mContext, "Error servicio: " + e.toString(), Toast.LENGTH_LONG).show();
//                                e.printStackTrace();
//                            }
//
//
////                            es.submit(new TaskOpen(mBt,address, nombre, getActivity()));
////                            jPrinterBluetoothSingleton.mBtOpen(mContext);
//                            //es.submit(new TaskTest(mPos, mBt, address, mActivity));
//                        }
//                    });
//                    button.getBackground().setAlpha(100);
//                    linearlayoutdevices.addView(button);
                    if(existeEntreLosDispositivosPareados(address) == false)
                        addButtonToLinearLayout(name, address);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
                        .equals(action)) {
                    progressBarSearchStatus.setIndeterminate(true);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                        .equals(action)) {
                    progressBarSearchStatus.setIndeterminate(false);
                }

            }

        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    private void uninitBroadcast() {
        if (broadcastReceiver != null)
            getActivity().unregisterReceiver(broadcastReceiver);
    }

    public static boolean isPrinterConnected(){
        return mPos.GetIO().IsOpened();
    }



    public static int contarJugadasDeLoteria(String idLoteria, JSONArray jsonArrayJugadas) {
        int contadorJugadas = 0;
        for (int contadorCicleJugadas = 0; contadorCicleJugadas < jsonArrayJugadas.length(); contadorCicleJugadas++) {
            try {
                JSONObject jugada = jsonArrayJugadas.getJSONObject(contadorCicleJugadas);

                if (jugada.getString("idLoteria").equals(idLoteria))
                    contadorJugadas++;

            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }

        }

        return contadorJugadas;
    }

    public static JSONArray jugadasPertenecientesALoteria(String idLoteria, JSONArray jsonArrayJugadas, boolean soloJugadasPendientes) {
        int contadorJugadas = 0;
        JSONArray jsonArrayJugadasRetornar = new JSONArray();
        for (int contadorCicleJugadas = 0; contadorCicleJugadas < jsonArrayJugadas.length(); contadorCicleJugadas++) {
            try {
                JSONObject jugada = jsonArrayJugadas.getJSONObject(contadorCicleJugadas);

                if(soloJugadasPendientes){
                    if (jugada.getString("idLoteria").equals(idLoteria) && jugada.getString("status").equals("0"))
                        jsonArrayJugadasRetornar.put(jugada);
                }else{
                    if (jugada.getString("idLoteria").equals(idLoteria))
                        jsonArrayJugadasRetornar.put(jugada);
                }


            } catch (Exception e) {
                e.printStackTrace();
                return jsonArrayJugadasRetornar;
            }

        }

        return jsonArrayJugadasRetornar;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode,  Intent data){
        if(resultCode == Activity.RESULT_OK){
           // miBT();
        }
    }

    public class TaskTest implements Runnable
    {
        Pos pos = null;
        BTPrinting bt = null;
        String address = null;
        Context context = null;

        public TaskTest(Pos pos, BTPrinting bt, String address, Context context)
        {
            this.pos = pos;
            this.bt = bt;
            this.address = address;
            this.context = context;
            pos.Set(bt);
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            for(int i = 0; i < 5; ++i)
            {
                long beginTime = System.currentTimeMillis();
                if(bt.Open(address,context))
                {
                    long endTime = System.currentTimeMillis();
                    pos.POS_S_Align(0);
                    pos.POS_S_TextOut(i+ " " + "Open   UsedTime:" + (endTime - beginTime) + "\r\n", 0, 0, 0, 0, 0);
                    beginTime = System.currentTimeMillis();
                    boolean ticketResult = pos.POS_TicketSucceed(i, 30000);
                    endTime = System.currentTimeMillis();
                    pos.POS_S_TextOut(i+ " " + "Ticket UsedTime:" + (endTime - beginTime) + " " + (ticketResult ? "Succeed" : "Failed") +  "\r\n", 0, 0, 0, 0, 0);
                    pos.POS_Beep(1, 500);
                    bt.Close();
                }
            }
        }
    }

    public class TaskOpen implements Runnable
    {
        BTPrinting bt = null;
        String address = null;
        String name = null;
        Context context = null;

        public TaskOpen(BTPrinting bt, String address, String name, Context context)
        {
            this.bt = bt;
            this.address = address;
            this.name = name;
            this.context = context;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            jPrinterBluetoothSingleton.setName(name);
            jPrinterBluetoothSingleton.setAddress(address);
            Log.d("PruebaOpen", String.valueOf(jPrinterBluetoothSingleton.getmBt().IsOpened()));
            //Toast.makeText(context, "Abierta: " + jPrinterBluetoothSingleton.getmBt().IsOpened(), Toast.LENGTH_LONG).show();
            if(jPrinterBluetoothSingleton.getmBt().IsOpened() == false)
                jPrinterBluetoothSingleton.mBtOpen(context);
//            bt.Open(address,context);
        }
    }

    static int dwWriteIndex = 1;
    public static class TaskPrint implements Runnable
    {
        Pos pos = null;
        Bitmap ticketImg;
        JSONObject response;
        boolean original;
        boolean cancelado;
        boolean pagado;

        public TaskPrint(Pos pos)
        {
            this.pos = pos;
        }
        public TaskPrint()
        {
            this.pos = mPos;
        }

        public TaskPrint(Bitmap ticketImg)
        {
            this.pos = mPos;
            this.ticketImg = ticketImg;
        }

        public TaskPrint(JSONObject response, boolean original)
        {
            this.pos = mPos;
            this.response = response;
            this.original = original;
        }

        public TaskPrint(JSONObject response, int cancelado_o_pagado)
        {
            this.pos = mPos;
            this.response = response;

            if(cancelado_o_pagado == 1)
                this.cancelado = true;
            else{
                this.pagado = true;
                this.original = true;
            }
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub

            final boolean bPrintResult = PrintTicket(AppStart.nPrintWidth, AppStart.bCutter, AppStart.bDrawer, AppStart.bBeeper, AppStart.nPrintCount, AppStart.nPrintContent, AppStart.nCompressMethod, AppStart.bCheckReturn);
            final boolean bIsOpened = pos.GetIO().IsOpened();

            mActivity1.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(mActivity1.getApplicationContext(), bPrintResult ? mActivity1.getResources().getString(R.string.printsuccess) : mActivity1.getResources().getString(R.string.printfailed), Toast.LENGTH_SHORT).show();
                    //btnPrint.setEnabled(bIsOpened);
                }
            });

        }


        public boolean PrintTicket(int nPrintWidth, boolean bCutter, boolean bDrawer, boolean bBeeper, int nCount, int nPrintContent, int nCompressMethod, boolean bCheckReturn)
        {
            boolean bPrintResult = false;

            byte[] status = new byte[1];

            if(!bCheckReturn || (bCheckReturn && pos.POS_QueryStatus(status, 3000, 2)))
            {
                Bitmap bm1 = getTestImage1(nPrintWidth, nPrintWidth);
                Bitmap bm2 = getTestImage2(nPrintWidth, nPrintWidth);
                Bitmap bmBlackWhite = getImageFromAssetsFile("blackwhite.png");
                Bitmap bmIu = getImageFromAssetsFile("iu.jpeg");
                Bitmap bmYellowmen = getImageFromAssetsFile("yellowmen.png");

//                pos.POS_FeedLine();
//                        pos.POS_S_Align(1);
//                        pos.POS_S_TextOut("REC" + String.format("%03d", 12) + "\r\nCaysn Printer\r\n测试页\r\n\r\n", 0, 1, 1, 0, 0x100);
//                        pos.POS_S_TextOut("扫二维码下载苹果APP\r\n", 0, 0, 0, 0, 0x100);
//                        pos.POS_S_SetQRcode("https://appsto.re/cn/2KF_bb.i", 8, 0, 3);
//                        pos.POS_FeedLine();
//                        pos.POS_S_SetBarcode("20160618", 0, 72, 3, 60, 0, 2);
//                        pos.POS_FeedLine();

                pos.POS_FeedLine();
//                        pos.POS_S_Align(1);
//                        pos.POS_S_TextOut("BANCA 1\n", 0, 1, 1, 0, 0x00);
//                        pos.POS_S_TextOut("** ORIGINAL **\n", 0, 1, 1, 0, 0x00);
//                        pos.POS_S_TextOut("2019/07/05 4:42 PM\n", 0, 0, 1, 0, 0x00);
//                        pos.POS_S_TextOut("Ticket: 000000050\n", 0, 0, 1, 0, 0x00);
//                        pos.POS_S_TextOut("Fecha: 2019/07/05 4:42 PM\n", 0, 0, 1, 0, 0x00);
//                        pos.POS_S_TextOut("1401831804\n", 1, 1, 1, 0, 0x00);
//                        pos.POS_S_TextOut("---------------\n", 1, 1, 1, 0, 0x00);
//                        pos.POS_S_TextOut("NACIONAL: 2\n", 1, 1, 1, 0, 0x00);
//                        pos.POS_S_TextOut("---------------\n", 1, 1, 1, 0, 0x00);
//                        pos.POS_FeedLine();
//                        pos.POS_S_TextOut("JUGADA  MONTO  JUGADA  MONTO\n", 1, 0, 1, 0, 0x00);
//                        pos.POS_S_TextOut("03-20   12.00 09-98-03 9.00", 1, 0, 1, 0, 0x00);
//                        pos.POS_FeedLine();
//                        pos.POS_FeedLine();
                //pos.POS_S_SetQRcode("https://appsto.re/cn/2KF_bb.i", 8, 0, 3);
//                        pos.POS_FeedLine();



                //pos.POS_PrintPicture(this.ticketImg, nPrintWidth, 1, nCompressMethod);
                //if(!pos.GetIO().IsOpened())
//                        break;

                try {
                    JSONObject venta = response.getJSONObject("venta");
                    JSONArray jsonArrayLoterias = venta.getJSONArray("loterias");
                    JSONArray jsonArrayJugadas = venta.getJSONArray("jugadas");

                    if(jsonArrayLoterias.length() == 0)
                        return false;
                    if(jsonArrayJugadas.length() == 0)
                        return false;


                    PrinterService.POS_S_Align(1);
                    PrinterService.POS_S_TextOut(venta.getJSONObject("banca").getString("descripcion")+"\n", 0, 1, 1, 0, 0x00);
                    if(this.original == true && this.cancelado == false)
                        PrinterService.POS_S_TextOut("** ORIGINAL **\n", 0, 1, 1, 0, 0x00);
                    else if(this.original == false && this.cancelado == false)
                        PrinterService.POS_S_TextOut("** COPIA **\n", 0, 1, 1, 0, 0x00);
                    else if(this.original == false && this.cancelado == true)
                        PrinterService.POS_S_TextOut("** CANCELADO **\n", 0, 1, 1, 0, 0x00);

                    PrinterService.POS_S_TextOut(venta.getString("fecha")+"\n", 0, 0, 1, 0, 0x00);
                    PrinterService.POS_S_TextOut("Ticket:"  +Utilidades.toSecuencia(venta.getString("idTicket"), venta.getString("codigo"))+ "\n", 0, 0, 1, 0, 0x00);
                    PrinterService.POS_S_TextOut("Fecha: " + venta.getString("fecha")+"\n", 0, 0, 1, 0, 0x00);
                    if(this.original == true && this.cancelado == false)
                        PrinterService.POS_S_TextOut(venta.getString("codigoBarra")+"\n", 1, 1, 1, 0, 0x00);

                    for(int i=0; i < jsonArrayLoterias.length(); i++){
//                        if(!pos.GetIO().IsOpened())
//                            break;
                        JSONObject loteria = jsonArrayLoterias.getJSONObject(i);
                        boolean esPrimeraJugadaAInsertar = true;
                        JSONArray jugadas = jugadasPertenecientesALoteria(loteria.getString("id"), jsonArrayJugadas, pagado);
                        if(jugadas.length() == 0)
                            continue;
                        for (int contadorCicleJugadas =0; contadorCicleJugadas < jugadas.length(); contadorCicleJugadas++){

//                            if(!pos.GetIO().IsOpened())
//                                break;
                            JSONObject jugada = jugadas.getJSONObject(contadorCicleJugadas);
                            Log.d("BluetoothSearchDia", "Print: " + jugada.getString("jugada") + " - " + Utilidades.agregarGuionPorSorteo(jugada.getString("jugada"), jugada.getString("sorteo")) +jugada.getString("sorteo"));

                            if(contadorCicleJugadas == 0){
                                PrinterService.POS_S_TextOut("---------------\n", 1, 1, 1, 0, 0x00);
                                PrinterService.POS_S_TextOut(loteria.getString("descripcion") + "\n", 1, 0, 1, 0, 0x00);
                                PrinterService.POS_S_TextOut("---------------\n", 1, 1, 1, 0, 0x00);
                            }
                            if(jugada.getString("idLoteria").equals(loteria.getString("id"))){
                                PrinterService.POS_S_Align(0);
                                if(esPrimeraJugadaAInsertar){
                                    PrinterService.POS_S_TextOut("JUGADA   MONTO  JUGADA   MONTO\n", 1, 0, 1, 0, 0x00);
                                    esPrimeraJugadaAInsertar = false;
                                }
                                if(((contadorCicleJugadas + 1) % 2) == 0){
                                    Log.d("cjPar", String.valueOf(contadorCicleJugadas));
                                    PrinterService.POS_S_TextOut("                " + Utilidades.agregarGuion(Utilidades.agregarGuionPorSorteo(jugada.getString("jugada"), jugada.getString("sorteo"))), 1, 0, 1, 0, 0x00);
                                    PrinterService.POS_S_TextOut("                         " + jugada.getDouble("monto") + "\n", 1, 0, 1, 0, 0x00);
                                }else{
                                    String saltoLinea = "";
                                    if((contadorCicleJugadas + 1) == jugadas.length())
                                        saltoLinea = "\n";
                                    PrinterService.POS_S_TextOut(Utilidades.agregarGuion(Utilidades.agregarGuionPorSorteo(jugada.getString("jugada"), jugada.getString("sorteo"))), 0, 0, 1, 0, 0x00);
                                    PrinterService.POS_S_TextOut("         " + jugada.getDouble("monto") + saltoLinea, 0, 0, 1, 0, 0x00);
                                }

//                                pos.POS_S_TextOut("culo23", 0, 0, 1, 0, 0x00);
                                //pos.POS_S_TextOut(" - total: " + getLoteriaTotal(loteria.getInt("id"), jugadas) + "-\n", 1, 0, 1, 0, 0x00);

                            }
                        }
                        PrinterService.POS_S_Align(1);
                        if(jsonArrayLoterias.length() > 1)
                            PrinterService.POS_S_TextOut("\n total: " + getLoteriaTotal(loteria.getString("id"), jugadas) + "\n\n\n", 1, 0, 1, 0, 0x00);
                    }

                    double total = venta.getDouble("total");
                    if(venta.getInt("hayDescuento") == 1){
                        total -= venta.getDouble("descuentoMonto");
                        PrinterService.POS_S_TextOut("subTotal: " + venta.getDouble("total") + "\n", 1, 0, 1, 0, 0x00);
                        PrinterService.POS_S_TextOut("descuento: " + venta.getDouble("descuentoMonto") + "\n", 1, 0, 1, 0, 0x00);
                    }
                    String saltoLineaTotal = "\n";
                    if(this.original == false || venta.getJSONObject("banca").getInt("imprimirCodigoQr") == 0){
                        saltoLineaTotal+="\n\n";
                    }
                    PrinterService.POS_S_TextOut("- TOTAL: " + total + " -" + saltoLineaTotal, 1, 0, 1, 0, 0x00);

                    if(this.original == false && this.cancelado == true){
                        PrinterService.POS_S_TextOut("** CANCELADO **\n\n\n", 0, 1, 1, 0, 0x00);
                    }

                    if(this.cancelado == false && this.original == true){
                        if(!venta.getJSONObject("banca").getString("piepagina1").equals("null"))
                            PrinterService.POS_S_TextOut(venta.getJSONObject("banca").getString("piepagina1") + "\n", 1, 0, 1, 0, 0x00);
                        if(!venta.getJSONObject("banca").getString("piepagina2").equals("null"))
                            PrinterService.POS_S_TextOut(venta.getJSONObject("banca").getString("piepagina2") + "\n", 1, 0, 1, 0, 0x00);
                        if(!venta.getJSONObject("banca").getString("piepagina3").equals("null"))
                            pos.POS_S_TextOut(venta.getJSONObject("banca").getString("piepagina3") + "\n", 1, 0, 1, 0, 0x00);
                        if(!venta.getJSONObject("banca").getString("piepagina4").equals("null"))
                            PrinterService.POS_S_TextOut(venta.getJSONObject("banca").getString("piepagina4") + "\n", 1, 0, 1, 0, 0x00);
                        if(venta.getJSONObject("banca").getInt("imprimirCodigoQr") == 1)
                            PrinterService.POS_S_SetQRcode(venta.getString("codigoQr"), 8, 0, 3);
                        PrinterService.POS_S_TextOut("\n\n\n", 1, 0, 1, 0, 0x00);
                        PrinterService.closeSocket(null, false);
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }



//                for(int i = 0; i < nCount; ++i)
//                {
//                    if(!pos.GetIO().IsOpened())
//                        break;
//
//                    Log.d("nPrintWidth", String.valueOf(nPrintWidth));
//                    Log.d("nPrintContent", String.valueOf(nPrintContent));
//
//                    if(1 >= 1)
//                    {
//                        pos.POS_FeedLine();
//                        pos.POS_S_Align(1);
//                        pos.POS_S_TextOut("REC" + String.format("%03d", i) + "\r\nCaysn Printer\r\n测试页\r\n\r\n", 0, 1, 1, 0, 0x100);
//                        pos.POS_S_TextOut("扫二维码下载苹果APP\r\n", 0, 0, 0, 0, 0x100);
//                        pos.POS_S_SetQRcode("https://appsto.re/cn/2KF_bb.i", 8, 0, 3);
//                        pos.POS_FeedLine();
//                        pos.POS_S_SetBarcode("20160618", 0, 72, 3, 60, 0, 2);
//                        pos.POS_FeedLine();
//
//                        //Bitmap t = Utilidades.toBitmap(getString());
//
//                        //pos.POS_PrintPicture(bm1, nPrintWidth, 1, nCompressMethod);
//
//                    }
//
//                    if(nPrintContent >= 2)
//                    {
//                        if(bm1 != null)
//                        {
//                            pos.POS_PrintPicture(bm1, nPrintWidth, 1, nCompressMethod);
//                        }
//                        if(bm2 != null)
//                        {
//                            pos.POS_PrintPicture(bm2, nPrintWidth, 1, nCompressMethod);
//                        }
//                    }
//
//                    if(nPrintContent >= 3)
//                    {
//                        if(bmBlackWhite != null)
//                        {
//                            pos.POS_PrintPicture(bmBlackWhite, nPrintWidth, 1, nCompressMethod);
//                        }
//                        if(bmIu != null)
//                        {
//                            pos.POS_PrintPicture(bmIu, nPrintWidth, 0, nCompressMethod);
//                        }
//                        if(bmYellowmen != null)
//                        {
//                            pos.POS_PrintPicture(bmYellowmen, nPrintWidth, 0, nCompressMethod);
//                        }
//                    }
//                }


            }

            return bPrintResult;
        }

        double getLoteriaTotal(String id, JSONArray jugadas){
            double total = 0;
            try {
                for (int i =0; i < jugadas.length(); i++){
                    JSONObject jugada = jugadas.getJSONObject(i);

                    if(jugada.getString("idLoteria").equals(id)){
                        total += jugada.getDouble("monto");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return total;
        }
    }
    public static class TaskPrint2 implements Runnable
    {
        Pos pos = null;
        Bitmap ticketImg;
        JSONObject response;
        boolean original;
        boolean cancelado;
        boolean pagado;

        public TaskPrint2(Pos pos)
        {
            this.pos = pos;
        }
        public TaskPrint2()
        {
            this.pos = mPos;
        }

        public TaskPrint2(Bitmap ticketImg)
        {
            this.pos = mPos;
            this.ticketImg = ticketImg;
        }

        public TaskPrint2(JSONObject response, boolean original)
        {
            this.pos = mPos;
            this.response = response;
            this.original = original;
        }

        public TaskPrint2(JSONObject response, int cancelado_o_pagado)
        {
            this.pos = mPos;
            this.response = response;

            if(cancelado_o_pagado == 1)
                this.cancelado = true;
            else{
                this.pagado = true;
                this.original = true;
            }
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub

            final boolean bPrintResult = PrintTicket(AppStart.nPrintWidth, AppStart.bCutter, AppStart.bDrawer, AppStart.bBeeper, AppStart.nPrintCount, AppStart.nPrintContent, AppStart.nCompressMethod, AppStart.bCheckReturn);
            final boolean bIsOpened = pos.GetIO().IsOpened();

            mActivity1.runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(mActivity1.getApplicationContext(), bPrintResult ? mActivity1.getResources().getString(R.string.printsuccess) : mActivity1.getResources().getString(R.string.printfailed), Toast.LENGTH_SHORT).show();
                    //btnPrint.setEnabled(bIsOpened);
                }
            });

        }


        public boolean PrintTicket(int nPrintWidth, boolean bCutter, boolean bDrawer, boolean bBeeper, int nCount, int nPrintContent, int nCompressMethod, boolean bCheckReturn)
        {
            boolean bPrintResult = false;

            byte[] status = new byte[1];

            if(!bCheckReturn || (bCheckReturn && pos.POS_QueryStatus(status, 3000, 2)))
            {
                Bitmap bm1 = getTestImage1(nPrintWidth, nPrintWidth);
                Bitmap bm2 = getTestImage2(nPrintWidth, nPrintWidth);
                Bitmap bmBlackWhite = getImageFromAssetsFile("blackwhite.png");
                Bitmap bmIu = getImageFromAssetsFile("iu.jpeg");
                Bitmap bmYellowmen = getImageFromAssetsFile("yellowmen.png");

//                pos.POS_FeedLine();
//                        pos.POS_S_Align(1);
//                        pos.POS_S_TextOut("REC" + String.format("%03d", 12) + "\r\nCaysn Printer\r\n测试页\r\n\r\n", 0, 1, 1, 0, 0x100);
//                        pos.POS_S_TextOut("扫二维码下载苹果APP\r\n", 0, 0, 0, 0, 0x100);
//                        pos.POS_S_SetQRcode("https://appsto.re/cn/2KF_bb.i", 8, 0, 3);
//                        pos.POS_FeedLine();
//                        pos.POS_S_SetBarcode("20160618", 0, 72, 3, 60, 0, 2);
//                        pos.POS_FeedLine();

                        pos.POS_FeedLine();
//                        pos.POS_S_Align(1);
//                        pos.POS_S_TextOut("BANCA 1\n", 0, 1, 1, 0, 0x00);
//                        pos.POS_S_TextOut("** ORIGINAL **\n", 0, 1, 1, 0, 0x00);
//                        pos.POS_S_TextOut("2019/07/05 4:42 PM\n", 0, 0, 1, 0, 0x00);
//                        pos.POS_S_TextOut("Ticket: 000000050\n", 0, 0, 1, 0, 0x00);
//                        pos.POS_S_TextOut("Fecha: 2019/07/05 4:42 PM\n", 0, 0, 1, 0, 0x00);
//                        pos.POS_S_TextOut("1401831804\n", 1, 1, 1, 0, 0x00);
//                        pos.POS_S_TextOut("---------------\n", 1, 1, 1, 0, 0x00);
//                        pos.POS_S_TextOut("NACIONAL: 2\n", 1, 1, 1, 0, 0x00);
//                        pos.POS_S_TextOut("---------------\n", 1, 1, 1, 0, 0x00);
//                        pos.POS_FeedLine();
//                        pos.POS_S_TextOut("JUGADA  MONTO  JUGADA  MONTO\n", 1, 0, 1, 0, 0x00);
//                        pos.POS_S_TextOut("03-20   12.00 09-98-03 9.00", 1, 0, 1, 0, 0x00);
//                        pos.POS_FeedLine();
//                        pos.POS_FeedLine();
                        //pos.POS_S_SetQRcode("https://appsto.re/cn/2KF_bb.i", 8, 0, 3);
//                        pos.POS_FeedLine();



                //pos.POS_PrintPicture(this.ticketImg, nPrintWidth, 1, nCompressMethod);
                //if(!pos.GetIO().IsOpened())
//                        break;

                try {
                    JSONObject venta = response.getJSONObject("venta");
                    JSONArray jsonArrayLoterias = venta.getJSONArray("loterias");
                    JSONArray jsonArrayJugadas = venta.getJSONArray("jugadas");

                    if(jsonArrayLoterias.length() == 0)
                        return false;
                    if(jsonArrayJugadas.length() == 0)
                        return false;


                    pos.POS_S_Align(1);
                    pos.POS_S_TextOut(venta.getJSONObject("banca").getString("descripcion")+"\n", 0, 1, 1, 0, 0x00);
                    if(this.original == true && this.cancelado == false)
                        pos.POS_S_TextOut("** ORIGINAL **\n", 0, 1, 1, 0, 0x00);
                    else if(this.original == false && this.cancelado == false)
                        pos.POS_S_TextOut("** COPIA **\n", 0, 1, 1, 0, 0x00);
                    else if(this.original == false && this.cancelado == true)
                        pos.POS_S_TextOut("** CANCELADO **\n", 0, 1, 1, 0, 0x00);

                    pos.POS_S_TextOut(venta.getString("fecha")+"\n", 0, 0, 1, 0, 0x00);
                    pos.POS_S_TextOut("Ticket:"  +Utilidades.toSecuencia(venta.getString("idTicket"), venta.getString("codigo"))+ "\n", 0, 0, 1, 0, 0x00);
                    pos.POS_S_TextOut("Fecha: " + venta.getString("fecha")+"\n", 0, 0, 1, 0, 0x00);
                    if(this.original == true && this.cancelado == false)
                        pos.POS_S_TextOut(venta.getString("codigoBarra")+"\n", 1, 1, 1, 0, 0x00);

                    for(int i=0; i < jsonArrayLoterias.length(); i++){
                        if(!pos.GetIO().IsOpened())
                            break;
                        JSONObject loteria = jsonArrayLoterias.getJSONObject(i);
                        boolean esPrimeraJugadaAInsertar = true;
                        JSONArray jugadas = jugadasPertenecientesALoteria(loteria.getString("id"), jsonArrayJugadas, pagado);
                        if(jugadas.length() == 0)
                            continue;
                        for (int contadorCicleJugadas =0; contadorCicleJugadas < jugadas.length(); contadorCicleJugadas++){

                            if(!pos.GetIO().IsOpened())
                                break;
                            JSONObject jugada = jugadas.getJSONObject(contadorCicleJugadas);
                            Log.d("BluetoothSearchDia", "Print: " + jugada.getString("jugada") + " - " + Utilidades.agregarGuionPorSorteo(jugada.getString("jugada"), jugada.getString("sorteo")) +jugada.getString("sorteo"));

                            if(contadorCicleJugadas == 0){
                                pos.POS_S_TextOut("---------------\n", 1, 1, 1, 0, 0x00);
                                pos.POS_S_TextOut(loteria.getString("descripcion") + "\n", 1, 0, 1, 0, 0x00);
                                pos.POS_S_TextOut("---------------\n", 1, 1, 1, 0, 0x00);
                            }
                            if(jugada.getString("idLoteria").equals(loteria.getString("id"))){
                                pos.POS_S_Align(0);
                                if(esPrimeraJugadaAInsertar){
                                    pos.POS_S_TextOut("JUGADA   MONTO  JUGADA   MONTO\n", 1, 0, 1, 0, 0x00);
                                    esPrimeraJugadaAInsertar = false;
                                }
                                if(((contadorCicleJugadas + 1) % 2) == 0){
                                    Log.d("cjPar", String.valueOf(contadorCicleJugadas));
                                    pos.POS_S_TextOut("                " + Utilidades.agregarGuion(Utilidades.agregarGuionPorSorteo(jugada.getString("jugada"), jugada.getString("sorteo"))), 1, 0, 1, 0, 0x00);
                                    pos.POS_S_TextOut("                         " + jugada.getDouble("monto") + "\n", 1, 0, 1, 0, 0x00);
                                }else{
                                    String saltoLinea = "";
                                    if((contadorCicleJugadas + 1) == jugadas.length())
                                        saltoLinea = "\n";
                                    pos.POS_S_TextOut(Utilidades.agregarGuion(Utilidades.agregarGuionPorSorteo(jugada.getString("jugada"), jugada.getString("sorteo"))), 0, 0, 1, 0, 0x00);
                                    pos.POS_S_TextOut("         " + jugada.getDouble("monto") + saltoLinea, 0, 0, 1, 0, 0x00);
                                }

//                                pos.POS_S_TextOut("culo23", 0, 0, 1, 0, 0x00);
                                //pos.POS_S_TextOut(" - total: " + getLoteriaTotal(loteria.getInt("id"), jugadas) + "-\n", 1, 0, 1, 0, 0x00);

                            }
                        }
                        pos.POS_S_Align(1);
                        if(jsonArrayLoterias.length() > 1)
                            pos.POS_S_TextOut("\n total: " + getLoteriaTotal(loteria.getString("id"), jugadas) + "\n\n\n", 1, 0, 1, 0, 0x00);
                    }

                    double total = venta.getDouble("total");
                    if(venta.getInt("hayDescuento") == 1){
                        total -= venta.getDouble("descuentoMonto");
                        pos.POS_S_TextOut("subTotal: " + venta.getDouble("total") + "\n", 1, 0, 1, 0, 0x00);
                        pos.POS_S_TextOut("descuento: " + venta.getDouble("descuentoMonto") + "\n", 1, 0, 1, 0, 0x00);
                    }
                    String saltoLineaTotal = "\n";
                    if(this.original == false || venta.getJSONObject("banca").getInt("imprimirCodigoQr") == 0){
                        saltoLineaTotal+="\n\n";
                    }
                    pos.POS_S_TextOut("- TOTAL: " + total + " -" + saltoLineaTotal, 1, 0, 1, 0, 0x00);

                    if(this.original == false && this.cancelado == true){
                        pos.POS_S_TextOut("** CANCELADO **\n\n\n", 0, 1, 1, 0, 0x00);
                    }

                    if(this.cancelado == false && this.original == true){
                        if(!venta.getJSONObject("banca").getString("piepagina1").equals("null"))
                            pos.POS_S_TextOut(venta.getJSONObject("banca").getString("piepagina1") + "\n", 1, 0, 1, 0, 0x00);
                        if(!venta.getJSONObject("banca").getString("piepagina2").equals("null"))
                            pos.POS_S_TextOut(venta.getJSONObject("banca").getString("piepagina2") + "\n", 1, 0, 1, 0, 0x00);
                        if(!venta.getJSONObject("banca").getString("piepagina3").equals("null"))
                            pos.POS_S_TextOut(venta.getJSONObject("banca").getString("piepagina3") + "\n", 1, 0, 1, 0, 0x00);
                        if(!venta.getJSONObject("banca").getString("piepagina4").equals("null"))
                            pos.POS_S_TextOut(venta.getJSONObject("banca").getString("piepagina4") + "\n", 1, 0, 1, 0, 0x00);
                        if(venta.getJSONObject("banca").getInt("imprimirCodigoQr") == 1)
                            pos.POS_S_SetQRcode(venta.getString("codigoQr"), 8, 0, 3);
                        pos.POS_S_TextOut("\n\n\n", 1, 0, 1, 0, 0x00);

                    }


                }catch (Exception e){
                    e.printStackTrace();
                }



//                for(int i = 0; i < nCount; ++i)
//                {
//                    if(!pos.GetIO().IsOpened())
//                        break;
//
//                    Log.d("nPrintWidth", String.valueOf(nPrintWidth));
//                    Log.d("nPrintContent", String.valueOf(nPrintContent));
//
//                    if(1 >= 1)
//                    {
//                        pos.POS_FeedLine();
//                        pos.POS_S_Align(1);
//                        pos.POS_S_TextOut("REC" + String.format("%03d", i) + "\r\nCaysn Printer\r\n测试页\r\n\r\n", 0, 1, 1, 0, 0x100);
//                        pos.POS_S_TextOut("扫二维码下载苹果APP\r\n", 0, 0, 0, 0, 0x100);
//                        pos.POS_S_SetQRcode("https://appsto.re/cn/2KF_bb.i", 8, 0, 3);
//                        pos.POS_FeedLine();
//                        pos.POS_S_SetBarcode("20160618", 0, 72, 3, 60, 0, 2);
//                        pos.POS_FeedLine();
//
//                        //Bitmap t = Utilidades.toBitmap(getString());
//
//                        //pos.POS_PrintPicture(bm1, nPrintWidth, 1, nCompressMethod);
//
//                    }
//
//                    if(nPrintContent >= 2)
//                    {
//                        if(bm1 != null)
//                        {
//                            pos.POS_PrintPicture(bm1, nPrintWidth, 1, nCompressMethod);
//                        }
//                        if(bm2 != null)
//                        {
//                            pos.POS_PrintPicture(bm2, nPrintWidth, 1, nCompressMethod);
//                        }
//                    }
//
//                    if(nPrintContent >= 3)
//                    {
//                        if(bmBlackWhite != null)
//                        {
//                            pos.POS_PrintPicture(bmBlackWhite, nPrintWidth, 1, nCompressMethod);
//                        }
//                        if(bmIu != null)
//                        {
//                            pos.POS_PrintPicture(bmIu, nPrintWidth, 0, nCompressMethod);
//                        }
//                        if(bmYellowmen != null)
//                        {
//                            pos.POS_PrintPicture(bmYellowmen, nPrintWidth, 0, nCompressMethod);
//                        }
//                    }
//                }

                if(bBeeper)
                    pos.POS_Beep(1, 5);
                if(bCutter)
                    pos.POS_CutPaper();
                if(bDrawer)
                    pos.POS_KickDrawer(0, 100);

                if(bCheckReturn)
                {
                    int dwTicketIndex = dwWriteIndex++;
                    bPrintResult = pos.POS_TicketSucceed(dwTicketIndex, 30000);
                }
                else
                {
                    bPrintResult = pos.GetIO().IsOpened();
                }
            }

            return bPrintResult;
        }

        double getLoteriaTotal(String id, JSONArray jugadas){
            double total = 0;
            try {
                for (int i =0; i < jugadas.length(); i++){
                    JSONObject jugada = jugadas.getJSONObject(i);

                    if(jugada.getString("idLoteria").equals(id)){
                        total += jugada.getDouble("monto");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return total;
        }
    }

    public class TaskClose implements Runnable
    {
        BTPrinting bt = null;

        public TaskClose(BTPrinting bt)
        {
            this.bt = bt;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
//            if(jPrinterBluetoothSingleton.getmBt().IsOpened())
//                jPrinterBluetoothSingleton.mBtClose();
            bt.Close();
        }

    }

    @Override
    public void OnOpen() {
        // TODO Auto-generated method stub
        getActivity().runOnUiThread(new Runnable(){

            @Override
            public void run() {
                btnDisconnect.setEnabled(true);
                btnPrint.setEnabled(true);
                btnSearch.setEnabled(false);
                linearlayoutdevices.setEnabled(false);
                for(int i = 0; i < linearlayoutdevices.getChildCount(); ++i)
                {
                    Button btn = (Button)linearlayoutdevices.getChildAt(i);
                    btn.setEnabled(false);
                }
                Toast.makeText(mActivity, "Connected", Toast.LENGTH_SHORT).show();
                jPrinterBluetoothSingleton.setConnected(true);
                if(AppStart.bAutoPrint)
                {
                    btnPrint.performClick();
                }
            }
        });
    }

    @Override
    public void OnOpenFailed() {
        // TODO Auto-generated method stub
        getActivity().runOnUiThread(new Runnable(){

            @Override
            public void run() {
                btnDisconnect.setEnabled(false);
                btnPrint.setEnabled(false);
                btnSearch.setEnabled(true);
                linearlayoutdevices.setEnabled(true);
                for(int i = 0; i < linearlayoutdevices.getChildCount(); ++i)
                {
                    Button btn = (Button)linearlayoutdevices.getChildAt(i);
                    btn.setEnabled(true);
                }
                Main2Activity.conectadoAImpresoraBluetooth = false;
                Toast.makeText(mActivity, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnClose() {
        // TODO Auto-generated method stub
        getActivity().runOnUiThread(new Runnable(){

            @Override
            public void run() {

                btnDisconnect.setEnabled(false);
                btnPrint.setEnabled(false);
                btnSearch.setEnabled(true);
                linearlayoutdevices.setEnabled(true);
//                for(int i = 0; i < linearlayoutdevices.getChildCount(); ++i)
//                {
//                    Button btn = (Button)linearlayoutdevices.getChildAt(i);
//                    btn.setEnabled(true);
//                }
                btnSearch.setEnabled(true);
                btnSearch.performClick();
            }
        });
    }

    /**
     * Leer imágenes de activos
     */
    public static Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = mActivity1.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;

    }

    public static Bitmap getTestImage1(int width, int height)
    {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, width, height, paint);

        paint.setColor(Color.BLACK);
        for(int i = 0; i < 8; ++i)
        {
            for(int x = i; x < width; x += 8)
            {
                for(int y = i; y < height; y += 8)
                {
                    canvas.drawPoint(x, y, paint);
                }
            }
        }
        return bitmap;
    }

    public static Bitmap getTestImage2(int width, int height)
    {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, width, height, paint);

        paint.setColor(Color.BLACK);
        for(int y = 0; y < height; y += 4)
        {
            for(int x = y%32; x < width; x += 32)
            {
                canvas.drawRect(x, y, x+4, y+4, paint);
            }
        }
        return bitmap;
    }
}
