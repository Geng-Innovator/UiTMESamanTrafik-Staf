package com.seladanghijau.uitme_reportstaf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.iceteck.silicompressorr.SiliCompressor;
import com.tooltip.Tooltip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class Laporan_Baru extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{


    private EditText edtTempat, edtNoKenderaan, edtSiriPelekat, edtPenerangan;
    private SharedPreferences sharedPreferences;
    private String img;
    private int kenderaanId;
    private ImageView imgPreview;
    private Spinner spnJenisKenderaan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan__baru);

        edtTempat = findViewById(R.id.edtTempat);
        edtNoKenderaan = findViewById(R.id.edtNoKenderaan);
        edtSiriPelekat = findViewById(R.id.edtSiriPelekat);
        edtPenerangan = findViewById(R.id.edtPenerangan);
        imgPreview = findViewById(R.id.imgReport);

        //Tooltip
        sharedPreferences = getSharedPreferences("pekerjaPref", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("checkLaporan", "").isEmpty()) {
            showToolTip(0);
        }

        //Spinner Drop down elements
        spnJenisKenderaan = findViewById(R.id.spnJenisKenderaan);
        spnJenisKenderaan.setOnItemSelectedListener(this);
        setSpinner();
    }

    public void showToolTip(final int i){
        Tooltip tooltip;
        switch (i){
            case 0:
                tooltip = new Tooltip.Builder(imgPreview, R.style.Tooltip).setText("PRATONTON GAMBAR LAPORAN").show();
                break;
            case 1:
                tooltip = new Tooltip.Builder(edtPenerangan, R.style.Tooltip).setText("INFORMASI LAPORAN PERLU DISEDIAKAN").show();
                break;
            case 2:
                tooltip = new Tooltip.Builder((findViewById(R.id.btnMuatGambar)), R.style.Tooltip).setText("MUAT NAIK GAMBAR DI SINI").show();
                break;
            default:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("checkLaporan", "ada");
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
        }, 2000);
    }

    private void setSpinner(){
        final List<String> categories = new ArrayList<>();

        String url = getResources().getString(R.string.url_kenderaan);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest spinnerRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    //Get the JSON object from the server
                    JSONObject obj = new JSONObject(response);
                    Log.d("Sampai", "Sini");

                    //Get status from the server. 0 - Failed, 1 - Success
                    if (obj.getString("status").equalsIgnoreCase("1")){
                        //Redirect to dashboard after laporan success
                        JSONObject data = obj.getJSONObject("data");
                        JSONArray jenisKenderaanList = data.getJSONArray("jenisKenderaanList");
                        for (int i=0; i<jenisKenderaanList.length(); i++){
                            JSONObject o = jenisKenderaanList.getJSONObject(i);
                            categories.add(o.getString("nama"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Laporan_Baru.this, android.R.layout.simple_spinner_dropdown_item, categories);
                        spnJenisKenderaan.setAdapter(adapter);
                    }else{
                        //Try again
                        AlertDialog alertDialog = new AlertDialog.Builder(Laporan_Baru.this)
                                .setMessage("Gagal mengakses internet anda")
                                .create();
                        alertDialog.show();
                    }

                }catch (Exception e){ e.printStackTrace(); }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog alertDialog = new AlertDialog.Builder(Laporan_Baru.this)
                        .setMessage("Gagal mengakses internet anda")
                        .create();
                alertDialog.show();
            }
        });

        requestQueue.add(spinnerRequest);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        kenderaanId = position + 1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean checkAll() {
        return !edtTempat.getText().toString().trim().isEmpty() && !edtNoKenderaan.getText().toString().trim().isEmpty() && !edtSiriPelekat.getText().toString().trim().isEmpty() && !edtPenerangan.getText().toString().trim().isEmpty();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnMuatGambar){
            //Take picture
            captureImage();
        }else if (v.getId() == R.id.btnHantar){
            //Send the report
            try{
                final ProgressDialog pDialog = new ProgressDialog(Laporan_Baru.this);
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String url = getResources().getString(R.string.url_laporan_baru);
                StringRequest laporanRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            //Get the JSON object from the server
                            JSONObject obj = new JSONObject(response);

                            //Get status from the server. 0 - Failed, 1 - Success
                            if (obj.getString("status").equalsIgnoreCase("1")){
                                //Redirect to dashboard after laporan success
                                AlertDialog alertDialog = new AlertDialog.Builder(Laporan_Baru.this)
                                        .setMessage("Laporan anda telah dimuat naik. Terima kasih kerana melapor")
                                        .setCancelable(false)
                                        .setPositiveButton("TERUSKAN", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent newIntent = new Intent(Laporan_Baru.this, Dashboard.class);
                                                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(newIntent);
                                            }
                                        })
                                        .create();
                                alertDialog.show();
                            }else{
                                //Try again
                                AlertDialog alertDialog = new AlertDialog.Builder(Laporan_Baru.this)
                                        .setMessage("Gagal melapor")
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
                        AlertDialog alertDialog = new AlertDialog.Builder(Laporan_Baru.this)
                                .setMessage("Gagal melapor")
                                .create();
                        alertDialog.show();

                        if(pDialog.isShowing())
                            pDialog.dismiss();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params;
                        if (checkAll()) {
                            //Get id from shared preferences
                            String pekerja_id = sharedPreferences.getString(Dashboard.id, "");

                            params = new HashMap<>();
                            params.put("staf_id", pekerja_id);  //From s.pref
                            params.put("imej_staf", img); //From camera
                            params.put("laporan_staf", edtPenerangan.getText().toString().trim());
                            params.put("tempat", edtTempat.getText().toString().trim());
                            params.put("no_siri_pelekat", edtSiriPelekat.getText().toString().trim());
                            params.put("no_kenderaan", edtNoKenderaan.getText().toString().trim());
                            params.put("jenis_kenderaan_id", "" + kenderaanId); //From spinner
                            return params;
                        }else{
                            AlertDialog alertDialog = new AlertDialog.Builder(Laporan_Baru.this)
                                    .setMessage("Sila isi kesemua informasi")
                                    .create();
                            alertDialog.show();
                            return null;
                        }
                    }
                };

                requestQueue.add(laporanRequest);
                pDialog.setMessage("Sedang memuat turun data...");
                pDialog.setCancelable(false);
                pDialog.show();
            }catch (Exception e){
                AlertDialog alertDialog = new AlertDialog.Builder(Laporan_Baru.this)
                        .setMessage("Anda telah gagal untuk menghantar laporan")
                        .create();
                alertDialog.show();

                e.printStackTrace();
            }
        }else{
            //Cancel to report and back to dashboard
            startActivity(new Intent(Laporan_Baru.this, Dashboard.class));
            finish();
        }
    }

    public void captureImage(){
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        img = encodeToBase64(bitmap);
        imgPreview.setImageBitmap(bitmap);
    }


    public static String encodeToBase64(Bitmap image){
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
}
