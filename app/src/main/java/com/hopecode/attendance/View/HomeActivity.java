package com.hopecode.attendance.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hopecode.attendance.Adapter.EmpAdapter;
import com.hopecode.attendance.Model.Employee;
import com.hopecode.attendance.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class HomeActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    RecyclerView empRecyclerView;
    private ArrayList<Employee> employees;
    EmpAdapter empAdapter;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        handleSSLHandshake();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();
        userid = preferences.getString("userid", "");


//        initcomponant();
//        if (getIntent().getExtras().get("userkind").equals("emp")) {
//            ((TextView) findViewById(R.id.welcome)).setVisibility(View.VISIBLE);
//            ((ImageView) findViewById(R.id.imageView3)).setVisibility(View.VISIBLE);
//            ((RecyclerView) findViewById(R.id.recyclerEmp)).setVisibility(View.INVISIBLE);
//            ((ImageView) findViewById(R.id.img_refresh)).setVisibility(View.INVISIBLE);
//            changeStatusLogin();
//
//        } else {
//            ((TextView) findViewById(R.id.welcome)).setVisibility(View.INVISIBLE);
//            ((ImageView) findViewById(R.id.imageView3)).setVisibility(View.INVISIBLE);
//            ((RecyclerView) findViewById(R.id.recyclerEmp)).setVisibility(View.VISIBLE);
//            ((ImageView) findViewById(R.id.img_refresh)).setVisibility(View.VISIBLE);
//            getLoginOrReport();
//
//        }
        initComponent();
