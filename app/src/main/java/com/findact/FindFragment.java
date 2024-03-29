package com.findact;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import com.findact.APIs.ActivityModel;
import com.findact.APIs.GameModel;
import com.findact.APIs.IGDbAPI;
import com.findact.APIs.MovieModel;
import com.findact.APIs.TMDbAPI;
import com.findact.Adapters.ActivityListItemAdapter;
import com.findact.Adapters.GameListItemAdapter;
import com.findact.Adapters.MovieListItemAdapter;
import com.findact.Adapters.UserListItemAdapter;
import com.findact.Firebase.FirebaseDBHelper;
import com.findact.Firebase.UserData;
import java.util.ArrayList;
import java.util.HashMap;

public class FindFragment extends Fragment implements View.OnClickListener {

    public static String TAG = "FindFragment";
    public static String selectedTab;
    String sParam;
    FirebaseDBHelper firebaseDBHelper;
    ProgressDialog progressDialog;
    DisplayActivityFragment displayActivityFragment;
    String oldSearchParameter;
    HashMap<String,Boolean> firstClick;

    RecyclerView searchPersonRecyclerView;
    RecyclerView searchMovieRecyclerView;
    RecyclerView searchGameRecyclerView;
    RecyclerView searchGroupRecyclerView;
    Button personButton;
    Button movieButton;
    Button gameButton;
    Button groupButton;
    EditText searchEditText;
    ImageView searchImageView;
    RecyclerView genreRecyclerView;
    RecyclerView modeRecyclerView;

    ArrayList<UserData> userDataArrayList;
    ArrayList<Integer> requestStatus;
    UserListItemAdapter findPersonAdapter;

    TMDbAPI tmDbAPI;
    ArrayList<MovieModel> movieModelArrayList;
    MovieListItemAdapter findMovieAdapter;

    IGDbAPI igDbAPI;
    ArrayList<GameModel> gameModelArrayList;
    GameListItemAdapter findGameAdapter;

    ArrayList<ActivityModel> activityArrayList;
    ActivityListItemAdapter findActivityAdapter;

    ArrayList<String> selectedGenreList;
    ArrayList<String> selectedModeList;
//    ArrayList<String> gameModesList;
//    ArrayList<String> gameGenreList;
//    ArrayList<String> movieGenresList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.setDisplayingFragment(FindFragment.TAG);

        selectedTab = "Person";

        userDataArrayList = new ArrayList<>();
        requestStatus = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        firebaseDBHelper = FirebaseDBHelper.getInstance();

        tmDbAPI = new TMDbAPI();
        movieModelArrayList = new ArrayList<>();
        igDbAPI = new IGDbAPI();

        selectedGenreList = new ArrayList<>();
        selectedModeList = new ArrayList<>();

