package com.androidexample.broadcastreceiver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;


public class IncomingSms extends BroadcastReceiver {
	
	// Get the object of SmsManager
	final SmsManager sms = SmsManager.getDefault();
	
	Date now = new Date();
	
	
	public void onReceive(Context context, Intent intent) {
	
		// Retrieves a map of extended data from the intent.
		final Bundle bundle = intent.getExtras();
		String logString = "";
		String senderNum="";
		String message="";
		String[] parts;
		String carrier="";
		String token="";
		
		try {
			
			if (bundle != null) {
				
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
				
				for (int i = 0; i < pdusObj.length; i++) {
					
					SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
					String phoneNumber = currentMessage.getDisplayOriginatingAddress();
					
			        senderNum = phoneNumber;
			        
			     
			        
			        
			        if (senderNum.length() > 8) {
			        	
			        	appendLog("["+now.toString()+"] Phone ["+phoneNumber+"] " +  currentMessage );
			        	
				        message = currentMessage.getDisplayMessageBody();
				        parts = message.split(" ");
				        
				        if ( parts.length < 2  ){
				        	
				        	appendLog("["+now.toString()+"] Phone ["+phoneNumber+"] [" +  currentMessage + "] Cadena o Token no valido" );
				        	SmsManager smsManager = SmsManager.getDefault();
				        	String messageError="Cadena o Token no valido";
							smsManager.sendTextMessage(senderNum, null, messageError, null, null);
							
				        }else{
				        	
				        	
				        	    carrier = parts[0].toLowerCase(); 
						        token = parts[1];
						      //logString = "telefono {22222} Token {BABSB} Carrier {uinefoin} Recibido: {timestamp} | ";
						        logString = "["+now.toString()+"] Phone ["+phoneNumber+"] | "+"Token ["+token+"] | "+" Carrier ["+carrier+"] @@ ";
						        
					        	if(
						        		(carrier.compareTo("telcel")   == 0  ||
						        		carrier.compareTo("unefon")    == 0  ||
						        		carrier.compareTo("iusacell")  == 0	 ||
						        		carrier.compareTo("nextel")    == 0	 ||
						        		carrier.compareTo("movistar")  == 0) ){
						        		
						        		//String url="http://68.169.52.238/BuyTime/service/abonar.do?carrier="+carrier+"&telefono="+phoneNumber+"&token="+token+"&nocache="+now.getTime();
						        		
						        	
						        		String url="http://192.168.1.105:8080/BuyTime/service/smsPromo.do?carrier="+carrier+"&telefono="+phoneNumber+"&token="+token+"&nocache="+now.getTime();
						        	
						        		//String url="http://68.169.52.238/BuyTime/service/smsPromo.do?carrier="+carrier+"&telefono="+phoneNumber+"&token="+token+"&nocache="+now.getTime();
						        	
						        		//appendLog("Antes de enviar...");
						        		
								        new HttpAsyncTask().execute( url , logString, phoneNumber );
						        }else{
						        	logString += "| Cadena o Token no valido";
						        	appendLog(logString);
						        	
						        	SmsManager smsManager = SmsManager.getDefault();
						        	String messageError="Operadora ["+ carrier + "] no valida";
									smsManager.sendTextMessage(senderNum, null, messageError, null, null);
						        }
				        	
				        	
				        	
				        }
				        
				        
				       
					} 
			        
			        
					
				} // end for loop
              } // bundle is null

		} catch (Exception e) {
			appendLog( "[onReceive] "+ e.getMessage() );
			appendLog( "Error en token: "+token +"" + e.getMessage() );
		}
	}

	public void appendLog(String text)
	{       
		Calendar cal = Calendar.getInstance();
		Date now = new Date();
		cal.setTime(now);
		File logFile = new File("sdcard/log/"+ cal.get(Calendar.DATE)+"_"+ ( cal.get(Calendar.MONTH) + 1 )+"_" + cal.get(Calendar.YEAR) + ".txt"); 
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
	 
	public   String GET(String url, String senderNum) {
		InputStream inputStream = null;
		String result = "";
		try {
			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();
			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";
		} catch (Exception e) {
			//Log.d("InputStream", e.getLocalizedMessage());
			appendLog( "[GET] "+ e.getMessage() );
			result = "0,Problema de conexion intentar mas tarde";
		}
		return result;
	}
		    private   String convertInputStreamToString(InputStream inputStream) throws IOException{
		        
		    	String line = "";
		        String result = "";
		        
		        try{
		        	BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
				    
		        	while((line = bufferedReader.readLine()) != null)
			            result += line;
			        
			        inputStream.close();
			        
		        }catch(Exception e){
		        	result = "0,Problema de conexion intentar mas tarde"; 
		        	appendLog( "[convertInputStreamToString] "+ e.getMessage() );
		        }
		       
		        
		        
		        return result;
		        
		    }
		    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		        
		    	String URL = ""; 
		    	String LOG =  ""; 
		    	String SENDER = ""; 
		    	
		    	@Override
		        protected String doInBackground(String... params) {
		    		
		    		//appendLog("[doInBackground] recibimos parametros");
		            
		    		URL = params[0];
		            LOG = params[1];
		            SENDER = params[2];
		             
		            /*
		    		appendLog("URL ["+URL+"]");
		    		appendLog("LOG ["+LOG+"]");
		    		appendLog("SENDER ["+SENDER+"]");
		    		*/
			    		
		             String result = ""; 
		             try{
		            	 result = GET( URL , SENDER  );
		             }catch(Exception e ){
		            	 result = "0,Problema de conexion intentar mas tarde"; 
		             }
		             
		             return result; 
		        }
		    	
		        // onPostExecute displays the results of the AsyncTask.
		        @Override
		        protected void onPostExecute(String result) {
		       
		        	String[] parts = result.split(",");
			        String id = parts[0]; 
			        String mensaje = parts[1];
			        Date now = new Date();
			        
			        if (id.compareTo("0") == 0 ) {
					
			        	SmsManager smsManager = SmsManager.getDefault();
			        	smsManager.sendTextMessage(SENDER, null, mensaje, null, null);
	
					}
			        
			        LOG += mensaje + " | response: [" + now.toString()+"]" ;
					appendLog( LOG );
		        	
			        
		       }
		    }
		    
		    

	
}