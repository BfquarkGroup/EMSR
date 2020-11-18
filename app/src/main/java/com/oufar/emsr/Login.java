package com.oufar.emsr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;

public class Login extends AppCompatActivity {

    EditText email, password;
    Button btn_login;
    ProgressBar progressBar;

    String txt_email, txt_password;

    FirebaseAuth auth;
    static String loggmail;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.white));
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }
        setNavigationBarButtonsColor(this, R.color.white);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        progressBar = findViewById(R.id.progressBar);

        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txt_email = email.getText().toString();
                txt_password = password.getText().toString();

                if (txt_email.isEmpty()){
                    Toast.makeText(Login.this, "enter your email", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(txt_email).matches()){
                    Toast.makeText(Login.this, "enter a valid email", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }
                if (txt_password.isEmpty()){
                    Toast.makeText(Login.this, "enter your password", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                login();
            }
        });
    }

    private void login() {


        auth.signInWithEmailAndPassword(txt_email, txt_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            download_data();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void download_data() {



        FirebaseUser firebaseUser = auth.getCurrentUser();
        Log.d("LOGGED","user"+firebaseUser);

        assert firebaseUser != null;
        if(firebaseUser != null){
            loggmail=firebaseUser.getEmail();

        }
        OneSignal.sendTag("User_ID",loggmail);



        //OneSignal.sendTag("User_ID",loggmail);
        final String userid = firebaseUser.getUid();

        final Intent intent = new Intent (Login.this, Home.class);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.getInstance().collection("Store").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {

                    progressBar.setVisibility(View.GONE);
                    startActivity(intent);
                    //Toast.makeText(MainActivity.this, "User", Toast.LENGTH_SHORT).show();
                } else {

                    FirebaseAuth.getInstance().signOut();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Wrong user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
    }


    private void setNavigationBarButtonsColor(Activity activity, int navigationBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = activity.getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (isColorLight(navigationBarColor)) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            decorView.setSystemUiVisibility(flags);
        }
    }

    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }
}
