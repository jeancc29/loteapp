package com.example.jean2.creta;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jean2.creta.Clases.JugadaClass;
import com.example.jean2.creta.Clases.LoteriaClass;
import com.example.jean2.creta.Clases.PrinterClass;
import com.example.jean2.creta.Clases.VentasClass;
import com.example.jean2.creta.Servicios.ActualizarService;
import com.example.jean2.creta.Servicios.JPrinterConnectService;
import com.example.jean2.creta.Servicios.VerificarAccesoAlSistemaService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonToken;
import com.lvrenyang.io.BTPrinting;
import com.lvrenyang.io.IOCallBack;
import com.lvrenyang.io.Pos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main2Activity extends AppCompatActivity implements DuplicarDialog.DuplicarDialogListener, PagarTicketDialog.PagarTicketDialogListener {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private static TabLayout tabLayout;
    private static DuplicarPrincipalInterface listener;
    public static ProgressBar progressBarToolbar;
    public static TextView_Icon txtBluetooth;
    public static TextView_Icon txtCamara;
    public static boolean conectadoAImpresoraBluetooth = false;

    private IntentFilter intentFilter = null;
    static ExecutorService es = Executors.newScheduledThreadPool(30);
    Pos mPos = new Pos();
    BTPrinting mBt = new BTPrinting();
    private LinearLayout linearlayoutdevices;
    private ProgressBar progressBarSearchStatus;
    private BroadcastReceiver broadcastReceiver = null;
    List<String> dispostivosLista = new ArrayList<String>();


    private TextView txtSelected;
    IconManager iconManager;
    CheckBox c;
    Spinner spinnerTicket;
    //private RequestQueue mQueue;

    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();

    View view;
    TextView txtJugada;
    TextView txtMontojugar;
    TextView txtMontodisponible;
    boolean jugada_monto_active = true;
    PrincipalFragment principalFragment;

    public static Context mContext;
    static Main2Activity mActivity;
    public static List<JugadaClass> jugadasLista = new ArrayList<JugadaClass>();
    public static List<LoteriaClass> loteriasLista = new ArrayList<LoteriaClass>();
    public static JSONObject duplicarDatos;
    public static JSONObject pagarTicketDatos;

    public static VentasClass ventasClasses;
    static int errores = 0;
    static String mensaje = "";


    String monto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        iniciarServicio();
        //Toast.makeText(Main2Activity.this, getVersionName(mContext), Toast.LENGTH_SHORT).show();
        principalFragment = new PrincipalFragment();



       // mQueue = Volley.newRequestQueue(this);
//        listener = (DuplicarPrincipalInterface) this;

        mActivity = this;
        mContext = Main2Activity.this;
        //Utilidades.conectarseAutomaticamente(mContext);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        //toolbar.setTitle("");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBarToolbar = (ProgressBar)findViewById(R.id.toolbar_progress_bar);
        txtBluetooth = (TextView_Icon)findViewById(R.id.txtBluetooth);
        txtCamara = (TextView_Icon)findViewById(R.id.txtCamara);
        txtBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, BluetoothDevices.class);
                intent.putExtra("esDuplicar", true);
                startActivity(intent);

