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
import com.google.gson.stream.JsonToken;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jean2.creta.Clases.JugadaClass;
import com.example.jean2.creta.Clases.VentasClass;
import com.example.jean2.creta.Servicios.JPrinterConnectService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.content.WebViewContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    Button btnVerTicket;
    ExecutorService es = Executors.newScheduledThreadPool(30);
    static int errores = 0;
    static String mensaje = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_imprimir_compartir_ver_ticket, null);
        txtTicket = (TextView)view.findViewById(R.id.ticket);
        btnImprimir = (Button) view.findViewById(R.id.btnImprimir);
        btnCompartir = (Button) view.findViewById(R.id.btnCompartir);
        btnVerTicket = (Button) view.findViewById(R.id.btnVerTicket);

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
        btnVerTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verTicket();
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
//        if(JPrinterConnectService.isPrinterConnected() == false){
//            Toast.makeText(mContext, "Debe conectarse a una impresora", Toast.LENGTH_SHORT).show();
//            MonitoreoActivity.mostrarFragmentDialogBluetoothSearch();
////                mostrarDispositivosBluetooth();
//            return;
//        }

        if(Utilidades.hayImpresorasRegistradas(mContext) == false){
            Main2Activity.txtBluetooth.performClick();
            Toast.makeText(mContext, "Debe registrar una impresora", Toast.LENGTH_SHORT).show();
            return;
        }





        String url = Utilidades.URL +"/api/reportes/getTicketById";
        //progressBar.setVisibility(View.VISIBLE);


        JSONObject dato = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            dato.put("idUsuario", Utilidades.getIdUsuario(mContext));
            dato.put("idTicket", MonitoreoActivity.selectedTicket.getString("idTicket"));
            datosObj.put("datos", dato);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String jsonString = datosObj.toString();
        imprimirTicketHttp g = new imprimirTicketHttp(datosObj);
        g.execute();

