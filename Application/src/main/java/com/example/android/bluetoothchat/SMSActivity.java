package com.example.android.bluetoothchat;

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

public class SMSActivity extends Activity {
    String SENT_SMS_ACTION="SENT_SMS_ACTION";
    String DELIVERED_SMS_ACTION="DELIVERED_SMS_ACTION";
    private BluetoothAdapter bta = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bta = BluetoothAdapter.getDefaultAdapter();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

    }
    public void send(View v) {

        if (isMobileConnected(getBaseContext()))
        {
            Toast.makeText(getBaseContext(),
                    "Cellular network connected successfully",
                    Toast.LENGTH_SHORT).show();
            EditText telNumberText = (EditText)findViewById(R.id.telNumber);
            EditText msgContentText = (EditText)findViewById(R.id.msgContent);

            String telNumber = telNumberText.getText().toString();
            String msgContent = msgContentText.getText().toString();
            if(validate(telNumber, msgContent))
            {
                sendSMS(telNumber, msgContent);
            }

        } else {
            Toast.makeText(getBaseContext(),
                    "Please check the cellular connectivity",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void addPerson (View v) {
        Intent intent = new Intent(SMSActivity.this, VictimScreen.class);
        Random rand = new Random();
        int n = rand.nextInt(999999)+1;
        bta.setName("VICTIM_" + Integer.toString(n));
        startActivity(intent);
        finish();
    }




    //Send SMS @param phoneNumber @param messageText
    public boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo.State mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            if (mMobileNetworkInfo == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    private void sendSMS(String phoneNumber, String messageText) {
        // create the sendIntent parameter
        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, sentIntent, 0);
        // create the deliverIntent parameter
        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        PendingIntent deliverPI = PendingIntent.getBroadcast(this, 0, deliverIntent, 0);

        SmsManager sms = SmsManager.getDefault();
        if (messageText.length() > 70) {
            List<String> msgs = sms.divideMessage(messageText);
            for (String msg : msgs) {
                sms.sendTextMessage(phoneNumber, null, msg, sentPI, deliverPI);
            }
        } else {
            sms.sendTextMessage(phoneNumber, null, messageText, sentPI, deliverPI);
        }
        Toast.makeText(SMSActivity.this, "Sent successfully!", Toast.LENGTH_LONG).show();

        //register the Broadcast Receivers
        registerReceiver(new BroadcastReceiver(){
                             @Override
                             public void onReceive(Context context,Intent _intent)
                             {
                                 switch(getResultCode()){
                                     case Activity.RESULT_OK:
                                         Toast.makeText(getBaseContext(),
                                                 "SMS sent success actions",
                                                 Toast.LENGTH_SHORT).show();
                                         break;
                                     case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                         Toast.makeText(getBaseContext(),
                                                 "SMS generic failure actions",
                                                 Toast.LENGTH_SHORT).show();
                                         break;
                                     case SmsManager.RESULT_ERROR_RADIO_OFF:
                                         Toast.makeText(getBaseContext(),
                                                 "SMS radio off failure actions",
                                                 Toast.LENGTH_SHORT).show();
                                         break;
                                     case SmsManager.RESULT_ERROR_NULL_PDU:
                                         Toast.makeText(getBaseContext(),
                                                 "SMS null PDU failure actions",
                                                 Toast.LENGTH_SHORT).show();
                                         break;
                                 }
                             }
                         },

                new IntentFilter(SENT_SMS_ACTION));
        registerReceiver(new BroadcastReceiver(){
                             @Override
                             public void onReceive(Context _context,Intent _intent)
                             {
                                 Toast.makeText(getBaseContext(),
                                         "SMS delivered actions",
                                         Toast.LENGTH_SHORT).show();
                             }
                         },
                new IntentFilter(DELIVERED_SMS_ACTION));
    }

    public boolean validate(String telNo, String content){

        if((null==telNo)||("".equals(telNo.trim()))){
            Toast.makeText(this, "Please input the telephone number!",Toast.LENGTH_LONG).show();
            return false;
        }
        if(!checkTelNo(telNo)){
            Toast.makeText(this, "Please input the correct telephone number!",Toast.LENGTH_LONG).show();
            return false;
        }
        if((null==content)||("".equals(content.trim()))){
            Toast.makeText(this, "Please input the message content!",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public boolean checkTelNo(String telNo){
        if("5556".equals(telNo)){
            return true;
        }else{
            String reg ="^0{0,1}(13[0-9]|15[0-9])[0-9]{8}$";
            return telNo.matches(reg);
        }
    }
}
