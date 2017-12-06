package com.example.android.bluetoothchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class ReceiverDemo extends BroadcastReceiver {

    private static final String strRes = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // TODO Auto-generated method stub
        if(strRes.equals(arg1.getAction())){
            StringBuilder sb = new StringBuilder();
            Bundle bundle = arg1.getExtras();
            if(bundle!=null){
                Object[] pdus = (Object[])bundle.get("pdus");
                SmsMessage[] msg = new SmsMessage[pdus.length];
                for(int i = 0 ;i<pdus.length;i++){
                    msg[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                }

                for(SmsMessage curMsg:msg){
                    sb.append("from:【");
                    sb.append(curMsg.getDisplayOriginatingAddress());
                    sb.append("】Content：");
                    sb.append(curMsg.getDisplayMessageBody());
                }
                Toast.makeText(arg0,
                        "Got a message " + sb.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

}
