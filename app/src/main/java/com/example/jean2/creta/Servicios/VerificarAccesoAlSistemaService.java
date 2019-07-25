package com.example.jean2.creta.Servicios;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.util.Log;
import android.view.View;
import android.widget.Toast;



import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jean2.creta.LoginActivity;
import com.example.jean2.creta.Main2Activity;
import com.example.jean2.creta.MySingleton;
import com.example.jean2.creta.Utilidades;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class VerificarAccesoAlSistemaService extends Service {

    public void onCreate(){
        super.onCreate();
        verificarSesion();
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        verificarSesion();
        return START_STICKY;
    }

    public void onDestroy(){
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void verificarSesion(){
        String url = "http://loterias.ml/api/acceder";


        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            loteria.put("usuario", Utilidades.getUsuario(VerificarAccesoAlSistemaService.this));
            loteria.put("password", Utilidades.getPassword(VerificarAccesoAlSistemaService.this));

            datosObj.put("datos", loteria);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String errores = response.getString("errores");
                            if(errores.equals("1")){
                                Log.v("verificarSV", "Todo mal");
                                Utilidades.eliminarUsuario(VerificarAccesoAlSistemaService.this);
                                Intent dialogIntent = new Intent(VerificarAccesoAlSistemaService.this, LoginActivity.class);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(dialogIntent);
                                stopSelf();
                            }
                            else{
                                Log.v("verificarSV", "Todo bien");
                                stopSelf();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            stopSelf();
                            Log.d("Error: ", e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stopSelf();
                error.printStackTrace();
                Log.d("responseerror: ", String.valueOf(error));
            }
        });

//        mQueue.add(request);

        MySingleton.getInstance(VerificarAccesoAlSistemaService.this).addToRequestQueue(request);
    }
}
