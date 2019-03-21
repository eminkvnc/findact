package com.example.emin.findact;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emin.findact.APIs.ActivityModel;
import com.example.emin.findact.Firebase.FirebaseDBHelper;
import com.example.emin.findact.Firebase.UserData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    DatePickerDialog datePickerDialog;
    ProgressDialog dialog;
    UsersListDialog usersListDialog;


    private ArrayList<UserData> followerAndFollowingArrayList;
    private ArrayList<String> invitedArrayList;
    private ArrayList<Integer> statusList;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng latLng;

    private String[] categoriesList = {"Movie","Game","Music","Trip"};
    private ArrayList<String> selectedSubItemsArray;
    private ArrayList<String> movieCategoriesList;
    private ArrayList<String> gameCategoriesList;
    private ArrayList<String> musicCategoriesList;
    private ArrayList<String> tripCategoriesList;

    private HashMap<String,ArrayList<String>> subCategoriesHashmap;

    private ArrayAdapter<String> categoryAdapter;

    private GridLayout subItemsGridLayout;
    private EditText activityNameEditText;
    private ImageView saveImageView;
    private ImageView imageSelectImageView;
    private TextView inviteTextView;
    private EditText dateEditText;
    private Spinner categorySpinner;
    private SupportMapFragment mapFragment;
    private EditText descriptionEditText;

    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        followerAndFollowingArrayList = new ArrayList<>();
        invitedArrayList = new ArrayList<>();
        statusList = new ArrayList<>();

        dialog = new ProgressDialog(CreateActivity.this);
        usersListDialog = UsersListDialog.getInstance();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_create_mv);
        mapFragment.getMapAsync(this);

        activityNameEditText = findViewById(R.id.activity_create_name_et);
        saveImageView = findViewById(R.id.activity_create_save_iv);
        imageSelectImageView = findViewById(R.id.activity_create_select_image_iv);
        dateEditText = findViewById(R.id.activity_create_date_et);
        categorySpinner = findViewById(R.id.activity_create_category_spinner);
        subItemsGridLayout = findViewById(R.id.activity_create_gl);
        descriptionEditText = findViewById(R.id.activity_create_description_et);
        inviteTextView = findViewById(R.id.activity_create_invite_tv);
        dateEditText.setFocusable(false);
        dateEditText.setClickable(true);
        imageSelectImageView.setOnClickListener(this);
        dateEditText.setOnClickListener(this);
        saveImageView.setOnClickListener(this);
        inviteTextView.setOnClickListener(this);
        setDateTimeField();

        categoryAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,categoriesList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }
        };

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        movieCategoriesList = new ArrayList<>();
        gameCategoriesList = new ArrayList<>();
        musicCategoriesList = new ArrayList<>();
        tripCategoriesList = new ArrayList<>();
        subCategoriesHashmap = new HashMap<>();
        selectedSubItemsArray = new ArrayList<>();

        movieCategoriesList.add("Action");
        movieCategoriesList.add("Comedy");
        movieCategoriesList.add("Biography");
        movieCategoriesList.add("War");
        movieCategoriesList.add("Horror");

        gameCategoriesList.add("Shooter");
        gameCategoriesList.add("RPG");
        gameCategoriesList.add("MultiPlayer");
        gameCategoriesList.add("SinglePlayer");

        musicCategoriesList.add("Rock");
        musicCategoriesList.add("Pop");
        musicCategoriesList.add("Jazz");
        musicCategoriesList.add("Rap");

        tripCategoriesList.add("Hiking and Camp");
        tripCategoriesList.add("City and Culture");
        tripCategoriesList.add("Historic Trip");
        tripCategoriesList.add("Sea Trip");

        subCategoriesHashmap.put("Movie",movieCategoriesList);
        subCategoriesHashmap.put("Game",gameCategoriesList);
        subCategoriesHashmap.put("Music",musicCategoriesList);
        subCategoriesHashmap.put("Trip",tripCategoriesList);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSubItemsArray.clear();
                subItemsGridLayout.removeAllViews();
                ArrayList<String> subItems = subCategoriesHashmap.get(categoriesList[position]);
                for(int i = 0; i < subItems.size() ; i++){
                    final String subItem = subItems.get(i);
                    CheckBox checkBox = new CheckBox(getApplicationContext());
                    checkBox.setTextColor(Color.BLACK);
                    checkBox.setText(subItem);
                    subItemsGridLayout.addView(checkBox);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                selectedSubItemsArray.add(subItem);
                            }else{
                                selectedSubItemsArray.remove(subItem);
                            }
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void saveActivity(){

        String activityId = UUID.randomUUID().toString();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, activityId+".jpg");
        saveToInternalStorage(imageBitmap,myPath);
        Uri storageImageUri = Uri.fromFile(new File(myPath.getAbsolutePath()));

        final ActivityModel activityModel = new ActivityModel(activityId,
                activityNameEditText.getText().toString(),
                storageImageUri,
                latLng,dateEditText.getText().toString(),
                categorySpinner.getSelectedItem().toString(),
                selectedSubItemsArray,
                invitedArrayList,
                descriptionEditText.getText().toString(),
                FirebaseDBHelper.getInstance().getCurrentUser());

        dialog.show();
        FirebaseDBHelper.getInstance().addGroupActivity(activityModel, new OnTaskCompletedListener() {
            @Override
            public void onTaskCompleted() {
                dialog.dismiss();
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_create_select_image_iv:
                CropImage.startPickImageActivity(this);
                break;
            case R.id.activity_create_date_et:
                datePickerDialog.show();
                break;
            case R.id.activity_create_invite_tv:
                showInviteDialog();
                break;
            case R.id.activity_create_save_iv:
                if(activityNameEditText.getText().toString().equals("") ||
                imageBitmap == null ||
                latLng == null ||
                selectedSubItemsArray.isEmpty() ||
                dateEditText.getText().toString().equals("") ||
                descriptionEditText.getText().toString().equals("")){
                    Toast.makeText(this, getResources().getText(R.string.toast_fill_blanks), Toast.LENGTH_SHORT).show();
                }else {
                    saveActivity();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //RESULT FROM SELECTED IMAGE
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = CropImage.getPickImageResultUri(this, data);
            croprequest(selectedImage);
        }

        //RESULT FROM CROPING ACTIVITY
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                    Picasso.get().load(result.getUri()).into(imageSelectImageView);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 222 && grantResults.length > 0)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                mMap.clear();
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    LatLng lastUserLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 17f));
                }
            }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setDateTimeField(){

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this,R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateEditText.setText(simpleDateFormat.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        mMap.clear();
        mMap.addMarker(new MarkerOptions().title("Selected Place").position(latLng));
        this.latLng = latLng;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                SharedPreferences sharedPreferences = getSharedPreferences("com.example.emin.foursquareclone",MODE_PRIVATE);
                boolean firstTimeCheck = sharedPreferences.getBoolean("notFirstTime",false);
                if(!firstTimeCheck) {
                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17f));
                    sharedPreferences.edit().putBoolean("notFirstTime",true).apply();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},222);
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            mMap.clear();
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnownLocation != null){
                LatLng lastUserLocation = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,17f));
            }
        }

    }

    private void croprequest(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    private void saveToInternalStorage(Bitmap bitmapImage, File myPath){

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 30,fos );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showInviteDialog(){
        final Bundle bundle = new Bundle();
        bundle.putString("Title",getResources().getText(R.string.activity_create_invite).toString());
        final Bundle followersArrayListBundle = new Bundle();
        final ArrayList<UserData> followingArrayList = new ArrayList<>();
        final ArrayList<UserData> followerArrayList = new ArrayList<>();
        OnTaskCompletedListener onTaskCompletedListener = new OnTaskCompletedListener() {
            @Override
            public void onTaskCompleted() {
                for(int i = 0; i < followerArrayList.size(); i++){
                    if(!followerAndFollowingArrayList.contains(followerArrayList.get(i))){
                        followerAndFollowingArrayList.add(followerArrayList.get(i));
                    }
                }
                FirebaseDBHelper.getInstance().getFollowing(FirebaseDBHelper.getInstance().getCurrentUser(), followingArrayList, statusList, new OnTaskCompletedListener() {
                    @Override
                    public void onTaskCompleted() {
                        for(int i = 0; i < followingArrayList.size(); i++){
                            if(!followerAndFollowingArrayList.contains(followingArrayList.get(i))){
                                followerAndFollowingArrayList.add(followingArrayList.get(i));
                            }
                        }
                        for(int i = 0; i < followingArrayList.size(); i++){
                            followersArrayListBundle.putBundle(String.valueOf(i),followerAndFollowingArrayList.get(i).UserDatatoBundle());
                        }
                        bundle.putBundle("UserDataArrayList",followersArrayListBundle);
                        bundle.putStringArrayList("Attendees",invitedArrayList);
                        bundle.putIntegerArrayList("StatusArrayList",null);
                        usersListDialog.setArguments(bundle);
                        usersListDialog.show(getSupportFragmentManager(),"dialog");
                    }
                });

            }
        };
        FirebaseDBHelper.getInstance().getFollowers(FirebaseDBHelper.getInstance().getCurrentUser(),followerArrayList,statusList, onTaskCompletedListener);
    }

}
