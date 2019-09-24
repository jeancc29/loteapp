package com.example.jean2.creta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.example.jean2.creta.Servicios.JPrinterConnectService;

public class ExampleBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
//        if(JPrinterConnectService.detenidoPresionandoBotonDesconectar == false){
//            Intent serviceIntent = new Intent(context, JPrinterConnectService.class);
//            serviceIntent.putExtra("address", JPrinterBluetoothSingleton.getInstance().getAddress());
//            serviceIntent.putExtra("name", JPrinterBluetoothSingleton.getInstance().getName());
//            context.startService(serviceIntent);
//        }
//        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
//            Toast.makeText(context, "Boot complete", Toast.LENGTH_SHORT).show();
//        }
//
//        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
//            Toast.makeText(context, "Connectivity changed", Toast.LENGTH_SHORT).show();
//
//        }
    }
}
