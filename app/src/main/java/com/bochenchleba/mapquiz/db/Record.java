package com.bochenchleba.mapquiz.db;



public class Record {

    private int id;
    private String name;
    private String continent;
    private String difficulty;
    private String latitude;
    private String longitude;

    public Record(){
    }

    public Record(int id, String name, String continent, String difficulty, String latitude,
                  String longitude){

        this.id = id;
        this.name = name;
        this.continent = continent;
        this.difficulty = difficulty;
        this.latitude = latitude;
        this.longitude = longitude;
    }



    public Record(String name, String continent, String difficulty, String latitude,
                  String longitude){

        this.name = name;
        this.continent = continent;
        this.difficulty = difficulty;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
