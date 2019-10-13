package com.example.jean2.creta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BluetoothDevices extends AppCompatActivity {
    Button btnAgregar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices);

//        btnAgregar = (Button)findViewById(R.id.btnAgregar);
//        btnAgregar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                BluetoothSearchDialog duplicarDialog = new BluetoothSearchDialog();
//                duplicarDialog.show(BluetoothDevices.this.getSupportFragmentManager(), "Buscar dispositivo");
////                mostrarDispositivosBluetooth();
//            }
//        });


    }

    public void agregar(View view)
    {
        BluetoothSearchDialog duplicarDialog = new BluetoothSearchDialog();
        duplicarDialog.show(BluetoothDevices.this.getSupportFragmentManager(), "Buscar dispositivo");
    }
}
