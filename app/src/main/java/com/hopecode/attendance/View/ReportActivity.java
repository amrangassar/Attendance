package com.hopecode.attendance.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hopecode.attendance.Adapter.EmpAdapter;
import com.hopecode.attendance.Adapter.ReportAdapter;
import com.hopecode.attendance.Model.Employee;
import com.hopecode.attendance.Model.Report;
import com.hopecode.attendance.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ReportActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    RecyclerView empRecyclerView;
    private ArrayList<Report> reports;
    ReportAdapter repAdapter;
    public String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        handleSSLHandshake();


        userid=getIntent().getExtras().getString("userid");
        initcomponant();
        getLoginOrReport();

    }

    private void initcomponant() {
        mQueue = Volley.newRequestQueue(this);
        empRecyclerView = findViewById(R.id.recyclerRep);
        reports = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        empRecyclerView.setLayoutManager(mLayoutManager);

    }

    public void getLoginOrReport() {

        final ProgressDialog dialog = new ProgressDialog(ReportActivity.this);
        dialog.setMessage("please waiting....");
        dialog.show();
        Log.e("logout: ","logout" );
        String connectionparamters;
        byte[] parametersbyt = new byte[0];


        String USER_ID = "15";

        final String url = "https://karimaster97.000webhostapp.com/college/getreportbyid.php?USER_ID="+userid;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.dismiss();

                            JSONArray jsonArray = response.getJSONArray("report");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject rep   = jsonArray.getJSONObject(i);

                                Report report = new Report(rep.getString("REPORT_ID"),
                                        rep.getString("START_TIME"), rep.getString("END_TIME"),
                                        rep.getString("DAILY_DATE"), rep.getString("USER_ID"));
                                reports.add(report);

                            }
                            repAdapter = new ReportAdapter(getApplicationContext(), R.layout.rep_raw, reports);
                            empRecyclerView.setAdapter(repAdapter);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                            Log.e("11111111111111","11111111111111111");

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
