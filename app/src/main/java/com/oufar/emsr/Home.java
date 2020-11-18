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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.onesignal.OneSignal;
import com.oufar.emsr.Database.DB;
import com.oufar.emsr.Fragments.Fragment_1;
import com.oufar.emsr.Fragments.Fragment_2;
import com.oufar.emsr.Fragments.Fragment_3;
import com.oufar.emsr.Fragments.Fragment_D;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    ProgressBar progressBar;

    private TextView textView_username;
    //private CircleImageView btn_logout;
    private ChipNavigationBar navigation_bar;
    private ViewPager viewPager;
    private int index = 0;
    private String check = "";
    private DB db;

    DatabaseReference reference;
    private FirebaseFirestore firestore;
    FirebaseUser firebaseUser;

    String txt_status = "";
    String txt_username = "";
    String txt_phone = "";
    String txt_wilaya = "";
    String txt_city = "";
    String txt_lat = "";
    String txt_lng = "";
    String txt_description = "";
    String imageURL = "";


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.white));
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.dark_orange));
        }

        setNavigationBarButtonsColor(this, R.color.dark_orange);

        db = new DB(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid());
        reference = FirebaseDatabase.getInstance().getReference("Client_Data").child(firebaseUser.getUid());

        firestore = FirebaseFirestore.getInstance();

        //downloadInfo();

        if (firebaseUser != null) {
            downloadInfo_();
        }

        textView_username = findViewById(R.id.username);
        //btn_logout = findViewById(R.id.btn_logout);
        navigation_bar = findViewById(R.id.navigation_bar);
        viewPager = findViewById(R.id.view_pager);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        /*btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearLocalData();

                check = "logout";

                FirebaseAuth.getInstance().signOut();
                finish();
                Intent intent = new Intent(Home.this, MainActivity.class);

                Cursor cursor = db.viewData();

                if (cursor.getCount() == 0){

                    Toast.makeText(Home.this, "there is no local data", Toast.LENGTH_SHORT).show();

                }else {

                    while (cursor.moveToNext()){

                        db.deleteData(cursor.getString(0), cursor.getString(1));
                    }

                    Toast.makeText(Home.this, "" +
                            "local data deleted", Toast.LENGTH_SHORT).show();
                }

                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });*/

        navigation_bar.setItemSelected(R.id.home, true);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(viewPagerAdapter);

        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        final Fragment_1 fragment_1 = new Fragment_1();
        final Fragment_2 fragment_2 = new Fragment_2();
        final Fragment_3 fragment_3 = new Fragment_3();
        final Fragment_D fragment_D = new Fragment_D();
        adapter.addFragment(fragment_1);
        adapter.addFragment(fragment_2);
        adapter.addFragment(fragment_D);
        adapter.addFragment(fragment_3);
        viewPager.setAdapter(adapter);

        index = 1;

        navigation_bar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {

                switch(id){
                    case R.id.home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.list:
                        viewPager.setCurrentItem(1);
                        //fragment_2.Refresh();
                        break;
                    case R.id.delivery:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.profile:
                        viewPager.setCurrentItem(3);
                        break;
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0){

                    navigation_bar.setItemSelected(R.id.home, true);
                    navigation_bar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(int id) {

                            switch(id){
                                case R.id.home:
                                    viewPager.setCurrentItem(0);
                                    break;
                                case R.id.list:
                                    viewPager.setCurrentItem(1);
                                    //fragment_2.Refresh();
                                    break;
                                case R.id.delivery:
                                    viewPager.setCurrentItem(2);
                                    break;
                                case R.id.profile:
                                    viewPager.setCurrentItem(3);
                                    break;
                            }
                        }
                    });

                    index = 0;

                }else if (position == 1){

                    navigation_bar.setItemSelected(R.id.list, true);

                    if(index == 0 || index == 2){

                    }
                    index = 1;
                    //Toast.makeText(Home.this, ""+index, Toast.LENGTH_SHORT).show();
                }else if (position == 2){

                    navigation_bar.setItemSelected(R.id.delivery, true);
                    navigation_bar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(int id) {

                            switch(id){
                                case R.id.home:
                                    viewPager.setCurrentItem(0);
                                    break;
                                case R.id.list:
                                    viewPager.setCurrentItem(1);
                                    //fragment_2.Refresh();
                                    break;
                                case R.id.delivery:
                                    viewPager.setCurrentItem(2);
                                    break;
                                case R.id.profile:
                                    viewPager.setCurrentItem(3);
                                    break;
                            }
                        }
                    });

                    index = 2;

                }else if (position == 3){

                    navigation_bar.setItemSelected(R.id.profile, true);
                    navigation_bar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(int id) {

                            switch(id){
                                case R.id.home:
                                    viewPager.setCurrentItem(0);
                                    break;
                                case R.id.list:
                                    viewPager.setCurrentItem(1);
                                    //fragment_2.Refresh();
                                    break;
                                case R.id.delivery:
                                    viewPager.setCurrentItem(2);
                                    break;
                                case R.id.profile:
                                    viewPager.setCurrentItem(3);
                                    break;
                            }
                        }
                    });

                    index = 3;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public  void  downloadInfo_(){

        firestore.collection("Store").document(firebaseUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                assert documentSnapshot != null;

                if (documentSnapshot != null) {

                    progressBar.setVisibility(View.GONE);

                    txt_status = documentSnapshot.get("status").toString();
                    txt_username = documentSnapshot.get("username").toString();
                    txt_phone = documentSnapshot.get("phone").toString();
                    txt_wilaya = documentSnapshot.get("wilaya").toString();
                    txt_city = documentSnapshot.get("city").toString();
                    txt_lat = documentSnapshot.get("lat").toString();
                    txt_lng = documentSnapshot.get("lng").toString();
                    txt_description = documentSnapshot.get("description").toString();
                    imageURL = documentSnapshot.get("imageURL").toString();

                    db.insertData(txt_username);

                }
                //Toast();

            }
        });

    }

    public String Status() {
        return txt_status;
    }
    public String Username() {

        //textView_username.setText(txt_username);

        return txt_username;
    }
    public String Phone() {
        return txt_phone;
    }
    public String Wilaya() {
        return txt_wilaya;
    }
    public String City() {
        return txt_city;
    }
    public String Lat() {
        return txt_lat;
    }
    public String Lng() {
        return txt_lng;
    }
    public String Description() {
        return txt_description;
    }
    public String ImageURL() {
        return imageURL;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {



        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter (FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment){
            fragments.add(fragment);

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        clearLocalData();

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

    private void clearLocalData(){

        // here we delete data from local database
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
