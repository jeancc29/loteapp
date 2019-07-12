package com.example.jean2.creta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

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
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

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
}
