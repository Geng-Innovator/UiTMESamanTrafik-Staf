package com.seladanghijau.uitme_reportstaf;

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
                            //Get the JSON array
                            //JSONArray array = response.getJSONArray("pekerja");
                            JSONObject obj = new JSONObject(response);
                            Pekerja pekerja = new Pekerja();
                            pekerja.setId(obj.getLong("id"));
                            pekerja.setNama(obj.getString("name"));
                            pekerja.setEmel(obj.getString("email"));
                            pekerja.setPassword(obj.getString("password"));

                            Log.d("Nama", pekerja.getNama());
                            Toast.makeText(getApplicationContext(), pekerja.getNama(), Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Login error", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params;

                        params = new HashMap<>();
                        params.put("email", "Test@gmail.com");
                        params.put("password", "test");

                        return params;
                    }
                };

                requestQueue.add(loginRequest);

            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "There seems to be an error with your internet connectivity", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
