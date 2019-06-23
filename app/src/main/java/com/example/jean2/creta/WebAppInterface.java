package com.example.jean2.creta;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebAppInterface {
    private Context context;

        public WebAppInterface(Context context){
            this.context = context;
        }

        @JavascriptInterface
        public void showToast(String message){
            Toast.makeText(context,message,Toast.LENGTH_LONG).show();
        }

    @JavascriptInterface
    public void sendSMS(String titleImage, String base64Image, boolean sms){
            String pack = "com.whatsapp";
            WebActivity wa = new WebActivity();
            Bitmap imageBitmap = wa.toBitmap(base64Image);
            Uri imageUri = wa.toUri(imageBitmap, titleImage);

        if(sms){

        }else{
            PackageManager pm = context.getPackageManager();
            try {

                @SuppressWarnings("unused")
                PackageInfo info = pm.getPackageInfo(pack, PackageManager.GET_META_DATA);

                Intent waIntent = new Intent(Intent.ACTION_SEND);
                waIntent.setType("image/*");
                waIntent.setPackage(pack);
                waIntent.putExtra(android.content.Intent.EXTRA_STREAM, imageUri);
                waIntent.putExtra(Intent.EXTRA_TEXT, pack);
                context.startActivity(Intent.createChooser(waIntent, "Share with"));
            } catch (Exception e) {
                Log.e("Error on sharing", e + " ");
                Toast.makeText(context, "App not Installed", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
