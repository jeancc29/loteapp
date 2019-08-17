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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;

public class PendientesDePago extends AppCompatActivity {

    private Toolbar toolbar;
    public static ProgressBar progressBarToolbar;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    static PendientesDePago mActivity;


    TextView txtFecha;
    Button btnBuscar;
    TableLayout tableLayout;
    Spinner spinnerBancas;
    CheckBox ckbTodasLasFechas;

    Context mContext;
    int idBanca = 0;
    private JSONArray tickets = new JSONArray();
    private JSONArray bancas = new JSONArray();
    private String[] listBancas;
    private String[] listIdBancas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendientes_de_pago);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mActivity = this;
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Pendientes de pago");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBarToolbar = (ProgressBar)findViewById(R.id.toolbar_progress_bar);

        mContext = this;
        idBanca = PrincipalFragment.idBanca;

        btnBuscar= (Button) findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPendientesDePago();
            }
        });
        tableLayout= (TableLayout) findViewById(R.id.tablePendientesPago);
        spinnerBancas = (Spinner)findViewById(R.id.spinnerBancas);
        if(Utilidades.getAdministrador(mContext) == false){
            spinnerBancas.setVisibility(View.GONE);
        }

        spinnerBancas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int itemIndex = Arrays.asList(listBancas).indexOf(String.valueOf(spinnerBancas.getSelectedItem().toString()));
                idBanca = Integer.parseInt(listIdBancas[itemIndex]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        txtFecha = (TextView) findViewById(R.id.txtFecha);



        Calendar calendarIncial = Calendar.getInstance();
        final int yearActual = calendarIncial.get(Calendar.YEAR);
        final int monthActual = calendarIncial.get(Calendar.MONTH) + 1;
        final int dayActual = calendarIncial.get(Calendar.DAY_OF_MONTH);


        txtFecha.setText(String.valueOf(yearActual) + "-" + String.valueOf(monthActual) + "-" + String.valueOf(dayActual));
        ckbTodasLasFechas= (CheckBox) findViewById(R.id.ckbTodasLasFechas);
        ckbTodasLasFechas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    txtFecha.setText("Todas las fechas");
                    txtFecha.setEnabled(false);
                }else{
                    txtFecha.setText(String.valueOf(yearActual) + "-" + String.valueOf(monthActual) + "-" + String.valueOf(dayActual));
                    txtFecha.setEnabled(true);
                }
            }
        });

        getPendientesDePagoIndex();

        txtFecha.setOnClickListener(new View.OnClickListener() {
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
                txtFecha.setText(date);

            }
        };
    }



    private void llenarSpinnerBancas(JSONArray bancasJsonArray){
        try{
            bancas = bancasJsonArray;
            listBancas = new String[bancas.length() + 1]; //Al tamano le anadimos una posicion mas para agregar el item "Todas"
            listIdBancas = new String[bancas.length() + 1]; //Al tamano le anadimos una posicion mas para agregar el item "Todas"
            listBancas[0] = "Todas las bancas";
            listIdBancas[0] = "0";
            for(int i=0; i < bancas.length(); i++){
                JSONObject banca = bancas.getJSONObject(i);
                listBancas[i + 1] = banca.getString("descripcion");
                listIdBancas[i + 1] = banca.getString("id");
            }


            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, listBancas);
            spinnerBancas.setAdapter(spinnerArrayAdapter);



            if(Utilidades.getAdministrador(mContext)){
                spinnerBancas.setSelection(0);
            }else{
                int itemIndex = Arrays.asList(listIdBancas).indexOf(String.valueOf(idBanca));
                spinnerBancas.setSelection(itemIndex);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void getPendientesDePagoIndex(){
        String url = "http://loterias.ml/api/reportes/ticketsPendientesDePagoIndex";
        progressBarToolbar.setVisibility(View.VISIBLE);


        JSONObject dato = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            dato.put("idUsuario", Utilidades.getIdUsuario(mContext));
            dato.put("idBanca", idBanca);
            dato.put("fecha", txtFecha.getText().toString());
            datosObj.put("datos", dato);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.d("PendientePago", datosObj.toString());
        String jsonString = datosObj.toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBarToolbar.setVisibility(View.GONE);
                        try {
                            JSONArray jsonArray = response.getJSONArray("ticketsPendientesDePago");
                            JSONArray jsonArrayBancas = response.getJSONArray("bancas");

                            llenarSpinnerBancas(jsonArrayBancas);
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

            }
        });

//        mQueue.add(request);
        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }


    private void getPendientesDePago(){
        String url = "http://loterias.ml/api/reportes/ticketsPendientesDePago";
        progressBarToolbar.setVisibility(View.VISIBLE);


        JSONObject dato = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            dato.put("idUsuario", Utilidades.getIdUsuario(mContext));
            dato.put("idBanca", idBanca);
            dato.put("fecha", txtFecha.getText().toString());
            datosObj.put("datos", dato);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.d("PendientePago", datosObj.toString());
        String jsonString = datosObj.toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBarToolbar.setVisibility(View.GONE);
                        try {
                            JSONArray jsonArray = response.getJSONArray("ticketsPendientesDePago");
                            JSONArray jsonArrayBancas = response.getJSONArray("bancas");


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

            }
        });

//        mQueue.add(request);
        MySingleton.getInstance(mContext).addToRequestQueue(request);
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




        if(datos == null){
            return ;
        }

        tickets = datos;
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
                    tableRow1.addView(createTv("Banca", true, mContext, true));
                    tableRow1.addView(createTv("Ticket", true, mContext, true));
                    tableRow1.addView(createTv("Monto a pagar", true, mContext, true));
                    tableLayout.addView(tableRow1);
                }
                /* add views to the row */
                tableRow.setId(idRow);

                TextView txtTicket = createTv(Utilidades.toSecuencia(dato.getString("idTicket"), dato.getString("codigo")), false, mContext, true);
                txtTicket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int id = ((View)view.getParent()).getId();


//                        TableRow r = (TableRow)((Activity) mContext).findViewById(id);
//                        TableLayout t = (TableLayout)((Activity) mContext).findViewById(R.id.tableJugadas);
//                        t.removeView(r);
//                        Log.d("Pariente:" , String.valueOf(id));

                        //Buscar ticket retorna un json object
                        MonitoreoActivity.selectedTicket = buscar(id - 1);
                        VerTicket();
                    }
                });


                //tableRow.addView(createTv(toSecuencia(dato.getString("idTicket"), dato.getString("codigo")), false, mContext, true));
                tableRow.addView(createTv(dato.getString("fecha"), false, mContext, true));
                String descripcion = dato.getJSONObject("banca").getString("descripcion");
                tableRow.addView(createTv(descripcion, false, mContext, true));
                tableRow.addView(txtTicket);
                tableRow.addView(createTv(dato.getString("montoAPagar"), false, mContext, true));





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

    public static void VerTicket(){
        VerTicketDialog verTicketDialog = new VerTicketDialog();
        verTicketDialog.show(mActivity.getSupportFragmentManager(), "Ver ticket");
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
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            tv.setTextColor(Color.BLACK);
        }

        if(center)
            tv.setGravity(Gravity.CENTER);

        return tv;
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
}
