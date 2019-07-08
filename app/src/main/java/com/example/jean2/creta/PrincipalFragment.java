package com.example.jean2.creta;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.content.WebViewContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
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
    Context mContext;
    private int idBanca = 1;
    private static int descontar = 0;
    private static int deCada = 0;
    private static int montoTotal = 0;
    private static int montoDescuento = 0;
    private TextView txtSelected;
    IconManager iconManager;
    CheckBox c;
    Spinner spinnerTicket;
   // private RequestQueue mQueue;

    ProgressBar progressBar;
    String[] listItems;
    String[] ventasItems;
    String[] ventasIdTicketSecuenciaItems;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    HashMap<Integer,String> idLoteriasMap = new HashMap<Integer, String>();
    HashMap<Integer,String> codigoBarraMap = new HashMap<Integer, String>();


    View view;
    TextView txtBanca;
    TextView txtJugada;
    TextView txtMontojugar;
    TextView txtMontodisponible;
    private static TextView txtTotal;
    private static TextView txtDescuento;
    private static CheckBox_Icon ckbDescuento;
    private static CheckBox_Icon ckbPrint;
    private static CheckBox_Icon ckbSms;
    private static CheckBox_Icon ckbWhatsapp;
    public static JSONArray jugadas = new JSONArray();
    public static Jugadas jugadasClase = new Jugadas();
    public static WebView webViewImg;


    String monto;
    boolean estoyProbando = false;

    boolean jugada_monto_active = true;

    ExecutorService es = Executors.newScheduledThreadPool(30);

    public PrincipalFragment() {
        // Required empty public constructor
    }

    public void getJugadas(){
        jugadas = jugadasClase.getJsonArrayJugadas();
        Log.d("getJugadas:", jugadas.toString());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        iconManager = new IconManager();



        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_principal, container, false);
        view = inflater.inflate(R.layout.fragment_principal, container, false);

        borderChange(true);

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
        Button btnBackspace = (Button)view.findViewById(R.id.btnBackspace);
        Button btnSlash = (Button)view.findViewById(R.id.btnSlash);
        Button btnPunto = (Button)view.findViewById(R.id.btnPunto);
