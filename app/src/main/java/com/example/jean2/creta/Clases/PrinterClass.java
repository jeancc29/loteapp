package com.example.jean2.creta.Clases;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

import org.json.JSONArray;
import org.json.JSONObject;

public class PrinterClass {
    private VentasClass venta;
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
    int imprimirticket_cuadre_o_probar_impresora_este_activa;
    int original_copia_cancelado_pagado;
    ExecutorService es = Executors.newScheduledThreadPool(30);
    ConnectedThread mConnectedThread;
    JSONObject cuadre;
    String fecha;


    public PrinterClass(Context context){this.mContext = context; this.address = Utilidades.getAddressImpresora(mContext);}
    public PrinterClass(Context context, VentasClass venta){
        this.mContext = context;
        this.venta = venta;
        this.address = Utilidades.getAddressImpresora(mContext);
    }

    public PrinterClass(Context context, JSONObject cuadre, String fecha){
        this.mContext = context;
        this.address = Utilidades.getAddressImpresora(mContext);
        this.cuadre = cuadre;
        this.fecha = fecha;
    }

    public void conectarEImprimir(int imprimirticket_cuadre_o_probar_impresora_este_activa, int original_copia_cancelado_pagado)
    {
        this.imprimirticket_cuadre_o_probar_impresora_este_activa = imprimirticket_cuadre_o_probar_impresora_este_activa;
        this.original_copia_cancelado_pagado = original_copia_cancelado_pagado;
        Utilidades.killAppServiceByPackageName(mContext, null);
//        BluetoothConnectAsyncTask bluetoothConnectAsyncTask = new BluetoothConnectAsyncTask(imprimir_o_probar_impresora_este_activa,original_copia_cancelado_pagado);
        BluetoothConnectAsyncTask bluetoothConnectAsyncTask = new BluetoothConnectAsyncTask();
        bluetoothConnectAsyncTask.execute("");
        Log.d("PrinterClass", "Ejecutando impresion");
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

                //CONNECT MEDIANTE UUID
                //mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
                //mBluetoothAdapter.cancelDiscovery();
                //mBluetoothSocket.connect();


                //CONNECT MEDIANTE PORT
                int bt_port_to_connect = 5; // just an example, could be any port number you wish
                BluetoothDevice device = mBluetoothDevice ; // get the bluetooth device (e.g., using bt discovery)
                BluetoothSocket deviceSocket = null;
                //Method m = device.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class});
                //mBluetoothSocket = (BluetoothSocket) m.invoke(device,bt_port_to_connect);

                UUID uuid = mBluetoothDevice.getUuids()[0].getUuid();
                mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
                mBluetoothSocket.connect();
                //mHandler.sendEmptyMessage(0);
                //IOException
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
                if(imprimirticket_cuadre_o_probar_impresora_este_activa == 1)
                    ticket(original_copia_cancelado_pagado);
                else if(imprimirticket_cuadre_o_probar_impresora_este_activa == 2){
                    cuadre();
                }
                else {
                    POS_S_TextOut("**PRUEBA EXISTOSA**\n\n\n", 0, 1, 1, 0, 0x00);
                }

                desconectarDesdeApp();

                //mConnectedThread.cancel(true);
                //closeSocket(mBluetoothSocket, false);
            }

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
                        conectarEImprimir(imprimirticket_cuadre_o_probar_impresora_este_activa, original_copia_cancelado_pagado);
                    }
                    else{
                        intentosDeConeccion = 1;
                        mostrarDialogErrorImpresion();
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
                            conectarEImprimir(imprimirticket_cuadre_o_probar_impresora_este_activa, original_copia_cancelado_pagado);
                        }
                        else{
                            intentosDeConeccion = 1;
                            mostrarDialogErrorImpresion();
                            //Toast.makeText(mContext, "Ha fallado la impresora, verifique de que la impresora este encendida y con carga suficiente", Toast.LENGTH_LONG).show();
                        }
