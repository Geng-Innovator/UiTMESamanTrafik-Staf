package com.seladanghijau.uitme_reportstaf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LogMasuk extends AppCompatActivity implements View.OnClickListener{

    public static final String id = "ID";
    public static final String pekerjaPrefs = "pekerjaPref";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_masuk);

        sharedPreferences = getSharedPreferences(pekerjaPrefs, Context.MODE_PRIVATE);

        String pekerja_id = sharedPreferences.getString(id, "");
        if (!pekerja_id.isEmpty()){
            startActivity(new Intent(LogMasuk.this, Dashboard.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnLogMasuk){

            try{
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String url = "http://beta.seladanghijau.com/uitm_e_laporan/public/staf/login";
                StringRequest loginRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            //Get the JSON object from the server, Response will return status and data
                            JSONObject obj = new JSONObject(response);
                            //Get status from the server. 0 - Failed, 1 - Success
                            if (obj.getString("status").equalsIgnoreCase("1")){
                                JSONObject data = obj.getJSONObject("data");
                                //Check Log Pertama
                                String log_pertama = data.getString("log_pertama");
                                if (log_pertama.equalsIgnoreCase("1")){ //First time log in
                                    //Redirect to daftar
                                    Intent i = new Intent(LogMasuk.this, Daftar.class);
                                    i.putExtra("id", data.getInt("id"));
                                    i.putExtra("cur_pass", ((EditText)findViewById(R.id.edtKataLaluan)).getText().toString().trim());
                                    startActivity(i);
                                    finish();
                                }else{
                                    //Redirect to dashboard and input id into shared preferences
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(id, data.getString("id"));
                                    editor.commit();

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
                        params.put("no_pekerja", ((EditText)findViewById(R.id.edtNoPekerja)).getText().toString().trim());
                        params.put("password", ((EditText)findViewById(R.id.edtKataLaluan)).getText().toString().trim());

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
