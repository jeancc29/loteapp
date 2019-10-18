package com.example.jean2.creta.Servicios;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.jean2.creta.Clases.ESCCMD;


import com.example.jean2.creta.Clases.UnicodeFormatter;
import com.example.jean2.creta.Utilidades;

public class PrinterService extends Service  {
    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    //    private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private static BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    String address;
    String name;
    static int intentosDeConeccion = 1;

    public static ESCCMD Cmd = new ESCCMD();

    //runs without a timer by reposting this handler at the end of the runnable
    long startTime = 0;
    boolean conectando = false;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            if(mBluetoothSocket != null){
                Log.d("TimerPrintService", "Conectado:" + mBluetoothSocket.isConnected());
            }
//            if(conectando == false){
//                if(confirmarDeQueEsteConectado() == false){
//                    BluetoothConnectAsyncTask bluetoothConnectAsyncTask = new BluetoothConnectAsyncTask();
//                    bluetoothConnectAsyncTask.execute("");
//                }
//            }

            if(confirmarDeQueEsteConectado() == false && conectando == false){
                BluetoothConnectAsyncTask bluetoothConnectAsyncTask = new BluetoothConnectAsyncTask();
                bluetoothConnectAsyncTask.execute("");
            }

            timerHandler.postDelayed(this, 500);
        }
    };


    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(PrinterService.this, "STATE_OFF", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(PrinterService.this, "STATE_TURNING_OFF", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(PrinterService.this, "STATE_ON", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(PrinterService.this, "STATE_TURNING_ON", Toast.LENGTH_SHORT).show();
                        break;

                }

            }



            switch(action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Toast.makeText(PrinterService.this, "ACTION_ACL_CONNECTED", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:
                    Toast.makeText(PrinterService.this, "ACTION_ACL_DISCONNECT_REQUESTED", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Toast.makeText(PrinterService.this, "ACTION_ACL_DISCONNECTED", Toast.LENGTH_SHORT).show();
                    closeSocket(mBluetoothSocket, false);
                    Log.d("PrinterService", "confirmar antes:" + confirmarDeQueEsteConectado());
                    BluetoothConnectAsyncTask bluetoothConnectAsyncTask = new BluetoothConnectAsyncTask();
                    bluetoothConnectAsyncTask.execute("");
                    break;


            }


        }
    };

    public void onCreate(){
        super.onCreate();
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("PrinterService", "Todo bien");
        address = intent.getStringExtra("address");
        name = intent.getStringExtra("name");
        //conectar(address);
        Utilidades.killAppServiceByPackageName(PrinterService.this, null);
        BluetoothConnectAsyncTask bluetoothConnectAsyncTask = new BluetoothConnectAsyncTask();
        bluetoothConnectAsyncTask.execute("");

//        startTime = System.currentTimeMillis();
//        timerHandler.postDelayed(timerRunnable, 0);

        IntentFilter filter1 = new IntentFilter();
        filter1.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter1.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter1.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mBroadcastReceiver1, filter1);

        return START_STICKY;
    }


    private class BluetoothConnectAsyncTask extends AsyncTask<String, Void, String> {
        BluetoothConnectAsyncTask(){

        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            p = new ProgressDialog(MainActivity.this);
//            p.setMessage("Please wait...It is downloading");
//            p.setIndeterminate(false);
//            p.setCancelable(false);
//            p.show();
            // Toast.makeText(MainActivity.this, "cargando", Toast.LENGTH_SHORT).show();
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                conectando = true;
                mBluetoothAdapter = null;
                mBluetoothDevice = null;
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);

//                CONNECT MEDIANTE UUID
//                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
//                mBluetoothAdapter.cancelDiscovery();
//                mBluetoothSocket.connect();


//                CONNECT MEDIANTE PORT
                int bt_port_to_connect = 5; // just an example, could be any port number you wish
                BluetoothDevice device = mBluetoothDevice ; // get the bluetooth device (e.g., using bt discovery)
                BluetoothSocket deviceSocket = null;
//                Method m = device.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class});
//                mBluetoothSocket = (BluetoothSocket) m.invoke(device,bt_port_to_connect);

                UUID uuid = mBluetoothDevice.getUuids()[0].getUuid();
                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
                mBluetoothSocket.connect();
                //mHandler.sendEmptyMessage(0);
                intentosDeConeccion = 1;
//                IOException
            } catch (Exception e) {
                e.printStackTrace();
                closeSocket(mBluetoothSocket, false);
                conectando = false;
                return "Failed";
            }


            return "Success";

        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            conectando = false;
            Log.d("onPostExecute", "string: "+result );
            if(result.equals("Success")) {
//                if(confirmarDeQueEsteConectado() == false){
//                    BluetoothConnectAsyncTask bluetoothConnectAsyncTask = new BluetoothConnectAsyncTask();
//                    bluetoothConnectAsyncTask.execute("");
//                }
//                else
                Toast.makeText(PrinterService.this, "Conectado", Toast.LENGTH_SHORT).show();
                PrinterService.POS_S_TextOut("** ORIGINAL **\n", 0, 1, 1, 0, 0x00);
                closeSocket(mBluetoothSocket, false);
            }else {
                BluetoothConnectAsyncTask bluetoothConnectAsyncTask = new BluetoothConnectAsyncTask();
                bluetoothConnectAsyncTask.execute("");
            }
        }
    }



    public static boolean confirmarDeQueEsteConectado()
    {
        //El metodo isConected del Bluetoothsocket no es tan exacto, asiq que para comprobar la conexion
        //Es mejor intentar escribir algo al socket y si muestra un error entonces eso quiere decir que no hay conexion
        //Otra cosa para que se pueda escribir algo en la impresora primero se debe mandar un salto de linea entonces como aqui
        //no he mandado ningun salto de linea entonces no se imprimira nada, pero de todas manera se intentara
        // escribir, asi que no hay problemas
        try {
            OutputStream os = mBluetoothSocket.getOutputStream();
            String BILL = "";
            os.write(BILL.getBytes());
            Log.d("PrinterService","probarConexion: true");
            return true;
        }catch (Exception e){
            e.printStackTrace();
            Log.d("PrinterService","probarConexion:" + e.toString());
            closeSocket(mBluetoothSocket, false);
            return false;
        }
    }

    public void conectar(String address)
    {
        mBluetoothAdapter = null;
        mBluetoothDevice = null;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);

