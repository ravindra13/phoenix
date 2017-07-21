package com.example.android.phoenix.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.example.android.phoenix.MainActivity;

import it.slyce.messaging.message.MessageSource;
import it.slyce.messaging.message.TextMessage;


public class NetworkChange extends BroadcastReceiver {

    MainActivity m;

    public NetworkChange(){
        m = new MainActivity();
    }
    /******I don't want to make object of mainActivity. any other option********/
    @Override
    public void onReceive(Context context, Intent intent){
//        if(m.connectivity()){
//            Uri uri = messagecontract.messageentry.CONTENT_URI;
//            Cursor data = m.getContentResolver().query(
//                    uri,
//                    null,
//                    null,
//                    null,
//                    null);
//
//            if (data == null || data.getCount() == 0) return;
//            while (data.moveToNext()) {
//                //String s =  data.getColumnIndex(msgContract.messageentry.COLUMN_USER);
//                String usr = data.getString(data.getColumnIndex(messagecontract.messageentry.COLUMN_USER));
//                String msg = data.getString(data.getColumnIndex(messagecontract.messageentry.COLUMN_MESSAGE));
//
//                m.sendmessage(msg,usr);
//            }
//        }
//        Uri uri = messagecontract.messageentry.CONTENT_URI;
//        m.getContentResolver().delete(uri,null,null);
    }


//    boolean connectivity(){
//        ConnectivityManager connMgr = (ConnectivityManager)
//                getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//        if(networkInfo != null && networkInfo.isConnected())return true;
//        else return  false;
//    }
}
