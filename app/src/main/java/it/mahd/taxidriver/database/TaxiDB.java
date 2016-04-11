package it.mahd.taxidriver.database;

/**
 * Created by salem on 4/6/16.
 */
public class TaxiDB {
    private String idTaxi, model, serial, places, luggages, color, date;
    private Boolean working;

    public TaxiDB(String idTaxi, String model, String serial, String places, String luggages, String color, String date, Boolean working) {
        this.idTaxi = idTaxi;
        this.model = model;
        this.serial = serial;
        this.places = places;
        this.luggages = luggages;
        this.color = color;
        this.date = date;
        this.working = working;
    }

    public String getIdTaxi() {
        return idTaxi;
    }

    public String getModel() {
        return model;
    }

    public String getSerial() {
        return serial;
    }

    public String getPlaces() {
        return places;
    }

    public String getLuggages() {
        return luggages;
    }

    public String getColor() {
        return color;
    }

    public String getDate() {
        return date;
    }

    public Boolean getWorking() {
        return working;
    }

    public void setIdTaxi(String idTaxi) {
        this.idTaxi = idTaxi;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public void setPlaces(String places) {
        this.places = places;
    }

    public void setLuggages(String luggages) {
        this.luggages = luggages;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setWorking(Boolean working) {
        this.working = working;
    }
}
