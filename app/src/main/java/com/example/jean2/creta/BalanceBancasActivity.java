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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.example.jean2.creta.Clases.LoteriaClass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BalanceBancasActivity extends AppCompatActivity {
    private Toolbar toolbar;
    Context mContext;
    TableLayout table;
    public static ProgressBar progressBar;
    TextView txtFechaDesde;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    Button btnBuscar;
    EditText txtBancaDescripcion;
    Spinner spinnerBanca;

    List<BancaClass> bancas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_bancas);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        progressBar = (ProgressBar)findViewById(R.id.toolbar_progress_bar);
        mContext = this;
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Balance bancas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        table = (TableLayout)findViewById(R.id.table);
        btnBuscar= (Button) findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBancas();
            }
        });

        spinnerBanca = (Spinner)findViewById(R.id.spinnerBancas);
        spinnerBanca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the spinner selected item text
                //String selectedItemText = (String) adapterView.getItemAtPosition(i);
                // Display the selected item into the TextView
                //mTextView.setText("Selected : " + selectedItemText);
                buscarTicket(spinnerBanca.getSelectedItem().toString());
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(mContext,"No selection",Toast.LENGTH_LONG).show();
            }
        });


        txtFechaDesde = (TextView) findViewById(R.id.txtFecha);
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

        getBancas();
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


    private void getBancas(){
        String url = Utilidades.URL +"/api/balance/bancas";
        progressBar.setVisibility(View.VISIBLE);


        JSONObject dato = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            dato.put("idUsuario", Utilidades.getIdUsuario(mContext));
            dato.put("fechaHasta", txtFechaDesde.getText());

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
                        progressBar.setVisibility(View.GONE);
                        try {


                            JSONArray jsonArrayBancas = response.getJSONArray("bancas");
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<BancaClass>>(){}.getType();
                            List<BancaClass> bancasGson = gson.fromJson(jsonArrayBancas.toString(), listType);

                            Log.e("bancas", jsonArrayBancas.toString());
                            bancas = bancasGson;
                            fillSpinner();
                            updatetable(bancas);
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



    public  void updatetable(List<BancaClass> bancas){

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


        int i =0;
        boolean primerCiclo = true;
        for(BancaClass b : bancas){

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
                tableRow1.addView(createTv("Usuario", 1, mContext, true));
                tableRow1.addView(createTv("Dueño", 1, mContext, true));
                tableRow1.addView(createTv("Balance", 1, mContext, true));
                tableRow1.addView(createTv("Prestamo", 1, mContext, true));
                table.addView(tableRow1);
                primerCiclo = false;
            }




            /* add views to the row */
            tableRow.setId(idRow);
            tableRow.addView(createTv(b.getDescripcion(), 3, mContext, true));
            tableRow.addView(createTv(b.getUsuario(), 3, mContext, true));
            tableRow.addView(createTv(b.getDueno(), 3, mContext, true));
            tableRow.addView(asignarColor(createTv(String.valueOf(b.getBalance()), 3, mContext, true), b.getBalance()));
            tableRow.addView(createTv(String.valueOf(b.getPrestamo()), 3, mContext, true));



            table.addView(tableRow);
            idRow++;
            i++;

//            if(i < bancas.size() == false){
//                TableRow tableRow1 = new TableRow(mContext);
//                tableRow1.setId(idRow);
//                tableRow1.setLayoutParams(tableRowParams);
//                idRow ++;
//                try{
//                    JSONObject object = calcularTotal(bancasTemporal);
//                    tableRow1.addView(createTv("Totales", 2, mContext, true));
//                    tableRow1.addView(createTv(object.getString("ventas"), 2, mContext, true));
//                    tableRow1.addView(createTv(object.getString("ventas"), 2, mContext, true));
//
//                    tableRow1.addView(asignarColor(createTv(object.getString("balances"), 2, mContext, true), object.getDouble("balances")));
//                    tableRow1.addView(asignarColor(createTv(object.getString("balancesMasVentas"), 2, mContext, true), object.getDouble("balancesMasVentas")));
//                    table.addView(tableRow1);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                TableRow tableRow2 = new TableRow(mContext);
//                tableRow2.setId(idRow);
//                tableRow2.setLayoutParams(tableRowParams);
//                idRow ++;
//                tableRow2.addView(createTv("", 2, mContext, true));
//                tableRow2.addView(createTv("Venta", 2, mContext, true));
//                tableRow2.addView(createTv("Comis.", 2, mContext, true));
//                tableRow2.addView(createTv("Desc.", 2, mContext, true));
//                tableRow2.addView(createTv("Premios", 2, mContext, true));
//                tableRow2.addView(createTv("Neto", 2, mContext, true));
//                tableRow2.addView(createTv("Balance", 2, mContext, true));
//                tableRow2.addView(createTv("Balance mas ventas", 2, mContext, true));
//                table.addView(tableRow2);
//            }


        }

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

    private TextView asignarColor(TextView txt, double valor){
        if(valor < 0){
            //txt.setTextColor(ContextCompat.getColor(mContext, R.color.bgRed));
            txt.setBackgroundColor(Color.parseColor("#ffcccc"));
            txt.setTextColor(Color.parseColor("#e22c2c"));
        }else{
            txt.setBackgroundColor(Color.parseColor("#bfdde0"));
            txt.setTextColor(Color.parseColor("#095861"));
        }

        return txt;
    }

    public void buscarTicket(String cadenaABuscar)
    {
        try{
            if(bancas == null){
                return;
            }

            List<BancaClass> bancasTmp = new ArrayList<>();

            if(bancas.size() > 0) {
                if(cadenaABuscar.equals("TODAS LAS BANCAS") == true){
                    bancasTmp = bancas;
                }else{
                    for(BancaClass b : bancas){
                        if(b.getDescripcion().equals(cadenaABuscar)){
                            bancasTmp.add(b);
                        }
                    }
                }

            }

            updatetable(bancasTmp);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void fillSpinner(){
        /********* Prepare value for spinner *************/
        // jsonArrayVentas = jsonArrayVentas2;
        String[] bancasSpinner = new String[bancas.size() + 1];
        int contador = 0;
        bancasSpinner[contador] = "TODAS LAS BANCAS";
        contador++;
        for (BancaClass banca : bancas)
        {
            bancasSpinner[contador] = banca.getDescripcion();
            contador++;
        }


        /********* Set value to spinner *************/
        if(bancasSpinner == null)
            return;
        try{
            ArrayAdapter<String> adapter =new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_item, bancasSpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBanca.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }

        seleccionarPrimerItem();
    }


    public void seleccionarPrimerItem(){
        if(bancas.size() > 0)
            spinnerBanca.setSelection(0);
    }
}
