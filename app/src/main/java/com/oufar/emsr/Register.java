package com.oufar.emsr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    EditText username, wilaya, city, phone, email, password, confirm_password;
    TextView Profession;
    Button btn_register;
    ProgressBar progressBar;
    RadioGroup radioGroup;
    RadioButton restaurant, cafeteria, patisseries, others;
    String txt_profession = "empty";

    FirebaseAuth auth;
    DatabaseReference reference;
    private FirebaseFirestore firestore;



    /*String txt_username;
    String txt_wilaya;
    String txt_city;
    String txt_phone;
    String txt_email;
    String txt_password;
    String txt_confirm_password;*/

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        firestore = FirebaseFirestore.getInstance();

        username = findViewById(R.id.username);
        wilaya = findViewById(R.id.wilaya);
        city = findViewById(R.id.city);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        Profession = findViewById(R.id.Profession);
        radioGroup = findViewById(R.id.radioGroup);
        restaurant = findViewById(R.id.restaurant);
        cafeteria = findViewById(R.id.cafeteria);
        patisseries = findViewById(R.id.patisseries);
        others = findViewById(R.id.others);

        progressBar = findViewById(R.id.progressBar);

        btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                if (restaurant.isChecked()){

                    txt_profession = "food";

                }else if (cafeteria.isChecked()){

                    txt_profession = "cafeteria";

                }else if (patisseries.isChecked()){

                    txt_profession = "patisseries";

                }else if (others.isChecked()){

                    txt_profession = "others";

                }

                /*txt_username = username.getText().toString();
                txt_wilaya = wilaya.getText().toString();
                txt_city = city.getText().toString();
                txt_phone = phone.getText().toString();
                txt_email = email.getText().toString();
                txt_password = password.getText().toString();
                txt_confirm_password = confirm_password.getText().toString();*/

                if (username.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "enter store name", Toast.LENGTH_SHORT).show();
                    username.requestFocus();
                    return;
                }
                if (txt_profession.equals("empty")){
                    Toast.makeText(Register.this, "select profession", Toast.LENGTH_SHORT).show();
                    Profession.requestFocus();
                    Profession.setTextColor(getColor(R.color.dark_orange));
                    return;
                }
                if (wilaya.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "enter your wilaya", Toast.LENGTH_SHORT).show();
                    wilaya.requestFocus();
                    return;
                }
                if (city.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "enter your city", Toast.LENGTH_SHORT).show();
                    city.requestFocus();
                    return;
                }
                if (phone.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "enter your phone", Toast.LENGTH_SHORT).show();
                    phone.requestFocus();
                    return;
                }
                if (email.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "enter your email", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                    Toast.makeText(Register.this, "enter a valid email", Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }
                if (password.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "enter your password", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    return;
                }
                if (password.getText().toString().length()<6){
                    Toast.makeText(getApplicationContext(), "Minimum length of Password should be 6", Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    return;
                }
                if (confirm_password.getText().toString().isEmpty()){
                    Toast.makeText(Register.this, "confirm your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!confirm_password.getText().toString().equals(password.getText().toString())){
                    Toast.makeText(Register.this, "check your password", Toast.LENGTH_SHORT).show();
                    confirm_password.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                Register_2(txt_profession);
            }
        });

    }

    private void Register(){

        auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Client_Data").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username.getText().toString());
                            hashMap.put("profession", txt_profession);
                            hashMap.put("phone", phone.getText().toString());
                            hashMap.put("email", email.getText().toString());
                            hashMap.put("address", wilaya.getText().toString());
                            hashMap.put("city", city.getText().toString());
                            hashMap.put("password", password.getText().toString());
                            hashMap.put("imageURL", "default");
                            hashMap.put("description", "nothing added yet");
                            hashMap.put("delivery", "yes");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(Register.this, Home.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(Register.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void Register_2(final String txt_profession){


        auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            //reference = FirebaseDatabase.getInstance().getReference("Client_Data").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username.getText().toString());
                            hashMap.put("profession", txt_profession);
                            hashMap.put("phone", phone.getText().toString());
                            hashMap.put("email", email.getText().toString());
                            hashMap.put("wilaya", wilaya.getText().toString());
                            hashMap.put("city", city.getText().toString());
                            hashMap.put("password", password.getText().toString());
                            hashMap.put("imageURL", "default");
                            hashMap.put("description", "nothing added yet");
                            hashMap.put("status", "close");
                            hashMap.put("lat", "0.0");
                            hashMap.put("lng", "0.0");

                            firestore.collection("Store").document(userid).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        Intent intent = new Intent(Register.this, Home.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();

                                    }else {

                                        Toast.makeText(Register.this, "ERROR", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }else {
                            Toast.makeText(Register.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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
