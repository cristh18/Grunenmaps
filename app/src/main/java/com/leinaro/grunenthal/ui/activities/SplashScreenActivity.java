package com.leinaro.grunenthal.ui.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.leinaro.grunenthal.GrnenthalApplication;
import com.leinaro.grunenthal.R;
import com.leinaro.grunenthal.api.client.ApiClientKt;
import com.leinaro.grunenthal.api.models.Pharmacy;
import com.leinaro.grunenthal.api.models.PharmarciesResponse;
import com.leinaro.grunenthal.api.models.TermsResponse;
import com.leinaro.grunenthal.api.services.PharmacyService;
import com.leinaro.grunenthal.api.services.TermsService;

import java.text.Collator;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SplashScreenActivity extends AppCompatActivity {

    private SharedPreferences sharedpreferences;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        hide();
        setContentView(R.layout.activity_splash_screen);
        getTerms();
    }

    private void getTerms() {
        TermsService termsService = ApiClientKt.getRemoteClient(getBaseContext()).create(TermsService.class);
        compositeDisposable.add(termsService.getTerms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::notifyTerms, t -> showServiceError(t, getString(R.string.copy_error_getting_terms))));
    }

    private void notifyTerms(TermsResponse termsResponse) {
        if (termsResponse.getResult()) {
            GrnenthalApplication.terms = termsResponse.getData();
            showTerms();
        } else {
            Toast.makeText(this, getString(R.string.copy_error_getting_terms), Toast.LENGTH_LONG).show();
        }
    }

    private void showServiceError(Throwable throwable, String errorMessage) {
        throwable.printStackTrace();
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void aceptTerms() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("Acepto_Terminos_y_condiciones", true);
        editor.commit();
        requestAll();
    }

    private void showTerms() {
        Log.d("iarl", "showTerms");

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.layout_dialog_terms, null);

        sharedpreferences = getSharedPreferences("MyPREFERENCESGrunenthal", Context.MODE_PRIVATE);

        boolean terminosYCondiciones = sharedpreferences.getBoolean("Acepto_Terminos_y_condiciones", false);

        Log.d("iarl", "showTerms " + terminosYCondiciones);

        if (terminosYCondiciones) {
            requestAll();
        } else {
            TextView textview = view.findViewById(R.id.textmsg);
            textview.setText(GrnenthalApplication.terms);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.terms_title);
            alertDialog.setCancelable(false);
            alertDialog.setView(view);
            alertDialog.setPositiveButton(getString(R.string.dialog_terms_ok), (dialog, which) -> aceptTerms());

            AlertDialog alert = alertDialog.create();
            alert.show();
        }
    }

    private void requestAll() {
        Log.d("iarl", "requestAll");
        PharmacyService pharmacyService = ApiClientKt.getRemoteClient(getBaseContext()).create(PharmacyService.class);
        compositeDisposable.add(pharmacyService.getAllPharmacies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getStablishmentsComplete, t -> showServiceError(t, getString(R.string.copy_error_getting_pharmacies))));
    }

    public void getStablishmentsComplete(PharmarciesResponse output) {
        Log.d("iarl", "getStablishmentsComplete ");

        try {
            if (output.getResult()) {
                setPalexis(output.getData().get(1).getPharmacies2());
                setTranstec(output.getData().get(0).getPharmacies1());
                setNorspan(output.getData().get(2).getPharmacies3());
                sortFranquise();
                GrnenthalApplication.franquicias.add(0, "Todos");
            } else {
                Toast.makeText(this, getString(R.string.copy_error_getting_pharmacies), Toast.LENGTH_LONG).show();
            }

            Intent mainIntent = new Intent().setClass(SplashScreenActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPalexis(List<Pharmacy> pharmacies1) {
        for (int i = 0; i < pharmacies1.size(); i++) {
            Log.d("iarl", "name " + pharmacies1.get(i).getName());
            pharmacies1.get(i).setColor("#92317c");
            addFranquicia(pharmacies1.get(i).getFranchise());
        }
        GrnenthalApplication.pharmacies1 = pharmacies1;
    }

    private void setTranstec(List<Pharmacy> pharmacies2) {
        for (int i = 0; i < pharmacies2.size(); i++) {
            Log.d("iarl", "name " + pharmacies2.get(i).getName());
            pharmacies2.get(i).setColor("#dc4338");
            addFranquicia(pharmacies2.get(i).getFranchise());
        }
        GrnenthalApplication.pharmacies2 = pharmacies2;
    }

    private void setNorspan(List<Pharmacy> pharmacies3) {
        for (int i = 0; i < pharmacies3.size(); i++) {
            Log.d("iarl", "name " + pharmacies3.get(i).getName());
            pharmacies3.get(i).setColor("#003CA5");
            addFranquicia(pharmacies3.get(i).getFranchise());
        }
        GrnenthalApplication.pharmacies3 = pharmacies3;
    }

    private void addFranquicia(String franquicia) {
        boolean exist = false;
        for (int i = 0; i < GrnenthalApplication.franquicias.size(); i++) {
            if (GrnenthalApplication.franquicias.get(i).compareToIgnoreCase(franquicia) == 0) {
                exist = true;
            }
        }
        if (!exist)
            GrnenthalApplication.franquicias.add(franquicia);
    }

    private void sortFranquise() {
        Collections.sort(GrnenthalApplication.franquicias, (o1, o2) -> {
            Collator usCollator = Collator.getInstance(Locale.US);
            return usCollator.compare(o1, o2);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}