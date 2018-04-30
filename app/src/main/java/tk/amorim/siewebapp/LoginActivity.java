package tk.amorim.siewebapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import tk.amorim.siewebapp.http.SieWebHttp;
import tk.amorim.siewebapp.util.DialogHelper;
import tk.amorim.siewebapp.util.ValidaCPF;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    ProgressDialog progressDialog;
    @BindView(R.id.input_cpf) EditText _cpf;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    public void login() {
        if (!validate()) {
            return;
        }
        _loginButton.setEnabled(false);


        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        //getString(R.string.authenticating)
        progressDialog.setMessage("wait");
        progressDialog.show();

        String cpf = _cpf.getText().toString();
        String password = _passwordText.getText().toString();
        new LoginTask(cpf, password).execute();

    }

    private class LoginTask extends AsyncTask<Void, Void, Integer> {
        private String cpf;
        private String password;

        LoginTask(String cpf, String password) {
            this.cpf = cpf;
            this.password = password;
        }
        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                boolean ret = SieWebHttp.login(cpf, password);
                if (ret)
                    return 1;
                return 0;
            }
            catch (Exception ex) {
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer res) {
            progressDialog.dismiss();
            if (res == 1)
                onLoginSuccess(cpf, password);
            else if (res == 0)
                onLoginFailed(false);
            else
                onLoginFailed(true);
        }
    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(String cpf, String password) {
        _loginButton.setEnabled(true);
        SharedPreferences sp = getSharedPreferences("credentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("cpf",cpf);
        editor.putString("password", password);
        editor.apply();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    public void onLoginFailed(boolean exception) {
        _loginButton.setEnabled(true);
        if (exception)
            DialogHelper.showDialog(this, "Login failed. Verify your internet connection.", "Oops", true);
        else
            DialogHelper.showDialog(this, "Username or Password incorrect.", "Oops", true);
    }

    public boolean validate() {
        boolean valid = true;

        String cpf = _cpf.getText().toString();
        String password = _passwordText.getText().toString();

        if (!ValidaCPF.isCPF(cpf)) {
            _cpf.setError(getString(R.string.invalid_cpf));
            valid = false;
        } else {
            _cpf.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError(getString(R.string.invalid_password));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
