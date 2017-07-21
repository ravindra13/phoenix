package com.example.android.phoenix.fcm;

import android.content.ContentValues;
import android.util.Log;

import com.example.android.phoenix.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import it.slyce.messaging.message.MessageSource;
import it.slyce.messaging.message.TextMessage;

import com.example.android.phoenix.messages.msgContract;


import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.android.phoenix.MainActivity.m;


public class phoenixFirebaseReceive extends FirebaseMessagingService {


    private static final long MINUTE_MILLIS = 1000 * 60;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("dd MMM");

    private static String LOG_TAG = phoenixFirebaseReceive.class.getSimpleName();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with FCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options\

        // The Squawk server always sends just *data* messages, meaning that onMessageReceived when
        // the app is both in the foreground AND the background

        Log.d(LOG_TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.

        Map<String, String> data = remoteMessage.getData();

        if (data.size() > 0) {
            Log.d(LOG_TAG, "Message data payload: " + data);
            String msg = data.get("message");
            String user = data.get("user");
            String profilePic = data.get("profilePic");
            String time = data.get("time");
            String place = data.get("place");
            MainActivity.m.put(user,place);
            // Get the date for displaying
            long dateMillis = Long.parseLong(time);




            Log.i("what the big fuss about",time);
            // String ra = System.currentTimeMillis();
            //  if(user.equals(MainActivity.user))return;
            TextMessage nmsg = new TextMessage();
            nmsg.setText(msg);
            nmsg.setDate(dateMillis);
            nmsg.setUserId(user);
            nmsg.setDisplayName(user);
            nmsg.setAvatarUrl(profilePic);
            nmsg.setSource(MessageSource.EXTERNAL_USER);
            MainActivity.slyceMessagingFragment.addNewMessage(nmsg);
            Log.i("which dbbbbbbbb",msgContract.messageentry.CONTENT_URI.toString());
            // Insert a new place into DB
            ContentValues contentValues = new ContentValues();
            contentValues.put(msgContract.messageentry.COLUMN_USER, data.get("user"));
            contentValues.put(msgContract.messageentry.COLUMN_MESSAGE, data.get("message"));
            contentValues.put(msgContract.messageentry.COLUMN_PIC, data.get("profilePic"));
            contentValues.put(msgContract.messageentry.COLUMN_TIME, data.get("time"));
            getContentResolver().insert(msgContract.messageentry.CONTENT_URI, contentValues);


//             Send a notification that you got a new message
//            sendNotification(data);
//            insertSquawk(data);


        }
    }

}