//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        //progressBar.setVisibility(View.GONE);
//                        try {
//                            MonitoreoActivity.selectedTicket = response.getJSONObject("ticket");
//
//                            JSONObject venta = new JSONObject();
//                            venta.put("venta", MonitoreoActivity.selectedTicket);
////                            es.submit(new BluetoothSearchDialog.TaskPrint(venta, false));
//                            Utilidades.imprimir(mContext, venta, 2);
//                            getDialog().dismiss();
//
//                            //getDialog().dismiss();
//                            //updateTable(jsonArray);
//                        } catch (JSONException e) {
//                            Log.d("Error: ", e.toString());
//                            e.printStackTrace();
//
//
//                            getDialog().dismiss();
//
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("responseerror: ", String.valueOf(error));
//                // progressBar.setVisibility(View.GONE);
//                error.printStackTrace();
//                if(error instanceof NetworkError){
//                    Toast.makeText(mContext, "Verifique coneccion e intente de nuevo", Toast.LENGTH_SHORT).show();
//                }
//                else if(error instanceof ServerError){
//                    Toast.makeText(mContext, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
//                }
//                else if(error instanceof TimeoutError){
//                    Toast.makeText(mContext, "Conexion lenta, verifique conexion e intente de nuevo", Toast.LENGTH_SHORT).show();
//                }
//
//
//                getDialog().dismiss();
//            }
//        });
//
////        mQueue.add(request);
//        MySingleton.getInstance(mContext).addToRequestQueue(request);

    }

    public class imprimirTicketHttp extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;
        JSONObject data;
        public imprimirTicketHttp(JSONObject data){
            this.data = data;
        }

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();

            try {
                //URL url = new URL("https://api.github.com/users/dmnugent80/repos");
                URL url = new URL(Utilidades.URL +"/api/reportes/getTicketById");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");

                //SE ESCRIBEN LOS BYTES QUE SE VAN A ENVIAR
                DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream ());
                //printout.writeBytes(URLEncoder.encode(datosObj.toString(),"UTF-8"));
                printout.writeBytes(data.toString());
                printout.flush ();
                printout.close ();


                if(urlConnection.getResponseCode() != 201)
                    return "Error";

                //GET THE REQUEST DATA
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.i("imprimirTicketHttp", result.toString());
                //SE BUSCA EL INDEX DE LAS JUGADAS EN EL JSONSTRING


                //SE LLENAN LAS LISTAS CON LOS JSONSTRING



            }catch( Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }


            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {

            //Do something with the JSON string
            if(!result.equals("Error")) {

                //SE LLENAN LAS LISTAS CON LOS JSONSTRING
                VentasClass ventasClass = llenarVenta(result.toString());
                if (errores == 1) {
                    Toast.makeText(mContext, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                    return;
                }

                Utilidades.imprimir(mContext, ventasClass, 2);
                getDialog().dismiss();
            }else{
                Toast.makeText(mContext, "Error del servidor", Toast.LENGTH_SHORT).show();
            }

        }

    }



    private void CompartirTicket(){

//        new AsyncTask<Void, Void, Bitmap>() {
//            @Override
//            protected Bitmap doInBackground(Void... voids) {
//                Bitmap bitmap;
//                try {
//                    bitmap = new Html2Bitmap.Builder().setContext(mContext).setContent(WebViewContent.html(obtenerAtributoJsonObjectTicket("img"))).setBitmapWidth(400).build().getBitmap();
//                }catch (Exception e){
//                    e.printStackTrace();
//                    Log.v("ErrorHtmlWsapp", e.toString());
//                    bitmap = null;
//                }
//
//                return bitmap;
//
//            }
//
//            @Override
//            protected void onPostExecute(Bitmap bitmap) {
//                if (bitmap != null) {
//                    String codigoQr;
//                    try{
//                        JSONObject venta = getJsonVentaSeleccionado().getJSONObject("venta");
//                        codigoQr = venta.getString("codigoQr");
//                    }catch (Exception e){
//                        e.printStackTrace();
//                        Log.v("ErrorWhatsappImg", e.toString());
//                        codigoQr = "";
//                    }
//                    QRGEncoder qrgEncoder = new QRGEncoder(codigoQr, null, QRGContents.Type.TEXT, 150);
//                    try {
//                        Bitmap bitmapQR = qrgEncoder.encodeAsBitmap();
//                        bitmapQR = combinarBitmap(bitmap, bitmapQR);
//                        Utilidades.sendSMS(getContext(), bitmapQR, true);
//                    }catch (Exception e){
//                        e.printStackTrace();
//                        Log.v("ErrorQr", e.toString());
//                    }
//
//                }
//            }
//        }.execute();





        String url = Utilidades.URL +"/api/reportes/getTicketById";
        //progressBar.setVisibility(View.VISIBLE);


        JSONObject dato = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            dato.put("idUsuario", Utilidades.getIdUsuario(mContext));
            dato.put("idTicket", MonitoreoActivity.selectedTicket.getString("idTicket"));
            datosObj.put("datos", dato);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String jsonString = datosObj.toString();
        compartirTicketHttp c = new compartirTicketHttp(datosObj);
        c.execute();
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        //progressBar.setVisibility(View.GONE);
//                        try {
//                            MonitoreoActivity.selectedTicket = response.getJSONObject("ticket");
//
//                             new AsyncTask<Void, Void, Bitmap>() {
//                                @Override
//                                protected Bitmap doInBackground(Void... voids) {
//                                    Bitmap bitmap;
//                                    try {
//                                        bitmap = new Html2Bitmap.Builder().setContext(mContext).setContent(WebViewContent.html(obtenerAtributoJsonObjectTicket("img"))).setBitmapWidth(400).build().getBitmap();
//                                    }catch (Exception e){
//                                        e.printStackTrace();
//                                        Log.v("ErrorHtmlWsapp", e.toString());
//                                        bitmap = null;
//                                    }
//
//                                    return bitmap;
//
//                                }
//
//                                @Override
//                                protected void onPostExecute(Bitmap bitmap) {
//                                    if (bitmap != null) {
//                                        String codigoQr;
//                                        try{
//                                            JSONObject venta = getJsonVentaSeleccionado().getJSONObject("venta");
//                                            codigoQr = venta.getString("codigoQr");
//                                        }catch (Exception e){
//                                            e.printStackTrace();
//                                            Log.v("ErrorWhatsappImg", e.toString());
//                                            codigoQr = "";
//                                        }
//                                        QRGEncoder qrgEncoder = new QRGEncoder(codigoQr, null, QRGContents.Type.TEXT, 150);
//                                        try {
//                                            Bitmap bitmapQR = qrgEncoder.encodeAsBitmap();
//                                            bitmapQR = combinarBitmap(bitmap, bitmapQR);
//                                            Utilidades.sendSMS(mContext, bitmapQR, true);
//                                        }catch (Exception e){
//                                            e.printStackTrace();
//                                            Log.v("ErrorQr", e.toString());
//                                        }
//
//                                    }
//                                }
//                            }.execute();
//                            getDialog().dismiss();
//                            //updateTable(jsonArray);
//                        } catch (JSONException e) {
//                            Log.d("Error: ", e.toString());
//                            e.printStackTrace();
//
//
//                            getDialog().dismiss();
//
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("responseerror: ", String.valueOf(error));
//                // progressBar.setVisibility(View.GONE);
//                error.printStackTrace();
//                if(error instanceof NetworkError){
//                    Toast.makeText(mContext, "Verifique coneccion e intente de nuevo", Toast.LENGTH_SHORT).show();
//                }
//                else if(error instanceof ServerError){
//                    Toast.makeText(mContext, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
//                }
//                else if(error instanceof TimeoutError){
//                    Toast.makeText(mContext, "Conexion lenta, verifique conexion e intente de nuevo", Toast.LENGTH_SHORT).show();
//                }
//
//
//                getDialog().dismiss();
//            }
//        });
//
////        mQueue.add(request);
//        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public class compartirTicketHttp extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;
        JSONObject data;
        public compartirTicketHttp(JSONObject data){
            this.data = data;
        }

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();

            try {
                //URL url = new URL("https://api.github.com/users/dmnugent80/repos");
                URL url = new URL(Utilidades.URL +"/api/reportes/getTicketById");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");

                //SE ESCRIBEN LOS BYTES QUE SE VAN A ENVIAR
                DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream ());
                //printout.writeBytes(URLEncoder.encode(datosObj.toString(),"UTF-8"));
                printout.writeBytes(data.toString());
                printout.flush ();
                printout.close ();


                if(urlConnection.getResponseCode() != 201)
                    return "Error";

                //GET THE REQUEST DATA
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.i("imprimirTicketHttp", result.toString());
                //SE BUSCA EL INDEX DE LAS JUGADAS EN EL JSONSTRING




            }catch( Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }


            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {

            //Do something with the JSON string
            if(!result.equals("Error")) {

                //SE LLENAN LAS LISTAS CON LOS JSONSTRING
                VentasClass ventasClass = llenarVenta(result.toString());
                if (errores == 1) {
                    Toast.makeText(mContext, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                    return;
                }

                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... voids) {
                        Bitmap bitmap;
                        try {
                            bitmap = new Html2Bitmap.Builder().setContext(mContext).setContent(WebViewContent.html(ventasClass.getImg())).setBitmapWidth(400).build().getBitmap();
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

                                codigoQr = ventasClass.getCodigoQr();
                            }catch (Exception e){
                                e.printStackTrace();
                                Log.v("ErrorWhatsappImg", e.toString());
                                codigoQr = "";
                            }
                            QRGEncoder qrgEncoder = new QRGEncoder(codigoQr, null, QRGContents.Type.TEXT, 150);
                            try {
                                Bitmap bitmapQR = qrgEncoder.encodeAsBitmap();
                                bitmapQR = combinarBitmap(bitmap, bitmapQR);
                                Utilidades.sendSMS(mContext, bitmapQR, true);
                            }catch (Exception e){
                                e.printStackTrace();
                                Log.v("ErrorQr", e.toString());
                            }

                        }
                    }
                }.execute();
                getDialog().dismiss();
            }else{
                Toast.makeText(mContext, "Error del servidor", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void verTicket(){



        String url = Utilidades.URL +"/api/reportes/getTicketById";
        //progressBar.setVisibility(View.VISIBLE);


        JSONObject dato = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            dato.put("idUsuario", Utilidades.getIdUsuario(mContext));
            dato.put("idTicket", MonitoreoActivity.selectedTicket.getString("idTicket"));
            datosObj.put("datos", dato);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String jsonString = datosObj.toString();
        verTicketHttp verTicketHttp = new verTicketHttp(datosObj);
        verTicketHttp.execute();
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        //progressBar.setVisibility(View.GONE);
//                        try {
//                            MonitoreoActivity.selectedTicket = response.getJSONObject("ticket");
//
//                           // MonitoreoActivity.VerTicket();
//                            Bundle arguments = new Bundle();
//                            arguments.putParcelable("venta", ventasClass);
//                            VerTicketDialog verTicketDialog = new VerTicketDialog();
//                            verTicketDialog.setArguments(arguments);
//                            verTicketDialog.show(((FragmentActivity)mContext).getSupportFragmentManager(), "Ver ticket");
//                            getDialog().dismiss();
//                            //updateTable(jsonArray);
//                        } catch (JSONException e) {
//                            Log.d("Error: ", e.toString());
//                            e.printStackTrace();
//
//
//                            getDialog().dismiss();
//
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("responseerror: ", String.valueOf(error));
//               // progressBar.setVisibility(View.GONE);
//                error.printStackTrace();
//                if(error instanceof NetworkError){
//                    Toast.makeText(mContext, "Verifique coneccion e intente de nuevo", Toast.LENGTH_SHORT).show();
//                }
//                else if(error instanceof ServerError){
//                    Toast.makeText(mContext, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
//                }
//                else if(error instanceof TimeoutError){
//                    Toast.makeText(mContext, "Conexion lenta, verifique conexion e intente de nuevo", Toast.LENGTH_SHORT).show();
//                }
//
//
//                getDialog().dismiss();
//            }
//        });
//
////        mQueue.add(request);
//        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }


    public class verTicketHttp extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;
        JSONObject data;

        public verTicketHttp(JSONObject data) {
            this.data = data;
        }

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();

            try {
                //URL url = new URL("https://api.github.com/users/dmnugent80/repos");
                URL url = new URL(Utilidades.URL +"/api/reportes/getTicketById");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");

                //SE ESCRIBEN LOS BYTES QUE SE VAN A ENVIAR
                DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream());
                //printout.writeBytes(URLEncoder.encode(datosObj.toString(),"UTF-8"));
                printout.writeBytes(data.toString());
                printout.flush();
                printout.close();


                if (urlConnection.getResponseCode() != 201)
                    return "Error";

                //GET THE REQUEST DATA
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }


                //SE BUSCA EL INDEX DE LAS JUGADAS EN EL JSONSTRING
                //int idx = result.toString().indexOf("\"jugadas\"");
                //String jugadasJsonString = "{"+ result.toString().substring(idx, result.toString().length());
                //String loteriasJsonString = result.toString().substring(0, idx - 1) + "}";



            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }


            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {

            //Do something with the JSON string
            if(!result.equals("Error")) {

                //SE LLENAN LAS LISTAS CON LOS JSONSTRING
                VentasClass ventasClass = llenarVenta(result.toString());
                if (errores == 1) {
                    Toast.makeText(mContext, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                    return;
                }

                Bundle arguments = new Bundle();
                arguments.putParcelable("venta", ventasClass);
                VerTicketDialog verTicketDialog = new VerTicketDialog();
                verTicketDialog.setArguments(arguments);
                verTicketDialog.show(((FragmentActivity)mContext).getSupportFragmentManager(), "Duplicar dialog");
                getDialog().dismiss();
            }else{
                Toast.makeText(mContext, "Error del servidor", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public static VentasClass llenarVenta(String result)
    {


        Log.e("llenarJugadaLoterias", "prueba: " + result);
        VentasClass ventasClass = null;
        errores = 0;
        Gson gson = new GsonBuilder().create();
        try (com.google.gson.stream.JsonReader reader1 = new com.google.gson.stream.JsonReader(new StringReader(result))){
            reader1.beginObject();

            int c=0;
            String nombre = "";
            boolean hasNext = true;
            while (reader1.hasNext()){
//                JsonToken nextToken = reader1.peek();


                Log.e("llenarJugadaLoterias", "nombre:" + nombre +" token:" + reader1.peek());
                if (JsonToken.BEGIN_ARRAY.equals(reader1.peek())) {


                    reader1.beginArray();

                }

                if (JsonToken.BEGIN_OBJECT.equals(reader1.peek())) {

                    if(nombre.equals("ticket")){
                        ventasClass = gson.fromJson(reader1, VentasClass.class);
                        return ventasClass;
                    }
                    else{
                        reader1.beginObject();
                    }


                }
                if (JsonToken.NAME.equals(reader1.peek())) {

                    nombre = reader1.nextName();
                    System.out.println("Token KEY >>>> " + nombre);

                }
                if (JsonToken.STRING.equals(reader1.peek())) {

                    String value = reader1.nextString();
                    if(nombre.equals("mensaje")){
                        mensaje = value;
                        Log.i("llenarJugadaLoterias", "mensaje:" + mensaje);
                    }
                    System.out.println("Token Value >>>> " + value);

                }
                if (JsonToken.NUMBER.equals(reader1.peek())) {

                    long value = reader1.nextLong();
                    if(nombre.equals("errores")){
                        errores = (int)value;
                        Log.i("llenarJugadaLoterias", "errores:" + errores);
                    }
                    System.out.println("Token Value >>>> " + value);

                }
                if (JsonToken.NULL.equals(reader1.peek())) {

                    reader1.nextNull();
                    System.out.println("Token Value >>>> null");

                }
                if (JsonToken.END_OBJECT.equals(reader1.peek())) {

                    reader1.endObject();
//                    JsonToken nextToken1 = reader1.peek();
                    if (JsonToken.END_ARRAY.equals(reader1.peek())) {
                        reader1.endArray();
                        hasNext = reader1.hasNext();
                    }
//                    Log.e("culo", "nombre:" + reader1.hasNext() +" token:" + nextToken1);

                }
                if (JsonToken.END_ARRAY.equals(reader1.peek())) {

                    reader1.endArray();

                }
                if (JsonToken.END_DOCUMENT.equals(reader1.peek())) {

                    return ventasClass;

                }


                c++;
            }
        }catch (Exception e){
            e.printStackTrace();
            return ventasClass;
        }

        return ventasClass;
    }


    public VentasClass llenarVentaViejo(String ventasJsonString) {
//        if(jugadasLista != null)
//            jugadasLista.clear();
        VentasClass ventasClass = null;
        Gson gson = new GsonBuilder().create();
        try (com.google.gson.stream.JsonReader reader1 = new com.google.gson.stream.JsonReader(new StringReader(ventasJsonString))) {
            //reader1.beginObject();

            int c = 0;
            while (reader1.hasNext()) {


                if (JsonToken.BEGIN_OBJECT.equals(reader1.peek())) {
                    reader1.beginObject();

                    Log.i("ImprmirCompartirVerPath", reader1.getPath());
                    Log.i("ImprmirCompartirVerPeek", reader1.peek().toString());
                    if (JsonToken.NAME.equals(reader1.peek())) {
                        reader1.nextName();
                        if (reader1.getPath().equals("$.ticket")) {

                            ventasClass = gson.fromJson(reader1, VentasClass.class);
//                            Utilidades.imprimir(mContext, ventasClass, 4);
                            Log.i("llenarVenta", ventasClass.getCodigo());
                            reader1.close();
                            return ventasClass;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ventasClass;
        }
        return ventasClass;
    }



    private void getMonitoreo(){

    }

}
