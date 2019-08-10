package com.example.jean2.creta;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.content.WebViewContent;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static com.example.jean2.creta.Utilidades.combinarBitmap;

public class ImprimirCompartirVerTicketDialog extends AppCompatDialogFragment {

    TextView txtTicket;
    Context mContext;
    Button btnImprimir;
    Button btnCompartir;
    ExecutorService es = Executors.newScheduledThreadPool(30);

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_imprimir_compartir_ver_ticket, null);
        txtTicket = (TextView)view.findViewById(R.id.ticket);
        btnImprimir = (Button) view.findViewById(R.id.btnImprimir);
        btnCompartir = (Button) view.findViewById(R.id.btnCompartir);

        String idTicketSecuencia = Utilidades.toSecuencia(obtenerAtributoJsonObjectTicket("idTicket"), obtenerAtributoJsonObjectTicket("codigo"));
        txtTicket.setText("Que desea hacer con el ticket " + idTicketSecuencia + " ?");

        btnImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImprimirTicket();
            }
        });
        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CompartirTicket();
            }
        });

        Log.d("ImprimirCompartirVer", MonitoreoActivity.selectedTicket.toString());

        builder.setView(view)
                .setTitle("Accion ticket");





        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=(FragmentActivity) context;
        //try {
//        listener = (DuplicarDialog.DuplicarDialogListener) context;
        Log.e("DuplicarDialog", "onAttach:");
//        }catch (ClassCastException e){
//            Log.e("DuplicarDialog", "onAttachError: " + e.toString());
////            throw new ClassCastException(context.toString() + "Must implement DuplicarDialogListener");
//            e.printStackTrace();
//        }
    }

    private String obtenerAtributoJsonObjectTicket(String atributo){
        try{
            return MonitoreoActivity.selectedTicket.getString(atributo);
        }catch (Exception e){
            e.printStackTrace();

            return "";
        }
    }

    private JSONObject getJsonVentaSeleccionado(){

        JSONObject venta = new JSONObject();
        try{

            return venta.put("venta", MonitoreoActivity.selectedTicket);

        }catch(Exception e){
            e.printStackTrace();
            return venta;
        }
    }

    private void ImprimirTicket(){
        if(BluetoothSearchDialog.isPrinterConnected() == false){
            Toast.makeText(mContext, "Debe conectarse a una impresora", Toast.LENGTH_SHORT).show();
            MonitoreoActivity.mostrarFragmentDialogBluetoothSearch();
//                mostrarDispositivosBluetooth();
            return;
        }


        try{
            JSONObject venta = new JSONObject();
            venta.put("venta", MonitoreoActivity.selectedTicket);
            es.submit(new BluetoothSearchDialog.TaskPrint(venta, false));
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    private void CompartirTicket(){

        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                Bitmap bitmap;
                try {
                    bitmap = new Html2Bitmap.Builder().setContext(mContext).setContent(WebViewContent.html(obtenerAtributoJsonObjectTicket("img"))).setBitmapWidth(400).build().getBitmap();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.v("ErrorHtmlWsapp", e.toString());
                    bitmap = null;
                }

                return bitmap;

            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    String codigoQr;
                    try{
                        JSONObject venta = getJsonVentaSeleccionado().getJSONObject("venta");
                        codigoQr = venta.getString("codigoQr");
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.v("ErrorWhatsappImg", e.toString());
                        codigoQr = "";
                    }
                    QRGEncoder qrgEncoder = new QRGEncoder(codigoQr, null, QRGContents.Type.TEXT, 150);
                    try {
                        Bitmap bitmapQR = qrgEncoder.encodeAsBitmap();
                        bitmapQR = combinarBitmap(bitmap, bitmapQR);
                        Utilidades.sendSMS(getContext(), bitmapQR, true);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.v("ErrorQr", e.toString());
                    }

                }
            }
        }.execute();
    }

}
