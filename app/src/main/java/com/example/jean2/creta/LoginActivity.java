package com.example.jean2.creta;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
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
import com.example.jean2.creta.Clases.BancaClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    TextInputEditText txtUsuario;
    TextInputEditText txtPassword;
    CheckBox checkBoxRecordar;
    ProgressBar progressBar;
    public static int idUsuario = 0;
    final private int REQUEST_CODE_ASK_PERMISSION = 111;
    List<BancaClass> bancas = new ArrayList<>();

    //private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        comprobarSesionGuardada();

        toolbar = findViewById(R.id.toolBarLogin);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Login");

        //mQueue = Volley.newRequestQueue(this);
        Button btnAcceder = (Button)findViewById(R.id.btnAcceder);
        txtUsuario = (TextInputEditText)findViewById(R.id.txtUsuario);
        txtPassword = (TextInputEditText)findViewById(R.id.txtPassword);
        txtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        checkBoxRecordar = (CheckBox) findViewById(R.id.ckbRecordar);
        progressBar = (ProgressBar)findViewById(R.id.progressBarLogin);

        btnAcceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                jsonParse(txtUsuario.getText().toString(), txtPassword.getText().toString());
            }
        });
    }

    private void comprobarSesionGuardada(){
        if(Utilidades.esSessionGuardada(LoginActivity.this)){
            Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }


    private void comprobarPermisos(){
        int permisosCamara = ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA);
        int permisosStorage = ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permisosStorage != PackageManager.PERMISSION_GRANTED || permisosCamara != PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSION);
            }
        }

    }
    private void jsonParse(String usuario, String password){
        String url = Utilidades.URL +"/api/acceder";


        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            loteria.put("usuario", usuario);
            loteria.put("password", password);

            datosObj.put("datos", loteria);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            String errores = response.getString("errores");
                            if(errores.equals("0")){
                                //creamos un json usuario
                                JSONObject usuario = new JSONObject();
                                usuario.put("idUsuario", response.getInt("idUsuario"));
                                usuario.put("usuario", txtUsuario.getText());
                                usuario.put("password", txtPassword.getText());
                                usuario.put("banca", response.getString("banca"));
                                usuario.put("idBanca", response.getString("idBanca"));
                                usuario.put("administrador", response.getBoolean("administrador"));
                                Utilidades.guardarUsuario(LoginActivity.this, checkBoxRecordar.isChecked(), usuario);
                                Toast.makeText(LoginActivity.this, "Datos correctos", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else
                                Toast.makeText(LoginActivity.this, response.getString("mensaje") + " e: " + errores, Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(LoginActivity.this, "Verifique coneccion e intente nuevamente", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof ServerError){
                    Toast.makeText(LoginActivity.this, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof TimeoutError){
                    Toast.makeText(LoginActivity.this, "Conexion lenta, verifique conexion e intente nuevamente", Toast.LENGTH_SHORT).show();
                }
                error.printStackTrace();
                progressBar.setVisibility(View.GONE);
                Log.d("responseerror: ", String.valueOf(error));
            }
        });

//        mQueue.add(request);

        MySingleton.getInstance(LoginActivity.this).addToRequestQueue(request);
    }


}
