package com.example.jean2.creta;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.jean2.creta.Clases.JugadaClass;
import com.example.jean2.creta.Clases.LoteriaClass;
import com.example.jean2.creta.Clases.SorteosClass;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    private BarChart barChart;
    TextView txtFecha;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListenerFinal;
    TextView_Icon txtBack;
    public static ProgressBar progressBar;
    TableLayout tableTotalesPorLoteria;
    Spinner spinnerLoterias;
    List<LoteriaClass> loterias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        txtBack = (TextView_Icon) findViewById(R.id.txtBack);
        txtFecha = (TextView) findViewById(R.id.txtFecha);
        barChart = findViewById(R.id.chart1);
        tableTotalesPorLoteria = (TableLayout)findViewById(R.id.tableTotalesPorLoteria);
        spinnerLoterias = (Spinner) findViewById(R.id.spinnerLoterias);
        spinnerLoterias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the spinner selected item text
                //String selectedItemText = (String) adapterView.getItemAtPosition(i);
                // Display the selected item into the TextView
                //mTextView.setText("Selected : " + selectedItemText);
                if(loterias.size() ==0)
                    return;

                LoteriaClass loteria = loterias.get((int)spinnerLoterias.getSelectedItemId());
                for(SorteosClass s : loteria.getSorteos()){
                    for(JugadaClass j: s.getJugadas()){
                        Log.e("channel", j.getJugada());
                    }
                }


            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(DashboardActivity.this,"No selection",Toast.LENGTH_LONG).show();
            }
        });

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Calendar calendarIncial = Calendar.getInstance();
        int yearActual = calendarIncial.get(Calendar.YEAR);
        int monthActual = calendarIncial.get(Calendar.MONTH) + 1;
        int dayActual = calendarIncial.get(Calendar.DAY_OF_MONTH);

        txtFecha.setText(String.valueOf(yearActual) + "-" + String.valueOf(monthActual) + "-" + String.valueOf(dayActual));
        getVentas();

        txtFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        DashboardActivity.this,
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
                getVentas();
            }
        };

        startChart();