//                PrinterClass printerClass = new PrinterClass(mContext);
//                printerClass.conectarEImprimir(false, 0);
//                BluetoothSearchDialog duplicarDialog = new BluetoothSearchDialog();
//                duplicarDialog.show(Main2Activity.this.getSupportFragmentManager(), "Duplicar dialog");
//                mostrarDispositivosBluetooth();
            }
        });
        txtCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 PopupMenu popupMenu = new PopupMenu(Main2Activity.this, txtCamara);
                popupMenu.getMenuInflater().inflate(R.menu.popup_duplicar_pagar_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(Main2Activity.this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();

                        if(menuItem.getTitle().equals("Duplicar")){

                            Intent intent = new Intent(Main2Activity.this, ScanQRActivity.class);
                            intent.putExtra("esDuplicar", true);
                            startActivity(intent);
                        }
                        if(menuItem.getTitle().equals("Pagar")){

                            Intent intent = new Intent(Main2Activity.this, ScanQRActivity.class);
                            intent.putExtra("esDuplicar", false);
                            startActivity(intent);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        viewPager = findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);

//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if(position == 2){
////                    //JugadasFragment jugadasFragment = new JugadasFragment();
////                    PrincipalFragment principalFragment = new PrincipalFragment();
////                    Log.d("Se ha actualizado:", String.valueOf(position));
////                    principalFragment.getJugadas();
////                    //jugadasFragment.update();
//                   // JugadasFragment.updateTable();
//                }
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                //JugadasFragment jugadasFragment = new JugadasFragment();
//                //PrincipalFragment principalFragment = new PrincipalFragment();
////                Log.d("Se ha actualizado:", String.valueOf(position));
//                //principalFragment.getJugadas();
//                //jugadasFragment.update();
////                Log.d("Seleccionada:", String.valueOf(position));
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

        //viewPager.getAdapter().notifyDataSetChanged();

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        viewPager.setCurrentItem(1);



        progressBarSearchStatus = (ProgressBar) findViewById(R.id.progressBarSearchStatus);
        //linearlayoutdevices = (LinearLayout) findViewById(R.id.linearlayoutdevices);



    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mContext.startService(new Intent(mContext, VerificarAccesoAlSistemaService.class));
        mContext.startService(new Intent(mContext, ActualizarService.class));
//        Toast.makeText(mContext, "On resume", Toast.LENGTH_SHORT).show();
    }

    public void iniciarServicio(){
        //mContext.startService(new Intent(getActivity(), VerificarAccesoAlSistemaService.class));
//        startService(new Intent(Main2Activity.this, ActualizarService.class));
    }

    public String getVersionName(Context ctx){
        return BuildConfig.VERSION_NAME;
    }



    public void hola(View v){
        TextView t = (TextView)findViewById(R.id.txtJugada);
        t.setText("Holaaa");
    }



    @Override
    public void setCodigoBarra(String codigoBarra) {
        duplicarTicket(codigoBarra, false);
    }

    @Override
    public void setCodigoBarraPagar(String codigoBarra) {
        Log.d("Main2Activity", "setCodigoBarra:" + codigoBarra);
        buscarTicketAPagar(codigoBarra, false);
    }

    public static void abrirDialogGuardarPrinter(final String address){
//        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which){
//                    case DialogInterface.BUTTON_POSITIVE:
//                        //Yes button clicked
//
//                        Utilidades.guardarImpresora(mContext, address);
//
//                        Toast.makeText(mContext, "Printer: " + Utilidades.getImpresora(mContext, address), Toast.LENGTH_SHORT).show();
//                        break;
//
//                    case DialogInterface.BUTTON_NEGATIVE:
//                        //No button clicked
//                        break;
//                }
//            }
//        };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setMessage("Desea guardar impresora ?").setPositiveButton("Yes", dialogClickListener)
//                .setNegativeButton("No", dialogClickListener).show();
    }


    public static class duplicarHttp extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;
        JSONObject data;
        public duplicarHttp(JSONObject data){
            this.data = data;
        }

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();

            try {
                //URL url = new URL("https://api.github.com/users/dmnugent80/repos");
                URL url = new URL(Utilidades.URL +"/api/principal/duplicar");
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

            if(!result.equals("Error")) {
                //Do something with the JSON string
                //SE BUSCA EL INDEX DE LAS JUGADAS EN EL JSONSTRING
                int idx = result.toString().indexOf("\"jugadas\"");


                //SE LLENAN LAS LISTAS CON LOS JSONSTRING
                jugadasLista.clear();
                loteriasLista.clear();
                if(llenarJugadaLoterias(result)){
                    if (errores == 1) {
                        Toast.makeText(mContext, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    tabLayout.getTabAt(1).select();
                    DuplicarAvanzadoDialog duplicarAvanzadoDialog = new DuplicarAvanzadoDialog();
                    duplicarAvanzadoDialog.show(mActivity.getSupportFragmentManager(), "Duplicar");
                }
//                llenarJugadasLista(jugadasJsonString);
//                llenarLoteriasLista(loteriasJsonString);

//                tabLayout.getTabAt(1).select();
//                DuplicarAvanzadoDialog duplicarAvanzadoDialog = new DuplicarAvanzadoDialog();
//                duplicarAvanzadoDialog.show(mActivity.getSupportFragmentManager(), "Duplicar");
            }
            else{
                Toast.makeText(mContext, "Error del servidor", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public static void llenarJugadasLista(String jugadasJsonString)
    {
        if(jugadasLista != null)
            jugadasLista.clear();
        Gson gson = new GsonBuilder().create();
        try (com.google.gson.stream.JsonReader reader1 = new com.google.gson.stream.JsonReader(new StringReader(jugadasJsonString))){
            reader1.beginObject();

            int c=0;
            while (reader1.hasNext()){
                if(c == 0){
                    reader1.nextName();
                    reader1.beginArray();

                }
                JugadaClass jugadaClass = gson.fromJson(reader1, JugadaClass.class);
                jugadasLista.add(jugadaClass);
                Log.i("DuplicarGson", jugadaClass.getJugada());
                c++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void llenarLoteriasLista(String loteriasJsonString)
    {
        if(loteriasLista != null)
            loteriasLista.clear();
        Gson gson = new GsonBuilder().create();
        try (com.google.gson.stream.JsonReader reader1 = new com.google.gson.stream.JsonReader(new StringReader(loteriasJsonString))){
            reader1.beginObject();
            int c=0;
            while (reader1.hasNext()){
                if(c == 0){
                    reader1.nextName();
                    reader1.beginArray();

                }
                LoteriaClass loteriaClass = gson.fromJson(reader1, LoteriaClass.class);
                loteriasLista.add(loteriaClass);
                Log.i("llenarLoteriasLista", loteriaClass.getDescripcion());
                c++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean llenarJugadaLoterias(String result)
    {
        if(jugadasLista != null)
            jugadasLista.clear();
        Log.e("llenarJugadaLoterias", "prueba: " + result);
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
                   if(nombre.equals("loterias") || nombre.equals("jugadas")){
                       if(nombre.equals("jugadas")){
                           JugadaClass jugadaClass = gson.fromJson(reader1, JugadaClass.class);
                           jugadasLista.add(jugadaClass);
                           Log.i("llenarJugadaLoterias", "jugadas:" + jugadaClass.getJugada());
                       }

                       if(nombre.equals("loterias")){
                           LoteriaClass loteriaClass  = gson.fromJson(reader1, LoteriaClass.class);
                           loteriasLista.add(loteriaClass);
                           Log.i("llenarJugadaLoterias", "loterias:" + loteriaClass.getDescripcion());
                       }
                   }else{
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

                   return true;

                }


                c++;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public static void llenarJugadaLoteriasViejo(String result)
    {
        if(jugadasLista != null)
            jugadasLista.clear();
        Log.e("llenarJugadaLoterias", "prueba: " + result);
        Gson gson = new GsonBuilder().create();
        try (com.google.gson.stream.JsonReader reader1 = new com.google.gson.stream.JsonReader(new StringReader(result))){
            reader1.beginObject();

            int c=0;
            String nombre = "";
            boolean hasNext = true;
            while (hasNext){
                JsonToken nextToken = reader1.peek();
                hasNext = reader1.hasNext();

                Log.e("llenarJugadaLoterias", "nombre:" + hasNext +" token:" + nextToken);
                if (JsonToken.BEGIN_ARRAY.equals(nextToken)) {


                    reader1.beginArray();

                }
                if (JsonToken.BEGIN_OBJECT.equals(nextToken)) {
                    if(nombre.equals("loterias") || nombre.equals("jugadas")){
                        if(nombre.equals("jugadas")){
                            JugadaClass jugadaClass = gson.fromJson(reader1, JugadaClass.class);
                            jugadasLista.add(jugadaClass);
                            Log.i("llenarJugadaLoterias", "jugadas:" + jugadaClass.getJugada());
                        }

                        if(nombre.equals("loterias")){
                            LoteriaClass loteriaClass  = gson.fromJson(reader1, LoteriaClass.class);
                            loteriasLista.add(loteriaClass);
                            Log.i("llenarJugadaLoterias", "loterias:" + loteriaClass.getDescripcion());
                        }
                    }else{
                        reader1.beginObject();
                    }




                } else if (JsonToken.NAME.equals(nextToken)) {

                    nombre = reader1.nextName();
                    System.out.println("Token KEY >>>> " + nombre);

                } else if (JsonToken.STRING.equals(nextToken)) {

                    String value = reader1.nextString();
                    System.out.println("Token Value >>>> " + value);

                } else if (JsonToken.NUMBER.equals(nextToken)) {

                    long value = reader1.nextLong();
                    System.out.println("Token Value >>>> " + value);

                } else if (JsonToken.NULL.equals(nextToken)) {

                    reader1.nextNull();
                    System.out.println("Token Value >>>> null");

                }
                else if (JsonToken.END_OBJECT.equals(nextToken)) {

                    reader1.endObject();
//                    JsonToken nextToken1 = reader1.peek();
                    if (JsonToken.END_ARRAY.equals(reader1.peek())) {
                        reader1.endArray();
                        hasNext = reader1.hasNext();
                    }
//                    Log.e("culo", "nombre:" + reader1.hasNext() +" token:" + nextToken1);

                }
                else if (JsonToken.END_ARRAY.equals(nextToken)) {

                    reader1.endArray();

                }



                c++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void duplicarTicket(String codigoBarraQR, boolean esQR){
//        String url = Utilidades.URL +"/api/principal/duplicar";

        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            if(esQR){
                loteria.put("codigoBarra", "");
                loteria.put("codigoQr", codigoBarraQR);
            }else{
                loteria.put("codigoBarra", codigoBarraQR);
                loteria.put("codigoQr", "");
            }
            loteria.put("razon", "Cancelado desde movil");
            loteria.put("idUsuario", Utilidades.getIdUsuario(mActivity));
            loteria.put("idBanca", Utilidades.getIdBanca(mActivity));

            datosObj.put("datos", loteria);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String jsonString = datosObj.toString();
        final String requestBody = datosObj.toString();
        duplicarHttp g = new duplicarHttp(datosObj);
        g.execute();

//        HttpURLConnection client = null;
//        try {
//            URL url = new URL(Utilidades.URL +"/api/principal/duplicar");
//            client = (HttpURLConnection) url.openConnection();
//            client.setRequestMethod("POST");
//            client.setRequestProperty("datos",loteria.toString());
//            client.setDoOutput(true);
//
//            OutputStream outputPost = new BufferedOutputStream(client.getOutputStream());
//
//            outputPost.flush();
//            outputPost.close();
//        }catch(MalformedURLException error) {
//            //Handles an incorrectly entered URL
//        }
//        catch(SocketTimeoutException error) {
//            //Handles URL access timeout.
//        }
//        catch (IOException error) {
//            //Handles input and output errors
//        }finally {
//            if(client != null) // Make sure the connection is not null.
//                client.disconnect();
//        }

        //mQueue.add(request);
//        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }



    public static void duplicarTicketViejo(String codigoBarraQR, boolean esQR){
        String url = Utilidades.URL +"/api/principal/duplicar";

        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            if(esQR){
                loteria.put("codigoBarra", "");
                loteria.put("codigoQr", codigoBarraQR);
            }else{
                loteria.put("codigoBarra", codigoBarraQR);
                loteria.put("codigoQr", "");
            }
            loteria.put("razon", "Cancelado desde movil");
            loteria.put("idUsuario", Utilidades.getIdUsuario(mActivity));
            loteria.put("idBanca", Utilidades.getIdBanca(mActivity));

            datosObj.put("datos", loteria);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String jsonString = datosObj.toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String errores = response.getString("errores");
                            if(errores.equals("0")){
                                /********* CODIGO ANTIGUO *************/
                                JSONArray jsonArray = response.getJSONArray("jugadas");
                                JSONArray jsonArrayLoterias = response.getJSONArray("loterias");
                                /********* END CODIGO ANTIGUO *************/

                                //PrincipalFragment.jugadasClase.duplicar(jsonArray);
                                //listener.setDuplicar(jsonArray);
//                                PrincipalFragment favoritesFragment = (PrincipalFragment) getSupportFragmentManager()
//                                        .getFragments()
//                                        .get(1);
//                                Log.d("Main2Activity", "fragment: " + getSupportFragmentManager()
//                                        .getFragments()
//                                        .get(1).getClass().getSimpleName().toString());
                                PrincipalFragment favoritesFragment;
                                if(((FragmentActivity)mActivity).getSupportFragmentManager().getFragments().get(0).getClass().getSimpleName().toString().equals("PrincipalFragment"))
                                    favoritesFragment = (PrincipalFragment) ((FragmentActivity)mActivity).getSupportFragmentManager().getFragments().get(0);
                                else if(((FragmentActivity)mActivity).getSupportFragmentManager().getFragments().get(1).getClass().getSimpleName().toString().equals("PrincipalFragment"))
                                    favoritesFragment = (PrincipalFragment) ((FragmentActivity)mActivity).getSupportFragmentManager().getFragments().get(1);
                                else
                                    favoritesFragment = (PrincipalFragment) ((FragmentActivity)mActivity).getSupportFragmentManager().getFragments().get(2);

                                //favoritesFragment.duplicar(jsonArray, jsonArrayLoterias);
                                tabLayout.getTabAt(1).select();

                                try{
                                    /********* CODIGO ANTIGUO *************/
                                    duplicarDatos = new JSONObject();
                                    duplicarDatos.put("jugadas", jsonArray);
                                    duplicarDatos.put("loterias", jsonArrayLoterias);
                                    /********* CODIGO ANTIGUO *************/



                                    DuplicarAvanzadoDialog duplicarAvanzadoDialog = new DuplicarAvanzadoDialog();
                                    duplicarAvanzadoDialog.show(mActivity.getSupportFragmentManager(), "Duplicar");

                                }catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(mContext, "Error a duplicar", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else
                                Toast.makeText(mContext, response.getString("mensaje") + " e: " + errores, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            Log.d("Error: ", e.toString());
                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("responseerror: ", String.valueOf(error));
                error.printStackTrace();
                if(error instanceof NetworkError){
                    Toast.makeText(mContext, "Verifique coneccion e intente de nuevo", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof ServerError){
                    Toast.makeText(mContext, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof TimeoutError){
                    Toast.makeText(mContext, "Conexion lenta, verifique conexion e intente de nuevo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //mQueue.add(request);
        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public static void duplicarTicketPrueba(String codigoBarraQR, boolean esQR){
        String url = Utilidades.URL +"/api/principal/duplicar";

        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            if(esQR){
                loteria.put("codigoBarra", "");
                loteria.put("codigoQr", codigoBarraQR);
            }else{
                loteria.put("codigoBarra", codigoBarraQR);
                loteria.put("codigoQr", "");
            }
            loteria.put("razon", "Cancelado desde movil");
            loteria.put("idUsuario", Utilidades.getIdUsuario(mActivity));
            loteria.put("idBanca", Utilidades.getIdBanca(mActivity));

            datosObj.put("datos", loteria);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String jsonString = datosObj.toString();
        final String requestBody = datosObj.toString();

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                Log.i("Voli", response.data.toString());
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        //mQueue.add(request);
        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }


    public static boolean tieneJugadasPendientes(){
        boolean tienePendientes = false;
        try {



            for (JugadaClass jugada : ventasClasses.getJugadas()) {

                if(jugada.getStatus() == 0) {
                    tienePendientes = true;
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            tienePendientes = true;
        }

        return tienePendientes;
    }

    private static String obtenerAtributoJsonObjectTicket(String atributo){

        try{

            Log.d("Main2Activit", "obtenerAtributo: " + Main2Activity.pagarTicketDatos.toString());
            return Main2Activity.pagarTicketDatos.getString(atributo);
        }catch (Exception e){
            e.printStackTrace();
            Log.d("Main2Activit", "obtenerAtributo: " + Main2Activity.pagarTicketDatos.toString());
            return "";
        }
    }

    public static void pagarTicket(String codigoBarraQR, boolean esQR){
        String url = Utilidades.URL +"/api/principal/pagar";
        boolean tienePremioYtienePendiente = false;

        double montoAPagar = ventasClasses.getMontoAPagar();
        if(montoAPagar > 0){
            if(tieneJugadasPendientes()){
                tienePremioYtienePendiente = true;
//                if(JPrinterConnectService.isPrinterConnected() == false){
//                    Toast.makeText(mContext, "Debe conectarse a una impresora", Toast.LENGTH_SHORT).show();
//                    mostrarFragmentDialogBluetoothSearch();
////                mostrarDispositivosBluetooth();
//                    return;
//                }

                if(Utilidades.hayImpresorasRegistradas(mContext) == false){
                    Main2Activity.txtBluetooth.performClick();
                    Toast.makeText(mContext, "Debe registrar una impresora", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        final boolean tienePremioYtienePendienteFinal = tienePremioYtienePendiente;
        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            if(esQR){
                loteria.put("codigoBarra", "");
                loteria.put("codigoQr", codigoBarraQR);
            }else{
                loteria.put("codigoBarra", codigoBarraQR);
                loteria.put("codigoQr", "");
            }
            loteria.put("razon", "Cancelado desde movil");
            loteria.put("idUsuario", Utilidades.getIdUsuario(mActivity));
            loteria.put("idBanca", Utilidades.getIdBanca(mActivity));

            datosObj.put("datos", loteria);



        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String jsonString = datosObj.toString();
        pagarTicketHttp p = new pagarTicketHttp(datosObj, tienePremioYtienePendiente);
        p.execute();

//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            String errores = response.getString("errores");
//                            if(errores.equals("0")){
//                                if(tienePremioYtienePendienteFinal){
//                                    //Crear un listener el archivo BluetoothSearchDialog para que cuando se conecte a la impresora
//                                    //este retorne la conexion aqui y se pueda imprimir, tambien crear opcion para que solamente se impriman
//                                    //las jugadas pendientes
////                                    es.submit(new BluetoothSearchDialog.TaskPrint(response, 2));
//                                    Utilidades.imprimir(mContext, response, 4);
//                                }
//                                Toast.makeText(mActivity, response.getString("mensaje") + " e: " + errores, Toast.LENGTH_LONG).show();
//                            }
//                            else
//                                Toast.makeText(mActivity, response.getString("mensaje") + " e: " + errores, Toast.LENGTH_LONG).show();
//
//                        } catch (JSONException e) {
//                            Log.d("Error: ", e.toString());
//                            e.printStackTrace();
//
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("responseerror: ", String.valueOf(error));
//                error.printStackTrace();
//                if(error instanceof NetworkError){
//                    Toast.makeText(mActivity, "Verifique coneccion e intente de nuevo", Toast.LENGTH_SHORT).show();
//                }
//                else if(error instanceof ServerError){
//                    Toast.makeText(mActivity, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
//                }
//                else if(error instanceof TimeoutError){
//                    Toast.makeText(mActivity, "Conexion lenta, verifique conexion e intente de nuevo", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
////        mQueue.add(request);
//        MySingleton.getInstance(mActivity).addToRequestQueue(request);
    }

    public static class pagarTicketHttp extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;
        JSONObject data;
        boolean tienePremioYtienePendiente;

        public pagarTicketHttp(JSONObject data, boolean tienePremioYtienePendiente) {
            this.data = data;
            this.tienePremioYtienePendiente = tienePremioYtienePendiente;
        }

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();

            try {
                //URL url = new URL("https://api.github.com/users/dmnugent80/repos");
                URL url = new URL(Utilidades.URL +"/api/principal/pagar");
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

                Log.e("pagarTicketHttp", result.toString());

                //SE BUSCA EL INDEX DE LAS JUGADAS EN EL JSONSTRING
//                int idx = result.toString().indexOf("\"jugadas\"");
//                String jugadasJsonString = "{" + result.toString().substring(idx, result.toString().length());
//                String loteriasJsonString = result.toString().substring(0, idx - 1) + "}";




            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }


            return result.toString();
        }
        @Override
        protected void onPostExecute(String result) {

            if(!result.equals("Error")) {
                //Do something with the JSON string

                 ventasClasses = llenarVenta(result.toString(), "venta");
                if (errores == 1) {
                        Toast.makeText(mContext, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                        return;
                    }

                Log.e("pagarTicketHttp", ventasClasses.getCodigo());

                if(this.tienePremioYtienePendiente){
                    //Crear un listener el archivo BluetoothSearchDialog para que cuando se conecte a la impresora
                    //este retorne la conexion aqui y se pueda imprimir, tambien crear opcion para que solamente se impriman
                    //las jugadas pendientes
                    Utilidades.imprimir(mContext, ventasClasses, 4);
                    Toast.makeText(mContext, "Se pago correctamente", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(mContext, "Se pago correctamente", Toast.LENGTH_LONG).show();
                }


//                llenarJugadasLista(jugadasJsonString);
//                llenarLoteriasLista(loteriasJsonString);

//                tabLayout.getTabAt(1).select();
//                DuplicarAvanzadoDialog duplicarAvanzadoDialog = new DuplicarAvanzadoDialog();
//                duplicarAvanzadoDialog.show(mActivity.getSupportFragmentManager(), "Duplicar");
            }else{
                Toast.makeText(mContext, "Error del servidor", Toast.LENGTH_SHORT).show();
            }
        }
    }

        public static VentasClass llenarVenta(String ventasJsonString, String nombreSolicitado) {
//        if(jugadasLista != null)
//            jugadasLista.clear();
            VentasClass ventasClass = null;
            Gson gson = new GsonBuilder().create();
            errores = 0;
            try (com.google.gson.stream.JsonReader reader1 = new com.google.gson.stream.JsonReader(new StringReader(ventasJsonString))) {
                //reader1.beginObject();

                int c = 0;
                String nombre = "";
                while (reader1.hasNext()) {

                    Log.e("llenarJugadaLoterias", "nombre:" + nombre +" token:" + reader1.peek());
                    if (JsonToken.BEGIN_ARRAY.equals(reader1.peek())) {


                        reader1.beginArray();

                    }

                    if (JsonToken.BEGIN_OBJECT.equals(reader1.peek())) {

                        if(nombre.equals(nombreSolicitado)){
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
            } catch (Exception e) {
                e.printStackTrace();
                return ventasClass;
            }
            return ventasClass;
        }


    public static void buscarTicketAPagar(String codigoBarraQR, boolean esQR){
        String url = Utilidades.URL +"/api/principal/buscarTicketAPagar";

        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            if(esQR){
                loteria.put("codigoBarra", "");
                loteria.put("codigoQr", codigoBarraQR);
            }else{
                loteria.put("codigoBarra", codigoBarraQR);
                loteria.put("codigoQr", "");
            }
            loteria.put("razon", "Cancelado desde movil");
            loteria.put("idUsuario", Utilidades.getIdUsuario(mActivity));
            loteria.put("idBanca", Utilidades.getIdBanca(mActivity));

            datosObj.put("datos", loteria);



        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String jsonString = datosObj.toString();
        buscarTicketAPagarHttp b = new buscarTicketAPagarHttp(datosObj);
        b.execute();

//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            String errores = response.getString("errores");
//                            if(errores.equals("0")){
//                                pagarTicketDatos = response.getJSONObject("venta");
//                                VerTicketPagarDialog verTicketPagarDialog = new VerTicketPagarDialog();
//                                verTicketPagarDialog.show(mActivity.getSupportFragmentManager(), "Duplicar dialog");
//                                //Toast.makeText(mActivity, response.getString("mensaje") + " e: " + errores, Toast.LENGTH_SHORT).show();
//                            }
//                            else
//                                Toast.makeText(mActivity, response.getString("mensaje") + " e: " + errores, Toast.LENGTH_LONG).show();
//
//                        } catch (JSONException e) {
//                            Log.d("Error: ", e.toString());
//                            e.printStackTrace();
//
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("responseerror: ", String.valueOf(error));
//                error.printStackTrace();
//                if(error instanceof NetworkError){
//                    Toast.makeText(mActivity, "Verifique coneccion e intente de nuevo", Toast.LENGTH_SHORT).show();
//                }
//                else if(error instanceof ServerError){
//                    Toast.makeText(mActivity, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
//                }
//                else if(error instanceof TimeoutError){
//                    Toast.makeText(mActivity, "Conexion lenta, verifique conexion e intente de nuevo", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
////        mQueue.add(request);
//        MySingleton.getInstance(mActivity).addToRequestQueue(request);
    }

        public static class buscarTicketAPagarHttp extends AsyncTask<String, String, String> {

            HttpURLConnection urlConnection;
            JSONObject data;

            public buscarTicketAPagarHttp(JSONObject data) {
                this.data = data;
            }

            @Override
            protected String doInBackground(String... args) {

                StringBuilder result = new StringBuilder();

                try {
                    //URL url = new URL("https://api.github.com/users/dmnugent80/repos");
                    URL url = new URL(Utilidades.URL +"/api/principal/buscarTicketAPagar");
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
                    Log.e("buscarTicketAPagar", "result: " + result.toString() );


                    //SE BUSCA EL INDEX DE LAS JUGADAS EN EL JSONSTRING
                    //int idx = result.toString().indexOf("\"jugadas\"");
                    //String jugadasJsonString = "{"+ result.toString().substring(idx, result.toString().length());
                    //String loteriasJsonString = result.toString().substring(0, idx - 1) + "}";

                    //SE LLENAN LAS LISTAS CON LOS JSONSTRING
//                    VentasClass ventasClass = llenarVenta(result.toString(), "venta");
//                    Log.e("buscarTicketAPagar", ventasClass.getCodigo());
//                    Bundle arguments = new Bundle();
//                    arguments.putParcelable("venta", ventasClass);
//                    VerTicketPagarDialog verTicketPagarDialog = new VerTicketPagarDialog();
//                    verTicketPagarDialog.setArguments(arguments);
//                    verTicketPagarDialog.show(mActivity.getSupportFragmentManager(), "Duplicar dialog");


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }


                return result.toString();
            }

            @Override
            protected void onPostExecute(String result) {

                if(!result.equals("Error")) {
                    //Do something with the JSON string
                     ventasClasses = llenarVenta(result.toString(), "venta");



                        if (errores == 1) {
                            Toast.makeText(mContext, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    Bundle arguments = new Bundle();
                    arguments.putParcelable("venta", ventasClasses);
                    VerTicketPagarDialog verTicketPagarDialog = new VerTicketPagarDialog();
                    verTicketPagarDialog.setArguments(arguments);
                    verTicketPagarDialog.show(mActivity.getSupportFragmentManager(), "Duplicar dialog");


                }
                else{
                    Toast.makeText(mContext, "Error del servidor", Toast.LENGTH_SHORT).show();
                }
            }
        }

    //Para poder llamar al metodo escribir desde xml que esta en el fragment
    //Obligatoriamente debo hacerlo de esta manera porque el xml buscara el metodo escribir en el activity y no en el fragment
//    public void escribir(View v) {
//        PrincipalFragment p = new PrincipalFragment();
//        p.escribir(v);
//    }

    public interface DuplicarPrincipalInterface{
        void setDuplicar(JSONArray jugadas);
    }

    public static void mostrarFragmentDialogBluetoothSearch(){
        BluetoothSearchDialog duplicarDialog = new BluetoothSearchDialog();
        duplicarDialog.show( mActivity.getSupportFragmentManager(), "Duplicar dialog");
//                mostrarDispositivosBluetooth();
    }



}
