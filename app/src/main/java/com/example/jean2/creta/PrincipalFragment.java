package com.example.jean2.creta;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jean2.creta.Clases.BancaClass;
import com.example.jean2.creta.Clases.JugadaClass;
import com.example.jean2.creta.Clases.LoteriaClass;
import com.example.jean2.creta.Clases.PrinterClass;
import com.example.jean2.creta.Clases.VentasClass;
import com.example.jean2.creta.Servicios.ActualizarService;
import com.example.jean2.creta.Servicios.JPrinterConnectService;
import com.example.jean2.creta.Servicios.PrinterService;
import com.example.jean2.creta.Servicios.VerificarAccesoAlSistemaService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonToken;
import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.content.WebViewContent;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static com.example.jean2.creta.Utilidades.combinarBitmap;


/**
 * A simple {@link Fragment} subclass.
 */
public class PrincipalFragment extends Fragment implements View.OnClickListener, Main2Activity.DuplicarPrincipalInterface {
    private static String idVenta = null;
    public static int idBanca = 0;

    static Context mContext;

    private static int descontar = 0;
    private static int deCada = 0;
    private static float montoTotal = 0;
    private static int montoDescuento = 0;
    private TextView txtSelected;
    IconManager iconManager;
    CheckBox c;
    static Spinner spinnerTicket;
   // private RequestQueue mQueue;

    ProgressBar progressBar;
    public static String[] listDescripcionLoterias;
    String[] ventasItems;
    String[] ventasIdTicketSecuenciaItems;
    boolean[] checkedItems;
    ArrayList<Integer> posicionDeNuevasLoteriasAnadidasParaDuplicar = new ArrayList<>();
    ArrayList<Integer> mUserItems = new ArrayList<>();
    public static HashMap<Integer,String> idLoteriasMap = new HashMap<Integer, String>();
    HashMap<Integer,String> codigoBarraMap = new HashMap<Integer, String>();
    static JSONArray jsonArrayVentas;
    static VentasClass venta;
    static List<VentasClass> ventasLista = new ArrayList<VentasClass>();
    static List<LoteriaClass> loteriasLista = new ArrayList<LoteriaClass>();
    static List<BancaClass> bancasLista = new ArrayList<BancaClass>();
    static String imgHtmlTmp;
    static int errores;
    static String mensaje;

    View view;
    TextView txtBanca;
    TextView txtJugada;
    TextView txtMontojugar;
    TextView txtMontodisponible;
    private static TextView txtTotal;
    private static TextView txtDescuento;
    private static TextView txtPrint;
    private static CheckBox_Icon ckbDescuento;
    private static CheckBox_Icon ckbPrint;
    private static CheckBox_Icon ckbSms;
    private static CheckBox_Icon ckbWhatsapp;
    public static JSONArray jugadas = new JSONArray();
    public static List<JugadaClass> jugadasClase = new ArrayList<JugadaClass>();
    public static WebView webViewImg;

    final private int REQUEST_CODE_ASK_PERMISSION = 111;
    private boolean cambioCursorDesdeElBotonEnter = false;


    String monto;
    boolean estoyProbando = false;

    boolean jugada_monto_active = true;


    static ExecutorService es = Executors.newScheduledThreadPool(30);

    public PrincipalFragment() {
        // Required empty public constructor
    }

//    public void getJugadas(){
//        jugadas = jugadasClase.getJsonArrayJugadas();
//        Log.d("getJugadas:", jugadas.toString());
//    }

    public void iniciarServicio(){
        mContext.startService(new Intent(getActivity(), VerificarAccesoAlSistemaService.class));
       // mContext.startService(new Intent(getActivity(), ActualizarService.class));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void comprobarPermisos(){
        int permisosCamara = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
        int permisosStorage = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        int permisosSms = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.SEND_SMS);
        int permisosLocation = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);


        //|| permisosSms != PackageManager.PERMISSION_GRANTED
        //, Manifest.permission.SEND_SMS
        if(permisosStorage != PackageManager.PERMISSION_GRANTED || permisosCamara != PackageManager.PERMISSION_GRANTED || permisosLocation != PackageManager.PERMISSION_GRANTED){
            //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSION);
            //}
        }

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        iniciarServicio();
        comprobarPermisos();
        iconManager = new IconManager();

        Log.e("PrincipalFragment", "onCreateView");


        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_principal, container, false);
        view = inflater.inflate(R.layout.fragment_principal, container, false);

        borderChange(true, false);

        //mQueue = Volley.newRequestQueue(getActivity());




        progressBar = (ProgressBar)view.findViewById(R.id.progressBarPrincipal);
        spinnerTicket = (Spinner)view.findViewById(R.id.spinnerTickets);
        txtBanca = (TextView)view.findViewById(R.id.txtBanca);
        txtSelected = (TextView)view.findViewById(R.id.txtItemSelected);

        txtJugada = (TextView)view.findViewById(R.id.txtJugada);
        txtMontojugar = (TextView)view.findViewById(R.id.txtMontojugar);
        txtMontodisponible = (TextView)view.findViewById(R.id.txtMontodisponible);
        txtTotal = (TextView)view.findViewById(R.id.txtTotal);
        txtDescuento = (TextView)view.findViewById(R.id.txtDescuento);
        ckbDescuento = (CheckBox_Icon)view.findViewById(R.id.ckbDescuento);
        ckbPrint = (CheckBox_Icon)view.findViewById(R.id.ckbPrint);
        ckbPrint.setChecked(true);
        ckbSms = (CheckBox_Icon)view.findViewById(R.id.ckbSms);
        ckbWhatsapp = (CheckBox_Icon)view.findViewById(R.id.ckbWhatsapp);

        ckbPrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    ckbSms.setChecked(false);
                    ckbWhatsapp.setChecked(false);
                }
            }
        });
        ckbSms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    ckbPrint.setChecked(false);
                    ckbWhatsapp.setChecked(false);
                }
            }
        });
        ckbWhatsapp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    ckbSms.setChecked(false);
                    ckbPrint.setChecked(false);
                }
            }
        });

        txtBanca.setText(Utilidades.getBanca(getContext()));
        if(estoyProbando)
            fillSpinnerWithoutInternetTest();
        else
            jsonParse();



        ckbDescuento.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                calcularTotal();
            }
        });

        TextView_Icon txtDelete = (TextView_Icon)view.findViewById(R.id.txtDelete);
        TextView_Icon txtPrint = (TextView_Icon)view.findViewById(R.id.txtPrint);
        Button btn0 = (Button)view.findViewById(R.id.btn0);
        Button btn1 = (Button)view.findViewById(R.id.btn1);
        Button btn2 = (Button)view.findViewById(R.id.btn2);
        Button btn3 = (Button)view.findViewById(R.id.btn3);
        Button btn4 = (Button)view.findViewById(R.id.btn4);
        Button btn5 = (Button)view.findViewById(R.id.btn5);
        Button btn6 = (Button)view.findViewById(R.id.btn6);
        Button btn7 = (Button)view.findViewById(R.id.btn7);
        Button btn8 = (Button)view.findViewById(R.id.btn8);
        Button btn9 = (Button)view.findViewById(R.id.btn9);
        Button btnMas = (Button)view.findViewById(R.id.btnMas);
        Button btnMenos = (Button)view.findViewById(R.id.btnMenos);
        Button btnEnter = (Button)view.findViewById(R.id.btnEnter);
        ImageView btnBackspace = (ImageView) view.findViewById(R.id.btnBackspace);
        Button btnSlash = (Button)view.findViewById(R.id.btnSlash);
        Button btnPunto = (Button)view.findViewById(R.id.btnPunto);


//        Button btnD = (Button)view.findViewById(R.id.btnD);
//        Button btnQ = (Button)view.findViewById(R.id.btnQ);


        txtJugada.setOnClickListener(this);
        txtMontojugar.setOnClickListener(this);
        txtDelete.setOnClickListener(this);
        txtPrint.setOnClickListener(this);
        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btnBackspace.setOnClickListener(this);
        btnMas.setOnClickListener(this);
        btnMenos.setOnClickListener(this);
        btnSlash.setOnClickListener(this);
        btnEnter.setOnClickListener(this);
        btnPunto.setOnClickListener(this);
