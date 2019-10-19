package com.example.jean2.creta;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jean2.creta.Clases.PrinterClass;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.util.Map;

public class BluetoothDevices extends AppCompatActivity {
    Button btnAgregar;
    Button btnEliminar;
    static TextView txtName;
    static TextView txtAddress;
    static CardView deviceCardView;
    static Context mContext;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices);
        toolbar = findViewById(R.id.toolBarMonitoreo);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Dispositivos bluetooth");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = this;
        deviceCardView = (CardView)findViewById(R.id.deviceCardView);
        txtName = (TextView)findViewById(R.id.txtName);
        txtAddress = (TextView)findViewById(R.id.txtAddress);
        mostrarCardView();

    }

    public static void mostrarCardView()
    {
        Map<String,?> impresoras = Utilidades.getTodasImpresoras(mContext);
        Log.d("BluetoothDevice", "Impresoras:" + impresoras.toString());
        if(impresoras.size() > 0){
            deviceCardView.setVisibility(View.VISIBLE);
            llenarCardView();
        }else{
            deviceCardView.setVisibility(View.GONE);
        }
    }

    public void agregar(View view)
    {
        BluetoothSearchDialog duplicarDialog = new BluetoothSearchDialog();
        duplicarDialog.show(BluetoothDevices.this.getSupportFragmentManager(), "Buscar dispositivo");
    }

    public void eliminar(View view)
    {
        Utilidades.eliminarImpresoras(BluetoothDevices.this);
        mostrarCardView();
    }

    public void probarImpresora(View view)
    {
        if(Utilidades.hayImpresorasRegistradas(BluetoothDevices.this))
        {
            PrinterClass printerClass = new PrinterClass(BluetoothDevices.this);
            printerClass.conectarEImprimir(false, 1);
        }
        else
        {
            Toast.makeText(this, "Debe registrar una impresora", Toast.LENGTH_SHORT).show();
        }
    }

    public static void llenarCardView()
    {
        Map<String,?> impresoras = Utilidades.getTodasImpresoras(mContext);
        Log.d("BluetoothDevice", "Impresoras:" + impresoras.toString());
        if(impresoras.size() > 0){
            String address = Utilidades.getAddressImpresora(mContext);
            String name = Utilidades.getNameImpresora(mContext);

            txtName.setText(name);
            txtAddress.setText(address);
//            Log.d("BluetoothDevices", "LLenarCardView:" + impresoras.values());
        }
    }
}
