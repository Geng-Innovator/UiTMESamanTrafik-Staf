package com.seladanghijau.uitme_reportstaf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Profil extends AppCompatActivity implements View.OnClickListener{

    private String staf_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        SharedPreferences sharedPreferences = getSharedPreferences(Dashboard.pekerjaPrefs, Context.MODE_PRIVATE);
        staf_id = sharedPreferences.getString(Dashboard.id, "");

        //Fetch profil from server
        profil();
    }

    private void profil(){
        String url = getResources().getString(R.string.url_profil); //Url profil
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest profilRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Get the JSON object from the server, Response will return status and data
                    JSONObject obj = new JSONObject(response);
                    //Get status from the server. 0 - Failed, 1 - Success

                    if (obj.getString("status").equalsIgnoreCase("1")) {
                        JSONObject data = obj.getJSONObject("data");

                        ((TextView) findViewById(R.id.txtNoPekerjaProfil)).setText(data.getString("no_pekerja"));
                        ((TextView) findViewById(R.id.txtNoICProfil)).setText(data.getString("no_ic"));
                        ((TextView) findViewById(R.id.txtNoHPProfil)).setText(data.getString("no_tel_hp"));
                        ((TextView) findViewById(R.id.txtNoPejabatProfil)).setText(data.getString("no_tel_pej"));
                        ((TextView) findViewById(R.id.txtJawatanProfil)).setText(data.getString("jawatan_nama"));
                        ((TextView) findViewById(R.id.txtJabatanProfil)).setText(data.getString("jabatan"));
                    }else{
                        Toast.makeText(Profil.this, "Profil tidak dijumpai", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) { e.printStackTrace(); }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog alertDialog = new AlertDialog.Builder(Profil.this)
                        .setMessage("Profil tidak dijumpai")
                        .setCancelable(false)
                        .setPositiveButton("TERUSKAN", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("staf_id", staf_id);
                return params;
            }
        };

        requestQueue.add(profilRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogOut:
                SharedPreferences settings = getSharedPreferences(Dashboard.pekerjaPrefs, Context.MODE_PRIVATE);
                settings.edit().clear().apply();

                AlertDialog alertDialog = new AlertDialog.Builder(Profil.this)
                        .setMessage("Anda pasti ingin log keluar?")
                        .setCancelable(false)
                        .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(Profil.this, LogMasuk.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            }
                        })
                        .setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        })
                        .create();
                alertDialog.show();
                break;
            case R.id.btnUbahKataLaluan:
                startActivity(new Intent(Profil.this, Daftar.class));
                break;
            case R.id.btnKembali:
                finish();
                break;
        }
    }
}
