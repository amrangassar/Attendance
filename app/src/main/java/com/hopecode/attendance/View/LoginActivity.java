package com.hopecode.attendance.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.hopecode.attendance.R;
import com.hopecode.attendance.SingleShotLocationProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LoginActivity extends AppCompatActivity {


    private static final int REQUEST_CODE = 100;
    EditText edtName, edtId;
    boolean userFound = false;
    boolean outCompany = false;
    static int dist = -1;
    public static float longitude = -1;
    public static float latitude = -1;
    boolean networkStatus = false;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    private RequestQueue mQueue;
    //SingleShotLocationProvider Provider = new SingleShotLocationProvider();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.e("onCreate", "onCreate: ");
        handleSSLHandshake();
        //isNetworkConnected();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (!canGetLocation()) {
                if (!preferences.getString("logout", "").equals("logoutStatus")) {
                    buildAlertMessageNoGps();
                } else {
                    editor.putString("logout", "");
                    editor.apply();
                }
            } else {
                foo(LoginActivity.this);
            }
        } else {
            if (statusCheck()) {
                if (!preferences.getString("logout", "").equals("logoutStatus")) {
                    buildAlertMessageNoGps();
                } else {
                    editor.putString("logout", "");
                    editor.apply();
                }
            } else {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            }
        }

        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        edtName = (EditText) findViewById(R.id.edtName);
        edtId = (EditText) findViewById(R.id.edtId);
        mQueue = Volley.newRequestQueue(this);

//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        if (!(preferences.getString("userid", "").equals(""))) {

            Intent userintent = new Intent(getApplicationContext(), HomeActivity.class);
