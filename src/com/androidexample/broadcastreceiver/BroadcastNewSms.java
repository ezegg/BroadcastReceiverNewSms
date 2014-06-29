package com.androidexample.broadcastreceiver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import android.os.BatteryManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.EditText;
import android.widget.Toast;


public class BroadcastNewSms extends Activity {
	Button buttonSend;
	EditText textPhoneNo;
	EditText textSMS;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.androidexample_broadcast_newsms);
		this.registerReceiver(this.batteryInfoReceiver,	new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		/*para enviar mensajes*/
//		buttonSend = (Button) findViewById(R.id.buttonSend);
//		textPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
//		textSMS = (EditText) findViewById(R.id.editTextSMS);
 
//		buttonSend.setOnClickListener(new View.OnClickListener() {
// 
//			@Override
//			public void onClick(View v) {
// 
//			  String phoneNo = textPhoneNo.getText().toString();
//			  String sms = textSMS.getText().toString();
// 
//			  try {
//				SmsManager smsManager = SmsManager.getDefault();
//				smsManager.sendTextMessage(phoneNo, null, sms, null, null);
//				Toast.makeText(getApplicationContext(), "SMS Sent!",
//							Toast.LENGTH_LONG).show();
//			  } catch (Exception e) {
//				Toast.makeText(getApplicationContext(),
//					"SMS faild, please try again later!",
//					Toast.LENGTH_LONG).show();
//				e.printStackTrace();
//			  }
// 
//			}
//		});
	}
	
		 
		 
		 private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
			 @Override
			 public void onReceive(Context context, Intent intent) {
				 int level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
				 Date now = new Date();
				 appendLog("["+now.toString()+"] "+" Porcentaje de la pila: "+String.valueOf(level));
				 
				 if (level <= 15) {
					 String numberPhone="5527274918";
					 SmsManager smsManager = SmsManager.getDefault();
			         String messageError="gestopagoDroid tiene la pila baja " + level;
					 smsManager.sendTextMessage(numberPhone, null, messageError, null, null);
					 
				}
			 }

			 };
			 
			 public void appendLog(String text)
				{       
					Calendar cal = Calendar.getInstance();
					Date now = new Date();
					cal.setTime(now);
					File logFile = new File("sdcard/log/battery.txt"); 
				   //File logFile = new File("sdcard/log/log_write.txt");
				   if (!logFile.exists())
				   {
				      try
				      {
				         logFile.createNewFile();
				      } 
				      catch (IOException e)
				      {
				         // TODO Auto-generated catch block
				         e.printStackTrace();
				      }
				   }
				   try
				   {
				      //BufferedWriter for performance, true to set append to file flag
				      BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
				      buf.append(text);
				      buf.newLine();
				      buf.close();
				   }
				   catch (IOException e)
				   {
				      // TODO Auto-generated catch block
				      e.printStackTrace();
				   }
				}
	
}
