package com.example.jean2.creta;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.example.jean2.creta.Clases.SorteosClass;
import com.example.jean2.creta.Clases.VentasClass;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RegistrarPremios extends AppCompatActivity {
    private Toolbar toolbar;

    List<LoteriaClass> loterias = new ArrayList<>();
    Spinner spinnerLoteria;
    ProgressBar progressBar;
    TextInputEditText txtPrimera;
    TextInputEditText txtSegunda;
    TextInputEditText txtTercera;
    TextInputEditText txtPick3;
    TextInputEditText txtPick4;
    Button btnGuardar;
    Button btnBorrar;
    static String mensaje = null;
    static Context mContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_premios);
        toolbar = (Toolbar) findViewById(R.id.toolBarMonitoreo);
        toolbar.setTitle("Registrar premios");
        setSupportActionBar(toolbar);
        mContext = this;

        progressBar = (ProgressBar)findViewById(R.id.progressBarPrincipal);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressBar = (ProgressBar)findViewById(R.id.progressBarPrincipal);
        btnBorrar = (Button) findViewById(R.id.btnBorrar);
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                borrar();
            }
        });

        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardar();
            }
        });

        txtPrimera = (TextInputEditText)findViewById(R.id.txtPrimera);
        txtPrimera.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int s, int b, int c) {
                Log.i("Key:", cs.toString());
                if(cs.toString().length() == 2 && txtPrimera.hasFocus()){
                    txtSegunda.setSelectAllOnFocus(true);
                    txtSegunda.requestFocus();
                }
            }
            public void afterTextChanged(Editable editable) { }
            public void beforeTextChanged(CharSequence cs, int i, int j, int
                    k) { }
        });
        txtSegunda = (TextInputEditText)findViewById(R.id.txtSegunda);
        txtSegunda.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int s, int b, int c) {

                if(cs.toString().length() == 2 && txtSegunda.hasFocus()){
                    txtTercera.setSelectAllOnFocus(true);
                    txtTercera.requestFocus();
                }
            }
            public void afterTextChanged(Editable editable) { }
            public void beforeTextChanged(CharSequence cs, int i, int j, int
                    k) { }
        });
        txtTercera = (TextInputEditText)findViewById(R.id.txtTercera);
        txtTercera.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int s, int b, int c) {

                if(cs.toString().length() == 2 && txtTercera.hasFocus()){
                    txtPick3.setSelectAllOnFocus(true);
                    txtPick3.requestFocus();
                }
            }
            public void afterTextChanged(Editable editable) { }
            public void beforeTextChanged(CharSequence cs, int i, int j, int
                    k) { }
        });
        txtPick3 = (TextInputEditText)findViewById(R.id.txtPick3);
        txtPick3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int s, int b, int c) {

                if(loterias.size() == 0)
                    return;
                LoteriaClass loteria = loterias.get((int)spinnerLoteria.getSelectedItemId());

                if(existeSorteo(loteria.sorteos, "Pick 3 Box") == false && existeSorteo(loteria.sorteos, "Pick 3 Straight") == false)
                    return;
                if(cs.toString().length() > 1){
//                    if(cs.toString().length() > 2){
                        String cadena = cs.toString().substring(1, cs.toString().length());
                        txtPrimera.setText(cadena);
//                    }
                    if(cs.toString().length() == 3){
                        txtPick4.setSelectAllOnFocus(true);
                        txtPick4.requestFocus();
                    }

                }else{
                    txtPrimera.setText("");
                }
            }
            public void afterTextChanged(Editable editable) { }
            public void beforeTextChanged(CharSequence cs, int i, int j, int
                    k) { }
        });
        txtPick4 = (TextInputEditText)findViewById(R.id.txtPick4);
        txtPick4.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence cs, int s, int b, int c) {
                if(loterias.size() == 0)
                    return;
                LoteriaClass loteria = loterias.get((int)spinnerLoteria.getSelectedItemId());
                if(existeSorteo(loteria.sorteos, "Pick 4 Box") == false && existeSorteo(loteria.sorteos, "Pick 4 Straight") == false)
                    return;

                if(cs.toString().length() > 0){
                    if(cs.toString().length() < 3){
                        String cadena = cs.toString().substring(0, cs.toString().length());
                        txtSegunda.setText(cadena);
                    }else{
                        String cadena = cs.toString().substring(2, cs.toString().length());
                        txtTercera.setText(cadena);
                    }

                }else{
                    txtTercera.setText("");
                    txtSegunda.setText("");
                }
            }
            public void afterTextChanged(Editable editable) { }
            public void beforeTextChanged(CharSequence cs, int i, int j, int
                    k) { }
        });
        spinnerLoteria = (Spinner)findViewById(R.id.spinnerLoterias);
        getLoterias(null);
        spinnerLoteria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Get the spinner selected item text
                //String selectedItemText = (String) adapterView.getItemAtPosition(i);
                // Display the selected item into the TextView
                //mTextView.setText("Selected : " + selectedItemText);
                if(loterias.size() ==0)
                    return;

                LoteriaClass loteria = loterias.get((int)spinnerLoteria.getSelectedItemId());
                if(!loteria.primera.equals("null"))
                    txtPrimera.setText(loteria.primera);
                else{
                    txtPrimera.setText("");
//                    if(existeSorteo(loteria.sorteos, "Directo") == true || existeSorteo(loteria.sorteos, "Pale") == true || existeSorteo(loteria.sorteos, "Tripleta") == true || existeSorteo(loteria.sorteos, "Super pale") == true){
//                        txtPrimera.requestFocus();
//                    }
                }

                if(!loteria.segunda.equals("null"))
                    txtSegunda.setText(loteria.segunda);
                else
                    txtSegunda.setText("");

                if(!loteria.tercera.equals("null"))
                    txtTercera.setText(loteria.tercera);
                else
                    txtTercera.setText("");

                if(!loteria.pick3.equals("null"))
                    txtPick3.setText(loteria.pick3);
                else{
                    txtPick3.setText("");
//                    if(existeSorteo(loteria.sorteos, "Pick 3 Box") == true || existeSorteo(loteria.sorteos, "Pick 3 Straight")){
//                        txtPick3.requestFocus();
//                    }
                }


                if(!loteria.pick4.equals("null"))
                    txtPick4.setText(loteria.pick4);
                else
                    txtPick4.setText("");


                if(existeSorteo(loteria.sorteos, "Directo") == false && existeSorteo(loteria.sorteos, "Pale") == false && existeSorteo(loteria.sorteos, "Tripleta")){
                    txtPrimera.setVisibility(View.GONE);
                    txtSegunda.setVisibility(View.GONE);
                    txtTercera.setVisibility(View.GONE);
                }else{
                    txtPrimera.setVisibility(View.VISIBLE);
                    txtSegunda.setVisibility(View.VISIBLE);
                    txtTercera.setVisibility(View.VISIBLE);
                }

                if(existeSorteo(loteria.sorteos, "Directo") == false && existeSorteo(loteria.sorteos, "Tripleta") == false && existeSorteo(loteria.sorteos, "Super pale") == true){
                    txtPrimera.setVisibility(View.VISIBLE);
                    txtSegunda.setVisibility(View.VISIBLE);
                    txtTercera.setVisibility(View.GONE);
                }

                if(existeSorteo(loteria.sorteos, "Pick 3 Box") == false && existeSorteo(loteria.sorteos, "Pick 3 Straight") == false){
//                    txtPrimera.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
                    txtPick3.setVisibility(View.GONE);
                    txtPrimera.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
                    txtPrimera.setFocusableInTouchMode(true);
                    txtPrimera.setSelectAllOnFocus(true);
                }else{
                    txtPrimera.setInputType(InputType.TYPE_NULL);
                    txtPrimera.setFocusable(false);
                    txtPick3.setVisibility(View.VISIBLE);
                }

                if(existeSorteo(loteria.sorteos, "Pick 4 Box") == false && existeSorteo(loteria.sorteos, "Pick 4 Straight") == false){
                    txtPick4.setVisibility(View.GONE);
                    txtSegunda.setFocusableInTouchMode(true);
                    txtTercera.setFocusableInTouchMode(true);
                    txtSegunda.setSelectAllOnFocus(true);
                    txtTercera.setSelectAllOnFocus(true);
                }else{
                    txtSegunda.setFocusable(false);
                    txtTercera.setFocusable(false);
                    txtPick4.setVisibility(View.VISIBLE);
                }

                Log.e("existeSorteo", String.valueOf(existeSorteo(loteria.sorteos, "Pick 4")));
            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(RegistrarPremios.this,"No selection",Toast.LENGTH_LONG).show();
            }
        });

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

    private void guardar()
    {
        progressBar.setVisibility(View.VISIBLE);
        String url = Utilidades.URL +"/api/premios/guardar";
        LoteriaClass l = loterias.get((int)spinnerLoteria.getSelectedItemId());
        if(validarCampos(l) == false ){
            return;
        }

        JSONObject loteria = new JSONObject();
        JSONObject datos = new JSONObject();
        JSONObject datosObj = new JSONObject();
        JSONArray loteriasArray = new JSONArray();

        try {


            Log.e("prueba", String.valueOf((int)spinnerLoteria.getSelectedItemId()));
            Log.e("prueba2", l.descripcion);
            Log.e("prueba3", l.tercera);
            loteria.put("id", l.getId());
            loteria.put("primera", txtPrimera.getText());
            loteria.put("segunda", txtSegunda.getText());
            loteria.put("tercera", txtTercera.getText());
            loteria.put("pick3", txtPick3.getText());
            loteria.put("pick4", txtPick4.getText());
            loteriasArray.put(loteria);

            datos.put("loterias", loteriasArray);
            datos.put("idUsuario", Utilidades.getIdUsuario(RegistrarPremios.this));
            datos.put("idBanca", Utilidades.getIdBanca(RegistrarPremios.this));
            datos.put("layout", "");
            datos.put("fecha", "");

            datosObj.put("datos", datos);



        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.d("datoa", datosObj.toString());
//
//        guardarHttp g = new guardarHttp(datosObj);
//        g.execute();

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(RegistrarPremios.this, "Se ha guardado correctamente", Toast.LENGTH_SHORT).show();
                        LoteriaClass l = loterias.get((int)spinnerLoteria.getSelectedItemId());
                        String loteria = l.getDescripcion();
                        getLoterias(loteria);
//                        seleccionarLoteria(loteria);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NetworkError){
                    Toast.makeText(RegistrarPremios.this, "Verifique coneccion y recargue la pagina", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof ServerError){
                    Toast.makeText(RegistrarPremios.this, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof TimeoutError){
                    Toast.makeText(RegistrarPremios.this, "Conexion lenta, verifique conexion y recargue de nuevo", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
                error.printStackTrace();
                Log.d("responseerror: ", String.valueOf(error));
            }
        });

        //mQueue.add(request);
        MySingleton.getInstance(RegistrarPremios.this).addToRequestQueue(request, 20000);
    }

    public boolean validarCampos(LoteriaClass l)
    {
        if(existeSorteo(l.sorteos, "Directo") || existeSorteo(l.sorteos, "Pale") || existeSorteo(l.sorteos, "Tripleta")){
            if(txtPrimera.getText().length() != 2){
                Toast.makeText(mContext, "El campo primera debe tener 2 digitos", Toast.LENGTH_LONG).show();
                return false;
            }
            if(txtSegunda.getText().length() != 2){
                Toast.makeText(mContext, "El campo segunda debe tener 2 digitos", Toast.LENGTH_LONG).show();
                return false;
            }
            if(txtTercera.getText().length() != 2){
                Toast.makeText(mContext, "El campo tercera debe tener 2 digitos", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if(existeSorteo(l.sorteos, "Super pale")){
            if(txtPrimera.getText().length() != 2){
                Toast.makeText(mContext, "El campo primera debe tener 2 digitos", Toast.LENGTH_LONG).show();
                return false;
            }
            if(txtSegunda.getText().length() != 2){
                Toast.makeText(mContext, "El campo segunda debe tener 2 digitos", Toast.LENGTH_LONG).show();
                return false;
            }

        }

        if(existeSorteo(l.sorteos, "Pick 3 Box") || existeSorteo(l.sorteos, "Pick 3 Straight")){
            if(txtPick3.getText().length() != 3){
                Toast.makeText(mContext, "El campo pick 3 debe tener 3 digitos", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if(existeSorteo(l.sorteos, "Pick 4 Box") || existeSorteo(l.sorteos, "Pick 4 Straight")){
            if(txtPick4.getText().length() != 4){
                Toast.makeText(mContext, "El campo pick 4 debe tener 4 digitos", Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }


    //Borrar premios
    private void borrar()
    {
        progressBar.setVisibility(View.VISIBLE);

        String url = Utilidades.URL +"/api/premios/erase";

        JSONObject datos = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {


            LoteriaClass l = loterias.get((int)spinnerLoteria.getSelectedItemId());

            datos.put("idLoteria", l.getId());
            datos.put("idUsuario", Utilidades.getIdUsuario(RegistrarPremios.this));
            datos.put("idBanca", Utilidades.getIdBanca(RegistrarPremios.this));
            datos.put("layout", "");
            datos.put("fecha", "");

            datosObj.put("datos", datos);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        Log.e("Borrar", datosObj.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);

                        try{
                            Toast.makeText(RegistrarPremios.this, response.getString("mensaje"), Toast.LENGTH_SHORT).show();
                            LoteriaClass l = loterias.get((int)spinnerLoteria.getSelectedItemId());
                            String loteria = l.getDescripcion();
                            getLoterias(loteria);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NetworkError){
                    Toast.makeText(RegistrarPremios.this, "Verifique coneccion y recargue la pagina", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof ServerError){
                    Toast.makeText(RegistrarPremios.this, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof TimeoutError){
                    Toast.makeText(RegistrarPremios.this, "Conexion lenta, verifique conexion y recargue de nuevo", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
                error.printStackTrace();
                Log.d("responseerror: ", String.valueOf(error));
            }
        });

        //mQueue.add(request);
        MySingleton.getInstance(RegistrarPremios.this).addToRequestQueue(request, 10000);
    }
    private void getLoterias(String loteriaASeleccionar)
    {
        String url = Utilidades.URL +"/api/premios";



        loterias.clear();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONArray loteriasArray = response.getJSONArray("loterias");
                            for(int i=0; i<loteriasArray.length() ; i++){
                                JSONArray jsonArraySorteos = loteriasArray.getJSONObject(i).getJSONArray("sorteos");
                                List<SorteosClass> sorteos = new ArrayList<SorteosClass>();;
                                for(int c=0; c<jsonArraySorteos.length() ; c++){
                                    SorteosClass sorteo = new SorteosClass();
                                    sorteo.setId(jsonArraySorteos.getJSONObject(c).getInt("id"));
                                    sorteo.setDescripcion(jsonArraySorteos.getJSONObject(c).getString("descripcion"));
                                    sorteos.add(sorteo);
                                }

                                LoteriaClass loteria = new LoteriaClass();
                                loteria.setId(loteriasArray.getJSONObject(i).getInt("id"));
                                loteria.setDescripcion(loteriasArray.getJSONObject(i).getString("descripcion"));
                                loteria.setPrimera(loteriasArray.getJSONObject(i).getString("primera"));
                                loteria.setSegunda(loteriasArray.getJSONObject(i).getString("segunda"));
                                loteria.setTercera(loteriasArray.getJSONObject(i).getString("tercera"));
                                loteria.setPick3(loteriasArray.getJSONObject(i).getString("pick3"));
                                loteria.setPick4(loteriasArray.getJSONObject(i).getString("pick4"));
                                loteria.setSorteos(sorteos);
                                loterias.add(loteria);
                            }
                            fillSpinner();
                            if(loteriaASeleccionar != null){
                                seleccionarLoteria(loteriaASeleccionar);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            Log.d("Error: ", e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NetworkError){
                    Toast.makeText(RegistrarPremios.this, "Verifique coneccion e intente nuevamente", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof ServerError){
                    Toast.makeText(RegistrarPremios.this, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof TimeoutError){
                    Toast.makeText(RegistrarPremios.this, "Conexion lenta, verifique conexion e intente nuevamente", Toast.LENGTH_SHORT).show();
                }
                error.printStackTrace();
                progressBar.setVisibility(View.GONE);
                Log.d("responseerror: ", String.valueOf(error));
            }
        });

//        mQueue.add(request);

        MySingleton.getInstance(RegistrarPremios.this).addToRequestQueue(request);
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
            ArrayAdapter<String> adapter =new ArrayAdapter<String>(RegistrarPremios.this,android.R.layout.simple_spinner_item, loteriasSpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLoteria.setAdapter(adapter);
        }catch (Exception e){
            e.printStackTrace();
        }


    }



    public static class guardarHttp extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;
        JSONObject data;

        public guardarHttp(JSONObject data) {
            this.data = data;
        }

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();

            try {
                //URL url = new URL("https://api.github.com/users/dmnugent80/repos");
                URL url = new URL(Utilidades.URL +"/api/premios/guardar");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");

                //SE ESCRIBEN LOS BYTES QUE SE VAN A ENVIAR
                DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream());
                //printout.writeBytes(URLEncoder.encode(datosObj.toString(),"UTF-8"));
                printout.writeBytes(data.toString());
                printout.flush();
                printout.close();


//                if (urlConnection.getResponseCode() != 201)
//                    return "Error";

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

                Log.e("pagarTicketHttp", result.toString());

                //SE BUSCA EL INDEX DE LAS JUGADAS EN EL JSONSTRING
//                int idx = result.toString().indexOf("\"jugadas\"");
//                String jugadasJsonString = "{" + result.toString().substring(idx, result.toString().length());
//                String loteriasJsonString = result.toString().substring(0, idx - 1) + "}";




            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }


            return result.toString();
        }
        @Override
        protected void onPostExecute(String result) {

            if(!result.equals("Error")) {
                //Do something with the JSON string

                int errores = llenarVenta(result.toString(), "errores");
                if (errores == 1) {
                    Toast.makeText(mContext, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                    return;
                }

                    Toast.makeText(mContext, "Se pago correctamente", Toast.LENGTH_LONG).show();

            }else{
                Toast.makeText(mContext, "Error del servidor", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static int llenarVenta(String ventasJsonString, String nombreSolicitado) {
//        if(jugadasLista != null)
//            jugadasLista.clear();
        VentasClass ventasClass = null;
        Gson gson = new GsonBuilder().create();

        try (com.google.gson.stream.JsonReader reader1 = new com.google.gson.stream.JsonReader(new StringReader(ventasJsonString))) {
            //reader1.beginObject();

            int c = 0;
            String nombre = "";
            while (reader1.hasNext()) {

                Log.e("llenarJugadaLoterias", "nombre:" + nombre +" token:" + reader1.peek());
                if (JsonToken.BEGIN_ARRAY.equals(reader1.peek())) {


                    reader1.beginArray();

                }

                if (JsonToken.BEGIN_OBJECT.equals(reader1.peek())) {


                        reader1.beginObject();



                }
                if (JsonToken.NAME.equals(reader1.peek())) {

                    nombre = reader1.nextName();
                    System.out.println("Token KEY >>>> " + nombre);

                }
                if (JsonToken.STRING.equals(reader1.peek())) {

                    String value = reader1.nextString();
                    if(nombre.equals("mensaje")){
                        mensaje = value;
                        Log.i("llenarJugadaLoterias", "mensaje:" + mensaje);
                    }
                    System.out.println("Token Value >>>> " + value);

                }
                if (JsonToken.NUMBER.equals(reader1.peek())) {

                    long value = reader1.nextLong();
                    if(nombre.equals("errores")){
                        return (int)value;
                    }
                    System.out.println("Token Value >>>> " + value);

                }
                if (JsonToken.NULL.equals(reader1.peek())) {

                    reader1.nextNull();
                    System.out.println("Token Value >>>> null");

                }
                if (JsonToken.END_OBJECT.equals(reader1.peek())) {

                    reader1.endObject();
//                    JsonToken nextToken1 = reader1.peek();
                    if (JsonToken.END_ARRAY.equals(reader1.peek())) {
                        reader1.endArray();
                    }
//                    Log.e("culo", "nombre:" + reader1.hasNext() +" token:" + nextToken1);

                }
                if (JsonToken.END_ARRAY.equals(reader1.peek())) {

                    reader1.endArray();

                }
                if (JsonToken.END_DOCUMENT.equals(reader1.peek())) {

                    return 0;

                }


                c++;

            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    public boolean existeSorteo(List<SorteosClass> sorteos, String sorteo)
    {
        for(SorteosClass s : sorteos){
            if(s.descripcion.equals(sorteo)){
                return true;
            }
        }

        return false;
    }

    public void seleccionarLoteria(String loteria){
        int idBanca = Utilidades.getIdBanca(mContext);
        int contador = 0;
        for (LoteriaClass l : loterias)
        {
            if(l.getDescripcion().equals(loteria)){
                break;
            }
            contador++;
        }
//        bancas.get((int)spinnerBanca.getSelectedItemId());
        spinnerLoteria.setSelection(contador);
    }

}
