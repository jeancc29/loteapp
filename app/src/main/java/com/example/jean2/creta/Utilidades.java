package com.example.jean2.creta;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.jean2.creta.Clases.PrinterClass;
import com.example.jean2.creta.Servicios.JPrinterConnectService;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Utilidades {
    public static boolean guardarUsuario(Context context, boolean recordar, JSONObject jsonObjectUsuario){
        SharedPreferences preferences = context.getSharedPreferences("usuario", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("recordar", recordar);
        try {
            editor.putInt("idUsuario", jsonObjectUsuario.getInt("idUsuario"));
            editor.putString("usuario", jsonObjectUsuario.getString("usuario"));
            editor.putString("password", jsonObjectUsuario.getString("password"));
            editor.putString("banca", jsonObjectUsuario.getString("banca"));
            editor.putInt("idBanca", jsonObjectUsuario.getInt("idBanca"));
            editor.putBoolean("administrador", jsonObjectUsuario.getBoolean("administrador"));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        editor.commit();
        return true;
    }


    public static boolean guardarImpresora(Context context, String name, String address){
        SharedPreferences preferences = context.getSharedPreferences("impresoras", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        String nombreKey = "impresora" + address;
        try {

            editor.putString("address", address);
            editor.putString("name", name);

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        editor.commit();
        return true;
    }

    public static String getAddressImpresora(Context context){
        SharedPreferences preferences = context.getSharedPreferences("impresoras", Context.MODE_PRIVATE);
        //String nombreKey = "address" + 1;
        //String nombreKey = "address" + 2;
        return preferences.getString("address", "");
    }

    public static String getNameImpresora(Context context){
        SharedPreferences preferences = context.getSharedPreferences("impresoras", Context.MODE_PRIVATE);
//        String nombreKey = "address" + 1;
//        String nombreKey = "address" + 2;
        return preferences.getString("name", "");
    }

    public static Map<String,?> getTodasImpresoras(Context context){
        SharedPreferences preferences = context.getSharedPreferences("impresoras", Context.MODE_PRIVATE);
        return preferences.getAll();
    }

    public static boolean hayImpresorasRegistradas(Context context)
    {
        Map<String,?> impresoras = Utilidades.getTodasImpresoras(context);
        Log.d("BluetoothDevice", "Impresoras:" + impresoras.toString());
        if(impresoras.size() > 0){
            return true;
        }else{
            return false;
        }
    }

    public static boolean eliminarImpresoras(Context context){
        SharedPreferences preferences = context.getSharedPreferences("impresoras", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        return true;
    }

    public static boolean eliminarUsuario(Context context){
        SharedPreferences preferences = context.getSharedPreferences("usuario", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        return true;
    }

    public static boolean esSessionGuardada(Context context){
        SharedPreferences preferences = context.getSharedPreferences("usuario", Context.MODE_PRIVATE);

        return preferences.getBoolean("recordar", false);
    }

    public static int getIdUsuario(Context context){
        SharedPreferences preferences = context.getSharedPreferences("usuario", Context.MODE_PRIVATE);

        return preferences.getInt("idUsuario", 0);
    }

    public static boolean getAdministrador(Context context){
        SharedPreferences preferences = context.getSharedPreferences("usuario", Context.MODE_PRIVATE);

        return preferences.getBoolean("administrador", false);
    }

    public static String getUsuario(Context context){
        SharedPreferences preferences = context.getSharedPreferences("usuario", Context.MODE_PRIVATE);

        return preferences.getString("usuario", "");
    }

    public static String getPassword(Context context){
        SharedPreferences preferences = context.getSharedPreferences("usuario", Context.MODE_PRIVATE);

        return preferences.getString("password", "");
    }

    public static int getIdBanca(Context context){
        SharedPreferences preferences = context.getSharedPreferences("usuario", Context.MODE_PRIVATE);

        return preferences.getInt("idBanca", 0);
    }

    public static String getBanca(Context context){
        SharedPreferences preferences = context.getSharedPreferences("usuario", Context.MODE_PRIVATE);

        return preferences.getString("banca", "");
    }

    public static Bitmap toBitmap(String base64Image){
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public static Uri toUri(Context context, Bitmap bitmap, String title){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    public static Bitmap combinarBitmap(Bitmap bitmap1, Bitmap bitmap2){
        Bitmap bmOverlay = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), bitmap1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bitmap1, 0f, 0f, null);
        canvas.drawBitmap(bitmap2, 125f, bitmap1.getHeight() - 150, null);
        return bmOverlay;
    }

    public static String toSecuencia(String idTicket, String codigoBanca){
        String pad = "000000000";
        String ans = codigoBanca + "-"+ pad.substring(0, pad.length() - idTicket.length()) + idTicket;
        return ans;
    }

    public static void sendSMS(Context context,  Bitmap base64Image, boolean sms){
        String pack = "com.whatsapp";
        String titleImage = "ticket";
//        Bitmap imageBitmap = toBitmap(base64Image);
//        Uri imageUri = toUri(context, imageBitmap, titleImage);
        Uri imageUri = toUri(context, base64Image, titleImage);

        if(sms){
          Intent mmsIntent = new Intent(Intent.ACTION_SEND);
                    mmsIntent.putExtra("sms_body", "Please see the attached image");
           // mmsIntent.setType("vnd.android-dir/mms-sms");
                    mmsIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    mmsIntent.setType("image/*");

                    context.startActivity(Intent.createChooser(mmsIntent,"Send"));
        }else{
            PackageManager pm = context.getPackageManager();
            try {

                @SuppressWarnings("unused")
                PackageInfo info = pm.getPackageInfo(pack, PackageManager.GET_META_DATA);

//                Intent mmsIntent = new Intent(Intent.ACTION_VIEW);
//                mmsIntent.putExtra("sms_body", "Please see the attached image");
//                mmsIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
//                mmsIntent.setType("vnd.android-dir/mms-sms");
//                context.startActivity(Intent.createChooser(mmsIntent,"Send"));

                if(!sms){
                    Intent waIntent = new Intent(Intent.ACTION_SEND);
                    waIntent.setType("image/*");
                    waIntent.setPackage(pack);
                    waIntent.putExtra(android.content.Intent.EXTRA_STREAM, imageUri);
                    waIntent.putExtra(Intent.EXTRA_TEXT, pack);
                    context.startActivity(Intent.createChooser(waIntent, "Share with"));
                }else{
//                    Intent mmsIntent = new Intent(Intent.ACTION_VIEW);
//                    mmsIntent.putExtra("sms_body", "Please see the attached image");
//                    mmsIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
//                    mmsIntent.setType("image/*");
//                    context.startActivity(Intent.createChooser(mmsIntent,"Send"));
                }


            } catch (Exception e) {
                Log.e("Error on sharing", e + " ");
                Toast.makeText(context, "App not Installed", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public static String ordenarMenorAMayor(String jugada)
    {
        //Esta funcion ordena los paleses de menor a mayor
        if(jugada.length() == 4 && Utilidades.toInt(jugada) != 0){
            String primerParNumeros = jugada.substring(0, 2);
            String segundoParNumeros = jugada.substring(2, 4);
            String jugadaRetornar = jugada;

            if(Utilidades.toInt(primerParNumeros) < Utilidades.toInt(segundoParNumeros)){
                return jugadaRetornar;
            }else{
                jugadaRetornar = segundoParNumeros + primerParNumeros;
                return  jugadaRetornar;
            }
        }

        return jugada;
    }

    public static String agregarGuion(String jugada){
        String cadena = jugada;
        //Si se ha enviado un sorteo diferente de nulo entonces se debe agregar a la cadena el signo (+ o -) correspondiente a cada sorteo
        //Y luego mas abajo se determinara que sorteo es gracias al signo agregado
//        if(sorteo.equals("no") == false){
//            if(sorteo.equals("Pick 3 Box")){
//                jugada +='+';
//            }
//            else if(sorteo.equals("Pick 4 Straight")){
//                jugada +='-';
//            }
//            else if(sorteo.equals("Pick 4 Box")){
//                jugada +='+';
//            }
//        }

        Log.v("UtilidadesGuion1", cadena);

        if(jugada.length() == 3){
            cadena +='S';
        }
        else if(jugada.length() == 4){
            Log.v("UtilidadesGuionpr", jugada.substring(0, 3));
            if(jugada.charAt(jugada.length() - 1) == '+'){
                cadena = jugada.substring(0, 3) + 'B';
            }
            else{
                cadena = jugada.substring(0, 2) + '-' +jugada.substring(2, 4);
            }
        }
        else if(jugada.length() == 5){
            if(jugada.charAt(jugada.length() - 1) == '+'){
                cadena = jugada.substring(0, 4) + 'B';
            }
            else if(jugada.charAt(jugada.length() - 1) == '-'){
                cadena = jugada.substring(0, 4) + 'S';
            }
        }
        else if(jugada.length() == 6){
            cadena = jugada.substring(0, 2) + '-' +jugada.substring(2, 4) + '-' +jugada.substring(4, 6);
        }

        Log.v("UtilidadesGuio2", cadena);
        return cadena;
    }

    public static String agregarGuionPorSorteo(String jugada, String sorteo){
        String cadena = jugada;

         if(sorteo.equals("Pick 3 Box")){
            cadena +='+';
        }
        else if(sorteo.equals("Pick 4 Straight")){
            cadena +='-';
        }
        else if(sorteo.equals("Pick 4 Box")){
            cadena +='+';
        }




        return cadena;
    }

    public static int toInt(String valor){
        try {
            return Integer.parseInt(valor);
        } catch(NumberFormatException nfe) {
            return 0;
        }
    }

    public static String getVersionName(Context ctx){
        return BuildConfig.VERSION_NAME;
    }

    public static void conectarseAutomaticamente(Context context){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                //if(Utilidades.getImpresora(context, deviceHardwareAddress).equals("") == false){
                    Intent serviceIntent = new Intent(context, JPrinterConnectService.class);
                    serviceIntent.putExtra("address", deviceHardwareAddress);
                    serviceIntent.putExtra("name", deviceName);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        context.startForegroundService(serviceIntent);
                    else
                        context.startService(serviceIntent);
                //}
                Toast.makeText(context, "Device: " + deviceName + " - " + deviceHardwareAddress, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void killAppServiceByPackageName(Context context, String packageName)
    {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = context.getPackageManager();
        //get a list of installed apps.
        packages = pm.getInstalledApplications(0);

        if(packageName == null)
            packageName = "com.example.lotecom.mobile";

        String packageTokill = packageName;
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);


        for (ApplicationInfo packageInfo : packages) {

            if(packageInfo.packageName.equals(packageTokill)) {
                mActivityManager.killBackgroundProcesses(packageInfo.packageName);
            }

        }
    }

    public String getPackNameByAppName(Context context, String name) {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> l = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        String packName = "";
        for (ApplicationInfo ai : l) {
            String n = (String)pm.getApplicationLabel(ai);
            if (n.contains(name) || name.contains(n)){
                packName = ai.packageName;
            }
        }

        return packName;
    }

    static void imprimir(Context context,JSONObject venta, int original_cancelado_copia)
    {
        PrinterClass printerClass = new PrinterClass(context, venta);
        printerClass.conectarEImprimir(true, original_cancelado_copia);
    }
}
