package com.udacity.gradle.builditbigger.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;


class NetworkUtilities {
    private final static String TAG = "LEE: <" + NetworkUtilities.class.getSimpleName() + ">";
    private static boolean firstTime = true;
    private static boolean isConnected = true; // assume online to begin
    private static boolean haveConnectedWifi = false;
    private static boolean haveConnectedMobile = false;
    private static Context sContext;

    private static void init() {
        if (firstTime) {
            firstTime = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean available = isNetworkAvailable(); // network ops must never be on the main thread
                    Log.v(TAG, "network available="+available);
                }
            }).start();
        }
    }

    // first call not reliable - as isNetworkAvailable() will complete in the background
    public static boolean isConnected(Context context) {
        sContext = context;
        init();
        return isConnected;
    }

/*
    public static boolean isHaveConnectedWifi() {
        init();
        return haveConnectedWifi;
    }

    public static boolean isHaveConnectedMobile() {
        init();
        return haveConnectedMobile;
    }
*/

    // from http://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    private static boolean isNetworkAvailable() {
        if (sContext == null) {
            Log.v(TAG, "isNetworkAvailable - sContext is null!");
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            // from: http://stackoverflow.com/questions/32242384/getAllNetworkInfo-is-deprecated-how-to-use-getAllNetworks-to-check-network
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = cm.getAllNetworks();
                NetworkInfo ni;
                for (Network network : networks) {
                    ni = cm.getNetworkInfo(network);
                    if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                        if (ni.isAvailable() && ni.isConnectedOrConnecting()) {
                            Log.v(TAG, "haveConnectedWifi = true");
                            haveConnectedWifi = true;
                        }
                    } else if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                        if (ni.isAvailable() && ni.isConnectedOrConnecting()) {
                            Log.v(TAG, "haveConnectedMobile = true");
                            haveConnectedMobile = true;
                        }
                    }
                }
            }
            else {
                @SuppressWarnings("deprecation") NetworkInfo[] netInfo = cm.getAllNetworkInfo();
                if (netInfo != null) {
                    for (NetworkInfo ni : netInfo) {
                        if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                            if (ni.isAvailable() && ni.isConnectedOrConnecting()) {
                                Log.v(TAG, "haveConnectedWifi = true");
                                haveConnectedWifi = true;
                            }
                        } else if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                            if (ni.isAvailable() && ni.isConnectedOrConnecting()) {
                                Log.v(TAG, "haveConnectedMobile = true");
                                haveConnectedMobile = true;
                            }
                        }
                    }
                }
            }
        }
        isConnected = (haveConnectedWifi || haveConnectedMobile) && isNetworkWorking();
        Log.v(TAG, "isConnected=" + isConnected);
        return isConnected;
    }

    // ensure we actually have connectivity, not just an active network interface
    private static boolean isNetworkWorking() {
        boolean workingNetwork = false;
        URL url;
        HttpURLConnection urlConnection = null;
        String site = "http://www.google.com";
        try {
            url = new URL(site);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);
            int data = isw.read();
            while (data != -1) {
                //char current = (char) data;
                //System.out.print(current);
                data = isw.read();
            }
            workingNetwork = true;
        } catch (ConnectException e) {
            Log.e(TAG, "problem connecting to " + site + " - error=" + e);
        } catch (IOException e) {
            Log.e(TAG, "problem reading from " + site + " - error=" + e);
        } catch (Exception e) {
            Log.e(TAG, "exception while connecting to " + site + " - error=" + e);
        } finally {
            try {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e(TAG, "problem disconnecting from " + site + " - error=" + e);
            }
        }
        return workingNetwork;
    }

}
