package rottiedog.com.mysport;

/**
 * Created by dehuffm on 11/30/2016.
 */

public class DbTrip {
    private long id;
    private String sport;
    private String title;
    private String date;
    private String time;
    private String location;
    private String startAt;
    private String summary;

    public DbTrip(){/*default constructor*/}

    public long getId(){
        return id;
    }
    public void setId(long id){
        this.id = id;
    }

    public String getSport() {return sport;}
    public void setSport(String sport){this.sport = sport;}

    public String getTitle() {return title;}
    public void setTitle(String title){
        this.title = title;
    }

    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }

    public String getTime(){
        return time;
    }
    public void setTime(String time){
        this.time = time;
    }

    public String getLocation(){
        return location;
    }
    public void setLocation(String location){
        this.location = location;
    }

    public String getStartAt(){
        return startAt;
    }
    public void setStartAt(String startAt){
        this.startAt = startAt;
    }

    public String getSummary(){
        return summary;
    }
    public void setSummary(String summary){
        this.summary = summary;
    }

    @Override
    public String toString() {
        return getTitle();

    }
}
