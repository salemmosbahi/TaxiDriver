package it.mahd.taxidriver.database;

/**
 * Created by salem on 3/24/16.
 */
public class ReclamationDB {
    private String id;
    private String subject;
    private String date;
    private Boolean status, me;

    public ReclamationDB(String id, String subject, String date, Boolean status, Boolean me) {
        this.id = id;
        this.subject = subject;
        this.date = date;
        this.status = status;
        this.me = me;
    }

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getMe() {
        return me;
    }

    public void setMe(Boolean me) {
        this.me = me;
    }
}
