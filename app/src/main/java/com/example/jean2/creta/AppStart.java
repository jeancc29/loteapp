package com.example.jean2.creta;



import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

public class AppStart extends Activity implements OnClickListener {
	
	public static int nPrintWidth = 384;
	public static boolean bCutter = false;
	public static boolean bDrawer = false;
	public static boolean bBeeper = true;
	public static int nPrintCount = 1;
	public static int nCompressMethod = 0;
	public static boolean bAutoPrint = false;
	public static int nPrintContent = 0;
	public static boolean bCheckReturn = false;
	
	private RadioButton 
	radio58,radio80,
	radioPrintCount1,radioPrintCount10,radioPrintCount100,radioPrintCount1000,
	radioPrintContentS,radioPrintContentM,radioPrintContentL;
	private CheckBox chkCutter,chkDrawer,chkBeeper,chkPictureCompress,chkAutoPrint,chkCheckReturn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

//        if(!isMyServiceRunning(Servicio.class, this)){
//            startService(new Intent(this, Servicio.class));
//        }
		setContentView(R.layout.start_private);


		
		/* Iniciar wifi */
		WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		switch (wifiManager.getWifiState()) {
		case WifiManager.WIFI_STATE_DISABLED:
			wifiManager.setWifiEnabled(true);
			break;
		default:
			break;
		}
		
		/* Iniciar bluetooth */
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (null != adapter) {
			if (!adapter.isEnabled()) {
				if (!adapter.enable()) 
				{
					finish();
					return;
				}
			}
		}
		
		radio58 = (RadioButton)findViewById(R.id.radioButtonTicket58);
		radio80 = (RadioButton)findViewById(R.id.radioButtonTicket80);
		radioPrintCount1 = (RadioButton)findViewById(R.id.radioButtonPrintCount1);
		radioPrintCount10 = (RadioButton)findViewById(R.id.radioButtonPrintCount10);
		radioPrintCount100 = (RadioButton)findViewById(R.id.radioButtonPrintCount100);
		radioPrintCount1000 = (RadioButton)findViewById(R.id.radioButtonPrintCount1000);
		radioPrintContentS = (RadioButton)findViewById(R.id.radioButtonPrintContentS);
		radioPrintContentM = (RadioButton)findViewById(R.id.radioButtonPrintContentM);
		radioPrintContentL = (RadioButton)findViewById(R.id.radioButtonPrintContentL);
		chkCutter = (CheckBox)findViewById(R.id.checkBoxCutter);
		chkDrawer = (CheckBox)findViewById(R.id.checkBoxDrawer);
		chkBeeper = (CheckBox)findViewById(R.id.checkBoxBeeper);
		chkPictureCompress = (CheckBox)findViewById(R.id.checkBoxPictureCompress);
		chkAutoPrint = (CheckBox)findViewById(R.id.checkBoxAutoPrint);
		chkCheckReturn = (CheckBox)findViewById(R.id.checkBoxCheckReturn);
		
		findViewById(R.id.btnTestBT).setOnClickListener(this);
		findViewById(R.id.btnTestBLE).setOnClickListener(this);
		findViewById(R.id.btnTestUSB).setOnClickListener(this);
		findViewById(R.id.btnTestNET).setOnClickListener(this);
		Button btnImg = (Button) findViewById(R.id.btnImagen);
		btnImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					//Toast.makeText(AppStart.this, "Connecting...", Toast.LENGTH_LONG).show();
					//Intent intent = new Intent(AppStart.this, paginWebView.class);
					//Intent intent = new Intent(AppStart.this, ConnectCP2102Activity.class);
					//startActivity(intent);

				}catch (Exception ex){
					ex.printStackTrace();
				}
			}
		});

	}

    private boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service already","running");
                return true;
            }
        }
        Log.i("Service not","running");
        return false;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(radio58.isChecked())
			nPrintWidth = 384;
		else if(radio80.isChecked())
			nPrintWidth = 576;
				
		if(radioPrintCount1.isChecked())
			nPrintCount = 1;
		else if(radioPrintCount10.isChecked())
			nPrintCount = 10;
		else if(radioPrintCount100.isChecked())
			nPrintCount = 100;
		else if(radioPrintCount1000.isChecked())
			nPrintCount = 1000;
		
		if(radioPrintContentS.isChecked())
			nPrintContent = 1;
		else if (radioPrintContentM.isChecked())
			nPrintContent = 2;
		else if (radioPrintContentL.isChecked())
			nPrintContent = 3;
		
		bCutter = chkCutter.isChecked();
		bDrawer = chkDrawer.isChecked();
		bBeeper = chkBeeper.isChecked();
		
		nCompressMethod = chkPictureCompress.isChecked() ? 1 : 0;
		bAutoPrint = chkAutoPrint.isChecked();
		bCheckReturn = chkCheckReturn.isChecked();
		
		switch(v.getId()){
		case R.id.btnTestBT:
		{
			Intent intent = new Intent(AppStart.this, SearchBTActivity.class);
			startActivity(intent);
			break;
		}
		
		case R.id.btnTestBLE:
		{
			Intent intent = new Intent(AppStart.this, SearchBLEActivity.class);
			startActivity(intent);
			break;
		}
		
		case R.id.btnTestUSB:
		{
			Intent intent = new Intent(AppStart.this, ConnectUSBActivity.class);
			//Intent intent = new Intent(AppStart.this, ConnectCP2102Activity.class);
			startActivity(intent);
			break;
		}
		
		case R.id.btnTestNET:
		{
			Intent intent = new Intent(AppStart.this, ConnectIPActivity.class);
			startActivity(intent);
			break;
		}

		}
	}

}
