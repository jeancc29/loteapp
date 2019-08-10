package com.example.jean2.creta;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MonitoreoActivity extends AppCompatActivity {
    Context mContext;
    static MonitoreoActivity mActivity;
    TableLayout tableLayout;
    TextView txtFechaMonitoreo;
    private Toolbar toolbar;
    ProgressBar progressBar;

    //private RequestQueue mQueue;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private JSONArray tickets = new JSONArray();
    public static JSONObject selectedTicket = new JSONObject();
    ExecutorService es = Executors.newScheduledThreadPool(30);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoreo);

        toolbar = (Toolbar) findViewById(R.id.toolBarMonitoreo);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = this;
        mActivity = this;
        //mQueue = Volley.newRequestQueue(mContext);
        tableLayout = (TableLayout)findViewById(R.id.tableMonitoreo);
        txtFechaMonitoreo = (TextView) findViewById(R.id.txtFechaMonitoreo);
        progressBar = (ProgressBar)findViewById(R.id.progressBarPrincipal);


        Calendar calendarIncial = Calendar.getInstance();
        int yearActual = calendarIncial.get(Calendar.YEAR);
        int monthActual = calendarIncial.get(Calendar.MONTH) + 1;
        int dayActual = calendarIncial.get(Calendar.DAY_OF_MONTH);


        txtFechaMonitoreo.setText(String.valueOf(yearActual) + "-" + String.valueOf(monthActual) + "-" + String.valueOf(dayActual));

        getMonitoreo();

        txtFechaMonitoreo.setOnClickListener(new View.OnClickListener() {
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
                txtFechaMonitoreo.setText(date);
                getMonitoreo();
            }
        };


    }

    public void aceptaCancelarTicket(final JSONObject ticket){
        if(BluetoothSearchDialog.isPrinterConnected() == false){
            Toast.makeText(mContext, "Debe conectarse a una impresora", Toast.LENGTH_SHORT).show();
            mostrarFragmentDialogBluetoothSearch();
//                mostrarDispositivosBluetooth();
            return;
        }

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        cancelarTicket(ticket);
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

    public  void updateTable(JSONArray datos){
        Log.d("JugadasFragment:", mContext.toString());

//                LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//
//        /* create a table row */
//        TableRow tableRow = new TableRow(mContext);
//        tableRow.setLayoutParams(tableRowParams);
//        tableRow.addView(createTv("Loteria", false, mContext));
//        tableRow.addView(createTv("Jugada", false, mContext));
//        tableRow.addView(createTv("Monto", false, mContext));
//        tableRow.addView(createTv("Eliminar", false, mContext));



        tickets = datos;
        if(datos == null){
            return ;
        }
        tableLayout.removeAllViews();
        int idRow = 0;

        if(datos.length() == 0){
            tableLayout.removeAllViews();
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

                if(i == 0) {
                    tableRow.setBackgroundColor(Color.parseColor("#eae9e9"));
                }
                else if((i % 2) == 0){
                    tableRow.setBackgroundColor(Color.parseColor("#eae9e9"));
                }

                tableRow.setId(idRow);




                if(i == 0){
                    /* add views to the row */
                    TableRow tableRow1 = new TableRow(mContext);
                    tableRow1.setId(idRow);
                    tableRow1.setLayoutParams(tableRowParams);
                    idRow ++;
                    tableRow1.addView(createTv("Numero/Imprim.", true, mContext, true));
                    tableRow1.addView(createTv("Mont", true, mContext, true));
                    tableRow1.addView(createTv("Cancelar", true, mContext, true));
                    tableLayout.addView(tableRow1);
                }
                /* add views to the row */
                tableRow.setId(idRow);

                TextView txtTicket = createTv(toSecuencia(dato.getString("idTicket"), dato.getString("codigo")), false, mContext, true);
                txtTicket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int id = ((View)view.getParent()).getId();


//                        TableRow r = (TableRow)((Activity) mContext).findViewById(id);
//                        TableLayout t = (TableLayout)((Activity) mContext).findViewById(R.id.tableJugadas);
//                        t.removeView(r);
//                        Log.d("Pariente:" , String.valueOf(id));

                        //Buscar ticket retorna un json object
                        selectedTicket = buscar(id - 1);
                        ImprimirCompartirVerTicketDialog imprimirCompartirVerTicketDialog = new ImprimirCompartirVerTicketDialog();
                        imprimirCompartirVerTicketDialog.show(getSupportFragmentManager(), "Accion a realizar");
                    }
                });


                //tableRow.addView(createTv(toSecuencia(dato.getString("idTicket"), dato.getString("codigo")), false, mContext, true));
                tableRow.addView(txtTicket);
                tableRow.addView(createTv(dato.getString("total"), false, mContext, false));

                /* create cell element - button */
                Button btn = new Button(mContext);
                btn.setText("x");
                btn.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                btn.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                //btn.setBackgroundColor(0xff12dd12);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int id = ((View)view.getParent()).getId();


