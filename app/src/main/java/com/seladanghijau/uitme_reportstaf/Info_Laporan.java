package com.seladanghijau.uitme_reportstaf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.tooltip.Tooltip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Info_Laporan extends AppCompatActivity implements View.OnClickListener {

    private TextView txtLaporanID, txtLaporanStatus, txtLaporanTarikh, txtLaporanMasa, txtLaporanTempat;
    private ImageView imgLaporan;
    private TextView txtPeneranganStaf,txtPeneranganPolis;
    private TextView txtNoKenderaan, txtJenisKenderaan, txtStatusKenderaan, txtPelekatKenderaan;
    private Button btnKembali;
    private RecyclerView rcyInfoKesalahan;
    private KesalahanAdapter adapter;
    private List<String> kesalahanList = new ArrayList<>();
    private SharedPreferences sharedPreferences;

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
        btnKembali = findViewById(R.id.btnInfoKembali);

        btnKembali.setOnClickListener(this);

        adapter = new KesalahanAdapter(kesalahanList);
        rcyInfoKesalahan = findViewById(R.id.rcyInfoKesalahan);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rcyInfoKesalahan.setLayoutManager(mLayoutManager);
        rcyInfoKesalahan.setItemAnimator(new DefaultItemAnimator());
        rcyInfoKesalahan.setAdapter(adapter);

        //Tooltip
        sharedPreferences = getSharedPreferences("pekerjaPref", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("checkInfo", "").isEmpty()) {
            showToolTip(0);
        }
        getLaporan(getIntent().getStringExtra("id"));
    }

    public void showToolTip(final int i){
        Tooltip tooltip;
        switch (i){
            case 0:
                tooltip = new Tooltip.Builder(imgLaporan, R.style.Tooltip).setText("GAMBAR LAPORAN").show();
                break;
            case 1:
                tooltip = new Tooltip.Builder(txtLaporanStatus, R.style.Tooltip).setText("STATUS LAPORAN").show();
                break;
            case 2:
                tooltip = new Tooltip.Builder(txtPeneranganStaf, R.style.Tooltip).setText("INFORMASI LAPORAN").show();
                break;
            case 3:
                tooltip = new Tooltip.Builder(txtPeneranganPolis, R.style.Tooltip).setText("INFORMASI LAPORAN MAKLUM BALAS DARIPADA POLIS").show();
                break;
            default:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("checkInfo", "ada");
                editor.apply();
                return;
        }
        Timer t = new Timer(false);
        final Tooltip finalTooltip = tooltip;
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        finalTooltip.dismiss();
                        int j = i + 1;
                        showToolTip(j);
                    }
                });
            }
        }, 4000);
    }

    public void getLaporan(final String id){
        final ProgressDialog pDialog = new ProgressDialog(Info_Laporan.this);
        String url = getResources().getString(R.string.url_laporan);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest laporanRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    //Get the JSON object from the server
                    JSONObject obj = new JSONObject(response);

                    //Get status from the server. 0 - Failed, 1 - Success
                    if (obj.getString("status").equalsIgnoreCase("1")){
                        JSONObject data = obj.getJSONObject("data");

                        // load image
                        LoadImageFromUrl loadImageFromUrl = new LoadImageFromUrl(imgLaporan);
                        loadImageFromUrl.execute(data.getString("staf_imej"));

                        switch(data.getString("laporan_status")) {
                            case "DILAPORKAN":
                                txtLaporanStatus.setBackground(getResources().getDrawable(R.drawable.status_laporan_dilaporkan));
                                break;
                            case "DIJADUALKAN":
                                txtLaporanStatus.setBackground(getResources().getDrawable(R.drawable.status_laporan_dijadualkan));
                                break;
                            case "DIKUATKUASAKAN":
                                txtLaporanStatus.setBackground(getResources().getDrawable(R.drawable.status_laporan_dikuatkuasakan));
                                break;
                            case "DITUTUPKAN":
                                txtLaporanStatus.setBackground(getResources().getDrawable(R.drawable.status_laporan_ditutup));
                                break;
                            default:
                                txtLaporanStatus.setBackground(getResources().getDrawable(R.drawable.status_laporan_ditutup));
                                break;
                        }

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
                        JSONArray jsonArray = data.getJSONArray("kesalahan_list");
                        for (int i=0; i<jsonArray.length(); i++){
                            kesalahanList.add(jsonArray.getString(i));
                            adapter.notifyDataSetChanged();
                        }
                    }else{
                        //Try again
                        AlertDialog alertDialog = new AlertDialog.Builder(Info_Laporan.this)
                                .setMessage("Laporan tidak dijumpai")
                                .create();
                        alertDialog.show();
                    }
                }catch (Exception e){ e.printStackTrace(); }

                if(pDialog.isShowing())
                    pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog alertDialog = new AlertDialog.Builder(Info_Laporan.this)
                        .setMessage("Laporan tidak dijumpai")
                        .create();
                alertDialog.show();

                if(pDialog.isShowing())
                    pDialog.dismiss();
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
        pDialog.setMessage("Sedang memuat turun data...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnInfoKembali:
                finish();
                break;
        }
    }

    // asynctask utk load image from url
    class LoadImageFromUrl extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public LoadImageFromUrl(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            Bitmap imageBitmap = null;

            try {
                InputStream is = new URL(url[0]).openStream();
                imageBitmap = BitmapFactory.decodeStream(is);
            } catch (Exception e) { e.printStackTrace(); }

            return imageBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null)
                imageView.setImageBitmap(bitmap);
        }
    }
}
