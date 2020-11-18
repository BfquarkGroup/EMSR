package com.oufar.emsr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {


    //private Animation uptodown, downtoup;

    private ProgressBar progressBar;
    private Button btn_login, register;

    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private Intent intent;


    @SuppressLint("NewApi")
    @Override
    protected void onStart() {
        super.onStart();

        intent = new Intent(MainActivity.this, Home.class);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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

        // checking if the user is online or not (if null means no user is online ein this device)
        if (firebaseUser != null){

            progressBar.setVisibility(View.VISIBLE);
            auth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = auth.getCurrentUser();
            assert firebaseUser != null;
            final String userid = firebaseUser.getUid();

            download_data(userid);

        }else {
            progressBar.setVisibility(View.GONE);
            btn_login.setEnabled(true);
            register.setEnabled(true);
        }
    }

    private void download_data(final String userid) {

        firestore = FirebaseFirestore.getInstance();
        firestore.getInstance().collection("Store").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {

                    progressBar.setVisibility(View.GONE);
                    //clearOldLocalData();
                    startActivity(intent);
                    //Toast.makeText(MainActivity.this, "User", Toast.LENGTH_SHORT).show();
                } else {

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Wrong user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.background_material_light));
        }

        firestore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);
        //downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        //l1.setAnimation(uptodown);
        //downtoup = AnimationUtils.loadAnimation(this, R.anim.downtoup);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });

        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });

        //btn_login.setAnimation(downtoup);
        //register.setAnimation(downtoup);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        //clearOldLocalData();

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return false;
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