//        if (getIntent().getExtras().get("userkind").equals("emp")) {
        if ((preferences.getString("userkind", "")).equals("emp")) {
            Log.e("onCreate: ", (preferences.getString("userkind", "")));
            (findViewById(R.id.welcome)).setVisibility(View.VISIBLE);
            (findViewById(R.id.imageView3)).setVisibility(View.VISIBLE);
            (findViewById(R.id.recyclerEmp)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.btn_refresh)).setVisibility(View.INVISIBLE);

            changeStatusLogin();

        } else {
            (findViewById(R.id.welcome)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.imageView3)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.recyclerEmp)).setVisibility(View.VISIBLE);
            (findViewById(R.id.btn_refresh)).setVisibility(View.VISIBLE);

            getLoginOrReport();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                final SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("logout","logoutStatus");
                    editor.apply();
                if (preferences.getString("adminstate", "").equals("1")) {
                    adminLogout();
                } else {
                    changeStatusOut();
                    userLogout();
                }
            }
        });
    }

    private void initComponent() {
        mQueue = Volley.newRequestQueue(this);
        empRecyclerView = findViewById(R.id.recyclerEmp);
        employees = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        empRecyclerView.setLayoutManager(mLayoutManager);

    }

    public void getLoginOrReport() {
        final ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        dialog.setMessage("please waiting....");
        dialog.show();

        String url = "https://karimaster97.000webhostapp.com/college/getLogin.php";


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.dismiss();

                            JSONArray jsonArray = response.getJSONArray("Login");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject login = jsonArray.getJSONObject(i);

                                Employee employee = new Employee(login.getString("NAME"), login.getString("STATUSE"), login.getString("USER_ID"), login.getString("DEVICE_ID"), login.getString("EMAIL"), login.getString("PHONE"));
                                employees.add(employee);

                                Log.e("11111111111111", employee.getEmpName() + "  /" + employee.getUSER_ID());

                            }
                            empAdapter = new EmpAdapter(getApplicationContext(), R.layout.emp_raw, employees);
                            empRecyclerView.setAdapter(empAdapter);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            Log.e("11111111111111", "11111111111111111");

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

    private void userLogout() {
        final ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        dialog.setMessage("please waiting....");
        dialog.show();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date endtime = new Date();


        //Log.e("logout: ", "logout");
        String connectionparamters;
        byte[] parametersbyt = new byte[0];

        String START_TIME = preferences.getString("starttime", "");
        String END_TIME = dateFormat.format(endtime);
        String DAILY_DATE = preferences.getString("date", "");
        String USER_ID = preferences.getString("userid", "");

        final String url = "https://karimaster97.000webhostapp.com/college/insertToReport.php";
        String START_TIMEkey = "START_TIME=";
        String END_TIMEkey = "&END_TIME=";
        String DAILY_DATEkey = "&DAILY_DATE=";
        String USER_IDkey = "&USER_ID=";

        try {
            connectionparamters = START_TIMEkey + URLEncoder.encode(START_TIME, "UTF-8") +
                    END_TIMEkey + URLEncoder.encode(END_TIME, "UTF-8") +
                    DAILY_DATEkey + URLEncoder.encode(DAILY_DATE, "UTF-8") +
                    USER_IDkey + URLEncoder.encode(USER_ID, "UTF-8");
            Log.e("logout: ", connectionparamters);
            parametersbyt = connectionparamters.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final byte[] finalParametersbyt = parametersbyt;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    URL insertUserUri = new URL(url);
                    HttpURLConnection insertconnection = (HttpURLConnection) insertUserUri.openConnection();
                    insertconnection.setRequestMethod("POST");
                    insertconnection.getOutputStream().write(finalParametersbyt);
                    InputStreamReader resultStreamReader = new InputStreamReader(insertconnection.getInputStream());
                    BufferedReader resultReader = new BufferedReader(resultStreamReader);
                    final String result = resultReader.readLine();
                    dialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result.equals("User Logout Successful")) {
                                editor.putString("userid", "");
                                editor.putString("username", "");
                                editor.putString("date", "");
                                editor.putString("starttime", "");
                                editor.putString("userkind", "");
                                editor.apply();
                                Toast.makeText(getApplicationContext(), "Logout Successful", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void adminLogout() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("adminstate", "0");
        editor.putString("userkind", "");
        editor.apply();
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));

    }

    public void changeStatusLogin() {

        Log.e("logout: ", "logout");
        String connectionparamters;
        byte[] parametersbyt = new byte[0];

        String USER_ID = userid;
        String STATUSE = "1";

        final String url = "https://karimaster97.000webhostapp.com/college/updatestatuse.php";

        String USER_IDkey = "USER_ID=";
        String STATUSEkey = "&STATUSE=";

        try {
            connectionparamters = USER_IDkey + URLEncoder.encode(USER_ID, "UTF-8") +
                    STATUSEkey + URLEncoder.encode(STATUSE, "UTF-8");
            Log.e("logout: ", connectionparamters);
            parametersbyt = connectionparamters.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final byte[] finalParametersbyt = parametersbyt;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    URL insertUserUri = new URL(url);
                    HttpURLConnection insertconnection = (HttpURLConnection) insertUserUri.openConnection();
                    insertconnection.setRequestMethod("POST");
                    insertconnection.getOutputStream().write(finalParametersbyt);
                    InputStreamReader resultStreamReader = new InputStreamReader(insertconnection.getInputStream());
                    BufferedReader resultReader = new BufferedReader(resultStreamReader);
                    final String result = resultReader.readLine();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result.equals("updated Successful")) {

                                Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void changeStatusOut() {

        String connectionparamters;
        byte[] parametersbyt = new byte[0];

        String USER_ID = userid;
        String STATUSE = "0";

        final String url = "https://karimaster97.000webhostapp.com/college/updatestatuse.php";

        String USER_IDkey = "USER_ID=";
        String STATUSEkey = "&STATUSE=";

        try {
            connectionparamters = USER_IDkey + URLEncoder.encode(USER_ID, "UTF-8") +
                    STATUSEkey + URLEncoder.encode(STATUSE, "UTF-8");
            parametersbyt = connectionparamters.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final byte[] finalParametersbyt = parametersbyt;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    URL insertUserUri = new URL(url);
                    HttpURLConnection insertconnection = (HttpURLConnection) insertUserUri.openConnection();
                    insertconnection.setRequestMethod("POST");
                    insertconnection.getOutputStream().write(finalParametersbyt);
                    InputStreamReader resultStreamReader = new InputStreamReader(insertconnection.getInputStream());
                    BufferedReader resultReader = new BufferedReader(resultStreamReader);
                    final String result = resultReader.readLine();
                    //dialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result.equals("updated Successful")) {


                            } else {
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void refresh(View view) {
        employees.clear();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();

//        if (getIntent().getExtras().get("userkind").equals("emp")) {
        if ((preferences.getString("userkind", "")).equals("emp")) {
            ((TextView) findViewById(R.id.welcome)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.imageView3)).setVisibility(View.VISIBLE);
            ((RecyclerView) findViewById(R.id.recyclerEmp)).setVisibility(View.INVISIBLE);
            ((ImageView) findViewById(R.id.btn_refresh)).setVisibility(View.INVISIBLE);


            changeStatusLogin();

        } else {
            ((TextView) findViewById(R.id.welcome)).setVisibility(View.INVISIBLE);
            ((ImageView) findViewById(R.id.imageView3)).setVisibility(View.INVISIBLE);
            ((RecyclerView) findViewById(R.id.recyclerEmp)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.btn_refresh)).setVisibility(View.VISIBLE);
            getLoginOrReport();
        }
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
}
