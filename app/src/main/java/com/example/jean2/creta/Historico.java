package com.example.jean2.creta;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class Historico extends AppCompatActivity {
    Context mContext;
    TableLayout table;
    public static ProgressBar progressBarToolbar;
    TextView txtFechaDesde;
    TextView txtFechaHasta;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListenerHasta;
    private DatePickerDialog.OnDateSetListener mDateSetListenerFinal;
    Button btnBuscar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        progressBarToolbar = (ProgressBar)findViewById(R.id.toolbar_progress_bar);
        mContext = this;



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
                        mDateSetListener,
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

    private void getHistorico(){
        String url = Utilidades.URL +"/api/reportes/historico";
        progressBarToolbar.setVisibility(View.VISIBLE);


        JSONObject dato = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            dato.put("idUsuario", Utilidades.getIdUsuario(mContext));
            dato.put("fechaDesde", txtFechaDesde.getText());
            dato.put("fechaHasta", txtFechaDesde.getText());

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


                            updatetable(response.getJSONArray("bancas"));
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

    public  void updatetable(JSONArray datos){
        Log.d("JugadasFragment:", mContext.toString());


        if(datos == null){
            return ;
        }
        table.removeAllViews();
        int idRow = 0;

        if(datos.length() == 0){
            table.removeAllViews();
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
                    tableRow1.addView(createTv("Banca", true, mContext, true));
                    tableRow1.addView(createTv("Venta", true, mContext, true));
                    tableRow1.addView(createTv("Comis.", true, mContext, true));
                    tableRow1.addView(createTv("Desc.", true, mContext, true));
                    tableRow1.addView(createTv("Premios", true, mContext, true));
                    tableRow1.addView(createTv("Neto", true, mContext, true));
                    tableRow1.addView(createTv("Balance", true, mContext, true));
                    table.addView(tableRow1);
                }
                /* add views to the row */
                tableRow.setId(idRow);
                tableRow.addView(createTv(dato.getString("descripcion"), false, mContext, true));
                tableRow.addView(createTv(dato.getString("ventas"), false, mContext, true));
                tableRow.addView(createTv(dato.getString("comisiones"), false, mContext, true));
                tableRow.addView(createTv(dato.getString("descuentos"), false, mContext, true));
                tableRow.addView(createTv(dato.getString("premios"), false, mContext, true));

                TextView txtNeto = createTv(dato.getString("totalNeto"), false, mContext, true);
                if(dato.getDouble("totalNeto") < 0){
                    txtNeto.setBackgroundColor(Color.parseColor("#ffcccc"));
                }
                tableRow.addView(txtNeto);

                TextView txtBalanceActual = createTv(dato.getString("balanceActual"), false, mContext, true);
                if(dato.getDouble("balanceActual") < 0){
                    txtBalanceActual.setBackgroundColor(Color.parseColor("#ffcccc"));
                }
                tableRow.addView(txtBalanceActual);




                table.addView(tableRow);
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
