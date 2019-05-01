package com.findact;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.findact.APIs.IGDbAPI;
import com.findact.APIs.TMDbAPI;
import com.findact.Firebase.FirebaseDBHelper;
import com.findact.Firebase.InitialLog;
import com.findact.Firebase.UserData;
import com.findact.RoomDatabase.User;
import com.findact.RoomDatabase.UserDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class GetUserDetailActivity extends AppCompatActivity implements View.OnClickListener {

    // Kayıt olduktan sonra kullanıcıdan tek sefere mahsuz belirli dataları almak için buraya yönlendirdik.

    Bitmap bitmap;
    FirebaseDBHelper firebaseDBHelper;

    EditText nameET, surnameET, birthdayET;
    Spinner citySpinner;
    ListView gameListView, movieListView;
    ImageView profilePicture, saveImageView;
    String name, surname, city, birthday, username, movieGenres, gameGenres;
    Uri selectedImage;

    IGDbAPI igdbApi;
    TMDbAPI tmDbApi;
    ArrayList<String> gameGenreList = new ArrayList<>();
    ArrayList<String> movieGenreList = new ArrayList<>();

    private String[] gameGenresList = {"FPS","MOBA","SINGLE PLAYER","MULTIPLAYER","BATTLEROYAL","VR"};

    private String[] movieGenresList = {"Action","Adventure","Animation",
            "Comedy","Crime","Documentary",
            "Drama","Family","Fantasy","History",
            "Horror","Music","Mystery","Romance",
            "Sci-Fi","TV-Movie",
            "Thriller","War","Western"};


    private ArrayAdapter<String> cityAdapter;
    private String[] citiesList = { "Select City","Adana", "Adıyaman","Afyon","Ağrı","Amasya","Ankara","Antalya","Artvin","Aydın","Balıkesir","Bilecik",
            "Bingöl","Bitlis","Bolu","Burdur","Bursa","Çanakkale","Çankırı","Çorum","Denizli","Diyarbakır","Edirne","Elazığ","Erzincan","Erzurum","Eskişehir",
            "Gaziantep","Giresun","Gümüşhane","Hakkari","Hatay","Isparta","İçel (Mersin)","İstanbul","İzmir","Kars","Kastamonu","Kayseri","Kırklareli","Kırşehir",
            "Kocaeli","Konya","Kütahya","Malatya","Manisa","K.maraş","Mardin","Muğla","Muş","Nevşehir","Niğde","Ordu","Rize","Sakarya","Samsun","Siirt","Sinop",
            "Sivas","Tekirdağ","Tokat","Trabzon","Tunceli","Şanlıurfa","Uşak","Van","Yozgat","Zonguldak","Aksaray","Bayburt","Karaman","Kırıkkale","Batman",
            "Şırnak","Bartın","Ardahan","Iğdır","Yalova","Karabük","Kilis","Osmaniye","Düzce"};

    private ArrayList<InfoCheckboxData> gameInfoData;
    private ArrayList<InfoCheckboxData> movieInfoData;

    private ArrayList<String> selectedGameGenres = null;
    private ArrayList<String> selectedMovieGenres = null;

    DatePickerDialog datePickerDialog;
    SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_detail);

        profilePicture = findViewById(R.id.get_user_detail_user_image);
        nameET = findViewById(R.id.get_user_detail_firstname);
        surnameET = findViewById(R.id.get_user_detail_lastname);
        citySpinner = findViewById(R.id.get_user_detail_city);
        birthdayET = findViewById(R.id.get_user_detail_birthday);
        saveImageView = findViewById(R.id.get_user_detail_save_icon);
        gameListView = findViewById(R.id.get_user_detail_game_genres);
        movieListView = findViewById(R.id.get_user_detail_movie_genres);

        igdbApi = new IGDbAPI();
        tmDbApi = new TMDbAPI();

        igdbApi.getGenres(gameGenreList, new ArrayList<String>(), new OnTaskCompletedListener() {
            @Override
            public void onTaskCompleted() {
                tmDbApi.getGenres(movieGenreList, new OnTaskCompletedListener() {
                    @Override
                    public void onTaskCompleted() {
                        gameInfoData = new ArrayList<>();
                        for (int i = 0; i < gameGenreList.size(); i++){
                            gameInfoData.add(new InfoCheckboxData(false, i));
                        }
                        gameListView.setAdapter(new GameCheckBoxAdapter());

                        movieInfoData = new ArrayList<>();
                        for (int i = 0; i < movieGenreList.size(); i++){
                            movieInfoData.add(new InfoCheckboxData(false, i));
                        }
                        movieListView.setAdapter(new MovieCheckBoxAdapter());
                    }
                });
            }
        });

        cityAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, citiesList );
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        saveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nameET.getText().toString().equals("") ||
                        surnameET.getText().toString().equals("") ||
                        birthdayET.getText().toString().equals("") ||
                        citySpinner.getSelectedItem().equals("Select City") ||
                        nameET.getText().toString().equals("") ||
                        bitmap == null ||
                        selectedGameGenres.isEmpty() ||
                        selectedMovieGenres.isEmpty()){
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.toast_fill_blanks), Toast.LENGTH_SHORT).show();
                }
                else{
                    saveDetail();
                }
            }
        });

        birthdayET.setFocusable(false);
        birthdayET.setClickable(true);
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        setDateTimeField();

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndroidVersion();
            }
        });



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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 555 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        } else {
            checkAndroidVersion();
        }
    }

    public void pickImage() {
        //CropImage.startPickImageActivity(this);
        Intent intent = CropImage.getPickImageChooserIntent(this,"pickImage",false,false);
        startActivityForResult(intent,CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
    }

    private void croprequest(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //RESULT FROM SELECTED IMAGE
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedImage = CropImage.getPickImageResultUri(this, data);
            croprequest(selectedImage);
        }

        //RESULT FROM CROPING ACTIVITY
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());

                    profilePicture.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Doğum tarihi almak için kullanıldı.

    public void setDateTimeField(){

        birthdayET.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this,R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                birthdayET.setText(simpleDateFormat.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    @Override
    public void onClick(View view){
        if (view == birthdayET){
            datePickerDialog.show();
        }
    }

    public void saveDetail(){

        firebaseDBHelper = new FirebaseDBHelper();

        name = nameET.getText().toString();
        surname = surnameET.getText().toString();
        city = citySpinner.getSelectedItem().toString();
        birthday = birthdayET.getText().toString();
        username = firebaseDBHelper.getUserEmailSplit();


        if(!selectedGameGenres.isEmpty()) {
            gameGenres = selectedGameGenres.get(0);
            for (int i = 1; i < selectedGameGenres.size(); i++) {
                gameGenres = gameGenres + "," + selectedGameGenres.get(i);
            }
        }
        if(!selectedMovieGenres.isEmpty()) {
            movieGenres = selectedMovieGenres.get(0);
            for (int i = 1; i < selectedMovieGenres.size(); i++) {
                movieGenres = movieGenres + "," + selectedMovieGenres.get(i);
            }
        }

        firebaseDBHelper = FirebaseDBHelper.getInstance();

        String uuidString = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, uuidString+".jpg");

        Log.d("SaveDetail", "SaveDetail: "+myPath.getAbsolutePath());
        saveToInternalStorage(bitmap,myPath);

        Uri storageImageUri = Uri.fromFile(new File(myPath.getAbsolutePath()));

        UserData userData = new UserData(name, surname, city, birthday ,username ,uuidString ,"true",storageImageUri);
        InitialLog initialLog = new InitialLog(gameGenres, movieGenres , UUID.randomUUID().toString(), Calendar.getInstance().getTime().toString());

        firebaseDBHelper.addUserDetail(userData, true);
        firebaseDBHelper.addInitialUserLog(initialLog);

        final User user = new User(uuidString,name,surname ,city ,birthday , selectedImage.toString(), "true", username);

        UserDatabase.getInstance(getApplicationContext()).getUserDao().insert(user);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private void saveToInternalStorage(Bitmap bitmapImage, File myPath){

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50,fos );
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


    // ADAPTERLAR için farklı bir class oluşturulabilir.

    public class GameCheckBoxAdapter extends BaseAdapter {
        public GameCheckBoxAdapter() {
            selectedGameGenres = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return gameGenreList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {

            View view = View.inflate(getApplicationContext(),R.layout.custom_checkbox_list ,null );

            TextView textView = view.findViewById(R.id.custom_check_list_tv);
            textView.setText(gameGenreList.get(position));
            final CardView cardView = view.findViewById(R.id.custom_checkbox_list_cv);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (gameInfoData.get(position).isClicked){
                        gameInfoData.get(position).isClicked = false;
                        cardView.setCardBackgroundColor(Color.TRANSPARENT);
                        selectedGameGenres.remove(gameGenreList.get(position));

                    } else {
                        gameInfoData.get(position).isClicked = true;
                        cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        if (!selectedGameGenres.contains(gameGenreList.get(position))){
                            selectedGameGenres.add(gameGenreList.get(position));
                        }
                    }

                }
            });
            if (gameInfoData.get(position).isClicked) {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            else {
                cardView.setCardBackgroundColor(Color.TRANSPARENT);
            }
            return view;
        }
    }

    public class MovieCheckBoxAdapter extends BaseAdapter {

        public MovieCheckBoxAdapter() {
            selectedMovieGenres = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return movieGenreList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {

            View view = View.inflate(getApplicationContext(),R.layout.custom_checkbox_list ,null );

            TextView textView = view.findViewById(R.id.custom_check_list_tv);
            textView.setText(movieGenreList.get(position));
            final CardView cardView = view.findViewById(R.id.custom_checkbox_list_cv);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (movieInfoData.get(position).isClicked){
                        movieInfoData.get(position).isClicked = false;
                        cardView.setCardBackgroundColor(Color.TRANSPARENT);
                        selectedMovieGenres.remove(movieGenreList.get(position));

                    } else {
                        movieInfoData.get(position).isClicked = true;
                        cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        if (!selectedMovieGenres.contains(movieGenreList.get(position))){
                            selectedMovieGenres.add(movieGenreList.get(position));
                        }
                    }
                }
            });

            if (movieInfoData.get(position).isClicked) {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            else {
                cardView.setCardBackgroundColor(Color.TRANSPARENT);
            }
            return view;
        }
    }

    boolean doubleBackTab = false;

    @Override
    public void onBackPressed() {
        if (doubleBackTab) {
            finishAffinity();
        } else {
            Toast.makeText(this, getResources().getText(R.string.toast_fill_blanks), Toast.LENGTH_SHORT).show();
            doubleBackTab = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackTab = false;
                }
            }, 500);
        }

    }
}
