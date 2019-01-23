package com.example.emin.findact;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.emin.findact.Firebase.FirebaseDBHelper;
import com.example.emin.findact.Firebase.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class GetUserDetailActivity extends AppCompatActivity implements View.OnClickListener {

    // Kayıt olduktan sonra kullanıcıdan tek sefere mahsuz belirli dataları almak için buraya yönlendirdik.

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    FirebaseDBHelper firebaseDBHelper;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    EditText nameET, surnameET, cityET, birthdayET;
    ListView gameListView, movieListView;
    String name, surname, city, birthday, movieGenres, gameGenres;

    private String[] gameGenresList = {"FPS","MOBA","SINGLE PLAYER","MULTIPLAYER","BATTLEROYAL","VR"};
    //private String[] movieGenresList = {"ACTION","ADVENTURE","SCI-FI","HORROR","COMEDY","WAR","FANTASTIC","CRIME"};
    private String[] movieGenresList = {"ACTION","ADVENTURE","COMEDY","BIOGRAPHY"};
    HashMap<String,Integer> hashMap;

    private ArrayList<String> gameData = null;
    private ArrayList<String> movieData = null;

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

        gameData = new ArrayList<>();
        gameData.add("FPS");
        gameData.add("MULTIPLAYER");
        gameData.add("SINGLE PLAYER");
        gameData.add("MOBA");
        gameData.add("BATTLEROYAL");
        gameData.add("VR");

        movieData = new ArrayList<>();
        movieData.add("ACTION");
        movieData.add("ADVENTURE");
        movieData.add("COMEDY");
        movieData.add("CRIME");
        movieData.add("FANTASTIC");
        movieData.add("HORROR");
        movieData.add("SCI-FI");
        movieData.add("WAR");

        hashMap = new HashMap<>();
        hashMap.put(movieGenresList[0],R.drawable.ic_action);
        hashMap.put(movieGenresList[1],R.drawable.ic_fantasy);
        hashMap.put(movieGenresList[2],R.drawable.ic_comedy);
        hashMap.put(movieGenresList[3],R.drawable.ic_biography);


        gameListView = findViewById(R.id.get_user_detail_game_genres);
        movieListView = findViewById(R.id.get_user_detail_movie_genres);

        gameInfoData = new ArrayList<>();
        for (int i = 0; i < gameGenresList.length; i++){
            gameInfoData.add(new InfoCheckboxData(false, i));
            System.out.println("Data is == "+gameGenresList[i]);
        }

        gameListView.setAdapter(new GameCheckBoxAdapter());

        movieInfoData = new ArrayList<>();
        for (int i = 0; i < movieGenresList.length; i++){
            movieInfoData.add(new InfoCheckboxData(false, i));
        }

        movieListView.setAdapter(new MovieCheckBoxAdapter());

        nameET = findViewById(R.id.get_user_detail_userName);
        surnameET = findViewById(R.id.get_user_detail_userSurname);
        cityET = findViewById(R.id.get_user_detail_city);
        birthdayET = findViewById(R.id.get_user_detail_birthday);

        birthdayET.setFocusable(false);
        birthdayET.setClickable(true);

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        setDateTimeField();

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

    public void SaveDetail(View view){

        name = nameET.getText().toString();
        surname = surnameET.getText().toString();
        city = cityET.getText().toString();
        birthday = birthdayET.getText().toString();


        gameGenres = selectedGameGenres.get(0);
        for (int i = 1; i < selectedGameGenres.size(); i++){
            gameGenres = gameGenres + "," + selectedGameGenres.get(i);
        }

        movieGenres = selectedMovieGenres.get(0);
        for (int i = 1; i < selectedMovieGenres.size(); i++){
            movieGenres = movieGenres + "," + selectedMovieGenres.get(i);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        String userEmail = firebaseUser.getEmail();
        String [] userEmailSplit = userEmail.split("@");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        UserModel.ActivitiesModel ActivityGenres = new UserModel.ActivitiesModel(gameGenres,movieGenres);


        UserModel userModel = new UserModel(name, surname, city, birthday, ActivityGenres);
        databaseReference.child("USERS").child(userEmailSplit[0]).setValue(userModel);

        Log.d("Save_Detail", "SaveDetail: "+ userModel.getName() + "  email: " + userEmailSplit[0]);
        firebaseDBHelper = FirebaseDBHelper.getInstance();
        //firebaseDBHelper.addUserDetail(userModel,userEmailSplit[0]);  // NULL OBJECT REFERENCE HATASI VERİYOR

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    // ADAPTERLAR için farklı bir class oluşturulabilir.

    public class GameCheckBoxAdapter extends BaseAdapter {
        public GameCheckBoxAdapter() {
            selectedGameGenres = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return gameGenresList.length;
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

            ImageView imageView = view.findViewById(R.id.imageView3);
            imageView.setImageResource(R.drawable.ic_action);
            final CardView cardView = view.findViewById(R.id.custom_checkbox_list_cv);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (gameInfoData.get(position).isClicked){
                        gameInfoData.get(position).isClicked = false;
                        cardView.setCardBackgroundColor(Color.TRANSPARENT);
                        selectedGameGenres.remove(gameGenresList[position]);

                    } else {
                        gameInfoData.get(position).isClicked = true;
                        cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        if (!selectedGameGenres.contains(gameGenresList[position])){
                            selectedGameGenres.add(gameGenresList[position]);
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
            return movieGenresList.length;
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

            ImageView imageView = view.findViewById(R.id.imageView3);
            imageView.setImageResource(hashMap.get(movieGenresList[position]));
            final CardView cardView = view.findViewById(R.id.custom_checkbox_list_cv);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (movieInfoData.get(position).isClicked){
                        movieInfoData.get(position).isClicked = false;
                        cardView.setCardBackgroundColor(Color.TRANSPARENT);
                        selectedMovieGenres.remove(movieGenresList[position]);

                    } else {
                        movieInfoData.get(position).isClicked = true;
                        cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        if (!selectedMovieGenres.contains(movieGenresList[position])){
                            selectedMovieGenres.add(movieGenresList[position]);
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



}
