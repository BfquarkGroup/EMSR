package com.oufar.emsr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Scan extends AppCompatActivity {

    private CodeScanner codeScanner;
    private CodeScannerView scannView;
    private TextView guyName;
    private CircleImageView guyPicture;
    FloatingActionButton btn_1;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private String QRCode_txt = "";
    private String GUY_ID, GUY_NAME, GUY_IMAGE, GUY_DESCRIPTION, GUY_EMAIL;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.test));
        }

        //transparentStatusAndNavigation();

        Bundle bundle = getIntent().getExtras();
        GUY_ID = bundle.getString("guyId");
        GUY_NAME = bundle.getString("guyName");
        GUY_IMAGE = bundle.getString("guyImage");
        GUY_DESCRIPTION = bundle.getString("guyDescription");
        GUY_EMAIL = bundle.getString("guyEmail");

        scannView = findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(this,scannView);
        guyPicture = findViewById(R.id.guyPicture);
        guyName = findViewById(R.id.guyName);
        btn_1 = findViewById(R.id.btn_1);

        if (!GUY_ID.equals("...")){

            Glide.with(this)
                    .asBitmap()
                    .load(GUY_IMAGE)
                    .into(guyPicture);

            guyName.setText(GUY_NAME);

        }

        codeScanner.setDecodeCallback(new DecodeCallback() {

            @Override
            public void onDecoded(@NonNull final Result result) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        QRCode_txt = result.getText();

                        Intent intent = new Intent(Scan.this, AfterScanList.class);
                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("guyId", QRCode_txt);// sending id
                        startActivity(intent);

                        /*if (GUY_ID.equals(QRCode_txt)){

                            Intent intent = new Intent(Scan.this, AfterScanList.class);
                            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("guyId", GUY_ID);// sending id
                            intent.putExtra("guyName", GUY_NAME);// sending name
                            intent.putExtra("guyImage", GUY_IMAGE);// sending image
                            intent.putExtra("guyDescription", GUY_DESCRIPTION);// sending description
                            intent.putExtra("guyEmail", GUY_EMAIL);// sending email
                            startActivity(intent);
                        }else {

                            Intent intent = new Intent(Scan.this, AfterScanList.class);
                            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("guyId", QRCode_txt);// sending id
                            intent.putExtra("guyName", "...");// sending name
                            intent.putExtra("guyImage", "...");// sending image
                            intent.putExtra("guyDescription", "...");// sending description
                            intent.putExtra("guyEmail", "...");// sending email
                            startActivity(intent);
                        }*/

                        //guyName.setText(QRCode_txt);
                    }
                });

            }
        });

        scannView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Orders");

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();

    }

    public void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(Scan.this, "Camera Permission is Required.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();

            }
        }).check();
    }

    private void transparentStatusAndNavigation() {
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}