//                        TableRow r = (TableRow)((Activity) mContext).findViewById(id);
//                        TableLayout t = (TableLayout)((Activity) mContext).findViewById(R.id.tableJugadas);
//                        t.removeView(r);
//                        Log.d("Pariente:" , String.valueOf(id));

                        //Buscar ticket retorna un json object
                        aceptaCancelarTicket(buscar(id - 1));
                    }
                });

                tableRow.addView(btn);




                /* add the row to the table */
                if(dato.getString("status").equals("0"))
                    tableRow.setVisibility(View.GONE);
                tableLayout.addView(tableRow);
                idRow++;
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    public String toSecuencia(String idTicket, String codigoBanca){
        String pad = "000000000";
        String ans = codigoBanca + "-"+ pad.substring(0, pad.length() - idTicket.length()) + idTicket;
        return ans;
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


    private void getMonitoreo(){
        String url = "http://loterias.ml/api/reportes/monitoreo";
        progressBar.setVisibility(View.VISIBLE);


        JSONObject dato = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            dato.put("idUsuario", Utilidades.getIdUsuario(mContext));
            dato.put("fecha", txtFechaMonitoreo.getText());
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
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONArray jsonArray = response.getJSONArray("monitoreo");
                            updateTable(jsonArray);
                        } catch (JSONException e) {
                            Log.d("Error: ", e.toString());
                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("responseerror: ", String.valueOf(error));
                progressBar.setVisibility(View.GONE);
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

    private JSONObject buscar(int id){
        JSONObject jsonObject = new JSONObject();
        try {
            for (int i=0; i < tickets.length(); i++){
                if(id == i){
                    jsonObject = tickets.getJSONObject(i);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return jsonObject;
    }

    private void cancelarTicket(JSONObject jsonObject){
        String url = "http://loterias.ml/api/principal/cancelar";

        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            loteria.put("codigoBarra", jsonObject.getString("codigoBarra"));
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

                                ImprimirTicketCancelado();
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


    public static void mostrarFragmentDialogBluetoothSearch(){
        BluetoothSearchDialog duplicarDialog = new BluetoothSearchDialog();
        duplicarDialog.show( mActivity.getSupportFragmentManager(), "Duplicar dialog");
//                mostrarDispositivosBluetooth();
    }


    private void ImprimirTicketCancelado(){
        if(BluetoothSearchDialog.isPrinterConnected() == false){
            Toast.makeText(mContext, "Debe conectarse a una impresora", Toast.LENGTH_SHORT).show();
            MonitoreoActivity.mostrarFragmentDialogBluetoothSearch();
//                mostrarDispositivosBluetooth();
            return;
        }


        try{
            JSONObject venta = new JSONObject();
            venta.put("venta", selectedTicket);
            Log.d("MonitoreoCancelado", venta.toString());
            es.submit(new BluetoothSearchDialog.TaskPrint(venta, 1));

            getMonitoreo();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void VerTicket(){
        VerTicketDialog verTicketDialog = new VerTicketDialog();
        verTicketDialog.show(mActivity.getSupportFragmentManager(), "Ver ticket");
    }

}
