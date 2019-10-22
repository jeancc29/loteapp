package com.example.jean2.creta.Servicios;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.jean2.creta.Clases.ESCCMD;
import com.example.jean2.creta.Clases.PrinterClass;
import com.example.jean2.creta.Clases.UnicodeFormatter;
import com.example.jean2.creta.R;
import com.example.jean2.creta.Utilidades;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrintService extends Service {
    private JSONObject venta;
    Context mContext;
    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private static BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    String address;
    String name;
    int intentosDeConeccion = 1;
    public static ESCCMD Cmd = new ESCCMD();

    //runs without a timer by reposting this handler at the end of the runnable
    long startTime = 0;
    boolean conectando = false;
    Handler timerHandler = new Handler();
    boolean imprimir_o_probar_impresora_este_activa;
    int original_copia_cancelado_pagado;
    ExecutorService es = Executors.newScheduledThreadPool(30);
    ConnectedThread mConnectedThread;

    public void onCreate(){
        try{

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




        }catch (Exception e){
            Toast.makeText(this, "Error servicio: " + e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("PrintService", "Todo bien");
        mContext = this;
        boolean imprimir_o_probar_impresora_este_activa;
        int original_copia_cancelado_pagado;

        try {
            venta = new JSONObject(intent.getStringExtra("venta"));
            address = Utilidades.getAddressImpresora(mContext);
            name = Utilidades.getNameImpresora(mContext);
            imprimir_o_probar_impresora_este_activa = intent.getBooleanExtra("imprimir_o_probar_impresora_este_activa", true);
            original_copia_cancelado_pagado = intent.getIntExtra("original_copia_cancelado_pagado", 0);
            conectarEImprimir(imprimir_o_probar_impresora_este_activa, original_copia_cancelado_pagado);
        }catch (Exception e){
            e.printStackTrace();
        }




        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                stopForeground(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();

    }


    public void conectarEImprimir(boolean imprimir_o_probar_impresora_este_activa, int original_copia_cancelado_pagado)
    {
        this.imprimir_o_probar_impresora_este_activa = imprimir_o_probar_impresora_este_activa;
        this.original_copia_cancelado_pagado = original_copia_cancelado_pagado;
        Utilidades.killAppServiceByPackageName(mContext, null);
//        BluetoothConnectAsyncTask bluetoothConnectAsyncTask = new BluetoothConnectAsyncTask(imprimir_o_probar_impresora_este_activa,original_copia_cancelado_pagado);
        BluetoothConnectAsyncTask bluetoothConnectAsyncTask = new BluetoothConnectAsyncTask();
        bluetoothConnectAsyncTask.execute("");
        Log.d("PrintService", "Ejecutando impresion");
    }

    private class BluetoothConnectAsyncTask extends AsyncTask<String, Void, String> {
        //Cuando es true es para imprimir y false para probar la conexion de la impresora
//        boolean imprimir_o_probar_impresora_este_activa = false;
//        int original_copia_cancelado_pagado;
        BluetoothConnectAsyncTask(){

        }
        //        BluetoothConnectAsyncTask(boolean imprimir_o_probar_impresora_este_activa, int original_copia_cancelado_pagado){
//         this.imprimir_o_probar_impresora_este_activa = imprimir_o_probar_impresora_este_activa;
//         this.original_copia_cancelado_pagado = original_copia_cancelado_pagado;
//        }
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
//                IOException
            } catch (Exception e) {
                e.printStackTrace();
                //closeSocket(mBluetoothSocket, false);
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
            mConnectedThread = new ConnectedThread(mBluetoothSocket, "secure");
            mConnectedThread.start();
            if(result.equals("Success")) {
                if(imprimir_o_probar_impresora_este_activa == true)
                    ticket(original_copia_cancelado_pagado);
//                    es.submit(new TaskImprimir(original_copia_cancelado_pagado));
                else
                    POS_S_TextOut("**PRUEBA EXISTOSA**\n\n\n", 0, 1, 1, 0, 0x00);

                mConnectedThread.cancel(true);
                //closeSocket(mBluetoothSocket, false);
            }
//            else {
//                BluetoothConnectAsyncTask bluetoothConnectAsyncTask = new BluetoothConnectAsyncTask();
//                bluetoothConnectAsyncTask.execute("");
//            }
        }
    }


    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private boolean fueCanceladoDesdeApp = false;
        boolean error = false;

        public ConnectedThread(BluetoothSocket socket, String socketType)
        {
            Log.d(TAG, "create ConnectedThread: " + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;


            // Get the BluetoothSocket input and output streams
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (Exception e)
            {
                Log.e(TAG, "temp sockets not created", e);
                //Aqui tratar de devolver la conexion para que todo se vuelva a imprimir
                //Para eso en la funcion conectar voy a crear un sharedpreference que guarde la variable original_copia_cancelado_pagado y que guarde la venta
                //para volver a utilizarla aqui
                if(this.fueCanceladoDesdeApp == false){
                    mBluetoothSocket = null;
//                    BluetoothConnectAsyncTask bluetoothConnectAsyncTask = new BluetoothConnectAsyncTask();
//                    bluetoothConnectAsyncTask.execute("");
                    if(intentosDeConeccion <= 5){
                        intentosDeConeccion++;
                        conectarEImprimir(imprimir_o_probar_impresora_este_activa, original_copia_cancelado_pagado);
                    }
                    else{
                        intentosDeConeccion = 1;
                       // mostrarDialogErrorImpresion();
                        // Toast.makeText(mContext, "Ha fallado la impresora, verifique de que la impresora este encendida y con carga suficiente", Toast.LENGTH_LONG).show();
                    }
                    error = true;
                }
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run()
        {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true && error == false)
            {
                try
                {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    //mHandler.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                } catch (Exception e)
                {
                    Log.e(TAG, "disconnected", e);
                    if(this.fueCanceladoDesdeApp)
                        Log.d("Connected2", "Desconectado desde app");
                    else{

                        mBluetoothSocket = null;
//                        BluetoothConnectAsyncTask bluetoothConnectAsyncTask = new BluetoothConnectAsyncTask();
//                        bluetoothConnectAsyncTask.execute("");
//                        break;
                        if(intentosDeConeccion <= 5){
                            intentosDeConeccion++;
                            conectarEImprimir(imprimir_o_probar_impresora_este_activa, original_copia_cancelado_pagado);
                        }
                        else{
                            intentosDeConeccion = 1;
//                            mostrarDialogErrorImpresion();
                            //Toast.makeText(mContext, "Ha fallado la impresora, verifique de que la impresora este encendida y con carga suficiente", Toast.LENGTH_LONG).show();
                        }
//                        conectarEImprimir(imprimir_o_probar_impresora_este_activa, original_copia_cancelado_pagado);
                        Log.d("Connected", "Conexion fallo: imprimir:" + imprimir_o_probar_impresora_este_activa + " original:"+ original_copia_cancelado_pagado);
                    }


                    //connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer)
        {
            try
            {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                //mHandler.obtainMessage(1, -1, -1, buffer).sendToTarget();
            } catch (IOException e)
            {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel(Boolean fueCanceladoDesdeApp)
        {
            try
            {
                this.fueCanceladoDesdeApp = fueCanceladoDesdeApp;
                if(mmSocket != null )
                    mmSocket.close();
            } catch (IOException e)
            {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }



    public void closeSocket(BluetoothSocket nOpenSocket, boolean intentarConectarOtraVez) {
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






    public byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }


    public void POS_S_TextOut(String pszString, int nOrgx, int nWidthTimes, int nHeightTimes, int nFontType, int nFontStyle) {


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


    public void POS_S_Align(int align) {

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

    public void POS_SetLineHeight(int nHeight) {


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

    public void POS_S_SetQRcode(String strCodedata, int nWidthX, int nVersion, int nErrorCorrectionLevel) {

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

    public void POS_SetRightSpacing(int nDistance) {


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



    public boolean ticket(int original_copia_cancelado_pagado)
    {
        try {
            boolean pagado = false;
            if(original_copia_cancelado_pagado == 4){
                original_copia_cancelado_pagado = 1;
                pagado = true;
            }

            JSONObject venta = this.venta.getJSONObject("venta");
            JSONArray jsonArrayLoterias = venta.getJSONArray("loterias");
            JSONArray jsonArrayJugadas = venta.getJSONArray("jugadas");

            if(jsonArrayLoterias.length() == 0)
                return false;
            if(jsonArrayJugadas.length() == 0)
                return false;


            POS_S_Align(1);
            POS_S_TextOut(venta.getJSONObject("banca").getString("descripcion")+"\n", 0, 1, 1, 0, 0x00);
            if(original_copia_cancelado_pagado == 1)
                POS_S_TextOut("** ORIGINAL **\n", 0, 1, 1, 0, 0x00);
            else if(original_copia_cancelado_pagado == 2)
                POS_S_TextOut("** COPIA **\n", 0, 1, 1, 0, 0x00);
            else if(original_copia_cancelado_pagado == 3)
                POS_S_TextOut("** CANCELADO **\n", 0, 1, 1, 0, 0x00);

            POS_S_TextOut(venta.getString("fecha")+"\n", 0, 0, 1, 0, 0x00);
            POS_S_TextOut("Ticket:"  + Utilidades.toSecuencia(venta.getString("idTicket"), venta.getString("codigo"))+ "\n", 0, 0, 1, 0, 0x00);
            POS_S_TextOut("Fecha: " + venta.getString("fecha")+"\n", 0, 0, 1, 0, 0x00);
            if(original_copia_cancelado_pagado == 1)
                POS_S_TextOut(venta.getString("codigoBarra")+"\n", 1, 1, 1, 0, 0x00);

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
                        POS_S_TextOut("---------------\n", 1, 1, 1, 0, 0x00);
                        POS_S_TextOut(loteria.getString("descripcion") + "\n", 1, 0, 1, 0, 0x00);
                        POS_S_TextOut("---------------\n", 1, 1, 1, 0, 0x00);
                    }
                    if(jugada.getString("idLoteria").equals(loteria.getString("id"))){
                        POS_S_Align(0);
                        if(esPrimeraJugadaAInsertar){
                            POS_S_TextOut("JUGADA   MONTO  JUGADA   MONTO\n", 1, 0, 1, 0, 0x00);
                            esPrimeraJugadaAInsertar = false;
                        }
                        if(((contadorCicleJugadas + 1) % 2) == 0){
                            Log.d("cjPar", String.valueOf(contadorCicleJugadas));
                            POS_S_TextOut("                " + Utilidades.agregarGuion(Utilidades.agregarGuionPorSorteo(jugada.getString("jugada"), jugada.getString("sorteo"))), 1, 0, 1, 0, 0x00);
                            POS_S_TextOut("                         " + jugada.getDouble("monto") + "\n", 1, 0, 1, 0, 0x00);
                        }else{
                            String saltoLinea = "";
                            if((contadorCicleJugadas + 1) == jugadas.length())
                                saltoLinea = "\n";
                            POS_S_TextOut(Utilidades.agregarGuion(Utilidades.agregarGuionPorSorteo(jugada.getString("jugada"), jugada.getString("sorteo"))), 0, 0, 1, 0, 0x00);
                            POS_S_TextOut("         " + jugada.getDouble("monto") + saltoLinea, 0, 0, 1, 0, 0x00);
                        }

//                                pos.POS_S_TextOut("culo23", 0, 0, 1, 0, 0x00);
                        //pos.POS_S_TextOut(" - total: " + getLoteriaTotal(loteria.getInt("id"), jugadas) + "-\n", 1, 0, 1, 0, 0x00);

                    }
                }
                POS_S_Align(1);
                if(jsonArrayLoterias.length() > 1)
                    POS_S_TextOut("\n total: " + getLoteriaTotal(loteria.getString("id"), jugadas) + "\n\n\n", 1, 0, 1, 0, 0x00);
            }

            double total = venta.getDouble("total");
            if(venta.getInt("hayDescuento") == 1){
                total -= venta.getDouble("descuentoMonto");
                POS_S_TextOut("subTotal: " + venta.getDouble("total") + "\n", 1, 0, 1, 0, 0x00);
                POS_S_TextOut("descuento: " + venta.getDouble("descuentoMonto") + "\n", 1, 0, 1, 0, 0x00);
            }
            String saltoLineaTotal = "\n";
            if(original_copia_cancelado_pagado != 1 || venta.getJSONObject("banca").getInt("imprimirCodigoQr") == 0){
                saltoLineaTotal+="\n\n";
            }
            POS_S_TextOut("- TOTAL: " + total + " -" + saltoLineaTotal, 1, 0, 1, 0, 0x00);

            if(original_copia_cancelado_pagado == 3){
                POS_S_TextOut("** CANCELADO **\n\n\n", 0, 1, 1, 0, 0x00);
            }

            if(original_copia_cancelado_pagado == 1){
                if(!venta.getJSONObject("banca").getString("piepagina1").equals("null"))
                    POS_S_TextOut(venta.getJSONObject("banca").getString("piepagina1") + "\n", 1, 0, 1, 0, 0x00);
                if(!venta.getJSONObject("banca").getString("piepagina2").equals("null"))
                    POS_S_TextOut(venta.getJSONObject("banca").getString("piepagina2") + "\n", 1, 0, 1, 0, 0x00);
                if(!venta.getJSONObject("banca").getString("piepagina3").equals("null"))
                    POS_S_TextOut(venta.getJSONObject("banca").getString("piepagina3") + "\n", 1, 0, 1, 0, 0x00);
                if(!venta.getJSONObject("banca").getString("piepagina4").equals("null"))
                    POS_S_TextOut(venta.getJSONObject("banca").getString("piepagina4") + "\n", 1, 0, 1, 0, 0x00);
                if(venta.getJSONObject("banca").getInt("imprimirCodigoQr") == 1)
                    POS_S_SetQRcode(venta.getString("codigoQr"), 8, 0, 3);
                POS_S_TextOut("\n\n\n", 1, 0, 1, 0, 0x00);
            }


        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
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


    public class TaskImprimir implements Runnable
    {
        int original_copia_cancelado_pagado = 0;
        TaskImprimir(int original_copia_cancelado_pagado){this.original_copia_cancelado_pagado = original_copia_cancelado_pagado;}
        @Override
        public void run() {
            // TODO Auto-generated method stub

            final boolean bPrintResult = ticket(this.original_copia_cancelado_pagado);
            mConnectedThread.cancel(true);

            ((Activity)mContext).runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Log.d("PrinterClass", "TaskImprimir:" + bPrintResult);
                    //btnPrint.setEnabled(bIsOpened);
                }
            });

        }
    }


}