//        Button btnD = (Button)view.findViewById(R.id.btnD);
//        Button btnQ = (Button)view.findViewById(R.id.btnQ);


        txtJugada.setOnClickListener(this);
        txtMontojugar.setOnClickListener(this);
        txtDelete.setOnClickListener(this);
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
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
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
                            item = item + listItems[mUserItems.get(i)];
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




        return view;
    }



    @Override
    public void onAttach(Context context) {
        mContext=(FragmentActivity) context;
        super.onAttach(context);
    }

    public void duplicar(final JSONArray jugadas, JSONArray loterias){
        jugadasClase.removeAll();
        for(int i=0; i < listItems.length; i++){
            checkedItems[i] = false;
            mUserItems.clear();
        }
        try {
            for (int i=0; i< loterias.length(); i++){
                JSONObject item = (JSONObject)loterias.get(i);
                for(int c=0; c < listItems.length; c++){
                    if(item.getString("descripcion").toString().equals(listItems[c])){
                        checkedItems[c] = true;
                        mUserItems.add(c);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setTitle("Seleccionar loteria");
        mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {

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
                    item = item + listItems[mUserItems.get(i)];
                    if (i != mUserItems.size() - 1) {
                        item = item + ", ";
                    }

                    try {
                        for (int c=0; c< jugadas.length(); c++){
                            JSONObject jugadaObject = (JSONObject)jugadas.get(c);
                            jugadaObject.put("descripcion", listItems[mUserItems.get(i)]);
                            jugadaObject.put("idLoteria", idLoteriasMap.get(mUserItems.get(i)));
                            jugadaObject.put("monto", (int)Float.parseFloat(jugadaObject.getString("monto").toString()));
                            Log.d("PrincipalFragment", "duplicar jugada:" + jugadaObject.getString("jugada"));
                            jugadasClase.add(jugadaObject);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                calcularTotal();
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
                    checkedItems[i] = true;
                    mUserItems.clear();
                    txtSelected.setText("");
//                    if(jugadas.length() > 0){
//                        for(int c=0)
//                    }
                }
            }
        });

        AlertDialog mDialog = mBuilder.create();
        //mDialog.getButton(DialogInterface.OnShowListener).performClick();
        mDialog.show();

    }

    @Override
    public void onClick(View v){
        String jugada = "";

        String p = "hola";
        String p2 = p.substring(1, 2).toString();
//        Log.d("Reemplazar:", p2);
//        Toast.makeText(getContext(), "p: " + p2, Toast.LENGTH_SHORT).show();
        if(jugada_monto_active)
            jugada = String.valueOf(txtJugada.getText());

        String caracteres = "";
        switch (v.getId()) {
            case (R.id.txtDelete):
                aceptaCancelarTicket();
                break;
                case (R.id.txtJugada):
                borderChange(true);
                break;
            case (R.id.txtMontojugar):
                borderChange(false);
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
                if(String.valueOf(txtJugada.getText()).length() == 4){
                    //Buscamos en el arreglo de las loterias seleccionadas para ver si la loteria de new york esta seleccionada
                    for (int i = 0; i < mUserItems.size(); i++) {
                        if (listItems[mUserItems.get(i)] == "New York") {
                            existe_loteria_newyork = true;
                            break;
                        }
                    }

                    if(existe_loteria_newyork)
                        caracteres = "+";
                }

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
                if(jugada_monto_active) getMontoDisponible(); else jugadaAdd();
                break;
            case (R.id.btnPunto):
                if(String.valueOf(txtJugada.getText()).length() == 2){
                    caracteres = ".";
                }
                break;
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

            caracteres = txtMontojugar.getText() + caracteres;
            txtMontojugar.setText(caracteres);
        }
    }

    private void borderChange(boolean jugada_monto_active){

        this.jugada_monto_active = jugada_monto_active;
        TextView txtPonerBorderActive, txtPonerBorderNormal;
        if(jugada_monto_active){
            txtPonerBorderActive = view.findViewById(R.id.txtJugada);
            txtPonerBorderNormal = view.findViewById(R.id.txtMontojugar);
        }else{
            txtPonerBorderActive = view.findViewById(R.id.txtMontojugar);
            txtPonerBorderNormal = view.findViewById(R.id.txtJugada);
        }

        txtPonerBorderActive.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_active_border));
        txtPonerBorderNormal.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.textview_border));
    }

    public void setDescuento(JSONArray bancas){
        int total = 0;
        try {
            for (int i=0; i < bancas.length(); i++){
                JSONObject item = (JSONObject)bancas.get(i);
                String id = item.getString("id").toString();
                String des = item.getString("descontar").toString();
                String deC = item.getString("deCada").toString();

                //if(isInteger(id)){
                    if(id.equals(String.valueOf(Utilidades.getIdBanca(mContext)))){

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
        String url = "http://loterias.ml/api/principal/indexPost";
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



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Main2Activity.progressBarToolbar.setVisibility(View.GONE);
                        try {
                            JSONArray jsonArray = response.getJSONArray("loterias");
                            JSONArray jsonArrayBancas = response.getJSONArray("bancas");
                            JSONArray jsonArrayVentas = response.getJSONArray("ventas");
                            setDescuento(jsonArrayBancas);
                            fillSpinner(jsonArray, jsonArrayVentas);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("Error: ", e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NetworkError){
                    Toast.makeText(mContext, "Verifique coneccion y recargue la pagina", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof ServerError){
                    Toast.makeText(mContext, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof TimeoutError){
                    Toast.makeText(mContext, "Conexion lenta, verifique conexion y recargue de nuevo", Toast.LENGTH_SHORT).show();
                }
                Main2Activity.progressBarToolbar.setVisibility(View.GONE);
                error.printStackTrace();
                Log.d("responseerror: ", String.valueOf(error));
            }
        });

        //mQueue.add(request);
        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    private void getMontoDisponible(){
        String url = "http://loterias.ml/api/principal/montodisponible";
        Main2Activity.progressBarToolbar.setVisibility(View.VISIBLE);
        monto = "0";
        Log.d("Arreglo loteria size: ", String.valueOf(mUserItems.size()));
        String jugada = String.valueOf(txtJugada.getText());
        if(jugada == "" || jugada == null || jugada.length() == 0 || jugada.length() == 1)
            return;
        if(mUserItems.size() == 0){
            Toast.makeText(getActivity(), "Debe seleccionar una loteria", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mUserItems.size() > 1){
            txtMontodisponible.setText("x");
            Main2Activity.progressBarToolbar.setVisibility(View.GONE);
            borderChange(false);
            return;
        }

        if(estoyProbando){
            txtMontodisponible.setText("20");
            return;
        }

            StringBuilder validarJugadaSeaNumerica = new StringBuilder(jugada);
            if(jugada.length() == 3 || jugada.length() == 5){
                //Si la jugada tiene 3 digitos y el ultimo digito
                if(jugada.charAt(jugada.length() - 1) == '.' && jugada.length() == 3)
                {
                    validarJugadaSeaNumerica.setLength(jugada.length() - 1);
                    Log.d("validar jug sea num.: ", validarJugadaSeaNumerica.toString());
                }
                else if(jugada.charAt(jugada.length() - 1) == '+' && jugada.length() == 5)
                {
                    validarJugadaSeaNumerica.setLength(jugada.length() - 1);
                    Log.d("validar jug sea num+: ", validarJugadaSeaNumerica.toString());
                }
                else{
                    Toast.makeText(getActivity(), "La jugada debe tener 2, 4 o 6 digitos", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if(!isInteger(validarJugadaSeaNumerica.toString())){
                Toast.makeText(getActivity(), "La jugada debe ser correcta", Toast.LENGTH_SHORT).show();
                return;
            }




        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            loteria.put("idLoteria", idLoteriasMap.get(mUserItems.get(0)));
            loteria.put("jugada", validarJugadaSeaNumerica.toString());
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
                        Main2Activity.progressBarToolbar.setVisibility(View.GONE);
                        try {
                            txtMontodisponible.setText(response.getString("monto"));
                            borderChange(false);
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
        mUserItems.clear();
        txtSelected.setText("");
        jugadasClase.removeAll();
        calcularTotal();
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
        String url = "http://loterias.ml/api/principal/guardar";


        if(ckbPrint.isChecked()){
            if(BluetoothSearchDialog.isPrinterConnected() == false){
                Toast.makeText(mContext, "Debe conectarse a una impresora", Toast.LENGTH_SHORT).show();
                Main2Activity.txtBluetooth.performClick();
                return;
            }
        }

        Main2Activity.progressBarToolbar.setVisibility(View.VISIBLE);
        JSONObject jugada = new JSONObject();
        JSONObject datosObj = new JSONObject();
        int[] arregloLoterias = new int[1];
        arregloLoterias[0] = 1;

        try {
            jugada.put("idUsuario", Utilidades.getIdUsuario(mContext));
            jugada.put("idBanca", Utilidades.getIdBanca(mContext));
            jugada.put("descuentoMonto", montoDescuento);
            jugada.put("hayDescuento", (ckbDescuento.isChecked()) ? 1 : 0);
            jugada.put("total", montoTotal - montoDescuento);
            jugada.put("subTotal", montoTotal);
            jugada.put("loterias", arregloLoterias);
            jugada.put("jugadas", jugadasClase.getJsonArrayJugadas());


            datosObj.put("datos", jugada);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String jsonString = datosObj.toString();

        //Log.d("jsonjugadas:", )

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {

                        try {
                            Log.d("Error: ", response.toString());
                            String errores = response.getString("errores");
                            Main2Activity.progressBarToolbar.setVisibility(View.GONE);
                            if(errores.equals("0")){
                                jsonParse();
                                borderChange(true);
                                limpiar();
                               // Bitmap img = screenshot(webViewImg, response.getString("img"));
                                //Log.d("webImg", String.valueOf(screenshot(webViewImg, response.getString("img"))));
                               // ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                Toast.makeText(getContext(), response.getString("mensaje"), Toast.LENGTH_SHORT).show();
                                if(ckbPrint.isChecked()){
//                                    if(BluetoothSearchDialog.isPrinterConnected() == false){
//                                        Main2Activity.txtBluetooth.performClick();
//                                    }

//                                    Bitmap ticketBitmap = Utilidades.toBitmap(response.getString("img"));
                                    es.submit(new BluetoothSearchDialog.TaskPrint(response, true));

//                                    JSONObject venta = response.getJSONObject("venta");
//                                    Log.d("printVenta", venta.getJSONArray("jugadas").toString());
                                }
                                else if(ckbSms.isChecked()){
                                    new AsyncTask<Void, Void, Bitmap>() {
                                        @Override
                                        protected Bitmap doInBackground(Void... voids) {
                                            Bitmap bitmap;
                                            try {
                                                bitmap = new Html2Bitmap.Builder().setContext(mContext).setContent(WebViewContent.html(response.getString("img"))).setBitmapWidth(400).build().getBitmap();
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
                                                    JSONObject venta = response.getJSONObject("venta");
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
                                else if(ckbWhatsapp.isChecked()){

                                    new AsyncTask<Void, Void, Bitmap>() {
                                        @Override
                                        protected Bitmap doInBackground(Void... voids) {
                                            Bitmap bitmap;
                                            try {
                                                bitmap = new Html2Bitmap.Builder().setContext(mContext).setContent(WebViewContent.html(response.getString("img"))).setBitmapWidth(400).build().getBitmap();
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
                                                    JSONObject venta = response.getJSONObject("venta");
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
                                                    Utilidades.sendSMS(getContext(), bitmapQR, false);
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                    Log.v("ErrorQr", e.toString());
                                                }

                                            }
                                        }
                                    }.execute();
                                    //Utilidades.sendSMS(getContext(), response.getString("img"), false);
                                }

                            }
                            else
                                Toast.makeText(getContext(), response.getString("mensaje") + " e: " + errores, Toast.LENGTH_SHORT).show();

//                            txtMontodisponible.setText(response.getString("monto"));
//                            borderChange(false);
                        } catch (Exception e) {
                            Log.d("Error: ", e.toString());
                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("responseerror: ", String.valueOf(error));
                error.printStackTrace();
                Main2Activity.progressBarToolbar.setVisibility(View.GONE);
                //error.getMessage();
                String body;
                //get status code here
                //String statusCode = String.valueOf(error.networkResponse.statusCode);
                //get response body and parse with appropriate encoding
//                if(error.networkResponse.data!=null) {
//                    try {
//                        body = new String(error.networkResponse.data,"UTF-8");
//                        crearArchivo(body);
//                        Log.d("responseerror: ", body);
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                }

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

//        request.setRetryPolicy(new DefaultRetryPolicy(
//                0,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        int socketTimeout = 30000;//30 seconds - change to what you want
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        request.setRetryPolicy(policy);
//        mQueue.add(request);
        MySingleton.getInstance(mContext).addToRequestQueue(request);
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

    public void fillSpinner(JSONArray array, JSONArray jsonArrayVentas){
        /********* Prepare value for spinner *************/
        String[] spinnerArray = new String[array.length()];
        String[] ventasSpinner = new String[jsonArrayVentas.length()];
        String[] ventasIdTicketSecuenciaSpinner = new String[jsonArrayVentas.length()];
        HashMap<Integer,String> spinnerMap = new HashMap<Integer, String>();
        for (int i = 0; i < array.length(); i++)
        {
            try {
                JSONObject dataobj = array.getJSONObject(i);
                //spinnerMap.put(i,dataobj.getString("id"));
                idLoteriasMap.put(i,dataobj.getString("id"));
                spinnerArray[i] = dataobj.getString("descripcion");
                Log.d("loterias: ", spinnerArray[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        for (int i = 0; i < jsonArrayVentas.length(); i++)
        {
            try {
                JSONObject dataobj = jsonArrayVentas.getJSONObject(i);

                //spinnerMap.put(i,dataobj.getString("id"));
                codigoBarraMap.put(i,dataobj.getString("id"));
                ventasSpinner[i] = dataobj.getString("codigoBarra");
                ventasIdTicketSecuenciaSpinner[i] = toSecuencia(dataobj.getString("idTicket"), dataobj.getString("subTotal"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        /********** LLenamos el listItems para el multiselect ***********/
        listItems = spinnerArray;
        ventasItems = ventasSpinner;
        ventasIdTicketSecuenciaItems = ventasIdTicketSecuenciaSpinner;
        checkedItems = new boolean[listItems.length];

        Log.d("PrincipalFragment", "fillSpinnerl Barra: " + ventasSpinner);
        Log.d("PrincipalFragment", "fillSpinnerl Barra2: " + ventasItems);
        Log.d("PrincipalFragment", "fillSpinnerl Barra3: " + ventasIdTicketSecuenciaSpinner);

        /********* Set value to spinner *************/
        if(ventasIdTicketSecuenciaSpinner == null)
            return;
        ArrayAdapter<String> adapter =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, ventasIdTicketSecuenciaItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTicket.setAdapter(adapter);
    }

    public void fillSpinnerWithoutInternetTest(){
        /********* Prepare value for spinner *************/

        String [] arrayLoteriasDescripcion = getResources().getStringArray(R.array.descripcionesLoterias);
        String [] arrayLoteriasIds = getResources().getStringArray(R.array.idLoterias);

        for (int i = 0; i < arrayLoteriasDescripcion.length; i++)
        {
                idLoteriasMap.put(i,arrayLoteriasIds[i]);

        }

        /********** LLenamos el listItems para el multiselect ***********/
        listItems = arrayLoteriasDescripcion;
        checkedItems = new boolean[listItems.length];

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

        getJugadas();

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
        if(!jugadasClase.validarJugadaSeaCorrecta(jugada)){
            Toast.makeText(getContext(), "La jugada no es correcta", Toast.LENGTH_SHORT).show();
            return;
        }





            JSONObject jugadaObject = new JSONObject();

            if(mUserItems.size() == 1){
                try {
                    boolean existe = false, existeInvertida = false;
                    existe = jugadasClase.siJugadaExisteActualizar(jugadasClase.jugadaQuitarPunto(jugada), idLoteriasMap.get(mUserItems.get(0)), monto);
                    if(jugada.length() == 3)
                        existeInvertida = jugadasClase.siJugadaExisteActualizar(jugadasClase.jugadaInvertir(jugadasClase.jugadaQuitarPunto(jugada)), idLoteriasMap.get(mUserItems.get(0)), monto);



                    if(!existe){

                        jugadaObject = new JSONObject();
                        jugadaObject.put("idLoteria", idLoteriasMap.get(mUserItems.get(0)));
                        jugadaObject.put("descripcion", listItems[mUserItems.get(0)]);
                        jugadaObject.put("jugada", jugadasClase.jugadaQuitarPunto(jugada));
                        jugadaObject.put("tam", txtJugada.getText().length());
                        jugadaObject.put("monto", txtMontojugar.getText());
                        jugadaObject.put("idBanca", Utilidades.getIdBanca(mContext));
                        jugadasClase.add(jugadaObject);


                    }

                    if(jugada.length() == 3 && existeInvertida == false) {

                        //Invertimos la jugada
                        String jugadaInvertida = jugadasClase.jugadaInvertir(jugadasClase.jugadaQuitarPunto(jugada));



                        JSONObject jugadaObject2 = new JSONObject();
                        jugadaObject2.put("idLoteria", idLoteriasMap.get(mUserItems.get(0)));
                        jugadaObject2.put("descripcion", listItems[mUserItems.get(0)]);
                        jugadaObject2.put("jugada", jugadaInvertida);
                        jugadaObject2.put("tam", txtJugada.getText().length());
                        jugadaObject2.put("monto", txtMontojugar.getText());
                        jugadaObject2.put("idBanca", Utilidades.getIdBanca(mContext));
                        jugadasClase.add(jugadaObject2);
                    }

                    txtJugada.setText("");
                    txtMontodisponible.setText("");
                }catch (Exception e){
                    Log.e("Error jugada:", e.toString());
                }
            }else if(mUserItems.size() > 1){
                for (int contadorLoteria = 0; contadorLoteria < mUserItems.size(); contadorLoteria++) {
                    try {
                        boolean existe = false, existeInvertida = false;

                        existe = jugadasClase.siJugadaExisteActualizar(jugadasClase.jugadaQuitarPunto(jugada), idLoteriasMap.get(mUserItems.get(contadorLoteria)), monto);
                        if(jugada.length() == 3)
                            existeInvertida = jugadasClase.siJugadaExisteActualizar(jugadasClase.jugadaInvertir(jugadasClase.jugadaQuitarPunto(jugada)), idLoteriasMap.get(mUserItems.get(contadorLoteria)), monto);

                        if(!existe){

                            jugadaObject = new JSONObject();
                            jugadaObject.put("idLoteria", idLoteriasMap.get(mUserItems.get(contadorLoteria)));
                            jugadaObject.put("descripcion", listItems[contadorLoteria]);
                            jugadaObject.put("jugada", jugadasClase.jugadaQuitarPunto(jugada));
                            jugadaObject.put("tam", txtJugada.getText().length());
                            jugadaObject.put("monto", txtMontojugar.getText());
                            jugadaObject.put("idBanca", Utilidades.getIdBanca(mContext));
                            jugadasClase.add(jugadaObject);

                            //Toast.makeText(getContext(), "lot:"+listItems[contadorLoteria] + " id:" + idLoteriasMap.get(contadorLoteria), Toast.LENGTH_SHORT).show();
                        }

                        if(jugada.length() == 3 && existeInvertida == false) {
                            //Invertimos la jugada
                            String jugadaInvertida = jugadasClase.jugadaInvertir(jugadasClase.jugadaQuitarPunto(jugada));

                            Log.d("jugadainvertida:", jugadaInvertida);

                            JSONObject jugadaObject2 = new JSONObject();
                            jugadaObject2.put("idLoteria", idLoteriasMap.get(mUserItems.get(contadorLoteria)));
                            jugadaObject2.put("descripcion", listItems[contadorLoteria]);
                            jugadaObject2.put("jugada", jugadaInvertida);
                            jugadaObject2.put("tam", txtJugada.getText().length());
                            jugadaObject2.put("monto", txtMontojugar.getText());
                            jugadaObject2.put("idBanca", Utilidades.getIdBanca(mContext));
                            jugadasClase.add(jugadaObject2);
                        }
                        txtJugada.setText("");
                        txtMontodisponible.setText("");
                    }catch (Exception e){
                        Log.e("Error jugada:", e.toString());
                    }
                }
            }


        //Log.d("jugadas:", jugadas.toString());
        calcularTotal();
        getJugadas();
        borderChange(true);

    } //End jugadasAdd


    public static void calcularTotal(){
        montoTotal = jugadasClase.calcularTotal();
        Log.d("PrincipalFragment", "calcularTotal: " + String.valueOf(montoTotal));
        txtTotal.setText("Tot: $"+String.valueOf(montoTotal) + ".00");
        if(ckbDescuento.isChecked() && montoTotal > 0){
            montoDescuento = (montoTotal / deCada) * descontar;
            txtDescuento.setText("Des: $"+String.valueOf(montoDescuento) + ".00");
        }else{
            montoDescuento = 0;
            txtDescuento.setText("Des: $"+String.valueOf(montoDescuento) + ".00");
        }
    }

    //private void arregloEliminarElemento(Array array, )

    @Override
    public void setDuplicar(JSONArray jugadas) {
        Log.d("PrincipalFragment", "Hola");
    }

    public void aceptaCancelarTicket(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        cancelarTicket();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


    private void cancelarTicket(){

        String url = "http://loterias.ml/api/principal/cancelar";

        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            loteria.put("codigoBarra", ventasItems[spinnerTicket.getSelectedItemPosition()]);
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
}
