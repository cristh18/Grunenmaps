package com.leinaro.grunenthal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity implements getStablishments.AsyncResponse, GetTerms.AsyncResponse{

    private boolean mVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        hide();
        setContentView(R.layout.activity_splash_screen);
        new GetTerms(this).execute();
        mVisible = true;
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void addFranquicia(String franquicia){
        boolean exist = false;
        for (int i=0; i < GrnenthalApplication.franquicias.size();i++){
            if (GrnenthalApplication.franquicias.get(i).compareToIgnoreCase(franquicia) == 0){
                exist = true;
            }
        }
        if (!exist)
            GrnenthalApplication.franquicias.add(franquicia);
    }

    @Override
    public void getStablishmentsComplete(JSONObject output) {
        try {
            if (output.optBoolean("result",false)) {
                setPalexis(output.optJSONArray("data").getJSONObject(1).getJSONArray("pharmacies2"));
                setTranstec(output.optJSONArray("data").getJSONObject(0).getJSONArray("pharmacies1"));
                sortFranquise();
                GrnenthalApplication.franquicias.add(0,"Todos");
            }else{
                Toast.makeText(this, "No se pudo obtener farmacias",Toast.LENGTH_LONG).show();
            }

            Intent mainIntent = new Intent().setClass(SplashScreenActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sortFranquise() throws ParseException {
        Collections.sort(GrnenthalApplication.franquicias, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Collator usCollator = Collator.getInstance(Locale.US);
                return usCollator.compare(o1, o2);
//                return o1.compareToIgnoreCase(o2);
            }
        });

        }

    private void setPalexis(JSONArray pharmacies1) throws JSONException {
        for (int i = 0; i < pharmacies1.length(); i++){
            Log.d("iarl","name "+pharmacies1.getJSONObject(i).getString("name"));

            addFranquicia(pharmacies1.getJSONObject(i).getString("franchise"));
            GrnenthalApplication.pharmacies1.add(
                    new Pharmacies(
                        pharmacies1.getJSONObject(i).getString("name"),
                        pharmacies1.getJSONObject(i).getString("address"),
                        pharmacies1.getJSONObject(i).getString("city"),
                        pharmacies1.getJSONObject(i).getDouble("lat"),
                        pharmacies1.getJSONObject(i).getDouble("lon"),
                        pharmacies1.getJSONObject(i).getString("idfranchise"),
                        pharmacies1.getJSONObject(i).getString("franchise"),"#92317c"
                )
            );
        }
    }

    private void setTranstec(JSONArray pharmacies2) throws JSONException {
        for (int i = 0; i < pharmacies2.length(); i++){
            Log.d("iarl","iarl transtec test test ::: "+i);
            Log.d("iarl","iarl transtec test test ::: "+pharmacies2.getJSONObject(i).getString("franchise"));
            Log.d("iarl","iarl transtec test test ::: "+pharmacies2.getJSONObject(i));

            addFranquicia(pharmacies2.getJSONObject(i).getString("franchise"));
            GrnenthalApplication.pharmacies2.add(
                    new Pharmacies(
                            pharmacies2.getJSONObject(i).optString("name"),
                            pharmacies2.getJSONObject(i).optString("address"),
                            pharmacies2.getJSONObject(i).optString("city"),
                            pharmacies2.getJSONObject(i).getDouble("lat"),
                            pharmacies2.getJSONObject(i).getDouble("lon"),
                            pharmacies2.getJSONObject(i).optString("idfranchise"),
                            pharmacies2.getJSONObject(i).optString("franchise"), "#dc4338"
                    )
            );
        }
    }

    @Override
    public void getTermsComplete(JSONObject output) {
        try {
            if (output.optBoolean("result",false)) {
                GrnenthalApplication.terms = output.optString("data");
                showTerms();

            }else{
                Toast.makeText(this, "No se pudo obtener términos y condiciones",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    SharedPreferences sharedpreferences;


    private void showTerms() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.layout_dialog_terms, null);

        sharedpreferences = getSharedPreferences("MyPREFERENCESGrunenthal", Context.MODE_PRIVATE);

        boolean terminosYCondiciones = sharedpreferences.getBoolean("Acepto_Terminos_y_condiciones", false);

        if (terminosYCondiciones) {
            new getStablishments(this).GetSomething();
        }else{
            TextView textview = (TextView) view.findViewById(R.id.textmsg);
            textview.setText(GrnenthalApplication.terms);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.terms_title);
            alertDialog.setCancelable(false);
//alertDialog.setMessage("Here is a really long message.");
            alertDialog.setView(view);
            alertDialog.setPositiveButton(getString(R.string.dialog_terms_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    aceptTerms();
                }
            });

            AlertDialog alert = alertDialog.create();
            alert.show();
        }


    }

    private void aceptTerms() {

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("Acepto_Terminos_y_condiciones", true);
        editor.commit();

        new getStablishments(this).execute();
    }
}