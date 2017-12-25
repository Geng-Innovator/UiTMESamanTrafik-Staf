package com.seladanghijau.uitme_reportstaf;

import android.content.Intent;
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

public class Laporan_Baru extends AppCompatActivity implements View.OnClickListener{

    private EditText edtTempat, edtNoKenderaan, edtSiriPelekat, edtPenerangan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan__baru);

        edtTempat = findViewById(R.id.edtTempat);
        edtNoKenderaan = findViewById(R.id.edtNoKenderaan);
        edtSiriPelekat = findViewById(R.id.edtSiriPelekat);
        edtPenerangan = findViewById(R.id.edtPenerangan);
    }

    public boolean checkAll() {
        return !edtTempat.getText().toString().trim().isEmpty() && !edtNoKenderaan.getText().toString().trim().isEmpty() && !edtSiriPelekat.getText().toString().trim().isEmpty() && !edtPenerangan.getText().toString().trim().isEmpty();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnMuatGambar){
            //Get picture from camera
        }else if (v.getId() == R.id.btnHantar){
            //Send the report
            try{
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String url = "http://test-ground.000webhostapp.com/register.php";
                StringRequest laporanRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            //Get the JSON object from the server
                            JSONObject obj = new JSONObject(response);
                            //Get status from the server. 0 - Failed, 1 - Success
                            if (obj.getBoolean("status")){
                                //Redirect to dashboard after laporan success
                                Toast.makeText(Laporan_Baru.this, "Laporan anda telah dimuat naik. Terima kasih kerana melapor", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Laporan_Baru.this, Dashboard.class));
                                finish();
                            }else{
                                //Try again
                                Toast.makeText(Laporan_Baru.this, "Laporan anda tidak berjaya", Toast.LENGTH_SHORT).show();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Laporan anda tidak berjaya", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params;
                        if (checkAll()) {
                            //Get id from shared preferences

                            params = new HashMap<>();
                            params.put("staf_id", "");  //From s.pref
                            params.put("imej_staf", ""); //From camera
                            params.put("laporan_staf", edtPenerangan.getText().toString().trim());
                            params.put("tempat", edtTempat.getText().toString().trim());
                            params.put("no_siri_pelekat", edtSiriPelekat.getText().toString().trim());
                            params.put("no_kenderaan", edtNoKenderaan.getText().toString().trim());
                            params.put("jenis_kenderaan", ""); //From spinner
                            return params;
                        }else{
                            Toast.makeText(Laporan_Baru.this, "Sila isi kesemua informasi", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                    }
                };

                requestQueue.add(laporanRequest);

            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Anda telah gagal untuk menghantar laporan", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else{
            //Cancel to report and back to dashboard
            startActivity(new Intent(Laporan_Baru.this, Dashboard.class));
            finish();
        }
    }
}
