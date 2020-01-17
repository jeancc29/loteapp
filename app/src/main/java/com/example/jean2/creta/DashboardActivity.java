package com.example.jean2.creta;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity {
    private BarChart barChart;
    TextView txtFecha;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListenerFinal;
    TextView_Icon txtBack;
    public static ProgressBar progressBar;
    TableLayout tableTotalesPorLoteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        txtBack = (TextView_Icon) findViewById(R.id.txtBack);
        txtFecha = (TextView) findViewById(R.id.txtFecha);
        barChart = findViewById(R.id.chart1);
        tableTotalesPorLoteria = (TableLayout)findViewById(R.id.tableTotalesPorLoteria);

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
        getVentas();
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
        for(int i=0; i < datos.length(); i++){
            try {

                JSONObject dato = datos.getJSONObject(i);


                LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                /* create a table row */
                TableRow tableRow = new TableRow(DashboardActivity.this);
                tableRow.setLayoutParams(tableRowParams);

                if((i % 2) != 0){
                    tableRow.setBackgroundColor(Color.parseColor("#eae9e9"));
                }

                tableRow.setId(idRow);




                if(i == 0){
                    /* add views to the row */
                    TableRow tableRow1 = new TableRow(DashboardActivity.this);
                    tableRow1.setId(idRow);
                    tableRow1.setLayoutParams(tableRowParams);
                    idRow ++;
                    tableRow1.addView(createTv("Loteria", 1, DashboardActivity.this, true));
                    tableRow1.addView(createTv("Venta total", 1, DashboardActivity.this, true));
                    tableRow1.addView(createTv("Premios", 1, DashboardActivity.this, true));
                    tableTotalesPorLoteria.addView(tableRow1);
                }
                /* add views to the row */
                tableRow.setId(idRow);
                String ventas = (dato.getString("ventas").equals("null")) ? "0" : dato.getString("ventas");
                String premios = (dato.getString("premios").equals("null")) ? "0" : dato.getString("premios");
                tableRow.addView(createTv(dato.getString("descripcion"), 3, DashboardActivity.this, true));
                tableRow.addView(createTv(ventas, 3, DashboardActivity.this, true));
                tableRow.addView(createTv(premios, 3, DashboardActivity.this, true));




                tableTotalesPorLoteria.addView(tableRow);
                idRow++;

                if(i + 1 < datos.length() == false){
                    TableRow tableRow1 = new TableRow(DashboardActivity.this);
                    tableRow1.setId(idRow);
                    tableRow1.setLayoutParams(tableRowParams);
                    tableRow1.addView(createTv("Total", 2, DashboardActivity.this, true));
                    tableRow1.addView(createTv(String.valueOf(totalVentasLoterias), 2, DashboardActivity.this, true));
                    tableRow1.addView(createTv(String.valueOf(totalPremiosLoterias), 2, DashboardActivity.this, true));
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

}
