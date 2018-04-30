package tk.amorim.siewebapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.AsyncLayoutInflater;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.text.DecimalFormat;
import java.util.ArrayList;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import tk.amorim.siewebapp.http.SieWebHttp;
import tk.amorim.siewebapp.models.Periodo;
import tk.amorim.siewebapp.models.Subject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String cpf, passsword;
    CircleProgressView cpv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("credentials", MODE_PRIVATE);
        if (!sp.contains("cpf") || !sp.contains("password")) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        cpf = sp.getString("cpf", "");
        passsword = sp.getString("password", "");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        cpv = (CircleProgressView)findViewById(R.id.circleView);
        cpv.setRoundToWholeNumber(false);

        cpv.setDecimalFormat(new DecimalFormat("0.00"));
        cpv.setBarColor(ContextCompat.getColor(this, R.color.gradient1), ContextCompat.getColor(this, R.color.gradient2), ContextCompat.getColor(this, R.color.gradient3));
        cpv.setRimColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        cpv.setSpinBarColor(ContextCompat.getColor(this, R.color.primary_darker));
        cpv.setTextColor(ContextCompat.getColor(this, R.color.white));
        cpv.setSeekModeEnabled(false);
        cpv.setTextMode(TextMode.VALUE);
        cpv.setMaxValue(10);
        cpv.setUnitVisible(false);
        new BoletimTask(this, cpv).execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class BoletimTask extends AsyncTask<Void, Void, ArrayList<Periodo>> {
        private Activity context;
        private CircleProgressView cpv;
        BoletimTask(Activity context, CircleProgressView cpv) {
            this.context = context;
            this.cpv = cpv;
        }

        @Override
        protected ArrayList<Periodo> doInBackground(Void... voids) {
            return SieWebHttp.boletim(context.getSharedPreferences("credentials", Context.MODE_PRIVATE));
        }

        @Override
        protected void onPostExecute(ArrayList<Periodo> periodos) {
            if (periodos == null) {
                context.getSharedPreferences("credentials", Context.MODE_PRIVATE).edit().clear().apply();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return;
            }
            float soma = 0;
            float qt = 0;
            for (Periodo p : periodos) {
                for (Subject s : p.getSubjects()) {
                    if (s.getAvaliacoes().getMf() != -1 && s.getStatus().equals("AP")) {
                          qt+=s.getCh();
                        soma += s.getCh() * s.getAvaliacoes().getMf();
                    }
                }
            }
            float coef = 0;
            if (qt != 0) {
                coef = soma/qt;
            }
            cpv.setValueAnimated(coef);
        }
    }

}

