package com.example.android.phoenix;



import com.example.android.phoenix.messages.msgContract;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import it.slyce.messaging.SlyceMessagingFragment;
import it.slyce.messaging.listeners.UserClicksAvatarPictureListener;
import it.slyce.messaging.listeners.UserSendsMessageListener;
import it.slyce.messaging.message.MessageSource;
import it.slyce.messaging.message.TextMessage;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    public static SlyceMessagingFragment slyceMessagingFragment;
    CallbackManager callbackManager;
    LoginButton loginButton;
    String url = "https://fcm.googleapis.com/fcm/send";
    AccessToken accessToken;
    ProfileTracker profileTracker;
    Profile profile;
    Uri profilePicUri;
    public static String user;
    String TAG = "googleapi";
    int messageLoaded = 0;
    String currentLocation ="";

    private GoogleApiClient mGoogleApiClient;

    public static Map <String,String> m = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        login();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)         //this /* FragmentActivity */  //this /* OnConnectionFailedListener */
                .build();
        refreshLocation();


    }







    void login(){

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                setContentView(R.layout.activity_main);
                init();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

                // display error
                String toastMessage = e.getMessage();
                Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_LONG).show();

            }
        });

        accessToken = AccessToken.getCurrentAccessToken();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged (Profile oldProfile, Profile currentProfile) {
                if (currentProfile != null) {

                    profile = currentProfile;

                    user = profile.getFirstName();
                    profilePicUri = profile.getProfilePictureUri(100, 100);
                    slyceMessagingFragment.setDefaultDisplayName(user);
                    slyceMessagingFragment.setDefaultAvatarUrl(profilePicUri.toString());
                    Log.i("This second one","is called");
                    Log.i("asdfghjkl", AccessToken.getCurrentAccessToken().getToken());
                    if(oldProfile==null)Log.i("null","nulle insaan");
                    else Log.i("old profile ka",oldProfile.getName());

                    if(currentProfile==null)Log.i("null","nulle insaan current vaale");
                    else Log.i("old profile ka",currentProfile.getName());

                    if(messageLoaded == 0)
                        msgLoader();                            //it was getting called twice, so have to use messageLoaded
                    Log.i("here","we comes again");
                }
            }
        };

        if (accessToken != null) {
            setContentView(R.layout.activity_main);
            init();
            Log.i("This one","is called");
            msgLoader();                            //can we called from init by checking whether profile null or not
        }                                           //For God sake check databases
        //For contentresolver
        //see google map app surimatcher
        //Actually no need of multiple content provider until two different entities

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                           //Remove logout button from login screen by checking access token
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {

            accessToken = AccessToken.getCurrentAccessToken();

            if(accessToken==null) {

                return true;
            }

            LoginManager.getInstance().logOut();
            messageLoaded = 0;

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;

        }


        return super.onOptionsItemSelected(item);
    }

    void init(){



        // If there is an access token then Login Button was used
        // Check if the profile has already been fetched
        Profile currentProfile = Profile.getCurrentProfile();
        if (currentProfile != null) {

            profile = currentProfile;
            user = profile.getFirstName();
            profilePicUri = profile.getProfilePictureUri(100, 100);
            Log.i("its not","emptyyyyyyyyyyyyyyy");

        }
        else {
            // Fetch the profile, which will trigger the onCurrentProfileChanged receiver
            Profile.fetchProfileForCurrentAccessToken();
            Log.i("it was","emptyyyyyyyyyyyyy");
            if(Profile.getCurrentProfile()==null)Log.i("it was  still ","emptyyyyyyyyyyyyuuuuuuuuuuy");
            else Log.i("not","empty this time");
//            profile = Profile.getCurrentProfile();
//            user = profile.getFirstName();
//            profilePicUri = profile.getProfilePictureUri(100, 100);

        }



        slyceMessagingFragment = (SlyceMessagingFragment) getFragmentManager().findFragmentById(R.id.messaging_fragment);
        if(profilePicUri!=null)
            slyceMessagingFragment.setDefaultAvatarUrl(profilePicUri.toString());
        if(user!=null)
            slyceMessagingFragment.setDefaultDisplayName(user);

        FirebaseMessaging.getInstance().subscribeToTopic("friends");


        slyceMessagingFragment.setUserClicksAvatarPictureListener(new UserClicksAvatarPictureListener() {
            @Override
            public void userClicksAvatarPhoto(String userId) {

                String h = "";
                if(m.containsKey(userId))h=m.get(userId);
                String s = "Message sent from " + h;
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();

            }
        });

        slyceMessagingFragment.setOnSendMessageListener(new UserSendsMessageListener() {

            @Override
            public void onUserSendsTextMessage(String text) {

                if(connectivity()) {
                    // Insert a new place into DB

//                    ContentValues contentValues = new ContentValues();
//                    contentValues.put(msgContract.messageentry.COLUMN_USER, user);                                //solve this problem
//                    contentValues.put(msgContract.messageentry.COLUMN_MESSAGE, text);
//                    contentValues.put(msgContract.messageentry.COLUMN_PIC, profilePicUri.toString());
//                    contentValues.put(msgContract.messageentry.COLUMN_TIME, String.valueOf(System.currentTimeMillis()));      //I think no need of stringvalueof
//                    getContentResolver().insert(msgContract.messageentry.CONTENT_URI, contentValues);
                    sendMessage(text);
                }

            }

            @Override
            public void onUserSendsMediaMessage(Uri imageUri) {
                Log.d("inf", "******************************** " + imageUri);
            }
        });
//
        //    msgLoader();
    }


    /***
     * Called when the Google API Client failed to connect to Google Play Services
     *
     * @param result A ConnectionResult that can be used for resolving the error
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.e(TAG, "API Client Connection Failed!");
    }

    public void refreshLocation() {


        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);


        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {

                String q = "";
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {

                    q = placeLikelihood.getPlace().getName().toString();                                    //Idon't know how to select firstone
                    Log.i(TAG, String.format("Place '%s' has likelihoooooooooooooooooooooood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                    break;
                }
                m.put(user,q);
                currentLocation = q;
                likelyPlaces.release();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public boolean connectivity(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo!=null){
            if(networkInfo.isConnected())return true;
            return false;
        }
        return false;
    }


    void msgLoader(){

        messageLoaded = 1;

        Log.i("This much","tymmmmmmmmmmmmmmm is called");
        Uri uri = msgContract.messageentry.CONTENT_URI;
        Cursor data = getContentResolver().query(
                uri,
                null,
                null,
                null,
                null);

        if (data == null || data.getCount() == 0) return;
        while (data.moveToNext()) {

            String u = data.getString(data.getColumnIndex(msgContract.messageentry.COLUMN_USER));
            String m = data.getString(data.getColumnIndex(msgContract.messageentry.COLUMN_MESSAGE));
            String p = data.getString(data.getColumnIndex(msgContract.messageentry.COLUMN_PIC));
            String t = data.getString(data.getColumnIndex(msgContract.messageentry.COLUMN_TIME));
            TextMessage nmsg = new TextMessage();
            nmsg.setText(m);
            nmsg.setUserId(u);
            nmsg.setAvatarUrl(p);
            long tym = Long.parseLong(t);
            nmsg.setDate(tym);
            if(u.equals(user)){
                nmsg.setSource(MessageSource.LOCAL_USER);
//                if(profilePicUri!=null)
//                nmsg.setAvatarUrl(profilePicUri.toString());
            }
            else {
                nmsg.setSource(MessageSource.EXTERNAL_USER);
            }

            slyceMessagingFragment.addNewMessage(nmsg);
        }

        data.close();
    }

    public void sendMessage(String message){                        //msg leak, buffered msgs and net down again before sendian all

        JSONObject json = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("message", message);
            data.put("user", user);
            data.put("profilePic", profilePicUri.toString());
            data.put("time", System.currentTimeMillis());
            data.put("place", currentLocation);
            json.put("data",data);
            json.put("to","/topics/friends");
        }catch (JSONException jx){
            Log.e("Json error",message + "cant be sent");
        }

        final String requestBody = json.toString();

        StringRequest strRequest = new StringRequest
                (Request.Method.POST, url,  new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.i("Response: ",  response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.i("Error: ",  error.toString());

                    }
                }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=AIzaSyCLjXHewCFbbK78MBRXff0FRvNB4cQQcv4");
                return headers;
            }


            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };

        AppController.getInstance().addToRequestQueue(strRequest,"newly added");
    }


}