//        btnD.setOnClickListener(this);
//        btnQ.setOnClickListener(this);




        txtSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle("Seleccionar loteria");
                mBuilder.setMultiChoiceItems(listDescripcionLoterias, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        Log.d("Hey", "HKk");
//                        if (isChecked) {
//                            if (!mUserItems.contains(position)) {
//                                mUserItems.add(position);
//                            }
//                        } else if (mUserItems.contains(position)) {
//                            mUserItems.remove(position);
//                        }
                        if(isChecked){
                            mUserItems.add(position);
                        }else{
                            mUserItems.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mUserItems.size(); i++) {
                            item = item + listDescripcionLoterias[mUserItems.get(i)];
                            if (i != mUserItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        txtSelected.setText(item);
                    }
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mUserItems.clear();
                            txtSelected.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });



        calcularTotal();

        return view;
    }



    @Override
    public void onAttach(Context context) {

        mContext=(FragmentActivity) context;
        super.onAttach(context);
    }

    public void seleccionarLoteriasMultiSelect(JSONArray loteriasASeleccionar, boolean seleccionarPrimeraLoteria ){
        try {
            Log.d("SeleccionarLoteria1", "primero");
            if(seleccionarPrimeraLoteria){
                if(listDescripcionLoterias.length == 0){
                    return;
                }

                Log.d("SeleccionarLoteria1", "segundo");

                checkedItems[0] = true;
                mUserItems = new ArrayList<>();
                mUserItems.add(0);
                String loteria = listDescripcionLoterias[0];


                Log.d("SeleccionarLoteria1", "tercero: " + loteria);
                txtSelected.setText(loteria);
            }else{
                mUserItems = new ArrayList<>();
                for (int i=0; i< loteriasASeleccionar.length(); i++){
                    JSONObject item = (JSONObject)loteriasASeleccionar.get(i);
                    for(int c=0; c < listDescripcionLoterias.length; c++){
                        if(item.getString("descripcion").toString().equals(listDescripcionLoterias[c])){
                            checkedItems[c] = true;
                            mUserItems.add(c);
                        }
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.d("errorSeleccionarLoteria", e.toString());
        }
    }

    public boolean existePosicionDeNuevasLoteriasAnadidasParaDuplicar(int posicion){
        for (int c =0; c < posicionDeNuevasLoteriasAnadidasParaDuplicar.size(); c++){
            if(posicionDeNuevasLoteriasAnadidasParaDuplicar.get(c) == posicion)
                return true;
        }

        return false;
    }

    public void duplicar(final JSONArray jugadas, JSONArray loterias){


//        jugadasClase.removeAll();
//        for(int i=0; i < listDescripcionLoterias.length; i++){
//            checkedItems[i] = false;
//            mUserItems.clear();
//        }
        limpiar();
//        try {
//            for (int i=0; i< loterias.length(); i++){
//                JSONObject item = (JSONObject)loterias.get(i);
//                for(int c=0; c < listDescripcionLoterias.length; c++){
//                    if(item.getString("descripcion").toString().equals(listDescripcionLoterias[c])){
//                        checkedItems[c] = true;
//                        mUserItems.add(c);
//                    }
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }

//        seleccionarLoteriasMultiSelect(loterias, false);
//
//        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
//        mBuilder.setTitle("Seleccionar loteria");
//        mBuilder.setMultiChoiceItems(listDescripcionLoterias, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
//                if(isChecked){
//                    mUserItems.add(position);
//                    posicionDeNuevasLoteriasAnadidasParaDuplicar.add(position);
//                }else{
//                    mUserItems.remove((Integer.valueOf(position)));
//                    if(existePosicionDeNuevasLoteriasAnadidasParaDuplicar(position))
//                        posicionDeNuevasLoteriasAnadidasParaDuplicar.remove((Integer.valueOf(position)));
//                }
//            }
//        });
//
//        mBuilder.setCancelable(false);
//        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int which) {
//                String item = "";
//                for (int i = 0; i < mUserItems.size(); i++) {
//                    item = item + listDescripcionLoterias[mUserItems.get(i)];
//                    if (i != mUserItems.size() - 1) {
//                        item = item + ", ";
//                    }
//                   // Toast.makeText(mContext, listDescripcionLoterias[mUserItems.get(i)] + " " + idLoteriasMap.get(mUserItems.get(i)), Toast.LENGTH_LONG).show();
//                    try {
//                        //Si la loteria no se ha anadido, osea que es la loteria orinal que tiene la jugada entonces entrara al ciclo
//                        // y solo se duplicaran las jugadas que pertenezcan a esta loteria
//                        if(!existePosicionDeNuevasLoteriasAnadidasParaDuplicar(i)){
//                            for (int c=0; c< jugadas.length(); c++){
//
//
//                                //Si la loteria es igual a la loteria que tiene la jugada entonces se duplicara la jugada
//                                if(((JSONObject) jugadas.get(c)).getString("idLoteria").equals(idLoteriasMap.get(mUserItems.get(i)))){
//                                    JSONObject jugadaObject = new JSONObject();
//                                    String jugada = ((JSONObject) jugadas.get(c)).getString("jugada");
//                                    jugadaObject.put("jugada", Utilidades.agregarGuionPorSorteo(jugada, ((JSONObject) jugadas.get(c)).getString("sorteo")));
//
//                                    jugadaObject.put("descripcion", listDescripcionLoterias[mUserItems.get(i)]);
//                                    jugadaObject.put("idLoteria", idLoteriasMap.get(mUserItems.get(i)));
//                                    jugadaObject.put("monto", (int)Float.parseFloat(((JSONObject) jugadas.get(c)).getString("monto").toString()));
//                                    Log.d("PrincipalFragment", "Duplicar jugada: " + jugadaObject.getString("descripcion") + " " +jugadaObject.getString("jugada"));
//                                    jugadasClase.add(jugadaObject);
//                                }
//                            }
//                        }else{
//                            //Como la loteria se ha anadido para duplicar entonces se duplicaran todas las jugadas del ticket para
//                            // esta loteria, sin importar que no sean de esta loteria
//                            for (int c=0; c< jugadas.length(); c++){
//                                JSONObject jugadaObject = (JSONObject)jugadas.get(c);
//                                jugadaObject.put("descripcion", listDescripcionLoterias[mUserItems.get(i)]);
//                                jugadaObject.put("idLoteria", idLoteriasMap.get(mUserItems.get(i)));
//                                String jugada = ((JSONObject) jugadas.get(c)).getString("jugada");
//                                jugadaObject.put("jugada", Utilidades.agregarGuionPorSorteo(jugada, ((JSONObject) jugadas.get(c)).getString("sorteo")));
//                                jugadaObject.put("monto", (int)Float.parseFloat(jugadaObject.getString("monto").toString()));
//                                Log.d("PrincipalFragment", "Duplicar jugada: " + jugadaObject.getString("descripcion") + " " +jugadaObject.getString("jugada"));
//                                jugadasClase.add(jugadaObject);
//                            }
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//                calcularTotal();
//                txtSelected.setText(item);
//                Log.d("PrincipalFragment", "Duplicar jugada objeto: " + jugadasClase.getJsonArrayJugadas().toString());
//            }
//        });
//
//
//
//
//
//
//        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
//
//
//
//        mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int which) {
//                for (int i = 0; i < checkedItems.length; i++) {
//                    checkedItems[i] = true;
//                    mUserItems.clear();
//                    txtSelected.setText("");
////                    if(jugadas.length() > 0){
////                        for(int c=0)
////                    }
//                }
//            }
//        });
//
////        AlertDialog mDialog = mBuilder.create();
//        AlertDialog alertDialog = mBuilder.create();
//
//        //Este evento se dispara cuando se agregan o quitan elemento de la vista
//        alertDialog.getListView().setOnHierarchyChangeListener(
//                new ViewGroup.OnHierarchyChangeListener() {
//                    @Override
//                    public void onChildViewAdded(View parent, final View child) {
//                        CharSequence text = ((AppCompatCheckedTextView)child).getText();
//                        int itemIndex = Arrays.asList(listDescripcionLoterias).indexOf(text);
//                        if(checkedItems[itemIndex] == true){
//                            child.setEnabled(checkedItems[itemIndex]);
//                        }else{
//                            child.setEnabled(checkedItems[itemIndex]);
//                            child.setOnClickListener(null);
//                        }
//
////                        child.setOnClickListener(null);
////                        child.setOnClickListener(new View.OnClickListener() {
////                            @Override
////                            public void onClick(View view) {
////                                child.setEnabled(checkedItems[4]);
////                                Toast.makeText(mContext, "Dentro child", Toast.LENGTH_SHORT).show();
////                            }
////                        });
//
//                    }
//
//                    @Override
//                    public void onChildViewRemoved(View view, View view1) {
//                    }
//                });
//
//        //mDialog.getButton(DialogInterface.OnShowListener).performClick();
////        mDialog.show();
//        alertDialog.show();

    }

//    private boolean[] desabilitarLoteriasMultiselectDialog


    //seleccionar loteria de index 0

    @Override
    public void onClick(View v){
        String jugada = "";



        String p = "hola";
        String p2 = p.substring(1, 2).toString();
//        Log.d("Reemplazar:", p2);
//        Toast.makeText(getContext(), "p: " + p2, Toast.LENGTH_SHORT).show();
        if(jugada_monto_active){
            if(String.valueOf(txtJugada.getText()).length() >= 6 && v.getId() != R.id.btnBackspace && v.getId() != R.id.btnEnter){
                return;
            }
            jugada = String.valueOf(txtJugada.getText());
        }

        String caracteres = "";
        switch (v.getId()) {
            case (R.id.txtDelete):
                if(Utilidades.hayImpresorasRegistradas(mContext) == false){
                    Main2Activity.txtBluetooth.performClick();
                    Toast.makeText(mContext, "Debe registrar una impresora", Toast.LENGTH_SHORT).show();

                }
                else{
                    aceptaCancelarTicket();
                }

                break;
            case (R.id.txtPrint):
                print();
                break;
                case (R.id.txtJugada):
                borderChange(true, false);
                break;
            case (R.id.txtMontojugar):
                borderChange(false, false);
                break;
            case (R.id.btn0):
                caracteres = "0";
                break;
            case (R.id.btn1):
                caracteres = "1";
                break;
            case (R.id.btn2):
                caracteres = "2";
                break;
            case (R.id.btn3):
                caracteres = "3";
                break;
            case (R.id.btn4):
                caracteres = "4";
                break;
            case (R.id.btn5):
                caracteres = "5";
                break;
            case (R.id.btn6):
                caracteres = "6";
                break;
            case (R.id.btn7):
                caracteres = "7";
                break;
            case (R.id.btn8):
                caracteres = "8";
                break;
            case (R.id.btn9):
                caracteres = "9";
                break;
            case (R.id.btnBackspace):
                if(jugada_monto_active){
                    caracteres = String.valueOf(txtJugada.getText());
                    //caracteres = caracteres.substring(0, caracteres.length() - 2);
                    Log.d("Backspace: ", caracteres);
                    //return;
                    if(caracteres != "" && caracteres != null && caracteres.length() != 0){
                        //Eliminamos el ultimo caracter y asignamos el nuevo valor
                        caracteres = caracteres.substring(0, caracteres.length() - 1);
                        txtJugada.setText(caracteres);
                        return;
                    }
                }else{
                    caracteres = String.valueOf(txtMontojugar.getText());
                    if(caracteres != "" && caracteres != null && caracteres.length() != 0){
                        //Eliminamos el ultimo caracter y asignamos el nuevo valor
                        caracteres = caracteres.substring(0, caracteres.length() - 1);
                        txtMontojugar.setText(caracteres);
                        return;
                    }
                }
                break;
            case (R.id.btnMas):
                boolean existe_loteria_newyork = false;
                if(!jugada_monto_active) return;
                if(String.valueOf(txtJugada.getText()).length() == 3 || String.valueOf(txtJugada.getText()).length() == 4){
                    caracteres = "+";
                    caracteres = txtJugada.getText() + caracteres;
                    txtJugada.setText(caracteres);
                    getMontoDisponible();
                    return;
                }

                break;
            case (R.id.btnMenos):
                //print();
                if(String.valueOf(txtJugada.getText()).length() == 4){
                    caracteres = "-";
                    caracteres = txtJugada.getText() + caracteres;
                    txtJugada.setText(caracteres);
                    getMontoDisponible();
                    return;
                }else if(String.valueOf(txtJugada.getText()).length() == 0){
                    guardar();
                    return;
                }

                break;
            case (R.id.btnSlash):
                //Cambiar de loteria
                break;
            case (R.id.btnEnter):
                //Insertar jugadas o cambiar focus
                if(jugada_monto_active) {
                    getMontoDisponible();
                    return;
                } else{
                    jugadaAdd();
                    return;
                }

            case (R.id.btnPunto):
                if(jugada_monto_active){

                }else{

                    if(txtMontojugar.getText().toString().length() == 0){
                        caracteres = "0.";
                    }else{
                        int indexOf = String.valueOf(txtMontojugar.getText()).indexOf(".");

                        if(indexOf == -1){
                            Log.d("caracter1", String.valueOf(indexOf));
                            caracteres = txtMontojugar.getText().toString() + ".";
                            txtMontojugar.setText(caracteres);
                            return;
                        }
                    }

                }

        }

        Log.d("Caracter: ", String.valueOf(jugada_monto_active));



        if(jugada_monto_active){
            if(jugada.length() > 0){
                if(jugada.charAt(jugada.length() - 1) == '.' || jugada.charAt(jugada.length() - 1) == '+')
                {
                    Log.d("punto: ", jugada.substring(jugada.length() - 1));
                    return;
                }
            }

            caracteres = txtJugada.getText() + caracteres;
            txtJugada.setText(caracteres);
        }else{
            if(caracteres.length() > 0){
                if(caracteres.charAt(0) == '.' || caracteres.charAt(0) == '+')
                    return;
            }

            //Si cambio' al precionar el boton enter entonces eso quiere decir que es una nueva jugada
            //asi que el monto que tiene el campo monto debe ser sustituido con el nuevo caracter introducido
            //asi la experiencia del usuario es mucho mejor porque este no tendra que eliminar el monto precionando
            //la tecla backspace a cada instante, sino que se eliminara automaticamente cuando la variable
            //cambioCursorDesdeElBotonEnter sea verdadera, en caso contrario entonces simplemente se concatenara el nuevo valor
            if(cambioCursorDesdeElBotonEnter){
                caracteres = caracteres;
                borderChange(false, false);
            }else{
                caracteres = txtMontojugar.getText() + caracteres;
            }

            txtMontojugar.setText(caracteres);
        }
    }

    private void borderChange(boolean jugada_monto_active, boolean desdeBotonEnter){
        cambioCursorDesdeElBotonEnter = desdeBotonEnter;

        this.jugada_monto_active = jugada_monto_active;
        TextView txtPonerBorderActive, txtPonerBorderNormal;
        if(jugada_monto_active){
            txtPonerBorderActive = view.findViewById(R.id.txtJugada);
            txtPonerBorderNormal = view.findViewById(R.id.txtMontojugar);
        }else{
            txtPonerBorderActive = view.findViewById(R.id.txtMontojugar);
            txtPonerBorderNormal = view.findViewById(R.id.txtJugada);
        }

        if(txtMontojugar != null)
        Log.d("Changefocus1: ", txtMontojugar.getText().toString());
        txtPonerBorderActive.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_active_border));
        txtPonerBorderNormal.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_border));
        if(txtMontojugar != null)
        Log.d("Changefocus2: ", txtMontojugar.getText().toString());
    }

    public void setDescuento(){
        int total = 0;
        try {
            for (BancaClass banca : bancasLista){

                String id = String.valueOf(banca.id);
                String des = String.valueOf(banca.descontar);
                String deC = String.valueOf(banca.deCada);

                Log.e("setDescuento", String.valueOf(banca.descontar));
                //if(isInteger(id)){
                    if(id.equals(String.valueOf(idBanca))){

                        if(des.matches("\\d+(?:\\.\\d+)?") && deC.matches("\\d+(?:\\.\\d+)?")){

                            descontar = (int)Float.parseFloat(des);
                            deCada = (int)Float.parseFloat(deC);
                        }else{
                            descontar = 0;
                            deCada = 0;
                        }
                        return;
                    }
                //}

            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("ErrorDescuento:", e.toString());
        }
    }

    private void jsonParse(){
        String url = "https://loterias.ml/api/principal/indexPost";
//        progressBar.setVisibility(View.VISIBLE);
        Main2Activity.progressBarToolbar.setVisibility(View.VISIBLE);

        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            loteria.put("idUsuario", Utilidades.getIdUsuario(mContext));

            datosObj.put("datos", loteria);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        indexPostHttp i = new indexPostHttp(datosObj);
        i.execute();
        Log.d("PrincipalFragment", datosObj.toString());

//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Main2Activity.progressBarToolbar.setVisibility(View.GONE);
//                        try {
//                            idVenta = response.getString("idVenta");
//                            idBanca = response.getInt("idBanca");
//                            JSONArray jsonArray = response.getJSONArray("loterias");
//                            JSONArray jsonArrayBancas = response.getJSONArray("bancas");
//                            JSONArray jsonArrayVentas = response.getJSONArray("ventas");
//                            setDescuento(jsonArrayBancas);
//                            fillSpinner();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Log.d("Error: ", e.toString());
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if(error instanceof NetworkError){
//                    Toast.makeText(mContext, "Verifique coneccion y recargue la pagina", Toast.LENGTH_SHORT).show();
//                }
//                else if(error instanceof ServerError){
//                    Toast.makeText(mContext, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
//                }
//                else if(error instanceof TimeoutError){
//                    Toast.makeText(mContext, "Conexion lenta, verifique conexion y recargue de nuevo", Toast.LENGTH_SHORT).show();
//                }
//                Main2Activity.progressBarToolbar.setVisibility(View.GONE);
//                error.printStackTrace();
//                Log.d("responseerror: ", String.valueOf(error));
//            }
//        });
//
//        //mQueue.add(request);
//        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    private void getMontoDisponible(){
        String url = "https://loterias.ml/api/principal/montodisponible";
        monto = "0";
        Log.d("Arreglo loteria size: ", String.valueOf(mUserItems.size()));
        final String jugada = Utilidades.ordenarMenorAMayor(String.valueOf(txtJugada.getText()));
        if(jugada == "" || jugada == null || jugada.length() == 0 || jugada.length() == 1)
            return;
        if(mUserItems.size() == 0){
            Toast.makeText(getActivity(), "Debe seleccionar una loteria", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mUserItems.size() > 1){
            txtMontodisponible.setText("x");
            Main2Activity.progressBarToolbar.setVisibility(View.GONE);
            borderChange(false, true);
            Log.d("Getmonto:", txtMontojugar.getText().toString());
            return;
        }

        if(estoyProbando){
            txtMontodisponible.setText("20");
            return;
        }

        Main2Activity.progressBarToolbar.setVisibility(View.VISIBLE);

//            StringBuilder validarJugadaSeaNumerica = new StringBuilder(jugada);
//            if(jugada.length() == 3 || jugada.length() == 5){
//                //Si la jugada tiene 3 digitos y el ultimo digito
//                if(jugada.charAt(jugada.length() - 1) == '.' && jugada.length() == 3)
//                {
//                    validarJugadaSeaNumerica.setLength(jugada.length() - 1);
//                    Log.d("validar jug sea num.: ", validarJugadaSeaNumerica.toString());
//                }
//                else if(jugada.charAt(jugada.length() - 1) == '+' && jugada.length() == 5)
//                {
//                    validarJugadaSeaNumerica.setLength(jugada.length() - 1);
//                    Log.d("validar jug sea num+: ", validarJugadaSeaNumerica.toString());
//                }
//                else{
//                    Toast.makeText(getActivity(), "La jugada debe tener 2, 4 o 6 digitos", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }

//            if(!isInteger(validarJugadaSeaNumerica.toString())){
//                Toast.makeText(getActivity(), "La jugada debe ser correcta", Toast.LENGTH_SHORT).show();
//                return;
//            }




        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            loteria.put("idLoteria", loteriasLista.get(mUserItems.get(0)).getId());
            loteria.put("jugada", jugada);
            loteria.put("idBanca", Utilidades.getIdBanca(mContext));

            datosObj.put("datos", loteria);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String jsonString = datosObj.toString();
        borderChange(false, true);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Main2Activity.progressBarToolbar.setVisibility(View.GONE);
                        try {
                            double montoDisponible = Utilidades.siJugadaExisteRestarMontoDisponible(jugadasClase, jugada, String.valueOf(loteriasLista.get(mUserItems.get(0)).getId()), response.getDouble("monto"));
                            if(montoDisponible < 0)
                                montoDisponible = 0;

                            txtMontodisponible.setText(String.valueOf(montoDisponible));

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
                Main2Activity.progressBarToolbar.setVisibility(View.GONE);
                borderChange(true, false);
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


    private void limpiar(){
        //Deseleccionamos las loterias del multiselect
        for(int i=0; i < listDescripcionLoterias.length; i++){
            checkedItems[i] = false;
            mUserItems.clear();
        }
        txtMontojugar.setText("");
        mUserItems.clear();
        txtSelected.setText("");
        jugadasClase.clear();
        calcularTotal();
        JugadasFragment.updateTable();
    }

    private static Bitmap screenshot(WebView webView, String img) {
//        webView.loadUrl(img);
        webView.setVisibility(View.VISIBLE);
        webView.loadData(img, "text/html", "UTF-8");
        webView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        webView.layout(0, 0, webView.getMeasuredWidth(),
                webView.getMeasuredHeight());
        webView.setDrawingCacheEnabled(true);
        webView.buildDrawingCache();

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        Bitmap bitmap = Bitmap.createBitmap(webView.getMeasuredWidth(),
                webView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        webView.setVisibility(View.INVISIBLE);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        int iHeight = bitmap.getHeight();
        canvas.drawBitmap(bitmap, 0, iHeight, paint);
        webView.draw(canvas);
        return bitmap;
    }


    private void guardar(){
        String url = "https://loterias.ml/api/principal/guardarMovil";


        if(jugadasClase.size() == 0){
            Toast.makeText(mContext, "Debe realizar al menos una jugada", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ckbPrint.isChecked()){
//            if(JPrinterConnectService.isPrinterConnected() == false){
//                Toast.makeText(mContext, "Debe conectarse a una impresora", Toast.LENGTH_SHORT).show();
//                Main2Activity.txtBluetooth.performClick();
//                return;
//            }

            if(Utilidades.hayImpresorasRegistradas(mContext) == false){
                Main2Activity.txtBluetooth.performClick();
                Toast.makeText(mContext, "Debe registrar una impresora", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Main2Activity.progressBarToolbar.setVisibility(View.VISIBLE);
        JSONObject jugada = new JSONObject();
        JSONObject datosObj = new JSONObject();
        int[] arregloLoterias = new int[1];
        arregloLoterias[0] = 1;

        try {
            int compartido = (ckbPrint.isChecked()) ? 0 : 1;
            jugada.put("idVenta", idVenta);
            jugada.put("compartido", compartido);
            jugada.put("idUsuario", Utilidades.getIdUsuario(mContext));
            jugada.put("idBanca", Utilidades.getIdBanca(mContext));
            jugada.put("descuentoMonto", montoDescuento);
            jugada.put("hayDescuento", (ckbDescuento.isChecked()) ? 1 : 0);
            jugada.put("total", montoTotal);
            jugada.put("subTotal", 0);
            jugada.put("loterias", arregloLoterias);
            Gson gson = new Gson();
//            gson.toJson(jugadasClase)
            jugada.put("jugadas", gson.toJson(jugadasClase));

            Log.e("guardar", gson.toJson(jugadasClase));

            datosObj.put("datos", jugada);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String jsonString = datosObj.toString();
        guardarHttp g = new guardarHttp(datosObj);
        g.execute();
        //Log.d("jsonjugadas:", )

//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(final JSONObject response) {
//
//                        try {
//
//                            String errores = response.getString("errores");
//                            Main2Activity.progressBarToolbar.setVisibility(View.GONE);
//                            if(errores.equals("0")){
//                                limpiar();
//                                idVenta = response.getString("idVenta");
//                                JSONArray jsonArray = response.getJSONArray("loterias");
//                                JSONArray jsonArrayBancas = response.getJSONArray("bancas");
//                                JSONArray jsonArrayVentas = response.getJSONArray("ventas");
//                                setDescuento(jsonArrayBancas);
//                                fillSpinner(jsonArray, jsonArrayVentas);
//                                borderChange(true, false);
//
//                               // Bitmap img = screenshot(webViewImg, response.getString("img"));
//                                //Log.d("webImg", String.valueOf(screenshot(webViewImg, response.getString("img"))));
//                               // ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                                Toast.makeText(getContext(), response.getString("mensaje"), Toast.LENGTH_SHORT).show();
//                                if(ckbPrint.isChecked()){
////                                    if(JPrinterConnectService.isPrinterConnected() == false){
////                                        Main2Activity.txtBluetooth.performClick();
////                                    }
//
////                                    Bitmap ticketBitmap = Utilidades.toBitmap(response.getString("img"));
////                                    Intent serviceIntent = new Intent(mContext, PrinterService.class);
////                                    serviceIntent.putExtra("address", Utilidades.getAddressImpresora(mContext));
////                                    serviceIntent.putExtra("name", "hola");
////
////                                    mContext.startService(serviceIntent);
//                                    //es.submit(new BluetoothSearchDialog.TaskPrint(response, true));
//
//
//                                    //Utilidades.imprimir(mContext,response, 1);
//                                }
//                                else if(ckbSms.isChecked()){
//                                    new AsyncTask<Void, Void, Bitmap>() {
//                                        @Override
//                                        protected Bitmap doInBackground(Void... voids) {
//                                            Bitmap bitmap;
//                                            try {
//                                                bitmap = new Html2Bitmap.Builder().setContext(mContext).setContent(WebViewContent.html(response.getString("img"))).setBitmapWidth(400).build().getBitmap();
//                                            }catch (Exception e){
//                                                e.printStackTrace();
//                                                Log.v("ErrorHtmlWsapp", e.toString());
//                                                bitmap = null;
//                                            }
//
//                                            return bitmap;
//
//                                        }
//
//                                        @Override
//                                        protected void onPostExecute(Bitmap bitmap) {
//                                            if (bitmap != null) {
//                                                String codigoQr;
//                                                try{
//                                                    JSONObject venta = response.getJSONObject("venta");
//                                                    codigoQr = venta.getString("codigoQr");
//                                                }catch (Exception e){
//                                                    e.printStackTrace();
//                                                    Log.v("ErrorWhatsappImg", e.toString());
//                                                    codigoQr = "";
//                                                }
//                                                QRGEncoder qrgEncoder = new QRGEncoder(codigoQr, null, QRGContents.Type.TEXT, 150);
//                                                try {
//                                                    Bitmap bitmapQR = qrgEncoder.encodeAsBitmap();
//                                                    bitmapQR = combinarBitmap(bitmap, bitmapQR);
//                                                    Utilidades.sendSMS(getContext(), bitmapQR, true);
//                                                }catch (Exception e){
//                                                    e.printStackTrace();
//                                                    Log.v("ErrorQr", e.toString());
//                                                }
//
//                                            }
//                                        }
//                                    }.execute();
//                                }
//                                else if(ckbWhatsapp.isChecked()){
//
//                                    new AsyncTask<Void, Void, Bitmap>() {
//                                        @Override
//                                        protected Bitmap doInBackground(Void... voids) {
//                                            Bitmap bitmap;
//                                            try {
//                                                bitmap = new Html2Bitmap.Builder().setContext(mContext).setContent(WebViewContent.html(response.getString("img"))).setBitmapWidth(400).build().getBitmap();
//                                            }catch (Exception e){
//                                                e.printStackTrace();
//                                                Log.v("ErrorHtmlWsapp", e.toString());
//                                                bitmap = null;
//                                            }
//
//                                            return bitmap;
//
//                                        }
//
//                                        @Override
//                                        protected void onPostExecute(Bitmap bitmap) {
//                                            if (bitmap != null) {
//                                                String codigoQr;
//                                                try{
//                                                    JSONObject venta = response.getJSONObject("venta");
//                                                    codigoQr = venta.getString("codigoQr");
//                                                }catch (Exception e){
//                                                    e.printStackTrace();
//                                                    Log.v("ErrorWhatsappImg", e.toString());
//                                                    codigoQr = "";
//                                                }
//                                                QRGEncoder qrgEncoder = new QRGEncoder(codigoQr, null, QRGContents.Type.TEXT, 150);
//                                                try {
//                                                    Bitmap bitmapQR = qrgEncoder.encodeAsBitmap();
//                                                    bitmapQR = combinarBitmap(bitmap, bitmapQR);
//                                                    Utilidades.sendSMS(getContext(), bitmapQR, false);
//                                                }catch (Exception e){
//                                                    e.printStackTrace();
//                                                    Log.v("ErrorQr", e.toString());
//                                                }
//
//                                            }
//                                        }
//                                    }.execute();
//                                    //Utilidades.sendSMS(getContext(), response.getString("img"), false);
//                                }
//
//                            }
//                            else
//                                Toast.makeText(getContext(), response.getString("mensaje") + " e: " + errores, Toast.LENGTH_SHORT).show();
//
////                            txtMontodisponible.setText(response.getString("monto"));
////                            borderChange(false);
//                        } catch (Exception e) {
//                            Log.d("Error: ", e.toString());
//                            e.printStackTrace();
//
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //Log.d("responseerror: ", String.valueOf(error));
//                error.printStackTrace();
//                Main2Activity.progressBarToolbar.setVisibility(View.GONE);
//                //error.getMessage();
//                String body;
//                //get status code here
//                //String statusCode = String.valueOf(error.networkResponse.statusCode);
//                //get response body and parse with appropriate encoding
////                if(error.networkResponse.data!=null) {
////                    try {
////                        body = new String(error.networkResponse.data,"UTF-8");
////                        crearArchivo(body);
////                        Log.d("responseerror: ", body);
////                    } catch (UnsupportedEncodingException e) {
////                        e.printStackTrace();
////                    }
////                }
//
//                if(error instanceof NetworkError){
//                    Toast.makeText(mContext, "Verifique coneccion e intente de nuevo", Toast.LENGTH_SHORT).show();
//                }
//                else if(error instanceof ServerError){
//                    Toast.makeText(mContext, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
//                }
//                else if(error instanceof TimeoutError){
//                    Toast.makeText(mContext, "Conexion lenta, verifique conexion e intente de nuevo", Toast.LENGTH_SHORT).show();
//                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            switch (which){
//                                case DialogInterface.BUTTON_POSITIVE:
//                                    //Yes button clicked
//                                    guardar();
//                                    break;
//
//                                case DialogInterface.BUTTON_NEGATIVE:
//                                    //No button clicked
//                                    break;
//                            }
//                        }
//                    };
//
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                    builder.setMessage("Conexion lenta, desea realizar la venta otra vez?").setPositiveButton("Si", dialogClickListener)
//                            .setNegativeButton("No", dialogClickListener).show();
//                }
//
//            }
//        });
//
////        request.setRetryPolicy(new DefaultRetryPolicy(
////                0,
////                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
////                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
////        int socketTimeout = 30000;//30 seconds - change to what you want
////        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
////        request.setRetryPolicy(policy);
////        mQueue.add(request);
//        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }


    public  class guardarHttp extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;
        JSONObject data;

        public guardarHttp(JSONObject data) {
            this.data = data;
        }

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();

            try {
                //URL url = new URL("https://api.github.com/users/dmnugent80/repos");
                URL url = new URL("https://loterias.ml/api/principal/guardarMovil");
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


                if (urlConnection.getResponseCode() != 201){
                    StringBuffer answer = new StringBuffer();
                    InputStream inputstream = null;

                    if(urlConnection.getResponseCode() == 500 ) {

                        inputstream = urlConnection.getErrorStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            answer.append(line);
                        }
                        Log.e("guardarHttpError", answer.toString());

                    }
                    Log.e("guardarHttp", urlConnection.getResponseMessage());
                    return "Error";
                }

                //GET THE REQUEST DATA
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }


                //SE BUSCA EL INDEX DE LAS JUGADAS EN EL JSONSTRING


                //SE LLENAN LAS LISTAS CON LOS JSONSTRING
                Log.i("cancelarHttp", result.toString());
//                VentasClass ventasClass = llenarVenta(result.toString());
//                ImprimirTicketCancelado(ventasClass);



            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }


            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Main2Activity.progressBarToolbar.setVisibility(View.GONE);
            if(!result.equals("Error")){
                if(llenarVentasLoteriasTickets(result) == false)
                    return;

                if(errores == 1){
                    Toast.makeText(mContext, "Error: " +mensaje, Toast.LENGTH_SHORT).show();
                    return;
                }
                limpiar();
                fillSpinner();
                setDescuento();

                if(ckbPrint.isChecked()){
                    //Utilidades.imprimir(mContext,response, 1);
                    Utilidades.imprimir(mContext, venta, 1);
                }
                else if(ckbSms.isChecked()){
                    venta.setImg(imgHtmlTmp);
                    compartirTicketHttp c = new compartirTicketHttp(venta, true);
                    c.execute();
                }
                else if(ckbWhatsapp.isChecked()){
                    venta.setImg(imgHtmlTmp);
                    compartirTicketHttp c = new compartirTicketHttp(venta, false);
                    c.execute();
                }


            }else{
                Toast.makeText(mContext, "Error del servidor", Toast.LENGTH_SHORT).show();
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    guardar();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };


                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Ha ocurrido un error de conexion, desea realizar la venta otra vez?").setPositiveButton("Si", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
            }
        }
    }


    //IndexPostHttp

    public  class indexPostHttp extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;
        JSONObject data;

        public indexPostHttp(JSONObject data) {
            this.data = data;
        }

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();

            try {
                //URL url = new URL("https://api.github.com/users/dmnugent80/repos");
                URL url = new URL("https://loterias.ml/api/principal/indexPost");
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


                if (urlConnection.getResponseCode() != 201){
                    StringBuffer answer = new StringBuffer();
                    InputStream inputstream = null;

                    if(urlConnection.getResponseCode() == 500 ) {

                        inputstream = urlConnection.getErrorStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            answer.append(line);
                        }
                        Log.e("guardarHttpError", answer.toString());

                    }
                    Log.e("guardarHttp", urlConnection.getResponseMessage());
                    return "Error";
                }

                //GET THE REQUEST DATA
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }


                //SE BUSCA EL INDEX DE LAS JUGADAS EN EL JSONSTRING


                //SE LLENAN LAS LISTAS CON LOS JSONSTRING
                Log.i("cancelarHttp", result.toString());
//                VentasClass ventasClass = llenarVenta(result.toString());
//                ImprimirTicketCancelado(ventasClass);



            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }


            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Main2Activity.progressBarToolbar.setVisibility(View.GONE);
            if(!result.equals("Error")){
                if(llenarVentasLoteriasTickets(result) == false)
                    return;

                if(errores == 1){
                    Toast.makeText(mContext, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                    return;
                }

                fillSpinner();
                setDescuento();
            }else{
                Toast.makeText(mContext, "Error del servidor", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static boolean llenarVentasLoteriasTickets(String result)
    {

        venta = null;
        ventasLista.clear();
        loteriasLista.clear();
        bancasLista.clear();
//
//        idVenta = response.getString("idVenta");
//        idBanca = response.getInt("idBanca");
//        JSONArray jsonArray = response.getJSONArray("loterias");
//        JSONArray jsonArrayBancas = response.getJSONArray("bancas");
//        JSONArray jsonArrayVentas = response.getJSONArray("ventas");
//        setDescuento(jsonArrayBancas);


        Log.e("llenarJugadaLoterias", "prueba: " + result);
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
                    if(nombre.equals("venta") || nombre.equals("ventas") || nombre.equals("loterias") || nombre.equals("bancas")){
                        if(nombre.equals("venta")){
                             venta = gson.fromJson(reader1, VentasClass.class);
                            Log.i("llenarVentasLoterias", "venta:" + venta.getCodigoBarra());
                        }

                        if(nombre.equals("loterias")){
                            LoteriaClass loteriaClass  = gson.fromJson(reader1, LoteriaClass.class);
                            loteriasLista.add(loteriaClass);
                            Log.i("llenarJugadaLoterias", "loterias:" + loteriaClass.getDescripcion());
                        }
                        if(nombre.equals("ventas")){
                            VentasClass ventasClass  = gson.fromJson(reader1, VentasClass.class);
                            ventasLista.add(ventasClass);
//                            Log.i("llenarJugadaLoterias", "ventas:" + ventasClass.getDescripcion());
                        }
                        if(nombre.equals("bancas")){
                            BancaClass bancaClass  = gson.fromJson(reader1, BancaClass.class);
                            bancasLista.add(bancaClass);
//                            Log.i("llenarJugadaLoterias", "ventas:" + ventasClass.getDescripcion());
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
                    System.out.println("Token Value >>>> " + value);

                    if(nombre.equals("idVenta")){
                        idVenta = value;
                        Log.i("llenarJugadaLoterias", "idVenta:" + idVenta);
                    }
                    if(nombre.equals("img")){
                        imgHtmlTmp = value;
                        Log.i("llenarJugadaLoterias", "img:" + imgHtmlTmp);
                    }

                    if(nombre.equals("mensaje")){
                        mensaje = value;
                        Log.i("llenarJugadaLoterias", "mensaje:" + mensaje);
                    }

                }
                if (JsonToken.NUMBER.equals(reader1.peek())) {

                    long value = reader1.nextLong();
                    System.out.println("Token Value >>>> " + value);

                    if(nombre.equals("idBanca")){
                        idBanca = (int)value;
                        Log.i("llenarJugadaLoterias", "idVenta:" + idBanca);
                    }

                    if(nombre.equals("errores")){
                        errores = (int)value;
                        Log.i("llenarJugadaLoterias", "errores:" + errores);
                    }
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

    //AQUI ES PARA COMPARTIR TICKET
    public  class compartirTicketHttp extends AsyncTask<Void, Void, Bitmap> {

        HttpURLConnection urlConnection;
        VentasClass ventasClass;
        boolean sms_o_whatsapp;

        public compartirTicketHttp(VentasClass ventasClass, boolean sms_o_whatsapp) {
            this.ventasClass = ventasClass;
            this.sms_o_whatsapp = sms_o_whatsapp;
        }

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
                    if(this.sms_o_whatsapp){
                        Utilidades.sendSMS(getContext(), bitmapQR, true);
                    }else{
                        Utilidades.sendSMS(getContext(), bitmapQR, false);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Log.v("ErrorQr", e.toString());
                }

            }
        }
    }

    public VentasClass llenarVenta(String ventasJsonString, String path) {
//        if(jugadasLista != null)
//            jugadasLista.clear();
        VentasClass ventasClass = null;
        Gson gson = new GsonBuilder().create();
        try (com.google.gson.stream.JsonReader reader1 = new com.google.gson.stream.JsonReader(new StringReader(ventasJsonString))) {
            //reader1.beginObject();

            int c = 0;
            while (reader1.hasNext()) {

                if (JsonToken.BEGIN_OBJECT.equals(reader1.peek()))
                    reader1.beginObject();

                if(JsonToken.NUMBER.equals(reader1.peek())){
                    reader1.nextDouble();
                }

                if(JsonToken.STRING.equals(reader1.peek())){
                    reader1.nextString();
                }

                Log.i("DuplicarGsonPeek", reader1.peek().toString());
                Log.i("DuplicarGsonPath", reader1.getPath());
                if (JsonToken.NAME.equals(reader1.peek())) {
                    reader1.nextName();

                    //reader1.getPath().equals("$.ticket") path: "$.ticket"
                    if (reader1.getPath().equals(path)) {

                        ventasClass = gson.fromJson(reader1, VentasClass.class);
                        Log.i("llenarVenta", ventasClass.getCodigo());
                        reader1.close();
                        return ventasClass;
                    }
                }

                //VIEJO
//                if (JsonToken.BEGIN_OBJECT.equals(reader1.peek())) {
//                    reader1.beginObject();
//
//
//                    Log.i("DuplicarGsonPeek", reader1.peek().toString());
//                    if (JsonToken.NAME.equals(reader1.peek())) {
//                        reader1.nextName();
//                        Log.i("DuplicarGsonPath", reader1.getPath());
//                        if (reader1.getPath().equals("$.ticket")) {
//
//                            ventasClass = gson.fromJson(reader1, VentasClass.class);
//                            Log.i("llenarVenta", ventasClass.getCodigo());
//                            reader1.close();
//                            return ventasClass;
//                        }
//                    }
//                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return ventasClass;
        }
        return ventasClass;
    }

    public void crearArchivo(String texto){
        String baseFolder;
// check if external storage is available
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            baseFolder = getContext().getExternalFilesDir(null).getAbsolutePath();
            baseFolder = getContext().getExternalFilesDir(null).getAbsolutePath();
        }
// revert to using internal storage
        else {
            baseFolder = getContext().getFilesDir().getAbsolutePath();
        }

        try {
            String string = texto;
            File file = new File(baseFolder + "archivo_html.html");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(string.getBytes());
            fos.close();
            Log.d("Archivo:", "creado correctamente");
        }catch (Exception e){
            Log.d("ErrorArchivo:", e.toString());
        }
    }

    public void fillSpinner(){
        /********* Prepare value for spinner *************/
       // jsonArrayVentas = jsonArrayVentas2;
        String[] spinnerArray = new String[loteriasLista.size()];
        String[] ventasSpinner = new String[ventasLista.size()];
        String[] ventasIdTicketSecuenciaSpinner = new String[ventasLista.size()];
        HashMap<Integer,String> spinnerMap = new HashMap<Integer, String>();
        int contador = 0;
        for (LoteriaClass loteria : loteriasLista)
        {
            //try {
                //JSONObject dataobj = array.getJSONObject(i);
                //spinnerMap.put(i,dataobj.getString("id"));
                //idLoteriasMap.put(i,dataobj.getString("id"));
                spinnerArray[contador] = loteria.getDescripcion();
                contador++;
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

        }

        if(ventasLista.size() == 0){
            ventasIdTicketSecuenciaSpinner = new String[1];
            ventasIdTicketSecuenciaSpinner[0] = "No hay ventas";
        }

        contador = 0;
        for (VentasClass venta : ventasLista)
        {
//            try {
//                JSONObject dataobj = jsonArrayVentas.getJSONObject(i);

                //spinnerMap.put(i,dataobj.getString("id"));
//                codigoBarraMap.put(i,dataobj.getString("id"));
                ventasSpinner[contador] = venta.getCodigoBarra();
                ventasIdTicketSecuenciaSpinner[contador] = toSecuencia(String.valueOf(venta.idTicket), String.valueOf(venta.getTotal()));

//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

            contador++;
        }

        /********** LLenamos el listDescripcionLoterias para el multiselect ***********/
        listDescripcionLoterias = spinnerArray;
        ventasItems = ventasSpinner;
        ventasIdTicketSecuenciaItems = ventasIdTicketSecuenciaSpinner;
        checkedItems = new boolean[listDescripcionLoterias.length];


        /********* Set value to spinner *************/
        if(ventasIdTicketSecuenciaSpinner == null)
            return;
       try{
           ArrayAdapter<String> adapter =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, ventasIdTicketSecuenciaSpinner);
           adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
           spinnerTicket.setAdapter(adapter);
       }catch (Exception e){
           e.printStackTrace();
       }

        seleccionarLoteriasMultiSelect(null, true);
    }

    static void print(){
        if(spinnerTicket.getSelectedItem() == "No hay ventas")
            return;


//        if(JPrinterConnectService.isPrinterConnected() == false){
//            Toast.makeText(mContext, "Debe conectarse a una impresora", Toast.LENGTH_SHORT).show();
//            Main2Activity.txtBluetooth.performClick();
//            return;
//        }
//       try{
//
//           JSONObject ticket  = jsonArrayVentas.getJSONObject((int)spinnerTicket.getSelectedItemId());
//           JSONObject venta = new JSONObject();
//           venta.put("venta", ticket);
//           Log.d("PrincipaFragment", "print: " + jsonArrayVentas.toString());
//           es.submit(new BluetoothSearchDialog.TaskPrint(venta, false));
//           Toast.makeText(mContext, "idx:" + spinnerTicket.getSelectedItemId(), Toast.LENGTH_SHORT).show();
//
//       }catch (Exception e){
//           e.printStackTrace();
//       }







        if(Utilidades.hayImpresorasRegistradas(mContext) == false){
            Main2Activity.txtBluetooth.performClick();
            Toast.makeText(mContext, "Debe registrar una impresora", Toast.LENGTH_SHORT).show();
            return;
        }


        String url = "https://loterias.ml/api/reportes/getTicketById";
        //progressBar.setVisibility(View.VISIBLE);


        JSONObject dato = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            VentasClass ticket  = ventasLista.get((int)spinnerTicket.getSelectedItemId());
            dato.put("idUsuario", Utilidades.getIdUsuario(mContext));
            dato.put("idTicket", ticket.getIdTicket());
            datosObj.put("datos", dato);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String jsonString = datosObj.toString();
        imprimirTicketHttp i = new imprimirTicketHttp(datosObj);
        i.execute();




//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        //progressBar.setVisibility(View.GONE);
//                        try {
//
//
//                            JSONObject venta = new JSONObject();
//                            venta.put("venta", response.getJSONObject("ticket"));
//                            //Utilidades.imprimir(mContext, venta, 2);
////                            es.submit(new BluetoothSearchDialog.TaskPrint(venta, false));
//
//
//                            //getDialog().dismiss();
//                            //updateTable(jsonArray);
//                        } catch (JSONException e) {
//                            Log.d("Error: ", e.toString());
//                            e.printStackTrace();
//
//
//
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
//
//            }
//        });
//
////        mQueue.add(request);
//        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public static class imprimirTicketHttp extends AsyncTask<String, String, String> {

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
                URL url = new URL("https://loterias.ml/api/reportes/getTicketById");
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


                Utilidades.imprimir(mContext, ventasClass, 2);
            }else{
                Toast.makeText(mContext, "Error del servidor", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public static VentasClass llenarVenta(String result)
    {


        Log.e("llenarJugadaLoterias", "prueba: " + result);
        VentasClass ventasClass = null;
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

    public void fillSpinnerWithoutInternetTest(){
        /********* Prepare value for spinner *************/

        String [] arrayLoteriasDescripcion = getResources().getStringArray(R.array.descripcionesLoterias);
        String [] arrayLoteriasIds = getResources().getStringArray(R.array.idLoterias);

//        for (int i = 0; i < arrayLoteriasDescripcion.length; i++)
//        {
//                idLoteriasMap.put(i,arrayLoteriasIds[i]);
//
//        }

        /********** LLenamos el listDescripcionLoterias para el multiselect ***********/
        listDescripcionLoterias = arrayLoteriasDescripcion;
        checkedItems = new boolean[listDescripcionLoterias.length];

        Log.d("LoteriasResource:", arrayLoteriasDescripcion[0]);
        /********* Set value to spinner *************/
//        ArrayAdapter<String> adapter =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, arrayLoteriasDescripcion);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerTicket.setAdapter(adapter);
    }

    public void getSpinnerValue(){
        /********* Get value to spinner *************/
        String name = spinnerTicket.getSelectedItem().toString();
        //String id = spinnerMap.get(spinnerTicket.getSelectedItemPosition());
    }


//    public void onClick(View v) {
//        Log.d("saludos: ", "dentro funcoin");
//        switch (v.getId()) {
//            case (R.id.btn0):
//                txtJugada.setText("Hola");
//                Log.d("saludos: ", "Hola");
//
//                break;
//        }
//    }

    public static boolean isInteger(String cadena) {



            try {
                Integer.parseInt(cadena);
            } catch(Exception e) {
                return false;
            }


        return true;
    }


    public void escribir(View v){
        String caracteres = "";
        switch (v.getId()) {
            case (R.id.btn0):
                caracteres = "0";
                break;
            case (R.id.btn1):
                caracteres = "1";
                break;
            case (R.id.btn2):
                caracteres = "2";
                break;
            case (R.id.btn3):
                caracteres = "3";
                break;
            case (R.id.btn4):
                caracteres = "4";
                break;
            case (R.id.btn5):
                caracteres = "5";
                break;
            case (R.id.btn6):
                caracteres = "6";
                break;
            case (R.id.btn7):
                caracteres = "7";
                break;
            case (R.id.btn8):
                caracteres = "8";
                break;
            case (R.id.btn9):
                caracteres = "9";
                break;
            case (R.id.btnBackspace):
                if(jugada_monto_active){
                    caracteres = String.valueOf(txtJugada.getText());
                    if(caracteres != "" && caracteres != null){
                        //Eliminamos el ultimo caracter y asignamos el nuevo valor
                        caracteres = caracteres.substring(0, caracteres.length() - 2);
                    }
                }else{
                    caracteres = String.valueOf(txtMontojugar.getText());
                    if(caracteres != "" && caracteres != null){
                        //Eliminamos el ultimo caracter y asignamos el nuevo valor
                        caracteres = caracteres.substring(0, caracteres.length() - 2);
                    }
                }



                break;
            case (R.id.btnMas):
                caracteres = "+";
                break;
            case (R.id.btnMenos):
                //print();
                guardar();
                break;
            case (R.id.btnSlash):
                //Cambiar de loteria
                break;
            case (R.id.btnEnter):
                //Insertar jugadas o cambiar focus
                break;
        }

        Log.d("Caracter: ", getView().toString());

//        if(jugada_monto_active){
//
//            TextView t = (TextView) v.findViewById(R.id.txtJugada);
//            t.setText(caracteres);
//        }else{
//            TextView t = (TextView) v.findViewById(R.id.txtMontojugar);
//            t.setText(caracteres);
//            //txtMontojugar.setText(caracteres);
//        }
    }

    private void jugadaAdd(){
        String monto = String.valueOf(txtMontojugar.getText());
        String montoDisponible = String.valueOf(txtMontodisponible.getText());
        String jugada = String.valueOf(txtJugada.getText());
        jugada = Utilidades.ordenarMenorAMayor(jugada);
        //getJugadas();

        if(mUserItems.size() == 0){
            Toast.makeText(getContext(), "Debe seleccionar una loteria", Toast.LENGTH_SHORT).show();
            return;
        }
        if(jugada == null || jugada == "" || jugada.length() == 0)
            return;
        if(montoDisponible == null || montoDisponible == "" || montoDisponible.length() == 0)
            return;
        if(monto == null || monto == "" || monto.length() == 0)
            return;
        else{
            if(montoDisponible != "x"){
                if((int)Float.parseFloat(monto) > (int)Float.parseFloat(montoDisponible)){
                    Toast.makeText(getActivity(), "No hay cantidad suficiente", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        }

        if(jugada.length() == 1)
            return;

        //validarJugadaSeaNumerica contenera la jugada numerica, osea, sin puntos ni signos de mas, etc..
        if(!Utilidades.validarJugadaSeaCorrecta(jugada)){
            Toast.makeText(getContext(), "La jugada no es correcta", Toast.LENGTH_SHORT).show();
            return;
        }

        if(txtMontojugar.getText().toString().equals("0")){
            return;
        }





            JSONObject jugadaObject = new JSONObject();

            if(mUserItems.size() == 1){
                try {
                    boolean existe = false, existeInvertida = false;
                    if(Utilidades.jugadaExiste(jugadasClase,Utilidades.jugadaQuitarPunto(jugada), String.valueOf(loteriasLista.get(mUserItems.get(0)).getId())))
                        aceptaInsertarJugadaExistente(Utilidades.jugadaQuitarPunto(jugada), String.valueOf(loteriasLista.get(mUserItems.get(0)).getId()), listDescripcionLoterias[mUserItems.get(0)], monto, 0);
//                    existe = jugadasClase.siJugadaExisteActualizar(jugadasClase.jugadaQuitarPunto(jugada), idLoteriasMap.get(mUserItems.get(0)), monto);
//                    if(jugada.length() == 3)
//                        existeInvertida = jugadasClase.siJugadaExisteActualizar(jugadasClase.jugadaInvertir(jugadasClase.jugadaQuitarPunto(jugada)), idLoteriasMap.get(mUserItems.get(0)), monto);



                    if(!Utilidades.jugadaExiste(jugadasClase, Utilidades.jugadaQuitarPunto(jugada), String.valueOf(loteriasLista.get(mUserItems.get(0)).getId()))){

//                        jugadaObject = new JSONObject();
//                        jugadaObject.put("idLoteria", idLoteriasMap.get(mUserItems.get(0)));
//                        jugadaObject.put("descripcion", listDescripcionLoterias[mUserItems.get(0)]);
//                        jugadaObject.put("jugada", jugada);
//                        jugadaObject.put("sorteo", "no");
//
//                        jugadaObject.put("tam", txtJugada.getText().length());
//                        jugadaObject.put("monto", txtMontojugar.getText());
//                        jugadaObject.put("idBanca", Utilidades.getIdBanca(mContext));
//                        jugadasClase.add(jugadaObject);

                        JugadaClass jugadaClass = new JugadaClass();
                        jugadaClass.setIdLoteria(loteriasLista.get(mUserItems.get(0)).getId());
                        jugadaClass.setDescripcion(listDescripcionLoterias[mUserItems.get(0)]);
                        jugadaClass.setJugada(jugada);
                        jugadaClass.setSorteo("no");
//                        jugadaClass.setTam(txtJugada.getText().length());
                        jugadaClass.setMonto(Double.parseDouble(txtMontojugar.getText().toString()));
                        jugadaClass.setIdBanca(Utilidades.getIdBanca(mContext));
                        //jugadasClase.add(jugadaClass);
                        Utilidades.addJugada(jugadaClass);
                    }

                    //jugada.length() == 3 && existeInvertida == false
//                    if(jugada.length() == 3 && existeInvertida == false) {
//
//                        //Invertimos la jugada
//                        String jugadaInvertida = jugadasClase.jugadaInvertir(jugadasClase.jugadaQuitarPunto(jugada));
//
//
//
//                        JSONObject jugadaObject2 = new JSONObject();
//                        jugadaObject2.put("idLoteria", idLoteriasMap.get(mUserItems.get(0)));
//                        jugadaObject2.put("descripcion", listDescripcionLoterias[mUserItems.get(0)]);
//                        jugadaObject2.put("jugada", jugadaInvertida);
//                        jugadaObject2.put("tam", txtJugada.getText().length());
//                        jugadaObject2.put("monto", txtMontojugar.getText());
//                        jugadaObject2.put("idBanca", Utilidades.getIdBanca(mContext));
//                        jugadasClase.add(jugadaObject2);
//                    }

                    txtJugada.setText("");
                    txtMontodisponible.setText("");
                }catch (Exception e){
                    Log.e("Error jugada:", e.toString());
                }
            }else if(mUserItems.size() > 1){
                for (int contadorLoteria = 0; contadorLoteria < mUserItems.size(); contadorLoteria++) {
                    try {
                        boolean existe = false, existeInvertida = false;

                        if(Utilidades.jugadaExiste(jugadasClase, Utilidades.jugadaQuitarPunto(jugada), String.valueOf(loteriasLista.get(mUserItems.get(contadorLoteria)).getId())))
                            aceptaInsertarJugadaExistente(Utilidades.jugadaQuitarPunto(jugada), String.valueOf(loteriasLista.get(mUserItems.get(contadorLoteria)).getId()), listDescripcionLoterias[contadorLoteria], monto, 0);

//                        existe = jugadasClase.siJugadaExisteActualizar(jugadasClase.jugadaQuitarPunto(jugada), idLoteriasMap.get(mUserItems.get(contadorLoteria)), monto);
//                        if(jugada.length() == 3)
//                            existeInvertida = jugadasClase.siJugadaExisteActualizar(jugadasClase.jugadaInvertir(jugadasClase.jugadaQuitarPunto(jugada)), idLoteriasMap.get(mUserItems.get(contadorLoteria)), monto);

                        if(!Utilidades.jugadaExiste(jugadasClase, Utilidades.jugadaQuitarPunto(jugada), String.valueOf(loteriasLista.get(mUserItems.get(contadorLoteria)).getId()))){

//                            jugadaObject = new JSONObject();
////                            jugadaObject.put("idLoteria", idLoteriasMap.get(mUserItems.get(contadorLoteria)));
////                            jugadaObject.put("descripcion", listDescripcionLoterias[mUserItems.get(contadorLoteria)]);
////                            jugadaObject.put("jugada", jugada);
////                            jugadaObject.put("sorteo", "no");
////                            jugadaObject.put("tam", txtJugada.getText().length());
////                            jugadaObject.put("monto", txtMontojugar.getText());
////                            jugadaObject.put("idBanca", Utilidades.getIdBanca(mContext));
////                            jugadasClase.add(jugadaObject);

                            JugadaClass jugadaClass = new JugadaClass();
                            jugadaClass.setIdLoteria(loteriasLista.get(mUserItems.get(contadorLoteria)).getId());
                            jugadaClass.setDescripcion(listDescripcionLoterias[mUserItems.get(contadorLoteria)]);
                            jugadaClass.setJugada(jugada);
                            jugadaClass.setSorteo("no");
//                        jugadaClass.setTam(txtJugada.getText().length());
                            jugadaClass.setMonto(Double.parseDouble(txtMontojugar.getText().toString()));
                            jugadaClass.setIdBanca(Utilidades.getIdBanca(mContext));
                            //jugadasClase.add(jugadaClass);
                            Utilidades.addJugada(jugadaClass);
                            //Toast.makeText(getContext(), "lot:"+listDescripcionLoterias[contadorLoteria] + " id:" + idLoteriasMap.get(contadorLoteria), Toast.LENGTH_SHORT).show();
                        }

//                        if(jugada.length() == 3) {
//                            //Invertimos la jugada
//                            String jugadaInvertida = jugadasClase.jugadaInvertir(jugadasClase.jugadaQuitarPunto(jugada));
//
//                            Log.d("jugadainvertida:", jugadaInvertida);
//
//                            JSONObject jugadaObject2 = new JSONObject();
//                            jugadaObject2.put("idLoteria", idLoteriasMap.get(mUserItems.get(contadorLoteria)));
//                            jugadaObject2.put("descripcion", listDescripcionLoterias[contadorLoteria]);
//                            jugadaObject2.put("jugada", jugadaInvertida);
//                            jugadaObject2.put("tam", txtJugada.getText().length());
//                            jugadaObject2.put("monto", txtMontojugar.getText());
//                            jugadaObject2.put("idBanca", Utilidades.getIdBanca(mContext));
//                            jugadasClase.add(jugadaObject2);
//                        }
                        txtJugada.setText("");
                        txtMontodisponible.setText("");
                    }catch (Exception e){
                        Log.e("Error jugada:", e.toString());
                    }
                }
            }


        //Log.d("jugadas:", jugadas.toString());
//        txtMontojugar.setText("");
        calcularTotal();
//        getJugadas();
        borderChange(true, false);

    } //End jugadasAdd


    public static void calcularTotal(){
        montoTotal = Utilidades.calcularTotal(jugadasClase);
        Log.d("PrincipalFragment", "calcularTotal: " + String.valueOf(montoTotal));
        txtTotal.setText("Tot: $"+String.valueOf(montoTotal));
        if(ckbDescuento.isChecked() && montoTotal > 0){
            float calculoDescuento = (montoTotal / deCada) * descontar;
            montoDescuento = (int) calculoDescuento;
            txtDescuento.setText("Des: $"+String.valueOf(montoDescuento) + ".0");
        }else{
            montoDescuento = 0;
            txtDescuento.setText("Des: $"+String.valueOf(montoDescuento));
        }
    }

    //private void arregloEliminarElemento(Array array, )

    @Override
    public void setDuplicar(JSONArray jugadas) {
        Log.d("PrincipalFragment", "Hola");
    }

    public void aceptaCancelarTicket(){
        if(spinnerTicket.getSelectedItem() == "No hay ventas")
            return;
//
//        if(JPrinterConnectService.isPrinterConnected() == false){
////            Toast.makeText(mContext, "Debe conectarse a una impresora", Toast.LENGTH_SHORT).show();
////            Main2Activity.txtBluetooth.performClick();
//////                mostrarDispositivosBluetooth();
////            return;
//        }
        if(Utilidades.hayImpresorasRegistradas(mContext) == false){
            Main2Activity.txtBluetooth.performClick();
            Toast.makeText(mContext, "Debe registrar una impresora", Toast.LENGTH_SHORT).show();
            return;
        }

        try{


            final VentasClass venta  = ventasLista.get((int)spinnerTicket.getSelectedItemId());


            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            cancelarTicket(venta);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };


            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Esta seguro de cancelar el ticket " + Utilidades.toSecuencia(String.valueOf(venta.getIdTicket()), venta.getCodigo()) + " ?").setPositiveButton("Si", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void ImprimirTicketCancelado(JSONObject venta){
//        if(JPrinterConnectService.isPrinterConnected() == false){
////            Toast.makeText(mContext, "Debe conectarse a una impresora", Toast.LENGTH_SHORT).show();
////            Main2Activity.txtBluetooth.performClick();
//////                mostrarDispositivosBluetooth();
////            return;
//        }

        if(Utilidades.hayImpresorasRegistradas(mContext) == false){
            Main2Activity.txtBluetooth.performClick();
            Toast.makeText(mContext, "Debe registrar una impresora", Toast.LENGTH_SHORT).show();

        }

        try{
            JSONObject v = new JSONObject();
            v.put("venta", venta);
            Log.d("MonitoreoCancelado", v.toString());
            //es.submit(new BluetoothSearchDialog.TaskPrint(v, 1));
           // Utilidades.imprimir(mContext, v, 3);

        }catch(Exception e){
            e.printStackTrace();
        }

    }


    public static void aceptaInsertarJugadaExistente(final String jugada, final String idLoteria, final String descripcion, final String monto, final int cantidadDeJugadasARevisar){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked

                         Utilidades.siJugadaExisteActualizar(jugadasClase, jugada, idLoteria, Double.parseDouble(monto), cantidadDeJugadasARevisar);
                        calcularTotal();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("La jugada "  + jugada + " existe en la loteria " + descripcion + ", desea agregar?").setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }






    private void cancelarTicket(VentasClass venta){
        String url = "https://loterias.ml/api/principal/cancelarMovil";

        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            loteria.put("codigoBarra", venta.getCodigoBarra());
            loteria.put("razon", "Cancelado desde movil");
            loteria.put("idUsuario", Utilidades.getIdUsuario(mContext));
            loteria.put("idBanca", Utilidades.getIdBanca(mContext));

            datosObj.put("datos", loteria);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String jsonString = datosObj.toString();
        cancelarHttp c = new cancelarHttp(datosObj);
        c.execute();

//        final JSONObject venta = new JSONObject();
//
//        try {
//            venta.put("venta", jsonObject);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            String errores = response.getString("errores");
//                            if(errores.equals("0")){
//
//                                JSONObject venta2 = new JSONObject();
//                                venta2.put("venta", response.getJSONObject("ticket"));
//                                Log.d("CancelarTicketDo", venta2.toString());
//                                jsonParse();
//                                ImprimirTicketCancelado(response.getJSONObject("ticket"));
//                                Toast.makeText(mContext, response.getString("mensaje"), Toast.LENGTH_SHORT).show();
//                            }
//                            else
//                                Toast.makeText(mContext, response.getString("mensaje") + " e: " + errores, Toast.LENGTH_SHORT).show();
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
//                    Toast.makeText(mContext, "Verifique coneccion e intente de nuevo", Toast.LENGTH_SHORT).show();
//                }
//                else if(error instanceof ServerError){
//                    Toast.makeText(mContext, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
//                }
//                else if(error instanceof TimeoutError){
//                    Toast.makeText(mContext, "Conexion lenta, verifique conexion e intente de nuevo", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//
////        mQueue.add(request);
//        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }


    public  class cancelarHttp extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;
        JSONObject data;

        public cancelarHttp(JSONObject data) {
            this.data = data;
        }

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();

            try {
                //URL url = new URL("https://api.github.com/users/dmnugent80/repos");
                URL url = new URL("https://loterias.ml/api/principal/cancelarMovil");
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


                //SE LLENAN LAS LISTAS CON LOS JSONSTRING
                Log.i("cancelarHttp", result.toString());
//                VentasClass ventasClass = llenarVenta(result.toString());
//                ImprimirTicketCancelado(ventasClass);



            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }


            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(!result.equals("Error")){
                VentasClass ventasClass = llenarVenta(result.toString());
                if(errores == 1){
                    Toast.makeText(mContext, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                    return;
                }

                Utilidades.imprimir(mContext, ventasClass, 3);
                jsonParse();
            }else{
                Toast.makeText(mContext, "Error del servidor", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private void cancelarTicketViejo(final JSONObject venta){

        String url = "https://loterias.ml/api/principal/cancelar";

        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            loteria.put("codigoBarra", venta.getString("codigoBarra"));
            loteria.put("razon", "Cancelado desde movil");
            loteria.put("idUsuario", Utilidades.getIdUsuario(mContext));
            loteria.put("idBanca", Utilidades.getIdBanca(mContext));

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
                                jsonParse();
                                ImprimirTicketCancelado(venta);
                                Toast.makeText(mContext, response.getString("mensaje"), Toast.LENGTH_SHORT).show();
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

//        mQueue.add(request);
        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public String toSecuencia(String idTicket, String monto){
        String pad = "000000000";
        String ans = pad.substring(0, pad.length() - idTicket.length()) + idTicket + " - $" + monto;
        return ans;
    }

//    static void imprimir(JSONObject venta, int original_cancelado_copia)
//    {
////        Intent serviceIntent = new Intent(mContext, PrinterService.class);
////        serviceIntent.putExtra("address", Utilidades.getAddressImpresora(mContext));
////        serviceIntent.putExtra("name", "hola");
//
//        //mContext.startService(serviceIntent);
//        PrinterClass printerClass = new PrinterClass(mContext, venta);
//        printerClass.conectarEImprimir(true, original_cancelado_copia);
////        if(original_cancelado_copia == 1)
////            es.submit(new BluetoothSearchDialog.TaskPrint(venta, true));
////        else if(original_cancelado_copia == 2)
////            es.submit(new BluetoothSearchDialog.TaskPrint(venta, false));
////        if(original_cancelado_copia == 3)
////            es.submit(new BluetoothSearchDialog.TaskPrint(venta, 1));
//    }
}
