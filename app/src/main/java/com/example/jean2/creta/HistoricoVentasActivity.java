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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TableLayout;
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

public class HistoricoVentasActivity extends AppCompatActivity {

    private Toolbar toolbar;
    public static ProgressBar progressBarToolbar;
    TextView txtFechaInicial;
    TextView txtFechaFinal;
    Button btnBuscar;

    Context mContext;
    ProgressBar progressBar;
    TableLayout tableLayout;
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
        setContentView(R.layout.activity_historico_ventas);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Historico de ventas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBarToolbar = (ProgressBar)findViewById(R.id.toolbar_progress_bar);


        mContext = this;
        //mQueue = Volley.newRequestQueue(mContext);
        tableLayout = (TableLayout)findViewById(R.id.tableMonitoreo);
        txtFechaInicial = (TextView) findViewById(R.id.txtFechaInicial);
        txtFechaFinal= (TextView) findViewById(R.id.txtFechaFinal);
        btnBuscar= (Button) findViewById(R.id.btnBuscar);



        txtBalanceHastaLaFecha = (TextView)findViewById(R.id.txtBalanceHastaLaFecha);
        txtBalance = (TextView)findViewById(R.id.txtBalance);
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
        txtFechaFinal.setText(String.valueOf(yearActual) + "-" + String.valueOf(monthActual) + "-" + String.valueOf(dayActual));

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

        txtFechaFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        mContext,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListenerFinal,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListenerFinal = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d("MonitoreoActivity", "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

//                String date = month + "/" + day + "/" + year;
                String date = year + "-" + month + "-" + day;
                txtFechaFinal.setText(date);

            }
        };

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVentas();
            }
        });

    }

    private void getVentas(){
        String url = "https://loterias.ml/api/reportes/ventas";
        progressBarToolbar.setVisibility(View.VISIBLE);


        JSONObject dato = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            dato.put("idUsuario", Utilidades.getIdUsuario(mContext));
            dato.put("fecha", txtFechaInicial.getText());
            dato.put("fechaFinal", txtFechaFinal.getText());
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

                        try {
                            progressBarToolbar.setVisibility(View.GONE);

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
//                String body;
//                if(error.networkResponse.data!=null) {
//                    try {
//                        body = new String(error.networkResponse.data,"UTF-8");
//
//                        Log.d("responseerror: ", body);
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        });

//        mQueue.add(request);
        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }
}
