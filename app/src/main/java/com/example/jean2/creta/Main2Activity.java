package com.example.jean2.creta;

import android.app.FragmentTransaction;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;

public class Main2Activity extends AppCompatActivity implements DuplicarDialog.DuplicarDialogListener, PagarTicketDialog.PagarTicketDialogListener {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private static DuplicarPrincipalInterface listener;
    public static ProgressBar progressBarToolbar;




    private TextView txtSelected;
    IconManager iconManager;
    CheckBox c;
    Spinner spinnerTicket;
    //private RequestQueue mQueue;

    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();

    View view;
    TextView txtJugada;
    TextView txtMontojugar;
    TextView txtMontodisponible;
    boolean jugada_monto_active = true;
    PrincipalFragment principalFragment;

    String monto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        principalFragment = new PrincipalFragment();

       // mQueue = Volley.newRequestQueue(this);
//        listener = (DuplicarPrincipalInterface) this;

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        //toolbar.setTitle("");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBarToolbar = (ProgressBar)findViewById(R.id.toolbar_progress_bar);

        viewPager = findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 2){
//                    //JugadasFragment jugadasFragment = new JugadasFragment();
//                    PrincipalFragment principalFragment = new PrincipalFragment();
//                    Log.d("Se ha actualizado:", String.valueOf(position));
//                    principalFragment.getJugadas();
//                    //jugadasFragment.update();
                    JugadasFragment.updateTable();
                }
            }

            @Override
            public void onPageSelected(int position) {
                JugadasFragment jugadasFragment = new JugadasFragment();
                PrincipalFragment principalFragment = new PrincipalFragment();
                Log.d("Se ha actualizado:", String.valueOf(position));
                principalFragment.getJugadas();
                jugadasFragment.update();
                Log.d("Seleccionada:", String.valueOf(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.getAdapter().notifyDataSetChanged();

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        viewPager.setCurrentItem(1);

    }

    public void hola(View v){
        TextView t = (TextView)findViewById(R.id.txtJugada);
        t.setText("Holaaa");
    }



    @Override
    public void setCodigoBarra(String codigoBarra) {
        duplicarTicket(codigoBarra);
    }

    @Override
    public void setCodigoBarraPagar(String codigoBarra) {
        pagarTicket(codigoBarra);
    }

    private void duplicarTicket(String codigoBarra){
        String url = "http://loterias.ml/api/principal/duplicar";

        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            loteria.put("codigoBarra", codigoBarra);
            loteria.put("razon", "Cancelado desde movil");
            loteria.put("idUsuario", Utilidades.getIdUsuario(Main2Activity.this));
            loteria.put("idBanca", Utilidades.getIdBanca(Main2Activity.this));

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
                                JSONArray jsonArray = response.getJSONArray("jugadas");
                                JSONArray jsonArrayLoterias = response.getJSONArray("loterias");
                                //PrincipalFragment.jugadasClase.duplicar(jsonArray);
                                //listener.setDuplicar(jsonArray);
//                                PrincipalFragment favoritesFragment = (PrincipalFragment) getSupportFragmentManager()
//                                        .getFragments()
//                                        .get(1);
//                                Log.d("Main2Activity", "fragment: " + getSupportFragmentManager()
//                                        .getFragments()
//                                        .get(1).getClass().getSimpleName().toString());
                                PrincipalFragment favoritesFragment;
                                if(getSupportFragmentManager().getFragments().get(0).getClass().getSimpleName().toString().equals("PrincipalFragment"))
                                     favoritesFragment = (PrincipalFragment) getSupportFragmentManager().getFragments().get(0);
                                else if(getSupportFragmentManager().getFragments().get(1).getClass().getSimpleName().toString().equals("PrincipalFragment"))
                                    favoritesFragment = (PrincipalFragment) getSupportFragmentManager().getFragments().get(1);
                                else
                                    favoritesFragment = (PrincipalFragment) getSupportFragmentManager().getFragments().get(2);

                                favoritesFragment.duplicar(jsonArray, jsonArrayLoterias);
                                tabLayout.getTabAt(1).select();
                            }
                            else
                                Toast.makeText(Main2Activity.this, response.getString("mensaje") + " e: " + errores, Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(Main2Activity.this, "Verifique coneccion e intente de nuevo", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof ServerError){
                    Toast.makeText(Main2Activity.this, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof TimeoutError){
                    Toast.makeText(Main2Activity.this, "Conexion lenta, verifique conexion e intente de nuevo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //mQueue.add(request);
        MySingleton.getInstance(Main2Activity.this).addToRequestQueue(request);
    }



    private void pagarTicket(String codigoBarra){
        String url = "http://loterias.ml/api/principal/pagar";

        JSONObject loteria = new JSONObject();
        JSONObject datosObj = new JSONObject();

        try {
            loteria.put("codigoBarra", codigoBarra);
            loteria.put("razon", "Cancelado desde movil");
            loteria.put("idUsuario", Utilidades.getIdUsuario(Main2Activity.this));
            loteria.put("idBanca", Utilidades.getIdBanca(Main2Activity.this));

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
                                Toast.makeText(Main2Activity.this, response.getString("mensaje") + " e: " + errores, Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(Main2Activity.this, response.getString("mensaje") + " e: " + errores, Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(Main2Activity.this, "Verifique coneccion e intente de nuevo", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof ServerError){
                    Toast.makeText(Main2Activity.this, "No se puede encontrar el servidor", Toast.LENGTH_SHORT).show();
                }
                else if(error instanceof TimeoutError){
                    Toast.makeText(Main2Activity.this, "Conexion lenta, verifique conexion e intente de nuevo", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        mQueue.add(request);
        MySingleton.getInstance(Main2Activity.this).addToRequestQueue(request);
    }

    //Para poder llamar al metodo escribir desde xml que esta en el fragment
    //Obligatoriamente debo hacerlo de esta manera porque el xml buscara el metodo escribir en el activity y no en el fragment
    public void escribir(View v) {
        PrincipalFragment p = new PrincipalFragment();
        p.escribir(v);
    }

    public interface DuplicarPrincipalInterface{
        void setDuplicar(JSONArray jugadas);
    }
}
