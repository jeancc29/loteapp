package com.example.jean2.creta;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class VentasActivity extends AppCompatActivity {
    private Toolbar toolbar;
    public static ProgressBar progressBarToolbar;


    TextView txtFechaInicial;
    TextView txtFechaFinal;
    Button btnBuscar;

    Context mContext;
    ProgressBar progressBar;
    TableLayout tableLayout;
    TableLayout tableTotalesPorLoteria;
    TableLayout tableNumerosGanadores;
    TableLayout tableTicketsGanadores;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListenerFinal;
    private JSONArray tickets = new JSONArray();
    TextView txtBalanceHastaLaFecha;
    TextView txtBalance;
    TextView txtBancaDescripcion;
    TextView txtCodigoBanca;
    TextView txtPendiente;
    TextView txtPerdedores;
    TextView txtGanadores;
    TextView txtTotalGanadoresPerdedoresPendiente;
    TextView txtVenta;
    TextView txtComisiones;
    TextView txtDescuentos;
    TextView txtPremios;
    TextView txtNeto;
    TextView txtFinal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Ventas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBarToolbar = (ProgressBar)findViewById(R.id.toolbar_progress_bar);



        mContext = this;
        //mQueue = Volley.newRequestQueue(mContext);
        tableLayout = (TableLayout)findViewById(R.id.tableMonitoreo);
        tableTotalesPorLoteria = (TableLayout)findViewById(R.id.tableTotalesPorLoteria);
        tableNumerosGanadores = (TableLayout)findViewById(R.id.tableNumerosGanadores);
        tableTicketsGanadores = (TableLayout)findViewById(R.id.tableTicketsGanadores);
        txtFechaInicial = (TextView) findViewById(R.id.txtFechaInicial);
        btnBuscar= (Button) findViewById(R.id.btnBuscar);


        txtBalanceHastaLaFecha = (TextView)findViewById(R.id.txtBalanceHastaLaFecha);
        txtBancaDescripcion = (TextView)findViewById(R.id.txtBancaDescripcion);
        txtCodigoBanca = (TextView)findViewById(R.id.txtCodigoBanca);
        txtPendiente = (TextView)findViewById(R.id.txtPendiente);
        txtPerdedores = (TextView)findViewById(R.id.txtPerdedores);
        txtGanadores = (TextView)findViewById(R.id.txtGanadores);
        txtTotalGanadoresPerdedoresPendiente = (TextView)findViewById(R.id.txtTotalGanadoresPerdedoresPendiente);
        txtVenta = (TextView)findViewById(R.id.txtVenta);
        txtComisiones = (TextView)findViewById(R.id.txtComisiones);
        txtDescuentos = (TextView)findViewById(R.id.txtDescuentos);
        txtPremios = (TextView)findViewById(R.id.txtPremios);
        txtNeto = (TextView)findViewById(R.id.txtNeto);
        txtFinal = (TextView)findViewById(R.id.txtFinal);
        txtBalance = (TextView)findViewById(R.id.txtBalance);


        Calendar calendarIncial = Calendar.getInstance();
        int yearActual = calendarIncial.get(Calendar.YEAR);
        int monthActual = calendarIncial.get(Calendar.MONTH) + 1;
        int dayActual = calendarIncial.get(Calendar.DAY_OF_MONTH);

        txtFechaInicial.setText(String.valueOf(yearActual) + "-" + String.valueOf(monthActual) + "-" + String.valueOf(dayActual));

        txtFechaInicial.setOnClickListener(new View.OnClickListener() {
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
                txtFechaInicial.setText(date);

            }
        };



        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVentas();
            }
        });
        btnBuscar.performClick();
    }


    private void getVentas(){
        String url = "https://loterias.ml/api/reportes/ventas";
        progressBarToolbar.setVisibility(View.VISIBLE);


        JSONObject dato = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            dato.put("idUsuario", Utilidades.getIdUsuario(mContext));
            dato.put("fecha", txtFechaInicial.getText());
            dato.put("idBanca", Utilidades.getIdBanca(mContext));

            dato.put("layout", "Principal");
            datosObj.put("datos", dato);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String jsonString = datosObj.toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBarToolbar.setVisibility(View.GONE);
                        try {
                            txtBalanceHastaLaFecha.setText(response.getString("balanceHastaLaFecha"));
                            JSONObject jsonObject = response.getJSONObject("banca");
                            txtBancaDescripcion.setText(jsonObject.getString("descripcion"));
                            txtCodigoBanca.setText(jsonObject.getString("codigo"));
                            txtPendiente.setText(response.getString("pendientes"));
                            txtPerdedores.setText(response.getString("perdedores"));
                            txtGanadores.setText(response.getString("ganadores"));
                            txtTotalGanadoresPerdedoresPendiente.setText(response.getString("total"));
                            txtVenta.setText(response.getString("ventas"));
                            txtComisiones.setText(response.getString("comisiones"));
                            txtDescuentos.setText(response.getString("descuentos"));
                            txtPremios.setText(response.getString("premios"));
                            txtNeto.setText(response.getString("neto"));
                            txtFinal.setText(response.getString("neto"));
                            txtBalance.setText(response.getString("balanceActual"));


                            if(response.getDouble("premios") > response.getDouble("ventas")){
                                txtPremios.setBackgroundColor(Color.parseColor("#ffcccc"));
                            }else{
                                txtPremios.setBackgroundColor(Color.parseColor("#eae9e9"));
                            }

                            if(response.getDouble("neto") < 0){
                                txtNeto.setBackgroundColor(Color.parseColor("#ffcccc"));
                            }else{
                                txtNeto.setBackgroundColor(Color.parseColor("#ffffff"));
                            }

                            if(response.getDouble("balanceActual") < 0){
                                txtBalance.setBackgroundColor(Color.parseColor("#ffcccc"));
                            }else{
                                txtBalance.setBackgroundColor(Color.parseColor("#eae9e9"));
                            }


                            updateTableTotalesPorLoteria(response.getJSONArray("loterias"));
                            updateTableNumerosGanadores(response.getJSONArray("loterias"));
                            updateTableTicketsGanadores(response.getJSONArray("ticketsGanadores"));
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


    public  void updateTableTotalesPorLoteria(JSONArray datos){
        Log.d("JugadasFragment:", mContext.toString());


        if(datos == null){
            return ;
        }
        tableTotalesPorLoteria.removeAllViews();
        int idRow = 0;

        if(datos.length() == 0){
            tableTotalesPorLoteria.removeAllViews();
            Toast.makeText(mContext, "No hay datos", Toast.LENGTH_SHORT).show();
            return;
        }
        for(int i=0; i < datos.length(); i++){
            try {

                JSONObject dato = datos.getJSONObject(i);


                LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                /* create a table row */
                TableRow tableRow = new TableRow(mContext);
                tableRow.setLayoutParams(tableRowParams);

//                if(i == 0) {
//                    tableRow.setBackgroundColor(Color.parseColor("#eae9e9"));
//                }
//                else if((i % 2) == 0){
//                    tableRow.setBackgroundColor(Color.parseColor("#eae9e9"));
//                }

                if((i % 2) != 0){
                    tableRow.setBackgroundColor(Color.parseColor("#eae9e9"));
                }

                tableRow.setId(idRow);




                if(i == 0){
                    /* add views to the row */
                    TableRow tableRow1 = new TableRow(mContext);
                    tableRow1.setId(idRow);
                    tableRow1.setLayoutParams(tableRowParams);
                    idRow ++;
                    tableRow1.addView(createTv("Loteria", true, mContext, true));
                    tableRow1.addView(createTv("Venta total", true, mContext, true));
                    tableRow1.addView(createTv("Comisiones", true, mContext, true));
                    tableRow1.addView(createTv("Premios", true, mContext, true));
                    tableRow1.addView(createTv("Neto", true, mContext, true));
                    tableTotalesPorLoteria.addView(tableRow1);
                }
                /* add views to the row */
                tableRow.setId(idRow);
                tableRow.addView(createTv(dato.getString("descripcion"), false, mContext, true));
                tableRow.addView(createTv(dato.getString("ventas"), false, mContext, true));
                tableRow.addView(createTv(dato.getString("comisiones"), false, mContext, true));
                tableRow.addView(createTv(dato.getString("premios"), false, mContext, true));
                tableRow.addView(createTv(dato.getString("neto"), false, mContext, true));




                tableTotalesPorLoteria.addView(tableRow);
                idRow++;
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    public  void updateTableNumerosGanadores(JSONArray datos){
        Log.d("JugadasFragment:", mContext.toString());


        if(datos == null){
            return ;
        }
        tableNumerosGanadores.removeAllViews();
        int idRow = 0;

        if(datos.length() == 0){
            tableNumerosGanadores.removeAllViews();
            Toast.makeText(mContext, "No hay datos", Toast.LENGTH_SHORT).show();
            return;
        }
        for(int i=0; i < datos.length(); i++){
            try {

                JSONObject dato = datos.getJSONObject(i);


                LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                /* create a table row */
                TableRow tableRow = new TableRow(mContext);
                tableRow.setLayoutParams(tableRowParams);


//                if(i == 0) {
//                    tableRow.setBackgroundColor(Color.parseColor("#eae9e9"));
//                }
//                else if((i % 2) == 0){
//                    tableRow.setBackgroundColor(Color.parseColor("#eae9e9"));
//                }

                if((i % 2) != 0){
                    tableRow.setBackgroundColor(Color.parseColor("#eae9e9"));
                }

                tableRow.setId(idRow);




                if(i == 0){
                    /* add views to the row */
                    TableRow tableRow1 = new TableRow(mContext);
                    tableRow1.setId(idRow);
                    tableRow1.setLayoutParams(tableRowParams);
                    idRow ++;
                    tableRow1.addView(createTv("Loteria", true, mContext, true));
                    tableRow1.addView(createTv("1ra", true, mContext, true));
                    tableRow1.addView(createTv("2da", true, mContext, true));
                    tableRow1.addView(createTv("3ra", true, mContext, true));
                    tableRow1.addView(createTv("Cash3", true, mContext, true));
                    tableRow1.addView(createTv("Cash4", true, mContext, true));
                    tableNumerosGanadores.addView(tableRow1);
                }
                /* add views to the row */
                tableRow.setId(idRow);
                String primera = dato.getString("primera");
                String segunda = dato.getString("segunda");
                String tercera = dato.getString("tercera");
                String pick3 = (dato.getString("pick3").equals("null")) ? "-" : dato.getString("pick3");
                String pick4 = (dato.getString("pick4").equals("null")) ? "-" : dato.getString("pick4");
                tableRow.addView(createTv(dato.getString("descripcion"), false, mContext, true));
                tableRow.addView(createTv(dato.getString("primera"), false, mContext, true));
                tableRow.addView(createTv(dato.getString("segunda"), false, mContext, true));
                tableRow.addView(createTv(dato.getString("tercera"), false, mContext, true));
                tableRow.addView(createTv(pick3, false, mContext, true));
                tableRow.addView(createTv(pick4, false, mContext, true));




                tableNumerosGanadores.addView(tableRow);
                idRow++;
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    public  void updateTableTicketsGanadores(JSONArray datos){
        Log.d("JugadasFragment:", mContext.toString());


        if(datos == null){
            return ;
        }
        tableTicketsGanadores.removeAllViews();
        int idRow = 0;

        if(datos.length() == 0){
            tableTicketsGanadores.removeAllViews();
            LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            /* create a table row */
//            TableRow tableRow = new TableRow(mContext);
////            tableRow.setLayoutParams(tableRowParams);
////
////            tableRow.setId(0);

            TableRow tableRow1 = new TableRow(mContext);
            tableRow1.setId(0);
            tableRow1.setLayoutParams(tableRowParams);
            tableRow1.addView(createTv("Fecha", true, mContext, true));
            tableRow1.addView(createTv("Numero de ticket", true, mContext, true));
            tableRow1.addView(createTv("A pagar", true, mContext, true));



            tableTicketsGanadores.addView(tableRow1);

            return;
        }
        for(int i=0; i < datos.length(); i++){
            try {

                JSONObject dato = datos.getJSONObject(i);


                LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                /* create a table row */
                TableRow tableRow = new TableRow(mContext);
                tableRow.setLayoutParams(tableRowParams);

//                if(i == 0) {
//                    tableRow.setBackgroundColor(Color.parseColor("#eae9e9"));
//                }
//                else if((i % 2) == 0){
//                    tableRow.setBackgroundColor(Color.parseColor("#eae9e9"));
//                }

                if((i % 2) != 0){
                    tableRow.setBackgroundColor(Color.parseColor("#eae9e9"));
                }
                tableRow.setId(idRow);




                if(i == 0){
                    /* add views to the row */
                    TableRow tableRow1 = new TableRow(mContext);
                    tableRow1.setId(idRow);
                    tableRow1.setLayoutParams(tableRowParams);
                    idRow ++;
                    tableRow1.addView(createTv("Fecha", true, mContext, true));
                    tableRow1.addView(createTv("Numero de ticket", true, mContext, true));
                    tableRow1.addView(createTv("A pagar", true, mContext, true));

                    tableTicketsGanadores.addView(tableRow1);
                }
                /* add views to the row */
                tableRow.setId(idRow);
                tableRow.addView(createTv(dato.getString("fecha"), false, mContext, true));
                String secuencia = Utilidades.toSecuencia(dato.getString("idTicket"), dato.getString("codigo"));
                tableRow.addView(createTv(secuencia, false, mContext, true));
                tableRow.addView(createTv(dato.getString("premio"), false, mContext, true));

                tableTicketsGanadores.addView(tableRow);
                idRow++;
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    private static TextView createTv(String text, boolean es_header, Context context, boolean center){
        /* create cell element - textview */
        TextView tv = new TextView(context);
        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        cellParams.weight = 1;
        tv.setLayoutParams(cellParams);

        //tv.setBackgroundColor(0xff12dd12);
        tv.setText(text);

        if(es_header){
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            tv.setBackgroundResource(R.color.colorPrimary);
            tv.setTextColor(Color.WHITE);
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
}
