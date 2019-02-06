package com.example.emin.findact;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.emin.findact.Firebase.FirebaseDBHelper;
import com.example.emin.findact.Firebase.UserData;
import com.example.emin.findact.RoomDatabase.User;
import com.example.emin.findact.RoomDatabase.UserDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class SettingsFragment extends Fragment{

    View v;

    ImageView profilePic, changePP;
    EditText fullName, username;
    static EditText birthdate;
    Spinner city;
    SwitchCompat switchCompat;
    Uri selectedImage, controlUri;
    FirebaseDBHelper firebaseDBHelper;
    UserData userData;

    private ArrayAdapter<String> cityAdapter;
    private static String[] citiesList = {"Adana", "Adıyaman", "Afyon", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin", "Aydın", "Balıkesir", "Bilecik",
            "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli", "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir",
            "Gaziantep", "Giresun", "Gümüşhane", "Hakkari", "Hatay", "Isparta", "İçel (Mersin)", "İstanbul", "İzmir", "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir",
            "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa", "K.maraş", "Mardin", "Muğla", "Muş", "Nevşehir", "Niğde", "Ordu", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop",
            "Sivas", "Tekirdağ", "Tokat", "Trabzon", "Tunceli", "Şanlıurfa", "Uşak", "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt", "Karaman", "Kırıkkale", "Batman",
            "Şırnak", "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük", "Kilis", "Osmaniye", "Düzce"};


    String  firstname, lastname,  surnameString,cityString;
    String switchData, defaultName;
    Bitmap bitmap;
    User  user;
    static SimpleDateFormat simpleDateFormat;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.title_settings);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_settings, container, false);

        firebaseDBHelper = new FirebaseDBHelper();

        profilePic = v.findViewById(R.id.fragment_settings_picture_iv);
        changePP = v.findViewById(R.id.fragment_settings_picture_change_picture);
        fullName = v.findViewById(R.id.fragment_settings_fullname);
        birthdate = v.findViewById(R.id.fragment_settings_age_tv);
        city = v.findViewById(R.id.fragment_settings_city_tv);
        switchCompat = v.findViewById(R.id.switch1);
        username = v.findViewById(R.id.fragment_settings_username);

        birthdate.setFocusable(false);
        birthdate.setClickable(true);

        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new selectDate();
                newFragment.show(getFragmentManager(),"DatePicker" );
            }
        });

        cityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, citiesList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(cityAdapter);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){ // switch on
                    switchData = "true";
                } else { // switch off
                    switchData = "false";
                }
            }
        });

        changePP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndroidVersion();
            }
        });

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Log.d("getUserData", "getUserData: " + surnameString);

        String ns = defaultName + " " + surnameString;

        Log.d("getUserData", "getUserData: " + ns);


        user = UserDatabase.getInstance(getContext()).getUserDao().getDatas();

        username.setText(user.getUsername());
        fullName.setText(user.getFirstname() +" "+user.getLastname());
        birthdate.setText(user.getBirthday());
        // Load image
        try{
            File file = new File("/data/user/0/com.example.emin.findact/app_imageDir","profile.jpg" );
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(file));
            profilePic.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        cityString = user.getCity();

        int pos = cityAdapter.getPosition(cityString);
        city.setSelection(pos);

        selectedImage = Uri.parse( user.getPictureUri());
        controlUri = selectedImage;
        switchData = user.getNotification();
        boolean b = Boolean.valueOf(switchData);
        Log.d("onCreateView", "onCreateView: "+b);
        switchCompat.setChecked(b);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actionbar_for_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.actionbar_save){
            updateDetail();
            Intent intent = new Intent(getContext(),MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkAndroidVersion(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            try{
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                        555);
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            pickImage();
        }
    }

    public void pickImage() {
        CropImage.startPickImageActivity(getActivity(),SettingsFragment.this);

        Log.d("pickImage", "pickImage: "+ getActivity());
    }

    private void croprequest(Uri imageUri) {
        Log.d("croprequest", "croprequest: "+ imageUri);
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.OFF)
                .setMultiTouchEnabled(true)
                .start(Objects.requireNonNull(getActivity()), SettingsFragment.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 555 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        } else {
            checkAndroidVersion();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "onActivityResult: "+ requestCode);
        //RESULT FROM SELECTED IMAGE
        if (resultCode == getActivity().RESULT_OK){
            if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                selectedImage = CropImage.getPickImageResultUri(Objects.requireNonNull(getContext()), data);
                Log.d("onActivityResult", "onActivityResult: "+ selectedImage);
                croprequest(selectedImage);
            }

            //RESULT FROM CROPING ACTIVITY
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                try {
                    Uri resultUri = result.getUri();
                    InputStream is = getActivity().getContentResolver().openInputStream(resultUri);
                    //bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), result.getUri());
                    bitmap = BitmapFactory.decodeStream(is);
                    profilePic.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void signOut() {

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void updateDetail(){

        String nameET = fullName.getText().toString();
        String[] nameSplit = nameET.split(" ");
        int i = nameSplit.length;
        firstname = nameSplit[0];

        if (i == 1){
            firstname = nameSplit[0];
            lastname = "";
        } else {
            for (int j = 1; j < i; j++){
                if (j == i-1){
                    lastname = nameSplit[j];
                } else {
                    firstname = firstname +" "+ nameSplit[j];
                }
            }
        }

        if(controlUri == selectedImage){
            userData = new UserData(firstname,lastname ,city.getSelectedItem().toString(),birthdate.getText().toString(),
                    username.getText().toString(), switchData, Uri.parse(""));
            firebaseDBHelper.updateUserDetailWithoutPicture(userData);
        } else {
            userData = new UserData(firstname,lastname ,city.getSelectedItem().toString(),birthdate.getText().toString(),
                    username.getText().toString(), switchData, selectedImage);
            firebaseDBHelper.addUserDetail(userData, firebaseDBHelper.getCurrentUser());
            if (bitmap != null){
                updateInternalStorage(bitmap);
            }
        }

        final User user = new User(1,firstname,lastname ,city.getSelectedItem().toString() ,
                birthdate.getText().toString(), selectedImage.toString(), switchData,username.getText().toString());

        UserDatabase.getInstance(getContext()).getUserDao().update(user);

    }

    public void updateInternalStorage(Bitmap bm){
        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(myPath);
            bm.compress(Bitmap.CompressFormat.JPEG, 100,fos );
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

    public static class selectDate extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(),R.style.DialogTheme,this,year,month,day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            populateSetDate(year,month,day );
        }

        public void populateSetDate(int year, int month, int day){
            Calendar newDate = Calendar.getInstance();
            newDate.set(year,month,day );
            birthdate.setText(simpleDateFormat.format(newDate.getTime()));
        }

    }

}
