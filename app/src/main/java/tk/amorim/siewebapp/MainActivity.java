package tk.amorim.siewebapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import tk.amorim.siewebapp.fragment.BoletimGenericFragment;
import tk.amorim.siewebapp.fragment.GPAFragment;
import tk.amorim.siewebapp.fragment.SimulatorFragment;
import tk.amorim.siewebapp.http.SieWebHttp;
import tk.amorim.siewebapp.interfaces.BoletimGetActivity;
import tk.amorim.siewebapp.models.Periodo;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BoletimGetActivity {

    String cpf, passsword;
    FrameLayout fl;
    FragmentManager fragmentManager;
    List<WeakReference<BoletimGenericFragment>> fragList = new ArrayList<>();
    List<Periodo> boletim;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        fl = findViewById(R.id.frameLayoutMain);
        GPAFragment gpa = GPAFragment.newInstance();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.frameLayoutMain, gpa).commit();
        new BoletimTask().execute();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        fragList.add(new WeakReference(fragment));
    }

    public List<BoletimGenericFragment> getFragments() {
        ArrayList<BoletimGenericFragment> ret = new ArrayList<>();
        for(WeakReference<BoletimGenericFragment> ref : fragList) {
            BoletimGenericFragment f = ref.get();
            if(f != null) {
                if(f.isVisible()) {
                    ret.add(f);
                }
            }
        }
        return ret;
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            getSharedPreferences("credentials", Context.MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment f = null;
        if (id == R.id.nav_gpa) {
            f = GPAFragment.newInstance();
        }
        else if (id == R.id.nav_simulator) {
            f = SimulatorFragment.newInstance();
        }
        if (f != null)
            fragmentManager.beginTransaction().replace(R.id.frameLayoutMain, f).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    private void boletimDownloaded(List<Periodo> boletim) {
        this.boletim = boletim;
        List<BoletimGenericFragment> fragments = getFragments();
        for (BoletimGenericFragment f : fragments) {
            f.boletimReceived(boletim);
        }
    }

    void goBackToLogin() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public List<Periodo> getBoletim() {
        return boletim;
    }

    private class BoletimTask extends AsyncTask<Void, Void, ArrayList<Periodo>> {
        BoletimTask() {
        }

        @Override
        protected ArrayList<Periodo> doInBackground(Void... voids) {
            return SieWebHttp.boletim(MainActivity.this.getSharedPreferences("credentials", Context.MODE_PRIVATE));
        }

        @Override
        protected void onPostExecute(ArrayList<Periodo> periodos) {
            if (periodos == null) {
                MainActivity.this.getSharedPreferences("credentials", Context.MODE_PRIVATE).edit().clear().apply();
                MainActivity.this.goBackToLogin();
                return;
            }
            TextView tv1 = findViewById(R.id.txtUserNameDrawer);
            String norm = periodos.get(0).getNome().split(" ")[0];
            String name = norm.substring(0, 1) + norm.substring(1).toLowerCase();
            tv1.setText("Welcome, " + name + "!");
            TextView tv2 = findViewById(R.id.txtUserInfoDrawer);
            tv2.setText(periodos.get(0).getCurso());
            MainActivity.this.boletimDownloaded(periodos);
        }
    }
}

