package com.example.jean2.creta.Servicios;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jean2.creta.ActualizarActivity;
import com.example.jean2.creta.BuildConfig;
import com.example.jean2.creta.LoginActivity;
import com.example.jean2.creta.MySingleton;
import com.example.jean2.creta.Utilidades;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

public class ActualizarService extends Service {
    public void onCreate(){
        super.onCreate();
        verificarVersion();
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("ActualizarService", "Todo bien");
        verificarVersion();
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

    private void verificarVersion(){
        String url = "https://loterias.ml/api/versiones/publicada";


        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            loteria.put("idUsuario", Utilidades.getUsuario(ActualizarService.this));
            loteria.put("password", Utilidades.getPassword(ActualizarService.this));

            datosObj.put("datos", loteria);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.v("ActualizarService", "Todo bien");


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datosObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String errores = response.getString("errores");
                            if(errores.equals("0")){
                                Log.v("ActualizarService", "Todo bien");
                                JSONObject jsonObjectVersion = response.getJSONObject("version");
                                if(jsonObjectVersion.getString("version").equals(getVersionName(ActualizarService.this)) == false){
                                    Intent intent = new Intent(ActualizarService.this, ActualizarActivity.class);
                                    intent.putExtra("enlace", jsonObjectVersion.getString("enlace"));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    stopSelf();
                                }
                                stopSelf();
                            }
                            else{
                                Log.v("ActualizarService", "Todo mal: " + errores);
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

        MySingleton.getInstance(ActualizarService.this).addToRequestQueue(request);
    }

    public String getVersionName(Context ctx){
        return BuildConfig.VERSION_NAME;
    }
}
