package com.example.jean2.creta;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jean2.creta.Clases.BancaClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Historico extends AppCompatActivity {
    private Toolbar toolbar;
    Context mContext;
    TableLayout table;
    public static ProgressBar progressBarToolbar;
    TextView txtFechaDesde;
    TextView txtFechaHasta;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListenerHasta;
    private DatePickerDialog.OnDateSetListener mDateSetListenerFinal;
    Button btnBuscar;
    Spinner spinnerOpcion;
    List<BancaClass> bancas = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        progressBarToolbar = (ProgressBar)findViewById(R.id.toolbar_progress_bar);
        mContext = this;
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Historico ventas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinnerOpcion = (Spinner)findViewById(R.id.spinnerOpcion);
        fillSpinner();
        spinnerOpcion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the spinner selected item text
                //String selectedItemText = (String) adapterView.getItemAtPosition(i);
                // Display the selected item into the TextView
                //mTextView.setText("Selected : " + selectedItemText);
                updatetable();
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                Toast.makeText(mContext,"No selection",Toast.LENGTH_LONG).show();
            }
        });


        table = (TableLayout)findViewById(R.id.tableTotalesPorLoteria);
        btnBuscar= (Button) findViewById(R.id.btnBuscar);


        txtFechaDesde = (TextView) findViewById(R.id.txtFechaDesde);
        txtFechaHasta = (TextView) findViewById(R.id.txtFechaHasta);
        Calendar calendarIncial = Calendar.getInstance();
        int yearActual = calendarIncial.get(Calendar.YEAR);
        int monthActual = calendarIncial.get(Calendar.MONTH) + 1;
        int dayActual = calendarIncial.get(Calendar.DAY_OF_MONTH);
        txtFechaDesde.setText(String.valueOf(yearActual) + "-" + String.valueOf(monthActual) + "-" + String.valueOf(dayActual));
        txtFechaDesde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        mContext,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d("MonitoreoActivity", "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

//                String date = month + "/" + day + "/" + year;
                String date = year + "-" + month + "-" + day;
                txtFechaDesde.setText(date);

            }
        };

        txtFechaHasta.setText(String.valueOf(yearActual) + "-" + String.valueOf(monthActual) + "-" + String.valueOf(dayActual));
        txtFechaHasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        mContext,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListenerHasta,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListenerHasta = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d("MonitoreoActivity", "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

//                String date = month + "/" + day + "/" + year;
                String date = year + "-" + month + "-" + day;
                txtFechaHasta.setText(date);

            }
        };



        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getHistorico();
            }
        });
        btnBuscar.performClick();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getHistorico(){
        String url = Utilidades.URL +"/api/reportes/historico";
        progressBarToolbar.setVisibility(View.VISIBLE);


        JSONObject dato = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            dato.put("idUsuario", Utilidades.getIdUsuario(mContext));
            dato.put("fechaDesde", txtFechaDesde.getText());
            dato.put("fechaHasta", txtFechaHasta.getText());

            dato.put("layout", "Principal");
            datosObj.put("datos", dato);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String jsonString = datosObj.toString();
        bancas.clear();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBarToolbar.setVisibility(View.GONE);
                        try {
//                            txtBalanceHastaLaFecha.setText(response.getString("balanceHastaLaFecha"));
//                            JSONObject jsonObject = response.getJSONObject("banca");
//                            txtBancaDescripcion.setText(jsonObject.getString("descripcion"));
//                            txtCodigoBanca.setText(jsonObject.getString("codigo"));
//                            txtPendiente.setText(response.getString("pendientes"));
//                            txtPerdedores.setText(response.getString("perdedores"));
//                            txtGanadores.setText(response.getString("ganadores"));
//                            txtTotalGanadoresPerdedoresPendiente.setText(response.getString("total"));
//                            txtVenta.setText(response.getString("ventas"));
//                            txtComisiones.setText(response.getString("comisiones"));
//                            txtDescuentos.setText(response.getString("descuentos"));
//                            txtPremios.setText(response.getString("premios"));
//                            txtNeto.setText(response.getString("neto"));
//                            txtFinal.setText(response.getString("neto"));
//                            txtBalance.setText(response.getString("balanceActual"));


//                            if(response.getDouble("premios") > response.getDouble("ventas")){
//                                txtPremios.setBackgroundColor(Color.parseColor("#ffcccc"));
//                            }else{
//                                txtPremios.setBackgroundColor(Color.parseColor("#eae9e9"));
//                            }
//
//                            if(response.getDouble("neto") < 0){
//                                txtNeto.setBackgroundColor(Color.parseColor("#ffcccc"));
//                            }else{
//                                txtNeto.setBackgroundColor(Color.parseColor("#ffffff"));
//                            }
//
//                            if(response.getDouble("balanceActual") < 0){
//                                txtBalance.setBackgroundColor(Color.parseColor("#ffcccc"));
//                            }else{
//                                txtBalance.setBackgroundColor(Color.parseColor("#eae9e9"));
//                            }

                            JSONArray jsonArrayBancas = response.getJSONArray("bancas");
                            for(int i=0; i< jsonArrayBancas.length(); i++){
                                BancaClass bancaClass = new BancaClass();
                                JSONObject jsonObject = jsonArrayBancas.getJSONObject(i);
                                bancaClass.setDescripcion(jsonObject.getString("descripcion"));
                                bancaClass.setVentas(jsonObject.getDouble("ventas"));
                                bancaClass.setComisiones(jsonObject.getDouble("comisiones"));
                                bancaClass.setDescuentos(jsonObject.getDouble("descuentos"));
                                bancaClass.setPremios(jsonObject.getDouble("premios"));
                                bancaClass.setNeto(jsonObject.getDouble("totalNeto"));
                                bancaClass.setBalance(jsonObject.getDouble("balance"));
                                bancaClass.setBalanceActual(jsonObject.getDouble("balanceActual"));
                                bancaClass.setTicketsPendientes(jsonObject.getInt("pendientes"));
                                bancas.add(bancaClass);
                            }
                            updatetable();
                        } catch (JSONException e) {
                            Log.d("Error: ", e.toString());
                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("responseerror: ", String.valueOf(error));
                progressBarToolbar.setVisibility(View.GONE);
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
                String body;
                if(error.networkResponse.data!=null) {
                    try {
                        body = new String(error.networkResponse.data,"UTF-8");

                        Log.d("responseerror: ", body);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

//        mQueue.add(request);
        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    public  void updatetable(){

        if(bancas == null){
            return ;
        }
        table.removeAllViews();
        int idRow = 0;

        if(bancas.size() == 0){
            table.removeAllViews();
            Toast.makeText(mContext, "No hay datos", Toast.LENGTH_SHORT).show();
            return;
        }

        List<BancaClass> bancasTemporal = new ArrayList<>();
        for(BancaClass b : bancas){
            if(spinnerOpcion.getSelectedItem().toString().equals("Con ventas")){
                if(b.getVentas() <= 0){
                    continue;
                }
            }
            else if(spinnerOpcion.getSelectedItem().toString().equals("Con premios")){
                if(b.getPremios() <= 0){
                    continue;
                }
            }
            else if(spinnerOpcion.getSelectedItem().toString().equals("Con tickets pendientes")){
                if(b.getTicketsPendientes() <= 0){
                    continue;
                }
            }

            bancasTemporal.add(b);
        }
        int i =0;
        boolean primerCiclo = true;
        for(BancaClass b : bancasTemporal){

                LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                /* create a table row */
                TableRow tableRow = new TableRow(mContext);
                tableRow.setLayoutParams(tableRowParams);
                

                if((i % 2) != 0){
                    tableRow.setBackgroundColor(Color.parseColor("#eae9e9"));
                }

                tableRow.setId(idRow);




                if(primerCiclo){
                    /* add views to the row */
                    TableRow tableRow1 = new TableRow(mContext);
                    tableRow1.setId(idRow);
                    tableRow1.setLayoutParams(tableRowParams);
                    idRow ++;
                    tableRow1.addView(createTv("Banca", 1, mContext, true));
                    tableRow1.addView(createTv("Venta", 1, mContext, true));
                    tableRow1.addView(createTv("Comis.", 1, mContext, true));
                    tableRow1.addView(createTv("Desc.", 1, mContext, true));
                    tableRow1.addView(createTv("Premios", 1, mContext, true));
                    tableRow1.addView(createTv("Neto", 1, mContext, true));
                    tableRow1.addView(createTv("Balance", 1, mContext, true));
                    tableRow1.addView(createTv("Balance mas ventas", 1, mContext, true));
                    table.addView(tableRow1);
                    primerCiclo = false;
                }




                /* add views to the row */
                tableRow.setId(idRow);
                tableRow.addView(createTv(b.getDescripcion(), 3, mContext, true));
                tableRow.addView(createTv(String.valueOf(b.getVentas()), 3, mContext, true));
                tableRow.addView(createTv(String.valueOf(b.getComisiones()), 3, mContext, true));
                tableRow.addView(createTv(String.valueOf(b.getDescuentos()), 3, mContext, true));
                tableRow.addView(createTv(String.valueOf(b.getPremios()), 3, mContext, true));
                tableRow.addView(asignarColorBackground(createTv(String.valueOf(b.getNeto()), 3, mContext, true), b.getNeto()));
                tableRow.addView(asignarColorBackground(createTv(String.valueOf(b.getBalance()), 3, mContext, true), b.getBalance()));
                tableRow.addView(asignarColorBackground(createTv(String.valueOf(b.getBalanceActual()), 3, mContext, true), b.getBalanceActual()));



            table.addView(tableRow);
            idRow++;
            i++;

            if(i < bancasTemporal.size() == false){
                    TableRow tableRow1 = new TableRow(mContext);
                    tableRow1.setId(idRow);
                    tableRow1.setLayoutParams(tableRowParams);
                    idRow ++;
                    try{
                        JSONObject object = calcularTotal(bancasTemporal);
                        tableRow1.addView(createTv("Totales", 2, mContext, true));
                        tableRow1.addView(asignarColor(createTv(object.getString("ventas"), 2, mContext, true), object.getDouble("ventas")));
                        tableRow1.addView(asignarColor(createTv(object.getString("comisiones"), 2, mContext, true), object.getDouble("comisiones")));
                        tableRow1.addView(asignarColor(createTv(object.getString("descuentos"), 2, mContext, true), object.getDouble("descuentos")));
                        tableRow1.addView(asignarColor(createTv(object.getString("premios"), 2, mContext, true), object.getDouble("premios")));
                        tableRow1.addView(asignarColor(createTv(object.getString("netos"), 2, mContext, true), object.getDouble("netos")));
                        tableRow1.addView(asignarColor(createTv(object.getString("balances"), 2, mContext, true), object.getDouble("balances")));
                        tableRow1.addView(asignarColor(createTv(object.getString("balancesMasVentas"), 2, mContext, true), object.getDouble("balancesMasVentas")));
                        table.addView(tableRow1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    TableRow tableRow2 = new TableRow(mContext);
                    tableRow2.setId(idRow);
                    tableRow2.setLayoutParams(tableRowParams);
                    idRow ++;
                    tableRow2.addView(createTv("", 2, mContext, true));
                    tableRow2.addView(createTv("Venta", 2, mContext, true));
                    tableRow2.addView(createTv("Comis.", 2, mContext, true));
                    tableRow2.addView(createTv("Desc.", 2, mContext, true));
                    tableRow2.addView(createTv("Premios", 2, mContext, true));
                    tableRow2.addView(createTv("Neto", 2, mContext, true));
                    tableRow2.addView(createTv("Balance", 2, mContext, true));
                    tableRow2.addView(createTv("Balance mas ventas", 2, mContext, true));
                    table.addView(tableRow2);
                }


        }

    }

    private TextView asignarColor(TextView txt, double valor){
        if(valor < 0){
            Log.e("Historico", "asignarColor: " + String.valueOf(valor) );
            txt.setTextColor(ContextCompat.getColor(mContext, R.color.bgRed));
        }

        return txt;
    }

    private TextView asignarColorBackground(TextView txt, double valor){
        if(valor < 0){
            Log.e("Historico", "asignarColor: " + String.valueOf(valor) );
            txt.setBackgroundColor(Color.parseColor("#ffcccc"));
            txt.setTextColor(ContextCompat.getColor(mContext, R.color.bgRed));
        }else{
            txt.setBackgroundColor(Color.parseColor("#bfdde0"));
            txt.setTextColor(Color.parseColor("#095861"));
        }

        return txt;
    }

    private JSONObject calcularTotal(List<BancaClass> bancaClass)
    {
        JSONObject object = new JSONObject();
        double ventas = 0;
        double comisiones = 0;
        double descuentos = 0;
        double premios = 0;
        double netos = 0;
        double balances = 0;
        double balancesMasVentas = 0;

        for(BancaClass b : bancaClass){
            ventas += b.getVentas();
            comisiones += b.getComisiones();
            descuentos += b.getDescuentos();
            premios += b.getPremios();
            netos += b.getNeto();
            balances += b.getBalance();
            balancesMasVentas += b.getBalanceActual();
        }

        try {
            object.put("ventas", Utilidades.redondear(ventas));
            object.put("comisiones", Utilidades.redondear(comisiones));
            object.put("descuentos", Utilidades.redondear(descuentos));
            object.put("premios", Utilidades.redondear(premios));
            object.put("netos", Utilidades.redondear(netos));
            object.put("balances", Utilidades.redondear(balances));
            object.put("balancesMasVentas", Utilidades.redondear(balancesMasVentas));
        }catch (Exception e){
            e.printStackTrace();
        }

        return object;
    }


    private static TextView createTv(String text, int es_header_total_normal, Context context, boolean center){
        /* create cell element - textview */
        TextView tv = new TextView(context);
        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        cellParams.weight = 1;
        tv.setLayoutParams(cellParams);

        //tv.setBackgroundColor(0xff12dd12);
        tv.setText(text);

        if(es_header_total_normal == 1){
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            tv.setBackgroundResource(R.color.colorPrimary);
            tv.setTextColor(Color.WHITE);
            tv.setPadding(2, 10, 2, 10);
        }
        else if(es_header_total_normal == 2){
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            tv.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            tv.setPadding(2, 10, 2, 10);
        }
        else{
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            tv.setTextColor(Color.BLACK);
        }

        if(center)
            tv.setGravity(Gravity.CENTER);

        return tv;
    }

    public void fillSpinner(){
        /********* Prepare value for spinner *************/
        // jsonArrayVentas = jsonArrayVentas2;
        String[] opcionesSpinner = new String[4];
        int contador = 0;

        opcionesSpinner[0] = "Con ventas";
        opcionesSpinner[1] = "Todos";
        opcionesSpinner[2] = "Con premios";
        opcionesSpinner[3] = "Con tickets pendientes";




        /********* Set value to spinner *************/
        if(opcionesSpinner == null)
            return;
        try{
            ArrayAdapter<String> adapter =new ArrayAdapter<String>(Historico.this,android.R.layout.simple_spinner_item, opcionesSpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerOpcion.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }

//        seleccionarBancaPertenecienteAUsuario();
    }


//    public void seleccionarBancaPertenecienteAUsuario(){
//        int idBanca = Utilidades.getIdBanca(Historico.this);
//        int contador = 0;
//        for (BancaClass banca : bancas)
//        {
//            if(idBanca == banca.getId()){
//                break;
//            }
//            contador++;
//        }
////        bancas.get((int)spinnerBanca.getSelectedItemId());
//        spinnerBanca.setSelection(contador);
//    }
}
