package com.example.emin.findact;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;

import com.example.emin.findact.Firebase.FirebaseDBHelper;
import com.example.emin.findact.Firebase.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment{

    View v;

    ImageView profilePic, changePP;
    EditText name;
    static EditText birthdate;
    Spinner city;
    SwitchCompat switchCompat;
    Uri selectedImage;
    FirebaseDBHelper firebaseDBHelper;
    UserData userData;

    private ArrayAdapter<String> cityAdapter;
    private String[] citiesList = {"Adana", "Adıyaman", "Afyon", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin", "Aydın", "Balıkesir", "Bilecik",
            "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale", "Çankırı", "Çorum", "Denizli", "Diyarbakır", "Edirne", "Elazığ", "Erzincan", "Erzurum", "Eskişehir",
            "Gaziantep", "Giresun", "Gümüşhane", "Hakkari", "Hatay", "Isparta", "İçel (Mersin)", "İstanbul", "İzmir", "Kars", "Kastamonu", "Kayseri", "Kırklareli", "Kırşehir",
            "Kocaeli", "Konya", "Kütahya", "Malatya", "Manisa", "Kahramanmaraş", "Mardin", "Muğla", "Muş", "Nevşehir", "Niğde", "Ordu", "Rize", "Sakarya", "Samsun", "Siirt", "Sinop",
            "Sivas", "Tekirdağ", "Tokat", "Trabzon", "Tunceli", "Şanlıurfa", "Uşak", "Van", "Yozgat", "Zonguldak", "Aksaray", "Bayburt", "Karaman", "Kırıkkale", "Batman",
            "Şırnak", "Bartın", "Ardahan", "Iğdır", "Yalova", "Karabük", "Kilis", "Osmaniye", "Düzce"};


    String defaultImage, user_name, user_surname;
    Toolbar toolbar;
    String switchData;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_settings, container, false);

        toolbar = v.findViewById(R.id.fragment_settings_toolbar);
        toolbar.setTitle("Settings");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        firebaseDBHelper = new FirebaseDBHelper();

        getUserData();

        profilePic = v.findViewById(R.id.fragment_settings_picture_iv);
        changePP = v.findViewById(R.id.fragment_settings_picture_change_picture);
        name = v.findViewById(R.id.fragment_settings_name_tv);
        birthdate = v.findViewById(R.id.fragment_settings_age_tv);
        city = v.findViewById(R.id.fragment_settings_city_tv);
        switchCompat = v.findViewById(R.id.switch1);

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    } else{
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent,2);
                    }
                }
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actionbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.actionbar_save){
            String nameET = name.getText().toString();
            String[] nameSplit = nameET.split(" ");
            if (selectedImage == null){
                Uri uri = Uri.parse(defaultImage);
                int i = nameSplit.length;
                user_name = nameSplit[0];
                for (int j = 1; j < i; j++){
                    if (j == i-1){
                        user_surname = nameSplit[j];
                    } else {
                        user_name = user_name +" "+ nameSplit[j];
                    }
                }
                userData = new UserData(user_name
                        ,user_surname
                        ,city.getSelectedItem().toString()
                        ,birthdate.getText().toString()
                        ,uri,switchData);
                firebaseDBHelper.addUserDetail(userData, firebaseDBHelper.getCurrentUser());
            } else {
                userData = new UserData(nameSplit[nameSplit.length-(nameSplit.length - 1)]
                        ,nameSplit[nameSplit.length-1]
                        ,city.getSelectedItem().toString()
                        ,birthdate.getText().toString()
                        ,selectedImage,switchData);
                firebaseDBHelper.addUserDetail(userData, firebaseDBHelper.getCurrentUser());
            }

            Intent intent = new Intent(getContext(),MainActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.actionbar_logout){
            signOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null){
            selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),selectedImage);
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


//    public Bitmap getResizeBitmap(Bitmap bitmap, int newHeight, int newWidth) {
//
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        float scaleWidth = ((float) newWidth) / width;
//        float scaleHeight = ((float) newHeight) / height;
//
//        Matrix matrix = new Matrix();
//
//        matrix.postScale(scaleWidth, scaleHeight);
//
//        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
//        return resizedBitmap;
//
//    }

    private void signOut() {

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void getUserData() {

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseDBHelper.getCurrentUser()).child("Data");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();

                name.setText(hashMap.get("name")+" "+ hashMap.get("surname"));

                String cityName = (hashMap.get("city"));
                int pos = cityAdapter.getPosition(cityName);
                city.setSelection(pos);

                birthdate.setText(hashMap.get("birth-date"));
                defaultImage = hashMap.get("profile-picture");
                String b = hashMap.get("notification");
                boolean b1 = Boolean.valueOf(b);
                Log.d("onDataChange", "onDataChange: "+b);
                switchCompat.setChecked(b1);
                Picasso.get().load(defaultImage).fit().into(profilePic);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
            populateSetDate(year,month ,day );
        }

        public void populateSetDate(int year, int month, int day){
            birthdate.setText( day+"/"+month+"/"+year);
        }

    }

}
