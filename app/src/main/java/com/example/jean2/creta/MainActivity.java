package com.example.jean2.creta;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 0;
    private WebView miWebView;
    private Button btnProbarBluetooth;
    private TextView txtView;
    //private ListView lv;


    BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        btnProbarBluetooth = (Button)findViewById(R.id.btnProbarBluetooth);
//
//        btnProbarBluetooth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                bluetooth();
//            }
//        });
//        txtView = (TextView)findViewById(R.id.txtView);
        //lv = (ListView)findViewById(R.id.lista);



//        miWebView = findViewById(R.id.webView);
//        miWebView.getSettings().setJavaScriptEnabled(true);
//        miWebView.setWebChromeClient(new WebChromeClient());
//        miWebView.setWebViewClient(new WebViewClient());
//
//        miWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
//        miWebView.loadUrl("https://loteria29.azurewebsites.net/login");
    }

//    public void toBitmap(String base64Image){
//        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
//        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//    }

    public void bluetooth(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null){
            Toast.makeText(MainActivity.this, "Device does not support bluetooth...", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivity.this, "Device support bluetooth...", Toast.LENGTH_LONG).show();
        }

        if(!bluetoothAdapter.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }

//        String devices = pairedDevice();
//
//        txtView.setText(devices);

        initBroadcast();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data){
        Toast.makeText(MainActivity.this, "Resultado: " + resultCode, Toast.LENGTH_LONG).show();
        if(resultCode == Activity.RESULT_OK){
            Toast.makeText(MainActivity.this, "Resultado: " + resultCode, Toast.LENGTH_LONG).show();
            bluetooth();
        }
    }

    private String pairedDevice(){
        Set<BluetoothDevice> pairedDevices;
        pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList<String> arrayList = new ArrayList<>();
        String dv = "";
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){

                dv += device.getName() + " - " + device.getAddress();
                dv += "\n";
            }

//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                        this,
//                        MainActivity.class
//                );
//
//                lv.setAdapter(arrayAdapter);
        }
        return dv;
    }

    private void initBroadcast(){
        //Create a BroadcastReceiver for ACTION_FOUND
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                txtView.setText("Dentro Broadcast");
                String action = intent.getAction();
                //When discovery finds a device
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    //Get the BluetoothDevice objtect from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //aDD THE NAME AND ADDRESS TO AN ARRAY ADAPTER TO SHOW IN A LISTVIEW OR CURRENT SETVIEW
                    txtView.setText(device.getName() + " - " + device.getAddress());
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, filter);
    }


    private void uninitBroadcast() {
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }


//    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//    register




}