//           userintent.putExtra("userkind", "emp");
            editor.putString("userkind", "emp");
            editor.apply();
            startActivity(userintent);
            finish();

        } else if (preferences.getString("adminstate", "").equals("1")) {

            Intent adminintent = new Intent(getApplicationContext(), HomeActivity.class);
//           adminintent.putExtra("userkind", "admin");
            editor.putString("userkind", "admin");
            editor.apply();
            startActivity(adminintent);
            finish();

        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (statusCheck()) {
                            buildAlertMessageNoGps();

                        }
                        {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE);
                            } else {
                                buildLocationRequest();
                                buildLocationCallBack();
                                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

                                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                        != PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                        != PackageManager.PERMISSION_GRANTED) {

                                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            REQUEST_CODE);

                                    return;
                                }
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                            }
                        }
                    }
                } else {
                }
            }
        }
    }

    public void btnLogin(View view) {

        if (!isNetworkConnected()) {
            Toast.makeText(this, "Please open network", Toast.LENGTH_SHORT).show();
            //isNetworkConnected();

        } else {

            if (edtName.getText().toString().equals("admin") && edtId.getText().toString().equals("000")) {
                sharedPrefAdmin();
                Intent adminintent = new Intent(getApplicationContext(), HomeActivity.class);
//            adminintent.putExtra("userkind", "admin");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userkind", "admin");
                editor.apply();
                startActivity(adminintent);
                finish();
            } else {
                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    Log.e("btnLogin: ", "KITKAT");
                    if (!canGetLocation()) {
                        buildAlertMessageNoGps();
                        //Toast.makeText(this, "Please open location!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("btnLoginkitkat: ", dist + "");
                        if (dist == -1) {
                            foo(LoginActivity.this);
                            //btnLogin(view);
                            Toast.makeText(LoginActivity.this, "pleas Login again!", Toast.LENGTH_LONG).show();
                        } else if (dist == 1) {
                            Log.e("distancebol", dist + "");
                            getLoginOrReport();
                            outCompany = false;
                        } else if (dist == 0) {
                            outCompany = true;
                            // Toast.makeText(LoginActivity.this, "You aren't in your company!", Toast.LENGTH_LONG).show();
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Company")
                                    .setMessage("you aren't in your company!")
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                        }
                    }
                } else {
                    if (statusCheck()) {
                        buildAlertMessageNoGps();
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_CODE);
                        } else {
                            buildLocationRequest();
                            buildLocationCallBack();
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE);

                                return;
                            }
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        }
                    }
                }
            }
        }
    }

    public void getLoginOrReport() {

        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage("please waiting....");
        dialog.show();

        String url = "https://karimaster97.000webhostapp.com/college/getLogin.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        try {
                            //  dialog.dismiss();
//                            String android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
//                                    Settings.Secure.ANDROID_ID);

                            JSONArray jsonArray = response.getJSONArray("Login");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject login = jsonArray.getJSONObject(i);

                                int USER_ID = login.getInt("USER_ID");
                                String NAME = login.getString("NAME");
                                String DEVICE_ID = login.getString("DEVICE_ID");

                                if (edtName.getText().toString().equals(NAME) && edtId.getText().toString().equals(String.valueOf(USER_ID))
                                        ) {

                                    if (outCompany == true) {
                                        userFound = true;
                                        Log.e("onResponse: ", "break");
                                        break;
                                    }
                                    Log.e("onResponse: ", "break22222222");
                                    userFound = true;
                                    Log.e("outCompany: ", outCompany + "");
                                    sharedPrefUser(USER_ID + "", NAME);
                                    Intent userintent = new Intent(getApplicationContext(), HomeActivity.class);
                                    //userintent.putExtra("userkind", "emp");
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                    final SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("userkind", "emp");
                                    editor.apply();
                                    Log.e("getLoginOrReport: ", "emp");
                                    startActivity(userintent);
                                    finish();
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (outCompany == true && userFound == true) {
                            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Company")
                                    .setMessage("you are not in your company!")
                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();
//                            Toast.makeText(LoginActivity.this, "Please enter correct information!", Toast.LENGTH_SHORT).show();
//                            Log.e("outCompany: ", outCompany + "");
                        } else if (userFound == false) {
                            Log.e("userFound: ", userFound + "");
                            Toast.makeText(LoginActivity.this, "please enter correct information!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private void sharedPrefUser(String userid, String username) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd ");
        Date date = new Date();
        Log.e("logout: ", dateFormat.format(date));
        Log.e("logout: ", dateFormat2.format(date));

        editor.putString("userid", userid);
        editor.putString("username", username);
        editor.putString("date", dateFormat2.format(date));
        editor.putString("starttime", dateFormat.format(date));
        editor.apply();


        Log.e("logout: ", preferences.getString("userid", ""));
        Log.e("logout: ", preferences.getString("username", ""));
        Log.e("logout: ", preferences.getString("date", ""));
        Log.e("logout: ", preferences.getString("starttime", ""));


    }

    private void sharedPrefAdmin() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("adminstate", "1");
        editor.apply();

        Log.e("logout: ", preferences.getString("userid", "1"));

    }

    public boolean statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            return true;
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildLocationCallBack() {
        final ProgressDialog dialogLocation = new ProgressDialog(LoginActivity.this);
        dialogLocation.setMessage("Getting location....");
        dialogLocation.show();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                dialogLocation.dismiss();
//                double Longitude, Latitude;
//                Longitude = locationResult.getLastLocation().getLongitude();
//                Latitude = locationResult.getLastLocation().getLatitude();

                Location loc1 = new Location("");
                loc1.setLatitude(locationResult.getLastLocation().getLatitude());
                loc1.setLongitude(locationResult.getLastLocation().getLongitude());

                Location loc2 = new Location("");
//                ///bahaa
//                loc2.setLatitude(29.987656);
//                loc2.setLongitude(31.250024);
                //com
                loc2.setLatitude(29.96493141);
                loc2.setLongitude(31.25023778);


                //me
//                loc2.setLatitude(28.986864);
//                loc2.setLongitude(30.8714402);

                Log.e("onSuccess: ", locationResult.getLastLocation().getLatitude() + "");
                Log.e("onSuccess: ", locationResult.getLastLocation().getLongitude() + "");
                Log.e("onSuccess: ", loc1.distanceTo(loc2) + "");

                // Toast.makeText(LoginActivity.this, locationResult.getLastLocation().getLatitude() + "  " +
                //  locationResult.getLastLocation().getLongitude() + "  " +
                //  loc1.distanceTo(loc2) + "  ", Toast.LENGTH_SHORT).show();
                //dialogLocation.dismiss();
                if (loc1.distanceTo(loc2) <= 100) {
                    dist = 1;
                    outCompany = false;
                    Log.e("distanceTo true: ", loc1.distanceTo(loc2) + "   " + outCompany);
                    getLoginOrReport();
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    // Log.e("onLocationResult: ", "getLoginOrReport");
                    // Log.e("11111111111111", loc1.distanceTo(loc2) + "");
                } else {
                    dist = 0;
                    Log.e("distanceTo false: ", loc1.distanceTo(loc2) + "   " + outCompany);

                    outCompany = true;
                    getLoginOrReport();
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//                        new AlertDialog.Builder(LoginActivity.this)
//                                .setTitle("Company")
//                                .setMessage("you are not in your company!")
//                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                }).create().show();
                }
                // Log.e("11111111111111", loc1.distanceTo(loc2) + "");
//                loc2.setLatitude(28.9868543);//                loc2.setLongitude(30.8714177);//                30.8713226  28.986925
//                Log.e("onLocationResult: ", Build.SERIAL);
//
//                Log.e("onLocationResult: ", Settings.Secure.getString(getApplicationContext().getContentResolver(),
//                        Settings.Secure.ANDROID_ID));
            }
        };
    }

    @SuppressLint("RestrictedApi")
    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(10);
    }

    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

    public void foo(Context context) {

        SingleShotLocationProvider.requestSingleUpdate(context,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        Log.d("Location", "my location is " + location.toString());

                    }
                });
    }

    public static class SingleShotLocationProvider {

        public static void requestSingleUpdate(final Context context, final LocationCallback callback) {
            final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestSingleUpdate(criteria, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        callback.onNewLocationAvailable(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                }, null);
            } else {
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (isGPSEnabled) {
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    locationManager.requestSingleUpdate(criteria, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            callback.onNewLocationAvailable(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    }, null);
                }
            }
        }

        public interface LocationCallback {
            public void onNewLocationAvailable(GPSCoordinates location);
        }

        // consider returning Location instead of this dummy wrapper class
        public static class GPSCoordinates {

            public GPSCoordinates(double theLatitude, double theLongitude) {

                longitude = (float) theLongitude;
                latitude = (float) theLatitude;
                Location loc1 = new Location("");
                loc1.setLatitude(latitude);
                loc1.setLongitude(longitude);

                Location loc2 = new Location("");

//                //bahaa
//                loc2.setLatitude(29.987656);
//                loc2.setLongitude(31.250024);
                //com
                loc2.setLatitude(29.96493141);
                loc2.setLongitude(31.25023778);


                //me
//                loc2.setLatitude(28.986864);
//                loc2.setLongitude(30.8714402);

                if (loc1.distanceTo(loc2) <= 100) {
                    //   getLoginOrReport();
                    dist = 1;

                    Log.e("GPSCoordinates: ", longitude + "  " + latitude + "\n distance" + loc1.distanceTo(loc2)
                            + "  " +
                            dist);

                } else {
                    dist = 0;
                }
                Log.e("GPSCoordinates: ", longitude + "  " + latitude + "\n distance" + loc1.distanceTo(loc2) + "");

            }
        }

    }

    public boolean canGetLocation() {
        boolean result = true;
        LocationManager lm = null;
        boolean gps_enabled = false;
        if (lm == null)

            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {

        }

        if (gps_enabled == false) {
            result = false;
        } else {
            result = true;
        }
        return result;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() == null) {
            Log.e("isInternetAvailable: ", "false");
            return false;
        }
        Log.e("isInternetAvailable: ", "true");
        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.e("on resume", "enter onresume");
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {

            foo(LoginActivity.this);
        } else {

        }
    }
}

