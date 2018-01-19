package com.seladanghijau.uitme_reportstaf;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Info_Laporan extends AppCompatActivity implements View.OnClickListener {

    private TextView txtLaporanID, txtLaporanStatus, txtLaporanTarikh, txtLaporanMasa, txtLaporanTempat;
    private ImageView imgLaporan;
    private TextView txtPeneranganStaf,txtPeneranganPolis;
    private TextView txtNoKenderaan, txtJenisKenderaan, txtStatusKenderaan, txtPelekatKenderaan;
    private RecyclerView rcyInfoKesalahan;
    private KesalahanAdapter adapter;
    private List<String> kesalahanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info__laporan);

        txtLaporanID = findViewById(R.id.txtInfoID);
        txtLaporanStatus = findViewById(R.id.txtInfoStatus);
        txtLaporanTarikh = findViewById(R.id.txtInfoTarikh);
        txtLaporanMasa = findViewById(R.id.txtInfoMasa);
        txtLaporanTempat = findViewById(R.id.txtInfoTempat);
        txtPeneranganStaf = findViewById(R.id.txtInfoPeneranganStaf);
        txtPeneranganPolis = findViewById(R.id.txtInfoLaporanPolis);
        txtNoKenderaan = findViewById(R.id.txtInfoNoKenderaan);
        txtJenisKenderaan = findViewById(R.id.txtInfoKenderaan);
        txtStatusKenderaan = findViewById(R.id.txtInfoStatusKenderaan);
        txtPelekatKenderaan = findViewById(R.id.txtInfoSiriPelekat);
        imgLaporan = findViewById(R.id.imgInfo);

        adapter = new KesalahanAdapter(kesalahanList);
        rcyInfoKesalahan = findViewById(R.id.rcyInfoKesalahan);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rcyInfoKesalahan.setLayoutManager(mLayoutManager);
        rcyInfoKesalahan.setItemAnimator(new DefaultItemAnimator());
        rcyInfoKesalahan.setAdapter(adapter);

        getLaporan(getIntent().getStringExtra("id"));
    }

    public void getLaporan(final String id){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest laporanRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.laporan), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    //Get the JSON object from the server
                    JSONObject obj = new JSONObject(response);

                    //Get status from the server. 0 - Failed, 1 - Success
                    if (obj.getString("status").equalsIgnoreCase("1")){
                        JSONObject data = obj.getJSONObject("data");
                        imgLaporan.setImageBitmap(decodeBase64(data.getString("staf_imej")));
                        txtLaporanID.setText(data.getString("id"));
                        txtLaporanStatus.setText(data.getString("laporan_status"));
                        txtLaporanMasa.setText(data.getString("laporan_masa"));
                        txtLaporanTempat.setText(data.getString("laporan_tempat"));
                        txtPeneranganStaf.setText(data.getString("staf_penerangan"));
                        txtPeneranganPolis.setText(data.getString("polis_penerangan"));
                        txtNoKenderaan.setText(data.getString("kenderaan_no"));
                        txtJenisKenderaan.setText(data.getString("kenderaan_jenis"));
                        txtStatusKenderaan.setText(data.getString("kenderaan_status"));
                        txtPelekatKenderaan.setText(data.getString("no_siri_pelekat"));
                        Log.d("List", data.getString("kesalahan_list"));
                        for (int i=1; i<=3; i++){
                            kesalahanList.add("Kesalahan " + i);
                            adapter.notifyDataSetChanged();
                        }
                    }else{
                        //Try again
                        Toast.makeText(Info_Laporan.this, "Laporan tidak dijumpai", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Info_Laporan.this, "Laporan tidak dijumpai", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("laporan_id", id);
                return params;
            }
        };
        requestQueue.add(laporanRequest);
    }

    @Override
    public void onClick(View v) {
        //Hello
    }

    private static Bitmap decodeBase64(String input){
        byte[] decodeBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodeBytes, 0, decodeBytes.length);
    }
}