        movieModelArrayList = new ArrayList<>();
        gameModelArrayList = new ArrayList<>();
        activityArrayList = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.setDisplayingFragment(FindFragment.TAG);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.title_find);
    }

    @Override
    public void onPause() {
        Bundle bundle = new Bundle();
        bundle.putString("SelectedTab",selectedTab);
        onSaveInstanceState(bundle);
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        displayActivityFragment = new DisplayActivityFragment();
        View v = inflater.inflate(R.layout.fragment_find, container, false);
        movieButton = v.findViewById(R.id.fragment_find_movie_btn);
        gameButton = v.findViewById(R.id.fragment_find_game_btn);
        groupButton = v.findViewById(R.id.fragment_find_group_btn);
        personButton = v.findViewById(R.id.fragment_find_person_btn);
        searchEditText = v.findViewById(R.id.fragment_find_search_et);
        searchImageView = v.findViewById(R.id.fragment_find_search_iv);
        searchPersonRecyclerView = v.findViewById(R.id.fragment_find_person_rv);
        searchMovieRecyclerView = v.findViewById(R.id.fragment_find_movie_rv);
        searchGameRecyclerView = v.findViewById(R.id.fragment_find_game_rv);
        searchGroupRecyclerView = v.findViewById(R.id.fragment_find_group_rv);

        if (savedInstanceState != null) {
            selectedTab = savedInstanceState.getString("SelectedTab");
        }

        genreRecyclerView = v.findViewById(R.id.fragment_find_genre_list_rv);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        genreRecyclerView.setLayoutManager(manager);

        modeRecyclerView = v.findViewById(R.id.fragment_find_mode_name_list_rv);
        LinearLayoutManager manager1 = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        modeRecyclerView.setLayoutManager(manager1);

        findPersonAdapter = new UserListItemAdapter(getContext(), userDataArrayList, requestStatus, TAG);
        searchPersonRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchPersonRecyclerView.setAdapter(findPersonAdapter);

        findMovieAdapter = new MovieListItemAdapter(getContext(),movieModelArrayList);
        searchMovieRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchMovieRecyclerView.setAdapter(findMovieAdapter);

        findGameAdapter = new GameListItemAdapter(getContext(),gameModelArrayList);
        searchGameRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchGameRecyclerView.setAdapter(findGameAdapter);

        findActivityAdapter = new ActivityListItemAdapter(getContext(),activityArrayList);
        searchGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchGroupRecyclerView.setAdapter(findActivityAdapter);

        oldSearchParameter = searchEditText.getText().toString();
        firstClick = new HashMap<>();
        firstClick.put("Person",true);
        firstClick.put("Movie",true);
        firstClick.put("Game",true);
        firstClick.put("Group",true);

        searchImageView.setOnClickListener(this);
        personButton.setOnClickListener(this);
        movieButton.setOnClickListener(this);
        gameButton.setOnClickListener(this);
        groupButton.setOnClickListener(this);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldSearchParameter = searchEditText.getText().toString();
                firstClick.put("Person",true);
                firstClick.put("Movie",true);
                firstClick.put("Game",true);
                firstClick.put("Group",true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        switch (selectedTab){
            case "Person":
                personButton.performClick();
                break;
            case "Movie":
                movieButton.performClick();
                break;
            case "Game":
                gameButton.performClick();
                break;
            case "Group":
                groupButton.performClick();
                break;
        }
        return v;
    }


    @Override
    public void onClick(View v) {
        final String searchParameter = searchEditText.getText().toString();
        switch (v.getId()){

            case R.id.fragment_find_person_btn:
                personButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                movieButton.setTextColor(Color.BLACK);
                gameButton.setTextColor(Color.BLACK);
                groupButton.setTextColor(Color.BLACK);

                searchPersonRecyclerView.setVisibility(View.VISIBLE);
                searchMovieRecyclerView.setVisibility(View.GONE);
                searchGameRecyclerView.setVisibility(View.GONE);
                searchGroupRecyclerView.setVisibility(View.GONE);
                genreRecyclerView.setVisibility(View.GONE);
                modeRecyclerView.setVisibility(View.GONE);

                selectedTab = "Person";
                if(!oldSearchParameter.equals(searchEditText.getText().toString()) && firstClick.get("Person")){
                    searchImageView.performClick();
                    oldSearchParameter = searchEditText.getText().toString();
                    firstClick.put("Person",false);
                }
                break;

            case R.id.fragment_find_movie_btn:
                personButton.setTextColor(Color.BLACK);
                movieButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                gameButton.setTextColor(Color.BLACK);
                groupButton.setTextColor(Color.BLACK);

                selectedGenreList.clear();
                GenreNamesAdapter adapter = new GenreNamesAdapter(getContext(), MainActivity.movieGenreList, selectedGenreList);
                genreRecyclerView.setAdapter(adapter);

                searchPersonRecyclerView.setVisibility(View.GONE);
                searchMovieRecyclerView.setVisibility(View.VISIBLE);
                searchGameRecyclerView.setVisibility(View.GONE);
                searchGroupRecyclerView.setVisibility(View.GONE);
                genreRecyclerView.setVisibility(View.VISIBLE);
                modeRecyclerView.setVisibility(View.GONE);

                selectedTab = "Movie";
                if(!oldSearchParameter.equals(searchEditText.getText().toString()) && firstClick.get("Movie")){
                    searchImageView.performClick();
                    oldSearchParameter = searchEditText.getText().toString();
                    firstClick.put("Movie",false);
                }
                break;

            case R.id.fragment_find_game_btn:
                personButton.setTextColor(Color.BLACK);
                movieButton.setTextColor(Color.BLACK);
                gameButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                groupButton.setTextColor(Color.BLACK);

                searchPersonRecyclerView.setVisibility(View.GONE);
                searchMovieRecyclerView.setVisibility(View.GONE);
                searchGameRecyclerView.setVisibility(View.VISIBLE);
                searchGroupRecyclerView.setVisibility(View.GONE);
                genreRecyclerView.setVisibility(View.VISIBLE);
                modeRecyclerView.setVisibility(View.VISIBLE);

                selectedGenreList.clear();
                GenreNamesAdapter adapter2 = new GenreNamesAdapter(getContext(), MainActivity.gameGenreList, selectedGenreList);
                genreRecyclerView.setAdapter(adapter2);

                ModeNamesAdapter modeNamesAdapter = new ModeNamesAdapter(getContext(),MainActivity.gameModeList ,selectedModeList );
                modeRecyclerView.setAdapter(modeNamesAdapter);
                modeNamesAdapter.notifyDataSetChanged();

                selectedTab = "Game";
                if(!oldSearchParameter.equals(searchEditText.getText().toString()) && firstClick.get("Game")){
                    searchImageView.performClick();
                    oldSearchParameter = searchEditText.getText().toString();
                    firstClick.put("Game",false);
                }
                break;

            case R.id.fragment_find_group_btn:
                personButton.setTextColor(Color.BLACK);
                movieButton.setTextColor(Color.BLACK);
                gameButton.setTextColor(Color.BLACK);
                groupButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                searchPersonRecyclerView.setVisibility(View.GONE);
                searchMovieRecyclerView.setVisibility(View.GONE);
                searchGameRecyclerView.setVisibility(View.GONE);
                searchGroupRecyclerView.setVisibility(View.VISIBLE);
                genreRecyclerView.setVisibility(View.GONE);
                modeRecyclerView.setVisibility(View.GONE);

                selectedTab = "Group";
                if(!oldSearchParameter.equals(searchEditText.getText().toString()) && firstClick.get("Group")){
                    searchImageView.performClick();
                    oldSearchParameter = searchEditText.getText().toString();
                    firstClick.put("Group",false);
                }
                break;

            case R.id.fragment_find_search_iv:
                if (MainActivity.isOnline) {
                    String[] split = searchParameter.split(" ");
                    int i = split.length;
                    sParam = split[0];
                    if (i > 1) {
                        for (int j = 1; j < i; j++) {
                            sParam = sParam + "+" + split[j];
                        }
                    }
                    if (!sParam.equals("") && selectedGenreList.isEmpty() && selectedModeList.isEmpty()) {
                        switch (selectedTab){
                            case "Person":
                                progressDialog.show();
                                firebaseDBHelper.searchUser(sParam, userDataArrayList, requestStatus, new OnTaskCompletedListener() {
                                    @Override
                                    public void onTaskCompleted() {
                                        findPersonAdapter.notifyDataSetChanged();
                                        progressDialog.dismiss();
                                    }
                                });
                                break;
                            case "Movie":
                                progressDialog.show();
                                tmDbAPI.searchMovie(sParam, movieModelArrayList, new OnTaskCompletedListener() {
                                    @Override
                                    public void onTaskCompleted() {
                                        findMovieAdapter.notifyDataSetChanged();
                                        progressDialog.dismiss();
                                    }
                                });
                                break;
                            case "Game":
                                progressDialog.show();
                                igDbAPI.searchGame(sParam, gameModelArrayList, new OnTaskCompletedListener() {
                                    @Override
                                    public void onTaskCompleted() {
                                        findGameAdapter.notifyDataSetChanged();
                                        progressDialog.dismiss();
                                    }
                                });
                                break;
                            case "Group":
                                progressDialog.show();
                                firebaseDBHelper.searchActivity(searchParameter, activityArrayList, new OnTaskCompletedListener() {
                                    @Override
                                    public void onTaskCompleted() {
                                        findActivityAdapter.notifyDataSetChanged();
                                        progressDialog.dismiss();
                                    }
                                });
                                break;
                        }
                    } else if (!selectedGenreList.isEmpty() || !selectedModeList.isEmpty()){
                        switch (selectedTab){
                            case "Person":
                                //Filter person here
                                break;
                            case "Movie":
                                if (!selectedGenreList.isEmpty()){
                                    if(!progressDialog.isShowing()){
                                        progressDialog.show();
                                    }
                                    tmDbAPI.searchMovieByGenre(selectedGenreList, movieModelArrayList, new OnTaskCompletedListener() {
                                        @Override
                                        public void onTaskCompleted() {
                                            findMovieAdapter.notifyDataSetChanged();
                                            progressDialog.dismiss();
                                        }
                                    } );
                                }
                                break;
                            case "Game":
                                    if(!progressDialog.isShowing()){
                                        progressDialog.show();
                                    }
                                    igDbAPI.searchByGenreAndModeName(selectedGenreList, selectedModeList, gameModelArrayList, new OnTaskCompletedListener() {
                                        @Override
                                        public void onTaskCompleted() {
                                            findGameAdapter.notifyDataSetChanged();
                                            progressDialog.dismiss();
                                        }
                                    });
                                break;
                            case "Group":
                                //Filter activities here
                                break;
                        }

                    }
                    else {
                        userDataArrayList.clear();
                        movieModelArrayList.clear();
                        gameModelArrayList.clear();
                        activityArrayList.clear();
                        requestStatus.clear();

                        findGameAdapter.notifyDataSetChanged();
                        findMovieAdapter.notifyDataSetChanged();
                        findPersonAdapter.notifyDataSetChanged();
                        findActivityAdapter.notifyDataSetChanged();
                    }
                    break;
                } else {
                    Toast.makeText(getContext(), getResources().getText(R.string.toast_check_internet_connection), Toast.LENGTH_SHORT).show();
                }
        }
    }

    //ADAPTER
    public class GenreNamesAdapter extends RecyclerView.Adapter<GenreNamesAdapter.CheckboxViewHolder>{

        private Context context;
        private ArrayList<String> genreList;
        private ArrayList<String> selectedGenreList;

        public GenreNamesAdapter(Context context, ArrayList<String> genreList, ArrayList<String> selectedGenreList) {
            this.context = context;
            this.genreList = genreList;
            this.selectedGenreList = selectedGenreList;
        }

        @NonNull
        @Override
        public CheckboxViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            CheckBox checkBox = new CheckBox(context);
            return new CheckboxViewHolder(checkBox);
        }

        @Override
        public void onBindViewHolder(@NonNull final CheckboxViewHolder viewHolder, int i) {
            final String name = genreList.get(i);
            viewHolder.checkBox.setText(name);
            viewHolder.checkBox.setChecked(false);
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b){
                            selectedGenreList.add(name);
                        } else {
                            selectedGenreList.remove(name);
                        }
                }
            });

        }

        @Override
        public int getItemCount() {
            return genreList.size();
        }

        class CheckboxViewHolder extends RecyclerView.ViewHolder{
             CheckBox checkBox;

            CheckboxViewHolder(@NonNull View itemView) {
                super(itemView);
                checkBox = (CheckBox) itemView;
            }
        }
    }


    // MODE ADAPTER
    public class ModeNamesAdapter extends RecyclerView.Adapter<ModeNamesAdapter.RadioButtonViewHolder>{

        private Context context;
        private ArrayList<String> modeList;
        private ArrayList<String> selectedModeList;
        private int selectedPosition = -1; // no selection by default

        ModeNamesAdapter(Context context, ArrayList<String> modeList, ArrayList<String> selectedModeList) {
            this.context = context;
            this.modeList = modeList;
            this.selectedModeList = selectedModeList;
        }


        @NonNull
        @Override
        public RadioButtonViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            RadioButton radioButton = new RadioButton(context);
            return new RadioButtonViewHolder(radioButton);
        }

        @Override
        public void onBindViewHolder(@NonNull final RadioButtonViewHolder viewHolder, final int i) {

            final String name = modeList.get(i);
            viewHolder.radioButton.setText(name);

            viewHolder.radioButton.setChecked(i == selectedPosition);
        }

        @Override
        public int getItemCount() {
            return modeList.size();
        }

        class RadioButtonViewHolder extends RecyclerView.ViewHolder{
            RadioButton radioButton;

            RadioButtonViewHolder(@NonNull final View itemView) {
                super(itemView);
                radioButton = (RadioButton) itemView;

                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (selectedModeList.size() == 0){
                            selectedPosition = getAdapterPosition();
                            selectedModeList.add(modeList.get(selectedPosition));

                        } else {
                            selectedPosition = getAdapterPosition();
                            if (selectedModeList.get(0).equals(modeList.get(selectedPosition))){
                                selectedPosition = -1;
                                selectedModeList.clear();
                            } else {
                                selectedModeList.clear();
                                radioButton.setChecked(false);
                                selectedModeList.add(modeList.get(selectedPosition));
                            }

                        }
                        notifyDataSetChanged();
                    }
                });
            }
        }
    }
}
