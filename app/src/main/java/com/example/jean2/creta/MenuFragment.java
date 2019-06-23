package com.example.jean2.creta;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment implements DuplicarDialog.DuplicarDialogListener {

    Button btnMonitoreo;
    Button btnHistoricoVentas;
    Button btnVentas;
    Button btnPagar;
    Button btnDuplicar;
    Button btnSalir;
    private FragmentActivity mContext;
    //private RequestQueue mQueue;
    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

       // mQueue = Volley.newRequestQueue(mContext);

        btnMonitoreo = (Button)view.findViewById(R.id.btnMonitoreo);
        btnMonitoreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MonitoreoActivity.class);
                startActivity(intent);
            }
        });

        btnHistoricoVentas = (Button)view.findViewById(R.id.btnHistoricoVentas);
        btnHistoricoVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), HistoricoVentasActivity.class);
                startActivity(intent);
            }
        });

        btnVentas = (Button)view.findViewById(R.id.btnVentas);
        btnVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), VentasActivity.class);
                startActivity(intent);
            }
        });

        btnPagar = (Button)view.findViewById(R.id.btnPagarTicket);
        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PagarTicketDialog pagarTicketDialog = new PagarTicketDialog();
                pagarTicketDialog.show(mContext.getSupportFragmentManager(), "Pagar dialog");
            }
        });

        btnDuplicar = (Button)view.findViewById(R.id.btnDuplicar);
        btnDuplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DuplicarDialog duplicarDialog = new DuplicarDialog();
                duplicarDialog.show(mContext.getSupportFragmentManager(), "Duplicar dialog");
            }
        });

        btnSalir = (Button)view.findViewById(R.id.btnSalir);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilidades.eliminarUsuario(getContext());
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        mContext=(FragmentActivity) context;
        super.onAttach(context);
    }

    @Override
    public void setCodigoBarra(String codigoBarra) {
        duplicarTicket(codigoBarra);
    }


    private void duplicarTicket(String codigoBarra){
        String url = "http://loterias.ml/api/principal/duplicar";

        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            loteria.put("codigoBarra", codigoBarra);
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
                                JSONArray jsonArray = response.getJSONArray("monitoreo");
                                PrincipalFragment.jugadasClase.duplicar(jsonArray);
                                Toast.makeText(mContext, "Se ha duplicado", Toast.LENGTH_SHORT).show();
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

            }
        });

        //mQueue.add(request);
        MySingleton.getInstance(mContext).addToRequestQueue(request);
    }
}
