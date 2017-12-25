package com.seladanghijau.uitme_reportstaf;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.seladanghijau.uitme_reportstaf.Entiti.Pekerja;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LogMasuk extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_masuk);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogMasuk){
            try{
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String url = "http://test-ground.000webhostapp.com/login.php";
                StringRequest loginRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            //Get the JSON object from the server, Response will return status and data
                            JSONObject obj = new JSONObject(response);
                            //Get status from the server. 0 - Failed, 1 - Success
                            if (obj.getBoolean("status")){
                                JSONObject data = obj.getJSONObject("data");
                                //Check Log Pertama
                                boolean log_pertama = data.getBoolean("log_pertama");
                                if (log_pertama){ //First time log in
                                    //Redirect to daftar
                                    Intent i = new Intent(LogMasuk.this, Daftar.class);
                                    i.putExtra("id", data.getString("id"));
                                    i.putExtra("cur_pass", data.getString("password"));
                                    startActivity(i);
                                }else{
                                    //Redirect to dashboard and input id into shared preferences
                                    startActivity(new Intent(LogMasuk.this, Dashboard.class));
                                    finish();
                                }
                            }else{
                                //Redirect to log masuk
                                Toast.makeText(LogMasuk.this, "Log Masuk gagal", Toast.LENGTH_SHORT).show();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Log Masuk error", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params;

                        params = new HashMap<>();
                        params.put("no_pekerja", "");
                        params.put("password", "");

                        return params;
                    }
                };

                requestQueue.add(loginRequest);

            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Terdapat masalah dengan rangkaian internet anda", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
