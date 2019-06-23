package com.example.jean2.creta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class WebActivity extends AppCompatActivity {

    private WebView miWebView;
    private ImageView imgPrueba;
    Bitmap bitmap;
    WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        imgPrueba = (ImageView)findViewById(R.id.imgPrueba);


//        WebView wv = new WebView(this);
         wv = findViewById(R.id.webView);
        wv.layout(0, 0, wv.getMeasuredWidth(), wv.getMeasuredHeight());
        wv.setDrawingCacheEnabled(true);
        wv.buildDrawingCache();
        wv.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        wv.loadData("<html><body><p>Hola palomo</p></body></html>", "text/html", "UTF-8");

        wv.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Toast.makeText(WebActivity.this, "Finalizo w:"+wv.getMeasuredWidth() + " h:"+wv.getMeasuredHeight(), Toast.LENGTH_LONG).show();
//                bitmap = Bitmap.createBitmap(wv.getMeasuredWidth(), wv.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//                img(bitmap);
            }
        });







//        miWebView = findViewById(R.id.webView);
//        //clearcache(miWebView);
//        miWebView.getSettings().setJavaScriptEnabled(true);
//        miWebView.setWebChromeClient(new WebChromeClient());
//        miWebView.setWebViewClient(new WebViewClient());
//
//        miWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
//        miWebView.loadUrl("http://loterias.ml/login");
    }

        public Bitmap toBitmap(String base64Image){
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public void img(Bitmap bitmap){
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        int iHeight = bitmap.getHeight();
        canvas.drawBitmap(bitmap, 0, iHeight, paint);
        wv.draw(canvas);

        imgPrueba.setImageBitmap(bitmap);
    }

    public Uri toUri(Bitmap bitmap, String title){
        Context context = this;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    public void clearcache(WebView webView){
        Toast.makeText(WebActivity.this, "Cache cleared", Toast.LENGTH_LONG).show();
        webView.clearCache(true);
        webView.reload();
    }
}
