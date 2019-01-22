package com.example.emin.findact.Firebase;

public class UserModel {

    String name, surname, city, birthday;
    ActivitiesModel activies;

    public UserModel(String name, String surname, String city, String birthday, ActivitiesModel activitiesModel) {
        this.name = name;
        this.surname = surname;
        this.city = city;
        this.birthday = birthday;
        this.activies = activitiesModel;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getCity() {
        return city;
    }

    public String getBirthday() {
        return birthday;
    }


    public static class ActivitiesModel {
        String gameGenres, movieGenres;

        public ActivitiesModel(String gameGenres, String movieGenres) {
            this.gameGenres = gameGenres;
            this.movieGenres = movieGenres;
        }

        public String getGameGenres() {
            return gameGenres;
        }

        public String getMovieGenres() {
            return movieGenres;
        }
    }

}
