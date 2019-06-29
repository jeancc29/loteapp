package com.example.jean2.creta;

import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
import com.lvrenyang.io.BTPrinting;
import com.lvrenyang.io.IOCallBack;
import com.lvrenyang.io.Pos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main2Activity extends AppCompatActivity implements DuplicarDialog.DuplicarDialogListener, PagarTicketDialog.PagarTicketDialogListener, IOCallBack {
    private Toolbar toolbar;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private static DuplicarPrincipalInterface listener;
    public static ProgressBar progressBarToolbar;
    public static TextView_Icon txtBluetooth;
    public static boolean conectadoAImpresoraBluetooth = false;

    private IntentFilter intentFilter = null;
    ExecutorService es = Executors.newScheduledThreadPool(30);
    Pos mPos = new Pos();
    BTPrinting mBt = new BTPrinting();
    private LinearLayout linearlayoutdevices;
    private ProgressBar progressBarSearchStatus;
    private BroadcastReceiver broadcastReceiver = null;
    List<String> dispostivosLista = new ArrayList<String>();


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
        initBroadcast();

        principalFragment = new PrincipalFragment();

       // mQueue = Volley.newRequestQueue(this);
//        listener = (DuplicarPrincipalInterface) this;

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        //toolbar.setTitle("");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBarToolbar = (ProgressBar)findViewById(R.id.toolbar_progress_bar);
        txtBluetooth = (TextView_Icon)findViewById(R.id.txtBluetooth);
        txtBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothSearchDialog duplicarDialog = new BluetoothSearchDialog();
                duplicarDialog.show(Main2Activity.this.getSupportFragmentManager(), "Duplicar dialog");
//                mostrarDispositivosBluetooth();
            }
        });

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



        progressBarSearchStatus = (ProgressBar) findViewById(R.id.progressBarSearchStatus);
        //linearlayoutdevices = (LinearLayout) findViewById(R.id.linearlayoutdevices);



        mPos.Set(mBt);
        mBt.SetCallBack(Main2Activity.this);
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


    public void mostrarDispositivosBluetooth(){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (null == adapter) {
            finish();
        }

        if (!adapter.isEnabled()) {
            if (adapter.enable()) {
                while (!adapter.isEnabled())
                    ;
                Log.v("mostrarDispositivosBlue", "Enable BluetoothAdapter");
            } else {

                finish();
            }
        }

        adapter.cancelDiscovery();
        adapter.startDiscovery();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Main2Activity.this);
        mBuilder.setTitle("Seleccionar dispositivo");
        String[] arrayString = dispostivosLista.toArray(new String[0]);
        mBuilder.setItems(arrayString, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {


            }
        });

        AlertDialog mDialog = mBuilder.create();
        //mDialog.getButton(DialogInterface.OnShowListener).performClick();
        mDialog.show();
    }


    private void initBroadcast() {
        Log.d("initBroad", "heyy");
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String action = intent.getAction();
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    if (device == null)
                        return;
                    final String address = device.getAddress();
                    String name = device.getName();
                    if (name == null)
                        name = "BT";
                    else if (name.equals(address))
                        name = "BT";
                    Button button = new Button(context);
                    button.setText(name + ": " + address);

                    String nombre = name + ": " + address;

//                    for(int i = 0; i < linearlayoutdevices.getChildCount(); ++i)
//                    {
//                        Button btn = (Button)linearlayoutdevices.getChildAt(i);
//                        if(btn.getText().equals(button.getText()))
//                        {
//                            return;
//                        }
//                    }

                    Log.d("initBroad", nombre);
                    button.setGravity(android.view.Gravity.CENTER_VERTICAL
                            | Gravity.LEFT);
                    button.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            Toast.makeText(Main2Activity.this, "Connecting...", Toast.LENGTH_SHORT).show();

                            linearlayoutdevices.setEnabled(false);
                            for(int i = 0; i < linearlayoutdevices.getChildCount(); ++i)
                            {
                                Button btn = (Button)linearlayoutdevices.getChildAt(i);
                                btn.setEnabled(false);
                            }

                            es.submit(new Main2Activity.TaskOpen(mBt,address, Main2Activity.this));
                            //es.submit(new TaskTest(mPos, mBt, address, mActivity));
                        }
                    });
                    button.getBackground().setAlpha(100);
                    dispostivosLista.add(nombre);
                    //linearlayoutdevices.addView(button);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
                        .equals(action)) {
                   // progressBarSearchStatus.setIndeterminate(true);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                        .equals(action)) {
                    //progressBarSearchStatus.setIndeterminate(false);
                }

            }

        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);
        mostrarDispositivosBluetooth();
    }

    private void uninitBroadcast() {
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void OnOpen() {
        // TODO Auto-generated method stub
        this.runOnUiThread(new Runnable(){

            @Override
            public void run() {

                linearlayoutdevices.setEnabled(false);
                for(int i = 0; i < linearlayoutdevices.getChildCount(); ++i)
                {
                    Button btn = (Button)linearlayoutdevices.getChildAt(i);
                    btn.setEnabled(false);
                }
                Toast.makeText(Main2Activity.this, "Connected", Toast.LENGTH_SHORT).show();
//                if(AppStart.bAutoPrint)
//                {
//                    btnPrint.performClick();
//                }
            }
        });
    }

    @Override
    public void OnOpenFailed() {
        // TODO Auto-generated method stub
        this.runOnUiThread(new Runnable(){

            @Override
            public void run() {

                linearlayoutdevices.setEnabled(true);
                for(int i = 0; i < linearlayoutdevices.getChildCount(); ++i)
                {
                    Button btn = (Button)linearlayoutdevices.getChildAt(i);
                    btn.setEnabled(true);
                }
                Toast.makeText(Main2Activity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnClose() {
        // TODO Auto-generated method stub
        this.runOnUiThread(new Runnable(){

            @Override
            public void run() {
                linearlayoutdevices.setEnabled(true);
                for(int i = 0; i < linearlayoutdevices.getChildCount(); ++i)
                {
                    Button btn = (Button)linearlayoutdevices.getChildAt(i);
                    btn.setEnabled(true);
                }
            }
        });
    }

    public class TaskOpen implements Runnable
    {
        BTPrinting bt = null;
        String address = null;
        Context context = null;

        public TaskOpen(BTPrinting bt, String address, Context context)
        {
            this.bt = bt;
            this.address = address;
            this.context = context;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            bt.Open(address,context);
        }
    }
}