//                        conectarEImprimir(imprimir_o_probar_impresora_este_activa, original_copia_cancelado_pagado);
                        Log.d("Connected", "Conexion fallo: imprimir:" + imprimirticket_cuadre_o_probar_impresora_este_activa + " original:"+ original_copia_cancelado_pagado);
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

    public void mostrarDialogErrorImpresion(){

        try{




            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            conectarEImprimir(imprimirticket_cuadre_o_probar_impresora_este_activa, original_copia_cancelado_pagado);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };


            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Error de impresion: revise la impresora y asegurese de que tenga suficiente carga y este encendida, desea intentar imprimir nuevamente?").setPositiveButton("Si", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }catch (Exception e){
            e.printStackTrace();
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
            os.flush();

//            InputStream largeDataInputStream = mBluetoothSocket.getInputStream();
//            int length;
//            while ((length = largeDataInputStream.read(data)) != -1) {
//                Log.d("largeDataInputStream", "index:" + length);
//            }
        } catch (Exception var15) {
            Log.i("Pos", var15.toString());
        }


    }

    public void desconectarDesdeApp()
    {
        try {

            OutputStream os = mBluetoothSocket.getOutputStream();
            os.flush();
            mConnectedThread.cancel(true);
        }catch (Exception e){
            e.printStackTrace();
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


    public void printTicket()
    {

    }
    public boolean ticket(int original_copia_cancelado_pagado)
    {
        try {
            boolean pagado = false;
            if(original_copia_cancelado_pagado == 4){
                original_copia_cancelado_pagado = 1;
                pagado = true;
            }



            if(venta.getLoterias().size() == 0)
                return false;
            if(venta.getJugadas().size() == 0)
                return false;


            POS_S_Align(1);
            POS_S_TextOut(venta.getBanca().descripcion+"\n", 0, 1, 1, 0, 0x00);
            if(original_copia_cancelado_pagado == 1)
                POS_S_TextOut("** ORIGINAL **\n", 0, 1, 1, 0, 0x00);
            else if(original_copia_cancelado_pagado == 2)
                POS_S_TextOut("** COPIA **\n", 0, 1, 1, 0, 0x00);
            else if(original_copia_cancelado_pagado == 3)
                POS_S_TextOut("** CANCELADO **\n", 0, 1, 1, 0, 0x00);

            POS_S_TextOut(venta.getFecha()+"\n", 0, 0, 1, 0, 0x00);
            POS_S_TextOut("Ticket:"  +Utilidades.toSecuencia(venta.getIdTicket().toString(), venta.getCodigo())+ "\n", 0, 0, 1, 0, 0x00);
            POS_S_TextOut("Fecha: " + venta.getFecha()+"\n", 0, 0, 1, 0, 0x00);
            if(original_copia_cancelado_pagado == 1)
                POS_S_TextOut(venta.getCodigoBarra()+"\n", 1, 1, 1, 0, 0x00);

            for(LoteriaClass loteria : venta.getLoterias()){
//                        if(!pos.GetIO().IsOpened())
//                            break;

                boolean esPrimeraJugadaAInsertar = true;
                List<JugadaClass> jugadas = jugadasPertenecientesALoteria(loteria.getId(), pagado);
                if(jugadas.size() == 0)
                    continue;
                int contadorCicleJugadas = 0;
                for (JugadaClass jugada : jugadas){

//                            if(!pos.GetIO().IsOpened())
//                                break;


                    if(contadorCicleJugadas == 0){
                        POS_S_TextOut("---------------\n", 1, 1, 1, 0, 0x00);
                        POS_S_TextOut(loteria.getDescripcion() + "\n", 1, 0, 1, 0, 0x00);
                        POS_S_TextOut("---------------\n", 1, 1, 1, 0, 0x00);
                    }
                    if(jugada.getIdLoteria() == loteria.getId()){
                        POS_S_Align(0);
                        if(esPrimeraJugadaAInsertar){
                            POS_S_TextOut("JUGADA   MONTO  JUGADA   MONTO\n", 1, 0, 1, 0, 0x00);
                            esPrimeraJugadaAInsertar = false;
                        }
                        if(((contadorCicleJugadas + 1) % 2) == 0){
                            Log.d("cjPar", String.valueOf(contadorCicleJugadas));
                            POS_S_TextOut("                " + Utilidades.agregarGuion(Utilidades.agregarGuionPorSorteo(jugada.getJugada(), jugada.getSorteo())), 1, 0, 1, 0, 0x00);
                            POS_S_TextOut("                         " + jugada.getMonto() + "\n", 1, 0, 1, 0, 0x00);
                        }else{
                            String saltoLinea = "";
                            if((contadorCicleJugadas + 1) == jugadas.size())
                                saltoLinea = "\n";
                            POS_S_TextOut(Utilidades.agregarGuion(Utilidades.agregarGuionPorSorteo(jugada.getJugada(), jugada.getSorteo())), 0, 0, 1, 0, 0x00);
                            POS_S_TextOut("         " + jugada.getMonto() + saltoLinea, 0, 0, 1, 0, 0x00);
                        }

//                                pos.POS_S_TextOut("culo23", 0, 0, 1, 0, 0x00);
                        //pos.POS_S_TextOut(" - total: " + getLoteriaTotal(loteria.getInt("id"), jugadas) + "-\n", 1, 0, 1, 0, 0x00);

                    }
                    contadorCicleJugadas++;
                }
                POS_S_Align(1);
                if(venta.getLoterias().size() > 1)
                    POS_S_TextOut("\n total: " + getLoteriaTotal(loteria.getId(), jugadas) + "\n\n\n", 1, 0, 1, 0, 0x00);

            }

            double total = venta.getTotal();
            if(venta.isHayDescuento() == 1){
                total -= venta.getDescuentoMonto();
                POS_S_TextOut("subTotal: " + venta.getTotal() + "\n", 1, 0, 1, 0, 0x00);
                POS_S_TextOut("descuento: " + venta.getDescuentoMonto() + "\n", 1, 0, 1, 0, 0x00);
            }
            String saltoLineaTotal = "\n";
            if(original_copia_cancelado_pagado != 1 || venta.getBanca().imprimirCodigoQr == 0){
                saltoLineaTotal+="\n\n";
            }
            POS_S_TextOut("- TOTAL: " + total + " -" + saltoLineaTotal, 1, 0, 1, 0, 0x00);

            if(original_copia_cancelado_pagado == 3){
                POS_S_TextOut("** CANCELADO **\n\n\n", 0, 1, 1, 0, 0x00);
            }

            if(original_copia_cancelado_pagado == 1){
                if(venta.getBanca().piepagina1 != null){
                    if(!venta.getBanca().piepagina1.equals("null")){
                        POS_S_TextOut(venta.getBanca().piepagina1 + "\n", 1, 0, 1, 0, 0x00);
                    }
                }
                if(venta.getBanca().piepagina2 != null){
                    if(!venta.getBanca().piepagina2.equals("null")){
                        POS_S_TextOut(venta.getBanca().piepagina2 + "\n", 1, 0, 1, 0, 0x00);
                    }
                }
                if(venta.getBanca().piepagina3 != null){
                    if(!venta.getBanca().piepagina3.equals("null")){
                        POS_S_TextOut(venta.getBanca().piepagina3 + "\n", 1, 0, 1, 0, 0x00);
                    }
                }
                if(venta.getBanca().piepagina4 != null){
                    if(!venta.getBanca().piepagina4.equals("null")){
                        POS_S_TextOut(venta.getBanca().piepagina4 + "\n", 1, 0, 1, 0, 0x00);
                    }
                }
                if(venta.getBanca().imprimirCodigoQr == 1)
                    POS_S_SetQRcode(venta.getCodigoQr(), 8, 0, 3);
                POS_S_TextOut("\n\n\n", 1, 0, 1, 0, 0x00);
            }else{
                POS_S_TextOut("\n\n\n", 1, 0, 1, 0, 0x00);
            }


        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }


    //cuadre

    public boolean cuadre()
    {
        try {
            boolean pagado = false;
            if(original_copia_cancelado_pagado == 4){
                original_copia_cancelado_pagado = 1;
                pagado = true;
            }



            POS_S_Align(1);
            POS_S_TextOut("Cuadre\n", 0, 1, 1, 0, 0x00);
            POS_S_TextOut(this.cuadre.getJSONObject("banca").getString("descripcion")+"\n", 0, 1, 1, 0, 0x00);
            POS_S_TextOut(  this.fecha + "\n\n", 0, 1, 1, 0, 0x00);

            POS_S_Align(0);
//            POS_SetRightSpacing(0);
            POS_S_TextOut("Balanace a la fecha: "  +this.cuadre.getString("balanceHastaLaFecha")+ "\n", 0, 0, 1, 0, 0x00);
            POS_S_TextOut("Tickets pendientes:  "  +this.cuadre.getString("pendientes")+ "\n", 0, 0, 1, 0, 0x00);
            POS_S_TextOut("Tickets perdedores:  "  +this.cuadre.getString("perdedores")+ "\n", 0, 0, 1, 0, 0x00);
            POS_S_TextOut("Tickets ganadores:   "  +this.cuadre.getString("ganadores")+ "\n", 0, 0, 1, 0, 0x00);
            POS_S_TextOut("Total:               "  +this.cuadre.getString("total")+ "\n", 0, 0, 1, 0, 0x00);
            POS_S_TextOut("Ventas:              "  +this.cuadre.getString("ventas")+ "\n", 0, 0, 1, 0, 0x00);
            POS_S_TextOut("Comisiones:          "  +this.cuadre.getString("comisiones")+ "\n", 0, 0, 1, 0, 0x00);
            POS_S_TextOut("Descuentos:          "  +this.cuadre.getString("descuentos")+ "\n", 0, 0, 1, 0, 0x00);
            POS_S_TextOut("Premios:             "  +this.cuadre.getString("premios")+ "\n", 0, 0, 1, 0, 0x00);
            POS_S_TextOut("Neto:                "  +this.cuadre.getString("neto")+ "\n", 0, 0, 1, 0, 0x00);
            POS_S_TextOut("Balance mas ventas:  "  +this.cuadre.getString("balanceActual")+ "\n\n\n\n\n", 0, 0, 1, 0, 0x08  );


        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public  List<JugadaClass> jugadasPertenecientesALoteria(int idLoteria, boolean soloJugadasPendientes) {
        int contadorJugadas = 0;
        List<JugadaClass> jugadasListaRetornar = new ArrayList<JugadaClass>();

        for (JugadaClass j: this.venta.getJugadas()) {
            if(j.getIdLoteria() == idLoteria){
                if(soloJugadasPendientes){
                    if(j.status == 0)
                        jugadasListaRetornar.add(j);
                }else{
                    jugadasListaRetornar.add(j);
                }
            }
        }

        return jugadasListaRetornar;
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

    double getLoteriaTotal(int id, List<JugadaClass> jugadas){
        double total = 0;

            for (JugadaClass jugada : jugadas){

                if(jugada.getIdLoteria() == id){
                    total += jugada.getMonto();
                }
            }


        return total;
    }



}