//        getVentas();
//        dashboardHttp dashboardHttp = new dashboardHttp();
//        dashboardHttp.execute();
    }

    void startChart()
    {
        //XAXIS
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setTextSize(13f);
        xAxis.setLabelCount(5);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(5f);
        String[] months = new String[] {"DIA 1", "DIA 2", "DIA 31", "DIA 4", "DIA 7"};
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularityEnabled(true);

        //YAXIS
        YAxis left = barChart.getAxisLeft();
        //left.setDrawLabels(false);
        left.setSpaceTop(25f);
        left.setSpaceBottom(25f);
        //left.setDrawAxisLine(false);
        //left.setDrawGridLines(false);
        left.setDrawZeroLine(true); // draw a zero line
        left.setZeroLineColor(Color.GRAY);
        left.setZeroLineWidth(4f);

        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        //ENTRIES
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<BarEntry> barEntries1 = new ArrayList<>();

        barEntries.add(new BarEntry(1,0));
        barEntries.add(new BarEntry(2,0));
        barEntries.add(new BarEntry(3,0));
        barEntries.add(new BarEntry(4,0));
        barEntries.add(new BarEntry(5,0));
        barEntries.add(new BarEntry(6,0));
        barEntries.add(new BarEntry(7,0));

        barEntries1.add(new BarEntry(1,0));
        barEntries1.add(new BarEntry(2,0));
        barEntries1.add(new BarEntry(3,0));
        barEntries1.add(new BarEntry(4,0));
        barEntries1.add(new BarEntry(5,0));
        barEntries1.add(new BarEntry(6,0));
        barEntries1.add(new BarEntry(7,0));

        BarDataSet barDataSet = new BarDataSet(barEntries,"DATA SET 1");
//        barDataSet.setColor(Color.parseColor("#F44336"));
        barDataSet.setColors(new int[] {Color.RED, Color.BLUE});
        BarDataSet barDataSet1 = new BarDataSet(barEntries1,"DATA SET 2");
        barDataSet1.setColors(Color.parseColor("#9C27B0"));


        BarData data = new BarData(barDataSet,barDataSet1);
        barChart.setData(data);

        //FUNCIONANDO BIEN PERO CON MAYOR ESPACIO ENTRE LAS BARRAS
        //float barSpace = 0.2f;
        //float groupSpace = 0.3f;
        //int groupCount = 5;

        //FUNCIONANDO BIEN CON MENOR ESPACIO ENTRE LAS BARRAS
        float barSpace = 0.18f;
        float groupSpace = 0.03f;
        int groupCount = 5;



        //IMPORTANT *****
        data.setBarWidth(0.30f);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        barChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
        //***** IMPORTANT
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    private void getVentas(){
        String url = Utilidades.URL +"/api/dashboard?fecha=" + txtFecha.getText() + "&idUsuario=" + String.valueOf(Utilidades.getIdUsuario(DashboardActivity.this));
        progressBar.setVisibility(View.VISIBLE);


        JSONObject dato = new JSONObject();
        JSONObject datosObj = new JSONObject();


        String jsonString = datosObj.toString();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, datosObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        try {

//                            Log.e("dashboard", response.getJSONArray("loterias").toString());

                            updateChart(response.getJSONArray("ventasGrafica"));
                            updateTableTotalesPorLoteria(response.getJSONArray("loterias"), response.getInt("totalVentasLoterias"), response.getInt("totalPremiosLoterias"));

                            JSONArray jsonArrayLoterias = response.getJSONArray("loteriasJugadasDashboard");
                            Gson gson = new Gson();
                                Type listType = new TypeToken<List<LoteriaClass>>(){}.getType();
                                List<LoteriaClass> posts = gson.fromJson(jsonArrayLoterias.toString(), listType);

                                Log.e("loterias", jsonArrayLoterias.toString());
                                loterias = posts;
//                            for(int i=0; i < jsonArrayLoterias.length(); i++){
//                                JSONObject object = jsonArrayLoterias.getJSONObject(i);
//                                JSONArray jsonArraySorteos = object.getJSONArray("sorteos");
//
//                                LoteriaClass l = new LoteriaClass();
//                                List<SorteosClass> sorteos = new ArrayList<>();
//
//
//                                l.setDescripcion(object.getString("descripcion"));
//
//                                Gson gson = new Gson();
//                                Type listType = new TypeToken<List<SorteosClass>>(){}.getType();
//                                List<SorteosClass> posts = gson.fromJson(jsonArraySorteos.toString(), listType);
//                                l.setSorteos(posts);
//                                Log.e("sorteos", posts.toString());
//
////                                for(int c=0; c < jsonArraySorteos.length(); c++){
////                                    SorteosClass s = new SorteosClass();
////                                    List<JugadaClass> jugadas = new ArrayList<>();
////                                    JSONObject jsonObjectSorteo = jsonArraySorteos.getJSONObject(c);
//////                                    JSONArray jsonArrayJugadas = jsonObjectSorteo.getJSONArray("jugadas");
////                                    JSONArray jsonArrayJugadas;
////
////
////
////
//////                                    for(int c2=0; c2 < jsonArrayJugadas.length(); c2++){
//////                                        JugadaClass j = new JugadaClass();
//////                                        JSONObject jsonObjectJugada = jsonArrayJugadas.getJSONObject(c2);
//////                                        j.setJugada(jsonObjectJugada.getString("jugada"));
//////                                        j.setDescripcion(jsonObjectJugada.getString("descripcion"));
//////
//////                                        jugadas.add(j);
//////                                    }
////
////
//////                                    jsonObjectSorteo.getJSONArray("jugadas");
//////                                    Log.d("jugadas", jsonObjectSorteo.getJSONArray("jugadas").toString());
////
//////                                    s.setDescripcion(jsonObjectSorteo.getString("descripcion"));
//////                                    s.setJugadas(jugadas);
//////                                    sorteos.add(s);
////                                }
//
////                                for(SorteosClass s : l.getSorteos()){
////                                    Log.e("sorteos", s.getDescripcion());
////                                }
//
////                                l.setSorteos(sorteos);
//                                loterias.add(l);
//                            }
                            fillSpinner();
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
                    Toast.makeText(DashboardActivity.this, "Verifique coneccion e intente de nuevo", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof ServerError){
                    Toast.makeText(DashboardActivity.this, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof TimeoutError){
                    Toast.makeText(DashboardActivity.this, "Conexion lenta, verifique conexion e intente de nuevo", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(DashboardActivity.this).addToRequestQueue(request, 20000);
    }

    public  class dashboardHttp extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;
        JSONObject data;

        public dashboardHttp() {
        }

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();

            try {
                //URL url = new URL("https://api.github.com/users/dmnugent80/repos");
                String urlString = Utilidades.URL +"/api/dashboard?fecha=" + txtFecha.getText() + "&idUsuario=" + String.valueOf(Utilidades.getIdUsuario(DashboardActivity.this));
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("GET");




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

                //SE LLENAN LAS LISTAS CON LOS JSONSTRING
                Log.i("guardarHttp", result.toString());

            } catch (Exception e) {
                result.append("Error");
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();

                urlConnection = null;
                return result.toString();
            }


            //return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            if(!result.equals("Error")){
//                idVenta = null;
//                if(llenarVentasLoteriasTickets(result) == false)
//                    return;
//
//                if(errores == 1){
//                    Toast.makeText(mContext, "Error: " +mensaje, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                limpiar();
//                fillSpinner();
//                setDescuento();
//
//
//
//                if(ckbPrint.isChecked()){
//                    //Utilidades.imprimir(mContext,response, 1);
//                    Utilidades.imprimir(mContext, venta, 1);
//                }
//                else if(ckbSms.isChecked()){
//                    venta.setImg(imgHtmlTmp);
//                    PrincipalFragment.compartirTicketHttp c = new PrincipalFragment.compartirTicketHttp(venta, true);
//                    c.execute();
//                }
//                else if(ckbWhatsapp.isChecked()){
//                    venta.setImg(imgHtmlTmp);
//                    PrincipalFragment.compartirTicketHttp c = new PrincipalFragment.compartirTicketHttp(venta, false);
//                    c.execute();
//                }


            }else{
//                Toast.makeText(mContext, "Error del servidor", Toast.LENGTH_SHORT).show();
//                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which){
//                            case DialogInterface.BUTTON_POSITIVE:
//                                //Yes button clicked
//                                guardar();
//                                break;
//
//                            case DialogInterface.BUTTON_NEGATIVE:
//                                //No button clicked
//                                break;
//                        }
//                    }
//                };
//
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                builder.setMessage("Ha ocurrido un error de conexion, desea realizar la venta otra vez?").setPositiveButton("Si", dialogClickListener)
//                        .setNegativeButton("No", dialogClickListener).show();
            }
        }
    }

    public void fillSpinner(){
        /********* Prepare value for spinner *************/
        // jsonArrayVentas = jsonArrayVentas2;
        String[] loteriasSpinner = new String[loterias.size()];
        int contador = 0;
        for (LoteriaClass loteria : loterias)
        {
            loteriasSpinner[contador] = loteria.getDescripcion();
            contador++;
        }


        /********* Set value to spinner *************/
        if(loteriasSpinner == null)
            return;
        try{
            ArrayAdapter<String> adapter =new ArrayAdapter<String>(DashboardActivity.this,android.R.layout.simple_spinner_item, loteriasSpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLoterias.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }

//        seleccionarBancaPertenecienteAUsuario();
    }

    public void updateChart(JSONArray datos)
    {
        //ENTRIES
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<BarEntry> barEntries1 = new ArrayList<>();
//        int[] coloresEntriesTotales = new int[datos.length()];

        //SI EL ARREGLO DATOS QUE CONTIENE LA VENTAS PARA LA GRAFICA ES <= 5 ENTONCES EL CONTADOR i = 0
        //PORQUE LA GRAFICA SOLO PUEDE TENER 5 GRUPOS O MENOS, PERO SI EL ARREGLO DATOS > 5 ENTONCES
        //DEBO ASEGURARME QUE EL CONTADOR SOLO RECORRA 5 CICLOS, ASI QUE LE ASIGNO UN NUMERO INDICE
        //QUE SOLO PERMITA RECORRER LAS ULTIMAS 5 VENTAS
        int i = 0;
        if(datos.length() <= 5){
            i = 0;
        }else{
            while(datos.length() - i != 5){
                i++;
            }
        }

        //AQUI TOMAMOS LAS ULTIMAS 5 VENTAS Y LA PASAMOS A OTRO ARREGLO
        JSONArray datosAMostrar = new JSONArray();
        for( ; i < datos.length(); i++) {
            try {
                JSONObject dato = datos.getJSONObject(i);
                datosAMostrar.put(dato);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.e("dashboardEntry", datosAMostrar.toString());
        Log.e("dashboardEntry1", datos.toString());
        Log.e("dashboardEntry2", String.valueOf(datos.length()));
        Log.e("dashboardEntry3", String.valueOf(i));

        int[] coloresEntriesNetos = new int[datosAMostrar.length()];
        String[] dias = new String[datosAMostrar.length()];

        //AQUI RECORREMOS EL NUEVO ARREGLO CON LAS ULTIMAS 5 VENTAS
        for( i = 0; i < datosAMostrar.length(); i++) {
            try {
                JSONObject dato = datosAMostrar.getJSONObject(i);
                float total = Float.parseFloat(dato.getString("total"));
                float neto = Float.parseFloat(dato.getString("neto"));
                barEntries.add(new BarEntry(i,total));
                barEntries1.add(new BarEntry(i,neto));

                if(neto >= 0){
                    coloresEntriesNetos[i] = Color.parseColor("#75b281");
                }else{
                    coloresEntriesNetos[i] = Color.parseColor("#dc2365");
                }
                dias[i] = dato.getString("dia");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        BarDataSet set1, set2;
        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {

            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setColor(Color.parseColor("#95999e"));
            set2 = (BarDataSet) barChart.getData().getDataSetByIndex(1);
//            set3 = (BarDataSet) chart.getData().getDataSetByIndex(2);
//            set4 = (BarDataSet) chart.getData().getDataSetByIndex(3);
            set1.setValues(barEntries);
            set2.setColors(coloresEntriesNetos);
            set2.setValues(barEntries1);
//            set3.setValues(values3);
//            set4.setValues(values4);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        }

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setTextSize(13f);
        xAxis.setLabelCount(5);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(5f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dias));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularityEnabled(true);

        float barSpace = 0.18f;
        float groupSpace = 0.03f;
        int groupCount = 5;



        //IMPORTANT *****
//        data.setBarWidth(0.30f);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        barChart.groupBars(0, groupSpace, barSpace); // perform the "explicit" grouping
        barChart.invalidate();

        //***** IMPORTANT
    }


    public  void updateTableTotalesPorLoteria(JSONArray datos, int totalVentasLoterias, int totalPremiosLoterias){



        if(datos == null){
            return ;
        }
        tableTotalesPorLoteria.removeAllViews();
        int idRow = 0;

        if(datos.length() == 0){
            tableTotalesPorLoteria.removeAllViews();
            Toast.makeText(DashboardActivity.this, "No hay datos", Toast.LENGTH_SHORT).show();
            return;
        }

        LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int color_normal_gris = 1;

        for(int i=0; i < datos.length(); i++){
            try {

                JSONObject dato = datos.getJSONObject(i);

                //DESCRIPCION
                String ventas = (dato.getString("ventas").equals("null")) ? "0" : dato.getString("ventas");
                String premios = (dato.getString("premios").equals("null")) ? "0" : dato.getString("premios");
                String descripcion = (dato.getString("descripcion").equals("null")) ? "" : dato.getString("descripcion");

                if(i == 0){
                    /* add views to the row */
                    TableRow tableRow = createRow(tableRowParams, idRow, color_normal_gris, new TextView[]{createTv("Loteria", 1, DashboardActivity.this, true), createTv("Venta total", 1, DashboardActivity.this, true), createTv("Premios", 1, DashboardActivity.this, true)});
                    tableTotalesPorLoteria.addView(tableRow);
                    idRow ++;
                }

                /* add views to the row */
                if((i % 2) != 0){
                    color_normal_gris = 2;
                }else{
                    color_normal_gris = 1;
                }

                TableRow tableRow = createRow(tableRowParams, idRow, color_normal_gris, new TextView[]{createTv(descripcion, 3, DashboardActivity.this, true), createTv(ventas, 3, DashboardActivity.this, true), createTv(premios, 3, DashboardActivity.this, true)});
                tableTotalesPorLoteria.addView(tableRow);

                idRow++;
                if(i + 1 < datos.length() == false){
                    TableRow tableRow1 = createRow(tableRowParams, idRow, 1, new TextView[]{createTv("Total", 2, DashboardActivity.this, true), createTv(String.valueOf(totalVentasLoterias), 2, DashboardActivity.this, true), createTv(String.valueOf(totalPremiosLoterias), 2, DashboardActivity.this, true)});
                    tableTotalesPorLoteria.addView(tableRow1);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
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

    private TableRow createRow(LinearLayout.LayoutParams tableRowParams, int id, int color_normal_gris, TextView[] textViews)
    {

        /* create a table row */
        TableRow tableRow = new TableRow(DashboardActivity.this);
        tableRow.setLayoutParams(tableRowParams);

        //GRIS
        if(color_normal_gris == 2){
            tableRow.setBackgroundColor(Color.parseColor("#eae9e9"));
        }

        for(int i=0; i < textViews.length ; i++){
            tableRow.addView(textViews[i]);
        }

        return tableRow;
    }

}