//        Thread mBlutoothConnectThread = new Thread(this);
//        mBlutoothConnectThread.start();
//        Toast.makeText(this, "Conectando...", Toast.LENGTH_SHORT).show();
    }

    public void onDestroy(){
        closeSocket(mBluetoothSocket, false);
        unregisterReceiver(mBroadcastReceiver1);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
            intentosDeConeccion = 1;
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);



            closeSocket(mBluetoothSocket, true);

            return;
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(PrinterService.this, "DeviceConnected", Toast.LENGTH_LONG).show();
        }
    };

    public static void closeSocket(BluetoothSocket nOpenSocket, boolean intentarConectarOtraVez) {
        try {

//            if(intentarConectarOtraVez){
//                if(intentosDeConeccion < 10){
//
//                    intentosDeConeccion ++;
//                    Log.d("intentosConexion", "intento: " + intentosDeConeccion + " ad: " + address);
//                    conectar(address);
//                }
//            }else{
//                nOpenSocket.close();
//                mBluetoothSocket = null;
//            }

            if(nOpenSocket == null)
                nOpenSocket = mBluetoothSocket;

            if(nOpenSocket != null)
                nOpenSocket.close();
            mBluetoothSocket = null;
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }









    /**************************** PRINT IMPRIMIR ************************************/

    public static void imprimir()
    {
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket
                            .getOutputStream();
                    String BILL = "";

                    byte[] arrayOfByte1 = { 27, 33, 0 };
                    byte[] format = { 27, 33, 0 };

                    //format[2] = ((byte)(0x8 | arrayOfByte1[2]));

                    format[2] = ((byte)(0x01 | arrayOfByte1[2]));
                    //format[2] = ((byte) (0x20 | arrayOfByte1[2]));

//                    os.write(format);
                    //La impresora no imprime si no se le anade un salto de linea \n, asi que para que imprima se le debe anadir un salto de linea

                    String str = "El culazo\n\n\n";
                    String salto = "\n";

                    byte[] printCmd = {0x1b, 0x00, 0x01};
                    os.write(format);
                    os.write(salto.getBytes());
                    os.write(str.getBytes(),0,str.getBytes().length);


                    //This is printer specific code you can comment ==== > Start



                    // Setting height
                    int gs = 29;
                    os.write(intToByteArray(gs));
                    int h = 104;
                    os.write(intToByteArray(h));
                    int n = 162;
                    os.write(intToByteArray(n));

                    // Setting Width
                    int gs_width = 29;
                    os.write(intToByteArray(gs_width));
                    int w = 119;
                    os.write(intToByteArray(w));
                    int n_width = 2;
                    os.write(intToByteArray(n_width));

                    // Print BarCode
                    int gs1 = 29;
                    os.write(intToByteArray(gs1));
                    int k = 107;
                    os.write(intToByteArray(k));
                    int m = 73;
                    os.write(intToByteArray(m));

                    String barCodeVal = "ASDFC028060000005";// "HELLO12345678912345012";
                    System.out.println("Barcode Length : "
                            + barCodeVal.length());
                    int n1 = barCodeVal.length();
                    os.write(intToByteArray(n1));


                    for (int i = 0; i < barCodeVal.length(); i++) {
                        os.write((barCodeVal.charAt(i) + "").getBytes());
                    }
                    //printer specific code you can comment ==== > End
                } catch (Exception e) {
                    Log.e("Main", "Exe ", e);
                }
            }
        };
        t.start();
    }

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }


    public static void POS_S_TextOut(String pszString, int nOrgx, int nWidthTimes, int nHeightTimes, int nFontType, int nFontStyle) {


        try {
            if (nOrgx > 65535 || nOrgx < 0 || nWidthTimes > 7 || nWidthTimes < 0 || nHeightTimes > 7 || nHeightTimes < 0 || nFontType < 0 || nFontType > 4 || pszString.length() == 0) {
                throw new Exception("invalid args");
            }
            OutputStream os = mBluetoothSocket
                    .getOutputStream();

            Cmd.ESC_dollors_nL_nH[2] = (byte)(nOrgx % 256);
            Cmd.ESC_dollors_nL_nH[3] = (byte)(nOrgx / 256);
            byte[] intToWidth = new byte[]{0, 16, 32, 48, 64, 80, 96, 112};
            byte[] intToHeight = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};
            Cmd.GS_exclamationmark_n[2] = (byte)(intToWidth[nWidthTimes] + intToHeight[nHeightTimes]);
            byte[] tmp_ESC_M_n = Cmd.ESC_M_n;
            if (nFontType != 0 && nFontType != 1) {
                tmp_ESC_M_n = new byte[0];
            } else {
                tmp_ESC_M_n[2] = (byte)nFontType;
            }

            Cmd.GS_E_n[2] = (byte)(nFontStyle >> 3 & 1);
            Cmd.ESC_line_n[2] = (byte)(nFontStyle >> 7 & 3);
            Cmd.FS_line_n[2] = (byte)(nFontStyle >> 7 & 3);
            Cmd.ESC_lbracket_n[2] = (byte)(nFontStyle >> 9 & 1);
            Cmd.GS_B_n[2] = (byte)(nFontStyle >> 10 & 1);
            Cmd.ESC_V_n[2] = (byte)(nFontStyle >> 12 & 1);
            Cmd.ESC_9_n[2] = 1;
            byte[] pbString = pszString.getBytes();
            byte[] data = byteArraysToBytes(new byte[][]{Cmd.ESC_dollors_nL_nH, Cmd.GS_exclamationmark_n, tmp_ESC_M_n, Cmd.GS_E_n, Cmd.ESC_line_n, Cmd.FS_line_n, Cmd.ESC_lbracket_n, Cmd.GS_B_n, Cmd.ESC_V_n, Cmd.FS_AND, Cmd.ESC_9_n, pbString});
            os.write(data, 0, data.length);
        } catch (Exception var15) {
            Log.i("Pos", var15.toString());
        }


    }


    public static void POS_S_Align(int align) {

        try {
            if (align < 0 || align > 2) {
                throw new Exception("invalid args");
            }
            OutputStream os = mBluetoothSocket
                    .getOutputStream();

            byte[] data = Cmd.ESC_a_n;
            data[2] = (byte)align;
            os.write(data, 0, data.length);
        } catch (Exception var6) {
            Log.i("Pos", var6.toString());
        }

    }

    public static void POS_SetLineHeight(int nHeight) {


        try {
            if (nHeight < 0 || nHeight > 255) {
                throw new Exception("invalid args");
            }
            OutputStream os = mBluetoothSocket
                    .getOutputStream();

            byte[] data = Cmd.ESC_3_n;
            data[2] = (byte)nHeight;
            os.write(data, 0, data.length);
        } catch (Exception var6) {
            Log.i("Pos", var6.toString());
        }


    }

    public static void POS_S_SetQRcode(String strCodedata, int nWidthX, int nVersion, int nErrorCorrectionLevel) {

        try {
            if (nWidthX < 1 || nWidthX > 16 || nErrorCorrectionLevel < 1 || nErrorCorrectionLevel > 4 || nVersion < 0 || nVersion > 16) {
                throw new Exception("invalid args");
            }

            OutputStream os = mBluetoothSocket
                    .getOutputStream();

            byte[] bCodeData = strCodedata.getBytes();
            Cmd.GS_w_n[2] = (byte)nWidthX;
            Cmd.GS_k_m_v_r_nL_nH[3] = (byte)nVersion;
            Cmd.GS_k_m_v_r_nL_nH[4] = (byte)nErrorCorrectionLevel;
            Cmd.GS_k_m_v_r_nL_nH[5] = (byte)(bCodeData.length & 255);
            Cmd.GS_k_m_v_r_nL_nH[6] = (byte)((bCodeData.length & '\uff00') >> 8);
            byte[] data = byteArraysToBytes(new byte[][]{Cmd.GS_w_n, Cmd.GS_k_m_v_r_nL_nH, bCodeData});
            os.write(data, 0, data.length);
        } catch (Exception var10) {
            Log.i("Pos", var10.toString());
        }


    }

    public static void POS_SetRightSpacing(int nDistance) {


        try {
            if (nDistance < 0 || nDistance > 255) {
                throw new Exception("invalid args");
            }
            OutputStream os = mBluetoothSocket
                    .getOutputStream();

            Cmd.ESC_SP_n[2] = (byte)nDistance;
            byte[] data = Cmd.ESC_SP_n;
            os.write(data, 0, data.length);
        } catch (Exception var6) {
            Log.i("Pos", var6.toString());
        }


    }


    private static byte[] byteArraysToBytes(byte[][] data) {
        int length = 0;

        for(int i = 0; i < data.length; ++i) {
            length += data[i].length;
        }

        byte[] send = new byte[length];
        int k = 0;

        for(int i = 0; i < data.length; ++i) {
            for(int j = 0; j < data[i].length; ++j) {
                send[k++] = data[i][j];
            }
        }

        return send;
    }
}