//package com.hopecode.attendance.View;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.net.ConnectivityManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Looper;
//import android.preference.PreferenceManager;
//import android.provider.Settings;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.hopecode.attendance.R;
//import com.hopecode.attendance.SingleShotLocationProvider;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.net.InetAddress;
//import java.security.SecureRandom;
//import java.security.cert.X509Certificate;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSession;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//
//public class LoginActivity extends AppCompatActivity {
//
//
//    private static final int REQUEST_CODE = 100;
//    EditText edtName, edtId;
//    boolean userFound = false;
//    boolean outCompany = false;
//    static int dist = -1;
//    public static float longitude = -1;
//    public static float latitude = -1;
//    boolean networkStatus = false;
//
//    FusedLocationProviderClient fusedLocationProviderClient;
//    LocationRequest locationRequest;
//    LocationCallback locationCallback;
//    private RequestQueue mQueue;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        Log.e("onCreate", "onCreate: ");
//        handleSSLHandshake();
//        //isNetworkConnected();
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        SharedPreferences.Editor editor = preferences.edit();
//
//        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
//            if (!canGetLocation()) {
//                if (!preferences.getString("logout", "").equals("logoutStatus")) {
//                    buildAlertMessageNoGps();
//                } else {
//                    editor.putString("logout", "");
//                    editor.apply();
//                }
//            } else {
//                foo(LoginActivity.this);
//            }
//        } else {
//            if (statusCheck()) {
//                if (!preferences.getString("logout", "").equals("logoutStatus")) {
//                    buildAlertMessageNoGps();
//                } else {
//                    editor.putString("logout", "");
//                    editor.apply();
//                }
//            } else {
//
//                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//                buildLocationRequest();
//                buildLocationCallBack();
//            }
//        }
//
//        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        edtName = (EditText) findViewById(R.id.edtName);
//        edtId = (EditText) findViewById(R.id.edtId);
//        mQueue = Volley.newRequestQueue(this);
//
////        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//
//
//        if (!(preferences.getString("userid", "").equals(""))) {
//
//            Intent userintent = new Intent(getApplicationContext(), HomeActivity.class);
////           userintent.putExtra("userkind", "emp");
//            editor.putString("userkind", "emp");
//            editor.apply();
//            startActivity(userintent);
//            finish();
//
//        } else if (preferences.getString("adminstate", "").equals("1")) {
//
//            Intent adminintent = new Intent(getApplicationContext(), HomeActivity.class);
////           adminintent.putExtra("userkind", "admin");
//            editor.putString("userkind", "admin");
//            editor.apply();
//            startActivity(adminintent);
//            finish();
//
//        }
//    }
//
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        switch (requestCode) {
//            case REQUEST_CODE: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                        if (statusCheck()) {
//                            buildAlertMessageNoGps();
//
//                        }
//                        {
//                            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                        REQUEST_CODE);
//                            } else {
//                                buildLocationRequest();
//                                buildLocationCallBack();
//                                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//                                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                                        != PackageManager.PERMISSION_GRANTED
//                                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                                        != PackageManager.PERMISSION_GRANTED) {
//
//                                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                            REQUEST_CODE);
//
//                                    return;
//                                }
//                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
//                            }
//                        }
//                    }
//                } else {
//                }
//            }
//        }
//    }
//
//    public void btnLogin(View view) {
//
//        if (!isNetworkConnected()) {
//            Toast.makeText(this, "Please open network", Toast.LENGTH_SHORT).show();
//            //isNetworkConnected();
//
//        } else {
//
//            if (edtName.getText().toString().equals("admin") && edtId.getText().toString().equals("000")) {
//                sharedPrefAdmin();
//                Intent adminintent = new Intent(getApplicationContext(), HomeActivity.class);
////            adminintent.putExtra("userkind", "admin");
//                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//                final SharedPreferences.Editor editor = preferences.edit();
//                editor.putString("userkind", "admin");
//                editor.apply();
//                startActivity(adminintent);
//                finish();
//            } else {
//                if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
//                    Log.e("btnLogin: ", "KITKAT");
//                    if (!canGetLocation()) {
//                        buildAlertMessageNoGps();
//                        //Toast.makeText(this, "Please open location!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Log.e("btnLoginkitkat: ", dist + "");
//                        if (dist == -1) {
//                            foo(LoginActivity.this);
//                            //btnLogin(view);
//                            Toast.makeText(LoginActivity.this, "pleas Login again!", Toast.LENGTH_LONG).show();
//                        } else if (dist == 1) {
//                            Log.e("distancebol", dist + "");
//                            getLoginOrReport();
//                            outCompany = false;
//                        } else if (dist == 0) {
//                            outCompany = true;
//                            // Toast.makeText(LoginActivity.this, "You aren't in your company!", Toast.LENGTH_LONG).show();
//                            new AlertDialog.Builder(LoginActivity.this)
//                                    .setTitle("Company")
//                                    .setMessage("you aren't in your company!")
//                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
//                                        }
//                                    }).create().show();
//                        }
//                    }
//                } else {
//                    if (statusCheck()) {
//                        buildAlertMessageNoGps();
//                    } else {
//                        Log.e("btnLoginLolipop ", dist + "");
//                        if (dist == -1) {
//                            buildLocationRequest();
//                            buildLocationCallBack();
//                            //btnLogin(view);
//                            Toast.makeText(LoginActivity.this, "pleas Login again!", Toast.LENGTH_LONG).show();
//                        } else if (dist == 1) {
//                            Log.e("distancebol", dist + "");
//                            getLoginOrReport();
//                            outCompany = false;
//                        } else if (dist == 0) {
//                            outCompany = true;
//                            // Toast.makeText(LoginActivity.this, "You aren't in your company!", Toast.LENGTH_LONG).show();
//                            new AlertDialog.Builder(LoginActivity.this)
//                                    .setTitle("Company")
//                                    .setMessage("you aren't in your company!")
//                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
//                                        }
//                                    }).create().show();
//                        }
////                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
////                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
////                                    REQUEST_CODE);
////                        } else {
////                            buildLocationRequest();
////                            buildLocationCallBack();
////                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
////
////                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
////                                    != PackageManager.PERMISSION_GRANTED
////                                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
////                                    != PackageManager.PERMISSION_GRANTED) {
////
////                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
////                                        REQUEST_CODE);
////
////                                return;
////                            }
////                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
////                        }
//                    }
//                }
//            }
//        }
//    }
//
//    public void getLoginOrReport() {
//
//        final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
//        dialog.setMessage("please waiting....");
//        dialog.show();
//
//        String url = "https://karimaster97.000webhostapp.com/college/getLogin.php";
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        dialog.dismiss();
//                        try {
//                            //  dialog.dismiss();
////                            String android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
////                                    Settings.Secure.ANDROID_ID);
//
//                            JSONArray jsonArray = response.getJSONArray("Login");
//
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject login = jsonArray.getJSONObject(i);
//
//                                int USER_ID = login.getInt("USER_ID");
//                                String NAME = login.getString("NAME");
//                                String DEVICE_ID = login.getString("DEVICE_ID");
//
//                                if (edtName.getText().toString().equals(NAME) && edtId.getText().toString().equals(String.valueOf(USER_ID))
//                                        ) {
//
//                                    if (outCompany == true) {
//                                        userFound = true;
//                                        Log.e("onResponse: ", "break");
//                                        break;
//                                    }
//                                    Log.e("onResponse: ", "break22222222");
//                                    userFound = true;
//                                    Log.e("outCompany: ", outCompany + "");
//                                    sharedPrefUser(USER_ID + "", NAME);
//                                    Intent userintent = new Intent(getApplicationContext(), HomeActivity.class);
//                                    //userintent.putExtra("userkind", "emp");
//                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
//                                    final SharedPreferences.Editor editor = preferences.edit();
//                                    editor.putString("userkind", "emp");
//                                    editor.apply();
//                                    Log.e("getLoginOrReport: ", "emp");
//                                    startActivity(userintent);
//                                    finish();
//                                    break;
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        if (outCompany == true && userFound == true) {
//                            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//                            new AlertDialog.Builder(LoginActivity.this)
//                                    .setTitle("Company")
//                                    .setMessage("you are not in your company!")
//                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
//                                        }
//                                    }).create().show();
////                            Toast.makeText(LoginActivity.this, "Please enter correct information!", Toast.LENGTH_SHORT).show();
////                            Log.e("outCompany: ", outCompany + "");
//                        } else if (userFound == false) {
//                            Log.e("userFound: ", userFound + "");
//                            Toast.makeText(LoginActivity.this, "please enter correct information!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        });
//        mQueue.add(request);
//    }
//
//    private void sharedPrefUser(String userid, String username) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = preferences.edit();
//
//        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//        DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd ");
//        Date date = new Date();
//        Log.e("logout: ", dateFormat.format(date));
//        Log.e("logout: ", dateFormat2.format(date));
//
//        editor.putString("userid", userid);
//        editor.putString("username", username);
//        editor.putString("date", dateFormat2.format(date));
//        editor.putString("starttime", dateFormat.format(date));
//        editor.apply();
//
//
//        Log.e("logout: ", preferences.getString("userid", ""));
//        Log.e("logout: ", preferences.getString("username", ""));
//        Log.e("logout: ", preferences.getString("date", ""));
//        Log.e("logout: ", preferences.getString("starttime", ""));
//
//
//    }
//
//    private void sharedPrefAdmin() {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = preferences.edit();
//
//        editor.putString("adminstate", "1");
//        editor.apply();
//
//        Log.e("logout: ", preferences.getString("userid", "1"));
//
//    }
//
//    public boolean statusCheck() {
//        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//
//            return true;
//        }
//        return false;
//    }
//
//    private void buildAlertMessageNoGps() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, final int id) {
//                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                    }
//                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, final int id) {
//                        dialog.cancel();
//                    }
//                });
//        final AlertDialog alert = builder.create();
//        alert.show();
//    }
//
//    private void buildLocationCallBack() {
////        final ProgressDialog dialogLocation = new ProgressDialog(LoginActivity.this);
////        dialogLocation.setMessage("Getting location....");
////        dialogLocation.show();
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                //dialogLocation.dismiss();
////                double Longitude, Latitude;
////                Longitude = locationResult.getLastLocation().getLongitude();
////                Latitude = locationResult.getLastLocation().getLatitude();
//
//                Location loc1 = new Location("");
//                loc1.setLatitude(locationResult.getLastLocation().getLatitude());
//                loc1.setLongitude(locationResult.getLastLocation().getLongitude());
//
//                Location loc2 = new Location("");
////                loc2.setLatitude(29.96493141);
////                loc2.setLongitude(31.25023778);
//
//                //me
//                loc2.setLatitude(28.986864);
//                loc2.setLongitude(30.8714402);
//
//                Log.e("onSuccess: ", locationResult.getLastLocation().getLatitude() + "");
//                Log.e("onSuccess: ", locationResult.getLastLocation().getLongitude() + "");
//                Log.e("onSuccess: ", loc1.distanceTo(loc2) + "");
//
//                // Toast.makeText(LoginActivity.this, locationResult.getLastLocation().getLatitude() + "  " +
//                //  locationResult.getLastLocation().getLongitude() + "  " +
//                //  loc1.distanceTo(loc2) + "  ", Toast.LENGTH_SHORT).show();
//                //dialogLocation.dismiss();
//                if (loc1.distanceTo(loc2) <= 100) {
//                    dist = 1;
//                    outCompany = false;
//                    Log.e("distanceTo true: ", loc1.distanceTo(loc2) + "   " + outCompany);
//                    //getLoginOrReport();
//                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//                    // Log.e("onLocationResult: ", "getLoginOrReport");
//                    // Log.e("11111111111111", loc1.distanceTo(loc2) + "");
//                } else {
//                    dist = 0;
//                    Log.e("distanceTo false: ", loc1.distanceTo(loc2) + "   " + outCompany);
//
//                    outCompany = true;
//                    //getLoginOrReport();
//                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
////                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
////                        new AlertDialog.Builder(LoginActivity.this)
////                                .setTitle("Company")
////                                .setMessage("you are not in your company!")
////                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
////                                    @Override
////                                    public void onClick(DialogInterface dialog, int which) {
////                                        dialog.dismiss();
////                                    }
////                                }).create().show();
//                }
//                // Log.e("11111111111111", loc1.distanceTo(loc2) + "");
////                loc2.setLatitude(28.9868543);//                loc2.setLongitude(30.8714177);//                30.8713226  28.986925
////                Log.e("onLocationResult: ", Build.SERIAL);
////
////                Log.e("onLocationResult: ", Settings.Secure.getString(getApplicationContext().getContentResolver(),
////                        Settings.Secure.ANDROID_ID));
//            }
//        };
//    }
//
//    @SuppressLint("RestrictedApi")
//    private void buildLocationRequest() {
//        locationRequest = new LocationRequest();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(5000);
//        locationRequest.setFastestInterval(1000);
//        locationRequest.setSmallestDisplacement(10);
//    }
//
//    /**
//     * Enables https connections
//     */
//    @SuppressLint("TrulyRandom")
//    public static void handleSSLHandshake() {
//        try {
//            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                public X509Certificate[] getAcceptedIssuers() {
//                    return new X509Certificate[0];
//                }
//
//                @Override
//                public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                }
//
//                @Override
//                public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                }
//            }};
//
//            SSLContext sc = SSLContext.getInstance("SSL");
//            sc.init(null, trustAllCerts, new SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String arg0, SSLSession arg1) {
//                    return true;
//                }
//            });
//        } catch (Exception ignored) {
//        }
//    }
//
//    public void foo(Context context) {
//
//        SingleShotLocationProvider.requestSingleUpdate(context,
//                new SingleShotLocationProvider.LocationCallback() {
//                    @Override
//                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
//                        Log.d("Location", "my location is " + location.toString());
//
//                    }
//                });
//    }
//
//    public static class SingleShotLocationProvider {
//
//        public static void requestSingleUpdate(final Context context, final LocationCallback callback) {
//            final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//            if (isNetworkEnabled) {
//                Criteria criteria = new Criteria();
//                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
//                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//                locationManager.requestSingleUpdate(criteria, new LocationListener() {
//                    @Override
//                    public void onLocationChanged(Location location) {
//                        callback.onNewLocationAvailable(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
//                    }
//
//                    @Override
//                    public void onStatusChanged(String provider, int status, Bundle extras) {
//                    }
//
//                    @Override
//                    public void onProviderEnabled(String provider) {
//                    }
//
//                    @Override
//                    public void onProviderDisabled(String provider) {
//                    }
//                }, null);
//            } else {
//                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//                if (isGPSEnabled) {
//                    Criteria criteria = new Criteria();
//                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
//                    locationManager.requestSingleUpdate(criteria, new LocationListener() {
//                        @Override
//                        public void onLocationChanged(Location location) {
//                            callback.onNewLocationAvailable(new GPSCoordinates(location.getLatitude(), location.getLongitude()));
//                        }
//
//                        @Override
//                        public void onStatusChanged(String provider, int status, Bundle extras) {
//                        }
//
//                        @Override
//                        public void onProviderEnabled(String provider) {
//                        }
//
//                        @Override
//                        public void onProviderDisabled(String provider) {
//                        }
//                    }, null);
//                }
//            }
//        }
//
//        public interface LocationCallback {
//            public void onNewLocationAvailable(GPSCoordinates location);
//        }
//
//        // consider returning Location instead of this dummy wrapper class
//        public static class GPSCoordinates {
//
//            public GPSCoordinates(double theLatitude, double theLongitude) {
//
//                longitude = (float) theLongitude;
//                latitude = (float) theLatitude;
//                Location loc1 = new Location("");
//                loc1.setLatitude(latitude);
//                loc1.setLongitude(longitude);
//
//                Location loc2 = new Location("");
//                loc2.setLatitude(28.986864);
//                loc2.setLongitude(30.8714402);
//
//                if (loc1.distanceTo(loc2) <= 100) {
//                    //   getLoginOrReport();
//                    dist = 1;
//
//                    Log.e("GPSCoordinates: ", longitude + "  " + latitude + "\n distance" + loc1.distanceTo(loc2)
//                            + "  " +
//                            dist);
//
//                } else {
//                    dist = 0;
//                }
//                Log.e("GPSCoordinates: ", longitude + "  " + latitude + "\n distance" + loc1.distanceTo(loc2) + "");
//
//            }
//        }
//
//    }
//
//    public boolean canGetLocation() {
//        boolean result = true;
//        LocationManager lm = null;
//        boolean gps_enabled = false;
//        if (lm == null)
//
//            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        // exceptions will be thrown if provider is not permitted.
//        try {
//            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch (Exception ex) {
//
//        }
//
//        if (gps_enabled == false) {
//            result = false;
//        } else {
//            result = true;
//        }
//        return result;
//    }
//
//    private boolean isNetworkConnected() {
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        if (cm.getActiveNetworkInfo() == null) {
//            Log.e("isInternetAvailable: ", "false");
//
//            return false;
//        }
//        Log.e("isInternetAvailable: ", "true");
//
//        return true;
//
//    }
//
//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//        Log.e("on resume", "enter onresume");
//        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
//
//            foo(LoginActivity.this);
//        } else {
////            buildLocationRequest();
////            buildLocationCallBack();
//        }
//    }
//}
