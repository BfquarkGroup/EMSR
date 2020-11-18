package com.oufar.emsr.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.WriterException;
import com.oufar.emsr.Home;
import com.oufar.emsr.MainActivity;
import com.oufar.emsr.R;

import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class Fragment_3 extends Fragment {

    private static final int IMAGE_REQUEST = 1;

    private CircleImageView profile_picture, language_picture;
    private ProgressBar progressBar;
    private TextView change_profile_picture, language_txt, wilaya, btn_logout;
    private EditText username, city, phone, description, storeAddressInput;
    private RelativeLayout popUp;
    private CardView box, QRCode_holder;
    private ImageView close, QRCode, close_open_sign;
    private RadioGroup radioGroup;
    private RadioButton radioButton, Arabic, English, French;
    private FloatingActionButton confirm;
    private Button btn_confirm;
    private RelativeLayout homeAddress;

    private Intent intent;

    private int i = 1;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Uri imageUri;
    private String imageURL;

    private Home home;

    private HashMap<String, Object> map = new HashMap<>();
    private DatabaseReference qreference;
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;

    private StorageReference storageReference;
    private StorageTask uploadTask;

    private String txt_store_lat = "", txt_store_lng = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fragment_3, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        //reference = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid());
        //reference = FirebaseDatabase.getInstance().getReference("Client_Data").child(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        home = (Home) getActivity();

        change_profile_picture = view.findViewById(R.id.change_profile_picture);
        profile_picture = view.findViewById(R.id.profile_picture);
        progressBar = view.findViewById(R.id.progressBar);
        close_open_sign = view.findViewById(R.id.close_open_sign);
        username = view.findViewById(R.id.username);
        wilaya = view.findViewById(R.id.wilaya);
        city = view.findViewById(R.id.city);
        storeAddressInput = view.findViewById(R.id.storeAddressInput);
        phone = view.findViewById(R.id.phone);
        description = view.findViewById(R.id.description);
        language_picture = view.findViewById(R.id.language_picture);
        language_txt = view.findViewById(R.id.language_txt);
        popUp = view.findViewById(R.id.popUp);
        box = view.findViewById(R.id.box);
        QRCode_holder = view.findViewById(R.id.QRCode_holder);
        close = view.findViewById(R.id.close);
        QRCode = view.findViewById(R.id.QRCode);
        radioGroup = view.findViewById(R.id.radioGroup);
        Arabic = view.findViewById(R.id.Arabic);
        English = view.findViewById(R.id.English);
        French = view.findViewById(R.id.French);
        confirm = view.findViewById(R.id.confirm);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_logout = view.findViewById(R.id.btn_logout);
        homeAddress = view.findViewById(R.id.homeAddress);

        Spinner mySpinner = view.findViewById(R.id.spinner1);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
                } else if (i == 2) {
                    Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
                }else if (i == 3) {
                    Toast.makeText(getContext(), "3", Toast.LENGTH_SHORT).show();
                }else if (i == 4) {
                    Toast.makeText(getContext(), "4", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //progressBar.setVisibility(View.VISIBLE);

        downloadInfo();

        change_profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        close_open_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Uploading...");
                progressDialog.show();

                if (home.Status().equals("open")){

                    map.put("status", "close");

                    //close_open_sign.setImageResource(R.drawable.close_sign);

                }else if (home.Status().equals("close")){

                    map.put("status", "open");

                    //close_open_sign.setImageResource(R.drawable.open_sign);
                }

        firestore.collection("Store").document(firebaseUser.getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){

                        progressDialog.dismiss();

                        downloadInfo();

                        //updatePage();
                    }else {

                        Toast.makeText(getContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }
            });
            }
        });


        language_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popUp.setVisibility(View.VISIBLE);
                box.setVisibility(View.VISIBLE);
                confirm.setVisibility(View.VISIBLE);
            }
        });

        language_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popUp.setVisibility(View.VISIBLE);
                box.setVisibility(View.VISIBLE);
                confirm.setVisibility(View.VISIBLE);
            }
        });

        popUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popUp.setVisibility(View.GONE);
                box.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
                QRCode_holder.setVisibility(View.GONE);
            }
        });

        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popUp.setVisibility(View.GONE);
                box.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Arabic.isChecked()){

                    Toast.makeText(getContext(), "Arabic", Toast.LENGTH_SHORT).show();
                }else if (English.isChecked()){

                    Toast.makeText(getContext(), "English", Toast.LENGTH_SHORT).show();
                }else if (French.isChecked()){

                    Toast.makeText(getContext(), "French", Toast.LENGTH_SHORT).show();
                }

                popUp.setVisibility(View.GONE);
            }
        });

        /*edit_home_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                androidx.appcompat.app.AlertDialog Alert = new AlertDialog.Builder(getContext())
                        .setMessage("Do you want to select your current location as home address ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(getContext(), "h a", Toast.LENGTH_SHORT).show();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("no", null)
                        .show();
            }
        });*/

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((username.getText().toString().isEmpty() || username.getText().toString().equals(home.Username()))   &&
                        (wilaya.getText().toString().isEmpty() || wilaya.getText().toString().equals(home.Wilaya()))  &&
                        (city.getText().toString().isEmpty() || city.getText().toString().equals(home.City()))  &&
                        (txt_store_lat == null || txt_store_lng == null || txt_store_lat.equals(home.Lat()) || txt_store_lng.equals(home.Lng()) || txt_store_lat.equals("null") || txt_store_lng.equals("null")) &&
                        (phone.getText().toString().isEmpty() || phone.getText().toString().equals(home.Phone()))  &&
                        (description.getText().toString().isEmpty() || description.getText().toString().equals(home.Description())))
                {

                    return;
                }

                uploadInfo();
            }
        });

        profile_picture.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        popUp.setVisibility(View.VISIBLE);
                        QRCode_holder.setVisibility(View.VISIBLE);

                        String data = firebaseUser.getUid();
                        if(data.isEmpty()){

                            Toast.makeText(getContext(), "check your network", Toast.LENGTH_SHORT).show();
                            //qrvalue.setError("Value Required.");
                        }else {
                            QRGEncoder qrgEncoder = new QRGEncoder(data,null, QRGContents.Type.TEXT,500);
                            try {
                                Bitmap qrBits = qrgEncoder.encodeAsBitmap();
                                QRCode.setImageBitmap(qrBits);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                        }




                    }
                }
        );

        locationManager = ( LocationManager ) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                txt_store_lat = location.getLatitude()+"";
                txt_store_lng = location.getLongitude()+"";
                storeAddressInput.setText(txt_store_lat+"\n"+txt_store_lng);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        homeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLocation();

                AlertDialog Alert = new AlertDialog.Builder(getContext())
                        .setMessage("set your current location as your store address by updating coordinates")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);

                            }
                        })
                        .setNegativeButton("no", null)
                        .show();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                home.finish();
                intent = new Intent(getContext(), MainActivity.class);

                home.overridePendingTransition(0,0);
            }
        });

        return view;
    }

    private void uploadInfo() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (!username.getText().toString().isEmpty() || !username.getText().toString().equals(home.Username())){

            map.put("username", username.getText().toString());
            //username.setText(edit_username.getText().toString());
        }
        if (!phone.getText().toString().isEmpty() || !phone.getText().toString().equals(home.Phone())){

            map.put("phone", phone.getText().toString());
            //address.setText(edit_address.getText().toString());
        }
        if (!wilaya.getText().toString().isEmpty() || !wilaya.getText().toString().equals(home.Wilaya())){

            map.put("wilaya", wilaya.getText().toString());
            //address.setText(edit_address.getText().toString());
        }
        if (!city.getText().toString().isEmpty() || !city.getText().toString().equals(home.City())){

            map.put("city", city.getText().toString());
            //address.setText(edit_address.getText().toString());
        }
        if (!txt_store_lat.isEmpty() || !txt_store_lng.isEmpty() || !txt_store_lat.equals(home.Lat()) || !txt_store_lng.equals(home.Lng())){

            map.put("lat", txt_store_lat);
            map.put("lng", txt_store_lng);
        }
        if (!description.getText().toString().isEmpty() || !description.getText().toString().equals(home.Description())){

            map.put("description", description.getText().toString());
            description.setHint(description.getText().toString());
        }

        firestore.collection("Store").document(firebaseUser.getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    progressDialog.dismiss();

                    //updatePage();
                }else {

                    Toast.makeText(getContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){

        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    String current_image;

    public void uploadImage(){

        current_image = imageURL;

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (imageUri != null){

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        //reference = FirebaseDatabase.getInstance().getReference("Client_Data").child(firebaseUser.getUid());

                        map.put("imageURL", mUri);

                        /*reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    deleteOldImage();

                                    progressDialog.dismiss();

                                }else {

                                    Toast.makeText(getContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });*/

                        firestore.collection("Store").document(firebaseUser.getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    deleteOldImage();

                                    progressDialog.dismiss();

                                }else {

                                    Toast.makeText(getContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }

                            }
                        });


                    }else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteOldImage() {

        if (!current_image.equals("default")) {

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference_2 = firebaseStorage.getReferenceFromUrl(current_image);

            storageReference_2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully

                    //Toast.makeText(getContext(), "old image has been deleted", Toast.LENGTH_SHORT).show();

                    imageURL = home.ImageURL();

                    if (imageURL.equals("default")) {

                        profile_picture.setImageResource(R.drawable.wallpaper1);
                    } else {
                        Glide.with(getContext()).load(imageURL).into(profile_picture);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!

                    Toast.makeText(getContext(), "an error occurred!", Toast.LENGTH_SHORT).show();
                }
            });
        }else {

            imageURL = home.ImageURL();

            if (imageURL.equals("default")) {

                profile_picture.setImageResource(R.drawable.wallpaper1);
            } else {
                Glide.with(getContext()).load(imageURL).into(profile_picture);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            }else {
                uploadImage();
                /*
                 we will add a method named confirm and uploadImage(); gonna be included in it so we can reduce the number of writ on firebase

                 and this method contain the edited username and address and ..
                */
            }

        }

    }

    public void downloadInfo(){

        if (home.Status().equals("open")){

            close_open_sign.setImageResource(R.drawable.open_sign);

        }else if (home.Status().equals("close")){

            close_open_sign.setImageResource(R.drawable.close_sign);
        }

        username.setText(home.Username());
        phone.setText(home.Phone());
        wilaya.setText(home.Wilaya());
        city.setText(home.City());
        txt_store_lat = home.Lat();
        txt_store_lng = home.Lng();
        storeAddressInput.setText(home.Lat()+"\n"+home.Lng());
        description.setText(home.Description());
        imageURL = home.ImageURL();

        if (imageURL.equals("default")){

            profile_picture.setImageResource(R.drawable.wallpaper1);
            progressBar.setVisibility(View.GONE);
        }else {

            Glide.with(getContext()).load(imageURL).into(profile_picture);
        }
    }


    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);

            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){

            case 10:

                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                 /*                   if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);
                */

                    Toast.makeText(getContext(), "this app request to access your location data", Toast.LENGTH_SHORT).show();

                return;
        }
    }

}
